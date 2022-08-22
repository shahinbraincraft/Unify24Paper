package com.meicam.effectsdkdemo.data.makeup;

import java.util.List;

public class MakeupArgs {
    private String className;
    private String makeupId;
    private String uuid;
    private String makeupUrl;
    private List<RecommendColor> makeupRecommendColors;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMakeupId() {
        return makeupId;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
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

    public class RecommendColor {
        private String makeupColor;

        public String getMakeupColor() {
            return makeupColor;
        }

        public void setMakeupColor(String makeupColor) {
            this.makeupColor = makeupColor;
        }
    }

}
