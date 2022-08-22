package com.meishe.modulearscene.bean;

/**
 * @author zcy
 * @Destription: ar特效父类
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class ArBean {
    private String name;
    private double strength;

    private String arId;
    private int resId;//背景图片索引
    private String resUrl;//背景图片网络地址
    public double defaultStrength = 0.0d;

    private boolean canReplace = true;

    public double getDefaultStrength() {
        return defaultStrength;
    }

    public void setDefaultStrength(double defaultStrength) {
        this.defaultStrength = defaultStrength;
    }

    public boolean isCanReplace() {
        return canReplace;
    }

    public void setCanReplace(boolean canReplace) {
        this.canReplace = canReplace;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public String getName() {
        return name;
    }

    public String getArId() {
        return arId;
    }

    public void setArId(String arId) {
        this.arId = arId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }


}
