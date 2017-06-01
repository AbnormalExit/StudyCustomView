package com.customui.sxshi.pathmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxshi on 2017/6/1.
 * province map info
 */

public class CityMapView extends View {
    private static final String TAG = "DetialMapView";
    private Paint mPaint;
    private List<CityItem> cityItemList ;
    private CityItem selectCity;
    private Context mContext;
    private float scale = 1.3f;
    private static Handler mHander;

    private int miniWidth;
    private int miniHeight;
    private static final int LOAD_FINISH = 1;

    private GestureDetectorCompat gestureDetectorCompat;
    private OnMapClickListener onMapClickListener;

    public void setOnMapClickListener(OnMapClickListener onMapClickListener) {
        this.onMapClickListener = onMapClickListener;
    }

    public CityMapView(Context context) {
        this(context, null);
    }

    public CityMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        init();
        initData();
    }

    /**
     * 解析数据
     */
    private void initData() {
        getCityItemList();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);

        miniWidth = getContext().getResources().getDimensionPixelSize(R.dimen.map_min_width);
        miniHeight = getContext().getResources().getDimensionPixelSize(R.dimen.map_min_height);
        Log.d(TAG, "miniWidth=" + miniWidth + "|miniHeight=" + miniHeight);
        cityItemList = new ArrayList<>();
        mHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOAD_FINISH:
                        cityItemList = (List<CityItem>) msg.obj;
                        postInvalidate();
                        break;
                }
            }
        };
        gestureDetectorCompat = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                handlerTouch((int) x, (int) y);
                return true;
            }
        });

    }

    /**
     * 处理点击事件
     *
     * @param x 点击的X坐标
     * @param y 点击的Y坐标
     */
    private boolean handlerTouch(int x, int y) {
        final List<CityItem> list = cityItemList;
        CityItem cityItem = null;
        if (list == null) return false;
        for (CityItem temp : list) {
            if (temp.isOnTouch((int) (x / scale), (int) (y / scale))) {//除以放大倍数
                cityItem = temp;
                break;
            }
        }
        if (cityItem != null && !cityItem.equals(selectCity)) {
            selectCity = cityItem;
            onMapClickListener.onClick(selectCity);
            postInvalidate();
        }
        return selectCity != null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final List<CityItem> list = cityItemList;
        canvas.save();
        canvas.scale(scale, scale);
        for (CityItem cityItem : list) {
            if (!cityItem.equals(selectCity))
                cityItem.onDraw(canvas, mPaint, false);
        }
        if (selectCity != null) {
            selectCity.onDraw(canvas, mPaint, true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasure(miniWidth, widthMeasureSpec);
        int height = getMeasure(miniHeight, heightMeasureSpec);
        Log.d(TAG, "onMeasure: " + width + "---" + height);
        setMeasuredDimension(width, height);
    }

    public int getMeasure(int defaultSize, int measureSpec) {
        int resultSize = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY: //自己定义大小
                resultSize = size;
                break;
            case MeasureSpec.AT_MOST: //wrap_content
            case MeasureSpec.UNSPECIFIED:
                resultSize = Math.max(defaultSize, size);
                break;
        }
        return resultSize;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged: " + w + "---" + h);
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 加载数据
     */
    public void getCityItemList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<CityItem> result = new ArrayList<>();
                    long startTime = System.currentTimeMillis();
                    InputStream inputStream = mContext.getResources().openRawResource(R.raw.china);
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(inputStream, "utf-8");
                    int eventType;
                    while ((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String name = parser.getName();
                            if ("path".equals(name)) {
                                String id = parser.getAttributeValue(null, "id");
                                String title = parser.getAttributeValue(null, "title");
                                String pathData = parser.getAttributeValue(null, "d");
                                Path path = PathParser.createPathFromPathData(pathData);
                                Region region = new Region();
                                if (path != null) {
                                    RectF r = new RectF();
                                    //得到Path的矩形边界
                                    path.computeBounds(r, true);
                                    // 设置区域路径和剪辑描述的区域
                                    region.setPath(path, new Region((int) (r.left), (int) (r.top), (int) (r.right), (int) (r.bottom)));
                                }
                                CityItem cityItem = new CityItem();
                                cityItem.setCityId(id);
                                cityItem.setCityName(title);
                                cityItem.setmPath(path);
                                cityItem.setmRegion(region);
                                result.add(cityItem);
                            }
                        }
                        parser.next();
                    }
                    Message msg = Message.obtain();
                    msg.what = LOAD_FINISH;
                    msg.obj = result;
                    mHander.sendMessage(msg);
                    Log.i("TAG", "加载SVG数据结束耗时->" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 点击监听事件 回调得到城市信息
     */
     interface OnMapClickListener {
        void onClick(CityItem cityItem);
    }
}
