package edu.columbia.locationreceiver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

public class PropertyHolder {
	private static final Map<String,String> propertyMap;
	
	static {
		propertyMap = new HashMap<String, String>();
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream("app.xml"));
			propertyMap.put(Constants.DB_URL, p.getProperty(Constants.DB_URL));
			propertyMap.put(Constants.DB_PORT, p.getProperty(Constants.DB_PORT));
			propertyMap.put(Constants.DB_NAME, p.getProperty(Constants.DB_NAME));
			propertyMap.put(Constants.DB_USERNAME, p.getProperty(Constants.DB_USERNAME));
			propertyMap.put(Constants.DB_PORT, p.getProperty(Constants.DB_PORT));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static final String getDBProperty(String propName) {
		return propertyMap.get(propName);
	}
}
