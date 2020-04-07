package com.example.mainactivity.CalendarObjects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.MainActivity;
import com.example.mainactivity.Overlay;
import com.example.mainactivity.R;

import java.util.ArrayList;
import java.util.Arrays;

public class EventsList extends AppCompatActivity {
    private static final String TAG = "EventsList";
    private int width, height;

    private TextView hour;
    private String toolbarDate;
    private ScrollView scrollView;

    private LinearLayout parentContainer;
    private LinearLayout timeline;
    private LinearLayout boxParent;
    private LinearLayout box;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout.LayoutParams layoutParams2;

    private RelativeLayout footer;
    private FrameLayout frameLayout;
    private LinearLayout eventLayout;

    private Resources r;
    private Toolbar toolbar;
    private String dateClicked;
    private String[] parser;
    private TextView toolbarTitle;
    private Intent intent;
    private MyDatabase db;
    private MyDatabaseHelper helper;
    private ImageView arrow;

    private ArrayList<String[]> mArrayList = new ArrayList<>();
    private String[] s;

    private int night;

    private static final int LAUNCH_EDIT_CALENDAR = 1;
    private static final int LAUNCH_VIEW_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        stopService(new Intent(this, Overlay.class));
        intent = getIntent();

        //Variables that decide the day (1-31) pressed
        dateClicked = intent.getStringExtra("DATE_CLICKED");
        if(dateClicked != null) {
            parser = dateClicked.split(" ");
        }

        //database
        db = new MyDatabase(this);
        helper = new MyDatabaseHelper(this);

