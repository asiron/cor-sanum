package lu.uni.psod.corsanum.models;

import java.util.List;

/**
 * Created by asiron on 12/6/15.
 */
public class Exercise {

    private String mExerciseName;
    private List<Action> mActions;

    public Exercise(String mExerciseName) {
        this.mExerciseName = mExerciseName;
    }

    public List<Action> getActions() {
        return mActions;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public void setExerciseName(String name) {
        this.mExerciseName = name;
    }
}
