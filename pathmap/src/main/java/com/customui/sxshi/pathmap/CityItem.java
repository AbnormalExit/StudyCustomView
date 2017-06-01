package com.customui.sxshi.pathmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;

/**
 * Created by sxshi on 2017/6/1.
 */

public class CityItem {
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市编码
     */
    private String cityId;
    /**
     * 区域路径
     */
    private Path mPath;
    /**
     * 路径区域
     */
    private Region mRegion;
    /**
     * 区域背景色，默认白色
     */
    private int drawColor = Color.YELLOW;

    public Path getmPath() {
        return mPath;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    public Region getmRegion() {
        return mRegion;
    }

    public void setmRegion(Region mRegion) {
        this.mRegion = mRegion;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    /**
     * 绘制出自己
     *
     * @param canvas     画布
     * @param paint      画笔
     * @param isSelected 是否是选中的地图
     */
    public void onDraw(Canvas canvas, Paint paint, boolean isSelected) {
        if (isSelected) {
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, 0xFFFFFFFF);
            canvas.drawPath(mPath, paint);

            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);
        } else {
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(mPath, paint);

            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = 0xFFD0E8F4;
            paint.setColor(strokeColor);
            canvas.drawPath(mPath, paint);
        }
    }

    /**
     * 当前地图是否是在点击区域
     *
     * @return
     */
    public boolean isOnTouch(int x, int y) {
        return mRegion.contains(x, y);
    }
}
