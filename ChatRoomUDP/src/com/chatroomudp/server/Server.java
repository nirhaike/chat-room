package com.chatroomudp.server;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class Server implements Runnable {

	private ArrayList<ClientHandler> clients;
	private DatagramSocket ss;
	private boolean running;
	
	public Server() {
		clients = new ArrayList<ClientHandler>();
		running = false;
		ss = null;
	}
	
	/**
	 * handles the new connections
	 */
	public void run() {
		// TODO
	}
	
	/**
	 * sends 'message' to all the remote clients
	 * @param message the message to send
	 */
	public void broadcast(String message) {
		// TODO
	}
	
	/**
	 * closes the server (and all the clients & threads) safety
	 * @pre the server is running
	 * @post all the server functionality won't work
	 */
	public synchronized void close() {
		
	}
	
	// Utility functions
	/**
	 * 
	 * @return whether the server is currently running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * sends 'message' to all the remote clients(including 'sender'!)
	 * 
	 * this function should use the 'broadcast' function
	 * 
	 * @param message the message to send
	 * @param sender the client that sent the message
	 * 
	 * @pre sender != null, message != null
	 */
	public void sendMessage(String message, ClientHandler sender) {
		
	}

}
