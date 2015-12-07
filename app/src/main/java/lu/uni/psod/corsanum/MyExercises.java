package lu.uni.psod.corsanum;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.swipe.util.Attributes;

import lu.uni.psod.corsanum.models.Action;
import lu.uni.psod.corsanum.models.ActionType;
import lu.uni.psod.corsanum.models.Exercise;
import lu.uni.psod.corsanum.models.Position;
import lu.uni.psod.corsanum.utils.DividerItemDecoration;
import lu.uni.psod.corsanum.utils.RecyclerViewAdapter;

//import com.daimajia.swipedemo.adapter.util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class MyExercises extends Activity {

    /**
     * RecyclerView: The new recycler view replaces the list view. Its more modular and therefore we
     * must implement some of the functionality ourselves and attach it to our recyclerview.
     * <p/>
     * 1) Position items on the screen: This is done with LayoutManagers
     * 2) Animate & Decorate views: This is done with ItemAnimators & ItemDecorators
     * 3) Handle any touch events apart from scrolling: This is now done in our adapter's ViewHolder
     */

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private ArrayList<String> mDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setTitle("RecyclerView");
            }
        }

        ArrayList<Exercise> exerciseList = new ArrayList<Exercise>();

        Exercise ex1 = new Exercise("Long run in Park");
        ex1.getActions().add(new Action(new Position(0,0), new Position(1,1), 0.0, ActionType.RUN));
        ex1.getActions().add(new Action(new Position(1,1), new Position(2,2), 0.0, ActionType.WALK_FAST));
        ex1.getActions().add(new Action(new Position(2,2), new Position(2,2), 2.0, ActionType.STRETCH));

        Exercise ex2 = new Exercise("Stretching session");
        ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));
        ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));
        ex2.getActions().add(new Action(new Position(0,0), new Position(0,0), 5.0, ActionType.STRETCH));

        Exercise ex3 = new Exercise("Sprinting");
        ex3.getActions().add(new Action(new Position(0,0), new Position(2,2), 0.0, ActionType.RUN));
        ex3.getActions().add(new Action(new Position(2,2), new Position(4,4), 0.0, ActionType.RUN_FAST));
        ex3.getActions().add(new Action(new Position(4,4), new Position(6,6.5), 0.0, ActionType.RUN));
        ex3.getActions().add(new Action(new Position(6,6.5), new Position(9,9), 0.0, ActionType.RUN_FAST));
        ex3.getActions().add(new Action(new Position(9,9), new Position(9,9), 10.0, ActionType.STRETCH));

        exerciseList.add(ex1);
        exerciseList.add(ex2);
        exerciseList.add(ex3);

        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mAdapter = new RecyclerViewAdapter(this, exerciseList);
        ((RecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);
    }

    /**
     * Substitute for our onScrollListener for RecyclerView
     */
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long

        return super.onOptionsItemSelected(item);
    }
}