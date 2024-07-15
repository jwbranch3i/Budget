package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Budget";

    static final String USER = "root";
    static final String PASS = "utopia";
    

//					+ DB_NAME;
	// public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\VSCwork\\Acct\\"
	// 				+ DB_NAME;

	private static Connection conn;

	//private static final PreparedStatement ps_updateFieldDate;

	private static DataSource instance = new DataSource();

	/****************************************************************/
	private DataSource()
	{
	}

	/****************************************************************/
	public static DataSource getInstance()
	{
		return instance;
	}

	public static Connection getConn()
	{
		return conn;
	}

	/******************************************************************/
	public boolean open()
	{
		try
		{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			return true;
		}
		catch (SQLException | ClassNotFoundException e)
		{
			System.out.println("Could not connect to database: " + e.getMessage());
			return false;
		}
	}

	/*******************************************************************/
	public void close()
	{
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (SQLException e)
		{
			System.out.println("Could not close connection" + e.getMessage());
		}

	}

}
