/*
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//It is responsible to maintain aggregation of scores.
//Score holds the information regarding player and respective score. 
 */

package com.blackjack.serverUtilities;

import java.util.*;

import com.blackjack.client.*;
import com.blackjack.server.*;

/*
 * BlackJackGame will assign each player with a special ID from GameStatus (0-6)
 * by passing in the number of players, we can know how many players to be in score board
 */
public class ScoreBoard {
	private Vector<Score> scores;
	private int numberOfPlayers;

	public ScoreBoard(int numberOfPlayers) {
		scores = new Vector<Score>();
		this.numberOfPlayers = numberOfPlayers;

		// create scores for each player
		for (int i = 1; i <= numberOfPlayers; i++) {
			scores.add(new Score(i));
		}
		scores.add(new Score(numberOfPlayers + 1)); /*This is for the dealer's score */
	}

	// Set the winner of the gameObject for that trial
	public void setWinner(int player) {
		this.scores.elementAt(player - 1).addPoint();
	}

	// get number of players in the score board
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	// get the string to display in score board
	public String getResults() {
		String results = "Player | Points";
		for (int i = 0; i < numberOfPlayers; i++) {
			results += "Player " + (i + 1) + " | "+ scores.elementAt(i).getPoints() + "\n";
		}
		results += "Dealer" + " | "+ scores.elementAt(numberOfPlayers).getPoints() + "\n";
		return results;
	}

}
