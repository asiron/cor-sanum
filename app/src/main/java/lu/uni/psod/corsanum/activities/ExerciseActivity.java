package lu.uni.psod.corsanum.activities;

import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.fragments.ControlExerciseFragment;
import lu.uni.psod.corsanum.models.fit.Exercise;

import lu.uni.psod.corsanum.utils.map.LocationSourceMock;
import lu.uni.psod.corsanum.utils.map.MapController;

public class ExerciseActivity extends BaseActivity implements OnMapReadyCallback,
        ControlExerciseFragment.OnMockEnabledListener {

    private final String TAG = "ExerciseActivity";

    private MapFragment mMapFragment = null;
    private ControlExerciseFragment mControlExerciseFragment  = null;

    private GoogleMap mMap = null;
    private LocationSourceMock mLSM = null;

    private HashMap<Integer, Marker> mMarkers;

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;
    private MapController mMC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);


        mMarkers = new HashMap<Integer, Marker>();

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
        mMC = new MapController(this, mMap, mCurrentExercise.getActions(), true);
        mLSM = new LocationSourceMock(mMC, new LocationSourceMock.OnPartialRouteCompletedListener() {
            @Override
            public void onPartialRouteCompleted() {

            }

            @Override
            public void onFullRouteCompleted() {

            }
        });
        mMC.initMapController();
        mMC.setFollowPosition(true);
        mMap.setMyLocationEnabled(true);
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
}
