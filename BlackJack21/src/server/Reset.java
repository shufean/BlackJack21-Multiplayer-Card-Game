//Submitted to Prof. L. Osborne
//Platform - Java, Eclipse
//Clears the previous log from the Flat File System 


package server;
//Resets the old datas to clear the log when new session is started.


import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Reset {
		public static void main (String args[]) throws IOException
		{
			
			FileWriter fstream ;
		    
			fstream = new FileWriter("trial.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(0+System.getProperty("line.separator"));
			out.close();
			fstream = new FileWriter("turn.txt");
			out = new BufferedWriter(fstream);
			out.write(1+System.getProperty("line.separator"));
			out.close();
			fstream = new FileWriter("continue.txt");
			out = new BufferedWriter(fstream);
			out.write("no"+System.getProperty("line.separator"));
			out.close();
			fstream = new FileWriter("iplist.txt");
			fstream.close();
		}
}
