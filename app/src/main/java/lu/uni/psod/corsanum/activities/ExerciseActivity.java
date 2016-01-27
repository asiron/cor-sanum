package lu.uni.psod.corsanum.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.fragments.ControlExerciseFragment;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.ActionType;
import lu.uni.psod.corsanum.models.fit.Exercise;

import lu.uni.psod.corsanum.models.fit.Position;
import lu.uni.psod.corsanum.utils.ObservableList;
import lu.uni.psod.corsanum.utils.map.LocationSourceMock;
import lu.uni.psod.corsanum.utils.map.MapController;
import lu.uni.psod.corsanum.utils.map.MapUtils;

public class ExerciseActivity extends BaseActivity implements OnMapReadyCallback,
        ControlExerciseFragment.OnMockEnabledListener, MapController.MyLocationChangedListener,
        ControlExerciseFragment.OnExerciseStateChangedListener
{

    public interface StrechTimerFinishedListener {
        void onTimerFinishedCallback();
    }

    private final String TAG = "ExerciseActivity";
    public static final String START_FREE_ROUTE = "StartFreeRoute";


    private MapFragment mMapFragment = null;
    private ControlExerciseFragment mControlExerciseFragment  = null;

    private GoogleMap mMap = null;
    private LocationSourceMock mLSM = null;

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;
    private MapController mMC = null;

    private Action currentRunningAction = null;
    private int currentRunningActionIndex = 0;

    private boolean isExerciseRunning = false;
    private boolean isExercisePaused  = false;
    private boolean isTimerRunning    = false;
    private long timerTimeLeft = 0;

    private boolean isFreeRoute = false;

    CountDownTimer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        isFreeRoute = getIntent().getBooleanExtra(START_FREE_ROUTE, false);

        mCurrentExerciseIndex = getIntent().getIntExtra(getString(R.string.current_exercise_idx), 0);
        mCurrentExercise = mExerciseList.get(mCurrentExerciseIndex);

        mControlExerciseFragment = (ControlExerciseFragment) getFragmentManager()
                .findFragmentById(R.id.control_exercise);

        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.control_exercise_map);

        mMapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (isFreeRoute)
            mMC =  new MapController(this, mMap, new ObservableList<Action>(), true);
        else
            mMC = new MapController(this, mMap, mCurrentExercise.getActions(), true);

        mLSM = new LocationSourceMock(mMC, new LocationSourceMock.OnExerciseStageChangedListener() {

            boolean mockTimerRunning = false;

            @Override
            public void onPartialRouteStarted(int actionIndex) {
                Log.i(TAG, "Action started: " + String.valueOf(actionIndex));
                Action currentAction = getCurrentExercise().getActions().get(actionIndex);
                if (currentAction.getActionType() == ActionType.STRETCH)
                {
                    Log.i(TAG, "Starting Stretching session!");
                    mControlExerciseFragment.startTimer((long) currentAction.getExpectedDuration(), new StrechTimerFinishedListener() {
                        @Override
                        public void onTimerFinishedCallback() {
                           // mMC.trySetMock(mLSM);
                            mLSM.resume();
                            mockTimerRunning = false;
                        }
                    });
                    mockTimerRunning = true;
                }
            }

            @Override
            public void onPartialRouteCompleted(int actionIndex) {
                Log.i(TAG, "Action finished: " + String.valueOf(actionIndex));
                Action actionFinished = getCurrentExercise().getActions().get(actionIndex);
                if (actionFinished.getActionType() == ActionType.STRETCH && isTimerRunning) {
                    mLSM.pause();
                   // mMC.disableMock();
                }
            }

            @Override
            public void onFullRouteCompleted() {
                Log.i(TAG, "Exercise finished");


            }
        });
        mMC.initMapController();
        mMC.setMyLocationChangedCallback(this);
        mMC.setFollowPosition(true, 100);
        mMap.setMyLocationEnabled(true);

        if (isFreeRoute)
            mMC.setFollowPosition(true, 1);
    }

    public int getCurrentExerciseIndex() {
        return mCurrentExerciseIndex;
    }
    public Exercise getCurrentExercise() {
        return mCurrentExercise;
    }

    @Override
    public void onMockEnabled() {
        mMC.trySetMock(mLSM);
        mLSM.resume();
    }

    @Override
    public void onMockDisabled() {
        mLSM.pause();
        mMC.disableMock();
    }

    @Override
    public void onMyLocationChanged(LatLng loc) {

        if (isExerciseRunning && !isExercisePaused) {
            Position goalPos = currentRunningAction.getEndPos();
            LatLng   goalLatLng = new LatLng(goalPos.getLat(), goalPos.getLong());

            double distance = MapUtils.distBetween(loc, goalLatLng);
            if(distance < 20.0) {

                Log.i(TAG, "Close enough to the goal!");

                if (currentRunningAction.getActionType() == ActionType.STRETCH) {

                    if (!isTimerRunning)
                        nextGoal();

                } else {
                    nextGoal();
                }

            }
        }
    }

    @Override
    public void onExerciseStart() {
        isExerciseRunning = true;
        isExercisePaused  = false;
        currentRunningActionIndex = 0;
        currentRunningAction = mCurrentExercise.getActions().get(0);

        for (int i=0; i<mMC.getActionCount(); ++i)
            mMC.getItem(i).setMarkerColor(BitmapDescriptorFactory.HUE_RED);

        startNewGoal();
    }

    @Override
    public void onExerciseResume() {
        isExercisePaused = false;
        if (isTimerRunning) {
            mTimer = createTimer(timerTimeLeft);
            mTimer.start();
        }
    }

    @Override
    public void onExerciseStop() {
        if (isTimerRunning) {
            mTimer.cancel();
        }
        isTimerRunning = false;
        isExerciseRunning = false;
        isExercisePaused  = false;
        mControlExerciseFragment.stopExercise();
        mControlExerciseFragment.updateActionTitleDisplay("");
    }

    @Override
    public void onExercisePause() {
        if (isTimerRunning) {
            mTimer.cancel();
        }
        isExercisePaused = true;
    }

    private void nextGoal() {
        if (currentRunningActionIndex+1 >= mCurrentExercise.getActions().size()) {
            mControlExerciseFragment.
                    updateActionTitleDisplay(getString(R.string.exercise_done));
            mMC.getItem(currentRunningActionIndex).
                    setMarkerColor(BitmapDescriptorFactory.HUE_GREEN);
            onExerciseStop();

        } else {

            currentRunningActionIndex++;
            currentRunningAction = mCurrentExercise.getActions().get(currentRunningActionIndex);

            startNewGoal();
        }
    }

    private void startNewGoal() {

        if (currentRunningAction.getActionType() == ActionType.STRETCH) {
            // start timer
            // update display

            isTimerRunning = true;
            mTimer = createTimer((long) currentRunningAction.getExpectedDuration() * 1000);
            mTimer.start();


        } else {
            //update display
            mControlExerciseFragment.updateActionTitleDisplay(currentRunningAction.getActionType().getName());
        }

        mMC.getItem(currentRunningActionIndex).setFirstMarkerColor(BitmapDescriptorFactory.HUE_GREEN);

        //update marker to yellow the second one
        if (currentRunningActionIndex < mCurrentExercise.getActions().size()-1) {
            mMC.getItem(currentRunningActionIndex+1).
                    setFirstMarkerColor(BitmapDescriptorFactory.HUE_YELLOW);

        } else {
            mMC.getItem(currentRunningActionIndex).
                    setSecondMarkerColor(BitmapDescriptorFactory.HUE_YELLOW);
        }
    }

    private CountDownTimer createTimer(long time) {
        return new CountDownTimer(time, 16) {

            public void onTick(long millisUntilFinished) {

                // Save in case we pause
                timerTimeLeft = millisUntilFinished;

                long secondsLeft = (millisUntilFinished / 1000) % 60;
                long minutesLeft = (millisUntilFinished / 1000) / 60;

                String time = String.format("%02d:%02d.%02d",
                        minutesLeft, secondsLeft, millisUntilFinished%100);

                mControlExerciseFragment.updateActionTitleDisplay(
                        getResources().getString(R.string.stretch_label_front) + " " + time);
            }

            public void onFinish() {
                isTimerRunning = false;
                mControlExerciseFragment.updateActionTitleDisplay(
                        getResources().getString(R.string.go_to_next_point));
                Log.i(TAG, "Timer finished!");
            }
        };
    }
}


