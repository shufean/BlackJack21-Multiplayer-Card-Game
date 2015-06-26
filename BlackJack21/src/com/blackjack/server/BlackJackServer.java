/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//This class consists of main method from where the execution starts for the com.blackjack.server section.
*/

package com.blackjack.server;

import java.util.*;

import javax.swing.*;

import com.blackjack.client.*;
import com.blackjack.server.*;
import com.blackjack.serverUtilities.*;

import java.awt.*;
import java.io.*;
import java.net.*;

public class BlackJackServer extends JFrame implements Runnable,PlayStatus{
	
	private Deck deck;
	private Player dealer;
	private Vector <Player> players;
	private Vector <Player> waitingPool;
	Vector<Card> cards; 
	private ScoreBoard scoreBoard;
	private int playersCount, waitingPlayersCount, whoseTurn, trial;
	private FileWriter fstream ;
	private FileInputStream instream;
	private Vector <String> clientlist;
	private int socket;
	private boolean continueValue;
	//To display out the messages
	int userID = 1;
	private JTextArea serverTextArea;
	


	
	/**
	 * 
	 * @throws IOException 
	 */

	//Constructor of a new BlackJackGame
	public BlackJackServer(int socket) throws IOException {
		
		playersCount = 0;
		waitingPlayersCount = 0;
		trial = 1;
		dealer = new Player();
		players = new Vector<Player>();
		waitingPool = new Vector<Player>();
		whoseTurn =1; 
		continueValue = this.readStatus();
		//whoseTurn = GameStatus.PLAYER1;
		clientlist = new Vector<String>();
		setupFrame();
		
		this.socket = socket;
		//System.out.println(continueValue);
		createConnections();
		
	}
		
	//initialise the deck, dealer and also players
	public void newGame(){
		
		deck = new Deck();
		deck.shuffleCards();
		trial = 1;
	}
	
	//restart the gameObject with another trial and less than 5
	public void restartGame() throws IOException{
		
		//initialise the deck and clear off the players and dealer
		//then add players from the waiting pool
		
		deck = new Deck();
		deck.shuffleCards();
		for(int indexOfPlayer=0; indexOfPlayer <= this.getPlayersCount(); indexOfPlayer++){
			 players.elementAt(indexOfPlayer).resetHand();
		}
		this.nextTrial();
		this.writeTurn(1);
		this.whoseTurn = 1;
	}
	
	
	/*****************ACTIONS**********************/
	
	public void deal(){
	//deal new card

		Card aCard = deck.dealTopCard();
		players.elementAt(whoseTurn -1).addCardToHand(aCard);
	}
	
	public boolean hitACard(){
		Card aCard = deck.dealTopCard();
		
		// allow total 5 trials
		if (players.elementAt(whoseTurn-1).getHand().getCardsTotal() < 5){
			players.elementAt(whoseTurn -1).addCardToHand(aCard);
			return true;
		}
		else
			return false;
	}
	
	public void stand() throws IOException{
		this.nextPlayer();
	}
	
	@Override
	public void run() {
		// generated method stub
		this.newGame();
	}
	
	/************* MODIFIER ******************************/

	public boolean addPlayer(Player player) {
		if (players.add(player))
			return true;
		return false;
	}

	public boolean addWaitingPlayers(Player player) {
		if (waitingPool.add(player))
			return true;
		return false;
	}

	public void removeWaitingPlayers() {
		waitingPool.removeAllElements();
	}

	public int nextPlayer() throws IOException {
		if (whoseTurn < players.size()) {
			whoseTurn++;

		} else
			whoseTurn = 1;
		this.writeTurn(whoseTurn);
		return whoseTurn;
	}

	public int nextTrial() {
		if (trial <= MAXTRIALS)
			trial++;
		else
			trial = 1;

		return trial;
	}

	public void addPlayersCount() {
		// increase the count of player
		playersCount++;
	}

	public void minusPlayersCount(){
		// decrease count of player	
		playersCount--;
	}

	public void addWaitingPlayersCount() {
		// add to waiting pool.
		waitingPlayersCount++;
	}

	public void minusWaitingPlayersCount() {
		// remove from waiting pool
		waitingPlayersCount--;
	}

	public void initScoreBoard(int numOfPlayers) {
		scoreBoard = new ScoreBoard(numOfPlayers);
	}

	public void setWinner(int playerTurn) {
		// set winner
		scoreBoard.setWinner(playerTurn);
	}

	/*************** ACCESSORS **************************/

	// implements a growable array of objects
	public Vector<Player> getPlayers() {

		return players;
	}

	public Player getDealer() {
		return dealer;
	}

