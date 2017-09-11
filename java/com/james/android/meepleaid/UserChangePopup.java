package com.james.android.meepleaid;

import android.app.Activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.R.attr.id;

/**
 * Created by 100599223 on 9/8/2017.
 */

public class UserChangePopup extends DialogFragment{
    public  static String mUsername;
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */

    public interface NoticeDialogListener{
           public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialogFragment);

        public void usernameDialog(String Username);

    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            Log.d("onAttach", "Activity opened");
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("onAttach", "Context opened");
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View main_view = inflater.inflate(R.layout.changeusername,null);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(main_view);
        builder.setMessage("Set Username");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //confirmation
                final EditText userEdit = (EditText) main_view.findViewById(R.id.popupUserEdit);
                mUsername = userEdit.getText().toString();
                Log.d("setpositivebutton", mUsername);
                if(mListener != null) {
                    Log.d("setpositivebutton", "mlistener not null!!");
                    mListener.onDialogPositiveClick(UserChangePopup.this);
                } else {
                    Log.d("setpositivebutton", "mlistener IS null!!");
                }

            // mListener.usernameDialog(mUsername);
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel the dialog
          }
        });
        //create the alertdialog object and return it
        return builder.create();

    }
}
