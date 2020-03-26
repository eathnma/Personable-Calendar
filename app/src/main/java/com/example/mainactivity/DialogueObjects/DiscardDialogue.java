package com.example.mainactivity.DialogueObjects;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;



public class DiscardDialogue extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Discard Activity?");
        builder.setMessage("All information will not be saved");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
//                SharedPreferences sharedPrefs = getSharedPreferences("DiscardBoolean", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPrefs.edit();

//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });

        return builder.create();
    }
}
