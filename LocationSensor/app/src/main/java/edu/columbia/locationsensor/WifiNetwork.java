package edu.columbia.locationsensor;

import android.os.SystemClock;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Pooja on 2/22/15.
 */
public class WifiNetwork implements Serializable, Comparable {
    private String SSID;
    private int frequency;
    private int level;
    private int levelInDb;
    private long timeStamp;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelInDb() {
        return levelInDb;
    }

    public void setLevelInDb(int levelInDb) {
        this.levelInDb = levelInDb;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }



    public WifiNetwork(String SSID, int frequency, int level, int levelInDb, long timestamp) {
        this.SSID = SSID;
        this.frequency = frequency;
        this.level = level;
        this.levelInDb = levelInDb;
        this.timeStamp = System.currentTimeMillis() - SystemClock.elapsedRealtime() + (timestamp / 1000);
    }

    public int compareTo(Object o){
        if(o!= null && o instanceof WifiNetwork) {
            return ((Integer)((WifiNetwork)o).level).compareTo(this.level);
        }
        return 0;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nFrequency: " + frequency+"MHz");
        builder.append("\nLevel: " + level);
        builder.append("\nLast Measured: " + new Date(timeStamp));

        return builder.toString();
    }
}
