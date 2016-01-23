package lu.uni.psod.corsanum.models.fit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.utils.ObservableList;

/**
 * Created by asiron on 12/6/15.
 */
public class Exercise {

    @SerializedName("name")
    private String mExerciseName;

    @SerializedName("actions")
    private ObservableList<Action> mActions;

    public Exercise() {
        this("");
    }

    public Exercise(String mExerciseName) {
        this.mActions = new ObservableList<Action>();
        this.mExerciseName = mExerciseName;
    }

    public ObservableList<Action> getActions() {
        return mActions;
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public void setExerciseName(String name) {
        this.mExerciseName = name;
    }
}
