package serverActivities;

import java.sql.*;
public class DatabaseConnection_2
{
	
	public static void main(String args[]){
		
		
		Connection conn = connect();
			Statement stmt = null;
			try {

					stmt = conn.createStatement();

			    ResultSet rs = stmt.executeQuery( "SELECT * FROM iplist" );
			    try {
			        while ( rs.next() ) {
			            int numColumns = rs.getMetaData().getColumnCount();
			            for ( int i = 1 ; i <= numColumns ; i++ ) {
			               // Column numbers start at 1.
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
            String password = "root";
            String url = "jdbc:mysql://localhost:3306/blackjack";
 
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
 
            System.out.println("Database connection established");
 
        } catch (Exception e) {
 
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
 
        }
        return conn;
 
    }
	
	
	
	
	
	
}
