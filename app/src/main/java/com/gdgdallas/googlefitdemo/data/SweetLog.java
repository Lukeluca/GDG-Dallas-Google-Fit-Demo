package com.gdgdallas.googlefitdemo.data;

import android.util.Log;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by luke.wallace on 8/25/15.
 */
public class SweetLog {

    private final String TAG = this.getClass().getCanonicalName();
    private DonutLog mDonutLog;
    private CupcakeLog mCupcakeLog;

     public SweetLog(CupcakeLog cupcakeLog, DonutLog donutLog) {
        this.mCupcakeLog = cupcakeLog;
        this.mDonutLog = donutLog;
    }

    public SweetLog(DonutLog donutLog) {
        this.mDonutLog = donutLog;
    }

    public DonutLog getDonutLog() {
        return mDonutLog;
    }

    public CupcakeLog getCupcakeLog() { return mCupcakeLog; }

    // More complex constructor that reads through a well formed Google Fit Data set and looks for
    // sweets to add to the log.
    public SweetLog(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat();

        int donutCount = 0;
        int cupcakeCount = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                if (field.getName().equals(Field.FIELD_FOOD_ITEM.getName())) {
                    switch (dp.getValue(field).toString()) {
                        case CupcakeLog.KEY_VALUE_CUPCAKE:
                            cupcakeCount++;
                            break;
                        case DonutLog.KEY_VALUE_DONUT:
                            donutCount++;
                            break;
                    }
                }
            }
        }

        this.mCupcakeLog = new CupcakeLog(System.currentTimeMillis(), cupcakeCount);
        this.mDonutLog = new DonutLog(System.currentTimeMillis(), donutCount);
    }

}
