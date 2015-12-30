package edu.columbia.locationreceiver;

/**
 * Location coordinates, defined in terms of latitude and longitude. 
 * Used to store coordinates of weather stations.
 * @author Pooja
 *
 */
public class Coordinates {
	private double latitude;
	private double longitude;
	
	public Coordinates(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public double getLongitude() {
		return this.longitude;
	}
}
