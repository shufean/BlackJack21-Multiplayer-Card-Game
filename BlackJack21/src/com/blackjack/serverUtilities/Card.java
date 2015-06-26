/**
 * Submitted by Md Abu Shufean
// Platform - Java, Eclipse
// Numerical value is assigned to each suit diamonds, clubs, hearts and spades (1 to 4) 
//and for card number(1 to 13).
 */

package com.blackjack.serverUtilities;

import java.io.*;

public class Card {
	// Card suits
	public final static int DIAMONDS = 0;
	public final static int CLUBS = 1;
	public final static int HEARTS = 2;
	public final static int SPADES = 3;

	// Non-numeric cards
	public final static int ACE = 1;
	public final static int JACK = 11;
	public final static int QUEEN = 12;
	public final static int KING = 13;

	private int suit;
	private int value;

	// A card will be made of a Suit and its Value
	public Card(int suit, int value) {
		this.suit = suit;
		this.value = value;
	}

	// Print out information of the card
	public String toString() {
		String string = "";
		if (suit == DIAMONDS)
			string += "DIAMONDS ";
		else if (suit == CLUBS)
			string += "CLUBS ";
		else if (suit == HEARTS)
			string += "HEARTS ";
		else if (suit == SPADES)
			string += "SPADES ";
		else
			string += "";

		if (value == ACE)
			string += "ACE";
		else if (value == JACK)
			string += "JACK";
		else if (value == QUEEN)
			string += "QUEEN";
		else if (value == KING)
			string += "KING";
		else
			string += value;

		return string;
	}

	// get the value of the card
	public int getValue() {
		if (value == JACK || value == QUEEN || value == KING) {
			return 10;
		}

		else
			return value;
	}

	public int getCardValue() {
		return value;
	}

	// get the suit of the card
	public int getSuit() {
		return suit;
	}

}
