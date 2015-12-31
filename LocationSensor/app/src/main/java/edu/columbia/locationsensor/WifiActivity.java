package edu.columbia.locationsensor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User interface for the Wi-Fi sensor widget. This shows a listing of all the access points
 * detected by the Wi-Fi sensor.
 */
public class WifiActivity extends FragmentActivity{

    private WifiResultsReceiver1 wifiResultsReceiver;
    WifiManager wifiManager = null;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        listView = (ListView) findViewById(R.id.wifi_results);

        wifiManager = (WifiManager )getSystemService(Context.WIFI_SERVICE);
        if(wifiManager == null)
            Toast.makeText(this, R.string.no_wifi_manager, Toast.LENGTH_SHORT).show();
        else if(!wifiManager.isWifiEnabled())
            Toast.makeText(this, R.string.wifi_disabled, Toast.LENGTH_SHORT).show();
        else {
            boolean started = wifiManager.startScan();
            if(started) {
                wifiResultsReceiver = new WifiResultsReceiver1(this, WifiActivity.this, wifiManager, listView);
                registerReceiver(wifiResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        }
    }

    @Override
    protected void onStop() {
        if (wifiResultsReceiver != null) {
            unregisterReceiver(wifiResultsReceiver);
            wifiResultsReceiver = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (wifiResultsReceiver != null) {
            unregisterReceiver(wifiResultsReceiver);
            wifiResultsReceiver = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class WifiResultsReceiver1 extends BroadcastReceiver implements AdapterView.OnItemClickListener {

    WifiManager wifiManager;
    Activity activity;
    ListView listView;
    Context mContext;
    List<WifiNetwork> wifiNetworksList = null;

    public WifiResultsReceiver1(Context context, Activity activity, WifiManager wifiManager, ListView listView) {
        this.wifiManager = wifiManager;
        this.listView = listView;
        this.mContext = context;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScanResult> wifiNetworks = wifiManager.getScanResults();
        if(wifiNetworks != null) {
            wifiNetworksList = new ArrayList<WifiNetwork>();
            for (ScanResult res : wifiNetworks) {
                int level = wifiManager.calculateSignalLevel(res.level, 5);
                WifiNetwork network = new WifiNetwork(res.SSID, res.frequency, level, res.level, res.timestamp);
                wifiNetworksList.add(network);
            }
            WifiNetwork nArr[] = wifiNetworksList.toArray(new WifiNetwork[wifiNetworksList.size()]);
            Arrays.sort(nArr);
            wifiNetworksList = Arrays.asList(nArr);
            WifiListAdapter networkAdapter = new WifiListAdapter(mContext,android.R.layout.simple_list_item_1, wifiNetworksList);
            listView.setAdapter(networkAdapter);
            listView.setOnItemClickListener(this);
        }

    }

    /**
     * Click handler to display the selected access point's details.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        WifiDetailsDialog detailsDialog = new WifiDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable("WifiNetwork", wifiNetworksList.get(position));
        detailsDialog.setArguments(args);
        detailsDialog.show(activity.getFragmentManager(), "wifidetails");
    }
}
