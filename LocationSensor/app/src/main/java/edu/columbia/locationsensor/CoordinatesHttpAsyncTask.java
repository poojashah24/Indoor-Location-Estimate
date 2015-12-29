package edu.columbia.locationsensor;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by Pooja on 4/29/15.
 */
public class CoordinatesHttpAsyncTask extends HTTPAsyncTask {

    private JSONObject coordinates;
    public CoordinatesHttpAsyncTask(Context mContext, JSONObject coordinates) {
        super(mContext);
        this.coordinates = coordinates;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        super.sendToServer(coordinates.toString());
        return 200;
    }
}
