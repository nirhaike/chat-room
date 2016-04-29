package com.chatroomudp.client;


public class Main {

	public static void main(String[] args) {
		// parse the arguments
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		// initialize the client handler
		Client c = new Client(ip, port);
		// start the client handler
		Thread t = new Thread(c);
		t.start();
	}
	
}
