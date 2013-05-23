package edu.drexel.GOP;

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class ClientListener extends Thread implements PacketAcceptor {
	private Socket client;
	private DataOutputStream writer;
	private DataInputStream reader;
	private AtomicBoolean sentinel;
	private GenericServer owner;
	
	public ClientListener(GenericServer _owner, Socket socket) {
		super();
		owner = _owner;
		sentinel = new AtomicBoolean(true);
		client = socket;
	}
	public void run() {
		int input;
		try {
			writer = new DataOutputStream(client.getOutputStream());
			reader = new DataInputStream(client.getInputStream());
		}
		catch (IOException e) {
			System.err.println("Error while initializing server stream: "+e.getMessage());
		}
		while (sentinel.get()) {
			try {
				while ((input = reader.read()) >= 0) {
					for(PacketAcceptor connection : owner.getPacketAcceptors()) {
						connection.accept(input);
					}
				}
			}
			catch (IOException e) {
				System.err.println("Error while server reading from socket: "+e.getMessage());
			}
		}
		try {
			writer.close();
			reader.close();
			client.close();
		}
		catch (IOException e) {
			System.err.println("Error while closing server stream: "+e.getMessage());
		}
	}
	public void accept(int packet) {
		System.out.print((char)packet);
		try {
			writer.write(packet);
		}
		catch (IOException e) {
			System.err.println("Error while server accepting packet: "+e.getMessage());
		}
	}
	public void close() {
		sentinel.set(false);
	}
}
