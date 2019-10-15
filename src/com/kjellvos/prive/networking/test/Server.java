package com.kjellvos.prive.networking.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server extends Thread{
	public static int PORT = 4444;
	public ServerSocket serverSocket = null;
	private static Socket socket;
	private PrintWriter out;
	private BufferedReader br;
	
	private boolean start = true;
	private int runCount = 0;
	
	private int[] gameField = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean playerOne = false;
	private boolean madeMove = false;
	private int inputMove = -1;
	
	public void setupVariables() {
		try {
			serverSocket = new ServerSocket(PORT);
	
			socket = serverSocket.accept();
			
			InputStream is = socket.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        br = new BufferedReader(isr);
	        
	    	out = new PrintWriter(socket.getOutputStream(), true);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void move(int move) {
		inputMove = move;
		madeMove = true;
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
			System.out.println("Server run #" + runCount);
			
			if(start) {
				setupVariables();
				start = false;
			}
			
			try {
	        	String JSON = br.readLine(); //receiving message
	        	System.out.println("Server received: " + JSON);
	        	
	        	if (JSON.equals("setup")) {            	            	
	            	if (playerOne) {
	        			System.out.println("Server send: startgame-1");
	            		out.println("startgame-1");
	            		
	            		synchronized (this) {
		            		while (!madeMove) {
		            			wait(25);
		            		}
	            		}
	            		
	            		int move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	            		
	        			gameField[move] = 1;
	            		
	            		System.out.println("Server send: move-" + move);
		        		out.println("move-" + move);
	            	}else {
	        			System.out.println("Server send: startgame-0");
	            		out.println("startgame-0");
	            	}
	        	}else if (JSON.equals("gameover")) {        		
	        		System.out.println("[" + gameField[0] + "][" + gameField[1] + "][" + gameField[2] + "]");
	        		System.out.println("[" + gameField[3] + "][" + gameField[4] + "][" + gameField[5] + "]");
	        		System.out.println("[" + gameField[6] + "][" + gameField[7] + "][" + gameField[8] + "]");
	        		
	        		System.exit(0);
	        	}else if (JSON.contains("move")) {
					int move = Integer.parseInt(JSON.split("-")[1]);
	        		gameField[move] = 2;
	        		
	        		if (moveLeft()) {
	        			synchronized (this) {
		        			while (!madeMove) {
		            			wait(25);
		            		}
	        			}
	            		
	            		move = inputMove;
	            		inputMove = -1;
	            		madeMove = false;
	        			
	        			gameField[move] = 1;
	        			
	        			System.out.println("Server send: move-" + move);
		        		out.println("move-" + move);
	        		}else {
	        			System.out.println("Server send: gameover");
	        			out.println("gameover");
	        		}
	        	}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runCount++;
		}
	}

	public boolean getIsPlayerOne() {
		return playerOne;
	}
	
	public void setIsPlayerOne(boolean playerOne) {
		this.playerOne = playerOne;
	}
}