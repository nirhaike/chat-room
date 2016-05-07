package com.chatroomudp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.chatroom.Utils;
import com.chatroomudp.client.Receiver;

import com.chatroomudp.Packet;

public class ClientHandler implements Runnable {
	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	private DatagramPacket client;
	private String nickname;
	private int id, port;
	private int numOfMessages;
	private Server server;
	private InetAddress IPAddress;
	private boolean connected;
	private Packet lastAck;
	DatagramSocket serverSocket = null;
	private Recieve r;

	public ClientHandler(DatagramPacket client, int id, Server s,Recieve r) {
		this.client = client;
		this.id = id;
		this.numOfMessages = 0;
		this.server = s;
		IPAddress = client.getAddress();
		port = client.getPort();
		connected = true;
		this.r = r;
		try {
			serverSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("errror");
		}

	}

	/**
	 * runs the ClientHandler: does the handshake, if passes (returns true):
	 * gets the nickname of the client handles received packets handles
	 * acknowledges
	 */
	public void run() {
		// do the handshake
		connected = connected && handShake();
		// get nickname
		nickname = receive();
		while(nickname == null){
			nickname = receive();
		}
		server.debug(getId(), "Got nickname " + nickname);
		 (new Thread(new AcknowledgerServer(this))).start();
		server.broadcast(Utils.getTime() + " " + getNickname() + "-" + getId()
				+ " connected, welcome!");
		while (connected) {
			String msg = receive();
			while (msg == null){
				msg = receive();
			}
			server.debug(getId(), "GOTTTTT:" + msg);
			
			if (msg == null || !connected || msg == "Error") { // if											// disconnected
				if (connected)
				{
					System.out.println("come to close");
					close();}
				break;
			}
			// if it's a chat message
			if (msg.startsWith("msg: ")) {
				numOfMessages++;
				server.sendMessage(msg.substring(5), this);
			} else if (msg.equals(CLIENT_ACK)) {
				server.debug(getId(), Utils.getTime()
						+ " Got client ack, responded!");
				send(SERVER_RES);
			} else if (msg.equals(CLIENT_RES)) {
				// client's response to the ack of the server
				server.debug(getId(), Utils.getTime() + " Got client response!"
						+ msg);
				// creates a new packet and adds it to the list
				// it comes here when the server gets a message of CLIENT_RES
				Packet p = new Packet(id, Receiver.getTime(), msg);
				lastAck = p;

			}
		}
	}

	public synchronized String recvAck(int timeout) throws IOException {
		long currTime = Receiver.getTime();
		while (true) {
			// check if the timeout passed
			if (Receiver.getTime() - currTime >= timeout || !connected) {
				server.debug(getId(), Utils.getTime() + " Timeout: "
						+ (Receiver.getTime() - currTime));
				throw new IOException("Timeout!");
			}
			if (lastAck != null) {
				Packet p = lastAck;
				lastAck = null;
				return p.getData();
			}
		}
	}

	public int getId() {
		return id;
	}

	public int getNumOfMessages() {
		return numOfMessages;
	}

	public String getNickname() {
		return nickname;
	}

	/**
	 * sends 'msg' to the client sides
	 * 
	 * @param msg
	 *            the message to send
	 */
	public void send(String msg) {
		byte[] sendData = new byte[1024];

		sendData = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, port);
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @post the Packet object returned will never be null
	 * @throws IOException
	 *             if the connection has been lost
	 * @return the first unread packet from the client
	 */
	public Packet recv() throws IOException {
		// TODO
		return null; // <- this is just a placeholder, never return null!
	}

	/**
	 * closes the client
	 * 
	 * @pre this function hasn't been called yet
	 * @post the client and all the related threads are closed
	 */
	public synchronized void close() {
			if (this.connected) {
				this.connected = false;
				server.broadcast(Utils.getTime() + " " + getNickname() + "-"
						+ getId() + " disconnected");
				server.removeClient(this);

			}
		}
	
	/**
	 * closes the connection with the remote client
	 */
	static String ByteArr(byte[] b) {
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

	// Utility functions, getters & setters

	/**
	 * does the handshake with the remote client
	 * 
	 * @return true if and only if the handshake succeeded
	 */
	private boolean handShake() {
		/** do the hand shake **/
		String dateSent = Utils.getDate();
		send(dateSent);
		String s = receive();
		while(s == null){
			s = receive();
		}
		if (!s.equals(Utils.changeDateHandShake(dateSent))) {
			System.out.println("fffffffffffffffffffffffffff");

			close();
			return false;
		}
		return true;
	}

	public String receive() {
		return r.recv(IPAddress, port);
	}
}
