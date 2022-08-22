package com.meishe.modulemakeupcompose.makeup;

import android.text.TextUtils;

import java.util.List;

/**
 * 单装
 */
public class MakeupArgs {
    private String className;
    //后面删掉
    private String makeupId;
    private String type;
    private List<Translation> translation;

    public String getMakeupId() {
        return makeupId;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
    }

    private String uuid;
    private String makeupUrl;
    private int canReplace;
    private List<RecommendColor> makeupRecommendColors;
    private float value = 1;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getType() {
        return !TextUtils.isEmpty(getMakeupId()) ? getMakeupId() : type;
    }

    public void setType(String type) {
        setMakeupId(type);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMakeupUrl() {
        return makeupUrl;
    }

    public void setMakeupUrl(String makeupUrl) {
        this.makeupUrl = makeupUrl;
    }

    public List<RecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public void setMakeupRecommendColors(List<RecommendColor> makeupRecommendColors) {
        this.makeupRecommendColors = makeupRecommendColors;
    }

    public int getCanReplace() {
        return canReplace;
    }

    public void setCanReplace(int canReplace) {
        this.canReplace = canReplace;
    }

    public class RecommendColor {
        private String makeupColor;

        public String getMakeupColor() {
            return makeupColor;
        }

        public void setMakeupColor(String makeupColor) {
            this.makeupColor = makeupColor;
        }
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }
}
