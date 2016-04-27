package com.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.chatroom.Utils;

public class Client implements Runnable {
	
	public static final String CONNECTION_CLOSED = "#CLOSED_CONNETION";
	
	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	
	public static final boolean DEBUGGING = false;
	
	
	private Socket s;
	private PrintWriter writer;
	private BufferedReader reader;
	private Receiver receiver;
	private Acknowledger acknowledger;
		
	private boolean active;
	private boolean closed;
	
	private String nickname;
	
	public Client(String addr, int port) throws UnknownHostException, IOException {
		s = new Socket(addr, port);
		writer = new PrintWriter(s.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		active = false;
	}
	
	public void send(String message) {
		writer.write(message + "\n");
		writer.flush();
	}
	
	public String recvAck(int timeout) throws IOException {
		return receiver.recvAck(timeout);
	}
	
	public String recv() {
		String msg;
		try {
			msg = reader.readLine();
		} catch (IOException e) {
			if (isActive()) {
				close();
			}
			return CONNECTION_CLOSED;
		}
		return msg;
	}

	
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
		acknowledger = new Acknowledger(this);
		(new Thread(receiver)).start();
		(new Thread(acknowledger)).start();
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
	 * Closes the connection.
	 * @pre The client is active
	 */
	public void close() {
		if (active) {
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
			closed = true;
		}
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
