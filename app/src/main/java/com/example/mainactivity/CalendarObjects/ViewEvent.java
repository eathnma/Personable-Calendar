package com.example.mainactivity.CalendarObjects;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewEvent extends AppCompatActivity {

    private static final String TAG = "ViewEvent";

    //layout row 1
    private ImageView arrow;
    private ImageView garbageButton;

    //layout row 2
    private ImageView circleView;
    private TextView titleView;

    //layout row 3
    private TextView dateView;
    private TextView timeView;
    private TextView descriptionView;

    //layout row 4
    private TextView locationView;

    //layout row 5
    private View mapView;

    MyDatabaseHelper mDatabaseHelper = new MyDatabaseHelper(this);
    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // layout row 1
        arrow = findViewById(R.id.arrow);

        // layout row 2
        circleView = findViewById(R.id.circleView);
        titleView = findViewById(R.id.titleView);

        // layout row 3
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);
        descriptionView = findViewById(R.id.descriptionView);

        // layout row 4
        locationView = findViewById(R.id.locationView);

        Intent i = getIntent();
        String dateClicked = i.getStringExtra("DATECLICKED");

        String[] parser  = dateClicked.split(" ");
        String MYD = parser[0] + " " + parser[1] + " " + parser[2];

        String[] stringData = i.getExtras().getStringArray("stringdata");
        Log.d(TAG, "STRING DATA " +  Arrays.toString(stringData));

        if(stringData == null){
            Log.d(TAG, "this is null");
        }
        else{

            String title = stringData[0];
            String timeone = stringData[1];
            String timetwo = stringData[2];
            String message = stringData[3];
            String color = stringData[4];
            String location = stringData[5];

            // within the string
            titleView.setText(title);
            timeView.setText(timeone + " - " + timetwo);
            descriptionView.setText(message);
            locationView.setText(location);

            // set date (change format of this soon)
            dateView.setText(MYD);
        }

        // layout row 5
        mapView = findViewById(R.id.mapView);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri locationString = Uri.parse("geo:0,0?q=" + Uri.encode(String.valueOf(locationView.getText())));

                Intent intent = new Intent(Intent.ACTION_VIEW, locationString);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // delete the database object
        garbageButton = findViewById(R.id.garbageButton);
        garbageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(titleView.getText());
                Log.d(TAG, "onItemClick: You clicked on " + name);

                Cursor data = mDatabaseHelper.getItemName(name); // returns name of database
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }

                if(itemID >  -1){
                    Log.d(TAG,"onItemClick: the ID IS " + itemID);
                }
                else{
                    Log.d(TAG, "NO ID ASSOCIATED WITH NAME");
                }

                mDatabaseHelper.deleteName(itemID,name);
                Log.d(TAG, "deleted " + itemID + " & "+ name + " from database");

                int result = 1;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK);
                finish();

            }
        });
    }


}
