package edu.drexel.GOP;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class GenericServer extends Thread implements PacketAcceptor{
	private ServerSocket server;
	private Socket client;
	private PrintWriter writer;
	private BufferedReader reader;
	private AtomicBoolean sentinel;
	private HashSet<PacketAcceptor> connections;
	private PacketBuffer buffer;
	
	public GenericServer(int port) {
		super();
		sentinel = new AtomicBoolean(true);
		connections = new HashSet<PacketAcceptor>();
		buffer = new PacketBuffer("Server");
		try {
			server = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}
	public void run() {
		while (sentinel.get()) {
			int input;
			try {
				client = server.accept();
				
				System.out.println("Accepted connection from "+client.getLocalAddress()+":"+client.getLocalPort());
				
				writer = new PrintWriter(client.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				buffer.setPrintWriter(writer);
				
				while ((input = reader.read()) >= 0) {
					for (PacketAcceptor connection : connections) {
						connection.accept(input);
					}
				}
				
				writer.close();
				reader.close();
				client.close();
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	public void addPacketAcceptor(PacketAcceptor connection) {
		connections.add(connection);
	}
	public void accept(int packet) {
		buffer.accept(packet);
	}
	public void close() {
		sentinel.set(false);
		try {
			server.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
