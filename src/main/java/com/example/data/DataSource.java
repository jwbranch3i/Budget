package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSource {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/Budget";

	static final String USER = "root";
	static final String PASS = "utopia";

	private static Connection conn;

	private static DataSource instance = new DataSource();

	/************************ DataSource ****************************************/
	private DataSource() {
	}

	/*********************** getInstance *****************************************/
	public static DataSource getInstance() {
		return instance;
	}

	public static Connection getConn() {
		return conn;
	}

	/*********************** open *******************************************/
	public boolean open() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			return true;
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Could not connect to database: " + e.getMessage());
			return false;
		}
	}

	/************************* close ******************************************/
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("Could not close connection" + e.getMessage());
		}

	}

	/****************** insert category record **********************/
	/**
	 * Inserts a category record into the database.
	 * 
	 * @param item The LineItem object representing the category record to be
	 *             inserted.
	 * @return true if the record was successfully inserted, false otherwise.
	 */
	public boolean insertCatogeoryRecord(LineItem item) {
		PreparedStatement psInsertRecord = null;
		try {

			String insertStmt = DB.INSERT_CATEGORY;
			psInsertRecord = conn.prepareStatement(insertStmt, Statement.RETURN_GENERATED_KEYS);

			psInsertRecord.setInt(1, item.getType());
			psInsertRecord.setString(2, item.getParent());
			psInsertRecord.setString(3, item.getCategory());

			psInsertRecord.executeUpdate();

			ResultSet rs = psInsertRecord.getGeneratedKeys();
			if (rs.next()) {
				item.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println("Could not insert record: " + e.getMessage());
			return false;
		} finally {
			try {
				if (psInsertRecord != null) {
					psInsertRecord.close();
				}
			} catch (SQLException e) {
				System.out.println("Could not close statement: " + e.getMessage());
			}
		}
		return true;
	}

	/*************************** find category record **********************/
	public int findCategeryRecords(LineItem item) {
		PreparedStatement psFindRecord = null;
		ResultSet rs = null;
		int count = 0;
		try {
			psFindRecord = conn.prepareStatement(DB.FIND_CATEGORY);
			psFindRecord.setString(1, item.getParent());
			psFindRecord.setString(2, item.getCategory());

			rs = psFindRecord.executeQuery();
			while (rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			System.out.println("Error searching for record: " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (psFindRecord != null) {
					psFindRecord.close();
				}
			} catch (SQLException e) {
				System.out.println("Could not close statement: " + e.getMessage());
			}
		}
		return count;
	}

	public void deleteAllCategeryRecords() {
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(DB.DELETE_ALL_CATEGORY);
		} catch (SQLException e) {
			System.out.println("Could not delete all records: " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("Could not close statement: " + e.getMessage());
			}
		}
	}


	/**********************************************************/
	
}
