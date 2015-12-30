package edu.columbia.locationreceiver;

/**
 * Stores details about Wi-Fi access points transmitted by each device.
 * 
 * @author Pooja
 *
 */
public class WifiReading {
	private String SSID;
	private double frequency;
	private int level;
	private long timestamp;

	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
