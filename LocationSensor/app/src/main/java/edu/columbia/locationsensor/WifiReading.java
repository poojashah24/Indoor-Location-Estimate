package edu.columbia.locationsensor;

import java.util.List;

/**
 * Created by Pooja on 2/22/15.
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
