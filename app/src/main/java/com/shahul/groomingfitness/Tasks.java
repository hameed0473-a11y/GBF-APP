package com.shahul.groomingfitness;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class Tasks {
    static final class Task {
        final String id, title, group, section, detail;
        final int intervalDays, hour, minute;
        Task(String id, String title, String group, String section, String detail, int days, int hour, int minute) {
            this.id=id; this.title=title; this.group=group; this.section=section; this.detail=detail;
            this.intervalDays=days; this.hour=hour; this.minute=minute;
        }
        /** Stable alarm request code; does not change if the list order changes. */
        int requestCode() { return id.hashCode() & 0x7fffffff; }
    }
    // Fitness diet tasks are listed in the order they should be eaten through the day.
    static final List<Task> ALL = Arrays.asList(
        new Task("haircut", "Haircut", "Grooming", "", "Every 30 days", 30, 10, 0),
        new Task("nails", "Trim nails", "Grooming", "", "Every 7 days (hands and feet)", 7, 18, 0),
        new Task("detan", "De-tan / skin care", "Beauty", "", "Every 7 days. Use a gentle pack; avoid harsh bleach or scrubbing", 7, 18, 0),
        new Task("honey", "Honey + kalonji", "Fitness", "Diet", "Early morning, empty stomach: 1 tsp honey with a pinch (¼–½ tsp) of kalonji seeds", 1, 6, 30),
        new Task("almonds", "10 almonds (badam)", "Fitness", "Diet", "Soak overnight, peel and eat in the morning", 1, 7, 30),
        new Task("oats", "Oats for breakfast", "Fitness", "Diet", "Cook in milk; add a fruit or a spoon of peanuts for extra protein", 1, 8, 30),
        new Task("egg", "1 egg", "Fitness", "Diet", "Any time today: boiled or omelette", 1, 13, 0),
        new Task("protein", "Protein serving", "Fitness", "Diet", "Lunch or dinner: dal, chana, rajma, soya chunks, paneer, curd or sprouts", 1, 13, 30),
        new Task("fruit", "2 fruits", "Fitness", "Diet", "Any two: guava, orange, apple, banana", 1, 16, 0),
        new Task("dates", "2 dates", "Fitness", "Diet", "Evening snack", 1, 16, 30),
        new Task("movement", "Move today", "Fitness", "Exercise", "Minimum 3,000 steps OR 10 minutes treadmill / cycling", 1, 18, 30)
    );
    static Task byId(String id) {
        if(id==null) return null;
        for(Task t:ALL) if(t.id.equals(id)) return t;
        return null;
    }
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("routine", Context.MODE_PRIVATE); }
    private static LocalDate lastDate(Context c, Task t) {
        String stamp=prefs(c).getString("done_"+t.id, "");
        if(stamp.isEmpty()) return null;
        try { return LocalDate.parse(stamp); } catch(Exception ignored) { return null; }
    }
    static boolean complete(Context c, Task t) {
        LocalDate last=lastDate(c,t);
        return last!=null && last.plusDays(t.intervalDays).isAfter(LocalDate.now());
    }
    /** Days until the task is due again; 0 or less means due now. Only meaningful after a completion. */
    static long daysLeft(Context c, Task t) {
        LocalDate last=lastDate(c,t);
        if(last==null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), last.plusDays(t.intervalDays));
    }
    static String nextDue(Context c, Task t) {
        LocalDate last=lastDate(c,t);
        return last==null ? "" : last.plusDays(t.intervalDays).toString();
    }
    static String last(Context c, Task t) { return prefs(c).getString("done_"+t.id, ""); }
    static void mark(Context c, Task t) { prefs(c).edit().putString("done_"+t.id, LocalDate.now().toString()).apply(); }
    static void undo(Context c, Task t) { prefs(c).edit().remove("done_"+t.id).apply(); }
    static List<Task> due(Context c) {
        List<Task> result=new ArrayList<>(); for(Task t:ALL) if(!complete(c,t)) result.add(t); return result;
    }
}
