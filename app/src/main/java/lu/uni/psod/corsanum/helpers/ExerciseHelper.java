package lu.uni.psod.corsanum.helpers;

import java.util.ArrayList;

import lu.uni.psod.corsanum.models.fit.Exercise;
import lu.uni.psod.corsanum.utils.ObservableList;

/**
 * Created by rlopez on 10/12/15.
 */
public class ExerciseHelper {

    public static ArrayList<Exercise> filter(ObservableList<Exercise> models, String query) {
        query = query.toLowerCase();

        final ArrayList<Exercise> filteredModelList = new ArrayList<>();
        for (Exercise model : models) {
            final String text = model.getExerciseName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
