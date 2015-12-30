package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

/**
 * This utility class contains methods used for integration with the National Weather Service XML API.
 * @author Pooja
 *
 */
public class LocationUtils {

	// Reverse mapping of location coordinates to a weather station code.
	private static final Map<String, String> coordinatesToStationMap = new ConcurrentHashMap<String, String>();
	
	// Mapping of a weather station code to its location coordinates.
	private static final Map<Coordinates, String> stationMap = new ConcurrentHashMap<Coordinates, String>();
	
	// Mapping of a weather station code to the cached pressure reading at the station.
	private static final Map<String, StationReading> pressureMap = new ConcurrentHashMap<String, StationReading>();

	// Timer to clear the pressure cache.
	private static final Timer timer = new Timer();
	private static final int DELAY_IN_MILLIS = 1000 * 60 * 10;

	private static final String NOAA_URL = "http://w1.weather.gov/xml/current_obs/{0}.xml";
	private static double R = 6371000;

	static {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
			LocationUtils.class.getClassLoader().getResourceAsStream(
					"edu/columbia/locationreceiver/stations.csv")));
			String entry = null;
			while ((entry = reader.readLine()) != null) {
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

	/**
	 * Calculates the closest weather station to the location specified by the
	 * coordinates, fetches the reference values and caches them.
	 * 
	 * @author Pooja @param latitude
	 * @author Pooja @param longitude
	 * @author Pooja @return
	 */
	public static StationReading getWeatherStationReading(double latitude,
			double longitude) {
		StationReading reading = null;

		String station = getClosestStation(latitude, longitude);
		if (pressureMap.containsKey(station)) {
			return pressureMap.get(station);
		}

		reading = getReadingForStation(station, latitude, longitude);
		pressureMap.put(station, reading);
		timer.schedule(new CleanupTask(station), DELAY_IN_MILLIS);

		return reading;
	}

	/**
	 * Returns that station code of the weather station closest to the given location (defined by the set of coordinates provided)
	 * The shortest distance is calculated using the Haversine formula.
	 * @author Pooja @param latitude
	 * @author Pooja @param longitude
	 * @author Pooja @return
	 */
	public static String getClosestStation(double latitude, double longitude) {
		String key = Math.round(latitude) + "," + Math.round(longitude);
		if (coordinatesToStationMap.containsKey(key)) {
			return coordinatesToStationMap.get(key);
		}

		Set<Entry<Coordinates, String>> coord = stationMap.entrySet();
		double min = Double.MAX_VALUE;
		String station = null;
		for (Entry<Coordinates, String> c : coord) {
			double distance = getDistanceBetweenLocations(latitude, longitude,
					c.getKey().getLatitude(), c.getKey().getLongitude());
			if (distance < min) {
				min = distance;
				station = c.getValue();
			}
		}
		coordinatesToStationMap.put(key, station);
		return station;
	}

	/**
	 * Implementation of the Haversine formula
	 * 
	 * @author Pooja @param lat1
	 * @author Pooja @param long1
	 * @author Pooja @param lat2
	 * @author Pooja @param long2
	 * @author Pooja @return the distance between the two locations defined by the two sets of coordinates provided.
	 */
	public static double getDistanceBetweenLocations(double lat1, double long1,
			double lat2, double long2) {
		double lat1Rad = Math.toRadians(lat1);
		double lat2Rad = Math.toRadians(lat2);

		double latDelta = Math.toRadians(lat2 - lat1);
		double longDelta = Math.toRadians(long2 - long1);

		double angle = (Math.sin(latDelta / 2) * Math.sin(latDelta / 2))
				+ Math.cos(lat1Rad) * Math.cos(lat2Rad)
				* (Math.sin(longDelta / 2) * Math.sin(longDelta / 2));

		double c = 2 * Math.atan2(Math.sqrt(angle), Math.sqrt(1 - angle));

		return R * c;
	}

	/**
	 * For a given station, fetches the current readings using the XML API and parses the XML document fetched.
	 * @author Pooja @param station
	 * @author Pooja @param lat
	 * @author Pooja @param lon
	 * @author Pooja @return
	 */
	public static StationReading getReadingForStation(String station, double lat,
			double lon) {
		StationReading reading = null;
		String addr = MessageFormat.format(NOAA_URL, station);

		try {
			URL url = new URL(addr);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String s = null;
			StringBuilder b = new StringBuilder();
			while ((s = reader.readLine()) != null) {
				b.append(s);
			}

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(b
					.toString())));
			if (doc != null) {

				double temp = 0;
				double pressure = 0;
				double humidity = 0;
				String weather = null;

				NodeList elem = doc.getElementsByTagName("current_observation");
				NodeList children = elem.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node n = children.item(i);
					if (n.getNodeName().equals("pressure_mb")) {
						pressure = Double.parseDouble(n.getTextContent());
					} else if (n.getNodeName().equals("temp_f")) {
						temp = Double.parseDouble(n.getTextContent());
					} else if (n.getNodeName().equals("relative_humidity")) {
						humidity = Double.parseDouble(n.getTextContent());
					} else if (n.getNodeName().equals("weather")) {
						weather = n.getTextContent();
					}
				}

				reading = new StationReading(lat, lon, temp, pressure, humidity,
						station, weather, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return reading;
	}

	/**
	 * This task cleans up stale pressure reading values, i.e., those values that are more than an hour old.
	 * @author Pooja
	 *
	 */
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
