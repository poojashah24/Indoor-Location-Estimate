package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Pooja on 4/23/15.
 */
public class SQLLiteHelper extends SQLiteOpenHelper {

    private static SQLLiteHelper INSTANCE = null;

    private static final String DB_NAME = "locationdb.db";
    private static final int DB_VERSION = 10;

    private static final String CREATE_PRESSURE_TABLE = "create table PRESSURE_READINGS (pressure real not null, timestamp real not null);";
    private static final String CREATE_COORDINATES_TABLE = "create table COORDINATES (longitude real not null, latitude real not null, accuracy real not null, speed not null, altitude not null, provider text not null, updated real not null)";
    private static final String CREATE_WIFI_TABLE = "create table WIFI_READINGS (ssid text, frequency real, level real, levelInDb real, timestamp real);";
    private static final String CREATE_MAGNETOMETER_TABLE = "create table MAGNETOMETER_READING (x real, y real, z real, timestamp real);";
    private static final String CREATE_FREQ_LOCATIONS_TABLE = "create table FREQ_LOCATIONS (location text);";
    private static final String CREATE_LOCATION_TABLE = "create table LOCATION (name text, building text, floor text, room text, street text, city text, zipcode text, timestamp real);";

    private SQLLiteHelper(Context mContext) {
        super(mContext, DB_NAME, null, DB_VERSION);
    }

    public static synchronized SQLLiteHelper getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new SQLLiteHelper(mContext);
        }

        return INSTANCE;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PRESSURE_TABLE);
        db.execSQL(CREATE_COORDINATES_TABLE);
        db.execSQL(CREATE_WIFI_TABLE);
        db.execSQL(CREATE_MAGNETOMETER_TABLE);
        db.execSQL(CREATE_FREQ_LOCATIONS_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);

        populateFrequentLocations(db);
    }

    private void populateFrequentLocations(SQLiteDatabase db) {
        ArrayList<String> locationList = new ArrayList<String>();
        locationList.add("home Level2 C");
        locationList.add("work Level6 620");
        locationList.add("CEPSR Level7 720");
        locationList.add("CEPSR Level7 lobby");
        locationList.add("CEPSR Level7 IRTLab");
        locationList.add("CEPSR Level6 Lounge");
        locationList.add("NWC Level4 library");
        locationList.add("NWC Level5 library");
        locationList.add("Mudd Level4 CSLounge");

        for(String location : locationList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("location", location);

            long id = db.insert("FREQ_LOCATIONS", null, contentValues);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS PRESSURE_READINGS");
        db.execSQL("DROP TABLE IF EXISTS COORDINATES");
        db.execSQL("DROP TABLE IF EXISTS WIFI_READINGS");
        db.execSQL("DROP TABLE IF EXISTS MAGNETOMETER_READING");
        db.execSQL("DROP TABLE IF EXISTS FREQ_LOCATIONS");
        db.execSQL("DROP TABLE IF EXISTS LOCATION");
        onCreate(db);
    }
}
