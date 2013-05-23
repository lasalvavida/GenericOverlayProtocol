package edu.drexel.GOP;

public class Test {
	public static void main(String[] args) {
		MulticastProxy proxy = new MulticastProxy("228.5.6.7", 6789, 4444, 5222);
		proxy.start();
	}
}
