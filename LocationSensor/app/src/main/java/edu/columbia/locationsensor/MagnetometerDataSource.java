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
public class MagnetometerDataSource {

    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "x", "y", "z", "timestamp" };

    public MagnetometerDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
    }*/

    public long insertMagnetometerReading(MagnetometerReading reading) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("x", reading.getX());
        contentValues.put("y", reading.getY());
        contentValues.put("z", reading.getZ());
        contentValues.put("timestamp", reading.getRefreshTime());

        long id = db.insert("MAGNETOMETER_READING", null, contentValues);
        return id;
    }

    public void deleteMagnetometerReading() {
        //db.delete("MAGNETOMETER_READING", "x="+x+" and y=" + y + " and z=" + z + "and timestamp="+refreshTime, null);
        db.delete("MAGNETOMETER_READING", null, null);
    }

    public List<MagnetometerReading> getAllMagnetometerReadings() {
        List<MagnetometerReading> comments = new ArrayList<MagnetometerReading>();

        Cursor cursor = db.query("MAGNETOMETER_READING",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MagnetometerReading comment = cursorToReading(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    private MagnetometerReading cursorToReading(Cursor cursor) {
        MagnetometerReading reading = new MagnetometerReading(cursor.getDouble(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getLong(3));
        return reading;
    }
}
