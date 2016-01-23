package lu.uni.psod.corsanum;

import android.os.Bundle;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lu.uni.psod.corsanum.fragments.ExerciseDetailHeaderFragment;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.models.fit.Position;
import lu.uni.psod.corsanum.utils.MapDecorator;
import lu.uni.psod.corsanum.utils.ObservableList;

public class    ExerciseDetailActivity extends BaseActivity
        implements OnMapReadyCallback, ExerciseDetailHeaderFragment.OnActionSelectedListener
{

    private final String TAG = "ExerciseDetailActivity";

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;

    private MapFragment mMapFragment                     = null;
    private ExerciseDetailHeaderFragment mHeaderFragment = null;

    private GoogleMap mMap = null;

    private MapDecorator md = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        mCurrentExerciseIndex = getIntent().getIntExtra(getString(R.string.current_exercise_idx),0);
        mCurrentExercise = mExerciseList.get(mCurrentExerciseIndex);

        mHeaderFragment = (ExerciseDetailHeaderFragment) getFragmentManager()
                        .findFragmentById(R.id.exercise_detail_header);

        mMapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.exercise_detail_map);


        mMapFragment.getMapAsync(this);

        mCurrentExercise.getActions().addListener(new ObservableList.Listener() {
            @Override
            public void onItemsAdded(ObservableList source, Collection items) {

            }

            @Override
            public void onItemsRemoved(ObservableList source, Collection items) {

            }

            @Override
            public void onStructuralChange(ObservableList source) {

            }

            @Override
            public void onSingleItemRemoved(ObservableList source, int index) {
                Log.i(TAG, "Item " + String.valueOf(index) + " removed!");

                if (index == 0 || index >= source.size()) {
                    Log.i(TAG, "Removed element was at the edge, no model change required!");
                } else {
                    Action previousAction = (Action) source.get(index-1);
                    Action nextAction     = (Action) source.get(index);
                    previousAction.setEndPos(nextAction.getStartPos());
                }

                md.deleteAction(index);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        md = new MapDecorator(this, mMap, mCurrentExercise.getActions());
        md.initMapDecorator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeaderFragment.updateAdapterDataset(mExerciseList.get(mCurrentExerciseIndex).getActions());
    }

    public int getCurrentExerciseIndex() {
        return mCurrentExerciseIndex;
    }

    public Exercise getCurrentExercise() {
        return mCurrentExercise;
    }

    @Override
    public void onActionSelected(int position) {
        md.selectPartialRoute(position);
    }


}
