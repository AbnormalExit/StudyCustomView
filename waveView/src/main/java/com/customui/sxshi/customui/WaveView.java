package com.customui.sxshi.customui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.animation.LinearInterpolator;

import com.customui.sxshi.R;

/**
 * Created by sxshi on 2017/5/25.
 */

public class WaveView extends View {
    private Paint mWavePaint;
    private Path mPath;

    private final int WAVE_RADIUS = 1000;//波浪直径
    private Bitmap mBoatBitmap;

    private float faction = 1;
    private int mDeltaX;

    private PathMeasure mPathMeasure;
    private float[] pos;
    private float[] tan;
    private Matrix mMatrix;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPath = new Path();
        mWavePaint = new Paint();
        mWavePaint.setColor(Color.RED);
        mWavePaint.setStrokeWidth(2f);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);


        mPath = new Path();
        pos = new float[2];
        tan = new float[2];
        mMatrix = new Matrix();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;//压缩图片到原来的1/2
        mBoatBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg, options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        int orginY = 800;//初始化波浪高度
        int halfWaveLength = WAVE_RADIUS / 2;//半径
        mPath.moveTo(-WAVE_RADIUS + mDeltaX, orginY);
        //画出getWidth()+WAVE_RADIUS 上面的的波纹
        for (int i = -WAVE_RADIUS; i < getWidth() + WAVE_RADIUS; i += WAVE_RADIUS) {
            mPath.rQuadTo(halfWaveLength / 2, 60, halfWaveLength, 0);
            mPath.rQuadTo(halfWaveLength / 2, -60, halfWaveLength, 0);
        }
        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();
        canvas.drawPath(mPath, mWavePaint);

        mPathMeasure = new PathMeasure(mPath, false);
        float length = mPathMeasure.getLength();
        mMatrix.reset();
        boolean posTan = mPathMeasure.getPosTan(length * faction, pos, tan);
        Log.d("tag", "length = " + length);
        Log.d("tag", "pos[0] = " + pos[0] + "pos[1] = " + pos[1]);
        Log.d("tag", "tan[0] = " + tan[0] + "tan[1] = " + tan[1]);
        Log.d("tag", "posTan = " + posTan);
        if (posTan) {
            mPathMeasure.getMatrix(length * faction, mMatrix, PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
            mMatrix.preTranslate(-mBoatBitmap.getWidth() / 2, -mBoatBitmap.getHeight()+5);
            canvas.drawBitmap(mBoatBitmap, mMatrix, mWavePaint);
        }
    }

    /**
     * 启动动画
     */
    public void startAnimation() {
        ValueAnimator animator1 = ValueAnimator.ofInt(0, WAVE_RADIUS);
        animator1.setDuration(5000);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDeltaX = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
//        animator1.start();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                faction = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }
}
