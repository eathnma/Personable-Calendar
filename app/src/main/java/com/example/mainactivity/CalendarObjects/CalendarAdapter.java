package com.example.mainactivity.CalendarObjects;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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
    private ArrayList<Integer> monthWithEvents;

    public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays,
                           Calendar calendar, ArrayList<Integer> dateWithEvents,
                           ArrayList<Integer> monthWithEvents, ArrayList<String>color) {

        super(context, R.layout.custom_calendar_day, days);
        inflater = LayoutInflater.from(context);
        this.eventDays = eventDays;
        this.calendarCompare = (Calendar) calendar.clone();
        this.dateWithEvents = dateWithEvents;
        this.color = color;
        this.monthWithEvents = monthWithEvents;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // day in question
        Calendar calendar = Calendar.getInstance();
        Date date = getItem(position);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);

        // today
        Date today = new Date();
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(today);

        // inflate item if it does not exist yet
        if (view == null) {
            view = inflater.inflate(R.layout.custom_calendar_day, parent, false);
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        int bdayMonth = -1;
        int bday = 0;
        boolean bdayIsSet = false;

        if (sharedPref.contains("birthday month") && sharedPref.contains("birthday day")) {
            bdayMonth = sharedPref.getInt("birthday month", -1);
            bday = sharedPref.getInt("birthday day", 0);
        }

        if(isNightMode()){
            Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.text_view_circle);
            ((GradientDrawable) background).setColor(Color.parseColor("#494949"));
            background = ContextCompat.getDrawable(getContext(), R.drawable.text_view_circle_selected);
            ((GradientDrawable) background).setColor(Color.parseColor("#494949"));
        }
        else {
            Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.text_view_circle);
            ((GradientDrawable) background).setColor(Color.parseColor("#EDEDED"));
            background = ContextCompat.getDrawable(getContext(), R.drawable.text_view_circle_selected);
            ((GradientDrawable) background).setColor(Color.parseColor("#EDEDED"));
        }

        //If the calendar currently being viewed is the current month
        if(calendarToday.get(Calendar.MONTH) == calendarCompare.get(Calendar.MONTH)){
            //Check for birthday
            if (month != calendarToday.get(Calendar.MONTH)) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(Color.parseColor("#d9d9d9"));
                if(isNightMode()){
                    ((TextView) view).setTextColor(Color.parseColor("#999999"));
                }
            }
            else if (day == calendarToday.get(Calendar.DATE)) {
                // if it is today, set it to blue/bold
                ((TextView) view).setTextColor(Color.BLACK);
                if(isNightMode()){
                    ((TextView)view).setTextColor(Color.WHITE);
                }
                ((TextView) view).setGravity(Gravity.CENTER);

                //Iterate through arraylist that contains days with events.
                view.setBackgroundResource(R.drawable.text_view_circle_selected);

                for(int i = 0; i < dateWithEvents.size(); i++){
                    if(day == dateWithEvents.get(i) && month == monthWithEvents.get(i)){
                        Drawable background = view.getBackground();
                        ((TextView) view).setTextColor(Color.WHITE);

                        decideColorWithStroke(color.get(i), background);

                        decideColor(color.get(i),view);
                        break;
                    }
                }
                if(day == bday && month == bdayMonth) {
                    ((TextView) view).setText("");
                    view.setBackgroundResource(R.drawable.cake_day);
                    bdayIsSet = true;
                }
            }
            else {
                // !*** CHECK FOR EVENT **
                ((TextView) view).setTextColor(Color.parseColor("#969696"));
                view.setBackgroundResource(R.drawable.text_view_circle);

                //Iterate through arraylist that contains days with events. (For days that isn't the current day)
                for(int i = 0; i < dateWithEvents.size(); i++){
                    if(day == dateWithEvents.get(i) && month == monthWithEvents.get(i)){
                        ((TextView) view).setTextColor(Color.WHITE);
                        decideColor(color.get(i),view);
                        break;
                    }
                }
                if (day == bday && month == bdayMonth) {
                    ((TextView) view).setText("");
                    view.setBackgroundResource(R.drawable.cake);
                    bdayIsSet = true;
                }

            }

        }
        else{
            //Checks when you check through other months that isnt the current one
            if (month != calendarCompare.get(Calendar.MONTH)) {
                // if this day is outside current month, grey it out
                ((TextView) view).setTextColor(Color.parseColor("#d9d9d9"));
            }
            else {
                // !*** CHECK FOR EVENT **
                ((TextView) view).setTextColor(Color.parseColor("#969696"));
                view.setBackgroundResource(R.drawable.text_view_circle);
                //Iterate through all the dates that have events
                for(int i = 0; i < dateWithEvents.size(); i++){
                    if(day == dateWithEvents.get(i) && month == monthWithEvents.get(i)){
                        ((TextView) view).setTextColor(Color.WHITE);
                        decideColor(color.get(i),view);
                        break;
                    }
                }

                if (day == bday && month == bdayMonth) {
                    ((TextView) view).setText("");
                    view.setBackgroundResource(R.drawable.cake);
                    bdayIsSet = true;
                }
            }
        }
        // set text
        if(bdayIsSet){
            ((TextView) view).setText("");
        }
        else {
            ((TextView) view).setText(String.valueOf(calendar.get(Calendar.DATE)));
        }
        return view;
    }

    private void decideColor(String color, View view){
        if(color != null && view != null) {
            if (color.contentEquals("@colors/boxColor1")) {
                view.setBackgroundResource(R.drawable.blue_circle);
            }
            else if (color.contentEquals("@colors/boxColor2")) {
                view.setBackgroundResource(R.drawable.red_circle);
            }
            else if (color.contentEquals("@colors/boxColor3")) {
                view.setBackgroundResource(R.drawable.yellow_circle);
            }
            else if (color.contentEquals("@colors/boxColor4")) {
                view.setBackgroundResource(R.drawable.light_blue_circle);
            }
            else if (color.contentEquals("@colors/boxColor5")) {
                view.setBackgroundResource(R.drawable.orange_circle);
            }
            else if (color.contentEquals("@colors/boxColor6")) {
                view.setBackgroundResource(R.drawable.green_circle);
            }
        }
        else if(color == null){
            view.setBackgroundResource(R.drawable.green_circle);
        }
    }

    private void decideColorWithStroke(String color, Drawable view) {
        if (view instanceof ShapeDrawable && color != null && view != null) {
            if (color.contentEquals("@colors/boxColor1")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor1));
            }
            else if (color.contentEquals("@colors/boxColor2")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor2));
            }
            else if (color.contentEquals("@colors/boxColor3")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor3));
            }
            else if (color.contentEquals("@colors/boxColor4")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor4));
            }
            else if (color.contentEquals("@colors/boxColor5")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor5));
            }
            else if (color.contentEquals("@colors/boxColor6")) {
                ((ShapeDrawable) view).getPaint().setColor(ContextCompat.getColor(getContext(), R.color.boxColor6));
            }
        }
    }

    //Will return true if nightmode is enabled
    private boolean isNightMode(){
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        if(sharedPrefs.getInt("night", 0) == 1){
            return true;
        }
        return false;
    }
}
