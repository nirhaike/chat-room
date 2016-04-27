package com.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.chatroom.Packet;
import com.chatroom.Utils;
import com.chatroom.client.Receiver;

public class ClientHandler implements Runnable {

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";

	private Server server;
	
	private Socket socket;
	private boolean connected;
	private PrintWriter out;
	private BufferedReader input;
	
	private ArrayList<Packet> acksList;

	
	private String nickname;
	private int id;
	private int numOfMessages;

	public ClientHandler(Socket s, int id, Server serv) {
		this.id = id;
		this.nickname = "";
		this.numOfMessages = 0;
		
		this.socket = s;
		this.server = serv;
		connected = true;
		start();
	}
	public synchronized String recvAck(int timeout) throws IOException {
		long currTime = Receiver.getTime();
		while (true) {
			// check if the timeout passed
			if (Receiver.getTime()-currTime >= timeout || acksList == null) {
				System.out.println("Timeout: " + (Receiver.getTime()-currTime));
				throw new IOException("Timeout!");
			}
			if (acksList.size() > 0) {
				Packet p = acksList.get(0);
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

	private boolean handShake() {
		/** do the hand shake **/
		String dateSent = Utils.getDate();
		send(dateSent);
		if (!receive().equals(Utils.changeDateHandShake(dateSent))) {
			close();
			return false;
		}
		return true;
	}

	public void start() {
		/** Initialize the variables with try and catch */
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// receive messages, close when needed
	public void run() {
		// do the handshake
		connected = connected && handShake();
		server.debug("Done handshake with " + connected + " result.");
		// get nickname
		nickname = receive();
		server.debug("Got nickname " + nickname);
		// TODO ~start acknowledges here!~
		while (connected) {
			String msg = receive();
			if (msg == null || !connected) { // if disconnected
				connected = false;
				break;
			}
			// if it's a chat message
			if (msg.startsWith("msg: ")) {
				server.sendMessage(msg.substring(5), this);
			} else if (msg.equals(CLIENT_ACK)) {
				System.out.println(">>Sent ack");
				send(SERVER_RES);
			} else {
				// response to the ack of the server
				System.out.println(">>" + msg);
				
				// creates a new packet and adds it to the list
				// it comes here when the server gets a message of CLIENT_RES
				Packet p = new Packet(Receiver.getTime(), msg);
				acksList.add(p);

			}
		}
	}

	public void close() {
		System.out.println("Closing!");
		if (this.connected) {
			this.connected = false;
			this.out.close();
			try {
				this.input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			server.removeClient(this);
		}
	}

	/**
	 * @pre data != null
	 * @post $ret: whether the message was sent
	 */
	public synchronized String receive() {
		try {
			return this.input.readLine();
		} catch (IOException e) {
			close();
			return "Error";
		}
	}

	public synchronized boolean send(String data) {
		try {
			this.out.println(data);
			out.flush();
		} catch (Exception e) {
			return false;
		}
		return true;

	}
}
