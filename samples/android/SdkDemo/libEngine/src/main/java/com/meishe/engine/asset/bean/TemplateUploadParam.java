package com.meishe.engine.asset.bean;

import java.io.File;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/3/25 17:03
 * @Description :上传模板用参数 Param for template uploading.
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TemplateUploadParam {
    /**
     * 素材文件
     */
    public File materialFile;
    /**
     * 封面
     */
    public File coverFile;
    /**
     * 预览视频文件
     */
    public File previewVideoFile;
    /**
     * 自定义名称
     */
    public String customDisplayName;
    /**
     * 素材type
     */
    public int materialType = AssetsConstants.AssetsTypeData.TEMPLATE.type;

    /**
     * 素材描述
     */
    public String description;
    /**
     * 素材中文描述
     */
    public String descriptinZhCn;

    /**
     * 是否通用 0：非通用 1：通用
     */
    public int ratioFlag;

    /**
     * 智能标签Id 以空格分隔 拼接而成的字符串
     *
     * (暂时后台未做校验)
     */
    public int intelTags;

    @Override
    public String toString() {
        return "TemplateUploadParam{" +
                "materialFile='" + materialFile + '\'' +
                ", coverFile='" + coverFile + '\'' +
                ", previewVideoFile='" + previewVideoFile + '\'' +
                ", customDisplayName='" + customDisplayName + '\'' +
                ", materialType=" + materialType +
                ", description='" + description + '\'' +
                ", descriptinZhCn='" + descriptinZhCn + '\'' +
                ", ratioFlag=" + ratioFlag +
                ", intelTags=" + intelTags +
                '}';
    }
}
