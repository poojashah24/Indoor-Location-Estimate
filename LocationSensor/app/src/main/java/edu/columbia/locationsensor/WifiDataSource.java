package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to query, insert and delete wifi sensor readings from and to the
 * in-memory SQLite database.
 */
public class WifiDataSource {

    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "ssid", "frequency", "level", "levelInDb", "timestamp" };

    public WifiDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public long insertWifiReading(String SSID, int frequency, int level, int levelInDb, long timestamp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ssid", SSID);
        contentValues.put("frequency", frequency);
        contentValues.put("level", level);
        contentValues.put("levelInDb", levelInDb);
        contentValues.put("timestamp", timestamp);

        long id = db.insert("WIFI_READINGS", null, contentValues);
        return id;
    }

    public void deleteWifiReading(String ssid, long timestamp) {
        db.delete("WIFI_READINGS", null, null);
    }

    public List<WifiNetwork> getAllWifiReadings() {
        List<WifiNetwork> comments = new ArrayList<WifiNetwork>();

        Cursor cursor = db.query("WIFI_READINGS",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WifiNetwork wifiReading = cursorToReading(cursor);
            comments.add(wifiReading);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    private WifiNetwork cursorToReading(Cursor cursor) {
        WifiNetwork reading = new WifiNetwork(cursor.getString(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getInt(3), cursor.getLong(4));
        return reading;
    }
}
