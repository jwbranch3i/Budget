package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
	public boolean insertCategoryRecord(LineItemCSV item) {
		return true;
	}

	/* check if category record is in database **********************/
	public LineItemCSV findCategeryRecords(LineItemCSV item) {

		LineItemCSV lineItem = new LineItemCSV();
		PreparedStatement psFindRecord = null;
		ResultSet rs = null;
		try {
			psFindRecord = conn.prepareStatement(DB.CAT_FIND_CATEGORY);
			psFindRecord.setString(1, item.getParent());
			psFindRecord.setString(2, item.getCategory());

			rs = psFindRecord.executeQuery();
			if (rs.next()) {
				lineItem.setId(rs.getInt(DB.CAT_COL_ID_INDEX));
				lineItem.setType(rs.getInt(DB.CAT_COL_TYPE_INDEX));
				lineItem.setParent(rs.getString(DB.CAT_COL_PARENT_INDEX));
				lineItem.setCategory(rs.getString(DB.CAT_COL_CATEGORY_INDEX));
				return lineItem;
			} else {
				return item;
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
		return null;
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
