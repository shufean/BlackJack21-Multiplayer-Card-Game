//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//Initializes BlackJackHand with no card and an empty "hand" vector. 
//Calculates values of the BlackJack with various values for ACE according to condition



package clientActivities;

public interface Status 
{

	//game status
	public static final int DRAW       = -1; // Indicate a draw
	
	//constants
	public static final int MAXPLAYERS = 2;//This is to set how many players are allowed in one game
	public static final int MAXTRIALS  = 5;
	
	//connect to server constants
	public static final int SERVERSOCKET = 8000;

	//action of client
	public static final int HIT   = 1;
	public static final int STAND = 2;
	
	//players' constants
	public static final int DEALER      = 7; // Indicate dealer's turn
	public static final int PLAYER1     = 1; // Indicate player 1 turn
	public static final int PLAYER2     = 2; // Indicate player 2 turn
	public static final int PLAYER3     = 3; // Indicate player 3 turn
	public static final int PLAYER4     = 4; // Indicate player 4 turn
	public static final int PLAYER5     = 5; // Indicate player 5 turn
	public static final int PLAYER6     = 6; // Indicate player 6 turn

}
