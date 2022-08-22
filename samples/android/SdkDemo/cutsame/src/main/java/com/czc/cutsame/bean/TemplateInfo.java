package com.czc.cutsame.bean;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/1/14 17:24
 * @Description: 模板描述文件，模板生产时所用
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class TemplateInfo {
    /**
     * The number of resource bundles used by this template
     * 该模板用到的资源包的数量
     */
    private int innerAssetTotalCount;

    /**
     * Creator
     * 创作者
     */
    private String creator;

    /**
     * What percentage of the template is supported? What percentage of the XML is generated by the call to the WriteTemplateXML interface
     * 模板支持的画幅比例，之前调用WriteTemplateXml接口生成了一个什么比例的xml，这里就需要加一个什么比例
     * 16v9|9v16|1v1|3v4|4v3|9v18|18v9
     */
    private String supportedAspectRatio;

    /**
     * Name of template
     * 模板的名称
     */
    private String name;

    /**
     * The UUID of the template must be the same as the UUID passed to the calling interface
     * 模板的uuid，必须和调用接口传入的uuid一致
     */
    private String uuid;

    /**
     * Description of template
     * 模板的描述
     */
    private String description;

    /**
     * The minimum SDK version supported by the template. The value must be 2.19.0 or above
     * 模板支持的最低sdk版本，值必须是2.19.0以上
     */
    private String minSdkVersion;

    /**
     * The version number of the template
     * 模板的版本号
     */
    private int version;

    /**
     * Default Aspect Ratio
     * 默认的画幅比例
     */
    private String defaultAspectRatio;

    /**
     * Length of template
     * 模板的时长
     */
    private long duration;

    /**
     * Template cover, not required
     * 模板封面，非必须
     */
    private String cover;

    /**
     * Number of footages that can be replaced
     * 可替换的footage数量
     */
    private int footageCount;

    /**
     * Chinese and English translation
     * 中英文翻译
     */
    private List<TranslationBean> translation;

    public int getInnerAssetTotalCount() {
        return innerAssetTotalCount;
    }

    public void setInnerAssetTotalCount(int innerAssetTotalCount) {
        this.innerAssetTotalCount = innerAssetTotalCount;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public void setMinSdkVersion(String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDefaultAspectRatio() {
        return defaultAspectRatio;
    }

    public void setDefaultAspectRatio(String defaultAspectRatio) {
        this.defaultAspectRatio = defaultAspectRatio;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getFootageCount() {
        return footageCount;
    }

    public void setFootageCount(int footageCount) {
        this.footageCount = footageCount;
    }

    public List<TranslationBean> getTranslation() {
        return translation;
    }

    public void setTranslation(List<TranslationBean> translation) {
        this.translation = translation;
    }

    public static class TranslationBean {
        private String targetText;
        private String originalText;
        private String targetLanguage;

        public String getTargetText() {
            return targetText;
        }

        public void setTargetText(String targetText) {
            this.targetText = targetText;
        }

        public String getOriginalText() {
            return originalText;
        }

        public void setOriginalText(String originalText) {
            this.originalText = originalText;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }

        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }
    }
}
