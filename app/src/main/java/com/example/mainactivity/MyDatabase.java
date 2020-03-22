package com.example.mainactivity;

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

    public long insertData (String name, String dateOne, String timeOne, String dateTwo, String message)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.DATEONE, dateOne);
        contentValues.put(Constants.TIMEONE, timeOne);
        contentValues.put(Constants.MESSAGE, message);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.NAME, Constants.DATEONE, Constants.TIMEONE, Constants.MESSAGE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }
}
