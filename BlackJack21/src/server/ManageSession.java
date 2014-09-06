//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//This class is responsible for handling the game sessions between client and server.
//It has socket connection and maintains communication between the players via socket. 



package server;
//This class is to handle the game sessions between client and server

import java.io.*;
import java.net.*;
import java.util.*;

import serverActivities.*;

/**
 * 
 * 
 * This class is to handle the game sessions between client and server
 */

class ManageSession extends Thread implements BlackJackStatus
{
	private Socket playerSocket[]  = null;
	private DataInputStream fromPlayer[]  = null;
	private DataOutputStream toPlayer[]  = null;
	private BlackJackServer game = null;
	private Vector <BlackJackPlayer> blackJackPlayers;
	private boolean isGameEnd = false;
	private int whoseTurn = 0;
	private String winnerMsg = "";
	private Vector<String> clients ;
	private Vector<Card> cards ;
	public ManageSession(BlackJackServer game) throws IOException
	{
		//initialise all the variables
		blackJackPlayers = game.getWaitingPlayers();
		playerSocket = new Socket [MAXPLAYERS];
		fromPlayer = new DataInputStream[MAXPLAYERS];
		toPlayer = new DataOutputStream [MAXPLAYERS];
		this.game = game;
		
		//Add the playing from waiting list to player list
		for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
		{
			if(game.addPlayer(blackJackPlayers.elementAt(i)))
			{
				game.addPlayersCount();
				game.minusWaitingPlayersCount();
			}
		}
		clients = game.read_clientList();
		//Show the total players joined for the game.
		game.append("Total players joined: " + game.getPlayersCount() + "\n\n");
		
		
		//TODO add the sorting and the condition 
		for (int i=0 ; i <= MAXPLAYERS-1 ; i++)
		{
			System.out.println(blackJackPlayers.get(i).getSocket().getInetAddress().toString()+" "+blackJackPlayers.get(i).getUsername());
			System.out.println(clients.get(i));
		}
		
		
		
		
		
		
		
		//The dealer is also one of the player. Add the dealer into the player list.
		game.addPlayer(game.getDealer());
		
		if (game.readstatus())
		{
		BlackJackPlayer temp;
		///// sort the clients according to ip list
		
		for (int i=0 ; i < clients.size() ; i++)
			for (int j=i ; j < game.getPlayersCount() ; j++)
			{
				if (clients.get(i).equals(game.getPlayers().elementAt(j).getUsername()))
				{
					temp = game.getPlayers().elementAt(i);
					game.getPlayers().set(i, game.getPlayers().elementAt(j)); 
					game.getPlayers().set(j, temp); 
				}
				
			}
		}
		
		blackJackPlayers = game.getPlayers();//reusing var players
		System.out.println(blackJackPlayers.get(0).getUsername());
		System.out.println(clients.toString());
		cards = new Vector<Card>();
		//Remove the players from waiting list after added.
		game.removeWaitingPlayers();//to change for connection
	}
	
