package de.fhl.yujia.multiClientServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Thread {
	static final int CLIENT_NUMBER = 20;
	static final int RAND_MIN = 10; //the minimal number of messages client should send
	static final int RAND_MAX = 100;//the maximal number of messages client should send
	static final String SENTINAL = "END";
	Socket clientSocket;
	PrintWriter writer;
	
	Client() {
		try {
			clientSocket = new Socket("localhost", 22000);
			writer = new PrintWriter(clientSocket.getOutputStream());
		} catch (Exception e) {
			System.out.println("Cannot connect to server");
		}
	}
	
	/**
	 * 
	 * @return a random number between 10(MIN)-100(MAX)
	 */
	public int getRandomNum() {
		return RAND_MIN + (int)(Math.random() * (RAND_MAX-RAND_MIN+1));
	}
	
	/**
	 * client gets a current time every 500ms and send it to server (by send method)
	 */
	public void run() {
		for (int printNumber = 0; printNumber<getRandomNum(); printNumber++) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String currentTime = dateFormat.format(new Date());
			send(currentTime);
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		send(SENTINAL);
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param s a String that client wants to send
	 */
	public void send(String s) {
		writer.println(s);
		writer.flush();
	}
	
	public static void main(String[] args) {
		System.out.println("Client start");
		//create 20 clients
		for(int i=0; i < CLIENT_NUMBER; i++) {
			Client client = new Client();
			client.start();
			try {
				sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
