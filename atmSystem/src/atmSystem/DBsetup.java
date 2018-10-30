package atmSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBsetup {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://atmsystemdatabaseducm.crfvvk6wvmnr.eu-central-1.rds.amazonaws.com:3306/atmSystem";
	static final String USER = "ducm";
	static final String PASS = "emmawatson";

	public static void main(String[] args) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		
		System.out.println("Load MySQL JDBC driver");
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("MySQL JDBC Driver found.");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found.");
			e.printStackTrace();
			return;
		}

		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
		System.out.println("Creating statement...");
		stmt = conn.createStatement();

		String sql;
		System.out.println("Creating table...");
		sql = 	"CREATE TABLE IF NOT EXISTS account (account_id INT, pinCode INT, balance DECIMAL, UNIQUE (account_id));"; 
		stmt.executeUpdate(sql);
		
		System.out.println("Inserting values...");
		sql = "INSERT INTO account VALUES(1, 1111, 50), (2, 2222, 50), (3,3333,50)";
		stmt.executeUpdate(sql);
		
		//TRANSACTION DEMARCATION
		//money transfer: send 10 from account 1 to account 2
		String update_1 = "UPDATE account SET balance=40 WHERE account_id=1";
		String update_2 = "UPDATE account SET balance=60 WHERE account_id=2";
		
		//enable transaction demarcation
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		
		try {
		//submit transaction (prepare)
		System.out.println("Submit demarcated transactions to database");
		stmt.executeUpdate(update_1);
		stmt.executeUpdate(update_2);
		
		//Commit both transactions.
		System.out.println("Commit demarcated transactions.");
		conn.commit();
		} catch (SQLException e) {
			System.out.println("Rollback demarcated transactions.");
			conn.rollback();
		}
		
		System.out.println("Closing connection...");
		stmt.close();
	}
}