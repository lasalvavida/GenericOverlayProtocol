package edu.drexel.GOP;

public class MulticastProxy {
	GenericServer server;
	GenericClient client;
	GenericMulticast multi;
	public MulticastProxy(String multiAddress, int multiPort, int fromPort, int toPort) {
		server = new GenericServer(fromPort);
		client = new GenericClient(toPort);
		server.addPacketAcceptor(client);
		multi = new GenericMulticast(multiAddress, multiPort);
		multi.addPacketAcceptor(server);
		client.addPacketAcceptor(multi);
	}
	public void start() {
		server.start();
		client.start();
		multi.start();
	}
	public void close() {
		server.close();
		client.close();
		multi.close();
	}
}
