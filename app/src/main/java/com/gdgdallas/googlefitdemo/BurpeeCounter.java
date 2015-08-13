package com.gdgdallas.googlefitdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.gdgdallas.googlefitdemo.data.BurpeeLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
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


public class BurpeeCounter extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BurpeeCounter";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    private DatePicker mDatePicker;
    private EditText mCounter;
    private Button mBtnSubmit;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.burpeecounter);

        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }

        mCounter = (EditText) findViewById(R.id.editText);
        mBtnSubmit = (Button) findViewById(R.id.button);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Editable mCounterText = mCounter.getText();
                final Integer count = Integer.parseInt(mCounterText.toString());
                BurpeeLog burpeeLog = new BurpeeLog(System.currentTimeMillis(), count);
                onSubmitCount(burpeeLog);
            }
        });

    }

    private void onSubmitCount(BurpeeLog burpeeLog) {
        Toast
            .makeText(this, R.string.snackbar_text, Toast.LENGTH_LONG)
            .show(); // Don’t forget to show!

        new SaveWorkoutTask().execute(burpeeLog);
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)

                    .addApi(Fitness.SESSIONS_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))

                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        // TODO: Start making API requests.
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }



    class SaveWorkoutTask extends AsyncTask<BurpeeLog, Void, Status> {
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
                Toast.makeText(BurpeeCounter.this, msg, Toast.LENGTH_LONG).show();
            } else {
                // At this point, the session has been inserted and can be read.
                CharSequence msg = "Session saved to Google Fit!";
                Log.i(TAG, msg.toString());
                Toast.makeText(BurpeeCounter.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
