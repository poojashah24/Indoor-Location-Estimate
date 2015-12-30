package edu.columbia.locationreceiver;

/**
 * Stores the pressure sensor readings transmitted by each device.
 * 
 * @author Pooja
 *
 */
public class PressureReading {
	private double pressure;
	private long timestamp;

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
