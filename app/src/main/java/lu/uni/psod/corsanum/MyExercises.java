package lu.uni.psod.corsanum;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.daimajia.swipe.util.Attributes;

import lu.uni.psod.corsanum.models.Action;
import lu.uni.psod.corsanum.models.ActionType;
import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.models.Position;
import lu.uni.psod.corsanum.utils.DividerItemDecoration;
import lu.uni.psod.corsanum.utils.ExercisesRecyclerViewAdapter;

//import com.daimajia.swipedemo.adapter.util.DividerItemDecoration;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import lu.uni.psod.corsanum.utils.ModelUtils;

public class MyExercises extends BaseActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        // Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Layout Managers
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter
        mAdapter = new ExercisesRecyclerViewAdapter(this, mExerciseList);
        ((ExercisesRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        // Listeners
        recyclerView.setOnScrollListener(onScrollListener);
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
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}