package edu.columbia.locationreceiver;

/**
 * Stores information about each device, including the os version, and model
 * details.
 * 
 * @author Pooja
 *
 */
public class DeviceInfo {
	private final String osVersion;
	private final String buildVersion;
	private final int apiLevel;
	private final String device;
	private final String model;
	private final String product;
	private final long timestamp;

	public DeviceInfo(String osVersion, String buildVersion, int apiLevel,
			String device, String model, String product, long timestamp) {
		this.osVersion = osVersion;
		this.buildVersion = buildVersion;
		this.apiLevel = apiLevel;
		this.device = device;
		this.model = model;
		this.product = product;
		this.timestamp = timestamp;
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

	public long getTimestamp() {
		return timestamp;
	}
}
