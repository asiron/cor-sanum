package lu.uni.psod.corsanum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.google.gson.Gson;

import java.util.ArrayList;

import lu.uni.psod.corsanum.ExerciseDetailActivity;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.Exercise;

public class ExercisesRecyclerViewAdapter extends RecyclerSwipeAdapter<ExercisesRecyclerViewAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView textViewPos;
        TextView textViewData;
        Button buttonDelete;

        boolean canEnter;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            textViewPos = (TextView) itemView.findViewById(R.id.position);
            textViewData = (TextView) itemView.findViewById(R.id.text_data);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);

            canEnter = true;
        }
    }

    private Context mContext;
    private ArrayList<Exercise> mExerciseList;
    private ArrayList<Exercise> mFilteredExerciseList;

    protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public ExercisesRecyclerViewAdapter(Context context, ArrayList<Exercise> objects) {
        this.mContext = context;
        this.mExerciseList = objects;
        this.mFilteredExerciseList = new ArrayList<Exercise>(objects);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercises_recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final Exercise exercise = mExerciseList.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                viewHolder.canEnter = false;
            }

            @Override
            public void onClose(SwipeLayout layout) {
                viewHolder.canEnter = true;
            }

        });
        viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.canEnter == false)
                    return;

                Intent intent = new Intent(mContext, ExerciseDetailActivity.class);
                intent.putExtra(mContext.getString(R.string.current_exercise_idx), position);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(
                                (Activity) mContext,
                                (View) viewHolder.textViewData,
                                mContext.getString(R.string.exc_detail_tran_name)
                        );

                mContext.startActivity(intent, options.toBundle());
            }
        });

        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);

                removeItem(position);

                //mExerciseList.remove(position);
                //notifyItemRemoved(position);

                notifyItemRangeChanged(position, mExerciseList.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        String positionText = String.valueOf(position + 1) + ".";

        viewHolder.textViewPos.setText(positionText);
        viewHolder.textViewData.setText(exercise.getExerciseName());
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mExerciseList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void updateDataset(ArrayList<Exercise> mExerciseList) {
        this.mExerciseList = mExerciseList;
    }

    public Exercise removeItem(int position) {
        final Exercise exc = mExerciseList.remove(position);
        notifyItemRemoved(position);
        return exc;
    }

    public void addItem(int position, Exercise exc) {
        mExerciseList.add(position, exc);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Exercise exc = mExerciseList.remove(fromPosition);
        mExerciseList.add(toPosition, exc);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(ArrayList<Exercise> excs) {
        applyAndAnimateRemovals(excs);
        applyAndAnimateAdditions(excs);
        applyAndAnimateMovedItems(excs);
    }

    private void applyAndAnimateRemovals(ArrayList<Exercise> newExercises) {
        for (int i = mExerciseList.size() - 1; i >= 0; i--) {
            final Exercise exc = mExerciseList.get(i);
            if (!newExercises.contains(exc)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Exercise> newExercises) {
        for (int i = 0, count = newExercises.size(); i < count; i++) {
            final Exercise exc = newExercises.get(i);
            if (!mExerciseList.contains(exc)) {
                addItem(i, exc);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Exercise> newExercises) {
        for (int toPosition = newExercises.size() - 1; toPosition >= 0; toPosition--) {
            final Exercise exc = newExercises.get(toPosition);
            final int fromPosition = mExerciseList.indexOf(exc);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
}