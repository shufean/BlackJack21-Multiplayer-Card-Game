// Submitted to Prof. L. Osborne
// Platform - Java, Eclipse
//It initializes the deck by adding in all 
//the cards and, defines all the cards possible in playing 
//cards with aggregation of card class i.e. BlackJackCard

package serverActivities;

import java.util.*;

public class BlackJackDeck 
{
	private final static int NUMBEROFCARDS = 52;
	
	private Vector <BlackJackCard> deck;
	private int cardsLeft = NUMBEROFCARDS - 1;
	
	public BlackJackDeck()
	{
		//initialise the deck by adding in all the cards
		deck = new Vector<BlackJackCard>();
		
		for (int i=BlackJackCard.DIAMONDS; i<=BlackJackCard.SPADES; i++)
		{
			for (int j=BlackJackCard.ACE; j<=BlackJackCard.KING; j++)
			{
				deck.add(new BlackJackCard(i,j));
			}
		}
	}
	
	//shuffle the card where necessary
	public void shuffle()
	{
		Collections.shuffle(deck);
	}
	
	public BlackJackCard topCard()
	{
		return deck.elementAt(cardsLeft);
	}
	
	//deal the top card, and remove from the collection
	public BlackJackCard dealTopCard()
	{
		BlackJackCard card = deck.elementAt(cardsLeft);
		deck.remove(cardsLeft--);		
		return card;
	}
	
	//print the deck
	public void printDeck()
	{
		Iterator<BlackJackCard> ite = deck.iterator();
		while (ite.hasNext())
		{
			System.out.println(ite.next().toString());
		}
	}
	
	public int getCardsLeft()
	{
		return cardsLeft;
	}
	
}
