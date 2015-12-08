package lu.uni.psod.corsanum.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.Exercise;

/**
 * Created by rlopez on 08/12/15.
 */
public class ModelUtils {

    private static final Gson GSON = new Gson();
    private static final Type EXERCISE_LIST_TYPE = new TypeToken<ArrayList<Exercise>>(){}.getType();

    public static String getSavedData(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void saveData(Context ctx, String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveExercises(Context ctx, ArrayList<Exercise> exercises) {
        saveData(ctx, ctx.getString(R.string.saved_exercise_list), GSON.toJson(exercises));
    }

    public static ArrayList<Exercise> loadExercises(Context ctx) {
        return (ArrayList<Exercise>) GSON.fromJson(
                getSavedData(ctx, ctx.getString(R.string.saved_exercise_list)),
                EXERCISE_LIST_TYPE
        );
    }
}
