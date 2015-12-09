package lu.uni.psod.corsanum;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;

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

public class MyExercises extends BaseActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ExercisesRecyclerViewAdapter mAdapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recyclerview);

        toolbar = (Toolbar) findViewById(R.id.exercise_tool_bar);

        setSupportActionBar(toolbar);

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

        mAdapter.getFilter().filter("Long");

        // Listeners
        // recyclerView.setOnScrollListener(onScrollListener);

    }

    /*
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
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_add_exercise){
            Toast.makeText(this, "Implement Add! ", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.updateDataset(mExerciseList);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final ArrayList<Exercise> filteredModelList = filter(mExerciseList, query);
        Log.i("CECECE", new Gson().toJson(filteredModelList));
        mAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private ArrayList<Exercise> filter(ArrayList<Exercise> models, String query) {
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