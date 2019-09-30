package com.kjellvos.prive.networking.test;
import java.io.IOException;

public class Main {
	private static Server server = null;
	private static Client client = null;
	private static Input input = null;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Creating objects");
		server = new Server();
		System.out.println("Server created");
		client = new Client();
		System.out.println("Client created");
		input = new Input(server, client);
		
		System.out.println("Starting objects");
		server.start();
		System.out.println("Server started");
		client.start();
		System.out.println("Client started");
		
		boolean amIServer = false;
		if (server.getIsPlayerOne()) {
			amIServer = true;
		}
		for (int i = 0; i < 9; i++) {
			input.waitForInput(amIServer);
			amIServer = !amIServer;
		}
	}
}
