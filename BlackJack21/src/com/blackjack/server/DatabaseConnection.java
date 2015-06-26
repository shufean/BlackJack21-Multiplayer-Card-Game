package com.blackjack.server;

import java.sql.*;
public class DatabaseConnection
{	
	
	public static void setIntoUserTable(int userID, String value) {
		Connection conn = connect();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();

			if (userID == 1) {
				stmt.execute("INSERT INTO userlist SET user_" + userID + "='"+ value + "'");
			} else {
				stmt.execute("UPDATE userlist SET user_"+ userID+ "='"+ value+ "' where game_id = " +
						"(select maxID from (select MAX(game_id) maxID from userlist) as t)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} 
			catch (Throwable ignore) { 
				//  Propagate the original exception instead of this one that you may want just logged
			}
		}
	}
	
	public static void setDataIntoCardsTable(String username, String card, String value) {
		Connection conn = connect();
		Statement stmt = null;
		try {

			stmt = conn.createStatement();

			stmt.execute("INSERT INTO cards SET username='" + username
					+ "', card_type = '" + card + "', value = '" + value + "'");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (Throwable ignore) {
				/*
				 * Propagate the original exception instead of this one that you
				 * may want just logged
				 */
			}
		}
	}
	
	
	public static String[] cardData(){		
		DatabaseConnection dc = new DatabaseConnection();

		String cards[] = new String[100];
		Connection conn = connect();
		Statement stmt = null;
			try {
				stmt = conn.createStatement();
			    ResultSet rs = stmt.executeQuery( "SELECT * FROM cards" ); 
			    try {
			        while ( rs.next() ) {
			            int numColumns = rs.getMetaData().getColumnCount();
			            for ( int i = 1 ; i <= numColumns ; i++ ) {
			               // Column numbers startButtonAction at 1.
			               // Also there are many methods on the result set to return
			               //  the column as a particular type. Refer to the Sun documentation
			               //  for the list of valid conversions.
			            	cards[i-1] = rs.getObject(i).toString();
			               //System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
			            }
			        }
			    } 
			    catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    finally {
			        try { rs.close(); } catch (Throwable ignore) { /* Propagate the original exception
			instead of this one that you may want just logged */ }
			    }
			} 
		    catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
			    try { stmt.close(); } catch (Throwable ignore) { /* Propagate the original exception
			instead of this one that you may want just logged */ }
			}
		return cards;
		
	}
	
	public static void main(String args[]){
		
		DatabaseConnection dc = new DatabaseConnection();
		//dc.insertIntoUserTable(1, "Hello");
		//dc.insertIntoCardsTable("Hello", "2", "10");
		
		Connection conn = connect();
			Statement stmt = null;
			try {

					stmt = conn.createStatement();

			    ResultSet rs = stmt.executeQuery( "SELECT * FROM userlist" );
			    try {
			        while ( rs.next() ) {
			            int numColumns = rs.getMetaData().getColumnCount();
			            for ( int i = 1 ; i <= numColumns ; i++ ) {
			               // Column numbers startButtonAction at 1.
			               // Also there are many methods on the result set to return
			               //  the column as a particular type. Refer to the Sun documentation
			               //  for the list of valid conversions.
			               System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
			            }
			        }
			    } 
			    catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    finally {
			        try { rs.close(); } catch (Throwable ignore) { /* Propagate the original exception
			instead of this one that you may want just logged */ }
			    }
			} 
		    catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
			    try { stmt.close(); } catch (Throwable ignore) { /* Propagate the original exception
			instead of this one that you may want just logged */ }
			}
		
		
	}
	
	
	
	
	private static Connection connect() {
        Connection conn = null;
 
        try {
            String userName = "root";
            String password = "";
            String url = "jdbc:mysql://localhost:3306/blackjack";
 
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
 
            System.out.println("Database connection established");
 
        } catch (Exception e) {
 
            System.err.println("Cannot connect to database com.blackjack.server");
            e.printStackTrace();
 
        }
        return conn;
 
    }
	
	
}
