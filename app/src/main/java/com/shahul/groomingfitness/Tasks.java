package com.shahul.groomingfitness;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The user's task list. Stored on the device as JSON and seeded with the default routine on first launch. */
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
        int minuteOfDay() { return hour*60+minute; }
        String repeatText() { return intervalDays<=1 ? "Daily" : "Every "+intervalDays+" days"; }
    }
    static final String[] GROUPS={"Grooming","Beauty","Fitness"};
    // Fitness diet tasks are listed in the order they should be eaten through the day.
    private static final List<Task> DEFAULTS = Arrays.asList(
        new Task("haircut", "Haircut", "Grooming", "", "Visit the salon", 30, 10, 0),
        new Task("nails", "Trim nails", "Grooming", "", "Hands and feet", 7, 18, 0),
        new Task("detan", "De-tan / skin care", "Beauty", "", "Use a gentle pack; avoid harsh bleach or scrubbing", 7, 18, 0),
        new Task("honey", "Honey + kalonji", "Fitness", "Diet", "Early morning, empty stomach: 1 tsp honey with a pinch (¼–½ tsp) of kalonji seeds", 1, 6, 30),
        new Task("almonds", "10 almonds (badam)", "Fitness", "Diet", "Soak overnight, peel and eat in the morning", 1, 7, 30),
        new Task("oats", "Oats for breakfast", "Fitness", "Diet", "Cook in milk; add a fruit or a spoon of peanuts for extra protein", 1, 8, 30),
        new Task("egg", "1 egg", "Fitness", "Diet", "Any time today: boiled or omelette", 1, 13, 0),
        new Task("protein", "Protein serving", "Fitness", "Diet", "Lunch or dinner: dal, chana, rajma, soya chunks, paneer, curd or sprouts", 1, 13, 30),
        new Task("fruit", "2 fruits", "Fitness", "Diet", "Any two: guava, orange, apple, banana", 1, 16, 0),
        new Task("dates", "2 dates", "Fitness", "Diet", "Evening snack", 1, 16, 30),
        new Task("movement", "Move today", "Fitness", "Exercise", "Minimum 3,000 steps OR 10 minutes treadmill / cycling", 1, 18, 30)
    );
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("routine", Context.MODE_PRIVATE); }

    // ---- Task list storage ----
    static List<Task> all(Context c) {
        String json=prefs(c).getString("tasks", null);
        if(json==null) { save(c, DEFAULTS); return new ArrayList<>(DEFAULTS); }
        List<Task> items=new ArrayList<>();
        try {
            JSONArray a=new JSONArray(json);
            for(int i=0;i<a.length();i++) {
                JSONObject o=a.getJSONObject(i);
                String id=o.optString("id"), title=o.optString("title"), group=o.optString("group");
                if(id.isEmpty() || title.isEmpty() || !Arrays.asList(GROUPS).contains(group)) continue;
                items.add(new Task(id, title, group, o.optString("section"), o.optString("detail"),
                    clamp(o.optInt("days",1),1,365), clamp(o.optInt("hour",9),0,23), clamp(o.optInt("minute",0),0,59)));
            }
        } catch(Exception ignored) { /* Corrupt data: show what was parsed. */ }
        return items;
    }
    private static int clamp(int v,int lo,int hi) { return Math.max(lo,Math.min(hi,v)); }
    private static void save(Context c, List<Task> items) {
        JSONArray a=new JSONArray();
        for(Task t:items) {
            try {
                JSONObject o=new JSONObject();
                o.put("id",t.id); o.put("title",t.title); o.put("group",t.group); o.put("section",t.section);
                o.put("detail",t.detail); o.put("days",t.intervalDays); o.put("hour",t.hour); o.put("minute",t.minute);
                a.put(o);
            } catch(Exception ignored) { }
        }
        prefs(c).edit().putString("tasks",a.toString()).apply();
    }
    static Task add(Context c, String title, String group, String section, String detail, int days, int hour, int minute) {
        Task t=new Task("c"+System.currentTimeMillis(), title, group, section, detail, clamp(days,1,365), hour, minute);
        List<Task> items=all(c); items.add(t); save(c,items);
        return t;
    }
    static void delete(Context c, Task t) {
        List<Task> items=all(c);
        for(int i=items.size()-1;i>=0;i--) if(items.get(i).id.equals(t.id)) items.remove(i);
        save(c,items);
        prefs(c).edit().remove("done_"+t.id).apply();
    }
    static Task byId(Context c, String id) {
        if(id==null) return null;
        for(Task t:all(c)) if(t.id.equals(id)) return t;
        return null;
    }

    // ---- Completion tracking ----
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
}
