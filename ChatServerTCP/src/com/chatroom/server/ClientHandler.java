package com.chatroom.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";

	private Socket socket;
	private boolean connected;

	public ClientHandler(Socket s) {
		this.socket = s;
		connected = true;
	}

	// send, receive, close, check
	public void run() {
		while (connected) {
			// TODO ...
		}
	}

	public void close() throws IOException {
		this.socket.close();
	}

	/**
	 * @pre data != null
	 * @post $ret: whether the message was sent
	 */
	public boolean send(String data) {
		
		try{
		out = new PrintWriter(socket.getOutputStream(), true);
		out.println(data);
		out.flush();
		}
		catch (IOException e) {
			return false;
		}
		return true;
		
		}
}
