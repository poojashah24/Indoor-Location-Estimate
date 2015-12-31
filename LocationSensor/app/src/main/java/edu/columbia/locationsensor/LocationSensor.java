package edu.columbia.locationsensor;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Callback client for the location API.
 */
public class LocationSensor implements Runnable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient gLocationClient;
    LocationCoordinates coordinates;
    String exception;


    @Override
    public void run() {
        gLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(gLocationClient);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String res = "Latitude: " + latitude + "\nLongitude: " + longitude;
        exception = null;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        exception = (cause == 1) ? "Service Disconnected" : "Network Lost";
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        exception = result.toString();
    }

    public LocationCoordinates getLatestCoordinates() {
        return coordinates;
    }
}
