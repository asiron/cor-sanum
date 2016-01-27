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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exercise exercise = (Exercise) o;

        if (mExerciseName != null ? !mExerciseName.equals(exercise.mExerciseName) : exercise.mExerciseName != null)
            return false;

        for (int i=0; i<mActions.size(); ++i) {
            if (!mActions.get(i).equals(exercise.getActions().get(i)))
                return false;
        }

        return !(mActions != null ? !mActions.equals(exercise.mActions) : exercise.mActions != null);

    }
}
