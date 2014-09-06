//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//This class accepts the players that are trying to connect to the server and add them 
//to the waiting Pool in game

package server;

import java.io.*;
import java.net.*;
import java.util.*;

import serverActivities.*;

class HandleConnection implements GameStatus
{
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private String inputLine = null, outputLine = null;
	BlackJackServer game = null;
	
	public HandleConnection (BlackJackServer game)
	{
		this.game = game;
		
	}
	
	public void connect(ServerSocket serverSocket)
	{
		try
		{
			//accept the players that are trying to connect to the server
			//and add them to the waitingPool in game
			
			if (game.getPlayersCount() < GameStatus.MAXPLAYERS )
			{
				Socket playerSocket = serverSocket.accept();
				out = new DataOutputStream(playerSocket.getOutputStream());//send to client
				in = new DataInputStream(playerSocket.getInputStream());//receive from client
				
				game.append("Accepted a client...\n" );
				game.append("Client connected. Awaiting username...\n");				
				//outputLine = "You are connected to server. Please input your username.\n";
				//out.writeUTF("You are connected to server. Please input your username.\n");//acknowledge user to input username
				//out.flush();
				
				
				if ((inputLine = in.readUTF())!= null)
				{
					String username = inputLine;//get the username as string
					
					game.append("Client has input username: "  + username + "\n");
					//outputLine = "You have entered username: " + username + "\n";					
					//out.writeUTF(outputLine);//send the msg to client side
					//out.flush();
					
					Player player = new Player(username,playerSocket);
					if (!game.readstatus())
					game.write_clientlist(username);
					if (game.addWaitingPlayers(player))
					{	
						outputLine="You have entered username: " + username + "\n" + "You are added into waiting list.\nPlease wait for the other players to get connected...\n";
						out.writeUTF(outputLine);//send the msg to client side
						out.flush();
						game.addWaitingPlayersCount();
					}
					else
					{	
						outputLine="You are not added into the game. Please try again.\n";
						out.writeUTF(outputLine);
					}
				}
				else
				{
					out.writeUTF("You are required to input a username\n");
				}
			}
			else
			{
				Socket playerSocket = serverSocket.accept();
				out = new DataOutputStream(playerSocket.getOutputStream());//send to client
				out.writeUTF("Room is full. Please try again later.\n");
				
			}
				
		}
		catch(IOException ex)
		{
			System.err.println(ex);
			game.append(ex.toString());
		}
		
	}
}
