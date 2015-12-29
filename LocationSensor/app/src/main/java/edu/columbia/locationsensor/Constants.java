package edu.columbia.locationsensor;

/**
 * Created by Pooja on 2/22/15.
 */
public final class Constants {
    public static final String PRESSURE_EXCEPTION = "No pressure sensor available!";
    public static final String SERVER_URL = "http://foo.bar";

    public static final String OPENING_BRACE = "{";
    public static final String CLOSING_BRACE = "}";
    public static final String PRESSURE_JSON = "\"pressure\":\"{0}\"";
    public static final String COORDINATES_JSON = "\"coordinates\":\"{0}\" {";
    public static final String LATITUDE_JSON = "\"latitude\":{0},";
    public static final String LONGITUDE_JSON = "\"longitude\":{0}}";
    public static final String TS_JSON = "\"timestamp\":\"{0}\"";
    public static final String WIFI_JSON = "\"wifilist\":[{0},{1}]";
    public static final String ACCESS_POINT_JSON = "{\"accesspoint\": {}";
    public static final String SSID_JSON = "\"ssid\":\"{0}\"";
    public static final String FREQ_JSON = "\"frequency\":\"{0}\"";
    public static final String LEVEL_JSON = "\"level\":\"{0}\"";


    public static final String PRESSURE_READING = "pressure_reading";
    public static final String PRESSURE = "pressure";

    public static final String MAGNETOMETER_X = "x";
    public static final String MAGNETOMETER_Y = "y";
    public static final String MAGNETOMETER_Z = "z";

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SPEED = "speed";
    public static final String ACCURACY = "accuracy";
    public static final String ALTITUDE = "altitude";
    public static final String PROVIDER = "provider";
    public static final String COORDINATES = "coordinates";

    public static final String WIFILIST = "wifilist";
    public static final String AP = "accesspoint";
    public static final String SSID = "ssid";
    public static final String FREQ = "frequency";
    public static final String LEVEL = "level";
    public static final String LEVEL_IN_DB = "levelInDb";
    public static final String TS = "timestamp";

    public static final String NAME = "location";
    public static final String BUILDING = "building";
    public static final String FLOOR = "floor";
    public static final String ROOM = "room";
    public static final String LOCATIONINFO = "locationinfo";
    public static final String STREET_ADDRESS = "street_address";
    public static final String CITY = "city";
    public static final String ZIPCODE = "zipcode";

    public static final String DEVICE_INFO = "device_info";
    public static final String OS_VERSION = "os_version";
    public static final String BUILD_VERSION = "build_version";
    public static final String BUILD_VERSION_SDK = "build_version_sdk";
    public static final String DEVICE = "device";
    public static final String MODEL = "model";
    public static final String PRODUCT = "product";

    public static final String PRESSURE_LIST = "pressure_list";
    public static final String COORDINATES_LIST = "coordinates_list";
    public static final String WIFI_LIST = "wifi_list";
    public static final String MAGNETOMETER_LIST = "magnetometer_list";

    public static final String WEATHER = "weather";
    public static final String MAIN = "main";


    public static final String REVERSE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String DEFAULT_SERVER_URL = "http://locationreceiverenv-w2g8sk2zuh.elasticbeanstalk.com/LocationUpdateServlet";
    public static final String COMMA = ",";

}
