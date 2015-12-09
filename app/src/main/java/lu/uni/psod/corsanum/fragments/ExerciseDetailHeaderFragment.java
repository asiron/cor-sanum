package lu.uni.psod.corsanum.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import lu.uni.psod.corsanum.ExerciseDetailActivity;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.Action;
import lu.uni.psod.corsanum.utils.ActionsRecyclerViewAdapter;
import lu.uni.psod.corsanum.utils.DividerItemDecoration;

/**
 * Created by rlopez on 08/12/15.
 */
public class ExerciseDetailHeaderFragment extends Fragment {

    TextView exerciseTitleTextView = null;
    Button startExerciseButton = null;

    ExerciseDetailActivity activity = null;

    private RecyclerView actionsRecyclerView;
    private ActionsRecyclerViewAdapter actionsRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (ExerciseDetailActivity) getActivity();

        actionsRecyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view_actions);

        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // Item Decorator:
        actionsRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        actionsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        actionsRecyclerViewAdapter = new ActionsRecyclerViewAdapter(activity,
               activity.getCurrentExercise().getActions());

        ((ActionsRecyclerViewAdapter) actionsRecyclerViewAdapter).setMode(Attributes.Mode.Single);
        actionsRecyclerView.setAdapter(actionsRecyclerViewAdapter);

        exerciseTitleTextView = (TextView) activity.findViewById(R.id.exercise_detail_title);
        exerciseTitleTextView.setText(activity.getCurrentExercise().getExerciseName());

        startExerciseButton = (Button) activity.findViewById(R.id.start_exercise);
        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(activity, "Implement Exercise Activity! ", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void updateAdapterDataset(ArrayList<Action> objects) {
        actionsRecyclerViewAdapter.updateDataset(objects);
    }

}
