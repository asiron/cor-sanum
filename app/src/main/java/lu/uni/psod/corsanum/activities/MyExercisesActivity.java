package lu.uni.psod.corsanum.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.utils.NewExerciseDialog;
import lu.uni.psod.corsanum.utils.recyclerviews.DividerItemDecoration;
import lu.uni.psod.corsanum.utils.recyclerviews.ExercisesRecyclerViewAdapter;

public class MyExercisesActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ExercisesRecyclerViewAdapter mAdapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_exercises);

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

    }

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
            DialogFragment dialog = new NewExerciseDialog();
            dialog.show(getFragmentManager(), "NewExerciseDialog");
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
        mAdapter.filter(query);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}