package com.chatroomudp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.chatroomudp.Packet;

public class ClientHandler implements Runnable {
	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";
	private DatagramPacket client;
	private String nickname;
	private int id;
	private int numOfMessages;
	private Server server;

	public ClientHandler(DatagramPacket client,int id, Server s) {
		this.client = client;
		this.id = id;
		this.numOfMessages = 0;
		this.server = s;

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
	public int getId() {
		return id;
	}
	
	public int getNumOfMessages() {
		return numOfMessages;
	}
	
	public String getNickname() {
		return nickname;
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
