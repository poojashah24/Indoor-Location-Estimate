package edu.columbia.locationreceiver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnector {
	
	private static final DBConnector INSTANCE = new DBConnector();
	
	private static Connection connection;
	
	private DBConnector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			/*String jdbcUrl = "jdbc:mysql://" + PropertyHolder.getDBProperty(Constants.DB_URL) 
					+ ":" + PropertyHolder.getDBProperty(Constants.DB_PORT) 
					+ "/" + PropertyHolder.getDBProperty(Constants.DB_NAME)
					+ "?user=" + PropertyHolder.getDBProperty(Constants.DB_USERNAME)
					+ "&password=" + PropertyHolder.getDBProperty(Constants.DB_PASSWORD));*/
			
			String jdbcUrl = "jdbc:mysql://" + "locationdb.ci8zhgzbiolo.us-east-1.rds.amazonaws.com" 
					+ ":" + "3306"
					+ "/" + "locationdb"
					+ "?user=" + "locationdbuser"
					+ "&password=" + "Ganapat1";
			
			connection = DriverManager.getConnection(jdbcUrl);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final DBConnector getInstance() {
		return INSTANCE;
	}
	
	public final ResultSet executeQuery(String query, Object... parameters) {
		try {
			PreparedStatement pstmt = connection.prepareStatement(query);
			if(parameters != null) {
				for(int i=0;i<parameters.length;i++) {
					pstmt.setObject(i+1, parameters[i]);
				}
			}
			//System.out.println("pstmt: " + pstmt);
			pstmt.execute();
			return pstmt.getResultSet();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public final ResultSet executeInsertQuery(String query, Object... parameters) {
		try {
			PreparedStatement pstmt = connection.prepareStatement(query);
			if(parameters != null) {
				for(int i=0;i<parameters.length;i++) {
					pstmt.setObject(i+1, parameters[i]);
				}
			}
			pstmt.execute();
			return pstmt.getGeneratedKeys();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
