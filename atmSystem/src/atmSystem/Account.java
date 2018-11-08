package atmSystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Account {
	
	private Integer account_Id ;
	private String pinCode ;
	
	public Account (int account_Id, String pinCode) {
		this.pinCode = pinCode;
		this.account_Id = account_Id;
	}
	
	public boolean isCustomer(Statement stmt) throws SQLException {
		String isCustomer = "SELECT COUNT(*) FROM account WHERE account_Id=" +
				Integer.toString(this.account_Id) + " AND PinCode =" +
				this.pinCode ;
		ResultSet resultat = null;
		resultat = stmt.executeQuery(isCustomer);
		while(resultat.next()) {
			if(resultat.getInt(1) < 1) {
				return false;
			}
		}
		return true;
	}
	
	public int getLock(Statement stmt) throws SQLException {
		String readBalance = "SELECT locker FROM account WHERE account_Id=" +
				Integer.toString(account_Id) + " AND PinCode =" +
				this.pinCode ;
		ResultSet resultat = null;
		resultat = stmt.executeQuery(readBalance);
		while(resultat.next()) {
			return resultat.getInt(1);
		}
		return 0;
	}
	
	public void lock(Connection conn, Statement stmt) throws SQLException {
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		String update_1 = "UPDATE account SET locker=1 WHERE account_id=" +
							Integer.toString(this.account_Id);
		try {
			stmt.executeUpdate(update_1);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		}
	}
	
	public void unlock(Connection conn, Statement stmt) throws SQLException {
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		String update_1 = "UPDATE account SET locker=0 WHERE account_id=" +
							Integer.toString(this.account_Id);
		try {
			stmt.executeUpdate(update_1);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		}
	}
	
	public float readBalance(Statement stmt) throws SQLException {
		String readBalance = "SELECT balance FROM account WHERE account_Id=" +
							Integer.toString(account_Id) + " AND PinCode =" +
							this.pinCode ;
		ResultSet resultat = null;
		resultat = stmt.executeQuery(readBalance);
		while(resultat.next()) {
			return resultat.getFloat(1);
		}
		return 0;
	}
	
	
	public void withdrawal(Connection conn, Statement stmt, float withdrawal) throws SQLException {
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		float balance = this.readBalance(stmt);
		int lock = this.getLock(stmt);
		if(withdrawal < 0 || withdrawal > balance || lock == 1) {
			System.out.println("Operation impossible");
		} else {
			this.lock(conn, stmt);
			balance = this.readBalance(stmt) - withdrawal;
			System.out.println("Whithdrawal Account...");
			String update_1 = "UPDATE account SET balance=" +
								Float.toString(balance) +
								"WHERE account_id=" +
								Integer.toString(this.account_Id);
			try {
				stmt.executeUpdate(update_1);
				conn.commit();
			} catch (SQLException e) {
				System.out.println("Rollback demarcated transactions.");
				conn.rollback();
			}
			this.unlock(conn, stmt);
		}
		System.out.println("Your account's balance is:");
		System.out.println(this.readBalance(stmt));
	}
	
	public void deposit(Connection conn, Statement stmt, float deposit) throws SQLException{
		conn.setAutoCommit(false);
		stmt = conn.createStatement();
		float balance = this.readBalance(stmt);
		int lock = this.getLock(stmt);
		if(deposit < 0 || lock == 1) {
			System.out.println("ERREUR : Depot négatif");
		} else {
			this.lock(conn, stmt);
			balance = balance + deposit;
			System.out.println("Deposit Account...");
			String update_1 = "UPDATE account SET balance=" +
					Float.toString(balance) +
					"WHERE account_id=" +
					Integer.toString(this.account_Id);
			try {
				stmt.executeUpdate(update_1);
				conn.commit();
			} catch (SQLException e) {
				System.out.println("Rollback demarcated transactions.");
				conn.rollback();
			}
		}
		this.unlock(conn, stmt);
		System.out.println("Your account's balance is:");
		System.out.println(this.readBalance(stmt));
	}

}
