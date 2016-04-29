package com.chatroomudp;


public class Packet {
	private int id;
	private long timeReceived;
	private String data;
	
	public Packet(int packetId, long time, String data) {
		this.setId(packetId);
		this.setTimeReceived(time);
		this.setData(data);
	}

	public int getId() {
		return id;
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
	
	public void setId(int id) {
		this.id = id;
	}

	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
	}
	
}


