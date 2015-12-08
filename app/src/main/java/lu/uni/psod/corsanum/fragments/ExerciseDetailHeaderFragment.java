package lu.uni.psod.corsanum.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.utils.ActionsRecyclerViewAdapter;
import lu.uni.psod.corsanum.utils.DividerItemDecoration;
import lu.uni.psod.corsanum.utils.ExercisesRecyclerViewAdapter;

/**
 * Created by rlopez on 08/12/15.
 */
public class ExerciseDetailHeaderFragment extends Fragment {

    Gson gson = null;
    TextView tv_title = null;
    Exercise exercise = null;

    Activity activity = null;

    private RecyclerView actionsRecyclerView;
    private RecyclerView.Adapter actionsRecyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();

        gson = new Gson();
        String exercise_json = activity.getIntent().getStringExtra("exercise");
        exercise = gson.fromJson(exercise_json, Exercise.class);

        Log.i("AAA", gson.toJson(exercise));


        actionsRecyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view_actions);

        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // Item Decorator:
        actionsRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        actionsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        actionsRecyclerViewAdapter = new ActionsRecyclerViewAdapter(activity, exercise.getActions());
        ((ActionsRecyclerViewAdapter) actionsRecyclerViewAdapter).setMode(Attributes.Mode.Single);
        actionsRecyclerView.setAdapter(actionsRecyclerViewAdapter);

        /* Listeners */
        actionsRecyclerView.setOnScrollListener(onScrollListener);

        tv_title = (TextView) activity.findViewById(R.id.exercise_detail_title);
        tv_title.setText(exercise.getExerciseName());
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };

}
