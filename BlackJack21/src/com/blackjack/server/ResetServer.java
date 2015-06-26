/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//Clears the previous log from the Flat File System 
*/

package com.blackjack.server;
//Resets the old data to clear the log when new session is started.


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.blackjack.serverUtilities.ServerProperties;

public class ResetServer {
		public static void main (String args[]) throws IOException
		{
			
			FileWriter fstream ;
		    
			fstream = new FileWriter(ServerProperties.getTrialFile());
			
			//Write trial data into logfile
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(0+System.getProperty("line.separator"));
			out.close();
			
			//Write turn of different players into logfile
			fstream = new FileWriter(ServerProperties.getTurnFile());
			out = new BufferedWriter(fstream);
			out.write(1+System.getProperty("line.separator"));
			out.close();
			
			// number of continuation has written in file
			fstream = new FileWriter(ServerProperties.getContinueFile());
			out = new BufferedWriter(fstream);
			out.write("no"+System.getProperty("line.separator"));
			out.close();
			
			fstream = new FileWriter(ServerProperties.getiPListFile());
			fstream.close();
		}
}
