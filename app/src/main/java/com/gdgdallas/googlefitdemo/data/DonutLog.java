package com.gdgdallas.googlefitdemo.data;

/**
 * Created by luke.wallace on 8/25/15.
 */
public class DonutLog {


    public static final String KEY_VALUE_DONUT = "donut";

    private final long mTime;
    private final int mCount;


    public DonutLog(long time, int count) {
        this.mCount = count;
        this.mTime = time;
    }

    public long getTime() {
        return mTime;
    }

    public long getCount() {
        return mCount;
    }
}
