package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Thread to send pressure sensor readings to the backend server application
 */
public class PressureSenderThread extends HTTPAsyncTask {

    private JSONObject pressureReadings;
    public PressureSenderThread(Context mContext, JSONObject pressureReadings) {
        super(mContext);
        this.pressureReadings = pressureReadings;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(pressureReadings.toString());
        return 200;
    }
}
