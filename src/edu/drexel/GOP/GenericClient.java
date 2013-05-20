package edu.drexel.GOP;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class GenericClient extends Thread implements PacketAcceptor{
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private AtomicBoolean sentinel;
	private HashSet<PacketAcceptor> connections;
	private PacketBuffer buffer;
	
	public GenericClient(String hostName, int port) {
		sentinel = new AtomicBoolean(true);
		connections = new HashSet<PacketAcceptor>();
		try {
			socket = new Socket(hostName, port);
			
			System.out.println("Successfully connected to "+hostName+":"+port);
			
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			buffer = new PacketBuffer("Client",writer);
		}
		catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public GenericClient(int port) {
		this("localhost", port);
	}
	public void run() {
		while(sentinel.get()) {
			int input;
			try {
				while ((input = reader.read()) >= 0) {
					for (PacketAcceptor connection : connections) {
						connection.accept(input);
					}
				}
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	public void accept(int packet) {
		buffer.accept(packet);
	}
	public void addPacketAcceptor(PacketAcceptor connection) {
		connections.add(connection);
	}
	public void close() {
		sentinel.set(false);
		try {
			reader.close();
			writer.close();
			socket.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}