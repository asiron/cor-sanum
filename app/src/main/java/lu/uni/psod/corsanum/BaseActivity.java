package lu.uni.psod.corsanum;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.utils.ModelUtils;

/**
 * Created by rlopez on 08/12/15.
 */
public abstract class BaseActivity extends Activity {

    protected ArrayList<Exercise> mExerciseList;

    public ArrayList<Exercise> getExerciseList() {
        return mExerciseList;
    }

    public void setExerciseList(ArrayList<Exercise> exerciseList) {
        this.mExerciseList = exerciseList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExerciseList = ModelUtils.loadExercises(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("A", "SAVED!!");
        ModelUtils.saveExercises(this, mExerciseList);
    }

}
