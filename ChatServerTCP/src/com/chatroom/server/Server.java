package com.chatroom.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.chatroom.Utils;

public class Server implements Runnable {

	public static final int PORT = 6655;

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
				debug("started client");
			} catch (IOException e) {
				break;
			}
		}
	}
	
	public void removeClient(ClientHandler client) {
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
		debug(finalMsg);
		// send it to the clients
		for (ClientHandler client : clients) {
			client.send(finalMsg);
		}
	}
	
	public void debug(String str) {
		System.out.println(str);
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
		debug("Server closed safety");
	}
	
}
