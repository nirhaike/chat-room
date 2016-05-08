package com.chatroomudp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.chatroom.Utils;
import com.chatroomudp.Packet;
import com.chatroomudp.client.Receiver;
import com.chatroomudp.client.Acknowledger;

public class Client implements Runnable {

	private boolean running;
	public static final boolean DEBUGGING = false;

	// server fields
	private int port;
	public static final String CONNECTION_CLOSED = "#CLOSED_CONNETION";

	public static final int MAX_HISTORY_SIZE = 100;
	
	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";

	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	
	// save 100 last sent packets
	private ArrayList<Packet> packetsHistory;
	// last received packet id (to check if there's any missing message)
	private int totalReceivedPackets;
	private int totalSentPackets;
	
	// client fields
	private String nickname;
	private boolean active;
	private boolean closed;
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private Receiver receiver;
	private Acknowledger aacknowledger;


	/**
	 * initializes the client
	 * 
	 * @param addr
	 *            the remote server's IP address
	 * @param port
	 *            the remote server's port
	 * 
	 * @pre addr != null
	 */
	public Client(String addr, int port) {
		this.port = port;
		active = false;
		try {
			this.IPAddress = InetAddress.getByName(addr);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		packetsHistory = new ArrayList<Packet>();
	}

	public static String ByteArr(byte[] b) {
		// this function gets an array of byte and return a string of the array

		byte[] c;
		for (int i = 0; i < b.length; i++) {
			if (String.valueOf(b[i]).equals("0")) {
				c = new byte[i];
				for (int j = 0; j < c.length; j++) {
					c[j] = b[j];
				}
				return new String(c);
			}
		}
		c = new byte[b.length];
		for (int j = 0; j < c.length; j++) {
			c[j] = b[j];
		}
		return new String(c);
	}

	/**
	 * 
	 * does the things at this order: gets the user's nickname connects to the
	 * server does the handshake with the server sends the nickname to the
	 * server creates a receiver and acknowledger
	 * 
	 * then starts the client's main loop: receives messages from the user and
	 * sends them to the server
	 */
	public void run() {
		// init the scanner
		Scanner sc = new Scanner(System.in);
		// ask for a nickname
		System.out.print("Please enter your nickname: ");
		nickname = sc.nextLine();
		send("connect");
		// handle the handshake
		String handshake = "";
		handshake = recv();
		// send the handshake
		String sendHandshake = Utils.changeDateHandShake(handshake);
		send(sendHandshake);
		// send the nickname
		send(nickname);
		// the socket is now active
		active = true;
		// start the receiver
		receiver = new Receiver(this);
		aacknowledger = new Acknowledger(this);
		(new Thread(receiver)).start();
		(new Thread(aacknowledger)).start();
		String msg = sc.nextLine();
		while (!msg.equals("-q") && !isClosed()) {
			send("msg: " + msg);
			// get the next message from the client
			msg = sc.nextLine();
		}
		close();
		sc.close();
		System.out.println("Disconnected.");
	}


	/**
	 * sends a message to the remote client
	 */
	public synchronized void send(String message) {
		// don't send if ended connection
		if (closed)
			return;
		byte[] sendData = new byte[1024];
		// combine the index of the packet and the message
		String packMsg = totalSentPackets + "," + message;
		debug("sent " + packMsg);
		sendData = (packMsg).getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, port);
		try {
			totalSentPackets++;
			packetsHistory.add(new Packet(totalSentPackets, Receiver.getTime(), message));
			// remove 1 packet if there's more than MAX_HISTORY_SIZE packets
			if (packetsHistory.size() > MAX_HISTORY_SIZE)
				packetsHistory.remove(0);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			totalSentPackets--;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public synchronized void resend(int startId) throws IOException {
		debug("RESEND!");
		// no history
		if (packetsHistory == null || packetsHistory.size() == 0)
			throw new IOException("no packet history");
		// too old packet
		if (packetsHistory.get(0).getId() > startId)
			throw new IOException("Can't get back old packets");
		
		// get the first packet
		int startHistoryId = startId - packetsHistory.get(0).getId();
		
		// requested too big number (didn't sent this amount of packets)
		if (startHistoryId < 0)
			throw new IOException("Packet not exists");
		
		totalSentPackets = startId - 1;
		for (int i = startHistoryId; i < packetsHistory.size() - startHistoryId; i++) {
			send(packetsHistory.get(i).getData());
		}
		
	}

	/**
	 * receives a message from the remote server.
	 * 
	 * @post the Packet object returned will never be null
	 * @throws IOException
	 *             if the connection has been lost
	 * @return the first unread packet from the server
	 */
	public String recv() {
		String msg;
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		try {
			clientSocket.receive(receivePacket);
		} catch (IOException e) {
			if (isActive()) {
				close();
			}
			return CONNECTION_CLOSED;
		}
		msg = ByteArr(receivePacket.getData());
		// serial number checks
		if (msg == null)
			return null;
		totalReceivedPackets++;
		int packId = Utils.getPacketId(msg);
		String str = Utils.removeIdFromPacket(msg);
		debug("got " + str);
		// problem (packet not numbered / problematic number)
		if (packId < 0) {
			close();
			// it's urgent to answer acknowledges, so we get them even if it's not the correct order
			if (isAckPacket(str))
				return str;
			return "";
		}
		if (packId > totalReceivedPackets) {
			// ask for the packets between totalReceivedPackets and packId
			send("askpacket " + totalReceivedPackets);
			if (isAckPacket(str))
				return str;
			return "";
		}
		return str;
	}

	/**
	 * ends the connection with the remote server
	 */
	public synchronized void close() {
		if (active) {
			clientSocket.close();
			active = false;
			closed = true;
		}
	}
	public String recvAck(int timeout) throws IOException {
		return receiver.recvAck(timeout);
	}
	// Utility functions, getters & setters

	/**
	 * 
	 * @return whether the client is running (the connection has started & not
	 *         terminated)
	 */
	public boolean isRunning() {
		return running;
	}
	
	public boolean isAckPacket(String msg) {
		return (msg.equals(CLIENT_ACK) || msg.equals(CLIENT_RES));
	}

	public void debug(String msg) {
		if (DEBUGGING) {
			System.out.println(msg);
		}
	}

	public boolean isActive() {
		return active;
	}

	public boolean isClosed() {
		return closed;
	}
}