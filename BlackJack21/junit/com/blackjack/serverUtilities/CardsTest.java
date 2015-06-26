package com.blackjack.serverUtilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackjack.serverUtilities.Card;

public class CardsTest{

	@BeforeClass
	public static void testClassSequence(){
		System.out.println("Check Class");
	}
	
	@Before
	public void testSequence(){
		System.out.println("Sequence Validation");
	}
	
	@Test
	public void testHitACard() {
		Card cardObj = new Card(1, 2);
		assertEquals("CLUBS 2", cardObj.toString());
		
	}
	
	@Test
	public void testHitACardTwo() {
		Card cardObj = new Card(2, 2);
		assertEquals("HEARTS 2", cardObj.toString());
		
	}

	
}
