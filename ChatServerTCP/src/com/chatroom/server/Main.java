package com.chatroom.server;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		// create the server
		Server server = new Server();
		Thread serverThread = new Thread(server);
		// run the server
		serverThread.start();
		Scanner sc = new Scanner(System.in);
		String command = sc.nextLine();
		while (!command.equals("-q")) {
			// check if it's a command
			if (command.startsWith("@")) {
				server.handleServerCommand(command.substring(1));
			// print the message otherwise
			} else {
				server.broadcast("[SERVER] " + command);
			}
			command = sc.nextLine();
		}
		sc.close();
		server.close();
		
	}
	
}
