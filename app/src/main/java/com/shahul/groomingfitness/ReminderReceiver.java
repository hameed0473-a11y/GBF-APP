package com.shahul.groomingfitness;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public final class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent intent) {
        int i=intent.getIntExtra("index",-1);
        if(i<0 || i>=Tasks.ALL.size()) return;
        Tasks.Task t=Tasks.ALL.get(i);
        // Always schedule tomorrow, including after today's item has already been marked.
        Reminders.schedule(c,t);
        if(Tasks.complete(c,t)) return;
        if(Build.VERSION.SDK_INT>=33 && c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) return;
        NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nm==null) return;
        nm.createNotificationChannel(new NotificationChannel("routine","Routine reminders",NotificationManager.IMPORTANCE_DEFAULT));
        PendingIntent open=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification n=new Notification.Builder(c,"routine").setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(t.title).setContentText(t.detail).setContentIntent(open).setAutoCancel(true).build();
        nm.notify(i+1,n);
    }
}
