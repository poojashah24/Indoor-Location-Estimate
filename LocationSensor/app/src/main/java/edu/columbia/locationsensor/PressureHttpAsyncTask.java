package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by Pooja on 4/29/15.
 */
public class PressureHttpAsyncTask extends HTTPAsyncTask {

    private JSONObject pressureReadings;
    public PressureHttpAsyncTask(Context mContext, JSONObject pressureReadings) {
        super(mContext);
        this.pressureReadings = pressureReadings;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(pressureReadings.toString());
        return 200;
    }
}
