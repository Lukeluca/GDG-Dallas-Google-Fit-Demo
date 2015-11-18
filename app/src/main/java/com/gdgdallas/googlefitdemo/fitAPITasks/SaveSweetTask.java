package com.gdgdallas.googlefitdemo.fitAPITasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gdgdallas.googlefitdemo.data.CupcakeLog;
import com.gdgdallas.googlefitdemo.data.DonutLog;
import com.gdgdallas.googlefitdemo.data.SweetLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.concurrent.TimeUnit;

/**
 * Created by luke.wallace on 11/17/15.
 */
public class SaveSweetTask extends AsyncTask<SweetLog, Void, Status> {

    private static final String TAG = "SaveSweetTask";
    private GoogleApiClient mGoogleApiClient;

    public SaveSweetTask(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }


    protected void onPreExecute() {}

    protected com.google.android.gms.common.api.Status doInBackground(SweetLog... logs) {

        final SweetLog sweetLog = logs[0];

        DataSource nutritionSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_NUTRITION)
                .setAppPackageName(getApplicationContext())
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet dataSet = DataSet.create(nutritionSource);

        final DonutLog log = sweetLog.getDonutLog();

        final long count = log.getCount();
        for (int i = 0; i < count; i++) {
            DataPoint donut = getDonutDataPoint(nutritionSource, 1, log.getTime() - (10000*count));

            dataSet.add(donut);
        }

        final CupcakeLog cupcakeLog = sweetLog.getCupcakeLog();
        final long cupcakeLogCount = cupcakeLog.getCount();
        for (int i = 0; i < cupcakeLogCount; i++) {
            DataPoint cupcake = getCupcakeDataPoint(nutritionSource, 1, log.getTime() - (10000 * count));

            dataSet.add(cupcake);
        }


        // Then, invoke the History API to insert the data and await the result,
        // which is possible here because of the AsyncTask. Always include a timeout when
        // calling await() to avoid hanging that can occur from the service being shutdown
        // because of low memory or other conditions.
        Log.d(TAG, "Inserting the Data via the Google Fit History API...");
        return Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet)
                .await(1, TimeUnit.MINUTES);
    }

    @NonNull
    private DataPoint getDonutDataPoint(DataSource nutritionSource, int count, long time) {
        DataPoint donut = DataPoint.create(nutritionSource);
        donut.setTimestamp(time, TimeUnit.MILLISECONDS);
        donut.getValue(Field.FIELD_FOOD_ITEM).setString(DonutLog.KEY_VALUE_DONUT);
        donut.getValue(Field.FIELD_MEAL_TYPE).setInt(Field.MEAL_TYPE_SNACK);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, 11f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, 0.14f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_POTASSIUM, 0.086f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CHOLESTEROL, 0.008f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, 22f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, 2.1f);
        donut.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, 195f);
        return donut;
    }

    @NonNull
    private DataPoint getCupcakeDataPoint(DataSource nutritionSource, int count, long time) {
        DataPoint cupcake = DataPoint.create(nutritionSource);
        cupcake.setTimestamp(time, TimeUnit.MILLISECONDS);
        cupcake.getValue(Field.FIELD_FOOD_ITEM).setString(CupcakeLog.KEY_VALUE_CUPCAKE);
        cupcake.getValue(Field.FIELD_MEAL_TYPE).setInt(Field.MEAL_TYPE_SNACK);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, 1.6f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, 0.178f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_POTASSIUM, 0.096f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CHOLESTEROL, 0.0f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, 29f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, 1.8f);
        cupcake.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, 131f);
        return cupcake;
    }

    @Override
    protected void onPostExecute(com.google.android.gms.common.api.Status status) {
        // Before querying the session, check to see if the insertion succeeded.
        if (!status.isSuccess()) {
            CharSequence msg = "There was a problem saving the session: " +
                    status.getStatusMessage();
            Log.i(TAG, msg.toString());
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        } else {
            // At this point, the session has been inserted and can be read.
            CharSequence msg = "Data saved to Google Fit!";
            Log.i(TAG, msg.toString());
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private Context getApplicationContext() {
        return mGoogleApiClient.getContext().getApplicationContext();
    }
}