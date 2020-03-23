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

    public long insertData (String title, String dateOne, String timeOne, String dateTwo, String timeTwo, String message)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TITLE, title);
        contentValues.put(Constants.DATEONE, dateOne);
        contentValues.put(Constants.TIMEONE, timeOne);
        contentValues.put(Constants.DATETWO, dateTwo);
        contentValues.put(Constants.TIMETWO, timeTwo);
        contentValues.put(Constants.MESSAGE, message);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.TITLE, Constants.DATEONE,
                Constants.TIMEONE, Constants.DATETWO, Constants.TIMETWO, Constants.MESSAGE};

        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }
}
