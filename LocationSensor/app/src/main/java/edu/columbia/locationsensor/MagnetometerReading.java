package edu.columbia.locationsensor;

/**
 * Created by Pooja on 4/11/15.
 */
public class MagnetometerReading {
    private double x;
    private double y;
    private double z;
    private long refreshTime;

    public MagnetometerReading(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.refreshTime = System.currentTimeMillis();
    }

    public MagnetometerReading(double x, double y, double z, long timestamp) {
        this(x, y, z);
        this.refreshTime = timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getRefreshTime() {
        return refreshTime;
    }
}
