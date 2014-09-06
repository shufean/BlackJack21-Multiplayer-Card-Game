//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//Replica of server responsible for fault tolerance and maintaining transparancy.
package server;

import java.io.IOException;

public class Mirror2 implements Runnable {
	private BlackJackServer server;
	private BlackJackServer server1; 
	public Mirror2() throws IOException {
		// TODO Auto-generated constructor stub
		server1 =  new BlackJackServer(4000);
		//server1 = new BlackJackGame(8002);
	}
	public static void main (String args[]) throws IOException
	{
		
		//new Thread(new FileGetter(file1, td1)).start();  
		//new Thread(new FileGetter(file2, td2)).start();
		//Thread t1 = new Thread(server);
		//Thread t2 = new Thread(server1);
		Mirror2 s = new Mirror2(); 
		//s.run();
	}

	@Override
	public void run() {
		
	}
}