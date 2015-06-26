package com.blackjack.clientUtilities;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;
import com.blackjack.clientUtilities.ClientHand;
import com.blackjack.serverUtilities.Card;

public class ClientHandTest{

	@Test
	public void testGetCardsTotal() {
		ClientHand clientCardObj = new ClientHand();
		ClientHand clientCardObjTwo = new ClientHand();
		assertTrue(clientCardObj.getCardsTotal()==clientCardObjTwo.getCardsTotal());
		
	}
}
