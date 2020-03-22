package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditCalendar extends AppCompatActivity implements View.OnClickListener {

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
//    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar);

        //setting listeners in gridlayout 1
        saveButtonRowOne = (Button) findViewById(R.id.saveButton);
        saveButtonRowOne.setOnClickListener(this);
        arrowRowOne = (ImageView) findViewById(R.id.arrow);
        arrowRowOne.setOnClickListener(this);

        //setting listeners in gridlayout 2
        addTitle = (EditText) findViewById(R.id.addTitle);

        //setting listeners in gridlayout 3
        editDateRowOne = (TextView) findViewById(R.id.editDateRowOne);
        editTimeRowOne = (TextView) findViewById(R.id.editTimeRowOne);

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
//        db = new MyDatabase(this);
    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, MainActivity.class); // this class is bound to change.
        startActivity(i);
    }
}
