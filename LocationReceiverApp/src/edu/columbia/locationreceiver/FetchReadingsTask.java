package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Fetches and saves reference values from the closest National Weather Service station.
 *
 */
public class FetchReadingsTask extends Thread {

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

	@Override
	public void run() {
		StationReading reading = LocationUtils.getWeatherStationReading(lat, lon);
		service.saveWeatherStationReading(reading); 
		
		ElevationReading elevationReading = new ElevationReading(altitude,
				reading.getPressure(),
				PressureCompensator.getPressureAtElevation(altitude), ts);	
		service.saveElevationReadings(elevationReading);
	}
	
	private void getOpenWeatherAPIData() {

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
            
            StationReading reading = new StationReading(lat, lon, temp, pressure, humidity, name, weather, weatherDesc);
            service.saveWeatherStationReading(reading);            
            
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
