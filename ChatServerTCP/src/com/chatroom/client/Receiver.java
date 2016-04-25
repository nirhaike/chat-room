package com.chatroom.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Receiver implements Runnable {
	
	private Client client;
	
	private ArrayList<Packet> acksList;
	private ArrayList<Packet> msgList;
	
	public Receiver(Client c) {
		this.client = c;
	}
	
	public synchronized String recvMsg() {
		long currTime = getTime();
		while (true) {
			for (int i = 0; i < msgList.size(); i++) {
				Packet p = msgList.get(i);
				msgList.remove(i); // remove the checked packet
				if (p.timeReceived > currTime) {
					return p.data;
				}
				i--;
			}
		}
	}
	
	public synchronized String recvAck(int timeout) throws IOException {
		long currTime = getTime();
		while (true) {
			for (int i = 0; i < acksList.size(); i++) {
				Packet p = acksList.get(i);
				msgList.remove(i); // remove the checked packet
				if (p.timeReceived > currTime) {
					return p.data;
				}
				i--;
			}
			// check if the timeout passed
			if (getTime()-currTime >= timeout)
				throw new IOException("Tiemout!");
		}
	}
	
	public void run() {
		while (true) {
			String data = client.recv();
			Packet p = new Packet(getTime(), data);
			// if the server responded an acknowledge
			if (isAcknowledgeResponse(data)) {
				acksList.add(p);
			}
			// if the server sended an acknowledge
			else if (isAcknowledgeRequest(data)) {
				// send a response
				client.send(Client.CLIENT_RES);
			}
			// a message packet
			else {
				msgList.add(p);
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
	public long getTime() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	private class Packet {
		long timeReceived;
		String data;
		
		public Packet(long time, String data) {
			this.timeReceived = time;
			this.data = data;
		}
		
	}
	
}
