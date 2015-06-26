/**Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//This class accepts the players that are trying to connect to the com.blackjack.server and add them 
//to the waiting Pool inStream gameObject
*/

package com.blackjack.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.blackjack.serverUtilities.*;

class ServerSocketConnection implements PlayStatus
{
	private DataOutputStream outStream = null;
	private DataInputStream inStream = null;
	private String inputLine = null, outputLine = null;
	BlackJackServer gameObject = null;
	
	public ServerSocketConnection (BlackJackServer game)
	{
		this.gameObject = game;
		
	}
	
	// connect using server socket
	public void connect(ServerSocket serverSocket)
	{
		try
		{
			//accept the players that are trying to connect to the com.blackjack.server
			//and add them to the waitingPool inStream gameObject
			
			if (gameObject.getPlayersCount() < PlayStatus.MAXPLAYERS )
			{
				Socket playerSocket = serverSocket.accept();
				outStream = new DataOutputStream(playerSocket.getOutputStream());//send to com.blackjack.client
				inStream = new DataInputStream(playerSocket.getInputStream());//receive from com.blackjack.client
				
				gameObject.append("Accepted a com.blackjack.client...\n" );
				gameObject.append("Client connected. Awaiting username...\n");	
				
				//outputLine = "You are connected to com.blackjack.server. Please input your username.\n";
				//outStream.writeUTF("You are connected to com.blackjack.server. Please input your username.\n");//acknowledge user to input username
				//outStream.flush();					
				if ((inputLine = inStream.readUTF())!= null)
				{
					//get the username as string
					String username = inputLine;
					
					gameObject.append("Client has input username: "  + username + "\n");
					
					//outputLine = "You have entered username: " + username + "\n";					
					//outStream.writeUTF(outputLine);//send the msg to com.blackjack.client side
					//outStream.flush();					
					Player player = new Player(username,playerSocket);
					
					if (!gameObject.readStatus())
					gameObject.writeClientlist(username);
					if (gameObject.addWaitingPlayers(player))
					{	
						outputLine="You have entered username: " + username + "\n" + "You are added into waiting list.\nPlease wait for the other players to get connected...\n";
						outStream.writeUTF(outputLine);//send the msg to com.blackjack.client side
						outStream.flush();
						gameObject.addWaitingPlayersCount();
					}
					else
					{	
						outputLine="You are not added into the gameObject. Please try again.\n";
						outStream.writeUTF(outputLine);
					}
				}
				else
				{
					outStream.writeUTF("You are required to input a username\n");
				}
			}
			else
			{
				Socket playerSocket = serverSocket.accept();
				outStream = new DataOutputStream(playerSocket.getOutputStream());//send to com.blackjack.client
				outStream.writeUTF("Room is full. Please try again later.\n");
				
			}
				
		}
		catch(IOException ex)
		{
			System.err.println(ex);
			gameObject.append(ex.toString());
		}
		
	}
}