        //Instantiating toolbar elements
        toolbarDate = intent.getStringExtra("TOOLBAR");
        toolbar = findViewById(R.id.actionBar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(toolbarDate);
        arrow = toolbar.findViewById(R.id.arrow);
        arrow.setImageResource(R.drawable.arrow);
        footer = findViewById(R.id.footer);

        //Toolbar stuff
        if(isNightMode()){
            toolbarTitle.setTextColor(Color.WHITE);
            toolbar.setBackgroundColor(Color.parseColor("#383C3F"));
            footer.setBackgroundColor(Color.parseColor("#383C3F"));
            arrow.setImageResource(R.drawable.white_arrow);
        }

        //Scrollview containers
        scrollView = findViewById(R.id.scrollViewContainer);
        r = getResources();
        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics()));
        height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics()));

        //Layout parameter variables
        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        //Layout Assignment
        setContainers();
        fillLayout();

        scrollView.addView(parentContainer);
    }

    private void fillLayout(){
        for(int i = 1; i < 12; i++){
            setHourParams(i, "am", i);
            if(i == 0){
                setHourParams(12 , "am", i);
            }

            //Add the boxes to the box parent
            instantiateBoxes(i);
            boxParent.addView(box);
            //Add the times to the timeline
            timeline.addView(hour);
        }
        for(int i = 0; i < 13; i++){
            setHourParams(i , "pm", i + 12);
            if(i == 12) {
                setHourParams(i + 12);
            }
            else if(i == 0){
                setHourParams(12, "pm", i + 12);
            }
            instantiateBoxes(i);
            boxParent.addView(box);
            timeline.addView(hour);
        }
    }

    //Set hour parameters for each hour textView
    private void setHourParams(int setHour, String ampm, int id){
        hour = new TextView(this);
        if(isNightMode()){
            hour.setTextColor(Color.parseColor("#ffffff"));
        }
        hour.setLayoutParams(layoutParams);
        hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        hour.setGravity(Gravity.CENTER_VERTICAL);
        hour.setId(id);
        hour.setText(setHour + " " + ampm);
    }

    //Sets an empty box for hour textView (for 12am -start and 12am -end)
    private void setHourParams(int id){
        hour = new TextView(this);
        hour.setLayoutParams(layoutParams);
        hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        hour.setGravity(Gravity.CENTER_VERTICAL);
        hour.setId(id);
        hour.setText(" ");
    }

    //Instantiate a box which determines where the events will be displayed
    private void instantiateBoxes(int id){
        box = new LinearLayout(this);
        box.setLayoutParams(layoutParams2);
        box.setId(id);
        box.setBackground(ContextCompat.getDrawable(this, R.drawable.background_stroke));
        if(isNightMode()){
            box.setBackground(ContextCompat.getDrawable(this, R.drawable.background_stroke_night));
        }
        box.setOrientation(LinearLayout.HORIZONTAL);

    }

    //Container to all the boxes
    private void setContainers(){
        //first child container to scrollview
        parentContainer = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentContainer.setLayoutParams(lp);
        parentContainer.setTag("parentContainer");

        //FrameLayout instantiation
        frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(lp);

        //container to boxes
        boxParent = new LinearLayout(this);
        boxParent.setLayoutParams(lp);
        boxParent.setOrientation(LinearLayout.VERTICAL);
        boxParent.setTag("boxes");

        //container to timeline
        timeline = new LinearLayout(this);
        lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeline.setLayoutParams(lp);

        int paddingTop = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
        timeline.setPadding(0, paddingTop, 0, -paddingTop);
        timeline.setOrientation(LinearLayout.VERTICAL);
        timeline.setTag("timeline");

        //add timeline and boxes to parent container

        if(isNightMode()){
            parentContainer.setBackgroundColor(Color.parseColor("#303437"));
        }
        parentContainer.addView(timeline);
        frameLayout.addView(boxParent);
        displayEvent();
        parentContainer.addView(frameLayout);
    }

    //next intent
    public void clickAddEvent(View v){
        Intent editCalendarIntent = new Intent(this, EditCalendar.class);
        editCalendarIntent.putExtra("DATECLICKED", dateClicked);
        startActivityForResult(editCalendarIntent, LAUNCH_EDIT_CALENDAR);
    }

    //Pull event data from database
    private void displayEvent(){
        Cursor cursor = db.getData();

        int index0 = cursor.getColumnIndex(Constants.DATECLICKED);
        int index1 = cursor.getColumnIndex(Constants.TITLE);
        int index2 = cursor.getColumnIndex(Constants.TIMEONE);
        int index3 = cursor.getColumnIndex(Constants.TIMETWO);
        int index4 = cursor.getColumnIndex(Constants.MESSAGE);
        int index5 = cursor.getColumnIndex(Constants.COLOR);
        int index6 = cursor.getColumnIndex(Constants.LOCATION);

        mArrayList = new ArrayList<>();
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            String[] dateClickedParse = (cursor.getString(index0)).split(" ");

            Log.d(TAG, "TESTING1 " + dateClickedParse[0]);

            if(     dateClickedParse[0].contentEquals(parser[0]) &&
                    dateClickedParse[1].contentEquals(parser[1]) &&
                    dateClickedParse[2].contentEquals(parser[2])) {

                String title = cursor.getString(index1);
                String timeone = cursor.getString(index2);
                String timetwo = cursor.getString(index3);
                String message = cursor.getString(index4);

                // probably add location
                String color = cursor.getString(index5);
                String location = cursor.getString(index6);

                s = new String[6];
                s[0] = title;
                s[1] = timeone;
                s[2] = timetwo;
                s[3] = message;
                s[4] = color;

                //If no color is chosen, choose green
                if(color == null) {
                    s[4] = "@colors/boxColor6";
                }

                s[5] = location;

                mArrayList.add(s);
            }
            cursor.moveToNext();
            }

            organizeEvents(mArrayList);
    }

    //Organize events on to the timeline (chronogically accurate to the hour)
    private void organizeEvents(final ArrayList<String[]> mArrayList) {
        int dp = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics()));

        //EVENT LAYOUTS
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView event;
        TextView eventTime;
        TextView eventDesc;

        for (int i = 0; i < mArrayList.size(); i++) {
            //Single event Layout
            eventLayout = new LinearLayout(this);
            if (mArrayList.get(i)[4].contentEquals("@colors/boxColor1")) {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_blue));
            }
            else if (mArrayList.get(i)[4].contentEquals("@colors/boxColor2")) {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_red));
            }
            else if (mArrayList.get(i)[4].contentEquals("@colors/boxColor3")) {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_yellow));
            }
            else if (mArrayList.get(i)[4].contentEquals("@colors/boxColor4")) {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_lightblue));
            }
            else if (mArrayList.get(i)[4].contentEquals("@colors/boxColor5")) {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_orange));
            }
            else {
                eventLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_green));
            }

            int hour2;
            int hour1;

            //Decide position for hour 2 (ending time)
            if((mArrayList.get(i)[2]).charAt(1) != ':' ){
                char tenth = (mArrayList.get(i)[2]).charAt(0);
                char oneth = (mArrayList.get(i)[2]).charAt(1);
                String time = new StringBuilder().append(tenth).append(oneth).toString();
                hour2 = Integer.parseInt(time);
            }
            else{
                char oneth = (mArrayList.get(i)[2]).charAt(0);
                hour2 = oneth - 48;
            }
            //Check if AM or PM
            if((mArrayList.get(i)[2]).charAt(mArrayList.get(i)[2].length() - 2) == 'P' ){
                hour2 += 12;
            }

            //Decide position for hour 1 (starting time)
            if((mArrayList.get(i)[1]).charAt(1) != ':' ){
                char tenth = (mArrayList.get(i)[1]).charAt(0);
                char oneth = (mArrayList.get(i)[1]).charAt(1);
                String time = new StringBuilder().append(tenth).append(oneth).toString();
                hour1 = Integer.parseInt(time);
            }
            else{
                char oneth = (mArrayList.get(i)[1]).charAt(0);
                hour1 = oneth - 48;
            }
            // Check if AM or PM
            if((mArrayList.get(i)[1]).charAt(mArrayList.get(i)[1].length() - 2) == 'P' ){
                hour1 += 12;
            }

            int heightOfEvent = height * Math.abs(((hour2) - (hour1)));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginTop = height * (hour1);
            lp.setMargins(0, marginTop, 0, 0);


            lp.height = heightOfEvent;
            eventLayout.setLayoutParams(lp);
            eventLayout.setOrientation(LinearLayout.VERTICAL);
            eventLayout.setPadding(dp, dp/3, dp, dp/3);

            //Event Textviews
            eventTime = new TextView(this);
            event = new TextView(this);
            eventDesc = new TextView(this);

            //Time Text View
            eventTime.setLayoutParams(rlp);
            eventTime.setTextColor(Color.WHITE);
            eventTime.setTypeface(ResourcesCompat.getFont(this, R.font.circular_medium));
            eventTime.setTextSize(10);
            eventTime.setPadding(0, 0, 0, 30);
            eventTime.setText(mArrayList.get(i)[1] + " " + mArrayList.get(i)[2]);

            //Event Title Text View
            event.setLayoutParams(rlp);
            event.setTextColor(Color.WHITE);
            event.setTypeface(ResourcesCompat.getFont(this, R.font.circular_medium));
            event.setTextSize(18);
            event.setText(mArrayList.get(i)[0]);

            //Event Description Text View
            eventDesc.setLayoutParams(rlp);
            eventDesc.setTextColor(Color.WHITE);
            eventDesc.setMaxWidth(width * 2);
            eventDesc.setTypeface(ResourcesCompat.getFont(this, R.font.circular_book));
            eventDesc.setTextSize(12);
            eventDesc.setText(mArrayList.get(i)[3]);

            //Add TextViews to event container
            eventLayout.addView(eventTime);
            eventLayout.addView(event);
            eventLayout.addView(eventDesc);

            String details = Arrays.toString(mArrayList.get(i));
            eventLayout.setTag(details);
            Log.d(TAG, "DETAILS " + details);
            //Set OnClick Listener
            eventLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   toEdit(v);
                }
            });

            //Add event container to FrameLayout
            frameLayout.addView(eventLayout);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUNCH_EDIT_CALENDAR){
            if(resultCode == Activity.RESULT_OK){
                recreate();
                Log.d(TAG,"this ran once");
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d(TAG,"no result");
            }
        }

        if(requestCode == LAUNCH_VIEW_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                recreate();
                Log.d(TAG,"this ran once");
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d(TAG,"no result");
            }
        }
    }

    //Intent to go back to MainActivity
    public void backHome(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Intent that goes to edit selected event
    public void toEdit(View v){
        Intent intent = new Intent(this, ViewEvent.class);
        String details = (String) v.getTag();
        details = details.substring(1, details.length() - 1);
        String[] parser = details.split(", ");
        intent.putExtra( "DATECLICKED", dateClicked);
        intent.putExtra("stringdata", parser);
        startActivityForResult(intent, LAUNCH_VIEW_ACTIVITY);
    }

    //Will return true if nightmode is enabled
    private boolean isNightMode(){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        night = sharedPrefs.getInt("night", 0);
        if(night == 1){
            return true;
        }
        return false;
    }

}
