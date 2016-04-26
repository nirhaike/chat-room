package com.chatroom.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Receiver implements Runnable {
	
	private Client client;
	
	private ArrayList<Packet> acksList;
	private ArrayList<Packet> msgList;
	
	public Receiver(Client c) {
		acksList = new ArrayList<Packet>();
		msgList = new ArrayList<Packet>();
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
			// check if the timeout passed
			if (getTime()-currTime >= timeout || acksList == null) {
				System.out.println("Timeout: " + (getTime()-currTime));
				throw new IOException("Timeout!");
			}
			if (acksList.size() > 0) {
				Packet p = acksList.get(0);
				return p.data;
			}
		}
	}
	
	public void run() {
		while (client.isActive()) {
			String data = client.recv();
			if (data == null) {
				continue;
			}
			if (!client.isActive() || data == Client.CONNECTION_CLOSED) {
				break;
			}
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
				//msgList.add(p);
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
	
	private class Packet {
		long timeReceived;
		String data;
		
		public Packet(long time, String data) {
			this.timeReceived = time;
			this.data = data;
		}
		
	}
	
}
