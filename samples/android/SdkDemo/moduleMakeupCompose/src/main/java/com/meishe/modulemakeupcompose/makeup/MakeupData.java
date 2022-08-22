package com.meishe.modulemakeupcompose.makeup;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/15 15:47
 * @Description:  记录选择使用的美妆数据
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeupData {
    private int index = -1;
    private String uuid;
    private float intensity;
    private ColorData colorData;

    public MakeupData(int index, float intensity, ColorData colorData) {
        this.index = index;
        this.intensity = intensity;
        this.colorData = colorData;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setColorData(ColorData colorData) {
        this.colorData = colorData;
    }

    public int getIndex() {
        return index;
    }

    public float getIntensity() {
        return intensity;
    }

    public ColorData getColorData() {
        return colorData;
    }
}
