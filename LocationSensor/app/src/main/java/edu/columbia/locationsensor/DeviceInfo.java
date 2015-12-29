package edu.columbia.locationsensor;

/**
 * Created by Pooja on 3/26/15.
 */
public class DeviceInfo {
    private final String osVersion;
    private final String buildVersion;
    private final int apiLevel;
    private final String device;
    private final String model;
    private final String product;
    private final long refreshTime;

    public DeviceInfo(String osVersion,
            String buildVersion,
            int apiLevel,
            String device,
            String model,
            String product) {
        this.osVersion = osVersion;
        this.buildVersion = buildVersion;
        this.apiLevel = apiLevel;
        this.device = device;
        this.model = model;
        this.product = product;
        this.refreshTime = System.currentTimeMillis();
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public int getApiLevel() {
        return apiLevel;
    }

    public String getDevice() {
        return device;
    }

    public String getModel() {
        return model;
    }

    public String getProduct() {
        return product;
    }

    public long getRefreshTime() {
        return refreshTime;
    }
}
