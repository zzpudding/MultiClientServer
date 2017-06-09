package de.fhl.yujia.multiClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	ServerSocket serverSocket;
	PrintWriter writer;
	int clientNum = 0;
	
	//create a .txt file to store client transmission log
	static String logFileDir = "clientlog.txt";
	static PrintWriter logWriter = null;
	static {
		try {
			logWriter = new PrintWriter(logFileDir, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Server() {
		try {
			serverSocket = new ServerSocket(22000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * server create a handler for a new client
	 */
	public void start() {
		System.out.println("Server start");
		while (true) {
			try {
				Socket s = serverSocket.accept();
				Handler handler = new Handler(s, clientNum++);
				handler.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * write a log of connection to the log file
	 * @param index the ID of the client
	 * @param time the transmitted String from client
	 */
	static synchronized void logStart(int index, String time) {
		String log = "Client" + index + " connected at " + time;
		System.out.println(log);
		logWriter.println(log);
		logWriter.flush();
	}
	
	/**
	 * write a log of disconnection to the log file
	 * @param index the ID of the client
	 * @param count the number of messages that sent from the client
	 */
	static synchronized void logClose(int index, int count) {
		String log = "Client" + index + " disconnect after " + count + " messages";
		System.out.println(log);
		logWriter.println(log);
		logWriter.flush();
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

}

//one handler(thread) handles one client
class Handler extends Thread {
	Socket socket = null;
	int index = 0; //the ID of the client
	int count = 0; //the number of messages sent from one client
	BufferedReader br = null;

	Handler(Socket s, int n) {
		this.socket = s;
		this.index = n;
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * run a handler to handle a client
	 * judge if the client ends and close it
	 */
	public void run() {
		try {
			Server.logStart(index, br.readLine());
			count++;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				if (br.readLine().equals("END")) {
					break;
				} else {
					count++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Server.logClose(index, count);
	}
}