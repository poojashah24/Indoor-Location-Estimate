package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by Pooja on 4/29/15.
 */
public class WifiHttpAsyncTask extends HTTPAsyncTask {

    private JSONObject wifiReadings;
    public WifiHttpAsyncTask(Context mContext, JSONObject wifiReadings) {
        super(mContext);
        this.wifiReadings = wifiReadings;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(wifiReadings.toString());
        return 200;
    }
}
