package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import clientActivities.ClientHand;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
public class BlackjackClient extends JPanel {
	//Labels,Textbox,buttons for GUI.
	  JPanel topPanel = new JPanel();
	  JPanel dcardPanel = new JPanel();
	  JPanel pcardPanel = new JPanel();
	  JTextPane winlosebox = new JTextPane();
	  JTextPane username = new JTextPane();
	  JTextPane host = new JTextPane();
	  JButton hitbutton = new JButton();
	  JButton startbutton = new JButton();
	  JButton staybutton = new JButton();
	  ClientBasic client ;
	  //JButton playagainbutton = new JButton();
	  JLabel []dealerlabel = new JLabel[4];
	  JLabel playerlabel = new JLabel();
	  JLabel [] playercard = new JLabel[4] ;
	  String hostAddress, playerName;
	
	  JLabel playercardhit;
	  String action;
	  public BlackjackClient () 
	  {
		  System.out.println(this.getClass().getResource(""));
		  action = null; 
		  //settting colour,dimensions of screen 
		  // topPanel.setBackground(new Color(0, 122, 0));
		  JLabel pic = new JLabel(new ImageIcon("cards/background1.jpg"));
			//midBoard.setBackground(java.awt.Color.RED);
		  
		  
		    topPanel.setBackground(new Color(6, 40, 12));
		    
		    dcardPanel.setBackground(new Color(6, 40, 12));
		    
		    pcardPanel.setBackground(new Color(6, 40, 12));
		    topPanel.setLayout(new GridLayout(1,6));
		    Dimension d = new Dimension(10, 15);
		    winlosebox.setSize(d);
		   
		    winlosebox.setText(" ");
		    winlosebox.setFont(new java.awt.Font("Helvetica Bold", 1, 10));
		    username.setSize(d);
		    //Text box to accept ip address and username
		    username.setText("Username ");
		    host.setSize(d);
		    host.setText("IP");
		    hitbutton.setText("  Hit");
		    hitbutton.addActionListener(new hitbutton(this));
		    startbutton.addActionListener(new start(this));
		    //According to status of game button click event is set.
		    hitbutton.setEnabled(false);
		    staybutton.setText("  Stay");
		    staybutton.addActionListener(new staybutton(this));   
		    staybutton.setEnabled(false);
		    startbutton.setText("Start");
		    
		    hostAddress = JOptionPane.showInputDialog("Server Address ?", null);
		    playerName = JOptionPane.showInputDialog("Player Name ?", null);
		    
		   // 101 topPanel.add(host);
		   // 101 topPanel.add(username);
		    topPanel.add(startbutton);
		   
		   // topPanel.add(dealbutton);
		    topPanel.add(hitbutton);
		    topPanel.add(staybutton);
		   // 101 topPanel.add(winlosebox); 

		    pcardPanel.setLayout(new FlowLayout());
		    for (int i=0 ; i <4 ; i++)
		    {
		    	dealerlabel[i] = new JLabel();
		    	dcardPanel.add(dealerlabel[i]);
		    }
		    pcardPanel.setLayout(new FlowLayout());
		    for (int i=0 ; i <4 ; i++)
		    {
		    	playercard[i] = new JLabel();
		    	pcardPanel.add(playercard[i]);
		    }
		    pcardPanel.add(playerlabel);
		   // dcardPanel.add(dealerlabel);
		    dcardPanel.add(pic);
		    setLayout(new BorderLayout());
		    add(topPanel,BorderLayout.NORTH);
		    add(dcardPanel,BorderLayout.CENTER);
		    add(pcardPanel,BorderLayout.SOUTH);
	  }
	  private static final class Lock { }
	  private final Object lock = new Lock();
	  public  void wait(int time) throws InterruptedException
	  {
		  synchronized (lock) {
			    //  while () {
			          lock.wait(time);
			      }
	  }
	  
	  public String requestaction(ClientBasic client) throws InterruptedException
	  {
	     //.wait();
		  staybutton.setEnabled(true);
		  hitbutton.setEnabled(true);
		  synchronized (lock) {
		    //  while () {
		          lock.wait();
		      }
		  
		  String temp = this.action;
		  this.action = null;
		  startbutton.setEnabled(false);
		  hitbutton.setEnabled(false);
		  return temp;
	  }
	  public void setaction (String act)
		 {
			 this.action = act;
		 }
	  
