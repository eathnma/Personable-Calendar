package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CalendarView cv = ((CalendarView)findViewById(R.id.calendar_view));
        cv.updateCalendar();

    }
}
