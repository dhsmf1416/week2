package com.example.q.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class TouchEventView extends android.support.v7.widget.AppCompatImageView {
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public GalleryActivity upper;
    public TouchEventView(Context context)
    {
        super(context);
    }

    public TouchEventView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }



}