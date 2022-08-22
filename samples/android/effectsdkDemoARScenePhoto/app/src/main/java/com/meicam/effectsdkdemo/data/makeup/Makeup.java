package com.meicam.effectsdkdemo.data.makeup;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/8 11:02
 * @Description: 整妆
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class Makeup implements BeautyData {
    private String folderPath;
    private String uuid;
    private String cover;
    private String url;
    private String name;
    protected boolean isCompose;
    private MakeupEffectContent effectContent;
    private List<Translation> translation;
    private int backgroundColor;
    private boolean isBuildIn = true;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return folderPath + File.separator + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsCompose() {
        return isCompose;
    }

    public void setIsCompose(boolean isCompose) {
        this.isCompose = isCompose;
    }

    public MakeupEffectContent getEffectContent() {
        return effectContent;
    }

    public void setEffectContent(MakeupEffectContent effectContent) {
        this.effectContent = effectContent;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }

    @Override
    public String getName(Context context) {
        if (translation == null || translation.isEmpty()) {
            return name;
        }
        if (!SystemUtils.isZh(context)) {
            return translation.get(0).getOriginalText();
        }
        return translation.get(0).getTargetText();
    }

    @Override
    public Object getImageResource() {
        return isBuildIn ? folderPath + File.separator + cover : cover;
    }

    @Override
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public boolean isBuildIn() {
        return isBuildIn;
    }

    @Override
    public void setIsBuildIn(boolean isBuildIn) {
        this.isBuildIn = isBuildIn;
    }

    @Override
    public int getBackgroundColor() {
        if (backgroundColor == 0) {
            backgroundColor = ColorUtil.getFilterRandomBgColor();
        }
        return backgroundColor;
    }

    private String className;
    private String makeupId;
    private String uuids;
    private String makeupUrl;
    private List<MakeupArgs.RecommendColor> makeupRecommendColors;

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

    public String getUuids() {
        return uuids;
    }

    public void setUuids(String uuids) {
        this.uuids = uuids;
    }

    public String getMakeupUrl() {
        return makeupUrl;
    }

    public void setMakeupUrl(String makeupUrl) {
        this.makeupUrl = makeupUrl;
    }

    public List<MakeupArgs.RecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public void setMakeupRecommendColors(List<MakeupArgs.RecommendColor> makeupRecommendColors) {
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
