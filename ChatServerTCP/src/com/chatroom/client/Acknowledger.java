package com.chatroom.client;

import java.io.IOException;

public class Acknowledger implements Runnable {

	private Client client;
	
	public Acknowledger(Client c) {
		this.client = c;
	}
	
	public void sleep(long currTime, long waitTime) {
		while (Receiver.getTime() < currTime + waitTime) {
			// wait
		}
	}
	
	public void run() {
		while (true) {
			// save the current time
			long currTime = Receiver.getTime();
			// wait 5 seconds
			sleep(currTime, 5000);
			// send acknowledge
			client.send(Client.CLIENT_ACK);
			// get the response
			try {
				if (!client.recvAck(1000).equals(Client.SERVER_RES)) {
					client.close();
					break;
				}
			} catch (IOException ioe) {
				client.close();
				break;
			}
		}
	}
	
}
