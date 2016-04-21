package com.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";

	private Socket socket;
	private boolean connected;
	private PrintWriter out;
	private BufferedReader input;
	
	public ClientHandler(Socket s) {
		this.socket = s;
		connected = true;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public String receive(){
		return "";
	}
	
	
	
	public boolean send(String data) throws IOException {

		this.out.println(data);
		out.flush();
		return true;
		
		
		}
}
