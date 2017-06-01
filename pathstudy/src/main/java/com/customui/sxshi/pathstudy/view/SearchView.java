package com.customui.sxshi.pathstudy.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by sxshi on 2017/5/31.
 */

public class SearchView extends View {
    private Paint mPaint;
    private final static int DEFAULTWIDTH = 500;
    private PathMeasure mPathMeasure;
    private int mWidth;
    private int mHeight;
    private Path pathSmail;
    private Path pathBig = new Path();
    private float currentValue;
    private States mCurrentState = States.NONE;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initPath();
        startAnimation();
        mCurrentState = States.STARTING;
    }

    private void initPath() {
        mPathMeasure = new PathMeasure();
        pathSmail = new Path();
        pathBig = new Path();
        //1.画出中间的圆
        RectF rectF_smail = new RectF(-50, -50, 50, 50);
        pathSmail.addArc(rectF_smail, 45f, 359.9f);
        //2.画出外面
        // 的圆
        RectF rectF_big = new RectF(-100, -100, 100, 100);
        pathBig.addArc(rectF_big, 45f, -359.9f);
        //3画出把手 然后连外圆上点
        mPathMeasure.setPath(pathBig, false);
        float[] pos = new float[2];
        mPathMeasure.getPosTan(0, pos, null);
        pathSmail.lineTo(pos[0], pos[1]);
        Log.d("TAG", "pos[0]=" + pos[0] + " pos[1]=" + pos[1]);
    }

    /**
     * 准备开始结束。
     */
    private static enum States {
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_NONE, mPaint);
        canvas.translate(mWidth / 2, mHeight / 2);
        mPaint.setColor(Color.BLUE);
        switch (mCurrentState) {
            case NONE:
                canvas.drawPath(pathSmail, mPaint);
                break;
            case STARTING:
                mPathMeasure.setPath(pathSmail, false);
                Path dst = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * currentValue, mPathMeasure.getLength(), dst, true);
                mPaint.setColor(Color.RED);
                canvas.drawPath(dst, mPaint);
                break;
            case SEARCHING:
                mPathMeasure.setPath(pathBig, false);
                Path dst2 = new Path();
                float stop = mPathMeasure.getLength() * currentValue;
                float start = (float) (stop - ((0.5 - Math.abs(currentValue - 0.5)) * 200f));
                mPathMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            case ENDING:
                mPathMeasure.setPath(pathSmail, false);
                Path dst3 = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * currentValue, mPathMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                break;
        }
    }

    /**
     * init paint
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasure(DEFAULTWIDTH, widthMeasureSpec);
        int height = getMeasure(DEFAULTWIDTH, heightMeasureSpec);
//        setMeasuredDimension(width, height);
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

    /**
     * 开始动画
     */
    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(3000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }
}
