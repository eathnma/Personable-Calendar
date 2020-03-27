package com.example.mainactivity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyDatabaseHelper helper;

    public MyDatabase(Context c) {
        context = c;
        helper = new MyDatabaseHelper(context);
    }

    public long insertData (String title, String timeOne, String timeTwo, String message, String color, String dateClicked, String location)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DATECLICKED, dateClicked);
        contentValues.put(Constants.TITLE, title);
        contentValues.put(Constants.TIMEONE, timeOne);
        contentValues.put(Constants.TIMETWO, timeTwo);
        contentValues.put(Constants.MESSAGE, message);
        contentValues.put(Constants.LOCATION, location);
        contentValues.put(Constants.COLOR, color);

        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.DATECLICKED, Constants.TITLE,
                Constants.TIMEONE, Constants.TIMETWO, Constants.MESSAGE,  Constants.LOCATION, Constants.COLOR};

        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }
}
