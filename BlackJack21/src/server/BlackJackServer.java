//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//This class consists of main method from where the execution starts for the server section.


package server;

import java.util.*;

import client.*;
import server.*;
import serverActivities.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class BlackJackServer extends JFrame implements Runnable,BlackJackStatus
{
	private Deck deck;
	private BlackJackPlayer dealer;
	private Vector <BlackJackPlayer> blackJackPlayers;
	private Vector <BlackJackPlayer> waitingPool;
	Vector<Card> cards; 
	private BlackJackScoreBoard blackJackScoreBoard;
	private int playersCount, waitingPlayersCount, whoseTurn, trial;
	private FileWriter fstream ;
	private FileInputStream instream;
	private Vector <String> clientlist;
	private int socket;
	private boolean cont;
	//To display out the messages
	int userID = 1;
	private JTextArea jtaLog;
	/**
	 * 
	 * @throws IOException 
	 */

	//Constructor of a new BlackJackGame
	public BlackJackServer(int socket) throws IOException 
	{
		playersCount = 0;
		waitingPlayersCount = 0;
		trial = 1;
		dealer = new BlackJackPlayer();
		blackJackPlayers = new Vector<BlackJackPlayer>();
		waitingPool = new Vector<BlackJackPlayer>();
		whoseTurn =1; 
		cont = this.readstatus();
		//whoseTurn = GameStatus.PLAYER1;
		clientlist = new Vector<String>();
		setupFrame();
		
		this.socket = socket;
		//System.out.println(cont);
		createConnections();
		
	}
		
	//initialise the deck, dealer and also players
	public void newGame()
	{
		deck = new Deck();
		deck.shuffleCards();
		trial = 1;
	}
	
	//restart the game with another trial and less than 5
	public void restartGame() throws IOException
	{
		//initialise the deck and clear off the players and dealer
		//then add players from the waiting pool
		
		deck = new Deck();
		deck.shuffleCards();
		for(int i=0; i <= this.getPlayersCount(); i++)
		{
			 blackJackPlayers.elementAt(i).resetHand();
		}
		this.nextTrial();
		this.writeTurn(1);
		this.whoseTurn = 1;
	}
	
	
	/*****************Actions**********************/
	public void deal()
	//deal new card
	{
		Card aCard = deck.dealTopCard();
		blackJackPlayers.elementAt(whoseTurn -1).addCardToHand(aCard);
	}
	
	public boolean hit()
	{
		Card aCard = deck.dealTopCard();
		if (blackJackPlayers.elementAt(whoseTurn-1).getHand().getCardsTotal() < 5)
		{
			blackJackPlayers.elementAt(whoseTurn -1).addCardToHand(aCard);
			return true;
		}
		else
			return false;
	}
	
	public void stand() throws IOException
	{
		this.nextPlayer();
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		this.newGame();
	}
	
	/***************************************************
	 * MODIFIER
	****************************************************/
	public boolean addPlayer(BlackJackPlayer blackJackPlayer)
	{
		if(blackJackPlayers.add(blackJackPlayer))
			return true;
		return false;
	}
	
	public boolean addWaitingPlayers(BlackJackPlayer blackJackPlayer)
	{
		if(waitingPool.add(blackJackPlayer))
			return true;
		return false;
	}
	
	public void removeWaitingPlayers()
	{
		waitingPool.removeAllElements();		
	}
	
	
	public int nextPlayer() throws IOException
	{
		if(whoseTurn < blackJackPlayers.size())
		{
			whoseTurn ++;
			
		}
		else
			whoseTurn = 1;
		this.writeTurn(whoseTurn);
		return whoseTurn;
	}
	
	public int nextTrial()
	{
		if(trial <= MAXTRIALS)
			trial ++;
		else
			trial = 1;
			
		return trial;
	}
	
	public void addPlayersCount()
	{
		//increase the count of player
		playersCount++;
	}
	
	public void minusPlayersCount()
	//decrease count of player
	{
		playersCount--;
	}
	
	public void addWaitingPlayersCount()
	{
//add to waiting pool.
		waitingPlayersCount++;
	}
	
	public void minusWaitingPlayersCount()
	{//remove from waiting pool
		waitingPlayersCount--;
	}
	
	public void initScoreBoard(int numOfPlayers)
	{
		blackJackScoreBoard = new BlackJackScoreBoard(numOfPlayers);
	}
	
	public void setWinner(int playerTurn)
	{//set winner
		blackJackScoreBoard.setWinner(playerTurn);
	}
	
	/***************************************************
	 * ACCESSORS
	****************************************************/
	////implements a growable array of objects
	public Vector<BlackJackPlayer> getPlayers()
	{
		
		return blackJackPlayers;
	}
	
	public BlackJackPlayer getDealer()
	{
		return dealer;
	}
	
	public Vector<BlackJackPlayer> getWaitingPlayers()
	{
		return waitingPool;
	}
	
	public BlackJackScoreBoard getScoreBoard()
	{
		return blackJackScoreBoard;
	}
	
	public Deck getDeck()
	{
		return deck;
	}
	
	public int getWhoseTurn() throws IOException
	{
		whoseTurn = this.read_trun();
		return whoseTurn;
	}
	
	public int getTrial()
	{
		return trial;
	}
	
	public int getPlayersCount()
	{
		return playersCount;
	}
	
	/***************************************************
	 * PRIVATE METHODS
	****************************************************/
	//create connection
	private void createConnections()
	{
		try
		{
			//Create a server socket
			ServerSocket serverSocket = new ServerSocket(this.socket);
			
			
			//Append a msg to the JTextArea
			append(new Date() + ": Server waiting for players to connect...\n" );
			
			ManageConnection connectionHandler = new ManageConnection(this);
			
			while(true)
			{
				//if(getPlayersCount() <= MAXPLAYERS)
					connectionHandler.connect(serverSocket);
				
				//Create a new thread for this session of 6 players			
				if (waitingPlayersCount == MAXPLAYERS)
				{
					ManageSession thread = new ManageSession(this);
					thread.start();
				}
			}
		}
		catch (IOException e)
		{
			append(e.toString());
		}
	}
	
	//setup the frame for the server UI
	private void setupFrame()
	{
		jtaLog = new JTextArea();
		jtaLog.setEditable(false);
		
		//Create a scroll pane to hold text area
		JScrollPane scrollPane = new JScrollPane(jtaLog);
		
		//Add the scroll pane to the frame
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setTitle("BlackJack Game - 21");
		setVisible(true);
	}
	
	
	
	/***************************************************
	 * DEFAULT METHODS
	****************************************************/
	void append(String msg)
	{
		jtaLog.append(msg);
	}
	public void write_clientlist(String ipUsername) throws IOException
	{//for writing to the file
		fstream = new FileWriter("iplist.txt",true);
		BufferedWriter out = new BufferedWriter(fstream);
		
		//// 101
		DatabaseConnection.setIntoUserTable(userID, ipUsername);
		/// 101
		
		out.write(ipUsername+System.getProperty( "line.separator" ));
		userID++;
		out.close();
	}
	public void write_clienthand(String filename , Hand hand) throws IOException
	{//for writing to the file.
		fstream = new FileWriter(filename+".txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for(int i=0 ; i < hand.gethandsize();i++)
		{
			//// 101
			String card = String.valueOf(hand.getCard(i).getSuit());
			String value = String.valueOf(hand.getCard(i).getCardValue());
			DatabaseConnection.setDataIntoCardsTable(filename, card, value);
			/// 101
			
		out.write(hand.getCard(i).getSuit()+" "+hand.getCard(i).getCardValue()+System.getProperty( "line.separator" ));
		}
		out.close();
	}
	public Vector<String> read_clientList() throws IOException
	{
		instream = new FileInputStream("iplist.txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		  //Read File Line By Line
	    while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  clientlist.add(strLine);
	    	System.out.println (strLine);
		  }
	    in.close();
	    return clientlist;
	}
	public Vector<Card> read_clienthand(String s) throws IOException
	{
		
		cards= new Vector<Card>();
		Card acrd;
		instream = new FileInputStream(s+".txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		  //Read File Line By Line
		StringTokenizer st;
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		    st = new StringTokenizer(strLine);
		    while(st.hasMoreTokens()) { 
		    	String suit = st.nextToken(); 
		    	String val = st.nextToken();
		    	acrd = new Card(Integer.parseInt(suit),Integer.parseInt(val));
		    	
		    	cards.add(acrd);
		    	
		    }
		  }
	    in.close();
	    return cards;
	}
	public int read_trun () throws IOException
	{
		instream = new FileInputStream("turn.txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();
		
		    	System.out.println (strLine);
	    
		    in.close();
		    return Integer.parseInt(strLine);
	}
	public int read_trial() throws IOException
	{
		//writing to the file for log
		instream = new FileInputStream("trial.txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();
		
		    	System.out.println (strLine);
	    
		    in.close();
		    return Integer.parseInt(strLine);
	}
	public boolean readstatus () throws IOException
	{
		instream = new FileInputStream("continue.txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();
		
		    	System.out.println (strLine);
	    
		    in.close();
		    if (strLine.equals("no"))
		    	return false;
		    else
		    	return true;
	}
	public void writestatus(String status) throws IOException
	{//writing to the file for log
		fstream = new FileWriter("continue.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(status+System.getProperty("line.separator"));
		out.close();
	}
	public void writeTurn(int turn) throws IOException
	{//writing to the file for log
		fstream = new FileWriter("turn.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(turn+System.getProperty("line.separator"));
		out.close();
	}
	public void writeTrial(int trial) throws IOException
	{//writing to the file for log
		fstream = new FileWriter("trial.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(trial+System.getProperty("line.separator"));
		out.close();
	}
	public static void main (String args[]) throws IOException
	{
		//BlackJackGame server = new BlackJackGame();
		//server.newGame();
		
	}
}
