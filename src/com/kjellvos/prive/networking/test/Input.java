package com.kjellvos.prive.networking.test;
import java.util.Random;
import java.util.Scanner;

public class Input {
	private Server server;
	private Client client;
	private Scanner scanner = null;
	
	
	public Input(Server server, Client client) {
		this.server = server;
		this.client = client;
		scanner = new Scanner(System.in);
	}

	public void waitForInput(boolean amIServer){
		int input = Integer.parseInt(scanner.nextLine());
		
		if (amIServer) {
			synchronized (this) {
				server.move(input);
			}
		}else {
			synchronized (this) {
				client.move(input);
			}
		}
	}
}
