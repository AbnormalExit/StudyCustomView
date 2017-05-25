package com.customui.sxshi.pathstudy.view;

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

import com.customui.sxshi.pathstudy.R;

/**
 * @auther Administrator on 2017/5/25.
 * @email emotiona_xiaoshi@126.com
 * 实现一个旋转的箭头动画
 */

public class RotateArrowView extends View {
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private float[] pos;                // 当前点的实际位置
    private float[] tan;
    private final static int DEFAULTWIDTH = 500;
    private int mWidth;
    private int mHeight;
    private float currentValue = 0;

    public RotateArrowView(Context context) {
        this(context, null);
    }

    public RotateArrowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(5f);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);

        mPath = new Path();
        pos = new float[2];
        tan = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;       // 缩放图片
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow, options);
        mMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.translate(mWidth / 2, mHeight / 2);      // 平移坐标系
        mPath.reset();
        mPath.addCircle(0, 0, 200, Path.Direction.CW);           // 添加一个圆形
        PathMeasure measure = new PathMeasure(mPath, false);     // 创建 PathMeasure
        //方法一自己计算角度
//        boolean getTan = measure.getPosTan(measure.getLength() * currentValue, pos, tan);        // 获取当前位置的坐标以及趋势
//        mMatrix.reset();                                                        // 重置Matrix
//        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI); // 计算图片旋转角度
//        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);   // 旋转图片
//        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合
//        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        // 获取当前位置的坐标以及趋势的矩阵  API计算角度
        measure.getMatrix(measure.getLength() * currentValue, mMatrix, PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
        mMatrix.preTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);   // <-- 将图片绘制中心调整到与当前点重合(注意:此处是前乘pre)
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        // 绘制箭头
        canvas.drawPath(mPath, mPaint);   // 绘制 Path
        invalidate();
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
