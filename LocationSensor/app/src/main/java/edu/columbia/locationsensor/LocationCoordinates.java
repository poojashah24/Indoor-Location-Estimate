package edu.columbia.locationsensor;

/**
 * Created by Pooja on 2/21/15.
 */
public class LocationCoordinates {
    private double longitude;
    private double latitude;
    private double altitude;
    private float accuracy;
    private float speed;
    private String provider;
    private long refreshTime;


    public LocationCoordinates(){}
    /**
     *
     * @param latitude
     * @param longitude
     * @param altitude
     * @param accuracy
     * @param speed
     * @param provider
     */
    public LocationCoordinates(double latitude, double longitude, double altitude,
                               float accuracy, float speed, String provider) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.altitude = altitude;
        this.provider = provider;
        refreshTime = System.currentTimeMillis();
    }

    public LocationCoordinates(double latitude, double longitude, double altitude,
                               float accuracy, float speed, String provider, long refreshTime) {
        this(latitude, longitude, altitude, accuracy, speed, provider);
        this.refreshTime = refreshTime;
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
