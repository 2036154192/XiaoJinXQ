package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {

    private int rotateDrgree = 0;
    private boolean mNeedRotate = false;

    public LoadingView(@NonNull @NotNull Context context) {
        this(context,null);
    }

    public LoadingView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        post(new Runnable() {
            @Override
            public void run() {
                rotateDrgree = rotateDrgree <= 360 ? rotateDrgree +=30 : 0 ;
                invalidate();
                if (mNeedRotate){
                    postDelayed(this,100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(rotateDrgree,getWidth() / 2 ,getHeight() / 2);
        super.onDraw(canvas);
    }
}
