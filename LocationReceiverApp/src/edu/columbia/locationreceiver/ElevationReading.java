package edu.columbia.locationreceiver;

public class ElevationReading {
	private double altitude;
	private double stationReading;
	private double elevationReading;
	private long ts;
	
	public ElevationReading(final double altitude, final double stationReading,
			final double elevationReading, final long ts) {
		this.altitude = altitude;
		this.stationReading = stationReading;
		this.elevationReading = elevationReading;
		this.ts = ts;
	}
	
	public double getAltitude() {
		return altitude;
	}
	public double getStationReading() {
		return stationReading;
	}
	public double getElevationReading() {
		return elevationReading;
	}
	public long getTs() {
		return ts;
	}	
}
