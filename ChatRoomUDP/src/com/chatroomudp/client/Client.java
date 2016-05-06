package com.chatroomudp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.chatroom.Utils;
import com.chatroomudp.client.Receiver;
import com.chatroomudp.client.acknowledger;

import com.chatroomudp.Packet;

public class Client implements Runnable {

	private DatagramSocket s;
	private boolean running;
	public static final boolean DEBUGGING = false;

	// server fields
	private String ip;
	private int port;
	public static final String CONNECTION_CLOSED = "#CLOSED_CONNETION";

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";

	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	// client fields
	private String nickname;
	private boolean active;
	private boolean closed;
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private Receiver receiver;
	private acknowledger aacknowledger;


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
		this.ip = addr;
		this.port = port;
		active = false;
		try {
			this.IPAddress = InetAddress.getByName(String.valueOf(port));
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

	}

	static String ByteArr(byte[] b) {

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
		aacknowledger = new acknowledger(this);
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
	public void send(String message) {
		byte[] sendData = new byte[1024];
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, port);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return msg; // <- this is just a placeholder, never return null!
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
