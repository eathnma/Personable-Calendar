package com.example.mainactivity.CalendarObjects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.mainactivity.CalendarObjects.EditCalendar;
import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class EventsList extends AppCompatActivity {
    private static final String TAG = "EventsList";
    private int width, height;

    TextView hour;
    String toolbarDate;
    ScrollView scrollView;

    LinearLayout parentContainer;
    LinearLayout timeline;
    LinearLayout boxParent;
    LinearLayout box;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout.LayoutParams layoutParams2;

    //EVENT LAYOUTS WITH BOXCONTAINER
    RelativeLayout eventsContainer;
    FrameLayout frameLayout;
    LinearLayout eventLayout;


    private Resources r;
    Toolbar toolbar;
    String dateClicked;
    String[] parser;
    TextView toolbarTitle;
    Intent intent;
    MyDatabase db;
    MyDatabaseHelper helper;

    private ArrayList<String[]> mArrayList = new ArrayList<String[]>();
    private String s[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        intent = getIntent();
        dateClicked = intent.getStringExtra("DATE_CLICKED");
        parser = dateClicked.split(" ");
        Log.d(TAG, dateClicked);

        //eventsContainer = findViewById(R.id.events_container);

        //database
        db = new MyDatabase(this);
        helper = new MyDatabaseHelper(this);

        //Toolbar stuff
        toolbarDate = intent.getStringExtra("TOOLBAR");
        toolbar = findViewById(R.id.actionBar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(toolbarDate);

        //Scrollview containers
        scrollView = findViewById(R.id.scrollViewContainer);
        r = getResources();
        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics()));
        height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics()));


        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        setContainers();
        fillLayout();
        
        scrollView.addView(parentContainer);

        // once the eventlayout has been instantiated
        if(mArrayList.size() != 0){
            eventLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent viewActivity = new Intent(getBaseContext(), ViewEvent.class);
                viewActivity.putExtra( "DATECLICKED", dateClicked);
                viewActivity.putExtra("stringdata", s);
                startActivity(viewActivity);
//                Log.d(TAG, Arrays.toString(s));

            }
        });
        }
    }

    private void fillLayout(){
        for(int i = 1; i < 12; i++){
            setHourParams(i, "am", i);
            if(i == 0){
                setHourParams(12 , "am", i);
            }
            instantiateBoxes(i);
            boxParent.addView(box);
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

    private void setHourParams(int setHour, String ampm, int id){
        hour = new TextView(this);
        hour.setLayoutParams(layoutParams);
        hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        hour.setGravity(Gravity.CENTER_VERTICAL);
        hour.setId(id);
        hour.setText(setHour + " " + ampm);
    }

    private void setHourParams(int id){
        hour = new TextView(this);
        hour.setLayoutParams(layoutParams);
        hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        hour.setGravity(Gravity.CENTER_VERTICAL);
        hour.setId(id);
        hour.setText(" ");
    }

    private void instantiateBoxes(int id){
        box = new LinearLayout(this);
        box.setLayoutParams(layoutParams2);
        box.setId(id);
        box.setBackground(ContextCompat.getDrawable(this, R.drawable.background_stroke));
        box.setOrientation(LinearLayout.HORIZONTAL);

    }

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
        parentContainer.addView(timeline);
        frameLayout.addView(boxParent);
        displayEvent();
        parentContainer.addView(frameLayout);
        //Display event

    }

    //next intent
    public void clickAddEvent(View v){
        Intent editCalendarIntent = new Intent(getBaseContext(), EditCalendar.class);
        editCalendarIntent.putExtra("DATECLICKED", dateClicked);
        startActivity(editCalendarIntent);
    }


    private void displayEvent(){
        Cursor cursor = db.getData();

        int index0 = cursor.getColumnIndex(Constants.DATECLICKED);
        int index1 = cursor.getColumnIndex(Constants.TITLE);
        int index2 = cursor.getColumnIndex(Constants.TIMEONE);
        int index3 = cursor.getColumnIndex(Constants.TIMETWO);
        int index4 = cursor.getColumnIndex(Constants.MESSAGE);
        int index5 = cursor.getColumnIndex(Constants.COLOR);
        int index6 = cursor.getColumnIndex(Constants.LOCATION);

        mArrayList = new ArrayList<String[]>();
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

                String[] s = new String[6];
                s[0] = title;
                s[1] = timeone;
                s[2] = timetwo;
                s[3] = message;
                s[4] = color;
                s[5] = location;

                mArrayList.add(s);
            }
            cursor.moveToNext();
            }

            organizeEvents(mArrayList);
        }

    private void organizeEvents(ArrayList<String[]> mArrayList) {
        int dp = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics()));

        int AMorPM;

        //EVENT LAYOUTS

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView event;
        TextView eventTime;
        TextView eventDesc;


        for (int i = 0; i < mArrayList.size(); i++) {
            Log.d(TAG, "TESTING " + mArrayList.get(i)[0] + " " + mArrayList.get(i)[1] + " " + mArrayList.get(i)[2] + " " + mArrayList.get(i)[3] + " " + mArrayList.get(i)[4]);

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

            int heightOfEvent = height * (((mArrayList.get(i)[2]).charAt(0) - 48) - ((mArrayList.get(i)[1]).charAt(0) - 48));
            Log.d(TAG, "HEIGHT " + heightOfEvent);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginTop = height * ((mArrayList.get(i)[1]).charAt(0) - 48);
            lp.setMargins(0, marginTop, 0, 0);
            lp.height = heightOfEvent;
            eventLayout.setLayoutParams(lp);
            eventLayout.setOrientation(LinearLayout.VERTICAL);
            eventLayout.setPadding(dp, dp/3, dp, dp/3);

            //Event Textviews
            eventTime = new TextView(this);
            event = new TextView(this);
            eventDesc = new TextView(this);

            eventTime.setLayoutParams(rlp);
            eventTime.setTextColor(Color.WHITE);
            eventTime.setTypeface(ResourcesCompat.getFont(this, R.font.circular_medium));
            eventTime.setTextSize(10);
            eventTime.setPadding(0, 0, 0, 30);
            eventTime.setText(mArrayList.get(i)[1] + " " + mArrayList.get(i)[2]);

            event.setLayoutParams(rlp);
            event.setTextColor(Color.WHITE);
            event.setTypeface(ResourcesCompat.getFont(this, R.font.circular_medium));
            event.setTextSize(18);
            event.setText(mArrayList.get(i)[0]);

            eventDesc.setLayoutParams(rlp);
            eventDesc.setTextColor(Color.WHITE);
            eventDesc.setMaxWidth(width * 2);
            eventDesc.setTypeface(ResourcesCompat.getFont(this, R.font.circular_book));
            eventDesc.setTextSize(12);
            eventDesc.setText(mArrayList.get(i)[3]);

            eventLayout.addView(eventTime);
            eventLayout.addView(event);
            eventLayout.addView(eventDesc);

            frameLayout.addView(eventLayout);
        }

    }
}
