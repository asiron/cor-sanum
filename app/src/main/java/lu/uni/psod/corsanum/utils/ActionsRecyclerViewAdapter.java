package lu.uni.psod.corsanum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import lu.uni.psod.corsanum.fragments.ExerciseDetailHeaderFragment;
import lu.uni.psod.corsanum.models.Action;
import lu.uni.psod.corsanum.models.Exercise;

public class ActionsRecyclerViewAdapter extends RecyclerSwipeAdapter<ActionsRecyclerViewAdapter.SimpleViewHolder> {

    private int mSelectedPosition = -1;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        private TextView textViewPos;
        private TextView textViewData;
        private Button buttonDelete;
        private Button buttonEdit;

        private LinearLayout surface;

        boolean canEnter;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            canEnter = true;

            surface      = (LinearLayout) itemView.findViewById(R.id.action_surface);
            swipeLayout  = (SwipeLayout) itemView.findViewById(R.id.actions_swipe_layout);
            textViewPos  = (TextView) itemView.findViewById(R.id.action_position);
            textViewData = (TextView) itemView.findViewById(R.id.action_title);
            buttonDelete = (Button) itemView.findViewById(R.id.delete_action);
            buttonEdit   = (Button) itemView.findViewById(R.id.edit_action);

        }
    }

    private ExerciseDetailHeaderFragment mContext;
    private ArrayList<Action> mDataset;

    protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public ActionsRecyclerViewAdapter(ExerciseDetailHeaderFragment context, ArrayList<Action> objects) {
        this.mContext = context;
        this.mDataset = objects;
    }

    public void updateDataset(ArrayList<Action> objects) {
        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final String exerciseName = mDataset.get(position).getActionType().toString();
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i("SwipeLayoutAction", "start open");
                viewHolder.canEnter = false;
            }

            @Override
            public void onClose(SwipeLayout layout) {
                Log.i("SwipeLayoutAction", "close");
                viewHolder.canEnter = true;
            }

        });
        viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.canEnter == false)
                    return;

                mContext.getActionSelectedCallback().onActionSelected(position);
                mSelectedPosition = position;
                notifyDataSetChanged();
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext.getActivity(), "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                mSelectedPosition = -1;
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Trying to edit " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();

            }
        });

        if (position == mSelectedPosition && mSelectedPosition != -1) {
            viewHolder.surface.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColorSelected));
        } else {
            viewHolder.surface.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundColor));
        }

        String positionText = String.valueOf(position + 1) + ".";

        viewHolder.textViewPos.setText(positionText);
        viewHolder.textViewData.setText(exerciseName);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.actions_swipe_layout;
    }
}