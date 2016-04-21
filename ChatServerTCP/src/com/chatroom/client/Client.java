package com.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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
		// init the scanner
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		while (!msg.equals("-q")) {
			writer.write(msg);
			// get the next message from the client
			msg = sc.nextLine();
		}
	}
	
}
