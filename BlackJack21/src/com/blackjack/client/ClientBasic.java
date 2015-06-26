/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//This class contains the basic operations performed by client.
 */
package com.blackjack.client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.*;

import com.blackjack.clientUtilities.*;

public class ClientBasic extends JFrame implements Runnable, Status {

	private JTextArea textAreaObj;
	private String clientUserName = null;
	private ClientHand playerHand = null;
	private int turn = 0, whoseTurn = 0, trials = 1;
	private boolean isContinue = true, isGameEnd = false, isMyTurn = true;

	// Talk to the com.blackjack.server
	private Socket socket = null;
	private DataOutputStream outStream = null;
	private DataInputStream inStream = null;

	private String host;

	// private BufferedReader stdIn = new BufferedReader(new
	// InputStreamReader(System.in));
	// userInput is here
	private String fromServer;// string from com.blackjack.server
	private String fromClient;// string from com.blackjack.client
	private String actionString;
	private BlackjackClient clientHandler;

	// constructor for com.blackjack.client to accept clientUserName,ipaddress
	public ClientBasic(String hostName, String userName, int port, BlackjackClient clientObj) {
		clientUserName = userName;
		host = hostName;
		clientHandler = clientObj;
		setupFrame();
		connectToServer(port);
	}

	public void run() {
		// get notification from the com.blackjack.server to startButtonAction the gameObject
		try {
			// Message from com.blackjack.server to clearify about turns and trials
			appendMessage("Server says: " + inStream.readUTF() + "\n");
			turn = inStream.readInt();// get player turn
		} catch (IOException ex) {
			// incase of exceptions abort
			System.err.println(ex);
			System.exit(ABORT);
		}

		try {

			for (int trialIndex = 0; trialIndex < MAXTRIALS; trialIndex++) {
				// For clearing old cards and results before starting each
				// trial.
				clientHandler.clearTable();
				playerHand = new ClientHand();
				// get the dealt cards (only two inStream the beginning)
				int max = inStream.readInt();
				System.out.println(max);
				
				for (int tempIndex = 0; tempIndex < max; tempIndex++) {
					// get the suit then value
					int cardSuit = inStream.readInt();
					int cardValue = inStream.readInt();
					playerHand.addCard(new ClientCard(cardSuit, cardValue));
				}
				clientHandler.updateTable(playerHand);
				appendMessage(inStream.readUTF());// get the trial number
				
				// append("\n\nWhose turn?\n");
				whoseTurn = inStream.readInt();// get whose turn number
				
				for (int playIndex = whoseTurn - 1; playIndex < MAXPLAYERS; playIndex++) {

					appendMessage("\n\nWhose turn?\n");
					whoseTurn = inStream.readInt();// get whose turn number

					// Server tell the com.blackjack.client whose turn now
					appendMessage("Server says: " + inStream.readUTF() + "\n");
					// to specify the cards inStream playerHand of
					// com.blackjack.client.

					appendMessage("Cards On Hand now: "+ playerHand.getCardsOnHand() + "\n");

					// to specify the total of cards inStream playerHand.
					appendMessage("Values on playerHand now = "+ playerHand.calculateValue() + "\n");

					// if is my turn, i startButtonAction hitting
					boolean hit = true;

					do {
						if (whoseTurn == turn) {
							sendAction();
							try {
								actionString = inStream.readUTF();
							} catch (SocketException e) {
								this.connectToServer(ClientProperties.SERVER_PORT);
							} catch (EOFException e) {
								// reconnect if there are connection is
								// unsuccesful
								this.faultTolerance(ClientProperties.SERVER_PORT);
							}

							appendMessage("Player " + whoseTurn + " action: "+ actionString + "\n");

							// get the suit then value of the card

							if (actionString.compareTo("HIT") == 0) {
								// compare to know action of
								// com.blackjack.client
								int cardSuit = inStream.readInt();
								int cardValue = inStream.readInt();
								playerHand.addCard(new ClientCard(cardSuit, cardValue));

								// show the card outStream inStream
								// com.blackjack.client side
								appendMessage("Cards on playerHand now: " + playerHand.getCardsOnHand() + "\n");
								appendMessage("Values on playerHand now = " + playerHand.calculateValue() + "\n");
								clientHandler.updateTable(playerHand);
							}
						}

						try {
							hit = inStream.readBoolean();
						} catch (SocketException e) {
							try {
								this.faultTolerance(ClientProperties.SERVER_PORT);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (EOFException e) {
							this.faultTolerance(ClientProperties.SERVER_PORT);
						}

					} while (hit);
				}
				
				// Read From com.blackjack.server
				ClientHand clientHandOne = new ClientHand();
				int maxValueRead = inStream.readInt();
				System.out.println(maxValueRead);
				for (int maxIndex = 0; maxIndex < maxValueRead; maxIndex++) {
					// get the suit then value
					int CardSuit = inStream.readInt();
					int cardValue = inStream.readInt();
					clientHandOne.addCard(new ClientCard(CardSuit, cardValue));
				}
				clientHandler.updateTableDealer(clientHandOne);

				// Read from com.blackjack.server isGameEnd?
				isGameEnd = inStream.readBoolean();

				// Read from com.blackjack.server who's the winner
				int winner = (inStream.readInt());
				String winnerMsg = "";

				if (winner == DRAW) {
					winnerMsg = ClientProperties.DRAW_MESSAGE;
					clientHandler.updateStatus(winnerMsg);
				} else {
					if (winner == turn) {
						winnerMsg = ClientProperties.WINNING_MESSAGE;
						clientHandler.updateStatus(winnerMsg);
					} else if (winner != turn) {
						winnerMsg = ClientProperties.LOSE_MESSAGE;
						clientHandler.updateStatus(winnerMsg);
					}
				}

				winnerMsg += "\n";

				appendMessage(winnerMsg);
			}

			appendMessage(inStream.readUTF());// get the scoreboard

			appendMessage(ClientProperties.THANKYOU_MESSAGE);
			
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**************** PRIVATE METHODS *******************/

	// setup the frame for the com.blackjack.client UI
	private void setupFrame() {
		textAreaObj = new JTextArea();
		textAreaObj.setEditable(false);

		// Create a scroll pane to hold text area
		JScrollPane scrollPane = new JScrollPane(textAreaObj);

		// Add the scroll pane to the frame
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// set default operation for close event.

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(ClientProperties.CLIENT_FRAME_WIDTH, ClientProperties.CLIENT_FRAME_HEIGHT);
		setTitle(ClientProperties.CLIENT_TITLE + this.clientUserName);

		// 101 setVisible(true);

		//appendMessage("Welcome to the Server-Client BlackJack Game\n");
		//appendMessage("Game created by: Shufean\n");
		//appendMessage("Please enjoy your stay with us =)\n\n");
	}

	// Connect to com.blackjack.server to be put inStream awaiting pool
	private void connectToServer(int port) {
		
		// Try to connect to a com.blackjack.server
		// catch exception if cannot connect to com.blackjack.server
		try {
			// create a socket to connect to com.blackjack.server
			socket = new Socket(host, port);// GameStatus.SERVERSOCKET

			// create I/O streams to send/receive from com.blackjack.server
			outStream = new DataOutputStream(socket.getOutputStream());// send to com.blackjack.server
			inStream = new DataInputStream(socket.getInputStream());// receive from com.blackjack.server
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about the host: " + host + "\n");
			System.exit(ABORT);
		}

		catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + host + "\n");
			System.exit(ABORT);
		}

		try {
			outStream.writeUTF(clientUserName); // send clientUserName over to
												// com.blackjack.server
			fromServer = inStream.readUTF(); // get acknowledgement after input
												// clientUserName
			appendMessage("Server says: " + fromServer + "\n");
		}

		catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + host+ "\n");
			System.exit(ABORT);
		}
	}

