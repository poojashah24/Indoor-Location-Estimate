package edu.columbia.locationreceiver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Stores key-value pairs read from the application level properties file.
 * @author Pooja
 *
 */
public class PropertyHolder {
	private static final Map<String, String> propertyMap;

	static {
		propertyMap = new HashMap<String, String>();
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream("app.xml"));
			propertyMap.put(Constants.DB_URL, p.getProperty(Constants.DB_URL));
			propertyMap
					.put(Constants.DB_PORT, p.getProperty(Constants.DB_PORT));
			propertyMap
					.put(Constants.DB_NAME, p.getProperty(Constants.DB_NAME));
			propertyMap.put(Constants.DB_USERNAME,
					p.getProperty(Constants.DB_USERNAME));
			propertyMap
					.put(Constants.DB_PORT, p.getProperty(Constants.DB_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String getDBProperty(String propName) {
		return propertyMap.get(propName);
	}
}
