package edu.drexel.GOP;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;

public class GenericMulticast extends Thread implements PacketAcceptor {
	private MulticastSocket socket;
	private AtomicBoolean sentinel;
	private HashSet<PacketAcceptor> connections;
	private InetAddress group;
	private int port;
	
	public GenericMulticast(String ip, int _port) {
		sentinel = new AtomicBoolean(true);
		connections = new HashSet<PacketAcceptor>();
		port = _port;
		try {
			socket = new MulticastSocket(port);
			group = InetAddress.getByName(ip);
			socket.joinGroup(group);
		}
		catch (UnknownHostException e) {
			System.err.println("Error while creating multicast socket: "+e.getMessage());
		}
		catch (IOException e) {
			System.err.println("Error while creating multicast socket: "+e.getMessage());
		}	
	}
	public void run() {
		byte[] buf = new byte[4];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		int data;
		while(sentinel.get()) {
			try {
				socket.receive(packet);
				data = GOPUtilities.byteArrayToInt(packet.getData());
				for(PacketAcceptor connection: connections) {
					connection.accept(data);
				}
			}
			catch (IOException e) {
				System.err.println("Error while receiving packet on multicast socket: "+e.getMessage());
			}	
		}
	}
	public void close() {
		try {
			socket.leaveGroup(group);
			socket.close();
		}
		catch (IOException e) {
			System.err.println("Error while closing multicast socket: "+e.getMessage());
		}
	}
	public void addPacketAcceptor(PacketAcceptor connection) {
		connections.add(connection);
	}
	public void accept(int packet) {
		byte[] data = GOPUtilities.intToByteArray(packet);
		try {
			socket.send(new DatagramPacket(data, data.length, group, port));
		}
		catch (IOException e) {
			System.err.println("Error while sending multicast data: "+e.getMessage());
		}
	}
	
}
