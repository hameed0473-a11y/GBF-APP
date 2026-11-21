package com.shahul.groomingfitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

final class Outfits {
    private static final String[] TYPES={"Shirt","Trousers","T-shirt","Night pants"};
    private static final String[] COLORS={"White","Black","Navy","Blue","Light blue","Grey","Beige","Brown","Olive","Green","Pink","Red","Yellow","Cream"};
    private static final int[] SWATCHES={0xFFFFFFFF,0xFF222222,0xFF172B50,0xFF2866AA,0xFF92BEDB,0xFF888B91,0xFFD4C3A5,0xFF78503B,0xFF66714D,0xFF388058,0xFFDF93A8,0xFFB94343,0xFFE8BE45,0xFFF2E6CB};
    private static final class Garment {
        final String type,color,name;
        Garment(String type,String color,String name) { this.type=type;this.color=color;this.name=name; }
        String label() { return name.isEmpty()?color+" "+type:name+" ("+color+" "+type+")"; }
    }
    private static List<Garment> load(Context c) {
        List<Garment> items=new ArrayList<>();
        try {
            JSONArray a=new JSONArray(c.getSharedPreferences("wardrobe",0).getString("items","[]"));
            for(int i=0;i<a.length();i++) {
                JSONObject o=a.getJSONObject(i);
                String type=o.optString("type"), color=o.optString("color");
                if(index(TYPES,type)>=0 && index(COLORS,color)>=0) items.add(new Garment(type,color,o.optString("name")));
            }
        } catch(Exception ignored) { /* Bad local data is ignored. */ }
        return items;
    }
    static int count(Context c) { return load(c).size(); }
    private static int index(String[] options,String value) {
        for(int i=0;i<options.length;i++) if(options[i].equals(value)) return i;
        return -1;
    }
    private static void save(Context c,List<Garment> items) {
        JSONArray a=new JSONArray();
        for(Garment item:items) { JSONObject o=new JSONObject();
            try { o.put("type",item.type);o.put("color",item.color);o.put("name",item.name);a.put(o); }
            catch(Exception ignored) { }
        }
        c.getSharedPreferences("wardrobe",0).edit().putString("items",a.toString()).apply();
    }
    static void render(Activity activity,LinearLayout content,Runnable refresh) {
        List<Garment> items=load(activity);
        TextView intro=text(activity,"Add the clothes you own, then choose an outfit. Match a bright color with a neutral for an easy combination.",15);
        content.addView(intro);
        Button add=Ui.lavender(new Button(activity));add.setText("+  Add clothing");
        LinearLayout.LayoutParams al=new LinearLayout.LayoutParams(-1,-2);al.setMargins(0,0,0,dp(activity,6));content.addView(add,al);
        add.setOnClickListener(v->addDialog(activity,refresh));
        if(items.isEmpty()) { content.addView(text(activity,"Your wardrobe is empty. Add a shirt and trousers, or a T-shirt and night pants.",15));return; }
        content.addView(text(activity,"My wardrobe",19));
        for(int i=0;i<items.size();i++) {
            final int position=i; Garment item=items.get(i);
            LinearLayout row=new LinearLayout(activity);row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            View swatch=new View(activity);swatch.setBackgroundColor(SWATCHES[index(COLORS,item.color)]);
            LinearLayout.LayoutParams sw=new LinearLayout.LayoutParams(dp(activity,22),dp(activity,22));sw.setMargins(0,0,dp(activity,10),0);row.addView(swatch,sw);
            TextView title=text(activity,item.label(),15);row.addView(title,new LinearLayout.LayoutParams(0,-2,1));
            Button remove=Ui.lavenderDanger(new Button(activity));remove.setText("Remove");
            LinearLayout.LayoutParams rl=new LinearLayout.LayoutParams(-2,-2);rl.setMargins(0,dp(activity,4),0,dp(activity,4));row.addView(remove,rl);
            remove.setOnClickListener(v->new AlertDialog.Builder(activity).setTitle("Remove clothing?")
                .setMessage(item.label()).setNegativeButton("Cancel",null).setPositiveButton("Remove",(d,w)->{
                    List<Garment> current=load(activity);if(position<current.size()){current.remove(position);save(activity,current);refresh.run();}
                }).show());
            content.addView(row);
        }
        content.addView(text(activity,"Outfit suggestions",19));
        int count=0;
        for(Garment top:items) for(Garment bottom:items) {
            boolean casual=top.type.equals("T-shirt") && bottom.type.equals("Night pants");
            boolean day=top.type.equals("Shirt") && bottom.type.equals("Trousers");
            if((day||casual) && matches(top.color,bottom.color)) {
                String occasion=day?"Day outfit":"At home / sleepwear";
                content.addView(text(activity,"• "+top.label()+" + "+bottom.label()+"  ·  "+occasion,15));
                if(++count>=12) break;
            }
            if(count>=12) break;
        }
        if(count==0) content.addView(text(activity,"Add a matching shirt and trousers, or T-shirt and night pants. Navy, black, beige and grey are easy starting colors.",15));
        content.addView(text(activity,"Ideas are based on simple color rules, not a camera analysis. You can remove and re-add items to change your wardrobe.",13));
    }
    private static boolean matches(String a,String b) {
        if(a.equals(b)) return a.equals("Black") || a.equals("Navy");
        if(a.equals("White") || a.equals("Cream") || b.equals("White") || b.equals("Cream")) return true;
        if(b.equals("Black") || b.equals("Navy") || b.equals("Grey") || b.equals("Beige")) return true;
        if(a.equals("Black") || a.equals("Navy") || a.equals("Grey") || a.equals("Beige")) return true;
        return (a.equals("Light blue") && b.equals("Brown")) || (a.equals("Blue") && b.equals("Olive"));
    }
    private static void addDialog(Activity activity,Runnable refresh) {
        LinearLayout form=new LinearLayout(activity);form.setOrientation(LinearLayout.VERTICAL);form.setPadding(dp(activity,20),0,dp(activity,20),0);
        Spinner type=new Spinner(activity),color=new Spinner(activity);
        type.setAdapter(new ArrayAdapter<>(activity,android.R.layout.simple_spinner_dropdown_item,TYPES));
        color.setAdapter(new ArrayAdapter<>(activity,android.R.layout.simple_spinner_dropdown_item,COLORS));
        EditText name=new EditText(activity);name.setSingleLine(true);name.setHint("Optional name, e.g. office shirt");
        form.addView(text(activity,"Clothing type",14));form.addView(type);form.addView(text(activity,"Color",14));form.addView(color);form.addView(name);
        new AlertDialog.Builder(activity).setTitle("Add to wardrobe").setView(form).setNegativeButton("Cancel",null)
            .setPositiveButton("Save",(d,w)->{
                List<Garment> items=load(activity);
                items.add(new Garment((String)type.getSelectedItem(),(String)color.getSelectedItem(),name.getText().toString().trim()));
                save(activity,items);refresh.run();
            }).show();
    }
    private static int dp(Context c,int v) { return (int)(v*c.getResources().getDisplayMetrics().density+.5f); }
    private static TextView text(Activity a,String text,int size) {
        TextView view=new TextView(a);view.setText(text);view.setTextSize(size);view.setTextColor(Color.rgb(26,65,57));view.setPadding(dp(a,4),dp(a,10),dp(a,4),dp(a,10));return view;
    }
}
