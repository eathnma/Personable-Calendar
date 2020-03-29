package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mainactivity.CalendarObjects.CalendarView;
import com.example.mainactivity.CalendarObjects.EventsList;
import com.example.mainactivity.CalendarObjects.ViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    TextView toolbarTitle;
    String[] dateToday;
    String toolbarDate;
    MenuItem settings;
    Calendar getHour;
    private SensorManager mySensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHour = Calendar.getInstance();
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //TESTING
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());
        CalendarView cv = ((CalendarView)findViewById(R.id.calendar_view));
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        sensorSetup();

        cv.updateCalendar();
        cv.setEventHandler(new CalendarView.EventHandler()
        {
            @Override
            public void onDayClick(Date date)
            {
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

    public void setToolbarHeader(String[] dateToday){
        String suffix;

        if(dateToday[3].contentEquals("1") || dateToday[3].contentEquals("31") || dateToday[3].contentEquals("21")){
            suffix = "st";
        }
        else if(dateToday[3].contentEquals("2") || dateToday[3].contentEquals("22")){
            suffix = "nd";
        }
        else if(dateToday[3].contentEquals("3") || dateToday[3].contentEquals("23")){
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

    private final SensorEventListener lightSensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "HOUR: " + getHour.get(Calendar.HOUR_OF_DAY));
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

}
