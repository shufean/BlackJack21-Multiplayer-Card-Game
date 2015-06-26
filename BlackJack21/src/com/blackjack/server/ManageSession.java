/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//This class is responsible for handling the gameObject sessions between com.blackjack.client and com.blackjack.server.
//It has socket connection and maintains communication between the players via socket. 
//This class is to handle the gameObject sessions between com.blackjack.client and com.blackjack.server
 */

package com.blackjack.server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.blackjack.serverUtilities.*;

/**
 * This class is to handle the gameObject sessions between
 * com.blackjack.client and com.blackjack.server
 */

class ManageSession extends Thread implements PlayStatus {
	private Socket playerSocket[] = null;
	private DataInputStream fromPlayer[] = null;
	private DataOutputStream toPlayer[] = null;
	private BlackJackServer gameObject = null;
	private Vector<Player> players;
	private boolean isGameEnd = false;
	private int whoseTurn = 0;
	private String winnerMsg = "";
	private Vector<String> clients;
	private Vector<Card> cards;

	public ManageSession(BlackJackServer game) throws IOException {
		// initialise all the variables
		players = game.getWaitingPlayers();
		playerSocket = new Socket[MAXPLAYERS];
		fromPlayer = new DataInputStream[MAXPLAYERS];
		toPlayer = new DataOutputStream[MAXPLAYERS];
		this.gameObject = game;

		// Add the playing from waiting list to player list
		for (int indexOfPlayers = 0; indexOfPlayers < players.size() && indexOfPlayers < PlayStatus.MAXPLAYERS; indexOfPlayers++) {
			if (game.addPlayer(players.elementAt(indexOfPlayers))) {
				game.addPlayersCount();
				game.minusWaitingPlayersCount();
			}
		}
		clients = game.readClientList();

		// Show the total players joined for the gameObject.
		game.append("Total players joined: " + game.getPlayersCount() + "\n\n");

		// add  sorting and condition
		for (int indexOfPlayer = 0; indexOfPlayer <= MAXPLAYERS - 1; indexOfPlayer++) {
			System.out.println(players.get(indexOfPlayer).getSocket().getInetAddress().toString()+ " " + players.get(indexOfPlayer).getUsername());
			System.out.println(clients.get(indexOfPlayer));
		}

		// The dealer is also one of the player. Add the dealer into the player list.
		game.addPlayer(game.getDealer());

		if (game.readStatus()) {
			Player temp;

			// sort the clients according to ip list
			for (int indexOfClients = 0; indexOfClients < clients.size(); indexOfClients++){
				for (int indexOfPlayer = indexOfClients; indexOfPlayer < game.getPlayersCount(); indexOfPlayer++) {
					if (clients.get(indexOfClients).equals(game.getPlayers().elementAt(indexOfPlayer).getUsername())) {
						temp = game.getPlayers().elementAt(indexOfClients);
						game.getPlayers().set(indexOfClients, game.getPlayers().elementAt(indexOfPlayer));
						game.getPlayers().set(indexOfPlayer, temp);
					}

				}
			}
		}

		players = game.getPlayers();
		// reusing players

		System.out.println(players.get(0).getUsername());
		System.out.println(clients.toString());
		cards = new Vector<Card>();

		// Remove the players from waiting list after added.
		game.removeWaitingPlayers();// to change for connection
	}

	public void writeHand(int handIndex) throws IOException {

		gameObject.writeClientHand(players.get(handIndex).getUsername(), players.get(handIndex).getHand());

	}

