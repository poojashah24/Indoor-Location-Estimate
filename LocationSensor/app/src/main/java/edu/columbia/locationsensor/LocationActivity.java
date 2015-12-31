package edu.columbia.locationsensor;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User interface for the location sensor.
 * This displays the user's current location using a marker on a map.
 */
public class LocationActivity extends ActionBarActivity
                    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                        OnMapReadyCallback, LocationListener{

    GoogleApiClient gLocationClient;
    Location location;
    LocationCoordinates coordinates;

    private GoogleMap googleMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "No location services available", Toast.LENGTH_SHORT).show();
        }
        gLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setIndoorEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(coordinates == null) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(gLocationClient);
            updateLocation(location);
        }
        if(coordinates != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng position = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
            markerOptions.position(position);
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateLocation(Location location) {
        if(location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coordinates = new LocationCoordinates(latitude,
                    longitude,
                    location.getAltitude(),
                    location.getAccuracy(),
                    location.getSpeed(),
                    location.getProvider()
            );
            showLocation(latitude, longitude);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        request.setFastestInterval(10 * 1000);
        request.setInterval(15 * 1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(gLocationClient, request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    private void showLocation(double latitude, double longitude) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng position = new LatLng(latitude, longitude);
        markerOptions.position(position);
        markerOptions.title("You are here ("+latitude+","+longitude+")");


        if(googleMap != null) {
            if(marker != null) {
                marker.remove();
            }
            googleMap.setBuildingsEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();

            IndoorBuilding building = googleMap.getFocusedBuilding();
            if (building != null) {
                boolean underground = building.isUnderground();
                List<IndoorLevel> levels = building.getLevels();
                if (levels != null) {
                    int activeLevel = building.getActiveLevelIndex();
                    IndoorLevel level = levels.get(activeLevel);
                    if (level != null) {
                        String name = level.getName();
                        String shortName = level.getShortName();
                    }
                }
            }
        }

        String res = "Latitude: " + latitude + "\nLongitude: " + longitude;
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        String msg = (cause == 1) ? "Service Disconnected" : "Network Lost";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public LocationCoordinates getLatestCoordinates() {
        return coordinates;
    }
}
