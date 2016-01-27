package lu.uni.psod.corsanum.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import lu.uni.psod.corsanum.activities.ExerciseDetailActivity;
import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.models.fit.Action;
import lu.uni.psod.corsanum.models.fit.ActionType;

public class EditActionFragment extends Fragment {

    public interface OnEditActions {
        void onEditActionStarted(int index);
        void onEditActionFinished();
        void onNewActionAdded(ActionType type, double duration);

    }

    private static final String EDIT_INDEX = "EditedIndex";
    private final String TAG = "EditActionFragment";

    private ExerciseDetailActivity activity = null;
    private int editedIndex = -1;

    private LinearLayout durationPickerLayout = null;

    private NumberPicker stretchSecondsDurationPicker = null;
    private NumberPicker stretchMinutesDurationPicker = null;

    private TextView exerciseTitleTextView = null;
    private Spinner  actionTypePicker = null;
    private Button   saveButton = null;
    private Button   addButton  = null;

    private OnEditActions mEditActionsCallback;

    public static EditActionFragment newInstance(int editedAction) {

        EditActionFragment f = new EditActionFragment();

        Bundle b = new Bundle();
        b.putInt(EDIT_INDEX, editedAction);
        f.setArguments(b);
        return f;
    }

    public EditActionFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mEditActionsCallback = (OnEditActions) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_action, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editedIndex = getArguments().getInt(EDIT_INDEX, -1);

        activity = (ExerciseDetailActivity) getActivity();

        durationPickerLayout = (LinearLayout) activity.findViewById(R.id.duration_picker_layout);
        durationPickerLayout.setVisibility(View.GONE);

        exerciseTitleTextView = (TextView) activity.findViewById(R.id.exercise_edit_title);
        exerciseTitleTextView.setText(activity.getCurrentExercise().getExerciseName());

        actionTypePicker = (Spinner) activity.findViewById(R.id.exercise_edit_spinner);

        String[] secondsValues = new String[]{"0", "10", "20", "30", "40", "50",};

        NumberPicker.OnValueChangeListener pickerListener  = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (stretchSecondsDurationPicker.getValue() == 0 &&
                        stretchMinutesDurationPicker.getValue() == 0) {
                    saveButton.setEnabled(false);
                    addButton.setEnabled(false);
                } else
                    saveButton.setEnabled(true);
                    addButton.setEnabled(true);
            }
        };

        stretchSecondsDurationPicker = (NumberPicker) activity.findViewById(R.id.exercise_sec_stretch_picker);
        stretchSecondsDurationPicker.setMinValue(0);
        stretchSecondsDurationPicker.setMaxValue(5);
        stretchSecondsDurationPicker.setDisplayedValues(secondsValues);
        stretchSecondsDurationPicker.setWrapSelectorWheel(false);
        stretchSecondsDurationPicker.setOnValueChangedListener(pickerListener);

        stretchMinutesDurationPicker = (NumberPicker) activity.findViewById(R.id.exercise_min_stretch_picker);
        stretchMinutesDurationPicker.setMinValue(0);
        stretchMinutesDurationPicker.setMaxValue(10);
        stretchMinutesDurationPicker.setWrapSelectorWheel(false);
        stretchMinutesDurationPicker.setOnValueChangedListener(pickerListener);

        saveButton = (Button) activity.findViewById(R.id.exercise_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Saving
                ActionType selectedActionType = (ActionType) actionTypePicker.getSelectedItem();
                Action editedAction = activity.getCurrentExercise().getActions().get(editedIndex);
                editedAction.setActionType(selectedActionType);

                if (selectedActionType == ActionType.STRETCH)
                    editedAction.setExpectedDuration(getDurationAsFloat());

                activity.getCurrentExercise().getActions().notifyItemChanged(editedIndex);

                mEditActionsCallback.onEditActionFinished();

                ExerciseDetailHeaderFragment frag = new ExerciseDetailHeaderFragment();
                activity.getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.in_from_left, R.animator.in_from_right, R.animator.in_from_left, R.animator.in_from_right)
                        .replace(R.id.exercise_detail_fragment_container, frag).commit();
            }
        });

        addButton  = (Button) activity.findViewById(R.id.exercise_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditActionsCallback.onEditActionFinished();
                mEditActionsCallback.onNewActionAdded(
                        (ActionType) actionTypePicker.getSelectedItem(),
                        getDurationAsFloat()
                );
                editedIndex = activity.getCurrentExercise().getActions().size()-1;
                saveButton.setVisibility(View.VISIBLE);
            }
        });


        actionTypePicker.setAdapter(new ArrayAdapter<>(activity, R.layout.action_spinner, ActionType.values()));
        actionTypePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ActionType.values()[position] == ActionType.STRETCH) {
                    durationPickerLayout.setVisibility(View.VISIBLE);
                    saveButton.setEnabled(false);
                    addButton.setEnabled(false);
                } else {
                    durationPickerLayout.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    addButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });


        if (editedIndex != -1) {
            Log.i(TAG, "Starting editing on index : " + String.valueOf(editedIndex));
            mEditActionsCallback.onEditActionStarted(editedIndex);
            ActionType at = activity.getCurrentExercise().getActions().get(editedIndex).getActionType();

            for (int i=0; i<ActionType.values().length; ++i)
                if (ActionType.values()[i] == at) {
                    actionTypePicker.setSelection(i);
                    break;
                }

        } else {
            saveButton.setVisibility(View.GONE);
        }
    }

    private double getDurationAsFloat() {
        return stretchMinutesDurationPicker.getValue() * 60.0 +
            stretchSecondsDurationPicker.getValue() * 10.0;
    }
}
