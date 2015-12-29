package edu.columbia.locationsensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MagnetometerService extends Service implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private String exception;
    private long lastUpdate;

    private static MagnetometerDataSource dataSource;
    private static List<MagnetometerReading> readingList;

    public MagnetometerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand in pressure service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(magneticSensor == null) {
            Toast.makeText(this, R.string.no_magnetic_sensor, Toast.LENGTH_SHORT).show();
            exception = getString(R.string.no_magnetic_sensor);
        }
        else {
            dataSource = new MagnetometerDataSource(this);
            //dataSource.open();
            lastUpdate = System.currentTimeMillis();
            readingList = new ArrayList<MagnetometerReading>();
            sensorManager.registerListener(this, magneticSensor, 5000);
            exception = null;
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(System.currentTimeMillis() - lastUpdate >= 2000) {
            MagnetometerReading reading = new MagnetometerReading(event.values[0],
                    event.values[1], event.values[2]);
            DataHolder.getInstance().setMagnetometerReading(reading);
            lastUpdate = System.currentTimeMillis();
            readingList.add(reading);
            if (readingList.size() > 50) {
                dataSource.open();
                for (MagnetometerReading mReading : readingList) {
                    dataSource.insertMagnetometerReading(mReading);
                }
                //dataSource.close();
                readingList.clear();
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //NO-OP
    }
}
