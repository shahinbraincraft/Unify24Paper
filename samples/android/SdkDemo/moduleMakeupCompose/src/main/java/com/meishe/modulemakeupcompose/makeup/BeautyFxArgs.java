package com.meishe.modulemakeupcompose.makeup;

import com.google.gson.annotations.SerializedName;

public class BeautyFxArgs {
    private String fxName;
    private String value;
    private String type;
    private String className;
    private int canReplace;
    private String uuid;
    @SerializedName("Advanced Beauty Enable")
    private int advancedBeautyEnable;
    @SerializedName("Advanced Beauty Type")
    private int advancedBeautyType;
    @SerializedName("Whitening Lut Enabled")
    private int whiteningLutEnabled;
    private String degreeName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public int getAdvancedBeautyEnable() {
        return advancedBeautyEnable;
    }

    public void setAdvancedBeautyEnable(int advancedBeautyEnable) {
        this.advancedBeautyEnable = advancedBeautyEnable;
    }

    public int getAdvancedBeautyType() {
        return advancedBeautyType;
    }

    public void setAdvancedBeautyType(int advancedBeautyType) {
        this.advancedBeautyType = advancedBeautyType;
    }

    public int getWhiteningLutEnabled() {
        return whiteningLutEnabled;
    }

    public void setWhiteningLutEnabled(int whiteningLutEnabled) {
        this.whiteningLutEnabled = whiteningLutEnabled;
    }

    public String getFxName() {
        return fxName;
    }

    public void setFxName(String fxName) {
        this.fxName = fxName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCanReplace() {
        return canReplace;
    }

    public boolean isCanReplace() {
        return getCanReplace() == 0;
    }

    public void setCanReplace(int canReplace) {
        this.canReplace = canReplace;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
