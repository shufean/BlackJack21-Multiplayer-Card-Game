/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//Replica of server responsible for fault tolerance and maintaining transparancy.
*/

package com.blackjack.server;

import java.io.IOException;

public class MirrorServerOne implements Runnable {
	private static BlackJackServer server;
	private static BlackJackServer server1; 
	
	public MirrorServerOne() throws IOException {
		// TODO Auto-generated constructor stub
		server =  new BlackJackServer(8000);
		server1 = new BlackJackServer(8002);
	}
	public static void main (String args[]) throws IOException
	{
		MirrorServerOne mirrorServerOneObj = new MirrorServerOne(); 
	}

	@Override
	public void run() {
		// thread not required for this server
	}
}
