package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mainactivity.PickerFragments.TimePickerFragment;

public class EditCalendar extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

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
    private TextView addLocation;

    //elements in gridlayout 6
    private ImageView blueCircle, redCircle, yellowCircle, lightBlueCircle, orangeCircle, greenCircle;

    // add database object
    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //setting listeners in gridlayout 1
        saveButtonRowOne = (Button) findViewById(R.id.saveButton);
//        saveButtonRowOne.setOnClickListener(this);
        arrowRowOne = (ImageView) findViewById(R.id.arrow);
//        arrowRowOne.setOnClickListener(this);

        //setting listeners in gridlayout 2
        addTitle = (EditText) findViewById(R.id.addTitle);

        //setting listeners in gridlayout 3
        editDateRowOne = (TextView) findViewById(R.id.editDateRowOne);

        editTimeRowOne = (TextView) findViewById(R.id.editTimeRowOne);
        editTimeRowOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        });

        editDateRowTwo = (TextView) findViewById(R.id.editDateRowTwo);
        editTimeRowTwo = (TextView) findViewById(R.id.editTimeRowTwo);

        //setting listeners in gridlayout 4
        notificationMessage = (EditText) findViewById(R.id.notificationMessage);

        //setting listeners in gridlayout 5
        addLocation = (TextView) findViewById(R.id.addLocation);

        //setting listeners in gridlayout 6
        blueCircle = (ImageView) findViewById(R.id.blueCircle);
        redCircle = (ImageView) findViewById(R.id.redCircle);
        yellowCircle = (ImageView) findViewById(R.id.yellowCircle);
        lightBlueCircle = (ImageView) findViewById(R.id.lightBlueCircle);
        orangeCircle = (ImageView) findViewById(R.id.orangeCircle);
        greenCircle = (ImageView) findViewById(R.id.greenCircle);

//      instantiate database object
        db = new MyDatabase(this);
    }


    public void addActivity(View view){
        String name = addTitle.getText().toString();
        String dateOne = editDateRowOne.getText().toString();
        String timeOne = editTimeRowOne.getText().toString();
        String dateTwo = editDateRowOne.getText().toString();
        String message = notificationMessage.getText().toString();

        Toast.makeText(this, name + dateOne + timeOne + dateTwo + message, Toast.LENGTH_SHORT).show();

        long id = db.insertData(name, dateOne, timeOne, dateTwo, message);

//        Toast.makeText(this, "SEND BUTTON CLICKED", Toast.LENGTH_SHORT).show();
    }


    public void discardMessage(){
        // if clicked back, run a discard message?
    }

    public void home(View view){
        // return the user back to the homepage
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = (TextView) findViewById(R.id.textView);

    }
}
