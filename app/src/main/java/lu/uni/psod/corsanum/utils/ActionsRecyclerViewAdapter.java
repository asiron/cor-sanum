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
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.actions_swipe_layout);
            textViewPos = (TextView) itemView.findViewById(R.id.action_position);
            textViewData = (TextView) itemView.findViewById(R.id.action_title);
            buttonDelete = (Button) itemView.findViewById(R.id.delete_action);
            buttonEdit   = (Button) itemView.findViewById(R.id.edit_action);
            surface = (LinearLayout) itemView.findViewById(R.id.action_surface);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + textViewData.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + textViewData.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            canEnter = true;
        }
    }

    private Context mContext;
    private ArrayList<Action> mDataset;

    protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public ActionsRecyclerViewAdapter(Context context, ArrayList<Action> objects) {
        this.mContext = context;
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
            public void onOpen(SwipeLayout layout) {
                //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                Log.i("SwipeLayoutAction", "open");
                Log.i("JSON", (new Gson()).toJson(mDataset));
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.i("SwipeLayoutAction", "update");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                Log.i("SwipeLayoutAction", "hand release");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i("SwipeLayoutAction", "start open");
                viewHolder.canEnter = false;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.i("SwipeLayoutAction", "start close");
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

                mSelectedPosition = position;
                notifyDataSetChanged();
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
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
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
        viewHolder.textViewPos.setText((position + 1) + ".");
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