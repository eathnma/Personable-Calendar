package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mainactivity.CalendarObjects.CalendarView;
import com.example.mainactivity.CalendarObjects.EventsList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private String[] dateToday;
    private String toolbarDate;
    private Calendar getHour;
    private SensorManager mySensorManager;
    private Sensor lightSensor;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        //Flag to determine if user defaults the themes to automatic
        if(!sharedPrefs.contains("flag")){
            //1 for day, 2 for night, 0 for user default
            editor.putInt("flag", 0);
            editor.commit();

        }
        //Schedules the periodic notifications
        schedule();

        getHour = Calendar.getInstance();
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Instantiating Calendar View class
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());
        CalendarView cv = ((CalendarView) findViewById(R.id.calendar_view));

        //Toolbar instantiation
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        //Check if NightMode is enabled
        if(isNightMode()){
            LinearLayout parent = findViewById(R.id.parent);
            parent.setBackgroundColor(Color.parseColor("#303437"));
            toolbar.setBackgroundColor(Color.parseColor("#383C3F"));
            toolbarTitle.setTextColor(Color.WHITE);
        }
        sensorSetup();

        //Updating CalendarView to check if events are scheduled
        cv.updateCalendar();
        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayClick(Date date) {
                // start activity
                Intent intent = new Intent(MainActivity.this, EventsList.class);
                Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("TOOLBAR", toolbarDate);
                intent.putExtra("DATE_CLICKED", date.toString());
                startActivity(intent);
            }
        });
        dateToday = cv.returnDate();

        setToolbarHeader(dateToday);
    }

    @Override
    protected void onPause() {
        super.onPause();
        askPermission();
    }

    @Override
    protected void onResume(){

        super.onResume();
    }

    //Determine the suffix for the day in Toolbar
    public void setToolbarHeader(String[] dateToday){
        String suffix;

        if(dateToday[3].contentEquals("01") || dateToday[3].contentEquals("31") || dateToday[3].contentEquals("21")){
            suffix = "st";
        }
        else if(dateToday[3].contentEquals("02") || dateToday[3].contentEquals("22")){
            suffix = "nd";
        }
        else if(dateToday[3].contentEquals("03") || dateToday[3].contentEquals("23")){
            suffix = "rd";
        }
        else{
            suffix = "th";
        }

        toolbarDate = dateToday[1] + " " + dateToday[3] + suffix + ", " + dateToday[2];
        toolbarTitle.setText(toolbarDate);
    }

    //Start user settings Activity
    public void startSettings(View v){
        Intent intent = new Intent(this, UserSettings.class);
        startActivity(intent);
    }

    public void sensorSetup(){
        if(lightSensor != null){
            mySensorManager.registerListener(
                    lightSensorListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    //If the user does not have "display over other apps" enabled, then ask for permission
    private void askPermission(){
        if(!Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
        }
    }

    //Setting up lightSensor
    private final SensorEventListener lightSensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                if(event.values[0] < 60 && sharedPrefs.contains("flag")) {
                    Log.d(TAG, Float.toString(event.values[0]));
                    if(sharedPrefs.getInt("flag", 0) != 2){
                        editor.putInt("flag", 2);
                        editor.putInt("night", 1);
                        editor.commit();
                        recreate();
                    }
                }
                else if(event.values[0] > 60 && sharedPrefs.contains("flag")) {
                    if(sharedPrefs.getInt("flag", 0) != 1 ){
                        editor.putInt("flag", 1);
                        editor.putInt("night", 0);
                        editor.commit();
                        recreate();
                    }
                }

            }
        }

    };

    //Schedules the periodic reminder of the notification
    public void schedule(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, PeriodicReminder.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, 5000, pendingIntent);
    }

    //Will return true if nightmode is enabled
    private boolean isNightMode(){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        if(sharedPrefs.getInt("night", 0) == 1){
            return true;
        }
        return false;
    }

}
