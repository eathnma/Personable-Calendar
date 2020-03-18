package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class EditCalendar extends AppCompatActivity implements View.OnClickListener {

    private ImageView arrowRowOne;
    private Button saveButtonRowOne;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_calendar);

        //setting listeners by grid-row
        saveButtonRowOne = (Button) findViewById(R.id.saveButtonRowOne);
        saveButtonRowOne.setOnClickListener(this);
        arrowRowOne = (ImageView) findViewById(R.id.arrowRowOne);
        arrowRowOne.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, MainActivity.class); // this class is bound to change.
        startActivity(i);
    }
}
