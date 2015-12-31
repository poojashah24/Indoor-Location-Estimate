package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to query and insert recent locations from and to the
 * in-memory SQLite database.
 */
public class FreqLocationsDataSource {

    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "location" };

    public FreqLocationsDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public long insertLocation(String location) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("location", location);
        long id = db.insert("FREQ_LOCATIONS", null, contentValues);
        return id;
    }

    public List<String> getAllFrequentLocations() {
        List<String> comments = new ArrayList<String>();

        Cursor cursor = db.query("FREQ_LOCATIONS",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String comment = cursor.getString(0);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }
}
