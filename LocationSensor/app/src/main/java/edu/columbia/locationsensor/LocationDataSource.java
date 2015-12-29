package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pooja on 5/21/15.
 */
public class LocationDataSource {
    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "name", "building", "floor", "room", "street", "city", "zipcode", "timestamp" };

    public LocationDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
    }*/

    public long insertLocation(Location location) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", location.getLocationName());
        contentValues.put("building", location.getBuilding());
        contentValues.put("floor", location.getFloor());
        contentValues.put("room", location.getRoom());
        contentValues.put("street", location.getStreetAddress());
        contentValues.put("city", location.getCity());
        contentValues.put("zipcode", location.getZipCode());
        contentValues.put("timestamp", location.getRefreshTime());

        long id = db.insert("LOCATION", null, contentValues);
        return id;
    }

    public void deleteLocation(String name, double timestamp) {
        db.delete("LOCATION", null, null);
    }

    public List<Location> getAllLocations() {
        List<Location> comments = new ArrayList<Location>();

        Cursor cursor = db.query("LOCATION",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Location comment = cursorToReading(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    // "name", "building", "floor", "room", "street", "city", "zipcode", "timestamp"
    private Location cursorToReading(Cursor cursor) {
        Location reading = new Location();
        reading.setLocationName(cursor.getString(0));
        reading.setBuilding(cursor.getString(1));
        reading.setFloor(cursor.getString(2));
        reading.setRoom(cursor.getString(3));
        reading.setStreetAddress(cursor.getString(4));
        reading.setCity(cursor.getString(5));
        reading.setZipCode(cursor.getString(6));
        reading.setRefreshTime(cursor.getLong(7));
        return reading;
    }
}