	//start the thread here
	public void writehand(int index) throws IOException
	{
		
		game.write_clienthand(blackJackPlayers.get(index).getUsername(), blackJackPlayers.get(index).getHand());
		
	}
	public void run()
	{
		//create the scoreboard
		game.initScoreBoard(game.getPlayersCount());
		
		try
		{
			//initilise all the sockets and data I/O streams
			for (int i=0; i<game.getPlayersCount(); i++)
			{
				playerSocket[i] = blackJackPlayers.elementAt(i).getSocket();
				fromPlayer[i] = new DataInputStream(playerSocket[i].getInputStream());
				toPlayer[i] = new DataOutputStream(playerSocket[i].getOutputStream());
			}
			
			//write to players and notify their turn
			for (int i=0; i<game.getPlayersCount(); i++)
			{
				blackJackPlayers.elementAt(i).setPlayerTurn(i+1);
				int turn = blackJackPlayers.elementAt(i).getPlayerTurn();
				toPlayer[i].writeUTF("Game started, your turn is: " + turn);
				toPlayer[i].writeInt(turn);
			}
			
			
			for (int trial=game.read_trial(); trial< MAXTRIALS; trial++)
			{
				//TODO
				game.writeTrial(trial);
				//get current trial number				
				//start a new game
				if(trial==0)
					game.newGame();				
				else if (trial!=0)			
					game.restartGame();
					
				//distribute to each player 2 cards (dealer the last)
				if(!game.readstatus())
				{
				for (int j=0; j<2; j++)
				{
					for (int i=0; i<game.getPlayersCount() +1 /*including the dealer */; i++)
					{
						game.deal();
						game.nextPlayer();
					}
				}
	////////////////
				for (int i=0; i<game.getPlayersCount()+1; i++)
				{
					this.writehand(i);
				}
				////////////////////////
				}
				else
				{
					Card acard;
					for (int i=0 ; i<game.getPlayersCount()+1;i++)
					{
						cards = game.read_clienthand(blackJackPlayers.get(i).getUsername());
						for (int j =0 ; j <cards.size();j++)
						{
							acard = cards.get(j);
							game.getPlayers().elementAt(i).getHand().addCard(acard);
						}
						
					}
				
				}
				//Tell the players the two card they are given 
				//This approach is implemented for GUI based
				for (int i=0; i<game.getPlayersCount(); i++)
				{
					
							
					System.out.println(game.getPlayers().elementAt(i).getHand().gethandsize())	;
					toPlayer[i].writeInt(game.getPlayers().elementAt(i).getHand().gethandsize());
							
							
					
				}
				
				for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
				{
					for (int j=0; j<game.getPlayers().elementAt(i).getHand().gethandsize(); j++)
					{
						if(i<MAXPLAYERS)
						{
							//write the suit then value
							int suit = game.getPlayers().elementAt(i).getHand().getCard(j).getSuit();
							int value = game.getPlayers().elementAt(i).getHand().getCard(j).getCardValue();
							
							toPlayer[i].writeInt(suit);
							toPlayer[i].writeInt(value);
						}
					}
				}
				
				game.writestatus("yes");
				game.append("\n------------------------------Trial = " + game.getTrial() + "--------------------------------\n");	
				
				//List down the cards that each players has in server side
				for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS + 1; i++)
				{
					Hand hand = game.getPlayers().elementAt(i).getHand();
					if(i<MAXPLAYERS)
						game.append("Player " + (i+1) + " has " + hand.getCardsOnHand() + "\n");
					else
						game.append("Dealer has "+ hand.getCardsOnHand() + "\n");
				}
				
				//Get whose turn for this game
				whoseTurn = game.getWhoseTurn();
				
				for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
				{
					//tell the players trial number
					toPlayer[i].writeUTF("\nTrial = " + game.getTrial());
				}
				
				//Tell all the players in the player list about their turns
				for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
				{
					//tell the players trial number
					
					toPlayer[i].writeInt(game.getWhoseTurn());
				}
				
				
				
				while(whoseTurn < DEALER && whoseTurn < blackJackPlayers.size())
				{
					//game.append("----------WhoseTurn2:----------" + game.getWhoseTurn() +"\n");
					
					game.append("\nNow is Player " + game.getWhoseTurn() + " turn.\n Waiting player to perform action.\n");
										
					//Notify player to start
					//toPlayer[game.getWhoseTurn()-1].writeInt(game.getWhoseTurn());
					for (int i=0; i<blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
					{
						//tell the players trial number
						//toPlayer[i].writeUTF("\nTrial = " + game.getTrial());
						
						//send an integer to client indicating whose turn now
						toPlayer[i].writeInt(game.getWhoseTurn());
						
						
						if ((i+1) == game.getWhoseTurn())
						{
							toPlayer[i].writeUTF("Your turn now.\nYou may type 1 to HIT, or 2 to STAND.\n");
							
						}
						else
							toPlayer[i].writeUTF("Now is Player " + game.getWhoseTurn() + " turn.\nWaiting player to perform action...\n");
						
						
					}
						
					
					//while the player is still elligible to continue hitting, hit one card to player
					boolean continueHit = true;
					String actionString = "", cards = "";
					
					while (continueHit)
					{
						int action = fromPlayer[game.getWhoseTurn() - 1].readInt();
						
						if (action == HIT)
						{
							if(game.getPlayers().elementAt(game.getWhoseTurn() - 1).getHand().getCardsTotal() < 5)
							{
								actionString ="HIT";
								//hit card if client select hit
								game.hit();
								this.writehand(game.getWhoseTurn() -1);
								
								//calculate the total of the card and send to the client side
								continueHit = true;
							}
							else
								action = STAND;
						}
						if (action == STAND)
						{
							actionString = "STAND";
							continueHit = false;
						}
						else if(action == HIT)
							actionString ="HIT";
						else
						{
							actionString = "INVALID";
							continueHit = true;
							
						}
						
						//Tell the player its action
						toPlayer[game.getWhoseTurn() -1].writeUTF(actionString);
												
						//tell the client the cards on hand that he has
						//write the suit then value
						if (action == HIT)
						{
							int suit = game.getPlayers().elementAt(game.getWhoseTurn() -1).getHand().getLastCard().getSuit();
							int value = game.getPlayers().elementAt(game.getWhoseTurn() -1).getHand().getLastCard().getCardValue();
							toPlayer[game.getWhoseTurn() -1].writeInt(suit);
							toPlayer[game.getWhoseTurn() -1].writeInt(value);
						}
						
						
						//tell all the player that current player can / cannot continue hitting
						for(int i = 0; i < blackJackPlayers.size() && i < BlackJackStatus.MAXPLAYERS; i++)
							toPlayer[i].writeBoolean(continueHit);
						
						String msg = "Player action: " + actionString + "\n";
						game.append(msg);
						game.append("Player " + game.getWhoseTurn() + " has cards: "  + game.getPlayers().elementAt(game.getWhoseTurn() - 1).getHand().getCardsOnHand() + "\n");
						game.append("Value on Player " + game.getWhoseTurn() + " hand: " + game.getPlayers().elementAt(whoseTurn -1).getHand().calculateValue() + '\n');
						
						if(!continueHit && action == STAND)
						{
							game.stand();
							whoseTurn = game.getWhoseTurn();
						}
					}
				}
				
				//now dealer's turn to hit/stand
				while (game.getDealer().getHand().isUnder17())
					game.hit();
				
				//show the cards in dealer's hand
				game.append("Dealer has cards: " + game.getPlayers().elementAt(whoseTurn -1).getHand().getCardsOnHand() + '\n');
				game.append("Value on dealer's hand: " + game.getPlayers().elementAt(whoseTurn -1).getHand().calculateValue() + '\n');
					// Todo
				//send to client
				for (int i = 0 ; i < game.getPlayersCount();i++)
				{
					
						toPlayer[i].writeInt(game.getPlayers().elementAt(whoseTurn -1).getHand().gethandsize());
				}
				for(int i = 0 ; i < game.getPlayersCount();i++)
				{
					for (int j=0; j<game.getPlayers().elementAt(whoseTurn -1).getHand().gethandsize(); j++)
					{
						
							//write the suit then value
							int suit = game.getPlayers().elementAt(whoseTurn -1).getHand().getCard(j).getSuit();
							int value = game.getPlayers().elementAt(whoseTurn -1).getHand().getCard(j).getCardValue();
							
							toPlayer[i].writeInt(suit);
							toPlayer[i].writeInt(value);
					}
				}
				//
				//display the results
				int playerValue [] = new int [game.getPlayersCount()];
				int highestPoint ;
				int winnerturn;
				int[] winnerTurn= new int [MAXPLAYERS];	
				int index =0;
				boolean hasMoreWinner = false, hasTwoAce = false, hasBlackJack = false; /*counter keep track of two or more winner*/
					
				//count the dealer's cards first
				Hand dealerHand = game.getDealer().getHand();
				int dealerValue = dealerHand.calculateValue(), dealerTurn = game.getPlayersCount() +1 ;
				
				//if the dealer has only two cards and is blackjack / two aces
				if(dealerHand.isOnlyTwoCards())
				{
					if (dealerHand.isBlackJack())
					{
						hasBlackJack = true;
					}
					else if (dealerHand.isTwoAce())
					{
						hasTwoAce = true;
					}
					
					highestPoint = dealerValue;
					winnerturn = dealerTurn;
				}
				
				//set dealer as the default winner if it is not burst
				else if (!dealerHand.isOnlyTwoCards() && !dealerHand.isBurst())
				{
					highestPoint = dealerValue;
					//winnerTurn[i] = game.getPlayersCount() + 1;
					//index++;
				}
				
			  else
			  {
					highestPoint = 0;
				//	winnerTurn = DRAW;
			  }
				//compare with the dealer's value (if dealer is not burst)
				for (int i = 0; i < game.getPlayersCount(); i ++)
				{
					Hand playerHand = game.getPlayers().elementAt(i).getHand();
					playerValue[i] = playerHand.calculateValue();
					
					if (playerHand.isOnlyTwoCards())
					{
						if (playerHand.isBlackJack() && hasBlackJack == false)
						{
							//hasBlackJack = true;
							winnerTurn[i] = i+1;
							//index++;
						}
						else if (playerHand.isTwoAce() &&  hasBlackJack == false )
						{
							winnerTurn[i] = i+1;
							index++;
						}
						else if ((playerValue[i] > highestPoint) &&( hasBlackJack == false && hasTwoAce == false))
						{
						highestPoint = playerValue[i];
						winnerTurn[i] = i+1;
						index++;
					}
					//	else if (playerValue[i] == highestPoint)
					//	{
						//	hasMoreWinner = true;
						//	winnerTurn = DRAW;
							//break;
					//	}
					}
					else if (!playerHand.isOnlyTwoCards() && (playerHand.isOver17() && playerHand.isUnder21()))
					{
						if(playerHand.is21() && hasBlackJack == false && hasTwoAce == false )
						{
							if (playerValue[i] > highestPoint)
							{
								winnerTurn[i] = i+1;
							//	index++;
							}
						}
						else if((hasBlackJack == false || hasTwoAce == false))
						{
							if (playerValue[i] > highestPoint)
							{
								winnerTurn[i] = i+1;
							//	index++;
							}
						}
						
					//	else if (playerValue[i] == highestPoint)
					//	{
						//	hasMoreWinner = true;
						//	winnerTurn = DRAW;
							//break;
					//	}
					}
				}
				
				//Determine the winner msg
				//if (winnerTurn == DRAW || winnerTurn == 0)
				//{	
				//	winnerMsg = "\nThis game is a draw. There is no winner.\n";
				//}
				//else
				//{
					//if(winnerTurn <= game.getPlayersCount())
						//winnerMsg = "\nThe winner is Player " + winnerTurn + '\n';
					//else
						//winnerMsg = "\n*******The dealer wins the game!*******\n";
				//}
				
				winnerMsg += "\n";
				game.append(winnerMsg);
				
				//determine if the game still continues
				if(game.getTrial() < MAXTRIALS)
					isGameEnd = false;
				else
					isGameEnd = true;
				
				//send the signal to the client whether to continue playing or not
				for (int i = 0; i < game.getPlayersCount(); i ++)
					toPlayer[i].writeBoolean(isGameEnd);			
				
				//tell the client who's the winner
				//for (int i = 0; i < game.getPlayersCount(); i ++)
				//	toPlayer[i].writeInt(winnerTurn);

				for (int j =0 ; j < winnerTurn.length ; j++)
				{
				System.out.print(winnerTurn[j]);
				}
				for (int i = 0 ; i < game.getPlayersCount();i++)
				{
						toPlayer[i].writeInt(winnerTurn[i]);
	
				}
				
				//add the winner into scoreboard
				//game.setWinner(winnerTurn);
				game.writestatus("no");
				
			}
			
			//display the score board
			String scoreBoard = game.getScoreBoard().getResults();
			game.append("Scores for this game: " + scoreBoard + "\n" );
			
			//send the scoreboard to the client
			for (int i = 0; i < game.getPlayersCount(); i ++)
				toPlayer[i].writeUTF(scoreBoard);	
		}
		catch(IOException ex)
		{
			game.append(ex.toString());
		}
	}
	
	
}
