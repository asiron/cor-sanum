package lu.uni.psod.corsanum.models.fit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.fit.Action;

/**
 * Created by asiron on 12/6/15.
 */
public class Exercise {

    @SerializedName("name")
    private String mExerciseName;

    @SerializedName("actions")
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
