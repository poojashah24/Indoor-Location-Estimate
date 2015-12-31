package edu.columbia.locationsensor;

import java.util.List;

/**
 * Stores a list of wifi networks.
 */
public class WifiReading {
    List<WifiNetwork> networks;

    public WifiReading(List<WifiNetwork> networks) {
        this.networks = networks;
    }

    public List<WifiNetwork> getWifiNetworks() {
        return networks;
    }
}
