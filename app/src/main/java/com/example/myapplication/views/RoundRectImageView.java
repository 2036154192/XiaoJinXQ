package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;



public class RoundRectImageView extends AppCompatImageView {

    public RoundRectImageView(@NonNull Context context) {
        this(context,null);
    }

    public RoundRectImageView(@NonNull  Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundRectImageView(@NonNull  Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float roundRation = 0.1f;
    private Path mPath;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPath == null){
            mPath = new Path();
            mPath.addRoundRect(new RectF(0,0,getWidth(),getHeight()),
                    roundRation * getWidth(),roundRation * getHeight(),Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(mPath);
        super.onDraw(canvas);
        canvas.restore();
    }
}
