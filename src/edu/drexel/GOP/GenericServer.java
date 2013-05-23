package edu.drexel.GOP;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class GenericServer extends Thread implements PacketAcceptor{
	private HashSet<ClientListener> servers;
	private ServerSocket socket;
	private HashSet<PacketAcceptor> connections;
	private AtomicBoolean sentinel;
	
	public GenericServer(int port) {
		super();
		connections = new HashSet<PacketAcceptor>();
		servers = new HashSet<ClientListener>();
		sentinel = new AtomicBoolean(true);
		try {
			socket = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}
	public void run() {
		while(sentinel.get()) {
			Socket client = null;
			try {
				client = socket.accept();
			}
			catch (IOException e) {
				System.err.println("Error while accepting client connection: "+e.getMessage());
			}
			if(client != null) {
				ClientListener spawn = new ClientListener(this, client);
				servers.add(spawn);
				System.out.println("Accepted connection from "+client.getLocalAddress()+":"+client.getLocalPort());
				spawn.start();
			}
			else {
				System.err.println("Accepted connection from null client!");
			}
		}
	}
	public void addPacketAcceptor(PacketAcceptor connection) {
		connections.add(connection);
	}
	public void accept(int packet) {
		for(ClientListener server : servers) {
			server.accept(packet);
		}
	}
	public void close() {
		for(ClientListener server : servers) {
			server.close();
		}
		try {
			socket.close();
		}
		catch (IOException e) {
			System.err.println("Error while trying to close server socket: "+e.getMessage());
		}
	}
	public HashSet<PacketAcceptor> getPacketAcceptors() {
		return connections;
	}
}
