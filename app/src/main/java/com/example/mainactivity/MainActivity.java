package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.mainactivity.CalendarObjects.CalendarView;
import com.example.mainactivity.CalendarObjects.EventsList;
import com.example.mainactivity.CalendarObjects.ViewEvent;

import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    TextView toolbarTitle;
    String[] dateToday;
    String toolbarDate;
    private Button viewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("TOOLBAR", toolbarDate);
                intent.putExtra("DATE_CLICKED", date.toString());
                startActivity(intent);
            }
        });
        dateToday = cv.returnDate();
        setToolbarHeader(dateToday);

        viewActivity = (Button) findViewById(R.id.viewActivity);
        viewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ViewEvent.class);
                startActivity(i);
            }
        });

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

        Log.d(TAG, "DATE: " + dateToday[3]);

        toolbarDate = dateToday[1] + " " + dateToday[3] + suffix + ", " + dateToday[2];
        toolbarTitle.setText(toolbarDate);
    }

}
