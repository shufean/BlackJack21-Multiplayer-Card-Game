/*
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//It holds the information related to Identification of Player and his/her total points.
*/

package com.blackjack.serverUtilities;

public class Score {
	private int player; // special identification of player
	private int points; // count how many trials the player has won

	public Score(int player) {
		this.player = player;
		this.points = 0;
	}

	public void addPoint() {
		points++;
	}

	public int getPlayer() {
		return player;
	}

	public int getPoints() {
		return points;
	}

}
