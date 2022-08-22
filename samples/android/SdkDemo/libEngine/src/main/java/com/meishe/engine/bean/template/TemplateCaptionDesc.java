package com.meishe.engine.bean.template;

import android.graphics.Bitmap;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsCompoundCaption;

/**
 * Created by CaoZhiChao on 2020/11/18 17:45
 * 模板标题Desc类
 * Template title Desc class
 */
public class TemplateCaptionDesc extends NvsAssetPackageManager.NvsTemplateCaptionDesc {

    public TemplateCaptionDesc( ) {
    }

    private Bitmap mBitmap;
    private long inPoint;
    //不限长模板使用到这个字段保存字幕对象
    private NvsCaption nvsCaption;
    private NvsCompoundCaption nvsCompoundCaption;
    private int captionType;
    //如果是组合字幕，拆分成多个子字幕对象，保存子字幕的index
    private int captionIndex;
    public long getInPoint() {
        return inPoint;
    }

    public void setInPoint(long inPoint) {
        this.inPoint = inPoint;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setCaptionType(int captionType) {
        this.captionType = captionType;
    }

    public int getCaptionType() {
        return captionType;
    }

    public void setNvsCaption(NvsCaption nvsCaption) {
        this.nvsCaption = nvsCaption;
    }

    public NvsCaption getNvsCaption() {
        return nvsCaption;
    }

    public void setNvsCompoundCaption(NvsCompoundCaption nvsCompoundCaption) {
        this.nvsCompoundCaption = nvsCompoundCaption;
    }

    public NvsCompoundCaption getNvsCompoundCaption() {
        return nvsCompoundCaption;
    }

    public void setCaptionIndex(int captionIndex) {
        this.captionIndex = captionIndex;
    }

    public int getCaptionIndex() {
        return captionIndex;
    }

    public static class TemplateCaptionType{
        /**
         * 时间线普通字幕
         */
        public static final int TIMELINE_CAPTION = 1;
        /**
         * 时间线组合字幕
         */
        public static final int TIMELINE_COMPOUND_CAPTION = 2;
        /**
         * clip字幕
         */
        public static final int CLIP_CAPTION = 3;
        /**
         * clip组合字幕
         */
        public static final int CLIP_COMPOUND_CAPTION = 4;
    }
}
