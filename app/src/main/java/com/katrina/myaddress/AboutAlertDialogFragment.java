package com.katrina.myaddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Ashley on 29/11/2017.
 */

public class AboutAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {



    public static AboutAlertDialogFragment newInstance(String message) 	{
        AboutAlertDialogFragment adf = new AboutAlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("alert-message", message);
        adf.setArguments(bundle);

        return adf;
    }

    @Override
    public void onAttach(Activity act) {
        // If the activity we're being attached to has
        // not implemented the OnDialogDoneListener
        // interface, the following line will throw a
        // ClassCastException. This is the earliest we
        // can test if we have a well-behaved activity.
        try {
            OnDialogDoneListener test = (OnDialogDoneListener)act;
        } catch(ClassCastException cce) {
            // Here is where we fail gracefully.
            Log.e(MyAddressList.LOGTAG, "Activity is not listening");
        }
        super.onAttach(act);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style,theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle("ABOUT")
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", this)
                .setMessage(this.getArguments().getString("alert-message"));

        return b.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        OnDialogDoneListener act = (OnDialogDoneListener) getActivity();
        boolean cancelled = false;

        if (which == AlertDialog.BUTTON_NEGATIVE)	{
            cancelled = true;
        }
        act.onDialogDone(getTag(), cancelled, "About dismissed");
    }
}
