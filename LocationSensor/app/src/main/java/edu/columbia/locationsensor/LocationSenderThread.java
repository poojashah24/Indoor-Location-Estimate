package edu.columbia.locationsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Pooja on 5/19/15.
 */
public class LocationSenderThread extends Thread {

    private Context mContext;
    private Location location;
    private DeviceInfo deviceInfo;

    public LocationSenderThread(Context mContext, LocationReading location) {
        this.mContext = mContext;
        this.location = location.getLocation();
        this.deviceInfo = location.getDeviceInfo();
    }

    @Override
    public void run() {
        JSONObject json = getJSONObject();
        Log.i("HTTPAsyncTask", json.toString());
        sendToServer(json.toString());
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

    private JSONObject getJSONObject() {
        JSONObject dataObj = null;
        try {
            dataObj = new JSONObject();
            if (deviceInfo != null) {
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
            if (location != null) {
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
        } catch(JSONException je) {
            je.printStackTrace();
        }
        return dataObj;
    }
}
