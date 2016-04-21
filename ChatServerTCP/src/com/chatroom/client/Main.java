package com.chatroom.client;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		// parse the arguments
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		// initialize the client handler
		Client c = null;
		try {
			c = new Client(ip, port);
		} catch (IOException e) {
			System.err.println("Couldn't set up the client");
			e.printStackTrace();
			System.exit(1);
		}
		// start the client handler
		Thread t = new Thread(c);
		t.start();
		// send the acknowledges
		while (c.isActive()) {
			// sleep 5 seconds
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// send acknowledge
			c.send(Client.CLIENT_ACK);
		}
	}
	
}
