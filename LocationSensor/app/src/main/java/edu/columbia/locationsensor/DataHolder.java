package edu.columbia.locationsensor;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Pooja on 2/22/15.
 */
public class DataHolder {

    private PressureReading pressureReading;
    private LocationCoordinates locationCoordinates;
    private WifiReading wifiReading;
    private MagnetometerReading magnetometerReading;
    private DeviceInfo deviceInfo;

    private static ReentrantLock pressureLock = new ReentrantLock();
    private static ReentrantLock locationLock = new ReentrantLock();
    private static ReentrantLock wifiLock = new ReentrantLock();
    private static ReentrantLock magnetometerLock = new ReentrantLock();

    private static final DataHolder INSTANCE = new DataHolder();
    private DataHolder(){}

    public static DataHolder getInstance() {
        return INSTANCE;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setPressureReading(PressureReading pressureReading) {
        synchronized (pressureLock) {
            this.pressureReading = pressureReading;
        }
    }

    public void setLocationCoordinates(LocationCoordinates locationCoordinates) {
        synchronized (locationLock) {
            this.locationCoordinates = locationCoordinates;
        }
    }

    public void setWifiReading(WifiReading wifiReading) {
        synchronized (wifiLock) {
            this.wifiReading = wifiReading;
        }
    }

    public void setMagnetometerReading(MagnetometerReading magnetometerReading) {
        synchronized (magnetometerLock) {
            this.magnetometerReading = magnetometerReading;
        }
    }

    public PressureReading getPressureReading() {
        synchronized (pressureLock) {
            return pressureReading;
        }
    }

    public LocationCoordinates getLocationCoordinates() {
        synchronized (locationLock) {
            return locationCoordinates;
        }
    }

    public WifiReading getWifiReading() {
        synchronized (wifiLock) {
            return wifiReading;
        }
    }

    public MagnetometerReading getMagnetometerReading() {
        synchronized (magnetometerLock) {
            return magnetometerReading;
        }
    }

}
