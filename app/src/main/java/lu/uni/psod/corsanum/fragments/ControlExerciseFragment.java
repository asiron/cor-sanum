package lu.uni.psod.corsanum.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import lu.uni.psod.corsanum.activities.ExerciseActivity;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.services.GoogleFitService;

public class ControlExerciseFragment extends Fragment {
    private static final String TAG = "ControlExerciseFragment";

    private static final String IS_EXERCISE_RUNNING = "exercise_running";

    private ExerciseActivity activity = null;

    private ConnectionResult mFitResultResolution;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1431;

    private TextView exerciseTitleTextView = null;

    private TextView stepCountTextView = null;
    private TextView speedTextView     = null;

    private Button stopExerciseButton = null;
    private ToggleButton startPauseExerciseButton = null;

    private boolean isExerciseRunning = false;

    public ControlExerciseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_EXERCISE_RUNNING, isExerciseRunning);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
            isExerciseRunning = savedInstanceState.getBoolean(IS_EXERCISE_RUNNING, false);

        activity = (ExerciseActivity) getActivity();

        stepCountTextView  = (TextView) activity.findViewById(R.id.step_count);
        speedTextView      = (TextView) activity.findViewById(R.id.speed);
        stopExerciseButton = (Button) activity.findViewById(R.id.stop_exercise);
        startPauseExerciseButton = (ToggleButton) activity.findViewById(R.id.start_pause_exercise);
        exerciseTitleTextView = (TextView) activity.findViewById(R.id.exercise_title);

        exerciseTitleTextView.setText(activity.getCurrentExercise().getExerciseName());

        startPauseExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    Log.i(TAG, "Starting/Resuming exercise");
                    startResumeExercise();

                } else {
                    Log.i(TAG, "Pausing exercise");
                    pauseExercise();
                }
            }
        });

        stopExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopExercise();
            }
        });

        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitStatusReceiver, new IntentFilter(GoogleFitService.FIT_NOTIFY_INTENT));
        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitStepsDataReceiver, new IntentFilter(GoogleFitService.FIT_STEPS_DATA_INTENT));
        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitLocationDataReceiver, new IntentFilter(GoogleFitService.FIT_LOCATION_DATA_INTENT));

    }

    private void startResumeExercise() {
        if (isExerciseRunning) {
            Log.i(TAG, "Resuming exercise.");
        } else {
            Log.i(TAG, "Starting new exercise.");
            startPauseExerciseButton.setTextOff(activity.getResources().getString(R.string.resume_exercise_label));
            stopExerciseButton.setVisibility(View.VISIBLE);

            startGoogleFitService();
        }
    }

    private void pauseExercise() {
        // TODO handle
    }

    private void stopExercise() {
        Intent service = new Intent(activity, GoogleFitService.class);
        activity.stopService(service);
        Log.i(TAG, "Service stopped.");

        isExerciseRunning = false;
        stopExerciseButton.setVisibility(View.INVISIBLE);
        startPauseExerciseButton.setTextOff(activity.getResources().getString(R.string.start_exercise_label));
        startPauseExerciseButton.setChecked(false);
        Log.i(TAG, "Exercise stopped.");
    }

    private void startGoogleFitService() {
        Intent service = new Intent(activity, GoogleFitService.class);
        service.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService.TYPE_REQUEST_CONNECTION);
        activity.startService(service);
    }

    private void requestFitConnection() {
        Intent intent = new Intent(GoogleFitService.FIT_LOGIN_INTENT);
        intent.putExtra(GoogleFitService.SERVICE_REQUEST_TYPE, GoogleFitService.TYPE_REQUEST_CONNECTION);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_exercise, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private BroadcastReceiver mFitStepsDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GoogleFitService.FIT_STEPS_DATA_EXTRA_STEPS)) {
                final int totalSteps = intent.getIntExtra(GoogleFitService.FIT_STEPS_DATA_EXTRA_STEPS, 0);
                stepCountTextView.setText(activity.getResources().getString(R.string.step_count) + String.valueOf(totalSteps));
            }
        }
    };

    private BroadcastReceiver mFitLocationDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LAT) &&
                    intent.hasExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LONG))
            {
                float latitude  = intent.getIntExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LAT, 0);
                float longitude = intent.getIntExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LONG, 0);


            }
        }
    };

    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.hasExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE) &&
                    intent.hasExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE)) {

                int statusCode = intent.getIntExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(GoogleFitService.FIT_EXTRA_NOTIFY_FAILED_INTENT);
                ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
                Log.d(TAG, "Fit connection failed - opening connect screen.");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(GoogleFitService.FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(TAG, "Fit connection successful - closing connect screen if it's open.");
                fitHandleConnection();
            }
        }
    };

    private void fitHandleConnection() {
        Toast.makeText(activity, "GoogleFit connected", Toast.LENGTH_SHORT).show();
    }

    private void fitHandleFailedConnection(ConnectionResult result) {
        Log.i(TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0).show();
            return;
        }

        if (!authInProgress) {
            try {
                Log.d(TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                result.startResolutionForResult(activity, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
            }
        }
    }

    private void fitActivityResult(int requestCode, int resultCode) {
        Log.i(TAG, "Inside fit actiity result");
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Fit auth completed.  Asking for reconnect.");
                requestFitConnection();
            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(activity, REQUEST_OAUTH);

                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mFitStepsDataReceiver);
        super.onDestroy();
    }
}
