package com.shahul.groomingfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.time.LocalDateTime;
import java.time.ZoneId;

final class Reminders {
    static void scheduleAll(Context c) {
        cancelLegacyAlarms(c);
        for(Tasks.Task t:Tasks.ALL) schedule(c,t);
    }
    static void schedule(Context c, Tasks.Task t) {
        AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(manager==null) return;
        Intent intent=new Intent(c,ReminderReceiver.class).putExtra("id",t.id);
        PendingIntent pi=PendingIntent.getBroadcast(c,t.requestCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        LocalDateTime when=LocalDateTime.now().withHour(t.hour).withMinute(t.minute).withSecond(0).withNano(0);
        if(!when.isAfter(LocalDateTime.now())) when=when.plusDays(1);
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),pi);
    }
    /** Version 0.1.0 keyed alarms by list position (0..10). Cancel them once so they cannot fire for the wrong task. */
    private static void cancelLegacyAlarms(Context c) {
        if(Tasks.prefs(c).getBoolean("legacy_alarms_cleared",false)) return;
        AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(manager==null) return;
        for(int code=0;code<20;code++) {
            PendingIntent old=PendingIntent.getBroadcast(c,code,new Intent(c,ReminderReceiver.class),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);
            if(old!=null) { manager.cancel(old); old.cancel(); }
        }
        Tasks.prefs(c).edit().putBoolean("legacy_alarms_cleared",true).apply();
    }
}
