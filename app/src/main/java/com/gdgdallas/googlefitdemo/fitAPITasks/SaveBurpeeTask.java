package com.gdgdallas.googlefitdemo.fitAPITasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gdgdallas.googlefitdemo.data.BurpeeLog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.WorkoutExercises;
import com.google.android.gms.fitness.request.SessionInsertRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by luke.wallace on 11/17/15.
 */
public class SaveBurpeeTask extends AsyncTask<BurpeeLog, Void, Status> {

    private final String TAG = this.getClass().getCanonicalName();
    private GoogleApiClient mGoogleApiClient;

    public SaveBurpeeTask(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    protected void onPreExecute() {}

    protected com.google.android.gms.common.api.Status doInBackground(BurpeeLog... logs) {

        final BurpeeLog log = logs[0];

        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("Burpee Workout")
                .setActivity(FitnessActivities.AEROBICS)
                .setStartTime(log.getEstimatedStartTime(), TimeUnit.MILLISECONDS)
                .setEndTime(log.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();

        DataSource exerciseSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setAppPackageName(getApplicationContext())
                .setType(DataSource.TYPE_RAW)
                .build();


        DataPoint burpees = DataPoint.create(exerciseSource);
        burpees.setTimestamp(log.getTimeInMillis(), TimeUnit.MILLISECONDS);
        burpees.getValue(Field.FIELD_EXERCISE).setString(WorkoutExercises.BURPEE);
        burpees.getValue(Field.FIELD_REPETITIONS).setInt(log.getCount());
        burpees.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(Field.RESISTANCE_TYPE_BODY);

        DataSet dataSet = DataSet.create(exerciseSource);
        dataSet.add(burpees);

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(dataSet)
                .build();

        // Then, invoke the Sessions API to insert the session and await the result,
        // which is possible here because of the AsyncTask. Always include a timeout when
        // calling await() to avoid hanging that can occur from the service being shutdown
        // because of low memory or other conditions.
        Log.d(TAG, "Inserting the Session via the Google Fit History API...");
        return Fitness.SessionsApi.insertSession(mGoogleApiClient, insertRequest)
                .await(1, TimeUnit.MINUTES);
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
            CharSequence msg = "Session saved to Google Fit!";
            Log.i(TAG, msg.toString());
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private Context getApplicationContext() {
        return mGoogleApiClient.getContext().getApplicationContext();
    }
}
