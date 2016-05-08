package com.chatroomudp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Recieve implements Runnable {

	private Server server;
	private ArrayList<DatagramPacket> list;
	private DatagramSocket ss;
	private byte[] receiveData;

	public Recieve(Server s, DatagramSocket ss) {
		this.server = s;
		list = new ArrayList<DatagramPacket>();
		this.ss = ss;
	}

	static String ByteArr(byte[] b) {
		byte[] c;
		if (b == null)
			return "";
		for (int i = 0; i < b.length; i++) {
			if (String.valueOf(b[i]).equals("0")) {
				c = new byte[i];
				for (int j = 0; j < c.length; j++) {
					c[j] = b[j];
				}
				return new String(c);
			}
		}
		c = new byte[b.length];
		for (int j = 0; j < c.length; j++) {
			c[j] = b[j];
		}
		return new String(c);
	}

	public void run() {
		// receive and store packets
		while (server.isRunning()) {
			receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			try {
				ss.receive(receivePacket);
				list.add(receivePacket);
			} catch (IOException e) {
				// print only if the server is running
				if (server.isRunning())
					e.printStackTrace();
			}
		}

	}

	public synchronized DatagramPacket recv(String msg) {
		boolean goodPacket = false;

		for (int i = 0; i < list.size(); i++) {
			try {
				DatagramPacket pack = list.get(i);
				if (pack == null)
					continue;
				goodPacket = ByteArr(pack.getData()).equals(msg);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("i: " + i + " len: " + list.size() + " ");
			}
			if (goodPacket) {
				DatagramPacket retPacket = list.get(i);
				list.remove(i);
				return retPacket;
			}
		}
		return null;
	}

	public synchronized String recv(InetAddress IPAddress, int port) {
		String s = null;
		for (int i = 0; i < list.size(); i++) {
			DatagramPacket pack = list.get(i);
			if (pack == null)
				continue;
			boolean goodPacket = false;
			try {
				goodPacket = (pack.getAddress().equals(IPAddress) && pack.getPort() == port);
			} catch (Exception e) {
				if (server.isRunning())
					e.printStackTrace();
			}
			if (goodPacket) {
				try {
					s = ByteArr(pack.getData());
				} catch (Exception e) {
					if (server.isRunning())
						e.printStackTrace();
				}
				try {
					list.remove(i);
				} catch (Exception e) {
					if (server.isRunning())
						e.printStackTrace();
				}
				return s;

			}

		}
		return null;
	}

}