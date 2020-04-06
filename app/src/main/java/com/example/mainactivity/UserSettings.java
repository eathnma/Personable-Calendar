package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class UserSettings extends AppCompatActivity {
    static final String TAG = "UserSettings";
    public static final String KEY_ISNIGHTMODE = "isNightMode";

    private EditText nameTextView;
    private TextView title;
    private Switch nightView;
    private Button saveButton;

    private int night;

    private String birthday;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stopService(new Intent(getApplicationContext(), Overlay.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        stopService(new Intent(this, Overlay.class));

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme);
        } else setTheme(R.style.AppTheme);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        nameTextView = findViewById(R.id.addName);
        title = findViewById(R.id.title);
        saveButton = findViewById(R.id.saveButton);

        nightView = (Switch) findViewById(R.id.nightView);

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            nightView.setChecked(true);
        }

        nightView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref =  getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    night = 1;
                    editor.putInt("night", night);
                    recreate();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    night = 0;
                    editor.putInt("night", night);
                    recreate();
                }
                editor.commit();
            }
        });


        night = sharedPrefs.getInt("night", 0);
        if(night == 1){
            nameTextView.setTextColor(Color.BLACK);
            saveButton.setTextColor(Color.WHITE);
        }

        if(sharedPrefs.contains("name")){
            name = sharedPrefs.getString("name", null);
            if(name.charAt(name.length() - 1) == 's') {
                title.setText(name + "' Settings");
            }
            else{
                title.setText(name + "'s Settings");
            }
        }
    }

    private boolean getName(){
        if(nameTextView.getText().toString() != null || nameTextView.getText().toString().charAt(0) != ' ') {
            name = nameTextView.getText().toString();
            return true;
        }
        Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
        return false;
    }


    public void saved(View v){
        if(getName()){
            //save to shared prefs
            SharedPreferences sharedPref =  getSharedPreferences("MyData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", name);
            editor.commit();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }
        else{
            //toast for failure
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(UserSettings.this, MainActivity.class);
        startActivity(intent);
    }

    //Save birthday once set
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
