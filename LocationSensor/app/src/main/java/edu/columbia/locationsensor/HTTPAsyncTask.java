package edu.columbia.locationsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Pooja on 2/22/15.
 */
public class HTTPAsyncTask extends AsyncTask<Void, Void, Integer> {

    private Context mContext;
    private Location location;
    private DeviceInfo deviceInfo;
    private List<LocationReading> locationReadings;

    private LocationCoordinates locationCoordinates;
    private PressureReading pressureReading;
    private WifiReading wifiReading;
    private MagnetometerReading magnetometerReading;


    public HTTPAsyncTask() {}

    public HTTPAsyncTask(Context mContext) {
        this.mContext = mContext;
    }

    public HTTPAsyncTask(Context mContext, Location location, LocationCoordinates locationCoordinates, PressureReading pressureReading,
                         WifiReading wifiReading, MagnetometerReading magnetometerReading, DeviceInfo deviceInfo) {
        this.mContext = mContext;
        this.location = location;
        this.locationCoordinates = locationCoordinates;
        this.pressureReading = pressureReading;
        this.wifiReading = wifiReading;
        this.magnetometerReading = magnetometerReading;
        this.deviceInfo = deviceInfo;
    }

    public HTTPAsyncTask(Context mContext, List<LocationReading> locationReadings) {
        this.mContext = mContext;
        this.location = locationReadings.get(0).getLocation();
        this.deviceInfo = locationReadings.get(0).getDeviceInfo();
        this.locationReadings = locationReadings;
    }

    public HTTPAsyncTask(Context mContext, LocationReading location) {
        this.mContext = mContext;
        this.location = location.getLocation();
        this.deviceInfo = location.getDeviceInfo();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        JSONObject json = getJSONObject();
        Log.i("HTTPAsyncTask", json.toString());
        sendToServer(json.toString());
        return 200;
    }

    protected int sendToServer(String jsonString) {
        HttpClient httpClient = new DefaultHttpClient();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String serverURL = sharedPreferences.getString("AppPreferences",
                                    Constants.DEFAULT_SERVER_URL);

        //HttpPost postMethod = new HttpPost(mContext.getString(R.string.server_url));
        HttpPost postMethod = new HttpPost(serverURL);

        try {
            StringEntity se = new StringEntity(jsonString);
            postMethod.setEntity(se);
            postMethod.setHeader("Accept","application/json");
            postMethod.setHeader("Content-type", "application/json");

                HttpResponse response = httpClient.execute(postMethod);

            return response.getStatusLine().getStatusCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        if(statusCode != 200) {
            Toast.makeText(mContext, mContext.getString(R.string.send_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getJSONObject() {
        JSONObject dataObj = null;
        try {
            dataObj = new JSONObject();
            if(deviceInfo != null) {
                JSONObject deviceInfoObj = new JSONObject();
                deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
                dataObj.put(Constants.DEVICE_INFO, deviceInfoObj);
            }
            if(location != null) {
                JSONObject locationInfoObj = new JSONObject();
                locationInfoObj.put(Constants.NAME, location.getLocationName());
                locationInfoObj.put(Constants.BUILDING, location.getBuilding());
                locationInfoObj.put(Constants.FLOOR, location.getFloor());
                locationInfoObj.put(Constants.ROOM, location.getFloor());
                locationInfoObj.put(Constants.STREET_ADDRESS, location.getStreetAddress());
                locationInfoObj.put(Constants.CITY, location.getCity());
                locationInfoObj.put(Constants.ZIPCODE, location.getZipCode());
                locationInfoObj.put(Constants.TS, location.getRefreshTime());
                dataObj.put(Constants.LOCATIONINFO, locationInfoObj);
            }

            /*if(locationReadings != null) {
                JSONArray pressureArray = new JSONArray();
                JSONArray coordinatesArray = new JSONArray();
                JSONArray wifiArray = new JSONArray();
                JSONArray magnetometerArray = new JSONArray();

                for(LocationReading reading : locationReadings) {
                    PressureReading pressureReading = reading.getPressureReading();
                    if(pressureReading != null) {
                        JSONObject pressureObj = new JSONObject();
                        pressureObj.put(Constants.PRESSURE, pressureReading.getPressure());
                        pressureObj.put(Constants.TS, pressureReading.getRefreshTime());
                        //dataObj.put(Constants.PRESSURE_READING, pressureObj);
                        pressureArray.put(pressureObj);
                    }
                    //dataObj.put(Constants.PRESSURE_LIST, pressureArray);

                    MagnetometerReading magnetometerReading = reading.getMagnetometerReading();
                    if(magnetometerReading != null) {
                        JSONObject magnetometerObj = new JSONObject();
                        magnetometerObj.put(Constants.MAGNETOMETER_X, magnetometerReading.getX());
                        magnetometerObj.put(Constants.MAGNETOMETER_Y, magnetometerReading.getY());
                        magnetometerObj.put(Constants.MAGNETOMETER_Z, magnetometerReading.getZ());
                        magnetometerObj.put(Constants.TS, magnetometerReading.getRefreshTime());

                        magnetometerArray.put(magnetometerObj);
                    }

                    LocationCoordinates locationCoordinates = reading.getCoordinates();
                    if(locationCoordinates != null) {
                        JSONObject coordinatesObj = new JSONObject();
                        coordinatesObj.put(Constants.LATITUDE, locationCoordinates.getLatitude());
                        coordinatesObj.put(Constants.LONGITUDE, locationCoordinates.getLongitude());
                        coordinatesObj.put(Constants.TS, locationCoordinates.getRefreshTime());
                        coordinatesObj.put(Constants.ACCURACY, locationCoordinates.getAccuracy());
                        coordinatesObj.put(Constants.ALTITUDE, locationCoordinates.getAltitude());
                        coordinatesObj.put(Constants.SPEED, locationCoordinates.getSpeed());
                        coordinatesObj.put(Constants.PROVIDER, locationCoordinates.getProvider());

                        coordinatesArray.put(coordinatesObj);
                        //dataObj.put(Constants.COORDINATES, coordinatesObj);
                    }

                    WifiReading wifiReading = reading.getWifiReading();
                    if(wifiReading != null) {
                        JSONArray wifiJSONArray = new JSONArray();
                        for(WifiNetwork network : wifiReading.getWifiNetworks()) {
                            JSONObject networkObj = new JSONObject();
                            networkObj.put(Constants.SSID, network.getSSID());
                            networkObj.put(Constants.FREQ, network.getFrequency());
                            networkObj.put(Constants.LEVEL, network.getLevel());
                            networkObj.put(Constants.TS, network.getTimeStamp());

                            wifiJSONArray.put(networkObj);
                        }
                        wifiArray.put(wifiJSONArray);
                        //dataObj.put(Constants.WIFILIST, wifiJSONArray);
                    }
                }
                dataObj.put(Constants.PRESSURE_LIST, pressureArray);
                dataObj.put(Constants.COORDINATES_LIST, coordinatesArray);
                dataObj.put(Constants.WIFI_LIST, wifiArray);
                dataObj.put(Constants.MAGNETOMETER_LIST, magnetometerArray);
            }*/
        } catch(JSONException je) {
            je.printStackTrace();
        }
       return dataObj;
    }
}
