package com.chatroomudp.server;

import java.io.IOException;

import com.chatroomudp.client.Client;
import com.chatroomudp.client.Receiver;

public class AcknowledgerServer implements Runnable {
	
	private ClientHandler client;
	
	public AcknowledgerServer(ClientHandler c) {
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
			client.send(Client.SERVER_ACK);
			// get the response
			try {
				if (!client.recvAck(1000).equals(Client.CLIENT_RES)) {
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
