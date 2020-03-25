package com.example.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarView extends LinearLayout {
    // calendar components
    LinearLayout header;
    Button btnToday;
    ImageView btnPrev;
    Button btnNext;
    TextView txtDateDay;
    TextView txtDisplayDate;
    TextView txtDateYear;
    GridView gridView;
    Toolbar toolbar;

    EventHandler eventHandler;

    private static final int DAYS_COUNT = 42;
    private static final String DATE_FORMAT = "MMM yyyy";
    private String dateFormat;
    String[] dateToday;

    private Calendar currentDate = Calendar.getInstance();

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    private void assignUiElements(Context context) {
        // layout is inflated, assign local variables to components
        header = findViewById(R.id.calendar_header);
        //btnPrev = findViewById(R.id.calendar_prev_button);
        btnNext = findViewById(R.id.button1);
        txtDisplayDate = findViewById(R.id.date_display_date);
       // btnToday = findViewById(R.id.date_display_today);
        gridView = findViewById(R.id.calendar_grid);
        //toolbar = findViewById(R.id.actionBar);
        setEventHandler(eventHandler);

    }

    private void assignClickHandler(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventHandler.onDayClick((Date)parent.getItemAtPosition(position));
            }
        });

    }


    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_calendar, this);
        loadDateFormat(attrs);
        assignUiElements(context);
        assignClickHandler();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }

    public void updateCalendar() {
        updateCalendar(null);
    }

    public void updateCalendar(HashSet<Date> events) {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        TextView dayOfWeek;

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        gridView.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM,yyyy,dd");
        dateToday = sdf.format(currentDate.getTime()).split(",");

        txtDisplayDate.setText(dateToday[1]);

        dayOfWeek = (TextView) findViewById(getResources().getIdentifier(dateToday[0], "id", "com.example.mainactivity"));
        dayOfWeek.setTextColor(Color.BLACK);
        dayOfWeek.setTypeface(null, Typeface.BOLD);
    }

    private void setColors(){
        GradientDrawable shape = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.text_view_circle);
        shape.setColor(ContextCompat.getColor(getContext(), R.color.boxColor1));

        ImageView color = findViewById(R.id.blueCircle);

        color.setBackground(shape);
    }

    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    //public interface so MainActivity can handle events when each itemView is clicked
    public interface EventHandler
    {
        void onDayClick(Date date);
    }

    public String[] returnDate(){
        return dateToday;
    }
}

