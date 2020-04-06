package com.example.mainactivity.CalendarObjects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.gridlayout.widget.GridLayout;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mainactivity.Database.MyDatabase;
import com.example.mainactivity.DialogueObjects.DiscardDialogue;
import com.example.mainactivity.DialogueObjects.TimePickerFragment;
import com.example.mainactivity.DialogueObjects.TimePickerFragmentTwo;
import com.example.mainactivity.Overlay;
import com.example.mainactivity.R;

import java.util.Calendar;

public class EditCalendar extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private String AM_PM;
    static final String TAG = "EditCalendar";


    private int flag = 0;
    private static final int FLAG_START_ONE = 1;
    private static final int FLAG_START_TWO = 2;

    //elements in gridlayout 1
    private ImageView arrowRowOne;
    private Button saveButtonRowOne;

    //elements in gridlayout 2
    private EditText addTitle;

    //elements in gridlayout 3
    private TextView editDateRowOne;
    private TextView editTimeRowOne;
    private TextView editDateRowTwo;
    private TextView editTimeRowTwo;

    //elements in gridlayout 4
    private EditText notificationMessage;

    //elements in gridlayout 5
    private GridLayout locationButton;
    private TextView addLocation;
    //get result from intent
    private int LAUNCH_SECOND_ACTIVITY = 1;

    //elements in gridlayout 6
    private ImageView[] imgs = new ImageView[6];
    private String chosenColor;

    // add database object
    MyDatabase db;
    private int night;

    private String adjustedMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar);
        stopService(new Intent(this, Overlay.class));
        //prevents the keyboard from showing up from EditText
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //loading calendar to get current dates / times
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int hourPlusOne = 1 + c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        if(c.get(Calendar.AM_PM) == 1){
            AM_PM = "PM";
        } else {
            AM_PM = "AM";
        }

        //GRID LAYOUT ONE
        saveButtonRowOne = (Button) findViewById(R.id.saveButton);
        saveButtonRowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = addTitle.getText().toString();
                String timeOne = editTimeRowOne.getText().toString();
                String timeTwo = editTimeRowTwo.getText().toString();
                String message = notificationMessage.getText().toString();
                String location = addLocation.getText().toString();
                Intent intent = getIntent();
                String dateClicked = intent.getStringExtra("DATECLICKED");
                long id = db.insertData(title, timeOne, timeTwo, message, chosenColor, dateClicked, location);

                Toast.makeText(getApplicationContext(), title + timeOne + timeTwo + message + chosenColor + location + dateClicked, Toast.LENGTH_SHORT).show();

                int result = 1;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        arrowRowOne = (ImageView) findViewById(R.id.arrow);
        arrowRowOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                discardMessage(v);
            }
        });

        //GRID LAYOUT TWO
        addTitle = (EditText) findViewById(R.id.addTitle);

        // GRID LAYOUT THREE
        editDateRowOne = (TextView) findViewById(R.id.editDateRowOne);
        editTimeRowOne = (TextView) findViewById(R.id.editTimeRowOne);
        editDateRowTwo = (TextView) findViewById(R.id.editDateRowTwo);
        editTimeRowTwo = (TextView) findViewById(R.id.editTimeRowTwo);

        Intent intent = getIntent();
        String dateClicked = intent.getStringExtra("DATECLICKED");

        String [] parser  = dateClicked.split(" ");
        String MYD = parser[0] + " " + parser[1] + " " + parser[2];

        editDateRowOne.setText(MYD + ", 2020");
        editDateRowTwo.setText(MYD + ", 2020");

        //onClick opens the TIME DIALOGUE ( ROW ONE )
        editTimeRowOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
                flag = FLAG_START_ONE;
            }
        });
        //
        editTimeRowOne.setText(Integer.toString(hour) + ":" + Integer.toString(minute) + AM_PM);

        //onClick opens the TIME DIALOGUE ( ROW TWO )
        editTimeRowTwo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragmentTwo();
                timePicker.show(getSupportFragmentManager(),"time picker");
                flag = FLAG_START_TWO;
            }
        });
        editTimeRowTwo.setText(Integer.toString(hourPlusOne) + ":" + Integer.toString(minute)+ AM_PM);

        //GRID LAYOUT FOUR
        notificationMessage = (EditText) findViewById(R.id.notificationMessage);

        //GRID LAYOUT FIVE
        locationButton = (GridLayout) findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditCalendar.this, AddLocationActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });

        addLocation = (TextView) findViewById(R.id.addLocation);

        //GRID LAYOUT SIX
        imgs[0]=findViewById(R.id.blueCircle);
        imgs[1]=findViewById(R.id.redCircle);
        imgs[2]=findViewById(R.id.yellowCircle);
        imgs[3]=findViewById(R.id.lightBlueCircle);
        imgs[4]=findViewById(R.id.orangeCircle);
        imgs[5]=findViewById(R.id.greenCircle);

        for(int j = 0; j < imgs.length; j++){
            final int finalJ = j;
            imgs[j].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    chosenColor = "@colors/boxColor" + (finalJ + 1);
                }
            });
        }

//      instantiate database object
        db = new MyDatabase(this);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        night = sharedPrefs.getInt("night", 0);

        if(night == 1){
            night = sharedPrefs.getInt("night", 0);
            Log.d(TAG, String.valueOf(night));

            // if darkmode else, skip
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                setTheme(R.style.darktheme);
            } else setTheme(R.style.AppTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            // set top left arrow to white
            arrowRowOne.setImageResource(R.drawable.white_arrow);
            addTitle.setTextColor(Color.WHITE);
            addTitle.setHintTextColor(Color.GRAY);
            saveButtonRowOne.setTextColor(Color.WHITE);
            notificationMessage.setHintTextColor(Color.GRAY);
            notificationMessage.setTextColor(Color.BLACK);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUNCH_SECOND_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                String currentlocation = data.getStringExtra("currentlocation");
                addLocation.setText("");
                addLocation.setText(currentlocation);
            }
            if(resultCode == Activity.RESULT_CANCELED){
                // Write code for no result
            }
        }
    }

    public void discardMessage(View view){
        DiscardDialogue discardDialogue = new DiscardDialogue();
        discardDialogue.show(getSupportFragmentManager(), "example dialogue");
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (flag == FLAG_START_ONE) {
            // change 24 hour clock to display 12
            if (hour >= 12) {
                hour = 1 + hour - 13;
                AM_PM = "PM";
            } else {
                AM_PM = "AM";
            }

            if(minute <= 9){
              adjustedMin = String.format("%02d", minute);
                Log.d(TAG, String.format("%02d", minute));
            }

            editTimeRowOne.setText(Integer.toString(hour) + ":" + adjustedMin + AM_PM);
        } else if (flag == FLAG_START_TWO) {
            // change 24 hour clock to display 12
            if (hour >= 12) {
                hour = 1 + hour - 13;
                AM_PM = "PM";
            } else {
                AM_PM = "AM";
            }

            if(minute <= 9){
                adjustedMin = String.format("%02d", minute);
                Log.d(TAG, String.format("%02d", minute));
            }

            editTimeRowTwo.setText(Integer.toString(hour) + ":" +  adjustedMin + AM_PM);
        }
    }

}
