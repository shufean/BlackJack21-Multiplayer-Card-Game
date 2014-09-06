//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//Replica of server responsible for fault tolerance and maintaining transparancy.

package server;

import java.io.IOException;

public class Mirror1 implements Runnable {
	private static BlackJackServer server;
	private static BlackJackServer server1; 
	public Mirror1() throws IOException {
		// TODO Auto-generated constructor stub
		server =  new BlackJackServer(8000);
		server1 = new BlackJackServer(8002);
	}
	public static void main (String args[]) throws IOException
	{
		Mirror1 s = new Mirror1(); 
	}

	@Override
	public void run() {
		
	}
}
