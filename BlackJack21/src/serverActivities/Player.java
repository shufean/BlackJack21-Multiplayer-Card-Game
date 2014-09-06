//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//It holds the information related to players like username,turns,cards in hand and designation of player. 


package serverActivities;

import serverActivities.*;

import java.net.*;

public class Player implements GameStatus
{
	private String username;
	private int playerTurn;
	private boolean isMyTurn;
	private Socket socket;
	private BlackJackHand hand;
	
	public Player(String username, Socket socket) //a normal player that needs a username
	{
		this.username = username;
		this.socket = socket;
		isMyTurn = false;
		hand = new BlackJackHand();
	}
	
	public Player() //a default player - dealer
	{
		this.playerTurn = GameStatus.DEALER;
		isMyTurn = false;
		hand = new BlackJackHand();
		this.username = "dealer";
	}
	public int gethandsie()
	{
		return hand.gethandsize();
	}
	
	/***************************************************
	 * MODIFIER
	****************************************************/
	public void setPlayerTurn(int turn)
	{
		playerTurn = turn;
	}
	
	public void addCardToHand(BlackJackCard aCard)
	{
		hand.addCard(aCard);
	}
	
	public void resetHand()
	{
		hand = new BlackJackHand();
	}
	
	/***************************************************
	 * ACCESSORS
	****************************************************/
	public Socket getSocket()
	{
		return socket;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public int getPlayerTurn()
	{
		return playerTurn;
	}
	
	public boolean isMyTurn()
	{
		return isMyTurn;
	}
	
	public BlackJackHand getHand()
	{
		return hand;
	}
	
	

}
