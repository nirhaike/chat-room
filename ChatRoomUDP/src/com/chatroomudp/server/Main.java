package com.chatroomudp.server;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// create the server
		Scanner sc = null;
		Server server = null;
		try{
		server = new Server();
		Thread serverThread = new Thread(server);
		// run the server
		serverThread.start();
		// handle server commands (until -q)
		sc = new Scanner(System.in);
		}
		catch(Exception e){
			System.out.println("error main");
		}
		String command = sc.nextLine();
		while (!command.equals("-q") && server.isRunning()) {
			command = sc.nextLine();
		}
		// close the server
		sc.close();
		server.close();
		
	}
	
}
