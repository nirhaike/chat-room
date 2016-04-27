package com.chatroom.client;

import java.io.IOException;
import java.util.Calendar;

import com.chatroom.Packet;
import com.chatroom.Utils;

public class Receiver implements Runnable {
	
	private Client client;
	
	private Packet lastAck;
	
	public Receiver(Client c) {
		lastAck = null;
		this.client = c;
	}

	
	public String recvAck(int timeout) throws IOException {
		long currTime = getTime();
		while (true) {
			// check if the timeout passed
			if (getTime()-currTime >= timeout) {
				client.debug(Utils.getTime() + " Timeout: " + (getTime()-currTime));
				throw new IOException("Timeout!");
			}
			if (lastAck != null) {
				Packet p = lastAck;
				lastAck = null;
				return p.getData();
			}
		}
	}
	
	public void run() {
		while (client.isActive()) {
			String data = client.recv();
			client.debug(Utils.getTime() + " got packet: " + data);
			if (data == null) {
				break;
			}
			if (!client.isActive() || data == Client.CONNECTION_CLOSED) {
				break;
			}
			Packet p = new Packet(getTime(), data);
			// if the server responded an acknowledge
			if (isAcknowledgeResponse(data)) {
				client.debug(Utils.getTime() + " Got server response!");
				lastAck = p;
			}
			// if the server sent an acknowledge
			else if (isAcknowledgeRequest(data)) {
				// send a response
				client.debug(Utils.getTime() + " Responded!");
				client.send(Client.CLIENT_RES);
			}
			// a message packet
			else {
				System.out.println(data);
			}
		}
	}
	
	/**
	 * @param str a message from the server
	 * @return whether this message is an acknowledge request response
	 */
	public boolean isAcknowledgeResponse(String str) {
		return str.equals(Client.SERVER_RES);
	}
	
	/**
	 * @param str a message from the server
	 * @return whether this message is an acknowledge request request
	 */
	public boolean isAcknowledgeRequest(String str) {
		return str.equals(Client.SERVER_ACK);
	}
	
	/**
	 * @return the time in milliseconds
	 */
	public static long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}
	

	
}
