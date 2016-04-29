package com.chatroomudp.server;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// create the server
		Server server = new Server();
		Thread serverThread = new Thread(server);
		// run the server
		serverThread.start();
		// handle server commands (until -q)
		Scanner sc = new Scanner(System.in);
		String command = sc.nextLine();
		while (!command.equals("-q")) {
			command = sc.nextLine();
		}
		// close the server
		sc.close();
		server.close();
		
	}
	
}
