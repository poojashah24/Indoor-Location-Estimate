package edu.columbia.locationreceiver;

/**
 * Queries used to insert and query location details and sensor readings.
 * @author Pooja
 *
 */
public class QueryConstants {
	public static final String GET_MAX_ID = "select max(location_id) as max_id from location;";
	
	public static final String INSERT_LOCATION = "insert into location(location_id, name, building, floor, room, street, city, zip, device_id, updated) "
			+ "values (?,?,?,?,?,?,?,?,?,?);";
	
	public static final String INSERT_PRESSURE_READING = "insert into pressure(device_id, pressure_reading, updated) values (?,?,?);";
	
	public static final String INSERT_MAGNETOMETER_READING = "insert into magnetometer_reading(device_id, x, y, z, updated) values (?,?,?,?,?);";
	
	public static final String INSERT_COORDINATES = "insert into coordinates(device_id, latitude, longitude, updated, altitude, speed, accuracy, provider) "
			+ "values (?,?,?,?,?,?,?,?);";
	
	public static final String INSERT_WIFI_READING = "insert into wifi_reading(device_id, ssid, frequency, level, updated) values (?,?,?,?,?);";
	
	public static final String INSERT_DEVICE_INFO = "insert into device_info(os_version,build_version,build_version_sdk,device,model,product,updated) "
			+ "values (?,?,?,?,?,?,?);";
	
	public static final String QUERY_LOCATION = "select location_id as loc_id from location where name=? and building=? and floor=? and room=?;";
	
	public static final String QUERY_DEVICE = "select device_id as device_id from device_info where device = ? and product = ?";
	
	public static final String UPDATE_PRESSURE = "update pressure set pressure_reading=?, updated=? where location_id=?";
	
	public static final String UPDATE_COORDINATES = "update coordinates set latitude=?, longitude=?, updated=? where location_id=?";
	
	public static final String DELETE_WIFI = "delete from wifi_reading where location_id=?";
	
	public static final String INSERT_READINGS = "insert into compensated_measurements"
			+ "(measurement_ts, latitude, longitude, weather, weather_desc, temperature, pressure, name, humidity) "
			+ "values (?,?,?,?,?,?,?,?,?);";
	
	public static final String INSERT_PRESSURE_COMP_READINGS = "insert into comp_pressure_readings "
			+ "(altitude, nws_pressure, comp_pressure, updated) values (?,?,?,?,?);";
}