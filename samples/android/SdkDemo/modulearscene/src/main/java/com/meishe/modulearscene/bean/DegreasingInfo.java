package com.meishe.modulearscene.bean;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/8/2 下午1:22
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class DegreasingInfo extends BeautyBean{
    private double subStrength;
    public double defaultSubStrength = 0.0d;
    private String arSubId;

    public double getSubStrength() {
        return subStrength;
    }

    public void setSubStrength(double subStrength) {
        this.subStrength = subStrength;
    }

    public double getDefaultSubStrength() {
        return defaultSubStrength;
    }

    public void setDefaultSubStrength(double defaultSubStrength) {
        this.defaultSubStrength = defaultSubStrength;
    }


    public String getArSubId() {
        return arSubId;
    }

    public void setArSubId(String arSubId) {
        this.arSubId = arSubId;
    }
}
