package edu.columbia.locationreceiver;

import java.util.List;

public class LocationInfo {
	private Location locationDetails;
	private DeviceInfo deviceInfo;
	private PressureReading pressure;
	private LocationCoordinates coordinates;
	private MagnetometerReading magnetometerReading;
	private List<WifiReading> wifiList;
	
	private List<PressureReading> pressureReadingList;
	private List<LocationCoordinates> locationCoordinatesList;
	private List<MagnetometerReading> magnetometerReadingList;
	private List<List<WifiReading>> wifiReadingsList;
	
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public PressureReading getPressure() {
		return pressure;
	}
	public void setPressure(PressureReading pressure) {
		this.pressure = pressure;
	}
	public MagnetometerReading getMagnetometerReading() {
		return magnetometerReading;
	}
	public void setMagnetometerReading(MagnetometerReading magnetometerReading) {
		this.magnetometerReading = magnetometerReading;
	}
	public LocationCoordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(LocationCoordinates coordinates) {
		this.coordinates = coordinates;
	}
	public Location getLocationDetails() {
		return locationDetails;
	}
	public void setLocationDetails(Location locationDetails) {
		this.locationDetails = locationDetails;
	}
	public List<WifiReading> getWifiList() {
		return wifiList;
	}
	public void setWifiList(List<WifiReading> wifiList) {
		this.wifiList = wifiList;
	}
	public List<PressureReading> getPressureReadingList() {
		return pressureReadingList;
	}
	public void setPressureReadingList(List<PressureReading> pressureReadingList) {
		this.pressureReadingList = pressureReadingList;
	}
	public List<MagnetometerReading> getMagnetometerReadingList() {
		return magnetometerReadingList;
	}
	public void setMagnetometerReadingList(
			List<MagnetometerReading> magnetometerReadingList) {
		this.magnetometerReadingList = magnetometerReadingList;
	}
	public List<LocationCoordinates> getLocationCoordinatesList() {
		return locationCoordinatesList;
	}
	public void setLocationCoordinatesList(
			List<LocationCoordinates> locationCoordinatesList) {
		this.locationCoordinatesList = locationCoordinatesList;
	}
	public List<List<WifiReading>> getWifiReadingsList() {
		return wifiReadingsList;
	}
	public void setWifiReadingsList(List<List<WifiReading>> wifiReadingsList) {
		this.wifiReadingsList = wifiReadingsList;
	}
}
