package edu.columbia.locationreceiver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class LocationDAO {
	private static final DBConnector dbInstance = DBConnector.getInstance();
	
	public void savePressureReading(List<PressureReading> pressureReadings, DeviceInfo deviceInfo) {
		System.out.println("in savePressureReading");
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for(PressureReading pressure : pressureReadings) {
			savePressureReading(deviceId, pressure);
		}
	}
	
	public void saveCoordinatesList(List<LocationCoordinates> coordinates, DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for(LocationCoordinates c : coordinates) {
			saveLocationCoordinates(deviceId, c);
		}
	}
	
	public void saveWifiLists(List<List<WifiReading>> wifiReadings, DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for(List<WifiReading> wifiReading : wifiReadings) {
			saveWifiList(deviceId, wifiReading);
		}
	}
	
	public void saveMagnetometerReadings(List<MagnetometerReading> magnetometerReadings, DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for(MagnetometerReading magnetometerReading : magnetometerReadings) {
			saveMagnetometerReading(deviceId, magnetometerReading);
		}
	}
	
	public void saveLocation(Location location, DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		int maxid = getMaxLocationID();
		saveLocationDetails(maxid, location, deviceId);
	}
	
	//insert into compensated_measurements(measurement_ts, latitude, longitude, weather, weather_desc, temperature, pressure, name, humidity)
	//values (?,?,?,?,?,?,?,?,?);
	public void saveReadings(CompReading compReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_READINGS, new Date(), compReading.getLat(), compReading.getLon(),
				compReading.getWeather(), compReading.getWeatherDesc(), compReading.getTemp(), compReading.getPressure(),
				compReading.getName(), compReading.getHumidity());
	}
	
	//insert into comp_pressure_readings (altitude, nws_pressure, comp_pressure, updated) values (?,?,?,?,?);
	public void saveElevationReading(ElevationReading elevationReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_COMP_READINGS, elevationReading.getAltitude(),
				elevationReading.getStationReading(), elevationReading.getElevationReading(), 
				new Date(elevationReading.getTs()));
	}
	
	//public static final String INSERT_PRESSURE_COMP_READINGS = "insert into comp_pressure_readings "
	//+ "(pressure, nws_pressure, comp_pressure) values (?,?,?);";
	public void saveAllPressureReadings(double altitude, double pressure, double nws_pressure, double comp_pressure) {
		dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_COMP_READINGS,
				altitude, pressure, nws_pressure, comp_pressure, new Date());
	}
	
	/*public void saveLocation(LocationInfo locationInfo) {
		//int id = checkIfExists(locationInfo.getLocationDetails());
		int deviceId = checkIfExists(locationInfo.getDeviceInfo());
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(locationInfo.getDeviceInfo());
		}
		int maxid = getMaxLocationID();
		saveLocationDetails(maxid, locationInfo.getLocationDetails());
		
		for(LocationCoordinates coordinates : locationInfo.getLocationCoordinatesList()) {
			saveLocationCoordinates(deviceId, coordinates);
		}
		
		for(PressureReading pressure : locationInfo.getPressureReadingList()) {
			savePressureReading(deviceId, pressure);
		}
		
		for(MagnetometerReading magnetometerReading : locationInfo.getMagnetometerReadingList()) {
			saveMagnetometerReading(deviceId, magnetometerReading);
		}
		
		for(List<WifiReading> wifiReadingList : locationInfo.getWifiReadingsList()) {
			saveWifiList(deviceId, wifiReadingList);
		}
	}*/
	
	private void updateLocationCoordinates(int id, LocationCoordinates locationCoordinates) {
		dbInstance.executeQuery(QueryConstants.UPDATE_COORDINATES,
				locationCoordinates.getLatitude(),
				locationCoordinates.getLongitude(),
				locationCoordinates.getTimestamp(), id);
	}
	
	private void updatePressureReading(int id, PressureReading pressureReading) {
		dbInstance.executeQuery(QueryConstants.UPDATE_PRESSURE,
				pressureReading.getPressure(),
				pressureReading.getTimestamp(),
				id);
	}
	
	private void updateWifiList(int id, List<WifiReading> wifiList) {
		dbInstance.executeQuery(QueryConstants.DELETE_WIFI, id);
		saveWifiList(id, wifiList);
	}
	
	
	private int checkIfExists(DeviceInfo device) {
		int id = -1;
		ResultSet rs = dbInstance.executeQuery(QueryConstants.QUERY_DEVICE, device.getDevice(), device.getProduct());
		try {
			if(rs.next()) {
				id = rs.getInt("device_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	private int checkIfExists(Location location) {
		int id = -1;
		ResultSet rs = dbInstance.executeQuery(QueryConstants.QUERY_LOCATION, location.getName(), location.getBuilding(),
				location.getFloor(), location.getRoom());
		try {
			if(rs.next()) {
				id = rs.getInt("loc_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	private int getMaxLocationID() {
		int locationID = -1;
		ResultSet rs = dbInstance.executeQuery(QueryConstants.GET_MAX_ID, null);
		try {
			if(rs.next()) {
				locationID = rs.getInt("max_id") + 1;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return locationID;
	}
	
	//insert into device_info(location_id,os_version,build_version,build_version_sdk,device,model,product,updated) values (?,?,?,?,?,?,?,?);
	private int saveDeviceInfo(DeviceInfo deviceInfo) {
		try {
			ResultSet rs = dbInstance.executeInsertQuery(QueryConstants.INSERT_DEVICE_INFO,
					deviceInfo.getOsVersion(), deviceInfo.getBuildVersion(), 
					deviceInfo.getApiLevel(), deviceInfo.getDevice(), deviceInfo.getModel(),
					deviceInfo.getProduct(), new Date(deviceInfo.getTimestamp()));
			return rs.getInt(0);
		} catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}

	private void savePressureReading(int deviceId, PressureReading pressure) {
		try {
			dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_READING,
					deviceId, pressure.getPressure(), new Date(pressure.getTimestamp()));
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void saveMagnetometerReading(int deviceId, MagnetometerReading magnetometerReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_MAGNETOMETER_READING, 
				deviceId, magnetometerReading.getX(), magnetometerReading.getY(), magnetometerReading.getZ(), 
					new Date(magnetometerReading.getTimestamp()));
	}

	private void saveWifiList(int deviceId, List<WifiReading> wifiList) {
		for(WifiReading w : wifiList) {
			dbInstance.executeQuery(QueryConstants.INSERT_WIFI_READING,
					deviceId, w.getSSID(), w.getFrequency(), w.getLevel(),
					new Date(w.getTimestamp()));
		}
	}

	private void saveLocationCoordinates(int deviceId, LocationCoordinates coordinates) {
		dbInstance.executeQuery(QueryConstants.INSERT_COORDINATES,
				deviceId, coordinates.getLatitude(),
				coordinates.getLongitude(), new Date(coordinates.getTimestamp()),
				coordinates.getAltitude(), coordinates.getSpeed(), coordinates.getAccuracy(), coordinates.getProvider());
	}

	private void saveLocationDetails(int maxId, Location locationDetails, int deviceId) {
		dbInstance.executeQuery(QueryConstants.INSERT_LOCATION, maxId,
				locationDetails.getName(), locationDetails.getBuilding(),
				locationDetails.getFloor(), locationDetails.getRoom(),
				locationDetails.getStreetAddress(), locationDetails.getCity(),
				locationDetails.getZipCode(), deviceId, new Date(locationDetails.getTimestamp()));
	}
}
