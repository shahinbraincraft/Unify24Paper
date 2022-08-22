package com.meishe.modulemakeupcompose.makeup;

import java.util.List;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/5/18.
 */
public class InfoJson {
    private String makeupId;
    private List<MakeupRecommendColor> makeupRecommendColors;
    private Translation translation;
    private String className;

    public List<MakeupRecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public String getClassName() {
        return className;
    }

    public String getMakeupId() {
        return makeupId;
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
    }

    public void setMakeupRecommendColors(List<MakeupRecommendColor> makeupRecommendColors) {
        this.makeupRecommendColors = makeupRecommendColors;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }
}
