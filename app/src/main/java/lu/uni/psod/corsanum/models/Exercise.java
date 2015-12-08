package lu.uni.psod.corsanum.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asiron on 12/6/15.
 */
public class Exercise {

    private String mExerciseName;
    private ArrayList<Action> mActions;

    public Exercise() {
        this("");
    }

    public Exercise(String mExerciseName) {
        this.mActions = new ArrayList<Action>();
        this.mExerciseName = mExerciseName;
    }

    public ArrayList<Action> getActions() {
        return mActions;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public void setExerciseName(String name) {
        this.mExerciseName = name;
    }
}
