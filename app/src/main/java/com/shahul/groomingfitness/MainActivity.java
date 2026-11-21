package com.shahul.groomingfitness;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private LinearLayout root, content;
    private String selected="Grooming";
    private int dp(float value) { return (int)(value*getResources().getDisplayMetrics().density+.5f); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Reminders.scheduleAll(this);
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
        draw();
    }
    @Override public void onResume() { super.onResume(); if(root!=null) draw(); }
    private TextView label(String value,int size,int color) {
        TextView v=new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(color); return v;
    }
    private void draw() {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(248,249,246));
        root.setPadding(dp(18),dp(20),dp(18),0); setContentView(root);
        TextView heading=label("My daily care",27,Color.rgb(26,65,57)); heading.setTypeface(null,1); root.addView(heading);
        TextView subtitle=label("Tap a task when you finish it. Your progress stays on this device.",14,Color.DKGRAY);
        subtitle.setPadding(0,dp(6),0,dp(18)); root.addView(subtitle);
        int done=Tasks.ALL.size()-Tasks.due(this).size();
        TextView progress=label(done+" of "+Tasks.ALL.size()+" tasks currently complete",16,Color.rgb(26,65,57));
        progress.setPadding(dp(12),dp(12),dp(12),dp(12)); progress.setBackgroundColor(Color.rgb(226,242,232)); root.addView(progress);
        // A horizontal row keeps all four sections reachable on smaller phones.
        android.widget.HorizontalScrollView tabRow=new android.widget.HorizontalScrollView(this);
        tabRow.setHorizontalScrollBarEnabled(false); root.addView(tabRow);
        LinearLayout tabs=new LinearLayout(this); tabs.setPadding(0,dp(18),0,dp(12)); tabRow.addView(tabs);
        for(String tab:new String[]{"Grooming","Beauty","Fitness","Outfits"}) {
            Button b=new Button(this); b.setText(tab); b.setAllCaps(false); b.setTextSize(12);
            b.setTextColor(tab.equals(selected)?Color.WHITE:Color.rgb(26,65,57));
            b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(tab.equals(selected)?Color.rgb(33,102,82):Color.rgb(222,232,225)));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(105),dp(48)); lp.setMargins(dp(2),0,dp(2),0);
            tabs.addView(b,lp); b.setOnClickListener(v->{ selected=tab; draw(); });
        }
        ScrollView scroll=new ScrollView(this); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); scroll.addView(content);
        if(selected.equals("Fitness")) {
            TextView tip=label("Movement: 3,000 steps OR 10 minutes on a treadmill or bicycle. Tick the task yourself after completing either option.\n\nFood suggestions are reminders, not a prescribed diet. Add dal, chickpeas, curd or peanuts as an affordable protein serving.",14,Color.DKGRAY);
            tip.setPadding(dp(8),dp(8),dp(8),dp(18)); content.addView(tip);
        }
        if(selected.equals("Outfits")) Outfits.render(this,content,this::draw);
        else for(Tasks.Task t:Tasks.ALL) if(t.group.equals(selected)) addTask(t);
        TextView footer=label("Reminder times are preset in this first version. Notifications may arrive later when Android limits background activity.",12,Color.GRAY);
        footer.setPadding(dp(5),dp(18),dp(5),dp(26)); content.addView(footer);
    }
    private void addTask(Tasks.Task t) {
        boolean checked=Tasks.complete(this,t);
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14),dp(12),dp(14),dp(12)); card.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.setMargins(0,0,0,dp(10)); content.addView(card,cp);
        TextView name=label((checked?"✓  ":"○  ")+t.title,18,Color.rgb(26,65,57)); name.setTypeface(null,1); card.addView(name);
        TextView info=label(t.detail+"  •  Reminder "+String.format(java.util.Locale.getDefault(),"%02d:%02d",t.hour,t.minute),13,Color.DKGRAY);
        info.setPadding(0,dp(5),0,dp(6)); card.addView(info);
        if(!Tasks.last(this,t).isEmpty()) {
            TextView last=label("Last completed: "+Tasks.last(this,t),12,Color.GRAY); card.addView(last);
        }
        Button action=new Button(this); action.setAllCaps(false); action.setText(checked?"Undo completion":"Mark complete");
        action.setContentDescription((checked?"Undo ":"Complete ")+t.title);
        card.addView(action); action.setOnClickListener(v->{
            if(Tasks.complete(this,t)) new AlertDialog.Builder(this).setTitle("Undo completion?")
                .setMessage("This removes the recorded completion date for "+t.title+".")
                .setNegativeButton("Cancel",null).setPositiveButton("Undo",(d,w)->{Tasks.undo(this,t);draw();}).show();
            else { Tasks.mark(this,t);draw(); }
        });
    }
}
