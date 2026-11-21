package com.shahul.groomingfitness;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Tasks {
    static final class Task {
        final String id, title, group, detail;
        final int intervalDays, hour, minute;
        Task(String id, String title, String group, String detail, int days, int hour, int minute) {
            this.id=id; this.title=title; this.group=group; this.detail=detail;
            this.intervalDays=days; this.hour=hour; this.minute=minute;
        }
    }
    static final List<Task> ALL = Arrays.asList(
        new Task("haircut", "Haircut", "Grooming", "Every 30 days", 30, 10, 0),
        new Task("nails", "Trim nails", "Grooming", "Every 7 days", 7, 18, 0),
        new Task("detan", "Gentle skin care / de-tan", "Beauty", "Every 7 days; avoid harsh products", 7, 18, 0),
        new Task("honey", "Honey (optional)", "Fitness", "Morning; honey is added sugar", 1, 7, 0),
        new Task("oats", "Oats for breakfast", "Fitness", "Morning", 1, 8, 0),
        new Task("almonds", "10 almonds (badam)", "Fitness", "Morning snack", 1, 10, 0),
        new Task("egg", "1 egg", "Fitness", "Any time today", 1, 13, 0),
        new Task("fruit", "2 fruits", "Fitness", "Any two: apple, banana, guava, orange", 1, 16, 0),
        new Task("dates", "2 dates", "Fitness", "Any time today", 1, 16, 0),
        new Task("protein", "Affordable protein serving", "Fitness", "Choose dal, chickpeas, curd, peanuts, or eggs", 1, 13, 0),
        new Task("movement", "Move today", "Fitness", "3,000 steps OR 10 minutes treadmill/cycling", 1, 18, 30)
    );
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("routine", Context.MODE_PRIVATE); }
    static boolean complete(Context c, Task t) {
        String stamp=prefs(c).getString("done_"+t.id, "");
        if(stamp.isEmpty()) return false;
        try { return LocalDate.parse(stamp).plusDays(t.intervalDays).isAfter(LocalDate.now()); }
        catch(Exception ignored) { return false; }
    }
    static String last(Context c, Task t) { return prefs(c).getString("done_"+t.id, ""); }
    static void mark(Context c, Task t) { prefs(c).edit().putString("done_"+t.id, LocalDate.now().toString()).apply(); }
    static void undo(Context c, Task t) { prefs(c).edit().remove("done_"+t.id).apply(); }
    static List<Task> due(Context c) {
        List<Task> result=new ArrayList<>(); for(Task t:ALL) if(!complete(c,t)) result.add(t); return result;
    }
}
