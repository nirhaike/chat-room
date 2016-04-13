package com.chatroom.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
		while (running) {
			try {
				Socket socket = ss.accept();
				// --- handshake ---
				boolean passedHandshake = false;
				// TODO handshake here!
				// if passed, changed 'passedHandshake' to true
				if (passedHandshake) {
					// --- add to the clients arraylist ---
					ClientHandler handler = new ClientHandler(socket);
					clients.add(handler);
					// --- run the client handler ---
					Thread clientThread = new Thread(handler);
					clientThread.start();
				} else {
					socket.close();
				}
			} catch (IOException e) {
				System.out.println("Server closed safety");
			}
		}
	}
	
	public void close() {
		try {
			running = false;
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ClientHandler client : clients) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
