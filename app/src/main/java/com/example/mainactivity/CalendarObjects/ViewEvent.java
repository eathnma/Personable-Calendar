package com.example.mainactivity.CalendarObjects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.mainactivity.Database.Constants.MAPVIEW_BUNDLE_KEY;

public class ViewEvent extends AppCompatActivity implements OnMapReadyCallback {

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

    //for shared preferences dark mode
    private int night;

    //implementing maps
    private MapView mMapView;


    MyDatabaseHelper mDatabaseHelper = new MyDatabaseHelper(this);

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

            Log.d(TAG, "COLOR TAG: " + color);

            // within the string
            titleView.setText(title);
            timeView.setText(timeone + " - " + timetwo);
            descriptionView.setText(message);
            locationView.setText(location);
            decideColor(color, circleView);
            // set date (change format of this soon)
            dateView.setText(MYD);
        }

        // layout row 5
        mapView = findViewById(R.id.user_list_map);
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
                int itemID;

                Cursor data = mDatabaseHelper.getItemName(name); // returns name of database

                data.moveToNext();
                itemID = data.getInt(0);

                mDatabaseHelper.deleteName(itemID,name);

                int result = 1;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK);
                finish();

            }
        });

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        night = sharedPrefs.getInt("night", 0);

        if(night == 1){
            night = sharedPrefs.getInt("night", 0);
            Log.d(TAG, String.valueOf(night));

            // if darkmode else, skip
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                setTheme(R.style.darktheme);
            } else setTheme(R.style.AppTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            // specialized styling
            arrow.setImageResource(R.drawable.white_arrow);
            titleView.setTextColor(Color.WHITE);
            dateView.setTextColor(Color.WHITE);
            timeView.setTextColor(Color.WHITE);
            locationView.setTextColor(Color.WHITE);
        }
    }

    public void decideColor(String color, View v){
        if(color.contentEquals("@colors/boxColor1")){
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_circle));
        }
        else if(color.contentEquals("@colors/boxColor2")){
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.red_circle));
        }
        else if(color.contentEquals("@colors/boxColor3")){
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.yellow_circle));
        }
        else if(color.contentEquals("@colors/boxColor4")){
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.light_blue_circle));
        }
        else if(color.contentEquals("@colors/boxColor5")){
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.orange_circle));
        }
        else{
            v.setBackground(ContextCompat.getDrawable(this, R.drawable.green_circle));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_event, container, false);
        mMapView = view.findViewById(R.id.user_list_map);

        initGoogleMap(savedInstanceState);

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



}
