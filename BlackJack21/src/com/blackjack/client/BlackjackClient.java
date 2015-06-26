/**
//Submitted by Md Abu Shufean
//Platform - Java, Eclipse
//This class consists of main method from where the execution starts for the com.blackjack.client section.
*/
package com.blackjack.client;

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

import com.blackjack.clientUtilities.ClientHand;
import com.blackjack.clientUtilities.ClientProperties;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BlackjackClient extends JPanel {

	// Labels,Textbox,buttons for GUI.
	JPanel topPanel = new JPanel();
	JPanel dcardPanel = new JPanel();
	JPanel pcardPanel = new JPanel();
	JTextPane winlosebox = new JTextPane();
	JTextPane username = new JTextPane();
	JTextPane host = new JTextPane();
	JButton hitbutton = new JButton();
	JButton startbutton = new JButton();
	JButton staybutton = new JButton();
	ClientBasic client;

	// JButton playagainbutton = new JButton();
	JLabel[] dealerLabel = new JLabel[4];
	JLabel playerlabel = new JLabel();
	JLabel[] playerCard = new JLabel[4];
	String hostAddress, playerName;

	JLabel playercardhit;
	String action;

	public BlackjackClient() {
		System.out.println(this.getClass().getResource(""));
		action = null;
		// settting colour,dimensions of screen
		// topPanel.setBackground(new Color(0, 122, 0));
		JLabel backgroundPicture = new JLabel(new ImageIcon(ClientProperties.BACKGROUND_IMG));
		// midBoard.setBackground(java.awt.Color.RED);

		topPanel.setBackground(new Color(6, 40, 12));
		dcardPanel.setBackground(new Color(6, 40, 12));
		pcardPanel.setBackground(new Color(6, 40, 12));
		topPanel.setLayout(new GridLayout(1, 6));

		Dimension d = new Dimension(10, 15);
		winlosebox.setSize(d);

		winlosebox.setText(" ");
		winlosebox.setFont(new java.awt.Font(ClientProperties.FONT, 1, 10));
		username.setSize(d);
		
		// Text box to accept ip address and username
		username.setText("Username ");
		host.setSize(d);
		host.setText("IP");
		hitbutton.setText("  Hit");
		
		hitbutton.addActionListener(new hitButtonAction(this));
		startbutton.addActionListener(new startButtonAction(this));
		
		// According to status of gameObject button click event is set.
		hitbutton.setEnabled(false);
		staybutton.setText("  Stay");
		staybutton.addActionListener(new stayButtonActions(this));
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
		// initialize for four card
		for (int i = 0; i < 4; i++) {
			dealerLabel[i] = new JLabel();
			dcardPanel.add(dealerLabel[i]);
		}
		
		pcardPanel.setLayout(new FlowLayout());
		// initialize for four card
		for (int i = 0; i < 4; i++) {
			playerCard[i] = new JLabel();
			pcardPanel.add(playerCard[i]);
		}
		pcardPanel.add(playerlabel);
		
		// dcardPanel.add(dealerLabel);
		dcardPanel.add(backgroundPicture);
		setLayout(new BorderLayout());
		
		add(topPanel, BorderLayout.NORTH);
		add(dcardPanel, BorderLayout.CENTER);
		add(pcardPanel, BorderLayout.SOUTH);
	}

	private static final class Lock {
	}

	private final Object lock = new Lock();

	// lock till a given time
	public void wait(int time) throws InterruptedException {
		synchronized (lock) {
			// while () {
			lock.wait(time);
		}
	}

	public String requestAction(ClientBasic client) throws InterruptedException {
		// .wait();
		staybutton.setEnabled(true);
		hitbutton.setEnabled(true);
		synchronized (lock) {
			// while 
			lock.wait();
		}

		String temp = this.action;
		this.action = null;
		startbutton.setEnabled(false);
		hitbutton.setEnabled(false);
		return temp;
	}

	public void setAction(String act) {
		this.action = act;
	}

	public void updateTableDealer(ClientHand hand) throws InterruptedException {

		// initialize for four card
		for (int i = 0; i < 4; i++) {
			dealerLabel[i].setIcon(null);
		}
		for (int indexOfCard = 0; indexOfCard < hand.getCardsOnHand().size(); indexOfCard++) {
			int suit = hand.getCard(indexOfCard).getSuit();
			int value = hand.getCard(indexOfCard).getCardValue();
			ImageIcon icon = createImageIcon(ClientProperties.CARD_DIRECTORY + suit + "_" + value + ClientProperties.CARD_IMG_EXT, "card");
			dealerLabel[indexOfCard].setIcon(icon);

		}
		// wait till starting new gameObject
		wait(10000);

	}

	// update player table according to card
	public void updateTable(ClientHand hand) {
		// playerCard = new JLabel[hand.getCardsOnHand().size()];
		for (int indexOfCard = 0; indexOfCard < hand.getCardsOnHand().size(); indexOfCard++) {
			int suit = hand.getCard(indexOfCard).getSuit();
			int value = hand.getCard(indexOfCard).getCardValue();
			// get the image of card
			ImageIcon icon = createImageIcon(ClientProperties.CARD_DIRECTORY + suit + "_" + value + ClientProperties.CARD_IMG_EXT, "card");
			playerCard[indexOfCard].setIcon(icon);

		}

	}

	// clear client table
	public void clearTable() {
		
		// total 4 cards to show
		for (int i = 0; i < 4; i++) {
			dealerLabel[i].setIcon(null);
		}
		ImageIcon icon = createImageIcon(ClientProperties.CARD_BACKGROUND, "card");
		dealerLabel[0].setIcon(icon);
		dealerLabel[1].setIcon(icon);
		// total 4 cards to show
		for (int i = 0; i < 4; i++) {
			playerCard[i].setIcon(null);
		}
	}

	// for updating status of gameObject
	public void updateStatus(String s) {
		// 101 this.winlosebox.setText(s);
		JOptionPane.showMessageDialog(this, s);
	}

	// Start button operation
	public class startButtonAction implements ActionListener {
		private BlackjackClient clientObj;

		public startButtonAction(BlackjackClient clientObj) {
			this.clientObj = clientObj;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			// Thread creation
			Thread threadObject = new Thread(new ClientBasic(hostAddress /* host.getText()*/, playerName /* username.getText() */, ClientProperties.PORT, clientObj));
			// Thread t2 =new Thread(new ClientBasic("140.158.130.228"
			// /*host.getText()*/,playerName /*username.getText()*/,8000,clientObj));
			threadObject.start();
			// t2.start();
		}
	}

	// hit button operation
	public class hitButtonAction implements ActionListener {
		private BlackjackClient clientObj;

		public hitButtonAction(BlackjackClient clientObj) {
			this.clientObj = clientObj;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			// com.blackjack.client = new Client(host.getText(), username.getText(), 8000);
			clientObj.setAction("1");
			// clientObj.lock.notifyAll();
			synchronized (lock) {
				// makeWakeupNeeded();
				lock.notifyAll();
			}
		}

	}

	// Stay button operation
	public class stayButtonActions implements ActionListener {
		private BlackjackClient clientObj;

		// functions for stay button.
		public stayButtonActions(BlackjackClient clientObj) {
			this.clientObj = clientObj;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			// com.blackjack.client = new Client(host.getText(), username.getText(), 8000);
			clientObj.setAction("2");
			synchronized (lock) {
				// makeWakeupNeeded();
				lock.notifyAll();
			}
		}
	}

	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = BlackjackClient.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			// incase image is not found print error message.
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void main(String[] args) {
		JFrame myFrame = new JFrame(ClientProperties.CLIENT_TITLE);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setContentPane(new BlackjackClient());
		myFrame.setPreferredSize(new Dimension(ClientProperties.CLIENT_WIDTH, ClientProperties.CLIENT_HEIGHT));

		// Display the window.
		myFrame.pack();
		myFrame.setVisible(true);

	}// end display

}