	  public void updatetable_dealer (ClientHand hand) throws InterruptedException
	  {
		 
		  for (int i=0 ; i<4;i++)
		  {
			  dealerlabel[i].setIcon(null);  
		  }
		  for (int i=0 ; i<hand.getCardsOnHand().size();i++)
		  {
			  int suit = hand.getCard(i).getSuit();
			  int value = hand.getCard(i).getCardValue();
			  ImageIcon icon = createImageIcon("../cards/"+suit+"_"+value+".png","card");
			  dealerlabel[i].setIcon(icon);
			
		  }
		  //wait till starting new game
		 wait(10000);
		  
	  }
	  public void updatetable (ClientHand hand)
	  {
		 // playercard = new JLabel[hand.getCardsOnHand().size()];
		  
		  for (int i=0 ; i<hand.getCardsOnHand().size();i++)
		  {
			  int suit = hand.getCard(i).getSuit();
			  int value = hand.getCard(i).getCardValue();
			  //get the image of card
			  ImageIcon icon = createImageIcon("../cards/"+suit+"_"+value+".png","card");
			  playercard[i].setIcon(icon);
			
		  }
		  
		   
		  
	  }
	  public void cleartable()
	  {
		  for (int i=0 ; i<4;i++)
		  {
			  dealerlabel[i].setIcon(null);  
		  }
		  ImageIcon icon = createImageIcon("../cards/b1fv.png","card");
		  dealerlabel[0].setIcon(icon);
		  dealerlabel[1].setIcon(icon);
		  for (int i=0 ; i<4;i++)
		  {
			  playercard[i].setIcon(null);  
		  }
	  }
	  //for updating status of game
	  public void update_status(String s)
	  {
		  // 101 this.winlosebox.setText(s);
		  JOptionPane.showMessageDialog(this, s);
	  }
	  public class start implements ActionListener
	  {
		 private BlackjackClient j;
		  public start(BlackjackClient j)
		 {
			 this.j = j;
		 }

		@Override
		public void actionPerformed(ActionEvent e) {


			//Thread creation			
			Thread t1 =new Thread(new ClientBasic(hostAddress /*host.getText()*/,playerName /*username.getText()*/,8000,j));
			//Thread t2 =new Thread(new ClientBasic("140.158.130.228" /*host.getText()*/,playerName /*username.getText()*/,8000,j));
			t1.start();
			//t2.start();
		}
	  }
		 public class hitbutton implements ActionListener
		  {
			 private BlackjackClient j;
			  public hitbutton(BlackjackClient j)
			 {
				  this.j = j;
			 }

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//client = new Client(host.getText(), username.getText(), 8000);
				j.setaction("1");
				//j.lock.notifyAll();
				synchronized (lock) {
				    // makeWakeupNeeded();
				     lock.notifyAll();
				 }
			}
			
	}
		 public class staybutton implements ActionListener
		  {
			 private BlackjackClient j;
			 //functions for stay button.
			  public staybutton (BlackjackClient j)
			 {
				  this.j = j;
			 }

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//client = new Client(host.getText(), username.getText(), 8000);
				j.setaction("2");
				synchronized (lock) {
				    // makeWakeupNeeded();
				     lock.notifyAll();
				 }
			}
		  }
		 protected static ImageIcon createImageIcon(String path,
	              String description) {
			  		java.net.URL imgURL = BlackjackClient.class.getResource(path);
			  			if (imgURL != null) {
			  				return new ImageIcon(imgURL, description);
			  			} else {
			  				//incase image is not found print error message.
			  					System.err.println("Couldn't find file: " + path);
			  					return null;
			  			}
		  }

	  public static void main (String [] args)
	  {
	    JFrame myFrame = new JFrame("BlackJack");
	    myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    myFrame.setContentPane(new BlackjackClient());
	    myFrame.setPreferredSize(new Dimension(700,550));

	    //Display the window.
	    myFrame.pack();
	    myFrame.setVisible(true);
	    
	  }//end display
	 
	  
}
