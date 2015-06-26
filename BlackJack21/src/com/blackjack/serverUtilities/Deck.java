/**
 * Submitted by Md Abu Shufean
 *Platform - Java, Eclipse
It initializes the deck by adding in all 
the cards and, defines all the cards possible in playing 
cards with aggregation of card class i.e. BlackJackCard
 */

package com.blackjack.serverUtilities;

import java.util.*;

public class Deck {
	private final static int NUMBEROFCARDS = 52;

	private Vector<Card> cardDeck;
	private int cardsLeft = NUMBEROFCARDS - 1;

	public Deck() {
		// prepare deck by adding all cards
		cardDeck = new Vector<Card>();
		
		// iterate through diamond to spades
		for (int i = Card.DIAMONDS; i <= Card.SPADES; i++) {
			
			// iterate through ace to king
			for (int j = Card.ACE; j <= Card.KING; j++) {
				cardDeck.add(new Card(i, j));
			}
		}
	}

	// card shuffle using shuffle method
	public void shuffleCards() {
		// shuffle the deck
		Collections.shuffle(cardDeck);
	}

	// get top card from the cards left in deck
	public Card getTopCard() {
		return cardDeck.elementAt(cardsLeft);
	}

	// deal and remove the card from the collection
	public Card dealTopCard() {
		Card currentCard = cardDeck.elementAt(cardsLeft);
		cardDeck.remove(cardsLeft--);
		return currentCard;
	}

	// print the deck
	public void printDeck() {
		Iterator<Card> ite = cardDeck.iterator();
		while (ite.hasNext()) {
			System.out.println(ite.next().toString());
		}
	}

	public int getCardsLeft() {
		return cardsLeft;
	}

}
