/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//Properties of the server 
*/

package com.blackjack.serverUtilities;

public class ServerProperties {
	
	public final static String iPListFile = "iplist.txt";
	public final static String turnFile = "turn.txt";
	public final static String trialFile = "trial.txt";
	public final static String continueFile = "continue.txt";
	
	public final static  String serverTitle = "BlackJack Game - 21";
	public final static int serverWidth = 600;
	public final static int serverHeight = 400;
	
	
	public static String getiPListFile() {
		return iPListFile;
	}
	public static String getTurnFile() {
		return turnFile;
	}
	public static String getTrialFile() {
		return trialFile;
	}
	public static String getContinueFile() {
		return continueFile;
	}
	public static String getServerTitle() {
		return serverTitle;
	}
	public static int getServerWidth() {
		return serverWidth;
	}
	public static int getServerHeight() {
		return serverHeight;
	}


}
