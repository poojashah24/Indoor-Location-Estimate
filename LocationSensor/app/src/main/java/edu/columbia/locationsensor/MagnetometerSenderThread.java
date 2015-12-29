package edu.columbia.locationsensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

/**
 * Created by Pooja on 5/20/15.
 */
public class MagnetometerSenderThread extends Thread {

    private String json;
    private Context mContext;

    public MagnetometerSenderThread(Context context, String json) {
        this.mContext = context;
        this.json = json;
    }

    public void run() {
        Log.i("HTTPAsyncTask", json.toString());
        sendToServer(json.toString());
    }

    protected int sendToServer(String jsonString) {
        HttpClient httpClient = new DefaultHttpClient();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String serverURL = sharedPreferences.getString("AppPreferences",
                Constants.DEFAULT_SERVER_URL);

        //HttpPost postMethod = new HttpPost(mContext.getString(R.string.server_url));
        HttpPost postMethod = new HttpPost(serverURL);

        try {
            StringEntity se = new StringEntity(jsonString);
            postMethod.setEntity(se);
            postMethod.setHeader("Accept","application/json");
            postMethod.setHeader("Content-type", "application/json");

            HttpResponse response = httpClient.execute(postMethod);

            return response.getStatusLine().getStatusCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
