package com.shahul.groomingfitness;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int GREEN_DARK=Color.rgb(26,65,57), GREEN=Color.rgb(33,102,82), GREEN_SOFT=Color.rgb(226,242,232), RED=Color.rgb(178,58,52);
    private LinearLayout root, content;
    private String selected="Grooming";
    private boolean guideOpen=false;
    private List<Tasks.Task> tasks=new ArrayList<>();
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
    private static String time(int hour,int minute) { return String.format(Locale.getDefault(),"%02d:%02d",hour,minute); }
    /** Tasks in one category (and optional Fitness section), earliest reminder first. */
    private List<Tasks.Task> list(String group,String section) {
        List<Tasks.Task> result=new ArrayList<>();
        for(Tasks.Task t:tasks) if(t.group.equals(group) && (section==null || t.section.equals(section))) result.add(t);
        result.sort(Comparator.comparingInt(Tasks.Task::minuteOfDay));
        return result;
    }
    private void draw() {
        tasks=Tasks.all(this);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(248,249,246));
        root.setPadding(dp(18),dp(20),dp(18),0); setContentView(root);
        TextView heading=label("GBF",30,GREEN_DARK); heading.setTypeface(null,Typeface.BOLD); heading.setLetterSpacing(.08f); root.addView(heading);
        TextView subtitle=label("Grooming · Beauty · Fitness\nTap a task when you finish it. Your progress stays on this device.",14,Color.DKGRAY);
        subtitle.setPadding(0,dp(4),0,dp(16)); root.addView(subtitle);
        int done=0; for(Tasks.Task t:tasks) if(Tasks.complete(this,t)) done++;
        TextView progress=label(done+" of "+tasks.size()+" tasks currently complete",16,GREEN_DARK);
        progress.setPadding(dp(12),dp(12),dp(12),dp(12)); progress.setBackgroundColor(GREEN_SOFT); root.addView(progress);
        // A horizontal row keeps all four sections reachable on smaller phones.
        HorizontalScrollView tabRow=new HorizontalScrollView(this);
        tabRow.setHorizontalScrollBarEnabled(false); root.addView(tabRow);
        LinearLayout tabs=new LinearLayout(this); tabs.setPadding(0,dp(16),0,dp(10)); tabRow.addView(tabs);
        for(String tab:new String[]{"Grooming","Beauty","Fitness","Outfits"}) {
            Button b=new Button(this); b.setText(tab); b.setAllCaps(false); b.setTextSize(12);
            b.setTextColor(tab.equals(selected)?Color.WHITE:GREEN_DARK);
            b.setBackgroundTintList(ColorStateList.valueOf(tab.equals(selected)?GREEN:Color.rgb(222,232,225)));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(105),dp(48)); lp.setMargins(dp(2),0,dp(2),0);
            tabs.addView(b,lp); b.setOnClickListener(v->{ selected=tab; draw(); });
        }
        ScrollView scroll=new ScrollView(this); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); scroll.addView(content);
        switch(selected) {
            case "Outfits": Outfits.render(this,content,this::draw); break;
            case "Fitness": drawFitness(); break;
            default: addTaskList(list(selected,null),selected,"","Add "+selected.toLowerCase(Locale.ROOT)+" task");
        }
        TextView footer=label("Notifications may arrive a little late when Android saves battery.",12,Color.GRAY);
        footer.setPadding(dp(5),dp(18),dp(5),dp(26)); content.addView(footer);
    }
    private void sectionHeader(String title,String note) {
        TextView h=label(title,19,GREEN_DARK); h.setTypeface(null,Typeface.BOLD); h.setPadding(dp(4),dp(10),dp(4),note.isEmpty()?dp(8):dp(2)); content.addView(h);
        if(!note.isEmpty()) { TextView n=label(note,13,Color.DKGRAY); n.setPadding(dp(4),0,dp(4),dp(10)); content.addView(n); }
    }
    private void addTaskList(List<Tasks.Task> items,String group,String section,String addLabel) {
        if(items.isEmpty()) {
            TextView empty=label("No tasks here yet. Tap the button below to add one.",14,Color.GRAY);
            empty.setPadding(dp(4),dp(4),dp(4),dp(10)); content.addView(empty);
        }
        for(Tasks.Task t:items) addTask(t);
        Button add=new Button(this); add.setAllCaps(false); add.setText("+  "+addLabel); add.setTextColor(GREEN);
        add.setBackgroundTintList(ColorStateList.valueOf(GREEN_SOFT));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,0,0,dp(12)); content.addView(add,lp);
        add.setOnClickListener(v->addDialog(group,section));
    }
    private void drawFitness() {
        sectionHeader("Exercise","Walk 3,000 steps or more, OR do 10 minutes on a treadmill or bicycle. Tick it yourself after either one.");
        addTaskList(list("Fitness","Exercise"),"Fitness","Exercise","Add exercise");
        List<Tasks.Task> diet=list("Fitness","Diet");
        int dietDone=0; for(Tasks.Task t:diet) if(Tasks.complete(this,t)) dietDone++;
        sectionHeader("Daily diet — in order","Today: "+dietDone+" of "+diet.size()+" done. Protein-rich foods that support healthy hair.");
        addTaskList(diet,"Fitness","Diet","Add diet item");
        // Fitness tasks with no section (none by default) still need to be visible and deletable.
        List<Tasks.Task> other=new ArrayList<>();
        for(Tasks.Task t:list("Fitness",null)) if(!t.section.equals("Exercise") && !t.section.equals("Diet")) other.add(t);
        if(!other.isEmpty()) { sectionHeader("Other",""); for(Tasks.Task t:other) addTask(t); }
        addFoodGuide();
    }
    private void addFoodGuide() {
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14),dp(12),dp(14),dp(12)); card.setBackgroundColor(Color.rgb(255,248,228));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.setMargins(0,dp(6),0,dp(10)); content.addView(card,cp);
        TextView title=label((guideOpen?"▾  ":"▸  ")+"Best affordable protein for hair growth",16,GREEN_DARK); title.setTypeface(null,Typeface.BOLD);
        card.addView(title);
        card.setOnClickListener(v->{ guideOpen=!guideOpen; draw(); });
        card.setContentDescription(guideOpen?"Hide hair growth food guide":"Show hair growth food guide");
        if(!guideOpen) { TextView hint=label("Tap to see cheap, easy-to-find foods",13,Color.DKGRAY); hint.setPadding(0,dp(4),0,0); card.addView(hint); return; }
        String guide=
            "Hair is made of protein (keratin), so a protein food at every meal matters most. Iron, zinc, biotin and vitamin D help too.\n\n"+
            "Top picks (cheap and easy to find)\n"+
            "• Eggs — complete protein plus biotin. 1–2 a day.\n"+
            "• Soya chunks — the cheapest high-protein food. ½ cup cooked in curry.\n"+
            "• Dal / lentils — moong, masoor, toor. Protein, iron and zinc. 1 bowl daily.\n"+
            "• Chana, rajma, lobia — protein and iron. Roasted chana is a great snack.\n"+
            "• Sprouted moong — protein plus vitamin C. Easy at home.\n"+
            "• Peanuts — protein plus biotin. A small handful.\n"+
            "• Curd, milk, paneer — protein and calcium.\n"+
            "• Fish (sardine, mackerel) or chicken — if you eat non-veg; fish adds omega-3.\n\n"+
            "Helpers\n"+
            "• Pumpkin, flax or sunflower seeds — zinc and omega-3. 1 spoon a day.\n"+
            "• Spinach and green leaves — iron.\n"+
            "• Amla, guava or orange — vitamin C, which helps your body absorb iron.\n"+
            "• 10–15 minutes of morning sunlight — vitamin D.\n\n"+
            "Good to know\n"+
            "• Results take 3–6 months of consistent eating.\n"+
            "• Honey is a sugar, so keep it to 1 teaspoon. Kalonji is a traditional remedy; a small pinch is enough.\n"+
            "• If hair fall is heavy or patchy, see a doctor. A blood test (iron, vitamin D, thyroid) finds the cause. Take supplements only if a test shows a deficiency.\n"+
            "• Skip any food you are allergic to.";
        TextView body=label(guide,14,Color.rgb(60,60,60)); body.setPadding(0,dp(8),0,0); body.setLineSpacing(dp(2),1f); card.addView(body);
    }
    private void addTask(Tasks.Task t) {
        boolean checked=Tasks.complete(this,t);
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14),dp(12),dp(14),dp(12)); card.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.setMargins(0,0,0,dp(10)); content.addView(card,cp);
        TextView name=label((checked?"✓  ":"○  ")+t.title,18,checked?GREEN:GREEN_DARK); name.setTypeface(null,Typeface.BOLD); card.addView(name);
        String info=(t.detail.isEmpty()?"":t.detail+"\n")+t.repeatText()+"  •  Reminder "+time(t.hour,t.minute);
        TextView infoView=label(info,13,Color.DKGRAY); infoView.setPadding(0,dp(5),0,dp(6)); card.addView(infoView);
        String status=status(t,checked);
        if(!status.isEmpty()) {
            TextView s=label(status,12,checked?Color.GRAY:Color.rgb(170,90,20)); card.addView(s);
        }
        LinearLayout buttons=new LinearLayout(this); buttons.setGravity(Gravity.CENTER_VERTICAL); card.addView(buttons);
        Button action=new Button(this); action.setAllCaps(false); action.setText(checked?"Undo completion":"Mark complete");
        if(!checked) { action.setTextColor(Color.WHITE); action.setBackgroundTintList(ColorStateList.valueOf(GREEN)); }
        action.setContentDescription((checked?"Undo ":"Complete ")+t.title);
        buttons.addView(action,new LinearLayout.LayoutParams(0,-2,1));
        action.setOnClickListener(v->{
            if(Tasks.complete(this,t)) new AlertDialog.Builder(this).setTitle("Undo completion?")
                .setMessage("This removes the recorded completion date for "+t.title+".")
                .setNegativeButton("Cancel",null).setPositiveButton("Undo",(d,w)->{Tasks.undo(this,t);draw();}).show();
            else { Tasks.mark(this,t);draw(); }
        });
        Button delete=new Button(this,null,android.R.attr.borderlessButtonStyle); delete.setAllCaps(false); delete.setText("Delete");
        delete.setTextColor(RED); delete.setContentDescription("Delete "+t.title);
        buttons.addView(delete,new LinearLayout.LayoutParams(-2,-2));
        delete.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete \""+t.title+"\"?")
            .setMessage("This removes the task, its reminder and its completion history.")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Delete",(d,w)->{ Reminders.cancel(this,t); Tasks.delete(this,t); draw(); }).show());
    }
    private String status(Tasks.Task t,boolean checked) {
        String last=Tasks.last(this,t);
        if(t.intervalDays<=1) return checked?"Done today":"";
        if(last.isEmpty()) return "Not done yet: mark it after your first one";
        if(checked) {
            long left=Tasks.daysLeft(this,t);
            return "Last done "+last+"  •  Next due "+Tasks.nextDue(this,t)+" (in "+left+(left==1?" day)":" days)");
        }
        return "Due now  •  Last done "+last;
    }
    private void addDialog(String group,String section) {
        String where=section.isEmpty()?group:group+" · "+section;
        final int[] when={9,0};
        LinearLayout form=new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(dp(22),dp(8),dp(22),0);
        EditText title=new EditText(this); title.setSingleLine(true); title.setHint("Task name, e.g. Face wash");
        title.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); form.addView(title);
        EditText detail=new EditText(this); detail.setHint("Details (optional)");
        detail.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE); form.addView(detail);
        TextView repeatLabel=label("Repeat every (days)",13,Color.DKGRAY); repeatLabel.setPadding(dp(4),dp(12),0,0); form.addView(repeatLabel);
        EditText days=new EditText(this); days.setInputType(InputType.TYPE_CLASS_NUMBER); days.setText(group.equals("Fitness")?"1":"7"); form.addView(days);
        LinearLayout quick=new LinearLayout(this); form.addView(quick);
        for(int n:new int[]{1,7,15,30}) {
            Button q=new Button(this,null,android.R.attr.borderlessButtonStyle); q.setAllCaps(false); q.setTextColor(GREEN);
            q.setText(n==1?"Daily":n+" days"); quick.addView(q,new LinearLayout.LayoutParams(0,-2,1));
            q.setOnClickListener(v->days.setText(String.valueOf(n)));
        }
        Button pick=new Button(this); pick.setAllCaps(false); pick.setText("Reminder time: "+time(when[0],when[1])); form.addView(pick);
        pick.setOnClickListener(v->new TimePickerDialog(this,(tp,h,m)->{ when[0]=h; when[1]=m; pick.setText("Reminder time: "+time(h,m)); },
            when[0],when[1],DateFormat.is24HourFormat(this)).show());
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Add to "+where).setView(form)
            .setNegativeButton("Cancel",null).setPositiveButton("Add",null).create();
        dialog.show();
        // Validate before closing, so a missing name does not lose what was typed.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            String name=title.getText().toString().trim();
            int n; try { n=Integer.parseInt(days.getText().toString().trim()); } catch(NumberFormatException e) { n=0; }
            if(name.isEmpty()) { title.setError("Enter a task name"); return; }
            if(n<1 || n>365) { days.setError("Enter 1 to 365 days"); return; }
            Tasks.Task t=Tasks.add(this,name,group,section,detail.getText().toString().trim(),n,when[0],when[1]);
            Reminders.schedule(this,t);
            dialog.dismiss(); draw();
            Toast.makeText(this,"Added "+name,Toast.LENGTH_SHORT).show();
        });
    }
}
