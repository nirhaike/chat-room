package com.chatroom.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.chatroom.Utils;

public class Server implements Runnable {

	public static final int PORT = 6655;

	public static final boolean DEBUGGING = false;
	
	private ArrayList<ClientHandler> clients;
	private ServerSocket ss;
	private boolean running;
	

	public Server() {
		clients = new ArrayList<ClientHandler>();
		ss = null;
		running = false;
	}
	
	public void run() {
		// create the server socket
		try {
			ss = new ServerSocket(PORT);
			running = true;
			System.out.println("The server is running on port " + PORT);
		} catch (IOException e) {
			System.out.println("Can't listen on port " + PORT);
		}
		// the current client id
		int currId = 0; 
		// run
		while (running) {
			try {
				Socket socket = ss.accept();
				// add the client
				ClientHandler handler = new ClientHandler(socket, currId++, this);
				Thread clientThread = new Thread(handler);
				clientThread.start();
				clients.add(handler);
				debug(-1, "started client");
			} catch (IOException e) {
				break;
			}
		}
	}
	
	public synchronized void removeClient(ClientHandler client) {
		for (int i = 0; i < clients.size(); i++) {
			ClientHandler c = clients.get(i);
			if (client == c) {
				clients.remove(i);
				return;
			}
		}
	}
	
	public void sendMessage(String message, ClientHandler sender) {
		// create the message format
		String finalMsg = sender.getNickname() + "-" + sender.getId() + " (" +
				Utils.getTime() + ", #" + sender.getNumOfMessages() + "): " + message;
		debug(-1, finalMsg);
		// send it to the clients
		broadcast(finalMsg);
	}
	
	public void broadcast(String message) {
		// send the message to the clients
		for (ClientHandler client : clients) {
			client.send(message);
		}
		// print the message to the server
		System.out.println(message);
	}
	
	public synchronized int findClientByName(String clientName) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getNickname().equals(clientName)) {
				return i;
			}
		}
		return -1;
	}
	
	public synchronized int findClientById(int clientId) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getId() == clientId) {
				return i;
			}
		}
		return -1;
	}
	
	public void handleServerCommand(String command) {
		String[] commandSplitted = command.split(" ");
		if (commandSplitted.length < 1)
			return;
		String commandName = commandSplitted[0];
		// handle the command "@kick -id userid" or "@kick -name username"
		if (commandName.equals("kick")) {
			try {
				// check if there's enough parameters
				if (commandSplitted.length < 3)
					return;
				int clientNum = -1; // client index in the array
				int clientId = -1; // client handler's id
				String clientName = "";
				if (commandSplitted[1].equals("-name")) { // by name
					clientName = commandSplitted[2];
					// get the client id
					clientNum = findClientByName(clientName);
				} else { // by id
					clientId = Integer.parseInt(commandSplitted[2]);
					clientNum = findClientById(clientId);
				}
				// if there's no such name
				if (clientNum == -1)
					return;
				// remove the client
				ClientHandler client = clients.get(clientNum);
				// make sure we have the valid name and id
				clientName = client.getNickname();
				clientId = client.getId();
				client.terminate();
				broadcast(clientName + "-" + clientId + " was kicked by the server");
			} catch (Exception e) {
				// can get here if the client disconnected in the middle of the function 
			}
		} else if (commandName.equals("help")) {
			System.out.println("----------------------------------------------");
			if (commandSplitted.length < 2) {
				System.out.println("Server commands:\n");
				System.out.println("* '@kick -name username' - kick by name");
				System.out.println("* '@kick -id userId' - kick by id");
				System.out.println("\ntype '@help commandName' for more information.");
			} else if (commandSplitted[1].equals("kick")) {
				System.out.println("* '@kick -name username' - kick a user by his \n  nickname. Example: '@kick -name Nir'");
				System.out.println("* '@kick -id userId' - kick a user by his \n  user ID. Example: '@kick -id 7'");
			}
			System.out.println("----------------------------------------------");
		}
	}
	
	public void debug(int id, String str) {
		// print only if debugging
		if (DEBUGGING)
			System.out.println("(" + id + ") "+str);
	}
	
	public synchronized void close() {
		try {
			running = false;
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < clients.size(); i++) {
			ClientHandler client = clients.get(i);
			client.close();
			i--;
		}
		debug(-1, "Server closed safety");
	}
	
}
