package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pooja on 4/23/15.
 */
public class CoordinatesDataSource {

    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "latitude", "longitude", "updated", "altitude", "speed", "accuracy", "provider" };

    public CoordinatesDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
    }*/

    public long insertLocationReading(LocationCoordinates locationCoordinates) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", locationCoordinates.getLatitude());
        contentValues.put("longitude", locationCoordinates.getLongitude());
        contentValues.put("updated", locationCoordinates.getRefreshTime());
        contentValues.put("altitude", locationCoordinates.getAltitude());
        contentValues.put("speed", locationCoordinates.getSpeed());
        contentValues.put("accuracy", locationCoordinates.getAccuracy());
        contentValues.put("provider", locationCoordinates.getProvider());

        long id = db.insert("COORDINATES", null, contentValues);
        return id;
    }

    public void deleteLocationReading(double latitude, double longitude) {
        db.delete("COORDINATES", "latitude="+latitude+" and longitude="+longitude, null);
    }

    public List<LocationCoordinates> getAllLocationCoordinates() {
        List<LocationCoordinates> comments = new ArrayList<LocationCoordinates>();

        Cursor cursor = db.query("COORDINATES",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationCoordinates coordinates = cursorToReading(cursor);
            comments.add(coordinates);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    private LocationCoordinates cursorToReading(Cursor cursor) {
        LocationCoordinates reading = new LocationCoordinates(cursor.getDouble(0),
                cursor.getDouble(1), cursor.getDouble(3),
                cursor.getFloat(4), cursor.getFloat(5),
                cursor.getString(6), cursor.getLong(2));
        return reading;
    }
}
