package com.example.mainactivity.DialogueObjects;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.mainactivity.CalendarObjects.EventsList;


public class DiscardDialogue extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Discard Activity?");
        builder.setMessage("All information will not be saved.");
        builder.setNegativeButton(Html.fromHtml("<font color='#969696'>Cancel</font>"), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                //destroys the dialogue object
                onDestroy();
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#edbe2f'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
             Intent intent= new Intent (getActivity(), EventsList.class);
             startActivity(intent);

//           Toast.makeText(getActivity(), "this works", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }


}
