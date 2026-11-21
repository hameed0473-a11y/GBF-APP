package com.shahul.groomingfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.time.LocalDateTime;
import java.time.ZoneId;

final class Reminders {
    static void scheduleAll(Context c) { for(Tasks.Task t:Tasks.ALL) schedule(c,t); }
    static void schedule(Context c, Tasks.Task t) {
        AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(manager==null) return;
        int code=Tasks.ALL.indexOf(t);
        Intent intent=new Intent(c,ReminderReceiver.class).putExtra("index",code);
        PendingIntent pi=PendingIntent.getBroadcast(c,code,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        LocalDateTime when=LocalDateTime.now().withHour(t.hour).withMinute(t.minute).withSecond(0).withNano(0);
        if(!when.isAfter(LocalDateTime.now())) when=when.plusDays(1);
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),pi);
    }
}
