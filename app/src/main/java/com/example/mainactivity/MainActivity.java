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
    private int night;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Alarm Schedule
        schedule();

        getHour = Calendar.getInstance();
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //TESTING
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());
        CalendarView cv = ((CalendarView) findViewById(R.id.calendar_view));

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        if(isNightMode()){
            LinearLayout parent = findViewById(R.id.parent);
            parent.setBackgroundColor(Color.parseColor("#303437"));
            toolbar.setBackgroundColor(Color.parseColor("#383C3F"));
            toolbarTitle.setTextColor(Color.WHITE);
        }
        //sensorSetup();

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
        //finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

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

    private void askPermission(){
        if(!Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
        }
    }

    private final SensorEventListener lightSensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
//            Log.d(TAG, "HOUR: " + getHour.get(Calendar.HOUR_OF_DAY));
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                if(event.values[0] < 0.5 && (getHour.get(Calendar.HOUR_OF_DAY) > 22 || getHour.get(Calendar.HOUR_OF_DAY) < 6)) {
                    toolbar.setBackgroundColor(Color.BLACK);
                    toolbarTitle.setTextColor(Color.WHITE);
                }
                else{
                    toolbar.setBackgroundColor(Color.WHITE);
                    toolbarTitle.setTextColor(Color.BLACK);
                }

            }
        }

    };


    public void schedule(){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, PeriodicReminder.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, 3000, pendingIntent);
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
