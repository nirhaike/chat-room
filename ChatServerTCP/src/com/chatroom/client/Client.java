package com.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.chatroom.Utils;

public class Client implements Runnable {
	
	public static final String CONNECTION_CLOSED = "#CLOSED_CONNETION";
	
	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	
	
	private Socket s;
	private PrintWriter writer;
	private BufferedReader reader;
	
	// arraylist of last acknowledge messages 
	private ArrayList<String> ack;
	
	private boolean active;
	
	public Client(String addr, int port) throws UnknownHostException, IOException {
		s = new Socket(addr, port);
		writer = new PrintWriter(s.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		active = false;
	}
	
	public void send(String message) {
		writer.write(message);
		writer.flush();
	}
	
	public String recv() {
		String msg;
		while (true) {
			try {
				msg = reader.readLine();
			} catch (IOException e) {
				System.out.println("Lost connection with the remote server");
				return CONNECTION_CLOSED;
			}
			if (isAcknowledgeResponse(msg)) {
				ack.add(msg);
			} else if (isAcknowledgeRequest(msg)) {
				// respond to the acknowledge
				
			} else {
				return msg;
			}
		}
	}
	
	/**
	 * @param str a message from the server
	 * @return whether this message is an acknowledge request response
	 */
	public boolean isAcknowledgeResponse(String str) {
		return str.equals(SERVER_RES);
	}
	
	/**
	 * @param str a message from the server
	 * @return whether this message is an acknowledge request request
	 */
	public boolean isAcknowledgeRequest(String str) {
		return str.equals(SERVER_ACK);
	}

	
	public void run() {
		// handle the handshake
		String handshake = "";
		handshake = recv();
		// if the handshake is not correct
		if (!handshake.equals(Utils.getDate())) {
			close();
			return;
		}
		// send the handshake
		send(Utils.changeDateHandShake(handshake));
		// the socket is now active
		active = true;
		// init the scanner
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		while (!msg.equals("-q")) {
			send(msg);
			// get the next message from the client
			msg = sc.nextLine();
		}
		sc.close();
	}
	
	/**
	 * Closes the connection.
	 * @pre The client is active
	 */
	public void close() {
		writer.close();
		try {
			reader.close();
		} catch (IOException e) {
			System.err.println("Error occured while closing the reader.");
		}
		try {
			s.close();
		} catch (IOException e) {
			System.err.println("Error occured while closing the client socket.");
		}
		active = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
}
