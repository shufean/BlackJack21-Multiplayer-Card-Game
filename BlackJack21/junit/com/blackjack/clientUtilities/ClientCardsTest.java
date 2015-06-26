package com.blackjack.clientUtilities;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;

import com.blackjack.clientUtilities.ClientCard;
import com.blackjack.serverUtilities.Card;

public class ClientCardsTest{

	@AfterClass
	public static void testClassSequence(){
		System.out.println("Check Class");
	}
	
	
	@Test
	public void testHitACard() {
		ClientCard clientCardObj = new ClientCard(1, 2);
		assertEquals("CLUBS 2", clientCardObj.toString());
		
	}
	
	@Test
	public void testHitACardTwo() {
		ClientCard clientCardObj = new ClientCard(2, 2);
		assertEquals("HEARTS 2", clientCardObj.toString());
		
	}

	
}
