package edu.columbia.locationreceiver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

/**
 * This class is used to connect to, and query the database that stores sensor
 * readings.
 * 
 * @author Pooja
 *
 */
public class DBConnector {

	private static final DBConnector INSTANCE = new DBConnector();
	private static final Logger LOGGER = Logger.getLogger(DBConnector.class);

	private static Connection connection;

	private final String DB_CONNECTOR = "com.mysql.jdbc.Driver";
	private final String DB_CONNECTOR_FORMAT = "jdbc:mysql://{0}:{1}/{2}?user={3}&password={4}";
	private final String DB_CONN_URL = "locationdb.ci8zhgzbiolo.us-east-1.rds.amazonaws.com";
	private final String DB_CONN_PORT = "3306";
	private final String DB_NAME = "locationdb";
	private final String DB_USER_NAME = "locationdbuser";
	private final String DB_PASSWORD = "Ganapat1";

	private DBConnector() {
		try {
			Class.forName(DB_CONNECTOR);

			String jdbcUrl = MessageFormat.format(DB_CONNECTOR_FORMAT,
					DB_CONN_URL, DB_CONN_PORT, DB_NAME, DB_USER_NAME,
					DB_PASSWORD);

			connection = DriverManager.getConnection(jdbcUrl);
		} catch (ClassNotFoundException | SQLException e) {
			LOGGER.error("Could not connect to the database");
			e.printStackTrace();
		}
	}

	public static final DBConnector getInstance() {
		return INSTANCE;
	}

	/**
	 * Execute the query provided, with parameters provided as the query
	 * parameters
	 * 
	 * @author Pooja @param query
	 * @author Pooja @param parameters
	 * @author Pooja @return
	 */
	public final ResultSet executeQuery(String query, Object... parameters) {
		try {
			PreparedStatement pstmt = connection.prepareStatement(query);
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					pstmt.setObject(i + 1, parameters[i]);
				}
			}
			LOGGER.debug("Executing query:" + query);
			pstmt.execute();
			return pstmt.getResultSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Execute the query provided to insert readings, with parameters provided
	 * as the query parameters.
	 * 
	 * @author Pooja @param query
	 * @author Pooja @param parameters
	 * @author Pooja @return
	 */
	public final ResultSet executeInsertQuery(String query,
			Object... parameters) {
		try {
			PreparedStatement pstmt = connection.prepareStatement(query);
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					pstmt.setObject(i + 1, parameters[i]);
				}
			}
			LOGGER.debug("Executing insert query:" + query);
			pstmt.execute();
			return pstmt.getGeneratedKeys();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
