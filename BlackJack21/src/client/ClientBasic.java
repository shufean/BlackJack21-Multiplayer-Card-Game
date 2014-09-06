package client;




import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.*;

import clientActivities.*;


public class  ClientBasic extends JFrame implements Runnable, Status 
{
	private JTextArea jtaLog;
	private String username = null;
	private ClientHand hand = null;
	private int turn = 0, whoseTurn = 0, trials = 1;
	private boolean isContinue = true, isGameEnd = false, isMyTurn = true;
	
	//Talk to the server
	private Socket socket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	
	private String host ;
	//private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); //userInput is here
	private String fromServer;//string from server
	private String fromClient;//string from client
	private String actionString ;
	private BlackjackClient handler;
	//constructor for client to accept username,ipaddress
	public ClientBasic(String s , String u , int port ,BlackjackClient j )
	{
		username=u;
		host = s;
		handler = j;
		setupFrame();
		connectToServer(port);
	}
	
	public void run()
	{
		
		//get notification from the server to start the game
		try
		{
			//Message from server to clearify about turns and trials
			append("Server says: " + in.readUTF() + "\n");
			turn = in.readInt();//get player turn
		}
		catch(IOException ex)
		{
			//incase of exceptions abort
			System.err.println(ex);
			System.exit(ABORT);
		}
			
		try 
		{
			
			for (int trial = 0; trial < MAXTRIALS; trial++)
			{
				//For clearing old cards and results before starting each trial.
				handler.cleartable();
				hand = new ClientHand();
				// get the dealt cards (only two in the beginning)
				int max = in.readInt();
				System.out.println(max);
				for (int j=0; j<max; j++)
				{
			    	//get the suit then value
					int suit = in.readInt();
					int value = in.readInt();
					hand.addCard(new ClientCard(suit, value));
				}
				handler.updatetable(hand);
				append(in.readUTF());//get the trial number
				//append("\n\nWhose turn?\n"); 
				whoseTurn = in.readInt();//get whose turn number
				for (int play = whoseTurn-1; play < MAXPLAYERS; play++)
				{
					
					
					append("\n\nWhose turn?\n"); 
					whoseTurn = in.readInt();//get whose turn number
		
					//Server tell the client whose turn now
					append("Server says: " + in.readUTF() + "\n");
					//to specify the cards in hand of client.
					
					append("Cards On Hand now: " + hand.getCardsOnHand() + "\n");
					//to specify the total of cards in hand.
					append("Values on hand now = " + hand.calculateValue() + "\n");
					
					
					//if is my turn, i start hitting
					boolean hit = true;
					
					do
					{
						if (whoseTurn == turn)
						{
							sendAction();
							try
							{
							     actionString = in.readUTF();
							}
							catch (SocketException e)
							{
								this.connectToServer(4000);
							}	
							catch(EOFException e)
							{
								//reconnect if there are connection is unsuccesful
								this.faultTolerance(4000);
							}
							
							append("Player " + whoseTurn + " action: " + actionString + "\n");
							
							//get the suit then value of the card
							
							if (actionString.compareTo("HIT") == 0)
							{
								//compare to know action of client
								int suit = in.readInt();
								int value = in.readInt();
								hand.addCard(new ClientCard(suit, value));
								
								//show the card out in client side
								append("Cards on hand now: " + hand.getCardsOnHand() + "\n");
								append("Values on hand now = " + hand.calculateValue() + "\n");
								handler.updatetable(hand);
							}
						}
						
						
						
						try
						{
							hit = in.readBoolean();	
						}
						catch (SocketException e)
						{
							try {
								this.faultTolerance(4000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						catch(EOFException e)
						{
							this.faultTolerance(4000);
						}
															
					}while (hit);
					
					
				}
				//Read From server
				ClientHand hand1 = new ClientHand();
				int max1 = in.readInt();
				System.out.println(max1);
				for (int j=0; j<max1; j++)
				{
			    	//get the suit then value
					int suit = in.readInt();
					int value = in.readInt();
					hand1.addCard(new ClientCard(suit, value));
				}
				handler.updatetable_dealer(hand1);
				
				//Read from server isGameEnd?
				isGameEnd = in.readBoolean();
				
				//Read from server who's the winner
				int winner = (in.readInt());
				String winnerMsg = "";
							
				if (winner == DRAW)
				{
					winnerMsg = "This game is a draw";
					handler.update_status(winnerMsg);
				}
				else
				{
					if (winner == turn)
					{
						winnerMsg = "++++++++ You Won! ++++++++";
						handler.update_status(winnerMsg);
					}
					else if (winner != turn)
					{
						winnerMsg = "You Lose! =(";
					handler.update_status(winnerMsg);
					}
				}
				
				winnerMsg += "\n";
				
				append(winnerMsg);
			}
			
			
			append(in.readUTF());//get the scoreboard
			
			append("Thank you for playing the Server-Client BlackJack Game\n");
			append("See you again! =)\n\n");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/***************************************************
	 * PRIVATE METHODS
	****************************************************/
	//setup the frame for the client UI
	private void setupFrame()
	{
		jtaLog = new JTextArea();
		jtaLog.setEditable(false);
		
		//Create a scroll pane to hold text area
		JScrollPane scrollPane = new JScrollPane(jtaLog);
		
		//Add the scroll pane to the frame
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		//set default operation for close event.
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setTitle("BlackJack Game - " + this.username);
		
		//101 setVisible(true);
		
		append("Welcome to the Server-Client BlackJack Game\n");
		append("Game created by: Shufean,Saki\n");
		append("Please enjoy your stay with us =)\n\n");
	}
	
	//Connect to server to be put in awaiting pool
	private void connectToServer(int port)
	{
		//Try to connect to a server
		//catch exception if cannot connect to server
		try
		{
			//create a socket to connect to server
			socket = new Socket (host, port );//GameStatus.SERVERSOCKET
			
			//create I/O streams to send/receive from server
			out = new DataOutputStream(socket.getOutputStream());//send to server
			in = new DataInputStream(socket.getInputStream());//receive from server
		}
		catch(UnknownHostException e)
		{
			System.err.println("Don't know about the host: " + host + "\n");
			System.exit(ABORT);
		}
		
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to " + host + "\n");
			System.exit(ABORT);
		}
		
		
		try
		{
			out.writeUTF(username);	//send username over to server		
			fromServer = in.readUTF(); //get acknowledgement after input username
			append("Server says: " + fromServer + "\n");					
		}
		
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to " + host + "\n");
			System.exit(ABORT);
		}
	}
	
	
	private void faultTolerance(int port) throws IOException, InterruptedException
	{
		//Put the parent thread on wait for doing transparency during the fault tolerance
        Thread thr =new Thread(new ClientBasic(host,username,4000,handler));
		thr.start();
		this.hide();
		//wait till player turn comes
		this.wait();		
	}
	
	
	/******************THREAD ACTIONS
	 * @throws InterruptedException ********************/	
	//send action to server
	private  void  sendAction() throws IOException, InterruptedException
	{
		//System.out.println("Please insert your action number: ");
		//BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		//String inputAction = stdIn.readLine();
		String inputAction =handler.requestaction(this);	
		
			
		boolean validInput = false;
		
		//input the action
		while (!validInput)
		{
			try
			{
		      // the String to int conversion happens here
		      int action = Integer.parseInt(inputAction.trim());

		      // send the action to server
		     try
				{
		    	  out.writeInt(action); //actionString = in.readUTF();
				}
				catch (SocketException e)
				{
					this.faultTolerance(4000);
				}	
		     catch(EOFException e)
				{
					this.faultTolerance(4000);
				}
		      
		      validInput = true;
		      
		    }
		    catch (NumberFormatException nfe)
		    {
		      append("NumberFormatException: " + nfe.getMessage() + "\n");
		      append("Please try to input action again.\n");
		      System.out.println("Please insert your action number: ");
			  inputAction = handler.requestaction(this);
			  
		    }						    
		}
	}
	/***************************************************
	 * DEFAULT METHODS
	****************************************************/
	void append(String msg)
	{
		jtaLog.append(msg);
	}
	
	
	
}