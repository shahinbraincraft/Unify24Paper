package com.meishe.modulemakeupcompose.makeup;

import android.content.Context;

import com.meicam.sdk.NvsColor;
import com.meishe.utils.ColorUtil;
import com.meishe.utils.SystemUtils;

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
    //标记是否是设计人员放到sd卡中的数据
    private boolean isCustom = false;
    //是否下载到本地
    private boolean localFlag = false;
    private String className;
    private String makeupId;
    private String uuids;
    private String makeupUrl;
    private List<MakeupArgs.RecommendColor> makeupRecommendColors;
    /*选中的颜色*/
    private NvsColor nvsColor;
    /*强度*/
    private float intensity;
    private String version;
    private String minSdkVersion;
    private String supportedAspectRatio;

    private float progress;
    private int color;
    /*单状类型*/
    private String type;

    public boolean isLocalFlag() {
        return localFlag;
    }

    public void setLocalFlag(boolean localFlag) {
        this.localFlag = localFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

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
        return isBuildIn || isCustom ? folderPath + File.separator + cover : cover;
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


    public NvsColor getNvsColor() {
        return nvsColor;
    }

    public void setNvsColor(NvsColor nvsColor) {
        this.nvsColor = nvsColor;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
