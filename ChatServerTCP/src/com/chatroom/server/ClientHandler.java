package com.chatroom.server;
import java.io.IOException;
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
		
	}
	
	/**
	 * @pre data != null
	 * @post $ret: whether the message was sent
	 */
	public boolean send(String data) {
		return false;
	}
	
	

}
