package edu.columbia.locationsensor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Pooja on 4/29/15.
 */
public class DataFlusher extends TimerTask {

    private Context mContext;
    private PressureDataSource pressureDataSource;
    private CoordinatesDataSource coordinatesDataSource;
    private WifiDataSource wifiDataSource;
    private MagnetometerDataSource magnetometerDataSource;
    private LocationDataSource locationDataSource;

    private WifiManager wifiManager;

    public DataFlusher(Context mContext) {
        this.mContext = mContext;
        this.pressureDataSource = new PressureDataSource(mContext);
        this.coordinatesDataSource = new CoordinatesDataSource(mContext);
        this.wifiDataSource = new WifiDataSource(mContext);
        this.magnetometerDataSource = new MagnetometerDataSource(mContext);
        this.locationDataSource = new LocationDataSource(mContext);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void run() {

        if (isOnline()) {
            this.pressureDataSource.open();
            sendPressureReadings();
            //this.pressureDataSource.close();

            this.coordinatesDataSource.open();
            sendCoordinateReadings();
            //this.coordinatesDataSource.close();

            this.wifiDataSource.open();
            sendWifiReadings();
            //this.wifiDataSource.close();

            this.magnetometerDataSource.open();
            sendMagnetometerReadings();
            //this.magnetometerDataSource.close();

            this.locationDataSource.open();
            sendLocationReadings();
            //this.locationDataSource.close();
        }
    }

    private void sendLocationReadings() {
        Log.i("DataFlusher", "Sending location readings to the server");
        List<Location> locationReadings = locationDataSource.getAllLocations();
        DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();

        try {
            List<JSONObject> locations = new ArrayList<JSONObject>();
            for (Location location : locationReadings) {
                JSONObject dataObj = new JSONObject();
                JSONObject locationInfoObj = new JSONObject();
                locationInfoObj.put(Constants.NAME, location.getLocationName());
                locationInfoObj.put(Constants.BUILDING, location.getBuilding());
                locationInfoObj.put(Constants.FLOOR, location.getFloor());
                locationInfoObj.put(Constants.ROOM, location.getRoom());
                locationInfoObj.put(Constants.STREET_ADDRESS, location.getStreetAddress());
                locationInfoObj.put(Constants.CITY, location.getCity());
                locationInfoObj.put(Constants.ZIPCODE, location.getZipCode());
                locationInfoObj.put(Constants.TS, location.getRefreshTime());
                dataObj.put(Constants.LOCATIONINFO, locationInfoObj);

                JSONObject deviceInfoObj = new JSONObject();
                deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
                dataObj.put(Constants.DEVICE_INFO, deviceInfoObj);

                locations.add(dataObj);
            }
            LocationReadingsSenderThread senderThread = new LocationReadingsSenderThread(mContext, locations);
            senderThread.start();

            for (Location location : locationReadings) {
                locationDataSource.deleteLocation(location.getLocationName(), location.getRefreshTime());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("DataFlusher", "Done with location readings to the server");


    }

    private void sendPressureReadings() {
        Log.i("DataFlusher", "Sending pressure readings to the server");
        List<PressureReading> readings = pressureDataSource.getAllPressureReadings();
        try {
            JSONArray array = new JSONArray();
            for (PressureReading pressureReading : readings) {
                JSONObject pressureObj = new JSONObject();
                pressureObj.put(Constants.PRESSURE, pressureReading.getPressure());
                pressureObj.put(Constants.TS, pressureReading.getRefreshTime());
                array.put(pressureObj);
            }
            JSONObject pressure = new JSONObject();
            pressure.put(Constants.PRESSURE_LIST, array);

            DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();
            JSONObject deviceInfoObj = new JSONObject();
            if(deviceInfo != null) {
                deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
            }
            pressure.put(Constants.DEVICE_INFO, deviceInfoObj);

            PressureHttpAsyncTask senderTask = new PressureHttpAsyncTask(mContext, pressure);
            senderTask.execute();

            for (PressureReading pressureReading : readings) {
                pressureDataSource.deletePressureReading(pressureReading.getPressure(), pressureReading.getRefreshTime());
            }
            Log.i("DataFlusher", "Done with pressure readings to the server");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendWifiReadings() {
        Log.i("DataFlusher", "Sending location readings to the server");
        List<WifiNetwork> readings = wifiDataSource.getAllWifiReadings();
        try {
            JSONArray array = new JSONArray();
            for (WifiNetwork  wifiNetwork : readings) {
                JSONObject wifiObj = new JSONObject();
                wifiObj.put(Constants.SSID, wifiNetwork.getSSID());
                wifiObj.put(Constants.FREQ, wifiNetwork.getFrequency());
                wifiObj.put(Constants.LEVEL, wifiNetwork.getLevel());
                wifiObj.put(Constants.LEVEL_IN_DB, wifiNetwork.getLevelInDb());
                wifiObj.put(Constants.TS, wifiNetwork.getTimeStamp());
                array.put(wifiObj);

                /*if(array.length() > 5)
                    break;*/
            }

            JSONObject coordinatesList = new JSONObject();
            coordinatesList.put(Constants.WIFILIST, array);

            DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();
            JSONObject deviceInfoObj = new JSONObject();
            if(deviceInfo != null) {
                deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
            }
            coordinatesList.put(Constants.DEVICE_INFO, deviceInfoObj);

            WifiHttpAsyncTask senderTask = new WifiHttpAsyncTask(mContext, coordinatesList);
            senderTask.execute();

            wifiDataSource.deleteWifiReading(null, 0);
            /*for (WifiNetwork  wifiNetwork : readings) {
                wifiDataSource.deleteWifiReading(wifiNetwork.getSSID(), wifiNetwork.getTimeStamp());
            }*/
            Log.i("DataFlusher", "Done with wifi readings to the server");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMagnetometerReadings() {
        Log.i("DataFlusher", "Sending magnetometer readings to the server");
        List<MagnetometerReading> readings = magnetometerDataSource.getAllMagnetometerReadings();
        try {
            JSONArray array = new JSONArray();
            for (MagnetometerReading magnetometerReading : readings) {
                JSONObject magnetometerObj = new JSONObject();
                magnetometerObj.put(Constants.MAGNETOMETER_X, magnetometerReading.getX());
                magnetometerObj.put(Constants.MAGNETOMETER_Y, magnetometerReading.getY());
                magnetometerObj.put(Constants.MAGNETOMETER_Z, magnetometerReading.getZ());
                magnetometerObj.put(Constants.TS, magnetometerReading.getRefreshTime());
                array.put(magnetometerObj);
            }
            JSONObject magnetometerReading = new JSONObject();
            magnetometerReading.put(Constants.MAGNETOMETER_LIST, array);

            DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();
            JSONObject deviceInfoObj = new JSONObject();
            if(deviceInfo != null) {
                deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
            }
            magnetometerReading.put(Constants.DEVICE_INFO, deviceInfoObj);

            MagnetometerSenderThread senderTask = new MagnetometerSenderThread(mContext, magnetometerReading.toString());
            senderTask.start();

            magnetometerDataSource.deleteMagnetometerReading();

            /*for (MagnetometerReading magnetometerReading : readings) {
                magnetometerDataSource.deleteMagnetometerReading(magnetometerReading.getX(), magnetometerReading.getY(), magnetometerReading.getZ(), magnetometerReading.getRefreshTime());
            }*/
            Log.i("DataFlusher", "Done with magnetometer readings to the server");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendCoordinateReadings() {
        Log.i("DataFlusher", "Sending coordinate readings to the server");
        List<LocationCoordinates> readings = coordinatesDataSource.getAllLocationCoordinates();
        if (readings != null && readings.size() > 0) {
            try {
                JSONArray array = new JSONArray();
                for (LocationCoordinates coordinates : readings) {
                    JSONObject locationObj = new JSONObject();
                    locationObj.put(Constants.LATITUDE, coordinates.getLatitude());
                    locationObj.put(Constants.LONGITUDE, coordinates.getLongitude());
                    locationObj.put(Constants.ACCURACY, coordinates.getAccuracy());
                    locationObj.put(Constants.ALTITUDE, coordinates.getAltitude());
                    locationObj.put(Constants.SPEED, coordinates.getSpeed());
                    locationObj.put(Constants.PROVIDER, coordinates.getProvider());
                    locationObj.put(Constants.TS, coordinates.getRefreshTime());
                    array.put(locationObj);
                }
                JSONObject coordinatesList = new JSONObject();
                coordinatesList.put(Constants.COORDINATES_LIST, array);

                DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();
                JSONObject deviceInfoObj = new JSONObject();
                if(deviceInfo != null) {
                    deviceInfoObj.put(Constants.OS_VERSION, deviceInfo.getOsVersion());
                    deviceInfoObj.put(Constants.BUILD_VERSION, deviceInfo.getBuildVersion());
                    deviceInfoObj.put(Constants.BUILD_VERSION_SDK, deviceInfo.getBuildVersion());
                    deviceInfoObj.put(Constants.DEVICE, deviceInfo.getDevice());
                    deviceInfoObj.put(Constants.MODEL, deviceInfo.getModel());
                    deviceInfoObj.put(Constants.PRODUCT, deviceInfo.getProduct());
                    deviceInfoObj.put(Constants.TS, deviceInfo.getRefreshTime());
                }
                coordinatesList.put(Constants.DEVICE_INFO, deviceInfoObj);

                CoordinatesHttpAsyncTask senderTask = new CoordinatesHttpAsyncTask(mContext, coordinatesList);
                senderTask.execute();

                for (LocationCoordinates coordinates : readings) {
                    coordinatesDataSource.deleteLocationReading(coordinates.getLatitude(), coordinates.getLongitude());
                }
                Log.i("DataFlusher", "Done with coordinate readings to the server");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
