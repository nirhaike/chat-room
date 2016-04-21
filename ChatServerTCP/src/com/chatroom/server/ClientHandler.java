package com.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {

	public static final String SERVER_ACK = "servercheck";
	public static final String CLIENT_RES = "servergood";
	public static final String CLIENT_ACK = "clientcheck";
	public static final String SERVER_RES = "clientgood";

	private Socket socket;
	private boolean connected, passedHandshake;
	private PrintWriter out;
	private BufferedReader input;

	public ClientHandler(Socket s) {
		this.socket = s;
		connected = true;
		start();
		handSake();
	}

	private boolean handSake() {
		/** do the hand shake **/
		Date d = new Date();
		if (!send(d.toString())){
			return false;
		}
		String rec = receive();
		return true;
	}

	public void start() {

		/** Initialize the variables with try and catch */
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
		this.out.close();
		this.input.close();
	}

	/**
	 * @pre data != null
	 * @post $ret: whether the message was sent
	 */
	public String receive() {
		try {
			return this.input.readLine();
		} catch (IOException e) {
			return "Error";
		}
	}

	public boolean send(String data) {
		try {
			this.out.println(data);
			out.flush();
		} catch (Exception e) {
			return false;
		}
		return true;

	}
}
