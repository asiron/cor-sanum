package lu.uni.psod.corsanum.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import lu.uni.psod.corsanum.R;
import lu.uni.psod.corsanum.activities.ExerciseDetailActivity;

/**
 * Created by rlopez on 23/01/16.
 */

public class NewExerciseDialog extends DialogFragment {

    private static final String TAG = "NewExerciseDialog";

    private AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.new_exercise_dialog, null);
        final EditText exerciseName = (EditText) v.findViewById(R.id.new_exercise_name);

        builder.setView(v).
                setPositiveButton(R.string.create_new_exercise_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "User clicked - create!");
                        Intent i = new Intent(getActivity(), ExerciseDetailActivity.class);
                        String newExerciseName = exerciseName.getText().toString();
                        i.putExtra(getActivity().getResources().getString(R.string.create_new_exercise_intent), newExerciseName);
                        getActivity().startActivity(i);
                    }
                })
                .setNegativeButton(R.string.cancel_create_new_exercise_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "User canceled dialog!");
                        NewExerciseDialog.this.getDialog().cancel();
                    }
                });
        dialog = builder.create();

        exerciseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence c, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        return dialog;
    }

    @Override
    public void onStart(){
        super.onStart();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }
}