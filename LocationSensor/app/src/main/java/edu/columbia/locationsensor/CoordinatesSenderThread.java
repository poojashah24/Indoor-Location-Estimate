package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Thread to send location coordinates to the backend server application
 */
public class CoordinatesSenderThread extends HTTPAsyncTask {

    private JSONObject coordinates;
    public CoordinatesSenderThread(Context mContext, JSONObject coordinates) {
        super(mContext);
        this.coordinates = coordinates;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(coordinates.toString());
        return 200;
    }
}
