package com.example.mainactivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarAdapter extends ArrayAdapter<Date> {
    // for view inflation
    private LayoutInflater inflater;
    private HashSet<Date> eventDays;
    private static final String TAG = "CalendarAdapter";

    public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays) {
        super(context, R.layout.custom_calendar_day, days);
        this.eventDays = eventDays;
        inflater = LayoutInflater.from(context);
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
        // clear styling
        if (month != calendarToday.get(Calendar.MONTH) || year != calendarToday.get(Calendar.YEAR)) {
            // if this day is outside current month, grey it out
            ((TextView) view).setTextColor(Color.parseColor("#d9d9d9"));
        }
        else if (day == calendarToday.get(Calendar.DATE)) {
            // if it is today, set it to blue/bold
            ((TextView)view).setTextColor(Color.BLACK);
            ((TextView)view).setGravity(Gravity.CENTER);
            view.setBackgroundResource(R.drawable.text_view_circle_selected);
        }
        else{
            ((TextView)view).setTextColor(Color.parseColor("#969696"));
            view.setBackgroundResource(R.drawable.text_view_circle);
        }

        // set text
        ((TextView)view).setText(String.valueOf(calendar.get(Calendar.DATE)));

        return view;
    }
}
