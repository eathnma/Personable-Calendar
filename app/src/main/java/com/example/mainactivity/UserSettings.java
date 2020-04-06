package com.example.mainactivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class UserSettings extends AppCompatActivity {
    static final String TAG = "UserSettings";
    private EditText nameTextView;
    private ImageView pictureImageView;
    private ImageView birthdayImageView;
    private TextView title;

    private String birthday;
    private String color;
    private String name;
    private String profilePicture;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        stopService(new Intent(this, Overlay.class));

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        nameTextView = findViewById(R.id.addName);
        birthdayImageView = findViewById(R.id.birthday);
        title = findViewById(R.id.title);

        if(sharedPrefs.contains("name")){
            name = sharedPrefs.getString("name", null);
            if(name.charAt(name.length() - 1) == 's') {
                title.setText(name + "' Settings");
            }
            else{
                title.setText(name + "'s Settings");
            }
        }

        color = null;

    }


    private boolean getName(){
        if(nameTextView.getText().toString() != null || nameTextView.getText().toString().charAt(0) != ' ') {
            name = nameTextView.getText().toString();
            return true;
        }
        Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void getColor(View v){
        color = v.getContentDescription().toString();
    }

    public void saved(View v){

        if(getName()){
            //save to shared prefs
            SharedPreferences sharedPref =  getSharedPreferences("MyData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", name);
            editor.putString("color", color);
            editor.commit();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
        else{
            //toast for failure
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
        }
    }


    public void setBirthday(View v){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Add birthday to shared preferences
                        SharedPreferences sharedPref =  getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("birthday month", monthOfYear);
                        editor.putInt("birthday day", dayOfMonth);
                        editor.commit();
                        Log.d("UserSettings", "Birthday Month: " + monthOfYear + " " + "Birth day: " + dayOfMonth);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
