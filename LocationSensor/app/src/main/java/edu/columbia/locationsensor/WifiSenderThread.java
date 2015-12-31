package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Thread to send wifi sensor readings to the backend server application
 */
public class WifiSenderThread extends HTTPAsyncTask {

    private JSONObject wifiReadings;
    public WifiSenderThread(Context mContext, JSONObject wifiReadings) {
        super(mContext);
        this.wifiReadings = wifiReadings;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(wifiReadings.toString());
        return 200;
    }
}
