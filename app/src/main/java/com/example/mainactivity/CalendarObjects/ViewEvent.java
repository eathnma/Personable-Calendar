package com.example.mainactivity.CalendarObjects;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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
    private GoogleMap mMap;

    //load database helped
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

        // retrieves date clicked string from previous intent
        Intent i = getIntent();
        String dateClicked = i.getStringExtra("DATECLICKED");

        // parses the date data into MYD
        String[] parser  = dateClicked.split(" ");
        String MYD = parser[0] + " " + parser[1] + " " + parser[2];

        // retrieves string array from intent
        String[] stringData = i.getExtras().getStringArray("stringdata");

        if(stringData == null){
            Log.d(TAG, "this is null");
        }
        else{
            // grabs data from String array -> into individual strings
            String title = stringData[0];
            String timeone = stringData[1];
            String timetwo = stringData[2];
            String message = stringData[3];
            String color = stringData[4];
            String location = stringData[5];

//            Log.d(TAG, "COLOR TAG: " + color);

            // place data into XML
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

                // go through cursor object and retrieve name
                Cursor data = mDatabaseHelper.getItemName(name); // returns name of database
                data.moveToNext();
                itemID = data.getInt(0);

                // DELETE item from database
                mDatabaseHelper.deleteName(itemID,name);

                // returning intent to EventsList
                int result = 1;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // if nightmode -> sharedpref = 1, run nightMode
        nightMode();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.user_list_map);
        mapFragment.getMapAsync(this);

    }

    public void nightMode(){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        night = sharedPrefs.getInt("night", 0);

        if(night == 1){
            night = sharedPrefs.getInt("night", 0);
            Log.d(TAG, String.valueOf(night));

            // if darkmode else, skip
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                setTheme(R.style.darktheme);
            }
            else setTheme(R.style.AppTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            // specialized styling
            arrow.setImageResource(R.drawable.white_arrow);
            titleView.setTextColor(Color.WHITE);
            dateView.setTextColor(Color.WHITE);
            timeView.setTextColor(Color.WHITE);
            locationView.setTextColor(Color.WHITE);
        }
    }

    // displays circle colour based on clicked event
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

    // parse address to LAT LNG
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
    }

    // google maps oncreate function
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(locationView.getText().equals("Add Location")){
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.NETWORK_PROVIDER;

            @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            double userLat = lastKnownLocation.getLatitude();
            double userLong = lastKnownLocation.getLongitude();

            LatLng loc = new LatLng(userLat, userLong);
            mMap.addMarker(new MarkerOptions().position(loc).title("Marker in Specified Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));

        } else {
            LatLng specLoc = getLocationFromAddress(getApplicationContext(),String.valueOf(locationView.getText()));
            LatLng loc = specLoc;
            mMap.addMarker(new MarkerOptions().position(loc).title("Marker in Specified Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }
    }

    //Go back to previous Activity when back button is pressed
    public void onClickBack(View v){
        int result = 1;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK);
        finish();
    }

}
