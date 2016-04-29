package com.chatroomudp.server;

import java.io.IOException;
import java.net.DatagramSocket;

import com.chatroomudp.Packet;

public class ClientHandler implements Runnable {

	private DatagramSocket client;
	
	public ClientHandler(DatagramSocket client) {
		this.client = client;
	}
	
	/**
	 * runs the ClientHandler:
	   * does the handshake, if passes (returns true):
	   * gets the nickname of the client
	   * handles received packets
	   * handles acknowledges
	 */
	public void run() {
		// TODO
	}
	
	/**
	 * sends 'msg' to the client sides
	 * @param msg the message to send
	 */
	public void send(String msg) {
		// TODO
	}
	
	/**
	 * @post the Packet object returned will never be null
	 * @throws IOException if the connection has been lost
	 * @return the first unread packet from the client
	 */
	public Packet recv() throws IOException {
		// TODO
		return null; // <- this is just a placeholder, never return null!
	}
	
	/**
	 * closes the client
	 * @pre this function hasn't been called yet
	 * @post the client and all the related threads are closed
	 */
	public synchronized void close() {
		
	}
	
	// Utility functions, getters & setters
	
	/**
	 * does the handshake with the remote client
	 * @return true if and only if the handshake succeeded
	 */
	public boolean handshake() {
		return false;
	}
	
}
