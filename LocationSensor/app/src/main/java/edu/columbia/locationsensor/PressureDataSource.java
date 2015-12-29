package edu.columbia.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Pooja on 4/23/15.
 */
public class PressureDataSource {

    private SQLLiteHelper dbHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { "pressure", "timestamp" };

    public PressureDataSource(Context mContext) {
        dbHelper = SQLLiteHelper.getInstance(mContext);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
    }*/

    public long insertPressureReading(double pressureReading, long refreshTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pressure", pressureReading);
        contentValues.put("timestamp", refreshTime);

        long id = db.insert("PRESSURE_READINGS", null, contentValues);
        return id;
    }

    public void deletePressureReading(double pressureReading, long refreshTime) {
        //db.delete("PRESSURE_READINGS", )
        db.delete("PRESSURE_READINGS", "pressure="+pressureReading+" and timestamp="+refreshTime, null);
    }

    public List<PressureReading> getAllPressureReadings() {
        List<PressureReading> comments = new ArrayList<PressureReading>();

        Cursor cursor = db.query("PRESSURE_READINGS",
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PressureReading comment = cursorToReading(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return comments;
    }

    private PressureReading cursorToReading(Cursor cursor) {
        PressureReading reading = new PressureReading(cursor.getDouble(0), cursor.getLong(1));
        return reading;
    }
}
