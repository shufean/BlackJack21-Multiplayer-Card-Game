//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//It holds the information related to Identification of Player and his/her total points.


package serverActivities;

public class BlackJackScore 
{
	private int player; //special identification of player
	private int points; //count how many trials the player has won
	
	public BlackJackScore(int player)
	{
		this.player = player;
		this.points = 0;
	}
	
	public void addPoint()
	{
		points++;
	}
	
	public int getPlayer()
	{
		return player;
	}
	
	public int getPoints()
	{
		return points;
	}

}
