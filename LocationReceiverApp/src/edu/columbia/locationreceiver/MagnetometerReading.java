package edu.columbia.locationreceiver;

/**
 * Stores the magnetometer sensor readings transmitted by each device.
 * 
 * @author Pooja
 *
 */
public class MagnetometerReading {
	private double x;
	private double y;
	private double z;
	private long timestamp;

	public MagnetometerReading() {
	}

	public MagnetometerReading(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
}
