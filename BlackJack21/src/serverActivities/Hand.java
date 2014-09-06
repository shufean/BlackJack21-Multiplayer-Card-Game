//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//Initializes BlackJackHand with no card and an empty "hand" vector. 
//Calculates values of the BlackJack with various values for ACE according to condition


package serverActivities;

import java.util.*;
import java.io.*;

public class Hand
{
	/**
	 * 
	 */
	private Vector<Card> hand;
	private int cardsTotal;
	
	//initialise BlackJackHand with no cards and an empty "hand" vector
	public Hand()
	{
		hand = new Vector<Card>();
		cardsTotal = 0;
	}
	
	
	/***************************************************
	 * ACCESSORS
	****************************************************/
	//return all the cards on hand
	public Vector <Card>getCardsOnHand()
	{
		return hand;
	}
	public int gethandsize ()
	{
		return hand.size();
	}
	//return number of cards
	public int getCardsTotal()
	{
		return cardsTotal;
	}
	
	//return last card added
	public Card getLastCard()
	{
		return hand.elementAt(cardsTotal-1);
	}
	
	//return one particular card
	public Card getCard(int atIndex)
	{
		return hand.elementAt(atIndex);
	}
	
	/***************************************************
	 * MODIFIER
	****************************************************/
	//add a card into hand
	public void addCard(Card aCard)
	{
		hand.add(aCard);
		cardsTotal++;
	}
	
	/***************************************************
	 * CALCULATION
	****************************************************/
	//calculate the values of blackjack
	public int calculateValue()
	{
		int total = 0;
		
		Iterator <Card> ite = hand.iterator() ;
		while (ite.hasNext())
		{
			Card current = ite.next();
			int value;
			
			if (current.getValue() == Card.ACE)
			{
				//If total number of cards is 2, then Ace = 11
				if (isOnlyTwoCards())
					value = 11;
				
				//If total number of cards is 3, then Ace is 11 or 1
				else if (cardsTotal == 3)
				{
					if ((total+11)> 21 ) 
					{
						value = 1;
					}
					else
						value = 11;
				}
				
				//If total number of cards is 4 and above, Ace is 1
				else 
					value = 1;
			}
			
			else
				value = current.getValue();
			
			total += value;
		}
		
		return total;
	}
	
	//check if the total cards in hand is has only a number of TWO
	public boolean isOnlyTwoCards()
	{
		if (cardsTotal == 2)
		{
			return true;
		}
		else
			return false;
	}
	
	//check if the cards on hand is blackjack
	//blackjack is an ACE with any face card or 10
	public boolean isBlackJack()
	{
		if (isOnlyTwoCards())
		{
			Card firstCard, secondCard;
			firstCard = hand.elementAt(0);
			secondCard = hand.elementAt(1);
			
			
			if((firstCard.getValue() == Card.ACE && secondCard.getValue() == 10)||(firstCard.getValue() == 10 && secondCard.getValue() == Card.ACE) )
				return true;
			else 
				return false;
		}
		else
			return false;
	}
	
	//check if the cards on hand are two Ace
	public boolean isTwoAce()
	{
		if (isOnlyTwoCards())
		{
			Card firstCard, secondCard;
			firstCard = hand.elementAt(0);
			secondCard = hand.elementAt(1);
			
			if(firstCard.getValue() == Card.ACE  && secondCard.getValue() == Card.ACE )
				return true;
			else 
				return false;
		}
		else
			return false;
	}
	
	//return true if the cards in hand is equal to 21
	public boolean is21()
	{
		if(!isOnlyTwoCards())
		{
			if(calculateValue() == 21)
				return true;
			else
				return false;
		}
		else
			return false;
	
	}
	
	//check if the cards in hand is over 17
	public boolean isUnder17()
	{
		if (calculateValue() < 17 )
			return true;
		else
			return false;
	}
	
	//check if the cards in hand is over 17
	public boolean isOver17()
	{
		if (calculateValue() >= 17 )
			return true;
		else
			return false;
	}
	
	//check if the value in hand is less than 21
	public boolean isUnder21()
	{
		if (calculateValue() <= 21 )
			return true;
		else
			return false;
	}
	
	//check if the value in hand is already burst (more than 21)
	public boolean isBurst()
	{
		if (calculateValue() > 21)
			return true;
		else
			return false;
	}
	
	//check if the value in hand is a pair
	public boolean isPair()
	{
		if (isOnlyTwoCards())
		{
			Card firstCard, secondCard;
			firstCard = hand.elementAt(0);
			secondCard = hand.elementAt(1);
			
			if (firstCard.getValue() == secondCard.getValue())
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
}
