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
	
	private Socket s;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public Client(String addr, int port) throws UnknownHostException, IOException {
		s = new Socket(addr, port);
		writer = new PrintWriter(s.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(s.getInputStream())); 
	}
	
	public void run() {
		// handle the handshake
		String handshake = "";
		try {
			handshake = reader.readLine();
		} catch (IOException e) {
			System.out.println("Couldn't connect to the server...");
		}
		// if the handshake is not correct
		if (!handshake.equals(Utils.getDate())) {
			close();
			return;
		}
		// send the handshake
		writer.write(Utils.changeDateHandShake(handshake));
		// init the scanner
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		while (!msg.equals("-q")) {
			writer.write(msg);
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
	}
	
}
