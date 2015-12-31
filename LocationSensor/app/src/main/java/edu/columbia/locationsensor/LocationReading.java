package edu.columbia.locationsensor;

/**
 * Wrapper around sensor readings, location and device information.
 */
public class LocationReading {
    private DeviceInfo deviceInfo;
    private Location location;
    private LocationCoordinates coordinates;
    private PressureReading pressureReading;
    private MagnetometerReading magnetometerReading;
    private WifiReading wifiReading;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LocationCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public PressureReading getPressureReading() {
        return pressureReading;
    }

    public void setPressureReading(PressureReading pressureReading) {
        this.pressureReading = pressureReading;
    }

    public MagnetometerReading getMagnetometerReading() {
        return magnetometerReading;
    }

    public void setMagnetometerReading(MagnetometerReading magnetometerReading) {
        this.magnetometerReading = magnetometerReading;
    }

    public WifiReading getWifiReading() {
        return wifiReading;
    }

    public void setWifiReading(WifiReading wifiReading) {
        this.wifiReading = wifiReading;
    }
}
