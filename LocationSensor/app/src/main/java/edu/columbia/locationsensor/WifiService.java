package edu.columbia.locationsensor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages Wi-Fi sensor updates and access point information sent by the Wi-Fi sensor.
 */
public class WifiService extends Service {

    WifiManager wifiManager = null;
    private List<WifiNetwork> readings;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager = (WifiManager )getSystemService(Context.WIFI_SERVICE);
        if(wifiManager == null)
            Toast.makeText(this, R.string.no_wifi_manager, Toast.LENGTH_SHORT).show();
        else if(!wifiManager.isWifiEnabled())
            Toast.makeText(this, R.string.wifi_disabled, Toast.LENGTH_SHORT).show();
        else {
            boolean started = wifiManager.startScan();
            if(started) {
                registerReceiver(new WifiResultsReceiver(wifiManager, this),
                        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

/**
 * Registers to receive Wi-Fi sensor updates.
 */
class WifiResultsReceiver extends BroadcastReceiver {

    WifiManager wifiManager;
    private static WifiDataSource wifiDataSource;

    public WifiResultsReceiver(WifiManager wifiManager, Context mContext) {

        this.wifiManager = wifiManager;
        wifiDataSource = new WifiDataSource(mContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScanResult> wifiNetworks = wifiManager.getScanResults();
        if(wifiNetworks != null) {
            List<WifiNetwork> wifiNetworkList = new ArrayList<>(wifiNetworks.size());
            wifiDataSource.open();
            for (ScanResult res : wifiNetworks) {
                int level = wifiManager.calculateSignalLevel(res.level, 5);
                WifiNetwork network = new WifiNetwork(res.SSID, res.frequency, level, res.level, res.timestamp);
                wifiNetworkList.add(network);
                wifiDataSource.insertWifiReading(res.SSID, res.frequency, level, res.level, res.timestamp);
            }
            WifiReading wifiReading = new WifiReading(wifiNetworkList);
            DataHolder.getInstance().setWifiReading(wifiReading);
        }
    }
}
