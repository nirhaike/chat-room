package com.chatroomudp.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Scanner;

import com.chatroomudp.Packet;

public class Client implements Runnable {

	private DatagramSocket s;
	private boolean running;
	
	// server fields
	private String ip;
	private int port;
	
	// client fields
	private String nickname;
	
	/**
	 * initializes the client
	 * 
	 * @param addr the remote server's IP address
	 * @param port the remote server's port
	 * 
	 * @pre addr != null
	 */
	public Client(String addr, int port) {
		this.running = false;
		this.ip = addr;
		this.port = port;
	}
	
	/**
	 * 
	 * does the things at this order:
	 	* gets the user's nickname
	 	* connects to the server
	 	* does the handshake with the server
	 	* sends the nickname to the server
	 	* creates a receiver and acknowledger
 	 *
	 * then starts the client's main loop:
	 	* receives messages from the user and sends them to the server
	 */
	public void run() {
		Scanner sc = new Scanner(System.in);
		// TODO
	}
	
	/**
	 * sends a message to the remote client
	 */
	public void send() {
		
	}
	
	/**
	 * receives a message from the remote server.
	 * 
	 * @post the Packet object returned will never be null
	 * @throws IOException if the connection has been lost
	 * @return the first unread packet from the server
	 */
	public Packet recv() {
		// TODO
		return null; // <- this is just a placeholder, never return null!
	}
	
	/**
	 * ends the connection with the remote server
	 */
	public synchronized void close() {
		
	}
	
	// Utility functions, getters & setters
	
	/**
	 * 
	 * @return whether the client is running (the connection has started & not terminated)
	 */
	public boolean isRunning() {
		return running;
	}
	
}