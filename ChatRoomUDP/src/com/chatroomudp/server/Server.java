package com.chatroomudp.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import com.chatroom.Utils;
import com.chatroomudp.server.TerminatingService;
import com.chatroomudp.server.ClientHandler;




public class Server implements Runnable {
	public static final int PORT = 1234;
	public static final boolean DEBUGGING = false;

	private TerminatingService terminateService;
	private ArrayList<ClientHandler> clients;
	private DatagramSocket ss;
	private boolean running;
	
	public Server() {
		terminateService = new TerminatingService(this);
		(new Thread(terminateService)).start();
		clients = new ArrayList<ClientHandler>();
		running = false;
		ss = null;
	}
	
	/**
	 * handles the new connections
	 */
	public void run() {
		try {
			ss = new DatagramSocket(PORT);
			running = true;
			System.out.println("The server is running on port " + PORT);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't listen on port " + PORT);
		}
		int currId = 0; 
		Recieve r = new Recieve(this, ss);
		DatagramPacket receivePacket = null;
		Thread clientR = new Thread(r);
		clientR.start();
		// start terminating (there's no client right now) 
		terminateService.startTerminating();
		while (running) {
			try{
			receivePacket = r.recv("0,connect");
			if( receivePacket != null){
				ClientHandler handler = new ClientHandler(receivePacket, currId++, this,r);
				Thread clientThread = new Thread(handler);
				clientThread.start();
				clients.add(handler);
				// stop terminating
				terminateService.stopTerminating();
				debug(-1, "started client");
			}


			}
			catch(Exception e){
				System.out.println(e.toString());
				System.out.println("error server");
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
		// start terminating if there's no client
		if (clients.size() == 0) { 
			terminateService.startTerminating();
		}
	}
	/**
	 * sends 'message' to all the remote clients
	 * @param message the message to send
	 */
	public void broadcast(String message) {
		for (ClientHandler client : clients) {
			client.send(message);
		}
		// print the message to the server
		System.out.println(message);
	}

	/**
	 * closes the server (and all the clients & threads) safety
	 * @pre the server is running
	 * @post all the server functionality won't work
	 */
	public synchronized void close() {
		running = false;
		ss.close();
		for (int i = 0; i < clients.size(); i++) {
			ClientHandler client = clients.get(i);
			client.close();
			i--;
		}
		terminateService.close();
		debug(-1, "Server closed safety");
	}
	public void debug(int id, String str) {
		// print only if debugging
		if (DEBUGGING)
			System.out.println("(" + id + ") "+str);
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
		// create the message format
		String finalMsg = sender.getNickname() + "-" + sender.getId() + " (" +
				Utils.getTime() + ", #" + sender.getNumOfMessages() + "): " + message;
		debug(-1, finalMsg);
		// send it to the clients
		broadcast(finalMsg);
	}
}