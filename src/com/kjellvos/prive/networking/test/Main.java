package com.kjellvos.prive.networking.test;
import java.io.IOException;
import java.util.Random;

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
    	Random random = new Random();
    	server.setIsPlayerOne(random.nextBoolean());
		server.start();
		System.out.println("Server started");
		client.start();
		System.out.println("Client started");
		
		boolean amIServer = server.getIsPlayerOne();
		for (int i = 0; i < 9; i++) {
			input.waitForInput(amIServer);
			amIServer = !amIServer;
		}
	}
}
