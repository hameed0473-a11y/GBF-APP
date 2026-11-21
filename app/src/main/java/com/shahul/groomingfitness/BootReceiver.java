package com.shahul.groomingfitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent intent) {
        String action=intent==null?null:intent.getAction();
        // Only reschedule for the system broadcasts declared in the manifest.
        if(Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action))
            Reminders.scheduleAll(c);
    }
}