	private void faultTolerance(int port) throws IOException,
			InterruptedException {
		// Put the parent thread on wait for doing transparency during the fault
		// tolerance
		Thread threadObject = new Thread(new ClientBasic(host, clientUserName, ClientProperties.SERVER_PORT, clientHandler));
		threadObject.start();
		this.hide();
		// wait till player turn comes
		this.wait();
	}

	/****************** THREAD ACTIONS ********************/

	// @throws InterruptedException
	// send action to com.blackjack.server
	private void sendAction() throws IOException, InterruptedException {
		// System.out.println("Please insert your action number: ");
		// BufferedReader stdIn = new BufferedReader(new
		// InputStreamReader(System.in));
		// String inputAction = stdIn.readLine();
		String inputAction = clientHandler.requestAction(this);

		boolean validInput = false;

		// input the action
		while (!validInput) {
			try {
				// the String to int conversion happens here
				int action = Integer.parseInt(inputAction.trim());

				// send the action to com.blackjack.server
				try {
					outStream.writeInt(action); // actionString =inStream.readUTF();
				} catch (SocketException e) {
					this.faultTolerance(ClientProperties.SERVER_PORT);
				} catch (EOFException e) {
					this.faultTolerance(ClientProperties.SERVER_PORT);
				}

				validInput = true;

			} catch (NumberFormatException nfe) {
				appendMessage("NumberFormatException: " + nfe.getMessage()+ "\n");
				appendMessage("Please try to input action again.\n");
				System.out.println("Please insert your action number: ");
				inputAction = clientHandler.requestAction(this);

			}
		}
	}

	/*************** DEFAULT METHODS ********************/
	void appendMessage(String msg) {
		textAreaObj.append(msg);
	}

}