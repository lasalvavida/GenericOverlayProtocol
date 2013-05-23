package edu.drexel.GOP;

public class GenericProxy {
	GenericServer server;
	GenericClient client;
	public GenericProxy(int fromPort, int toPort) {
		server = new GenericServer(fromPort);
		client = new GenericClient(toPort);
		server.addPacketAcceptor(client);
		client.addPacketAcceptor(server);
	}
	public void start() {
		server.start();
		client.start();
	}
	public void close() {
		server.close();
		client.close();
	}
}
