package com.example.mainactivity.CalendarObjects;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
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

import com.example.mainactivity.CalendarObjects.CalendarAdapter;
import com.example.mainactivity.Database.Constants;
import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.Database.MyDatabaseHelper;
import com.example.mainactivity.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarView extends LinearLayout {
    // calendar components
    static final String TAG = "CalendarView";
    private LinearLayout header;
    private Button btnPrev;
    private Button btnNext;
    private TextView txtDateDay;
    private TextView txtDisplayDate;
    private TextView txtDateYear;
    private GridView gridView;
    private Toolbar toolbar;
    private MyDatabase db;
    private MyDatabaseHelper helper;
    private ArrayList<String> colorList;
    EventHandler eventHandler;

    private static final int DAYS_COUNT = 42;
    private static final String DATE_FORMAT = "MMM yyyy";
    private String dateFormat;
    String[] dateToday;

    private Calendar currentDate = Calendar.getInstance();
    private Calendar currentDateComparison = Calendar.getInstance();

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
        btnPrev = findViewById(R.id.buttonPrev);
        btnNext = findViewById(R.id.buttonNext);
        txtDisplayDate = findViewById(R.id.date_display_date);
        gridView = findViewById(R.id.calendar_grid);
        setEventHandler(eventHandler);

    }

    private void assignClickHandler(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //send date clicked
                eventHandler.onDayClick((Date)parent.getItemAtPosition(position));
            }
        });

        btnPrev.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

    }


    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_calendar, this);
        loadDateFormat(attrs);
        assignUiElements(context);
        assignClickHandler();
        //database stuff

        //Assign colors
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

        db = new MyDatabase(getContext());
        helper = new MyDatabaseHelper(getContext());

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
        gridView.setAdapter(new CalendarAdapter(getContext(), cells, events, currentDate, setDayColor(), colorList));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM,yyyy,dd");
        dateToday = sdf.format(currentDate.getTime()).split(",");

        txtDisplayDate.setText(dateToday[1]);

        if(currentDate.get(Calendar.MONTH) == currentDateComparison.get(Calendar.MONTH)) {
            dayOfWeek = (TextView) findViewById(getResources().getIdentifier(dateToday[0], "id", "com.example.mainactivity"));
            dayOfWeek.setTextColor(Color.BLACK);
            dayOfWeek.setTypeface(null, Typeface.BOLD);
        }
        else{
            //DO MORE EFFICIENTLY PLS
            dayOfWeek = (TextView) findViewById(R.id.Wednesday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Thursday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Friday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Saturday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Sunday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Monday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);

            dayOfWeek = (TextView) findViewById(R.id.Tuesday);
            dayOfWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            dayOfWeek.setTypeface(null, Typeface.NORMAL);
        }

    }

    private ArrayList<Integer> setDayColor(){
        Cursor cursor = db.getData();

        int index0 = cursor.getColumnIndex(Constants.DATECLICKED);
        int index1 = cursor.getColumnIndex(Constants.COLOR);

        ArrayList<Integer> mArrayList = new ArrayList<>();
        colorList = new ArrayList<>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String date = cursor.getString(index0);
            String[] parser = date.split(" ");
            Log.d(TAG, "PARSE: " + parser[2]);
            String color = cursor.getString(index1);
            colorList.add(color);
            mArrayList.add(Integer.parseInt(parser[2]));
            cursor.moveToNext();
        }
        return mArrayList;
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

