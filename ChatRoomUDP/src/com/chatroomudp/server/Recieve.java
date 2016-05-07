package com.chatroomudp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Recieve implements Runnable {

	
	private ArrayList<DatagramPacket> list;
	private DatagramSocket ss;
	private byte[] receiveData;
	public Recieve(DatagramSocket d){
		list = new ArrayList<DatagramPacket>();
		this.ss =d; 
	}
	static String ByteArr(byte[] b) {
		byte[] c;
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
		while(true){

		receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		try {
			ss.receive(receivePacket);
			list.add(receivePacket);
		} catch (IOException e) {
			System.out.println("error");
		}
		}
		
		
	}
	public DatagramPacket recv(String msg){
		DatagramPacket d = null;
		for (int i = 0; i < list.size(); i++) {
			if (ByteArr(list.get(i).getData()).equals(msg)){
				d= list.get(i);;
				list.remove(i);
				return d;
			}
		}
		return d;
	}
	public String recv(InetAddress IPAddress, int port){
		String s = null;
		DatagramPacket d = null;
		for (int i = 0; i < list.size(); i++) {
			d= list.get(i);
			boolean b = false;
			try{
			b = (d.getAddress().equals(IPAddress) && d.getPort() == port);
			}
			catch(Exception e){
				System.out.println("i: "+ i + " len: " + list.size() + e.toString() + " " );
			}
			if (b){
			
			
				try{
				s = ByteArr(list.get(i).getData());
				}
				catch(Exception e){
					System.out.println("i: "+ i + " len: " + list.size() + e.toString() + " " );
				}
				try{
				list.remove(i);}
				catch(Exception e){
					System.out.println("i: "+ i + " len: " + list.size() + e.toString() + " " );
				}
				
				return s;
				
			}


		}
		return null;
	}
	
	
}