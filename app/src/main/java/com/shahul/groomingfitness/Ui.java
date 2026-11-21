package com.shahul.groomingfitness;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.widget.Button;

/** Shared look: rounded shapes and the lavender buttons used inside each category. */
final class Ui {
    static final int GREEN_DARK=0xFF1A4139, GREEN=0xFF216652, GREEN_SOFT=0xFFE2F2E8, RED=0xFFB23A34;
    static final int LAVENDER=0xFFE6DDFB, LAVENDER_STRONG=0xFFCDBDF6, LAVENDER_LIGHT=0xFFF3EFFD, PURPLE_TEXT=0xFF43307A;
    private static final int RIPPLE=0x33433078;

    static int dp(Context c,float v) { return (int)(v*c.getResources().getDisplayMetrics().density+.5f); }

    static GradientDrawable rounded(Context c,int color,float radiusDp) {
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(c,radiusDp)); return d;
    }
    static GradientDrawable rounded(Context c,int color,float radiusDp,int strokeColor) {
        GradientDrawable d=rounded(c,color,radiusDp); d.setStroke(dp(c,1),strokeColor); return d;
    }
    /** Rounded background with a touch ripple clipped to the same shape. */
    static Drawable pressable(Context c,Drawable shape) {
        return new RippleDrawable(ColorStateList.valueOf(RIPPLE),shape,null);
    }
    private static void style(Button b,int fill,int text,boolean bold) {
        Context c=b.getContext();
        b.setBackground(pressable(c,rounded(c,fill,14)));
        b.setTextColor(text); b.setAllCaps(false); b.setStateListAnimator(null);
        b.setTypeface(Typeface.create(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL));
        b.setMinHeight(dp(c,46)); b.setMinimumHeight(dp(c,46));
        b.setPadding(dp(c,16),dp(c,8),dp(c,16),dp(c,8));
    }
    /** Standard lavender button (curved rectangle). */
    static Button lavender(Button b) { style(b,LAVENDER,PURPLE_TEXT,false); return b; }
    /** Main action on a card, e.g. "Mark complete". */
    static Button lavenderStrong(Button b) { style(b,LAVENDER_STRONG,PURPLE_TEXT,true); return b; }
    /** Secondary action, e.g. "Undo completion". */
    static Button lavenderLight(Button b) { style(b,LAVENDER_LIGHT,PURPLE_TEXT,false); return b; }
    /** Lavender button with red text for deleting. */
    static Button lavenderDanger(Button b) { style(b,LAVENDER_LIGHT,RED,false); return b; }
}
