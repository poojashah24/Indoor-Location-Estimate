package edu.columbia.locationreceiver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 
 * This class is used as the access layer to retrieve and save data into the database.
 * @author Pooja
 *
 */
public class LocationDAO {
	private static final DBConnector dbInstance = DBConnector.getInstance();

	/**
	 * Persists a list of pressure readings to the database.
	 * @author Pooja @param pressureReadings
	 * @author Pooja @param deviceInfo
	 */
	public void savePressureReading(List<PressureReading> pressureReadings,
			DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for (PressureReading pressure : pressureReadings) {
			savePressureReading(deviceId, pressure);
		}
	}

	/**
	 * Persists a list of coordinates to the database.
	 * @author Pooja @param coordinates
	 * @author Pooja @param deviceInfo
	 */
	public void saveCoordinatesList(List<LocationCoordinates> coordinates,
			DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for (LocationCoordinates c : coordinates) {
			saveLocationCoordinates(deviceId, c);
		}
	}

	/**
	 * Persists a list of wifi access point details to the database.
	 * @author Pooja @param wifiReadings
	 * @author Pooja @param deviceInfo
	 */
	public void saveWifiLists(List<List<WifiReading>> wifiReadings,
			DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for (List<WifiReading> wifiReading : wifiReadings) {
			saveWifiList(deviceId, wifiReading);
		}
	}

	/**
	 * Persists a list of magnetometer sensor readings to the database.
	 * @author Pooja @param magnetometerReadings
	 * @author Pooja @param deviceInfo
	 */
	public void saveMagnetometerReadings(
			List<MagnetometerReading> magnetometerReadings,
			DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		for (MagnetometerReading magnetometerReading : magnetometerReadings) {
			saveMagnetometerReading(deviceId, magnetometerReading);
		}
	}

	/**
	 * Persists location details including the street address and building details, to the database.
	 * @author Pooja @param location
	 * @author Pooja @param deviceInfo
	 */
	public void saveLocation(Location location, DeviceInfo deviceInfo) {
		int deviceId = checkIfExists(deviceInfo);
		if (deviceId == -1) {
			deviceId = saveDeviceInfo(deviceInfo);
		}
		int maxid = getMaxLocationID();
		saveLocationDetails(maxid, location, deviceId);
	}
	
	/**
	 * Persists pressure readings and other weather details for a weather station to the database.
	 * @author Pooja @param compReading
	 */
	public void saveReadings(StationReading stationReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_READINGS, new Date(),
				stationReading.getLat(), stationReading.getLon(),
				stationReading.getWeather(), stationReading.getWeatherDesc(),
				stationReading.getTemp(), stationReading.getPressure(),
				stationReading.getName(), stationReading.getHumidity());
	}

	// insert into comp_pressure_readings (altitude, nws_pressure,
	// comp_pressure, updated) values (?,?,?,?,?);
	public void saveElevationReading(ElevationReading elevationReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_COMP_READINGS,
				elevationReading.getAltitude(), elevationReading
						.getStationReading(), elevationReading
						.getElevationReading(),
				new Date(elevationReading.getTs()));
	}

	// public static final String INSERT_PRESSURE_COMP_READINGS =
	// "insert into comp_pressure_readings "
	// + "(pressure, nws_pressure, comp_pressure) values (?,?,?);";
	public void saveAllPressureReadings(double altitude, double pressure,
			double nws_pressure, double comp_pressure) {
		dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_COMP_READINGS,
				altitude, pressure, nws_pressure, comp_pressure, new Date());
	}

	/**
	 * Check if a device's information is already present in the database.
	 * @author Pooja @param device
	 * @author Pooja @return
	 */
	private int checkIfExists(DeviceInfo device) {
		int id = -1;
		ResultSet rs = dbInstance.executeQuery(QueryConstants.QUERY_DEVICE,
				device.getDevice(), device.getProduct());
		try {
			if (rs.next()) {
				id = rs.getInt("device_id");
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
			if (rs.next()) {
				locationID = rs.getInt("max_id") + 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return locationID;
	}

	
	/**
	 * Persists a new device's information to the database.
	 * @author Pooja @param deviceInfo
	 * @author Pooja @return
	 */
	private int saveDeviceInfo(DeviceInfo deviceInfo) {
		try {
			ResultSet rs = dbInstance.executeInsertQuery(
					QueryConstants.INSERT_DEVICE_INFO, deviceInfo
							.getOsVersion(), deviceInfo.getBuildVersion(),
					deviceInfo.getApiLevel(), deviceInfo.getDevice(),
					deviceInfo.getModel(), deviceInfo.getProduct(), new Date(
							deviceInfo.getTimestamp()));
			return rs.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/** 
	 * Persists a single pressure reading, along with the ID of the device that recorded it, to the database.
	 * @author Pooja @param deviceId
	 * @author Pooja @param pressure
	 */
	private void savePressureReading(int deviceId, PressureReading pressure) {
		try {
			dbInstance.executeQuery(QueryConstants.INSERT_PRESSURE_READING,
					deviceId, pressure.getPressure(),
					new Date(pressure.getTimestamp()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Persists a single magnetometer reading, along with the ID of the device that recorded it, to the database.
	 * @author Pooja @param deviceId
	 * @author Pooja @param magnetometerReading
	 */
	private void saveMagnetometerReading(int deviceId,
			MagnetometerReading magnetometerReading) {
		dbInstance.executeQuery(QueryConstants.INSERT_MAGNETOMETER_READING,
				deviceId, magnetometerReading.getX(),
				magnetometerReading.getY(), magnetometerReading.getZ(),
				new Date(magnetometerReading.getTimestamp()));
	}

	/**
	 * Persists a list of wifi access points, along with the ID of the device that recorded the list, to the database.
	 * @author Pooja @param deviceId
	 * @author Pooja @param wifiList
	 */
	private void saveWifiList(int deviceId, List<WifiReading> wifiList) {
		for (WifiReading w : wifiList) {
			dbInstance.executeQuery(QueryConstants.INSERT_WIFI_READING,
					deviceId, w.getSSID(), w.getFrequency(), w.getLevel(),
					new Date(w.getTimestamp()));
		}
	}

	/**
	 * Persists a location's coordinates and altitude details to the database.
	 * 
	 * @author Pooja @param deviceId
	 * @author Pooja @param coordinates
	 */
	private void saveLocationCoordinates(int deviceId,
			LocationCoordinates coordinates) {
		dbInstance.executeQuery(QueryConstants.INSERT_COORDINATES, deviceId,
				coordinates.getLatitude(), coordinates.getLongitude(),
				new Date(coordinates.getTimestamp()),
				coordinates.getAltitude(), coordinates.getSpeed(),
				coordinates.getAccuracy(), coordinates.getProvider());
	}

	/**
	 * Persists a location entry including a street address, building and room
	 * details, along with the ID of the device that recorded it, to the
	 * database.
	 * @author Pooja @param maxId
	 * @author Pooja @param locationDetails
	 * @author Pooja @param deviceId
	 */
	private void saveLocationDetails(int maxId, Location locationDetails,
			int deviceId) {
		dbInstance.executeQuery(QueryConstants.INSERT_LOCATION, maxId,
				locationDetails.getName(), locationDetails.getBuilding(),
				locationDetails.getFloor(), locationDetails.getRoom(),
				locationDetails.getStreetAddress(), locationDetails.getCity(),
				locationDetails.getZipCode(), deviceId, new Date(
						locationDetails.getTimestamp()));
	}
}