	// startButtonAction the thread here
	public void run() {
		// create the scoreboard
		gameObject.initScoreBoard(gameObject.getPlayersCount());

		try {
			// initilise all the sockets and data I/O streams
			for (int indexOfPlayer = 0; indexOfPlayer < gameObject.getPlayersCount(); indexOfPlayer++) {
				playerSocket[indexOfPlayer] = players.elementAt(indexOfPlayer).getSocket();
				fromPlayer[indexOfPlayer] = new DataInputStream(
						playerSocket[indexOfPlayer].getInputStream());
				toPlayer[indexOfPlayer] = new DataOutputStream(
						playerSocket[indexOfPlayer].getOutputStream());
			}

			// write to players and notify their turn
			for (int playerIterator = 0; playerIterator < gameObject.getPlayersCount(); playerIterator++) {
				players.elementAt(playerIterator).setPlayerTurn(playerIterator + 1);
				int turn = players.elementAt(playerIterator).getPlayerTurn();
				
				toPlayer[playerIterator].writeUTF("Game started, your turn is: " + turn);
				toPlayer[playerIterator].writeInt(turn);
			}

			for (int trial = gameObject.readTrial(); trial < MAXTRIALS; trial++) {
				// TODO
				gameObject.writeTrial(trial);
				
				// get current trial number
				// startButtonAction a new gameObject
				if (trial == 0)
					gameObject.newGame();
				else if (trial != 0)
					gameObject.restartGame();

				// distribute to each player 2 cards (dealer the last)
				if (!gameObject.readStatus()) {
					for (int j = 0; j < 2; j++) {
						for (int indexOfPlayer = 0; indexOfPlayer < gameObject.getPlayersCount() + 1 /*including the dealer*/; indexOfPlayer++) {
							gameObject.deal();
							gameObject.nextPlayer();
						}
					}

					for (int i = 0; i < gameObject.getPlayersCount() + 1; i++) {
						this.writeHand(i);
					}

				} else {
					Card acard;
					for (int indexOfPlayer = 0; indexOfPlayer < gameObject.getPlayersCount() + 1; indexOfPlayer++) {
						cards = gameObject.readClientHand(players.get(indexOfPlayer).getUsername());
						for (int cardIndex = 0; cardIndex < cards.size(); cardIndex++) {
							acard = cards.get(cardIndex);
							gameObject.getPlayers().elementAt(indexOfPlayer).getHand().addCard(acard);
						}

					}

				}
				
				// Tell the players the two card they are given
				// This approach is implemented for GUI based
				for (int i = 0; i < gameObject.getPlayersCount(); i++) {

					System.out.println(gameObject.getPlayers().elementAt(i).getHand().getHandSize());
					toPlayer[i].writeInt(gameObject.getPlayers().elementAt(i).getHand().getHandSize());

				}

				for (int indexOfPlayer = 0; indexOfPlayer < players.size() && indexOfPlayer < PlayStatus.MAXPLAYERS; indexOfPlayer++) {
					for (int playerHandIndex = 0; playerHandIndex < gameObject.getPlayers().elementAt(indexOfPlayer).getHand().getHandSize(); playerHandIndex++) {
						if (indexOfPlayer < MAXPLAYERS) {
							// write the suit then value
							int suit = gameObject.getPlayers().elementAt(indexOfPlayer).getHand().getCard(playerHandIndex).getSuit();
							int value = gameObject.getPlayers().elementAt(indexOfPlayer).getHand().getCard(playerHandIndex).getCardValue();

							toPlayer[indexOfPlayer].writeInt(suit);
							toPlayer[indexOfPlayer].writeInt(value);
						}
					}
				}

				gameObject.writeStatus("yes");
				gameObject.append("\n Trial = " + gameObject.getTrial() + " \n");

				// List down the cards that each players has in
				// com.blackjack.server side
				for (int indexOfPlayer = 0; indexOfPlayer < players.size() && indexOfPlayer < PlayStatus.MAXPLAYERS + 1; indexOfPlayer++) {
					Hand hand = gameObject.getPlayers().elementAt(indexOfPlayer).getHand();
					if (indexOfPlayer < MAXPLAYERS)
						gameObject.append("Player " + (indexOfPlayer + 1) + " has "+ hand.getCardsOnHand() + "\n");
					else
						gameObject.append("Dealer has " + hand.getCardsOnHand()+ "\n");
				}

				// Get whose turn for this gameObject
				whoseTurn = gameObject.getWhoseTurn();

				for (int i = 0; i < players.size() && i < PlayStatus.MAXPLAYERS; i++) {
					// tell the players trial number
					toPlayer[i].writeUTF("\nTrial = " + gameObject.getTrial());
				}

				// Tell all the players in the player list about their turns
				for (int i = 0; i < players.size() && i < PlayStatus.MAXPLAYERS; i++) {
					// tell the players trial number

					toPlayer[i].writeInt(gameObject.getWhoseTurn());
				}

				while (whoseTurn < DEALER && whoseTurn < players.size()) {
					// gameObject.append("----------WhoseTurn2:----------" +
					// gameObject.getWhoseTurn() +"\n");

					gameObject.append("\nNow is Player "+ gameObject.getWhoseTurn()+ " turn.\n Waiting player to perform action.\n");

					// Notify player to startButtonAction
					// toPlayer[gameObject.getWhoseTurn()-1].writeInt(gameObject.getWhoseTurn());
					for (int indexOfPlayer = 0; indexOfPlayer < players.size() && indexOfPlayer < PlayStatus.MAXPLAYERS; indexOfPlayer++) {
						// tell the players trial number
						// toPlayer[i].writeUTF("\nTrial = " +
						// gameObject.getTrial());

						// send an integer to com.blackjack.client
						// indicating whose turn now
						toPlayer[indexOfPlayer].writeInt(gameObject.getWhoseTurn());

						if ((indexOfPlayer + 1) == gameObject.getWhoseTurn()) {
							toPlayer[indexOfPlayer].writeUTF("Your turn now.\nYou may type 1 to HIT, or 2 to STAND.\n");

						} else
							toPlayer[indexOfPlayer].writeUTF("Now is Player "+ gameObject.getWhoseTurn()+ " turn.\nWaiting player to perform action...\n");

					}

					// while the player is still elligible to continue hitting,
					// hit one card to player
					boolean continueHit = true;
					String actionString = "", cards = "";

					while (continueHit) {
						int action = fromPlayer[gameObject.getWhoseTurn() - 1].readInt();

						if (action == HIT) {
							if (gameObject.getPlayers().elementAt(gameObject.getWhoseTurn() - 1).getHand().getCardsTotal() < 5) {
								actionString = "HIT";
								// hit card if com.blackjack.client select hit
								
								gameObject.hitACard();
								this.writeHand(gameObject.getWhoseTurn() - 1);

								// calculate the total of the card and send to
								// the com.blackjack.client side
								continueHit = true;
							} else
								action = STAND;
						}
						if (action == STAND) {
							actionString = "STAND";
							continueHit = false;
						} else if (action == HIT)
							actionString = "HIT";
						else {
							actionString = "INVALID";
							continueHit = true;

						}

						// Tell the player its action
						toPlayer[gameObject.getWhoseTurn() - 1].writeUTF(actionString);

						// tell the com.blackjack.client the cards on hand
						// that he has
						// write the suit then value
						if (action == HIT) {
							int suit = gameObject.getPlayers().elementAt(gameObject.getWhoseTurn() - 1).getHand().getLastCard().getSuit();
							int value = gameObject.getPlayers().elementAt(gameObject.getWhoseTurn() - 1).getHand().getLastCard().getCardValue();
							toPlayer[gameObject.getWhoseTurn() - 1].writeInt(suit);
							toPlayer[gameObject.getWhoseTurn() - 1].writeInt(value);
						}

						// tell all the player that current player can / cannot
						// continue hitting
						for (int playerIndex = 0; playerIndex < players.size()&& playerIndex < PlayStatus.MAXPLAYERS; playerIndex++)
							toPlayer[playerIndex].writeBoolean(continueHit);

						String msg = "Player action: " + actionString + "\n";
						gameObject.append(msg);
						gameObject.append("Player " + gameObject.getWhoseTurn() + " has cards: " + gameObject.getPlayers().elementAt(gameObject.getWhoseTurn() - 1).getHand().getCardsOnHand() + "\n");
						
						gameObject.append("Value on Player " + gameObject.getWhoseTurn() + " hand: " + gameObject.getPlayers().elementAt(whoseTurn - 1).getHand().calculateValue() + '\n');

						if (!continueHit && action == STAND) {
							gameObject.stand();
							whoseTurn = gameObject.getWhoseTurn();
						}
					}
				}

				// now dealer's turn to hit/stand
				while (gameObject.getDealer().getHand().isUnder17())
					gameObject.hitACard();

				// show the cards in dealer's hand
				gameObject.append("Dealer has cards: "
						+ gameObject.getPlayers().elementAt(whoseTurn - 1).getHand().getCardsOnHand() + '\n');
				
				gameObject.append("Value on dealer's hand: " + gameObject.getPlayers().elementAt(whoseTurn - 1)
								.getHand().calculateValue() + '\n');

				// send to com.blackjack.client
				for (int i = 0; i < gameObject.getPlayersCount(); i++) {

					toPlayer[i].writeInt(gameObject.getPlayers().elementAt(whoseTurn - 1).getHand().getHandSize());
				}
				
				for (int playerIndex = 0; playerIndex < gameObject.getPlayersCount(); playerIndex++) {
					for (int playerHandIndex = 0; playerHandIndex < gameObject.getPlayers().elementAt(whoseTurn - 1).getHand().getHandSize(); playerHandIndex++) {

						// write the suit then value
						int suit = gameObject.getPlayers() .elementAt(whoseTurn - 1).getHand().getCard(playerHandIndex).getSuit();
						int value = gameObject.getPlayers() .elementAt(whoseTurn - 1).getHand().getCard(playerHandIndex).getCardValue();

						toPlayer[playerIndex].writeInt(suit);
						toPlayer[playerIndex].writeInt(value);
					}
				}


				// display the results
				int playerValue[] = new int[gameObject.getPlayersCount()];
				int highestPoint;
				int winnerturn;
				int[] winnerTurn = new int[MAXPLAYERS];
				int index = 0;
				boolean hasMoreWinner = false, hasTwoAce = false, hasBlackJack = false; /*counter keep track of two or more winner */

				// count the dealer's cards first
				Hand dealerHand = gameObject.getDealer().getHand();
				int dealerValue = dealerHand.calculateValue(), dealerTurn = gameObject.getPlayersCount() + 1;

				// if the dealer has only two cards and is blackjack / two aces
				if (dealerHand.isOnlyTwoCards()) {
					if (dealerHand.isBlackJack()) {
						hasBlackJack = true;
					} else if (dealerHand.isTwoAce()) {
						hasTwoAce = true;
					}

					highestPoint = dealerValue;
					winnerturn = dealerTurn;
				}

				// set dealer as the default winner if it is not burst
				else if (!dealerHand.isOnlyTwoCards() && !dealerHand.isBurst()) {
					highestPoint = dealerValue;
					// winnerTurn[i] = gameObject.getPlayersCount() + 1;
					// index++;
				}

				else {
					highestPoint = 0;
					// winnerTurn = DRAW;
				}
				
				// compare with the dealer's value (if dealer is not burst)
				for (int indexPlayerCount = 0; indexPlayerCount < gameObject.getPlayersCount(); indexPlayerCount++) {
					Hand playerHand = gameObject.getPlayers().elementAt(indexPlayerCount).getHand();
					playerValue[indexPlayerCount] = playerHand.calculateValue();

					if (playerHand.isOnlyTwoCards()) {
						if (playerHand.isBlackJack() && hasBlackJack == false) {
							// hasBlackJack = true;
							winnerTurn[indexPlayerCount] = indexPlayerCount + 1;
							// index++;
						} else if (playerHand.isTwoAce() && hasBlackJack == false) {
							winnerTurn[indexPlayerCount] = indexPlayerCount + 1;
							index++;
						} else if ((playerValue[indexPlayerCount] > highestPoint) && (hasBlackJack == false && hasTwoAce == false)) {
							highestPoint = playerValue[indexPlayerCount];
							winnerTurn[indexPlayerCount] = indexPlayerCount + 1;
							index++;
						}
						// else if (playerValue[i] == highestPoint)
						// {
						// hasMoreWinner = true;
						// winnerTurn = DRAW;
						// break;
						// }
					} else if (!playerHand.isOnlyTwoCards() && (playerHand.isOver17() && playerHand.isUnder21())) {
						if (playerHand.is21() && hasBlackJack == false && hasTwoAce == false) {
							if (playerValue[indexPlayerCount] > highestPoint) {
								winnerTurn[indexPlayerCount] = indexPlayerCount + 1;
								// index++;
							}
						} else if ((hasBlackJack == false || hasTwoAce == false)) {
							if (playerValue[indexPlayerCount] > highestPoint) {
								winnerTurn[indexPlayerCount] = indexPlayerCount + 1;
								// index++;
							}
						}

						// else if (playerValue[i] == highestPoint)
						// {
						// hasMoreWinner = true;
						// winnerTurn = DRAW;
						// break;
						// }
					}
				}

				// Determine the winner msg
				// if (winnerTurn == DRAW || winnerTurn == 0)
				// {
				// winnerMsg =
				// "\nThis gameObject is a draw. There is no winner.\n";
				// }
				// else
				// {
				// if(winnerTurn <= gameObject.getPlayersCount())
				// winnerMsg = "\nThe winner is Player " + winnerTurn + '\n';
				// else
				// winnerMsg =
				// "\n*******The dealer wins the gameObject!*******\n";
				// }

				winnerMsg += "\n";
				gameObject.append(winnerMsg);

				// determine if the gameObject still continues
				if (gameObject.getTrial() < MAXTRIALS)
					isGameEnd = false;
				else
					isGameEnd = true;

				// send the signal to the com.blackjack.client whether to
				// continue playing or not
				for (int i = 0; i < gameObject.getPlayersCount(); i++)
					toPlayer[i].writeBoolean(isGameEnd);

				// tell the com.blackjack.client who's the winner
				// for (int i = 0; i < gameObject.getPlayersCount(); i ++)
				// toPlayer[i].writeInt(winnerTurn);

				for (int j = 0; j < winnerTurn.length; j++) {
					System.out.print(winnerTurn[j]);
				}
				for (int i = 0; i < gameObject.getPlayersCount(); i++) {
					toPlayer[i].writeInt(winnerTurn[i]);

				}

				// add the winner into scoreboard
				// gameObject.setWinner(winnerTurn);
				gameObject.writeStatus("no");

			}

			// display the score board
			String scoreBoard = gameObject.getScoreBoard().getResults();
			gameObject.append("Scores for this gameObject: " + scoreBoard+ "\n");

			// send the scoreboard to the com.blackjack.client
			for (int indexOfPlayer = 0; indexOfPlayer < gameObject.getPlayersCount(); indexOfPlayer++)
				toPlayer[indexOfPlayer].writeUTF(scoreBoard);
		} catch (IOException ex) {
			gameObject.append(ex.toString());
		}
	}

}
