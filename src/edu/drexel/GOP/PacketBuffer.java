package edu.drexel.GOP;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketBuffer extends Thread implements PacketAcceptor{
	private String data;
	private PrintWriter writer;
	private AtomicBoolean sentinel;
	private AtomicBoolean flush;
	final int FLUSH_TIMEOUT = 100;
	String id;
	
	public PacketBuffer(String name, PrintWriter output) {
		data = "";
		id = name;
		writer = output;
		sentinel = new AtomicBoolean(true);
		flush = new AtomicBoolean(false);
		start();
	}
	public PacketBuffer(String name) {
		this(name, null);
	}
	public void accept(int packet) {
		data += ((char)packet);
		flush.set(false);
	}
	public void setPrintWriter(PrintWriter out) {
		writer = out;
	}
	public void run() {
		while (sentinel.get()) {
			flush.set(true);
			try {
				Thread.sleep(FLUSH_TIMEOUT);
			}
			catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
			if(flush.get()) {
				if (!data.equals("")) {
					if(writer != null) {
						System.out.println(id+" : "+data);
						writer.print(data);
						writer.flush();
						data = "";
					}
					else {
						System.err.println("Couldn't send message: PrintWriter was null");
					}
				}
			}
		}
	}
}
