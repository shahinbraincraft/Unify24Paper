package com.meishe.sdkdemo.themeshoot.bean;

import java.io.Serializable;
/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2020/8/5.
 * @Description :字幕bean。CaptionBean
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionBean implements Serializable {
    String text;//显示文字
    int countIndex;//位置标识

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(int countIndex) {
        this.countIndex = countIndex;
    }
}
