package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

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
    private Resources r;
    Toolbar toolbar;
    TextView toolbarTitle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        intent = getIntent();


        toolbarDate = intent.getStringExtra("TOOLBAR");
        toolbar = findViewById(R.id.actionBar);
        toolbarTitle= toolbar.findViewById(R.id.toolbarTitle);

        toolbarTitle.setText(toolbarDate);

        scrollView = findViewById(R.id.scrollViewContainer);
        r = getResources();
        width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics()));
        height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics()));

        layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        setContainers();
        scrollView.addView(parentContainer);
        fillLayout();
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
        parentContainer = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentContainer.setLayoutParams(lp);
        parentContainer.setTag("parentContainer");

        boxParent = new LinearLayout(this);
        boxParent.setLayoutParams(lp);
        boxParent.setOrientation(LinearLayout.VERTICAL);
        boxParent.setTag("boxes");


        timeline = new LinearLayout(this);
        lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeline.setLayoutParams(lp);

        int paddingTop = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
        timeline.setPadding(0, paddingTop, 0, -paddingTop);
        timeline.setOrientation(LinearLayout.VERTICAL);
        timeline.setTag("timeline");

        parentContainer.addView(timeline);
        parentContainer.addView(boxParent);
    }

    public void clickAddEvent(View v){
        Intent editCalendarIntent = new Intent(getBaseContext(), EditCalendar.class);
        startActivity(editCalendarIntent);
    }
}
