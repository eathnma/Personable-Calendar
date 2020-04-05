package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mainactivity.CalendarObjects.EventsList;

public class UserSettings extends AppCompatActivity {
    static final String TAG = "UserSettings";
    public static final String KEY_ISNIGHTMODE = "isNightMode";

    private EditText nameTextView;
    private ImageView pictureImageView;
    private ImageView birthdayImageView;
    private TextView title;
    private Switch nightView;

    private int night;

    private String birthday;
    private String color;
    private String name;
    private String profilePicture;

    SharedPreferences sharedPrefs;


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
        pictureImageView = findViewById(R.id.picture);
        birthdayImageView = findViewById(R.id.birthday);
        title = findViewById(R.id.title);

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

}
