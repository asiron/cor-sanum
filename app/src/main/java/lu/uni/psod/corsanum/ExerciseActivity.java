package lu.uni.psod.corsanum;

import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;
import lu.uni.psod.corsanum.fragments.ControlExerciseFragment;
import lu.uni.psod.corsanum.models.fit.Exercise;

import lu.uni.psod.corsanum.utils.MapDecorator;

public class ExerciseActivity extends BaseActivity implements OnMapReadyCallback {

    private final String TAG = "ExerciseActivity";

    private MapFragment mMapFragment = null;
    private ControlExerciseFragment mControlExerciseFragment  = null;

    private GoogleMap mMap = null;

    private HashMap<Integer, Marker> mMarkers;

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;

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

        MapDecorator md = new MapDecorator(this, mMap, mCurrentExercise.getActions());
        md.initMapDecorator();

    }


    public int getCurrentExerciseIndex() {
        return mCurrentExerciseIndex;
    }

    public Exercise getCurrentExercise() {
        return mCurrentExercise;
    }

}
