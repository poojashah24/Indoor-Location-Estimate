package edu.columbia.locationsensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PressureService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private String exception;
    private long lastUpdate;

    private static PressureDataSource dataSource;
    private static List<PressureReading> readingList;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand in pressure service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        readingList = new ArrayList<PressureReading>();
        if(pressureSensor == null) {
            Toast.makeText(this, R.string.no_pressure_sensor, Toast.LENGTH_SHORT).show();
            exception = getString(R.string.no_pressure_sensor);
        }
        else {
            sensorManager.registerListener(this, pressureSensor, 100000);
            dataSource = new PressureDataSource(this);
            //dataSource.open();
            lastUpdate = System.currentTimeMillis();
            exception = null;
        }
        return START_STICKY;
       }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(System.currentTimeMillis() - lastUpdate >= 500) {
            PressureReading r = new PressureReading(event.values[0]);
            DataHolder.getInstance().setPressureReading(r);
            lastUpdate = System.currentTimeMillis();
            readingList.add(r);
            if(readingList.size() >= 100) {
                dataSource.open();
                for(PressureReading reading : readingList) {
                    dataSource.insertPressureReading(reading.getPressure(), reading.getRefreshTime());
                }
                //dataSource.close();
                readingList.clear();
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("accuracy", accuracy + "");
    }
}
