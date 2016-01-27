package lu.uni.psod.corsanum.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Collection;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.fragments.EditActionFragment;
import lu.uni.psod.corsanum.fragments.ExerciseDetailHeaderFragment;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.ActionType;
import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.utils.map.MapDecorator;
import lu.uni.psod.corsanum.utils.ObservableList;

public class ExerciseDetailActivity extends BaseActivity implements OnMapReadyCallback,
        ExerciseDetailHeaderFragment.OnActionSelectedListener, EditActionFragment.OnEditActions {

    private final String TAG = "ExerciseDetailActivity";

    private int mCurrentExerciseIndex = 0;
    private Exercise mCurrentExercise = null;

    private MapFragment mMapFragment = null;
    private ExerciseDetailHeaderFragment mDetailFragment = null;
    private EditActionFragment mEditFragment = null;

    private GoogleMap mMap = null;

    private MapDecorator md = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        Intent intent = getIntent();
        String newExerciseIntentString = getString(R.string.create_new_exercise_intent);


        Fragment topFrag = null;
        mMapFragment = MapFragment.newInstance();

        if (intent.hasExtra(newExerciseIntentString)) {
            String newExerciseName = intent.getStringExtra(newExerciseIntentString);
            mCurrentExercise = new Exercise(newExerciseName);
            mExerciseList.add(mCurrentExercise);
            mCurrentExerciseIndex = mExerciseList.size() - 1;
            topFrag = mEditFragment  = EditActionFragment.newInstance(-1);
        } else {
            mCurrentExerciseIndex = intent.getIntExtra(getString(R.string.current_exercise_idx), 0);
            mCurrentExercise = mExerciseList.get(mCurrentExerciseIndex);
            topFrag = mDetailFragment = new ExerciseDetailHeaderFragment();
        }

        getFragmentManager().beginTransaction().add(R.id.exercise_detail_fragment_container, topFrag, "detail_frag").commit();
        getFragmentManager().beginTransaction().add(R.id.exercise_detail_map, mMapFragment, "map_frag").commit();

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
                    Action previousAction = (Action) source.get(index - 1);
                    Action nextAction = (Action) source.get(index);
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
        if (mDetailFragment != null)
            mDetailFragment.updateAdapterDataset(mExerciseList.get(mCurrentExerciseIndex).getActions());
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

    @Override
    public void onEditActionStarted(int position) {
        md.startShowingEdit(position);
    }

    @Override
    public void onEditActionFinished() {
        md.finishShowingEdit();
    }

    @Override
    public void onNewActionAdded(ActionType type, double duration) {
        md.addAction(type, duration);
    }
}


