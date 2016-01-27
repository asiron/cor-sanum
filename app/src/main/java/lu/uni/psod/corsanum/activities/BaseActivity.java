package lu.uni.psod.corsanum.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.utils.ModelUtils;
import lu.uni.psod.corsanum.utils.ObservableList;

/**
 * Created by rlopez on 08/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected ObservableList<Exercise> mExerciseList;
    boolean mReloadExercises = false;

    public ObservableList<Exercise> getExerciseList() {
        return mExerciseList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExerciseList = ModelUtils.loadExercises(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReloadExercises)
            mExerciseList = ModelUtils.loadExercises(this);
        else
            mReloadExercises = true;
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        ModelUtils.saveExercises(this, mExerciseList);
    }
}
