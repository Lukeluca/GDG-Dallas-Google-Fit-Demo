package com.gdgdallas.googlefitdemo.data;

/**
 * Created by luke.wallace on 9/4/15.
 */
public class CupcakeLog {

    public static final String KEY_VALUE_CUPCAKE = "cupcake";

    private final long mTime;
    private final int mCount;


    public CupcakeLog(long time, int count) {
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
