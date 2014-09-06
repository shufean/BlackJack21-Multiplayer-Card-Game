//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//It holds the information related to players like username,turns,cards in hand and designation of player. 


package serverActivities;

import serverActivities.*;

import java.net.*;

public class BlackJackPlayer implements BlackJackStatus
{
	private String username;
	private int playerTurn;
	private boolean isMyTurn;
	private Socket socket;
	private Hand hand;
	
	public BlackJackPlayer(String username, Socket socket) //a normal player that needs a username
	{
		this.username = username;
		this.socket = socket;
		isMyTurn = false;
		hand = new Hand();
	}
	
	public BlackJackPlayer() //a default player - dealer
	{
		this.playerTurn = BlackJackStatus.DEALER;
		isMyTurn = false;
		hand = new Hand();
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
	
	public void addCardToHand(Card aCard)
	{
		hand.addCard(aCard);
	}
	
	public void resetHand()
	{
		hand = new Hand();
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
	
	public Hand getHand()
	{
		return hand;
	}
	
	

}
