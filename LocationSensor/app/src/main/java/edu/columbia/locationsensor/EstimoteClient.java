package edu.columbia.locationsensor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

/**
 * Created by Pooja on 4/22/15.
 */
public class EstimoteClient extends Activity implements BeaconManager.RangingListener{

    private static final String TAG = EstimoteClient.class.getSimpleName();
    private BeaconManager beaconManager;
    private static final Region ALL_ESTIMOTES_REGION = new Region("regionid", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "This device doesn't support BLE", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        } else {
            connectToBeaconService();
        }

    }

    @Override
    public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
        for(Beacon b : beacons) {
            Log.i("Beacon found", b.toString());
        }
    }

    private void connectToBeaconService() {
        getActionBar().setSubtitle("Scanning...");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTES_REGION);
                } catch (RemoteException re) {
                    Toast.makeText(EstimoteClient.this, "Cannot start ranging!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Cannot start ranging", re);
                }

            }
        });
    }
}

