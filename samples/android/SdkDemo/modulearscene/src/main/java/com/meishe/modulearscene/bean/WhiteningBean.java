package com.meishe.modulearscene.bean;

/**
 * @author zcy
 * @Destription:美白Bean
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class WhiteningBean extends BeautyBean{
    //美白A还是B 0是A 1是B
    private int whiteningType = 0;
    public int getWhiteningType() {
        return whiteningType;
    }

    public void setWhiteningType(int whiteningType) {
        this.whiteningType = whiteningType;
    }
}
