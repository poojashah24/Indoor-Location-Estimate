package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


public class FetchReadingsTask extends Thread {

	private static final String NOAA_URL = "http://w1.weather.gov/xml/current_obs/{0}.xml";
	private LocationService service;
	private double lat;
	private double lon;
	private double altitude;
	private long ts;
	
	public FetchReadingsTask(LocationService service, double lat, double lon, double altitude, long ts) {
		this.service = service;
		this.lat = lat;
		this.lon = lon;
		this.altitude = altitude;
		this.ts = ts;
	}
	/*
	 * {"coord":{"lon":145.77,"lat":-16.92},
		"sys":{"message":0.0288,"country":"AU","sunrise":1432154125,"sunset":1432194696},
		"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],
		"base":"stations",
		"main":{"temp":297.539,"temp_min":297.539,"temp_max":297.539,"pressure":1011.06,"sea_level":1028.57,"grnd_level":1011.06,"humidity":100},
		"wind":{"speed":4.67,"deg":157.001},
		"clouds":{"all":68},
		"rain":{"3h":0.325},
		"dt":1432178170,
		"id":2172797,
		"name":"Cairns",
		"cod":200}
*/
	@Override
	public void run() {
		//String station = LocationUtils.getClosestStation(lat, lon);
		CompReading reading = LocationUtils.getCompensatedReading(lat, lon);
		service.saveCompensatedReadings(reading); 
		
		ElevationReading elevationReading = new ElevationReading(altitude,
				reading.getPressure(),
				PressureCompensator.getPressureAtElevation(altitude), ts);	
		service.saveElevationReadings(elevationReading);
		
		
		/*String addr = MessageFormat.format(NOAA_URL, station);
		
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
				
				CompReading reading = new CompReading(lat, lon, temp, pressure, humidity, station, weather, null);
	            service.saveCompensatedReadings(reading); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	
	public void walk() {

		BufferedReader reader = null;
		String res = null;
		StringBuilder builder = new StringBuilder();
		try {
			URLConnection connection = new URL(Constants.WEATHER_INFO_URL + "lat="+lat+"&lon="+lon).openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.connect();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((res = reader.readLine()) != null) {
				builder.append(res);
			}
			String weatherJSON = builder.toString();
			JSONObject resObj = new JSONObject(weatherJSON);
			System.out.println(resObj.toString());
            JSONArray weatherObj = resObj.getJSONArray(Constants.WEATHER_WEATHER);
            JSONObject mainObj = resObj.getJSONObject(Constants.WEATHER_MAIN);
            JSONObject coordObj = resObj.getJSONObject(Constants.WEATHER_COORD);
            
            double lat = coordObj.getDouble(Constants.WEATHER_LAT);
            double lon = coordObj.getDouble(Constants.WEATHER_LON);
            
            String weather = weatherObj.getJSONObject(0).getString(Constants.WEATHER_MAIN);
            String weatherDesc = weatherObj.getJSONObject(0).getString(Constants.WEATHER_DESC);
            
            double temp = mainObj.getDouble(Constants.WEATHER_TEMP);
            double pressure = mainObj.getDouble(Constants.WEATHER_PRESSURE);
            double humidity = mainObj.getDouble(Constants.WEATHER_HUMIDITY);
            
            String name = resObj.getString(Constants.WEATHER_NAME);
            
            CompReading reading = new CompReading(lat, lon, temp, pressure, humidity, name, weather, weatherDesc);
            service.saveCompensatedReadings(reading);            
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}

}
