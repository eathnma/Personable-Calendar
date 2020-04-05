package com.example.mainactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PeriodicReminder.class);
        i.putExtra("foo", "bar");
        context.startService(i);
    }
}
