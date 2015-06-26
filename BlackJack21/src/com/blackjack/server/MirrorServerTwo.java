/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//Replica of server responsible for fault tolerance and maintaining transparancy.
*/

package com.blackjack.server;

import java.io.IOException;

public class MirrorServerTwo implements Runnable {
	private BlackJackServer server;
	private BlackJackServer server1; 
	
	public MirrorServerTwo() throws IOException {
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
		MirrorServerTwo mirrorServertwo = new MirrorServerTwo(); 
		//s.run();
	}

	@Override
	public void run() {
		// thread not required for this server2
	}
}