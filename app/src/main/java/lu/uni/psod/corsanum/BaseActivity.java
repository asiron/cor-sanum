package lu.uni.psod.corsanum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.utils.ModelUtils;

/**
 * Created by rlopez on 08/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ArrayList<Exercise> mExerciseList;
    boolean mReloadExercises = false;

    public ArrayList<Exercise> getExerciseList() {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("CorSanum-base-activity", "onPause");
        ModelUtils.saveExercises(this, mExerciseList);
    }
}
