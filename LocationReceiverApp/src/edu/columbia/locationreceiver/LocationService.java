package edu.columbia.locationreceiver;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parses JSON readings sent by the client application, and sends them to the DAO layer for persistence.
 * @author Pooja
 *
 */
public class LocationService {

	private static final LocationService INSTANCE = new LocationService();
	private static final LocationDAO locationDAO = new LocationDAO();

	public static final LocationService getInstance() {
		return INSTANCE;
	}

	public void saveWeatherStationReading(StationReading reading) {
		locationDAO.saveReadings(reading);
	}

	public void saveElevationReadings(ElevationReading reading) {
		locationDAO.saveElevationReading(reading);
	}

	public void saveLocationDetails(JSONObject locationObj) {

		DeviceInfo deviceInfo = null;
		List<PressureReading> pressureList = new ArrayList<PressureReading>();

		JSONObject deviceObj = locationObj.getJSONObject(Constants.DEVICE_INFO);
		if (deviceObj != null) {
			deviceInfo = new DeviceInfo(
					deviceObj.getString(Constants.OS_VERSION),
					deviceObj.getString(Constants.BUILD_VERSION),
					deviceObj.getInt(Constants.BUILD_VERSION_SDK),
					deviceObj.getString(Constants.DEVICE),
					deviceObj.getString(Constants.MODEL),
					deviceObj.getString(Constants.PRODUCT),
					deviceObj.getLong(Constants.TS));
		}

		if (locationObj.has(Constants.PRESSURE_LIST)) {
			System.out.println("getting the pressure list");
			JSONArray pressureArray = locationObj
					.getJSONArray(Constants.PRESSURE_LIST);
			for (int i = 0; i < pressureArray.length(); i++) {
				PressureReading pressure = null;
				JSONObject pressureObj = pressureArray.getJSONObject(i);
				if (pressureObj != null) {
					pressure = new PressureReading();
					pressure.setPressure(pressureObj
							.getDouble(Constants.PRESSURE));
					pressure.setTimestamp(pressureObj.getLong(Constants.TS));
					pressureList.add(pressure);
				}
			}
			locationDAO.savePressureReading(pressureList, deviceInfo);
		} else if (locationObj.has(Constants.WIFILIST)) {
			System.out.println("getting the wifi list");
			List<List<WifiReading>> wifiReadingsList = new ArrayList<List<WifiReading>>();
			JSONArray wifiArray = locationObj.getJSONArray(Constants.WIFILIST);
			List<WifiReading> wifiList = new ArrayList<WifiReading>();
			for (int i = 0; i < wifiArray.length(); i++) {
				JSONObject wReading = wifiArray.getJSONObject(i);
				WifiReading reading = new WifiReading();
				reading.setSSID(wReading.getString(Constants.SSID));
				reading.setFrequency(wReading.getDouble(Constants.FREQUENCY));
				reading.setLevel(wReading.getInt(Constants.LEVEL));
				reading.setTimestamp(wReading.getLong(Constants.TIMESTAMP));
				wifiList.add(reading);
			}
			wifiReadingsList.add(wifiList);
			locationDAO.saveWifiLists(wifiReadingsList, deviceInfo);
		} else if (locationObj.has(Constants.COORDINATES_LIST)) {
			System.out.println("getting the coordinates list");
			List<LocationCoordinates> coordinatesList = new ArrayList<LocationCoordinates>();
			JSONArray coordinatesArray = locationObj
					.getJSONArray(Constants.COORDINATES_LIST);
			for (int i = 0; i < coordinatesArray.length(); i++) {
				LocationCoordinates coordinates = null;
				JSONObject coordinatesObj = coordinatesArray.getJSONObject(i);
				if (coordinatesObj != null) {
					coordinates = new LocationCoordinates();
					coordinates.setLatitude(coordinatesObj
							.getDouble(Constants.LATITUDE));
					coordinates.setLongitude(coordinatesObj
							.getDouble(Constants.LONGITUDE));
					coordinates.setTimestamp(coordinatesObj
							.getLong(Constants.TS));
					coordinates.setAccuracy(new Float(coordinatesObj
							.getDouble(Constants.ACCURACY)));
					coordinates.setAltitude(coordinatesObj
							.getDouble(Constants.ALTITUDE));
					coordinates.setSpeed(new Float(coordinatesObj
							.getDouble(Constants.SPEED)));
					coordinates.setProvider(coordinatesObj
							.getString(Constants.PROVIDER));
					coordinatesList.add(coordinates);
					Thread readingsThread = new FetchReadingsTask(this,
							coordinates.getLatitude(),
							coordinates.getLongitude(),
							coordinates.getAltitude(),
							coordinates.getTimestamp());
					readingsThread.start();
				}
			}
			locationDAO.saveCoordinatesList(coordinatesList, deviceInfo);

		} else if (locationObj.has(Constants.MAGNETOMETER_LIST)) {
			System.out.println("getting the magnetometer list");
			List<MagnetometerReading> magnetometerList = new ArrayList<MagnetometerReading>();
			JSONArray magnetometerArray = locationObj
					.getJSONArray(Constants.MAGNETOMETER_LIST);
			for (int i = 0; i < magnetometerArray.length(); i++) {
				MagnetometerReading magnetometerReading = null;
				JSONObject magnetometerObj = magnetometerArray.getJSONObject(i);
				if (magnetometerObj != null) {
					magnetometerReading = new MagnetometerReading();
					magnetometerReading.setX(magnetometerObj
							.getDouble(Constants.MAGNETOMETER_X));
					magnetometerReading.setY(magnetometerObj
							.getDouble(Constants.MAGNETOMETER_Y));
					magnetometerReading.setZ(magnetometerObj
							.getDouble(Constants.MAGNETOMETER_Z));
					magnetometerReading.setTimestamp(magnetometerObj
							.getLong(Constants.TS));

					magnetometerList.add(magnetometerReading);
				}
			}
			locationDAO.saveMagnetometerReadings(magnetometerList, deviceInfo);
		} else if (locationObj.has(Constants.LOCATIONINFO)) {
			System.out.println("in save locationinfo");
			Location locationDetails = new Location();
			JSONObject locationDetailsObj = locationObj
					.getJSONObject(Constants.LOCATIONINFO);
			locationDetails.setName(locationDetailsObj
					.getString(Constants.NAME));
			locationDetails.setBuilding(locationDetailsObj
					.getString(Constants.BUILDING));
			locationDetails.setFloor(locationDetailsObj
					.getString(Constants.FLOOR));
			locationDetails.setRoom(locationDetailsObj
					.getString(Constants.ROOM));
			locationDetails.setStreetAddress(locationDetailsObj
					.getString(Constants.STREET_ADDRESS));
			locationDetails.setCity(locationDetailsObj
					.getString(Constants.CITY));
			locationDetails.setZipCode(locationDetailsObj
					.getString(Constants.ZIPCODE));
			locationDetails.setTimestamp(locationDetailsObj
					.getLong(Constants.TIMESTAMP));
			locationDAO.saveLocation(locationDetails, deviceInfo);
		}
	}
}
