package com.chatroom;


public class Packet {
	private long timeReceived;
	private String data;
	
	public Packet(long time, String data) {
		this.setTimeReceived(time);
		this.setData(data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
	}
	
}


