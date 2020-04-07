package com.example.mainactivity;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class PeriodicReminder extends IntentService {
    private static final String TAG = "PeriodicReminder";
    private MyDatabase db;
    private String[] eventDetails;
    private ArrayList<String[]> events;
    private Calendar calendar;


    public PeriodicReminder() {
        super("PeriodicReminder");
        db = new MyDatabase(this);
        eventDetails = null;
        events = new ArrayList<>();
        calendar = Calendar.getInstance();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(checkTime()){
            sortHourly();
            filterEvent();
        }

        if(events.size() > 0){
            //start overlay
            Intent overlayIntent = new Intent(this, Overlay.class);
            if(sharedPrefs.contains("message") && sharedPrefs.getString("message", null) != null){
                if(!(sharedPrefs.getString("message", null)).contentEquals(events.get(0)[3]) ||
                      calendar.get(Calendar.HOUR_OF_DAY) < hourToInt(events.get(0)[4])){
                    editor.putString("message", events.get(0)[3]);
                    editor.putString("messageColor", events.get(0)[2]);
                    editor.commit();
                    Log.d("PeriodicReminder", "COMMIT");
                    startService(overlayIntent);
                }

            }
            else{
                editor.putString("message", events.get(0)[3]);
                editor.commit();
                startService(overlayIntent);
            }

        }

    }

    private boolean checkTime(){
        Cursor cursor = db.getData();
        int index0 = cursor.getColumnIndex(Constants.DATECLICKED);
        int index1 = cursor.getColumnIndex(Constants.COLOR);
        int index2 = cursor.getColumnIndex(Constants.TITLE);
        int index3 = cursor.getColumnIndex(Constants.TIMEONE);


        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                eventDetails = new String[5];
                eventDetails[0] = cursor.getString(index0);
                String date[];

                date = eventDetails[0].split(" ");
                eventDetails[0] = date[1]; // Month
                eventDetails[1] = date[2]; // Day
                eventDetails[2] = cursor.getString(index1); //color
                eventDetails[3] = cursor.getString(index2); //message
                eventDetails[4] = cursor.getString(index3); //time one
                events.add(eventDetails);
                cursor.moveToNext();
            }
            return true;
        }
        return false;
    }

    private void filterEvent(){
        int size = events.size();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int index = 0;

        while(index < size) {
            if (checkMonth(events.get(index)[0]) == month) {
                if (!(Integer.parseInt(events.get(index)[1]) == day)) {
                    events.remove(index);
                    size = events.size();
                }
                else{
                    index++;
                }
            }
            else{
                events.remove(index);
                size = events.size();
            }
        }
    }

    //Sorts events arraylist by hour
    private void sortHourly(){
        ArrayList<String[]> holderArrayList = new ArrayList<>();
        String[] smallest;

        for(int i = 0; i < events.size(); i++){
            smallest = events.get(i);
            for(int j = i + 1; j < events.size(); j++){
                if(hourToInt(smallest[4]) > hourToInt(events.get(j)[4])){
                    smallest = events.get(j);
                }
            }
            holderArrayList.add(smallest);

        }

        int index = 0;
        int size = holderArrayList.size();
        while(index < size){
            if(hourToInt(holderArrayList.get(index)[4]) < calendar.get(Calendar.HOUR_OF_DAY)){
                holderArrayList.remove(index);
                size = holderArrayList.size();
            }
            else{
                index++;
            }
        }

        events = new ArrayList<>();
        events = holderArrayList;

    }

    //Convert the time (format 0:00 am/pm) into an int for comparison
    private int hourToInt(String hour){
        String intHour = hour.substring(0, 2);
        if(hour.charAt(1) == ':') {
            intHour = hour.substring(0, 1);
        }

        int hr = 0;
        int hourAdd = Integer.parseInt(intHour);
        if(hour.charAt(hour.length() - 2) == 'P'){
            hr = 12;
        }
        return hr + hourAdd;
    }

    //Converts the string of each month to an int that is parsable by Calendar class
    private int checkMonth(String month){
        if(month.contentEquals("Jan")){
            return 0;
        }
        else if(month.contentEquals("Feb")){
            return 1;
        }
        else if(month.contentEquals("Mar")){
            return 2;
        }
        else if(month.contentEquals("Apr")){
            return 3;
        }
        else if(month.contentEquals("May")){
            return 4;
        }
        else if(month.contentEquals("Jun")){
            return 5;
        }
        else if(month.contentEquals("Jul")){
            return 6;
        }
        else if(month.contentEquals("Aug")){
            return 7;
        }
        else if(month.contentEquals("Sep")){
            return 8;
        }
        else if(month.contentEquals("Oct")){
            return 9;
        }
        else if(month.contentEquals("Nov")){
            return 10;
        }
        return 11;
    }



}
