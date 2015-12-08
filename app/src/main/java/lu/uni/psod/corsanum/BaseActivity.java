package lu.uni.psod.corsanum;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.utils.ModelUtils;

/**
 * Created by rlopez on 08/12/15.
 */
public abstract class BaseActivity extends Activity {

    protected ArrayList<Exercise> mExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExerciseList = ModelUtils.loadExercises(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        ModelUtils.saveExercises(this, mExerciseList);
    }

}
