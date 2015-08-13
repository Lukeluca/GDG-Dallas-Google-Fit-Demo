package com.gdgdallas.googlefitdemo.data;

/**
 * Created by lukewallace on 8/9/15.
 */
public class BurpeeLog {

    private final long mTime;
    private final int mCount;
    private long estimatedStartTime;

    public BurpeeLog(long time, Integer count) {
        mTime = time;
        mCount = count.intValue();
    }

    public long getTimeInMillis() {
        return mTime;
    }

    public int getCount() {
        return mCount;
    }

    // Because all excercise sessions need a start time, we'll just estimate it as 5 seconds per burpee.
    public long getEstimatedStartTime() {
        return getTimeInMillis() - (5000 * getCount());
    }
}
