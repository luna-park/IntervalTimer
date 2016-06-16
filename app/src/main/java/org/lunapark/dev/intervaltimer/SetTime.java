package org.lunapark.dev.intervaltimer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by znak on 13.06.2016.
 */
public class SetTime extends DialogFragment {

    // Use this instance of the interface to deliver action events
    DialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        final NumberPicker npMinutes = (NumberPicker) view.findViewById(R.id.npMinutes);
        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);
        npMinutes.setFormatter(formatter);
        npMinutes.setWrapSelectorWheel(true);
        npMinutes.setValue(getArguments().getInt("Min"));


        final NumberPicker npSeconds = (NumberPicker) view.findViewById(R.id.npSeconds);
        npSeconds.setMaxValue(59);
        npSeconds.setMinValue(0);
        npSeconds.setFormatter(formatter);
        npSeconds.setWrapSelectorWheel(true);
        npSeconds.setValue(getArguments().getInt("Sec"));


        final int interval = getArguments().getInt("Interval");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(getString(R.string.txt_set) + " " + interval + " " + getString(R.string.txt_hint));
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        int time = npMinutes.getValue() * 60 + npSeconds.getValue();
                        mListener.onDialogPositiveClick(SetTime.this, time, interval);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
//                        mListener.onDialogNegativeClick(SetTime.this);
                    }
                });
//                // Add action buttons
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        SetTime.this.getDialog().cancel();
//                    }
//                });
        return builder.create();
    }


    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int time, int interval);
    }
}
