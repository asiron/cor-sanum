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
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Timer;
import java.util.TimerTask;

import lu.uni.psod.corsanum.activities.ExerciseActivity;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.services.GoogleFitService;

public class ControlExerciseFragment extends Fragment {


    public interface OnMockEnabledListener {
        void onMockEnabled();
        void onMockDisabled();
    }

    public interface OnExerciseStateChangedListener {
        void onExerciseStart();
        void onExerciseResume();
        void onExerciseStop();
        void onExercisePause();
    }

    private static final String TAG = "ControlExerciseFragment";

    private static final String IS_EXERCISE_RUNNING = "exercise_running";

    private OnMockEnabledListener mMockEnabledCallbacks;
    private OnExerciseStateChangedListener mOnExerciseStateChangedListener;

    private ExerciseActivity activity = null;

    private ConnectionResult mFitResultResolution;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1431;

    private TextView exerciseTitleTextView = null;
    private TextView stepCountTextView     = null;
    private TextView speedTextView         = null;
    private TextView stretchTimerTextView   = null;

    private Button stopExerciseButton = null;
    private ToggleButton startPauseExerciseButton = null;

    private Switch mockEnableSwitch = null;

    private boolean isExerciseRunning = false;

    private boolean isFreeRoute = false;
    private Timer freeRouteTimer = null;
    private long timeCounted = 0;


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

        Intent intent = activity.getIntent();
        if (intent.hasExtra(ExerciseActivity.START_FREE_ROUTE)) {
            isFreeRoute = true;
        }

        if (activity.getCurrentExercise().getActions().isEmpty())
            isFreeRoute = true;

        stretchTimerTextView = (TextView) activity.findViewById(R.id.stretch_timer);
        stepCountTextView  = (TextView) activity.findViewById(R.id.step_count);
        speedTextView      = (TextView) activity.findViewById(R.id.speed);
        stopExerciseButton = (Button) activity.findViewById(R.id.stop_exercise);
        startPauseExerciseButton = (ToggleButton) activity.findViewById(R.id.start_pause_exercise);

        mockEnableSwitch = (Switch) activity.findViewById(R.id.mock_enable_switch);
        mockEnableSwitch.setChecked(false);
        mockEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mMockEnabledCallbacks.onMockEnabled();
                else
                    mMockEnabledCallbacks.onMockDisabled();
            }
        });

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
                if (!isFreeRoute) {
                    mOnExerciseStateChangedListener.onExerciseStop();
                } else {
                    stopStopwatch();
                }
                stopExercise();
            }
        });

        if (isFreeRoute) {
            mockEnableSwitch.setVisibility(View.GONE);
            exerciseTitleTextView.setText(activity.getString(R.string.free_route_label));
        }

        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitStatusReceiver, new IntentFilter(GoogleFitService.FIT_NOTIFY_INTENT));
        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitStepsDataReceiver, new IntentFilter(GoogleFitService.FIT_STEPS_DATA_INTENT));
        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitLocationDataReceiver, new IntentFilter(GoogleFitService.FIT_LOCATION_DATA_INTENT));
        LocalBroadcastManager
                .getInstance(activity)
                .registerReceiver(mFitSpeedDataReceiver, new IntentFilter(GoogleFitService.FIT_SPEED_DATA_INTENT));

    }

    private void startResumeExercise() {
        if (isExerciseRunning) {
            Log.i(TAG, "Resuming exercise.");
            if (isFreeRoute) {
                startStopwatch();
            } else
                mOnExerciseStateChangedListener.onExerciseResume();

        } else {
            Log.i(TAG, "Starting new exercise.");

            if (isFreeRoute) {
                startStopwatch();
            } else
                mOnExerciseStateChangedListener.onExerciseStart();


            startPauseExerciseButton.setTextOff(activity.getResources().getString(R.string.resume_exercise_label));
            stopExerciseButton.setVisibility(View.VISIBLE);
            isExerciseRunning = true;
            startGoogleFitService();

        }
    }

    private void pauseExercise()
    {
        if (isFreeRoute) {
            freeRouteTimer.cancel();
        } else {
            mOnExerciseStateChangedListener.onExercisePause();
        }
    }

    public void stopExercise() {
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

        try {
            mMockEnabledCallbacks = (OnMockEnabledListener) activity;
            mOnExerciseStateChangedListener = (OnExerciseStateChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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
                stepCountTextView.setText(activity.getResources().getString(R.string.step_count) + " " +  String.valueOf(totalSteps));
            }
        }
    };

    private BroadcastReceiver mFitSpeedDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GoogleFitService.FIT_SPEED_DATA_EXTRA_SPEED)) {
                final float speed = intent.getFloatExtra(GoogleFitService.FIT_SPEED_DATA_EXTRA_SPEED, 0);
                speedTextView.setText(activity.getResources().getString(R.string.speed_front_label)
                        + " " + String.format("%.2f", speed)
                        + " " + activity.getResources().getString(R.string.speed_back_label));
            }
        }
    };


    private BroadcastReceiver mFitLocationDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LAT) &&
                    intent.hasExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LONG))
            {
                float latitude  = intent.getFloatExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LAT, 0f);
                float longitude = intent.getFloatExtra(GoogleFitService.FIT_LOCATION_DATA_EXTRA_LONG, 0f);

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

    public void updateActionTitleDisplay(String title) {
        stretchTimerTextView.setText(title);
    }

    public void startTimer(long seconds, final ExerciseActivity.StrechTimerFinishedListener callback) {
        new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Log.i(TAG, "Mock Timer finished!");
                callback.onTimerFinishedCallback();
            }
        }.start();
        Log.i(TAG, "Mock Timer started for " + seconds);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mFitStepsDataReceiver);
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mFitSpeedDataReceiver);

        super.onDestroy();
    }

    private void stopStopwatch() {
        freeRouteTimer.cancel();
        timeCounted = 0;
    }


    private void startStopwatch() {
        freeRouteTimer = new Timer();
        freeRouteTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                timeCounted += 30;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        long secondsLeft = (timeCounted / 1000) % 60;
                        long minutesLeft = (timeCounted / 1000) / 60;

                        String time = String.format("%02d:%02d.%02d",
                                minutesLeft, secondsLeft, timeCounted % 100);
                        stretchTimerTextView.setText(time);

                    }
                });

            }
        },0,30);
    }
}
