package com.meishe.engine.bean;

import com.meishe.engine.adapter.TimelineDataToLocalAdapter;
import com.meishe.engine.local.LMaskRegionInfoData;

import java.io.Serializable;

/**
 * @author :Jml
 * @date :2020/9/21 16:07
 * @des : 构建NvRegionInfo 使用的数据源
 */
public class MaskRegionInfoData implements Cloneable, Serializable, TimelineDataToLocalAdapter<LMaskRegionInfoData> {
    //选择的锚点
    //private PointF mCenter;
    private float centerX;
    private float centerY;
    //蒙版宽度
    private int maskWidth;
    //萌版的高度
    private int maskHeight;
    //旋转角度
    private int rotation;
    //蒙版类型 0--6
    private int type;
    //item 名字
    private String itemName;
    //图标
    private int drawableIcon;
    //区域反转
    private boolean reverse;
    //羽化值
    private float featherWidth;

    private float roundCornerRate;
    private float translationX;

    private float translationY;

    //水平压缩值
    private float horizontalScale = 1F;

    //竖直方向压缩值
    private float verticalScale = 1F;


    public float getHorizontalScale() {
        return horizontalScale;
    }

    public void setHorizontalScale(float horizontalScale) {
        this.horizontalScale = horizontalScale;
    }

    public float getVerticalScale() {
        return verticalScale;
    }

    public void setVerticalScale(float verticalScale) {
        this.verticalScale = verticalScale;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setDrawableIcon(int drawableIcon) {
        this.drawableIcon = drawableIcon;
    }

    public int getDrawableIcon() {
        return drawableIcon;
    }

//    public PointF getCenter() {
//        return mCenter;
//    }
//
//    public void setCenter(PointF mCenter) {
//        this.mCenter = mCenter;
//    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaskWidth() {
        return maskWidth;
    }

    public void setMaskWidth(int mMaskWidth) {
        this.maskWidth = mMaskWidth;
    }

    public int getMaskHeight() {
        return maskHeight;
    }

    public void setMaskHeight(int mMashHeight) {
        this.maskHeight = mMashHeight;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setFeatherWidth(float featherWidth) {
        this.featherWidth = featherWidth;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public float getFeatherWidth() {
        return featherWidth;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setRoundCornerRate(float roundCornerRate) {
        this.roundCornerRate = roundCornerRate;
    }

    public float getRoundCornerRate() {
        return roundCornerRate;
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    @Override
    public LMaskRegionInfoData parseToLocalData() {
        LMaskRegionInfoData lMaskRegionInfoData = new LMaskRegionInfoData();
        lMaskRegionInfoData.setDrawableIcon(getDrawableIcon());
        lMaskRegionInfoData.setFeatherWidth(getFeatherWidth());
        lMaskRegionInfoData.setItemName(getItemName());
        //lMaskRegionInfoData.setCenter(getCenter());
        lMaskRegionInfoData.setmCenterX(getCenterX());
        lMaskRegionInfoData.setmCenterY(getCenterY());
        lMaskRegionInfoData.setMaskHeight(getMaskHeight());
        lMaskRegionInfoData.setMaskWidth(getMaskWidth());
        lMaskRegionInfoData.setmRotation(getRotation());
        lMaskRegionInfoData.setmType(getType());
        lMaskRegionInfoData.setReverse(isReverse());
        lMaskRegionInfoData.setRoundCornerRate(getRoundCornerRate());
        lMaskRegionInfoData.setTranslationX(getTranslationX());
        lMaskRegionInfoData.setTranslationY(getTranslationY());
        lMaskRegionInfoData.setHorizontalScale(getHorizontalScale());
        lMaskRegionInfoData.setVerticalScale(getVerticalScale());
        return lMaskRegionInfoData;
    }

    public class MaskType {
        public static final int NONE = 0;
        public static final int LINE = 1;
        public static final int MIRROR = 2;
        public static final int CIRCLE = 3;
        public static final int RECT = 4;
        public static final int HEART = 5;
        public static final int STAR = 6;

    }
}