	public Vector<Player> getWaitingPlayers() {
		return waitingPool;
	}

	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}

	public Deck getDeck() {
		return deck;
	}

	public int getWhoseTurn() throws IOException {
		whoseTurn = this.readTrun();
		return whoseTurn;
	}

	public int getTrial() {
		return trial;
	}

	public int getPlayersCount() {
		return playersCount;
	}

	// create connection
	private void createConnections() {
		try {
			// Create a com.blackjack.server socket
			ServerSocket serverSocket = new ServerSocket(this.socket);

			// Append a msg to the JTextArea
			append(new Date() + ": Server waiting for players to connect...\n");

			ServerSocketConnection connectionHandler = new ServerSocketConnection(
					this);

			while (true) {
				// if(getPlayersCount() <= MAXPLAYERS)
				connectionHandler.connect(serverSocket);

				// Create a new thread for this session of 6 players
				if (waitingPlayersCount == MAXPLAYERS) {
					ManageSession thread = new ManageSession(this);
					thread.start();
				}
			}
		} catch (IOException e) {
			append(e.toString());
		}
	}

	// setup the frame for the com.blackjack.server UI
	private void setupFrame() {
		serverTextArea = new JTextArea();
		serverTextArea.setEditable(false);

		// Create a scroll pane to hold text area
		JScrollPane scrollPane = new JScrollPane(serverTextArea);

		// Add the scroll pane to the frame
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ServerProperties.getServerWidth(), ServerProperties.getServerHeight());
		setTitle(ServerProperties.getServerTitle());
		setVisible(true);
	}

	/*************** DEFAULT METHODS **********************/

	void append(String msg) {
		serverTextArea.append(msg);
	}

	public void writeClientlist(String ipUsername) throws IOException {

		// for writing to the file
		fstream = new FileWriter(ServerProperties.getiPListFile(), true);
		BufferedWriter out = new BufferedWriter(fstream);

		// Database operation
		DatabaseConnection.setIntoUserTable(userID, ipUsername);
		// Database operation end
		
		out.write(ipUsername + System.getProperty("line.separator"));
		userID++;
		out.close();
	}

	public void writeClientHand(String filename, Hand hand) throws IOException {

		// for writing to the file.
		fstream = new FileWriter(filename + ".txt");
		BufferedWriter out = new BufferedWriter(fstream);

		for (int indexOfClientHand = 0; indexOfClientHand < hand.getHandSize(); indexOfClientHand++) {
			// Database operation
			String card = String.valueOf(hand.getCard(indexOfClientHand).getSuit());
			String value = String.valueOf(hand.getCard(indexOfClientHand).getCardValue());
			DatabaseConnection.setDataIntoCardsTable(filename, card, value);
			// Database operation end

			out.write(hand.getCard(indexOfClientHand).getSuit() + " " + hand.getCard(indexOfClientHand).getCardValue() + System.getProperty("line.separator"));
		}
		out.close();
	}

	// get the list of client
	public Vector<String> readClientList() throws IOException {
		instream = new FileInputStream(ServerProperties.getiPListFile());
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			clientlist.add(strLine);
			System.out.println(strLine);
		}
		in.close();
		return clientlist;
	}

	public Vector<Card> readClientHand(String fileName) throws IOException {

		cards = new Vector<Card>();
		Card acrd;
		instream = new FileInputStream(fileName + ".txt");
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		
		// Read File Line By Line
		StringTokenizer st;
		
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			st = new StringTokenizer(strLine);
			while (st.hasMoreTokens()) {
				String suit = st.nextToken();
				String val = st.nextToken();
				acrd = new Card(Integer.parseInt(suit), Integer.parseInt(val));

				cards.add(acrd);

			}
		}
		in.close();
		return cards;
	}

	public int readTrun() throws IOException {
		instream = new FileInputStream(ServerProperties.getTurnFile());
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();

		System.out.println(strLine);

		in.close();
		return Integer.parseInt(strLine);
	}

	public int readTrial() throws IOException {

		// writing to the file for log
		instream = new FileInputStream(ServerProperties.getTrialFile());
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();

		System.out.println(strLine);

		in.close();
		return Integer.parseInt(strLine);
	}

	public boolean readStatus() throws IOException {
		instream = new FileInputStream(ServerProperties.getContinueFile());
		DataInputStream in = new DataInputStream(instream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		strLine = br.readLine();

		System.out.println(strLine);

		in.close();
		if (strLine.equals("no"))
			return false;
		else
			return true;
	}

	// writing to the file for log
	public void writeStatus(String status) throws IOException {
		fstream = new FileWriter(ServerProperties.getContinueFile());
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(status + System.getProperty("line.separator"));
		out.close();
	}

	// writing to the file for log
	public void writeTurn(int turn) throws IOException {
		fstream = new FileWriter(ServerProperties.getTurnFile());
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(turn + System.getProperty("line.separator"));
		out.close();
	}

	// writing to the file for log
	public void writeTrial(int trial) throws IOException {
		fstream = new FileWriter(ServerProperties.getTrialFile());
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(trial + System.getProperty("line.separator"));
		out.close();
	}

	public static void main(String args[]) throws IOException {
		// BlackJackGame com.blackjack.server = new BlackJackGame();
		// com.blackjack.server.newGame();

	}
}
