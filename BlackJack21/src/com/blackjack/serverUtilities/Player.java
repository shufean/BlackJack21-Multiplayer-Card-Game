/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//It holds the information related to players like username,turns,cards in hand and designation of player. 
 */

package com.blackjack.serverUtilities;

import java.net.*;

import com.blackjack.serverUtilities.*;

public class Player implements PlayStatus {
	private String username;
	private int playerTurn;
	private boolean isMyTurn;
	private Socket socket;
	private Hand hand;

	// a normal player that needs a username
	public Player(String username, Socket socket) {
		this.username = username;
		this.socket = socket;
		isMyTurn = false;
		hand = new Hand();
	}

	// default player - dealer
	public Player() {
		this.playerTurn = PlayStatus.DEALER;
		isMyTurn = false;
		hand = new Hand();
		this.username = "dealer";
	}

	public int gethandsie() {
		return hand.getHandSize();
	}

	/************* MODIFIER ****************************/

	public void setPlayerTurn(int turn) {
		playerTurn = turn;
	}

	public void addCardToHand(Card aCard) {
		hand.addCard(aCard);
	}

	public void resetHand() {
		hand = new Hand();
	}

	/***************** ACCESSORS *************************/

	public Socket getSocket() {
		return socket;
	}

	public String getUsername() {
		return username;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public Hand getHand() {
		return hand;
	}

}
