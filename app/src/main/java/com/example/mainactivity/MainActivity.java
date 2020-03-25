package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    TextView toolbarTitle;
    String[] dateToday;
    String toolbarDate;

    // loading editcalendar button
    private Button editCalendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCalendarButton = (Button) findViewById(R.id.editCalendarButton);
        editCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditCalendar.class);
                startActivity(i);
            }
        });

        //TESTING
        HashSet<Date> events = new HashSet<>();
        events.add(new Date());
        CalendarView cv = ((CalendarView)findViewById(R.id.calendar_view));
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        cv.updateCalendar();

        cv.setEventHandler(new CalendarView.EventHandler()
        {
            @Override
            public void onDayClick(Date date)
            {
                // start activity
                Intent intent = new Intent(MainActivity.this, EventsList.class);
                Toast.makeText(MainActivity.this, "working", Toast.LENGTH_SHORT).show();
                intent.putExtra("TOOLBAR", toolbarDate);
                startActivity(intent);
            }
        });
        dateToday = cv.returnDate();
        setToolbarHeader(dateToday);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

//    public void editCalendarIntent(View view){
//        Intent i = new Intent(MainActivity.this, EventsList.class);
//        startActivity(i);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void setToolbarHeader(String[] dateToday){
        String suffix;
        suffix = "th";
        if(dateToday[3].contains("1") || dateToday[3].contains("31") || dateToday[3].contains("21")){
            suffix = "st";
        }
        else if(dateToday[3].contains("2") || dateToday[3].contains("22")){
            suffix = "nd";
        }
        else if(dateToday[3].contains("3") || dateToday[3].contains("23")){
            suffix = "rd";
        }
        Log.d(TAG, "DATE: " + dateToday[3]);

        toolbarDate = dateToday[1] + " " + dateToday[3] + suffix + ", " + dateToday[2];
        toolbarTitle.setText(toolbarDate);
    }

}
