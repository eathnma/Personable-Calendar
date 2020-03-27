package com.example.mainactivity.CalendarObjects;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarAdapter extends ArrayAdapter<Date> {
    // for view inflation
    private LayoutInflater inflater;
    private HashSet<Date> eventDays;
    private Calendar calendarCompare;
    private static final String TAG = "CalendarAdapter";
    private ArrayList<Integer> dateWithEvents;
    private ArrayList<String> color;

    public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays, Calendar calendar, ArrayList<Integer> dateWithEvents, ArrayList<String>color) {
        super(context, R.layout.custom_calendar_day, days);
        this.eventDays = eventDays;
        inflater = LayoutInflater.from(context);
        this.calendarCompare = (Calendar) calendar.clone();
        this.dateWithEvents = dateWithEvents;
        this.color = color;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // day in question
        Calendar calendar = Calendar.getInstance();
        Date date = getItem(position);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // today
        Date today = new Date();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(today);

        // inflate item if it does not exist yet
        if (view == null) {
            view = inflater.inflate(R.layout.custom_calendar_day, parent, false);
        }

        if(calendarToday.get(Calendar.MONTH) == calendarCompare.get(Calendar.MONTH)) {
            // clear styling
            if (month != calendarToday.get(Calendar.MONTH)) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(Color.parseColor("#d9d9d9"));
            } else if (day == calendarToday.get(Calendar.DATE)) {
                // if it is today, set it to blue/bold
                ((TextView) view).setTextColor(Color.BLACK);
                ((TextView) view).setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.text_view_circle_selected);
            } else {
                // !*** CHECK FOR EVENT **
                ((TextView) view).setTextColor(Color.parseColor("#969696"));
                view.setBackgroundResource(R.drawable.text_view_circle);

                //Iterate through arraylist that contains days with events. (For days that isn't the current day)
                for(int i = 0; i < dateWithEvents.size(); i++){
                    Log.d(TAG, "DAY " + day + " " + "ALIST " + dateWithEvents.get(i));
                    if(day == dateWithEvents.get(i)){
                        ((TextView) view).setTextColor(Color.WHITE);
                        Log.d(TAG, "COLOR: " + color.get(i));
                        decideColor(color.get(i),view);
                        break;
                    }
                }

            }
        }
        else{
            //Checks when you check through other months that isnt the current one
            if (month != calendarCompare.get(Calendar.MONTH)) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(Color.parseColor("#d9d9d9"));
            } else {
                // !*** CHECK FOR EVENT **
                ((TextView) view).setTextColor(Color.parseColor("#969696"));
                view.setBackgroundResource(R.drawable.text_view_circle);
            }
        }

        // set text
        ((TextView)view).setText(String.valueOf(calendar.get(Calendar.DATE)));

        return view;
    }

    private void decideColor(String color, View view){
        if(color.contentEquals("@colors/boxColor1")){
            view.setBackgroundResource(R.drawable.blue_circle);
        }
        else if(color.contentEquals("@colors/boxColor2")){
            view.setBackgroundResource(R.drawable.red_circle);
        }
        else if(color.contentEquals("@colors/boxColor3")){
            view.setBackgroundResource(R.drawable.yellow_circle);
        }
        else if(color.contentEquals("@colors/boxColor4")){
            view.setBackgroundResource(R.drawable.light_blue_circle);
        }
        else if(color.contentEquals("@colors/boxColor5")){
            view.setBackgroundResource(R.drawable.orange_circle);
        }
        else if(color.contentEquals("@colors/boxColor6")){
            view.setBackgroundResource(R.drawable.green_circle);
        }
    }


}
