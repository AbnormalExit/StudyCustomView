package com.customui.sxshi.pathstudy.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by sxshi on 2017/5/25.
 * 实现太极八卦图
 */

public class BaguaView extends View {
    private Paint mDeafultPaint;
    private float currentValue;
    private Matrix mMatrix;
    private final static int DEFAULTWIDTH = 200;

    public BaguaView(Context context) {
        this(context, null);
    }

    public BaguaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();
        Path path0 = new Path();
        int radius_big = getWidth() / 2;
        int radius_smal = radius_big / 2;
        Log.d("tag", "width=" + getWidth());
        Log.d("tag", "getheight=" + getHeight());
        path0.addRect(0, -radius_big, radius_big, radius_big, Path.Direction.CW);
        path1.addCircle(0, 0, radius_big, Path.Direction.CW);
        path2.addRect(0, -radius_big, radius_big, radius_big, Path.Direction.CW);
        path3.addCircle(0, -radius_smal, radius_smal, Path.Direction.CW);
        path4.addCircle(0, radius_smal, radius_smal, Path.Direction.CCW);
//        //画左边
        path1.op(path2, Path.Op.DIFFERENCE);
        path1.op(path3, Path.Op.UNION);
        path1.op(path4, Path.Op.DIFFERENCE);
        mDeafultPaint.setColor(Color.BLACK);
        canvas.drawPath(path1, mDeafultPaint);

        mDeafultPaint.setColor(Color.WHITE);
        canvas.drawCircle(0, -radius_smal, radius_smal / 2, mDeafultPaint);
//        //画右边
        path1.addCircle(0, 0, radius_big, Path.Direction.CW);
        path1.op(path2, Path.Op.INTERSECT);
        path1.op(path3, Path.Op.DIFFERENCE);
        path1.op(path4, Path.Op.UNION);
        mDeafultPaint.setColor(Color.WHITE);
        canvas.drawPath(path1, mDeafultPaint);
        mDeafultPaint.setColor(Color.BLACK);
        canvas.drawCircle(0, radius_smal, radius_smal / 2, mDeafultPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("tag", "width=" + w);
        Log.d("tag", "getheight=" +h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasure(DEFAULTWIDTH, widthMeasureSpec);
        int height = getMeasure(DEFAULTWIDTH, heightMeasureSpec);
        //保证画出来的是正方形
        if (width < height) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }
    }

    /**
     * 测量布局
     *
     * @param measureSpec
     * @return
     */
    public int getMeasure(int defaultSize, int measureSpec) {
        int resultSize = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            resultSize = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            resultSize = Math.min(defaultSize, size);
        } else if (mode == MeasureSpec.UNSPECIFIED) {
            resultSize = size;
        }
        return resultSize;
    }

    private void init() {
        mDeafultPaint = new Paint();
        mDeafultPaint.setStrokeWidth(1f);
        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDeafultPaint.setColor(Color.BLACK);
        mMatrix = new Matrix();
        startAnimation();
    }

    private void startAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setFillAfter(false);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());//不停顿
        startAnimation(rotateAnimation);
    }
}
