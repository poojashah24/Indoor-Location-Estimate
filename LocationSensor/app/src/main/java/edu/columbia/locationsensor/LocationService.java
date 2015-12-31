package edu.columbia.locationsensor;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage sensor updates for location coordinates.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    GoogleApiClient gLocationClient;
    String exception;

    private static CoordinatesDataSource dataSource;
    private static List<LocationCoordinates> readingList = new ArrayList<LocationCoordinates>();


    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();
        double altitude = location.getAltitude();
        float speed = location.getSpeed();
        String provider = location.getProvider();

        LocationCoordinates coordinates = new LocationCoordinates(latitude, longitude,
                altitude, accuracy, speed, provider);
        DataHolder.getInstance().setLocationCoordinates(coordinates);
        String res = "Latitude: " + latitude + "\nLongitude: " + longitude;
        exception = null;
        readingList.add(coordinates);
        dataSource.open();
        dataSource.insertLocationReading(coordinates);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocationService", "onStartCommand");
        Log.i("LocationService", String.valueOf(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)));
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "No location services available", Toast.LENGTH_SHORT).show();
        }
        gLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        gLocationClient.connect();
        dataSource = new CoordinatesDataSource(this);

       return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        return super.onUnbind(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
       Log.i("LocationService","onConnected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(gLocationClient);
        onLocationChanged(location);

        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        request.setFastestInterval(30 * 1000);
        request.setInterval(120 * 1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(gLocationClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        exception = (cause == 1) ? "Service Disconnected" : "Network Lost";
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        exception = result.toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(gLocationClient, this);
    }
}
