package com.meishe.modulearscene.bean;

/**
 * @author zcy
 * @Destription: 磨皮Bean
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class StrengthBean extends BeautyBean{
    /**
     * 磨皮type
     * 0/1
     */
    private int advancedBeautyType = 0;
    /**
     * 高级磨皮标记
     */
    private boolean advancedBeauty = false;

    public int getAdvancedBeautyType() {
        return advancedBeautyType;
    }

    public void setAdvancedBeautyType(int advancedBeautyType) {
        this.advancedBeautyType = advancedBeautyType;
    }

    public boolean isAdvancedBeauty() {
        return advancedBeauty;
    }

    public void setAdvancedBeauty(boolean advancedBeauty) {
        this.advancedBeauty = advancedBeauty;
    }
}
