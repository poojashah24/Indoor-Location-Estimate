package edu.columbia.locationsensor;

import java.util.Calendar;
import java.util.Date;

/**
 * Stores pressure readings captured by the pressure sensor.
 */
public class PressureReading {
    private double pressureInMb;
    private long refreshTime;

    public PressureReading(double pressureInMb) {
        this.pressureInMb = pressureInMb;
        refreshTime = System.currentTimeMillis();
    }

    public PressureReading(double pressureInMb, long refreshTime) {
        this.pressureInMb = pressureInMb;
        this.refreshTime = refreshTime;
    }

    public double getPressure() {
        return pressureInMb;
    }

    public long getRefreshTime() {
        return this.refreshTime;
    }
}
