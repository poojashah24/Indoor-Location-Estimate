package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//Haversine formula
/*var R = 6371000; // metres
var φ1 = lat1.toRadians();
var φ2 = lat2.toRadians();
var Δφ = (lat2-lat1).toRadians();
var Δλ = (lon2-lon1).toRadians();

var a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
        Math.cos(φ1) * Math.cos(φ2) *
        Math.sin(Δλ/2) * Math.sin(Δλ/2);
var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

var d = R * c;*/

public class LocationUtils {
	
	
	private static final Map<String, String> coordinatesToStationMap = new ConcurrentHashMap<String, String>();
	private static final Map<Coordinates, String> stationMap = new ConcurrentHashMap<Coordinates, String>();
	private static final Map<String,CompReading> pressureMap = new ConcurrentHashMap<String, CompReading>();
	
	private static final Timer timer = new Timer();
	private static final int DELAY_IN_MILLIS = 1000*60*10;
	
	private static final String NOAA_URL = "http://w1.weather.gov/xml/current_obs/{0}.xml";
	private static double R = 6371000;
	
	static {
		BufferedReader reader = null;
		try {
			 /*reader = new BufferedReader(new FileReader(
					"edu/columbia/locationreceiver/stations.csv"));*/
			 
			reader = new BufferedReader(new InputStreamReader(LocationUtils.class
					.getClassLoader().getResourceAsStream("edu/columbia/locationreceiver/stations.csv")));
			String entry = null;
			while((entry = reader.readLine()) != null) {
				String[] tokens = entry.split(",");
				Coordinates coord = new Coordinates(
						Double.parseDouble(tokens[2]),
						Double.parseDouble(tokens[3]));
				stationMap.put(coord, tokens[0]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CompReading getCompensatedReading(double latitude, double longitude) {
		CompReading reading = null;
		
		String station = getClosestStation(latitude, longitude);
		if(pressureMap.containsKey(station)) {
			return pressureMap.get(station);
		}
		
		reading = getReadingForStation(station, latitude, longitude);
		pressureMap.put(station, reading);		
		timer.schedule(new CleanupTask(station) , DELAY_IN_MILLIS);
		
		return reading;
	}
	
	public static String getClosestStation(double latitude, double longitude) {
		String key = Math.round(latitude)+","+Math.round(longitude);
		if(coordinatesToStationMap.containsKey(key)) {
			return coordinatesToStationMap.get(key);
		}
		
		Set<Entry<Coordinates, String>> coord = stationMap.entrySet();
		double min = Double.MAX_VALUE;
		String station = null;
		for(Entry<Coordinates, String> c : coord) {
			double distance = getDistanceBetweenLocations(latitude, longitude, c.getKey().getLatitude(), c.getKey().getLongitude());
			if(distance < min) {
				min = distance;
				station = c.getValue();
			}
		}
		coordinatesToStationMap.put(key, station);
		return station;
	}
	
	public static double getDistanceBetweenLocations(double lat1, double long1,
			double lat2, double long2) {
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);
		
		double latDelta = Math.toRadians(lat2-lat1);
		double longDelta = Math.toRadians(long2-long1);
		
		double angle = (Math.sin(latDelta / 2) * Math.sin(latDelta / 2))
				+ Math.cos(lat1Rad) * Math.cos(lat2Rad)
				* (Math.sin(longDelta / 2) * Math.sin(longDelta / 2));
		
		double c = 2 * Math.atan2(Math.sqrt(angle), Math.sqrt(1-angle));
		
		return R * c;
	}
	
	public static CompReading getReadingForStation(String station, double lat, double lon) {
		CompReading reading = null;
		String addr = MessageFormat.format(NOAA_URL, station);
	
		try {
			URL url = new URL(addr);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String s = null;
			StringBuilder b = new StringBuilder();
			while((s = reader.readLine()) != null) {
				b.append(s);
			}
			System.out.println(b.toString());
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(b.toString())));
			if(doc != null) {
				
				double temp = 0;
				double pressure = 0;
				double humidity = 0;
				String weather = null;
				
				NodeList elem = doc.getElementsByTagName("current_observation");
				NodeList children = elem.item(0).getChildNodes();
				for(int i=0;i<children.getLength();i++) {
					Node n = children.item(i);
					if(n.getNodeName().equals("pressure_mb")) {
						pressure = Double.parseDouble(n.getTextContent());
					} else if(n.getNodeName().equals("temp_f")) {
						temp = Double.parseDouble(n.getTextContent());
					} else if(n.getNodeName().equals("relative_humidity")) {
						humidity = Double.parseDouble(n.getTextContent());
					} else if(n.getNodeName().equals("weather")) {
						weather = n.getTextContent();
					} 
				}	
				
				reading = new CompReading(lat, lon, temp, pressure, humidity, station, weather, null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return reading;
	}
	
	static class CleanupTask extends TimerTask {
		
		private String key;
		public CleanupTask(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			pressureMap.remove(key);
		}
		
	}
}


