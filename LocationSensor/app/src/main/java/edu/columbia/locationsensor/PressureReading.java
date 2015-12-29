package edu.columbia.locationsensor;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pooja on 2/22/15.
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
