package com.gdgdallas.googlefitdemo.fitAPITasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gdgdallas.googlefitdemo.data.SweetLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by luke.wallace on 11/17/15.
 */
public class LoadNutritionTask extends AsyncTask<Void, Void, SweetLog> {

    private final String TAG = this.getClass().getCanonicalName();
    private final GoogleApiClient mGoogleApiClient;
    private final ILoadNutritionTaskListener mListener;

    public LoadNutritionTask(GoogleApiClient googleApiClient, ILoadNutritionTaskListener listener) {
        mGoogleApiClient = googleApiClient;
        mListener = listener;
    }


    @Override
    protected SweetLog doInBackground(Void... voids) {
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // In this case we are just going to look through all the nutrition data logged from the last week
                .read(DataType.TYPE_NUTRITION)
                        //.bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        SweetLog sweetLog = null;

        if (dataReadResult.getDataSets().size() > 0) {
            sweetLog = new SweetLog(dataReadResult.getDataSets().get(0));
        }


        return sweetLog;
    }


    @Override
    protected void onPostExecute(SweetLog sweetLog) {
        if (mListener != null) {
            mListener.onNutritionLoaded(sweetLog);
        }
    }


}