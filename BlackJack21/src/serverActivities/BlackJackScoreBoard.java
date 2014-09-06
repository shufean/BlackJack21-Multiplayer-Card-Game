//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//It is responsible to maintain aggregation of scores.
//Score holds the information regarding player and respective score. 


package serverActivities;

import java.util.*;

import server.*;
import client.*;
/*
 * BlackJackGame will assign each player with a special ID
 * from GameStatus (0-6)
 * by passing in the number of players, we can know how many
 * players to be in scoreboard
 */
public class BlackJackScoreBoard 
{
	private Vector <BlackJackScore> blackJackScores;
	private int numberOfPlayers; 

	public BlackJackScoreBoard(int numberOfPlayers)
	{
		blackJackScores = new Vector <BlackJackScore>();
		this.numberOfPlayers = numberOfPlayers;
		
		//create scores for each player
		for (int i=1; i<=numberOfPlayers; i++)
		{
			blackJackScores.add(new BlackJackScore(i));
		}
		blackJackScores.add(new BlackJackScore(numberOfPlayers +1 )); /* This is for the dealer's score*/ 
	}
	
		
	//Set the winner of the game for that trial
	public void setWinner(int player)
	{
		this.blackJackScores.elementAt(player-1).addPoint();
	}
	
	//get number of players in the scoreboard
	public int getNumberOfPlayers()
	{
		return numberOfPlayers;
	}
	
	//get the string to display out the scores
	public String getResults()
	{
		String results = "\nPlayer   | Points\n------------------\n";
		for (int i=0; i<numberOfPlayers; i++)
		{
			results += "Player " + (i+1) + " | " + blackJackScores.elementAt(i).getPoints() + "\n";
		}
		results += "Dealer  " + " | " + blackJackScores.elementAt(numberOfPlayers).getPoints() + "\n";
		return results;
	}

}
