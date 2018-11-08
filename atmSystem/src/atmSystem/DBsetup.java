package atmSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DBsetup {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://atmsystemdatabaseducm.crfvvk6wvmnr.eu-central-1.rds.amazonaws.com:3306/atmSystem";
	static final String USER = "ducm";
	static final String PASS = "emmawatson";

	public static void main(String[] args) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		int state = 1 ;
		Scanner sc = new Scanner(System.in);
		
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
		sql = 	"CREATE TABLE IF NOT EXISTS account (account_id INT, pinCode INT, balance DECIMAL, locker INT, UNIQUE (account_id));"; 
		stmt.executeUpdate(sql);
		
//		System.out.println("Inserting values...");
//		sql = "INSERT INTO account VALUES(1, 1111, 50, 0), (2, 2222, 50, 0), (3,3333,50,0)";
//		stmt.executeUpdate(sql);
		
		
		while( state == 1) {
			System.out.println("What is your account number?");
			String account_Id = sc.nextLine();
			System.out.println("What is your pin code");
			String pinCode = sc.nextLine();
			Account account = new Account(Integer.parseInt(account_Id),pinCode);
			if(account.isCustomer(stmt)) {
				System.out.println("Whitdrawl, Deposit or Balance ? W/D/B");
				String operation = sc.nextLine();
				switch (operation) {
					case "W":
						System.out.println("How much do you want to withdraw?");
						String w = sc.nextLine();
						account.withdrawal(conn, stmt, Float.parseFloat(w));
						break;
					case "D":
						System.out.println("How much do you want to deposit?");
						String d = sc.nextLine();
						account.deposit(conn, stmt, Float.parseFloat(d));
						break;
					case "B":
						System.out.println("Your account's balance is:");
						System.out.println(account.readBalance(stmt));
						break;
					default:
						System.out.println("Do you want to exit the application? Y/N");
						String quit = sc.nextLine();
						switch(quit) {
							case "Y":
								state = 0;
								break;
							default:
								
						}		
				}
			} else {
				System.out.println("You are not a customer! Do you want to exit the application ? Y/N");
				String quit = sc.nextLine();
				switch(quit) {
					case "Y":
						state = 0;
						break;
					default:
						
				}
			}	
		}
		System.out.println("Closing connection...");
		stmt.close();
	}
	
	
//	public static void main(String[] args) throws SQLException {
//		Connection conn = null;
//		Statement stmt = null;
//		
//		System.out.println("Load MySQL JDBC driver");
//		try {
//			Class.forName(JDBC_DRIVER);
//			System.out.println("MySQL JDBC Driver found.");
//		} catch (ClassNotFoundException e) {
//			System.out.println("MySQL JDBC Driver not found.");
//			e.printStackTrace();
//			return;
//		}
//
//		System.out.println("Connecting to database...");
//		conn = DriverManager.getConnection(DB_URL, USER, PASS);
//		
//		System.out.println("Creating statement...");
//		stmt = conn.createStatement();
//
//		String sql;
//		System.out.println("Creating table...");
//		sql = 	"CREATE TABLE IF NOT EXISTS account (account_id INT, pinCode INT, balance DECIMAL, UNIQUE (account_id));"; 
//		stmt.executeUpdate(sql);
//		
//		System.out.println("Inserting values...");
//		sql = "INSERT INTO account VALUES(1, 1111, 50), (2, 2222, 50), (3,3333,50)";
//		stmt.executeUpdate(sql);
//		
//		//TRANSACTION DEMARCATION
//		//money transfer: send 10 from account 1 to account 2
//		String update_1 = "UPDATE account SET balance=40 WHERE account_id=1";
//		String update_2 = "UPDATE account SET balance=60 WHERE account_id=2";
//		
//		//enable transaction demarcation
//		conn.setAutoCommit(false);
//		stmt = conn.createStatement();
//		
//		try {
//		//submit transaction (prepare)
//		System.out.println("Submit demarcated transactions to database");
//		stmt.executeUpdate(update_1);
//		stmt.executeUpdate(update_2);
//		
//		//Commit both transactions.
//		System.out.println("Commit demarcated transactions.");
//		conn.commit();
//		} catch (SQLException e) {
//			System.out.println("Rollback demarcated transactions.");
//			conn.rollback();
//		}
//		
//		System.out.println("Closing connection...");
//		stmt.close();
//	}
	
}