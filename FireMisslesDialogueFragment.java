package com.tommybear.batcitycrossfit;

import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by dot on 8/20/2017.
 */

public class FireMisslesDialogueFragment extends DialogFragment {
    public String resultchosen;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Fire Missles?")
                .setPositiveButton("Fire", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        resultchosen = "Y";
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        resultchosen = "N";
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public String resultsgiven()
    {
       return resultchosen;
    }
}
