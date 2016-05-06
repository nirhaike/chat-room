package com.chatroom.server;

import com.chatroom.client.Receiver;

public class TerminatingService implements Runnable {

	// the terminate time in seconds
	public static final int TERMINATE_TIME = 60;
	
	private boolean isTerminating;
	private boolean running;
	private Server s;
	
	public TerminatingService(Server server) {
		this.s = server;
		this.running = true;
		this.isTerminating = false;
	}
	
	public void startTerminating() {
		isTerminating = true;
	}
	
	public void stopTerminating() {
		isTerminating = false;
	}
	
	private void tryTerminate() {
		long startTime = Receiver.getTime();
		while ((Receiver.getTime() - startTime) / 1000 < TERMINATE_TIME) {
			// stop processing if not terminating anymore 
			if (!isTerminating) {
				return;
			}
			// sleep for a second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Don't do anything if sleep interrupted
			}
		}
		// terminate if reached this point
		System.out.println("Terminated due to lack in activity.\nPress any key to continue...");
		s.close();
	}
	
	public void run() {
		while (running) {
			if (isTerminating) {
				tryTerminate();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Don't do anything if sleep interrupted
			}
		}
	}
	
	public void close() {
		running = false;
		isTerminating = false;
	}

}
