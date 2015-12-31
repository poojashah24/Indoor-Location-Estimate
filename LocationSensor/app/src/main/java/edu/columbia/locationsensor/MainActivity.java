package edu.columbia.locationsensor;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.internal.fr;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    public static final String EXTRA_MESSAGE = "edu.columbia.locationsensor.MESSAGE";
    EditText editText;
    String msg;
    SharedPreferences pref = null;
    private FreqLocationsDataSource freqLocationDataSource;
    private LocationDataSource locationDataSource;
    private ArrayList<String> locationList = null;
    private Notification noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Record device information
        DeviceInfo deviceInfo = new DeviceInfo(System.getProperty("os.version"),
                Build.VERSION.INCREMENTAL,
                Build.VERSION.SDK_INT,
                Build.DEVICE,
                Build.MODEL,
                Build.PRODUCT);

        DataHolder.getInstance().setDeviceInfo(deviceInfo);

        //Create a background service for each sensor.
        Intent pressureService = new Intent(getApplicationContext(), PressureService.class);
        startService(pressureService);

        Intent magnetometerService = new Intent(getApplicationContext(), MagnetometerService.class);
        startService(magnetometerService);

        Intent locationService = new Intent(getApplicationContext(), LocationService.class);
        startService(locationService);

        Intent wifiService = new Intent(getApplicationContext(), WifiService.class);
        startService(wifiService);

        List<Intent> intents = new ArrayList<>();
        intents.add(new Intent(this, PressureActivity.class));
        intents.add(new Intent(this, WifiActivity.class));
        intents.add(new Intent(this, LocationActivity.class));
        intents.add(new Intent(this, MagnetometerActivity.class));

        //Record frequent locations for the home screen widget.
        freqLocationDataSource = new FreqLocationsDataSource(this);
        locationDataSource = new LocationDataSource(this);
        createNotification();

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ButtonAdapter(this, intents));

        //Initialize application main screen
        pref = getSharedPreferences("APP_PREF", MODE_PRIVATE);
        if(pref == null) {
            System.out.print("Pref is null!");
        } else {
            String location = pref.getString("location", "");
            String building = pref.getString("building", "");
            String floor = pref.getString("floor", "");
            String room = pref.getString("room", "");

            editText = (EditText)findViewById(R.id.edit_message);
            editText.setText(location, TextView.BufferType.EDITABLE);

            editText = (EditText)findViewById(R.id.building_name);
            editText.setText(building, TextView.BufferType.EDITABLE);

            editText = (EditText)findViewById(R.id.floor);
            editText.setText(floor, TextView.BufferType.EDITABLE);

            editText = (EditText)findViewById(R.id.room);
            editText.setText(room, TextView.BufferType.EDITABLE);
        }

        //Start a background thread for flushing collected sensor readings to the web server application.
        DataFlusher flusher = new DataFlusher(this);
        Timer dataFlushTimer = new Timer();
        dataFlushTimer.scheduleAtFixedRate(flusher, 0, 1000*60);

        Timer weatherTimer = new Timer();
        weatherTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                noti.contentView.setImageViewResource(R.id.weather, R.drawable.ic_brightness_5_black_48dp);
                LocationCoordinates coordinates = DataHolder.getInstance().getLocationCoordinates();
                if(coordinates != null) {
                    String serverURL = getString(R.string.weather_url) + "lat="+coordinates.getLatitude()+"&lon="+coordinates.getLongitude();
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet getMethod = new HttpGet(serverURL);

                    try {
                        getMethod.setHeader("Accept","application/json");
                        getMethod.setHeader("Content-type", "application/json");

                        HttpResponse response = httpClient.execute(getMethod);

                        HttpEntity result = response.getEntity();
                        StringBuilder buff = new StringBuilder();
                        String res = null;
                        BufferedReader is = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        while((res = is.readLine()) != null) {
                            buff.append(res);
                        }
                        String weatherJSON = buff.toString();
                        JSONObject resObj = new JSONObject(weatherJSON);
                        JSONArray weatherObj = resObj.getJSONArray(Constants.WEATHER);
                        JSONObject mainObj = resObj.getJSONObject(Constants.MAIN);
                        String weather = weatherObj.getJSONObject(0).getString(Constants.MAIN);
                        String icon = weatherObj.getJSONObject(0).getString("icon");
                        String iconURL = getString(R.string.weather_icon_url);

                        int resourceID = getResources().getIdentifier("icon_"+icon, "drawable", getPackageName());
                        if (resourceID != 0) {
                            noti.contentView.setImageViewResource(R.id.weather, resourceID);
                        }

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, noti);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 1000*60, 1000*60*5);

    }

    /**
     * Initializes the location sensor home screen widget.
     */
    public void createNotification() {
        int position = 0;
        locationList = new ArrayList<String>();

        freqLocationDataSource.open();
        List<String> freqLocations = freqLocationDataSource.getAllFrequentLocations();
        for(String location : freqLocations) {
            locationList.add(location);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.mynotificationlayout);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        noti.contentView = notificationView;
        noti.contentIntent = pIntent;
        noti.flags |= Notification.FLAG_NO_CLEAR;

        Intent nextIntent = new Intent(this, NextButtonListener.class);
        nextIntent.putExtra("position",position);
        nextIntent.putStringArrayListExtra("locations", locationList);
        nextIntent.putExtra("notification", noti);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent prevIntent = new Intent(this, PrevButtonListener.class);
        prevIntent.putExtra("position",position);
        prevIntent.putStringArrayListExtra("locations", locationList);
        prevIntent.putExtra("notification", noti);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent selectIntent = new Intent(this, SelectLocationListener.class);
        selectIntent.putExtra("position",position);
        selectIntent.putStringArrayListExtra("locations", locationList);
        selectIntent.putExtra("notification", noti);
        PendingIntent pendingSelectIntent = PendingIntent.getBroadcast(this, 0,
                selectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationView.setTextViewText(R.id.locationName, locationList.get(position));
        notificationView.setOnClickPendingIntent(R.id.prev_button,
                pendingPrevIntent);
        notificationView.setOnClickPendingIntent(R.id.next_button,
                pendingNextIntent);
        notificationView.setOnClickPendingIntent(R.id.locationName,
                pendingSelectIntent);


        notificationManager.notify(0, noti);

    }

    /**
     * Invoked when the next button is pressed on the home screen widget.
     */
    public static class NextButtonListener extends BroadcastReceiver {
        static int position = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am in next");
            ArrayList<String> locationList = intent.getStringArrayListExtra("locations");
            Notification noti = intent.getParcelableExtra("notification");
            int position = intent.getIntExtra("position",-1);
            if (locationList != null) {
                if (position < locationList.size()-1) {
                    position++;
                    noti.contentView.setTextViewText(R.id.locationName, locationList.get(position));
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    Intent nextIntent = new Intent(context, NextButtonListener.class);
                    nextIntent.putExtra("position",position);
                    nextIntent.putStringArrayListExtra("locations", locationList);
                    nextIntent.putExtra("notification", noti);
                    PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, 0,
                            nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.next_button,
                            pendingNextIntent);

                    Intent prevIntent = new Intent(context, PrevButtonListener.class);
                    prevIntent.putExtra("position",position);
                    prevIntent.putStringArrayListExtra("locations", locationList);
                    prevIntent.putExtra("notification", noti);
                    PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context, 0,
                            prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.prev_button,
                            pendingPrevIntent);

                    Intent selectIntent = new Intent(context, SelectLocationListener.class);
                    selectIntent.putExtra("position",position);
                    selectIntent.putStringArrayListExtra("locations", locationList);
                    selectIntent.putExtra("notification", noti);
                    PendingIntent pendingSelectIntent = PendingIntent.getBroadcast(context, 0,
                            selectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.locationName,
                            pendingSelectIntent);

                    notificationManager.notify(0,noti);
                }
            }
        }
    }

    /**
     * Invoked when the previous button is pressed on the home screen widget.
     */
    public static class PrevButtonListener extends BroadcastReceiver {
        static int position = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            ArrayList<String> locationList = intent.getStringArrayListExtra("locations");
            Notification noti = intent.getParcelableExtra("notification");
            if (locationList != null) {
                if (position > 0) {
                    position--;
                    noti.contentView.setTextViewText(R.id.locationName, locationList.get(position));
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);


                    Intent prevIntent = new Intent(context, PrevButtonListener.class);
                    prevIntent.putExtra("position",position);
                    prevIntent.putStringArrayListExtra("locations", locationList);
                    prevIntent.putExtra("notification", noti);
                    PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(context, 0,
                            prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.prev_button,
                            pendingPrevIntent);


                    Intent nextIntent = new Intent(context, NextButtonListener.class);
                    nextIntent.putExtra("position",position);
                    nextIntent.putStringArrayListExtra("locations", locationList);
                    nextIntent.putExtra("notification", noti);
                    PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, 0,
                            nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.next_button,
                            pendingNextIntent);

                    Intent selectIntent = new Intent(context, SelectLocationListener.class);
                    selectIntent.putExtra("position",position);
                    selectIntent.putStringArrayListExtra("locations", locationList);
                    selectIntent.putExtra("notification", noti);
                    PendingIntent pendingSelectIntent = PendingIntent.getBroadcast(context, 0,
                            selectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    noti.contentView.setOnClickPendingIntent(R.id.locationName,
                            pendingSelectIntent);


                    notificationManager.notify(0,noti);
                }
            }
        }
    }

    /**
     * Invoked when a location is selected on the home screen widget.
     * This listener stores the selected location to the in-memory database.
     */
    public static class SelectLocationListener extends BroadcastReceiver {
        static int position = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",-1);
            ArrayList<String> locationList = intent.getStringArrayListExtra("locations");
            Notification noti = intent.getParcelableExtra("notification");
            if (locationList != null) {
                if (position > -1) {
                    String location = locationList.get(position);
                    LocationUpdateSender sender = new LocationUpdateSender(location, context);
                    sender.start();
                    locationList.remove(position);
                    locationList.add(0, location);
                }
            }
        }
    }

    /**
     * Reloads the list of frequently accessed locations in the home screen widgets.
     */
    private void reloadFrequentLocations(){
        freqLocationDataSource.open();
        List<String> freqLocations = freqLocationDataSource.getAllFrequentLocations();
        if (freqLocations != null) {
            locationList.clear();
            for(String location : freqLocations) {
                locationList.add(location);
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent nextIntent = new Intent(this, NextButtonListener.class);
            nextIntent.putExtra("position",0);
            nextIntent.putStringArrayListExtra("locations", locationList);
            nextIntent.putExtra("notification", noti);
            PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0,
                    nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.contentView.setOnClickPendingIntent(R.id.next_button,
                    pendingNextIntent);

            Intent prevIntent = new Intent(this, PrevButtonListener.class);
            prevIntent.putExtra("position",0);
            prevIntent.putStringArrayListExtra("locations", locationList);
            prevIntent.putExtra("notification", noti);
            PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 0,
                    prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.contentView.setOnClickPendingIntent(R.id.prev_button,
                    pendingPrevIntent);

            Intent selectIntent = new Intent(this, SelectLocationListener.class);
            selectIntent.putExtra("position",0);
            selectIntent.putStringArrayListExtra("locations", locationList);
            selectIntent.putExtra("notification", noti);
            PendingIntent pendingSelectIntent = PendingIntent.getBroadcast(this, 0,
                    selectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            noti.contentView.setOnClickPendingIntent(R.id.locationName,
                    pendingSelectIntent);

            notificationManager.notify(0,noti);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_search:
                openSearch();
                return true;

            case R.id.action_settings:
                openSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void openSearch() {
        startActivity(new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH));
        Toast.makeText(this, "Search Button Pressed", Toast.LENGTH_SHORT).show();
    }

    public void openSettings() {
        startActivity(new Intent(this, AppPreferenceActivity.class));
    }


    public void testDatabase(View view) {
        Intent intent = new Intent(this, TestDBActivity.class);
        startActivity(intent);
    }

    public void startEstimote(View view) {
        Intent intent = new Intent(this, EstimoteClient.class);
        startActivity(intent);

    }

    /**
     * Records a new location entry.
     * @param view
     */
    public void sendToServer(View view) {

        PressureReading pressureReading = DataHolder.getInstance().getPressureReading();
        LocationCoordinates locationCoordinates = DataHolder.getInstance().getLocationCoordinates();
        WifiReading  wifiReading = DataHolder.getInstance().getWifiReading();
        DeviceInfo deviceInfo = DataHolder.getInstance().getDeviceInfo();

        editText = (EditText)findViewById(R.id.edit_message);
        String location = editText.getText().toString();
        editText = (EditText)findViewById(R.id.building_name);
        String building = editText.getText().toString();
        editText = (EditText)findViewById(R.id.floor);
        String floor = editText.getText().toString();
        editText = (EditText)findViewById(R.id.room);
        String room = editText.getText().toString();
        editText = (EditText)findViewById(R.id.street);
        String street = editText.getText().toString();
        editText = (EditText)findViewById(R.id.city);
        String city = editText.getText().toString();
        editText = (EditText)findViewById(R.id.zipcode);
        String zipCode = editText.getText().toString();

        String locationID = building + " " + floor + " " + room;
        if(!locationList.contains(locationID)) {
            freqLocationDataSource.open();
            freqLocationDataSource.insertLocation(locationID);
        }

        if(pref == null) {
            System.out.print("pref is null!!!");
        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("location", location);
            editor.putString("building", building);
            editor.putString("floor", floor);
            editor.putString("room", room);
            editor.commit();
        }

        edu.columbia.locationsensor.Location locationEntry = new edu.columbia.locationsensor.Location();
        locationEntry.setLocationName(location);
        locationEntry.setBuilding(building);
        locationEntry.setFloor(floor);
        locationEntry.setRoom(room);
        locationEntry.setStreetAddress(street);
        locationEntry.setCity(city);
        locationEntry.setZipCode(zipCode);
        locationDataSource.open();
        locationDataSource.insertLocation(locationEntry);

        String toastMessage = getString(R.string.location_sent);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        reloadFrequentLocations();
    }

    public void sendLocation(View view) {
        LocationCoordinates locationCoordinates = DataHolder.getInstance().getLocationCoordinates();

        if(locationCoordinates != null) {
            edu.columbia.locationsensor.Address addr = new edu.columbia.locationsensor.Address();

            EditText streetText = (EditText)findViewById(R.id.street);
            EditText cityText = (EditText)findViewById(R.id.city);
            EditText zipText = (EditText)findViewById(R.id.zipcode);

            performReverseGeocoding(locationCoordinates, addr, streetText, cityText, zipText);
        } else {
            editText = (EditText)findViewById(R.id.street);
            editText.setText("", TextView.BufferType.EDITABLE);

            editText = (EditText)findViewById(R.id.city);
            editText.setText("", TextView.BufferType.EDITABLE);

            editText = (EditText)findViewById(R.id.zipcode);
            editText.setText("", TextView.BufferType.EDITABLE);

        }
    }

    public void startPressure(View view) {
        Intent intent = new Intent(this, PressureActivity.class);
        startActivity(intent);
    }

    public void getWifiInfo(View view) {
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void performReverseGeocoding(LocationCoordinates coordinates, edu.columbia.locationsensor.Address address,
                                         EditText streetText, EditText cityText, EditText zipText) {
        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Please wait", "Loading.. Please Wait", true, false);
        Thread mThread = new ReverseGeocoder(address, coordinates, streetText, cityText,zipText, dialog, this, MainActivity.this);
        mThread.start();
    }
}


/**
 * This thread invokes the reverse geocoding service to obtain a street address, city and zip code.
 * This is used by the home screen widget to send updates once a location has been selected.
 *
 */
class LocationUpdateSender extends Thread {
    private String location;
    private LocationDataSource locationDataSource;
    private Context mContext;

    LocationUpdateSender(String location, Context mContext) {
        this.location = location;
        this.mContext = mContext;
        this.locationDataSource = new LocationDataSource(mContext);
    }

    @Override
    public void run() {
        String[] tokens = location.split(" ");
        edu.columbia.locationsensor.Location locationObj = new edu.columbia.locationsensor.Location();
        locationObj.setLocationName(location);
        locationObj.setBuilding(tokens[0]);
        locationObj.setFloor(tokens[1]);
        locationObj.setRoom(tokens[2]);

        LocationCoordinates coordinates = DataHolder.getInstance().getLocationCoordinates();

        try {
            StringBuilder builder = new StringBuilder();
            builder.append(Constants.REVERSE_GEOCODING_URL);
            builder.append(coordinates.getLatitude());
            builder.append(Constants.COMMA);
            builder.append(coordinates.getLongitude());

            String url = builder.toString();
            HttpPost post = new HttpPost(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            InputStream stream = response.getEntity().getContent();
            builder = new StringBuilder();

            int b;
            while ((b = stream.read()) != -1) {
                builder.append((char) b);
            }

            String street = null, route = null;
            String json = builder.toString();
            json = json.replaceAll("\\n", "");
            JSONObject jsonObj = new JSONObject(json);
            JSONArray resultsArr = jsonObj.getJSONArray("results");

            for (int i = 0; i < resultsArr.length(); i++) {
                JSONArray addressArr = resultsArr.getJSONObject(0).getJSONArray("address_components");
                for (int j = 0; j < addressArr.length(); j++) {
                    JSONObject jsonAddress = addressArr.getJSONObject(j);
                    String longName = jsonAddress.getString("long_name");
                    String shortName = jsonAddress.getString("short_name");
                    JSONArray types = jsonAddress.getJSONArray("types");

                    for (int k = 0; k < types.length(); k++) {
                        String type = types.getString(k);
                        switch (type) {
                            case "street_number":
                                street = longName;
                                break;

                            case "route":
                                route = longName;
                                break;

                            case "administrative_area_level_1":
                                locationObj.setCity(longName);
                                break;

                            case "postal_code":
                                locationObj.setZipCode(longName);
                                break;
                        }
                        Log.i("ReverseGeocoder:", type);
                    }
                }
            }
            locationObj.setStreetAddress(street + " " + route);
            locationDataSource.open();
            locationDataSource.insertLocation(locationObj);

            String toastMessage = mContext.getString(R.string.location_sent);
            Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * This class is responsible for the reverse translation from coordinates to a street, city and zip address.
 * Once it gets the required details, it populates the required user interface fields.
 * This is used by the application main screen.
 */
class ReverseGeocoder extends Thread {
    edu.columbia.locationsensor.Address address;
    LocationCoordinates coordinates;
    ProgressDialog dialog;
    Context mContext;
    EditText streetText;
    EditText cityText;
    EditText zipText;
    Activity activity;

    ReverseGeocoder(edu.columbia.locationsensor.Address address, LocationCoordinates coordinates,
                    EditText streetText, EditText cityText, EditText zipText,
                    ProgressDialog dialog, Context mContext, Activity activity) {
        this.address = address;
        this.coordinates = coordinates;

        this.streetText = streetText;
        this.cityText = cityText;
        this.zipText = zipText;

        this.dialog = dialog;
        this.mContext = mContext;
        this.activity = activity;
    }

    @Override
    public void run() {
        EditText editText = null;
        final edu.columbia.locationsensor.Address address = new edu.columbia.locationsensor.Address();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(Constants.REVERSE_GEOCODING_URL);
            builder.append(coordinates.getLatitude());
            builder.append(Constants.COMMA);
            builder.append(coordinates.getLongitude());

            String url = builder.toString();

            HttpPost post = new HttpPost(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            InputStream stream = response.getEntity().getContent();
            builder = new StringBuilder();

            int b;
            while((b = stream.read()) != -1) {
                builder.append((char) b);
            }

            String json = builder.toString();
            json = json.replaceAll("\\n", "");
            JSONObject jsonObj = new JSONObject(json);
            JSONArray resultsArr = jsonObj.getJSONArray("results");

            String street = null;
            String route = null;

            for(int i=0; i<resultsArr.length(); i++) {
                JSONArray addressArr = resultsArr.getJSONObject(0).getJSONArray("address_components");
                for(int j=0; j<addressArr.length(); j++) {
                    JSONObject jsonAddress = addressArr.getJSONObject(j);
                    String longName = jsonAddress.getString("long_name");
                    String shortName = jsonAddress.getString("short_name");
                    JSONArray types = jsonAddress.getJSONArray("types");

                    for(int k=0;k<types.length();k++) {
                        String type = types.getString(k);
                        switch(type) {
                            case "street_number" :
                                street = longName;
                                break;

                            case "route":
                                route = longName;
                                break;

                            case "administrative_area_level_1":
                                address.setCity(longName);
                                break;

                            case "postal_code":
                                address.setZipCode(longName);
                                break;
                        }
                        Log.i("ReverseGeocoder:", type);
                    }
                }
            }

            address.setStreetAddress(street + " " + route);
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch(JSONException je) {
            je.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (address != null && address.getStreetAddress() != null) {
                            streetText.setText(address.getStreetAddress(), TextView.BufferType.EDITABLE);
                            cityText.setText(address.getCity(), TextView.BufferType.EDITABLE);
                            zipText.setText(address.getZipCode(), TextView.BufferType.EDITABLE);
                        } else {
                            streetText.setText("", TextView.BufferType.EDITABLE);
                            cityText.setText("", TextView.BufferType.EDITABLE);
                            zipText.setText("", TextView.BufferType.EDITABLE);

                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dialog.dismiss();
            }
        }
    }
}
