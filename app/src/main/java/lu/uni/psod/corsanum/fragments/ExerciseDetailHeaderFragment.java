package lu.uni.psod.corsanum.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lu.uni.psod.corsanum.R;

/**
 * Created by rlopez on 08/12/15.
 */
public class ExerciseDetailHeaderFragment extends Fragment{

    TextView tv_title = null;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String title = getActivity().getIntent().getStringExtra("title");
        tv_title = (TextView) getActivity().findViewById(R.id.exercise_detail_title);
        tv_title.setText(title);
    }
}
