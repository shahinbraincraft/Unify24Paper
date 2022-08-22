package com.meicam.effectsdkdemo.data;

import android.graphics.PointF;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: CaoZhiChao
 * @CreateDate: 2021/11/17 17:05:39
 * @Description: 用于记录销毁的effect数据，销毁的时候保存，恢复的时候使用
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class EffectData {
    private int type;
    private long startTimeStamp;
    private String markTag;
    private float scale;
    private PointF translatePoint;
    private float rotation;

    //字幕特有
    private String captionText;

    public EffectData() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getMarkTag() {
        return markTag;
    }

    public void setMarkTag(String markTag) {
        this.markTag = markTag;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public PointF getTranslatePoint() {
        return translatePoint;
    }

    public void setTranslatePoint(PointF translatePoint) {
        this.translatePoint = translatePoint;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public String getCaptionText() {
        return captionText;
    }

    public void setCaptionText(String captionText) {
        this.captionText = captionText;
    }
}
