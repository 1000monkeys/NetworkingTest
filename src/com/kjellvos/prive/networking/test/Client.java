package com.kjellvos.prive.networking.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	public static int PORT = 4444;
	private static Socket socket;
	private static String serverIP = "192.168.1.8";

	private PrintWriter out;
	private BufferedReader in;
	
	private boolean start = true;
	private int runCount = 0;
	private int[] gameField = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean madeMove = false;
	private int inputMove = -1;

	public void setupVariables() {
		try {
			socket = new Socket(serverIP, PORT);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void move(int move) {
		inputMove = move;
		madeMove = true;
		/*int tryMove;
		Random random = new Random();
		while (true) {
			tryMove = random.nextInt(9);
			if (gameField[tryMove] == 0) {
				return tryMove;
			}
		}*/
	}
	
	public boolean moveLeft() {
		for (int i = 0; i < gameField.length; i++) {
			if (gameField[i] == 0) {
				return true;
			}
		}
		return false;
	}
	
	public void run() {
		while (true) {	
			System.out.println("Client run #" + runCount);

			try {
	    		String JSON = "";
				if (start) {
					setupVariables();
					
					out.println("setup");
					System.out.println("Client send: setup");
					
					start = false;
				}
				
				JSON = in.readLine();
				System.out.println("Client received: " + JSON);
				
				if (JSON.equals("startgame-0") || JSON.equals("startgame-1")) {
					if (JSON.equals("startgame-0")) {
						synchronized (this) {
							while (!madeMove) {
		            			wait(25);
		            		}
						}
	            		
	            		int move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
						
						gameField[move] = 2;
						
						System.out.println("Client send: move-" + move);
						out.println("move-" + move);
					}else if (JSON.equals("startgame-1")) {
						JSON = in.readLine();
						System.out.println("Client received: " + JSON);
					}
				}
				
				if (JSON.contains("move")) {
					int move = Integer.parseInt(JSON.split("-")[1]);
	        		gameField[move] = 1;
	        		
	        		if (moveLeft()) {
	        			synchronized (this) {
		        			while (!madeMove) {
		            			wait(25);
		            		}
	        			}
	            		
	            		move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	        			
	        			gameField[move] = 2;
	        			
	        			System.out.println("Client send: move-" + move);
		        		out.println("move-" + move);
	        		}else {
	        			System.out.println("Client send: gameover");
	        			out.println("gameover");
	        			
	        			System.exit(0);
	        		}
				}else if (JSON.equals("gameover")) {
					System.out.println("[" + gameField[0] + "][" + gameField[1] + "][" + gameField[2] + "]");
	        		System.out.println("[" + gameField[3] + "][" + gameField[4] + "][" + gameField[5] + "]");
	        		System.out.println("[" + gameField[6] + "][" + gameField[7] + "][" + gameField[8] + "]");
					
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runCount++;
		}
	}	
}
