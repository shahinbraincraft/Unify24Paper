package com.czc.cutsame.fragment.presenter;

import android.text.TextUtils;

import com.czc.cutsame.CutSameNetApi;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.czc.cutsame.fragment.iview.TemplateView;
import com.czc.cutsame.util.TemplateUtils;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.util.PathUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;

import java.io.File;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板逻辑处理类
 * Template logic handles classes
 */
public class TemplatePresenter extends Presenter<TemplateView> {
    private String TEMPLATE_POSTFIX = ".template";
    private String VIDEO_POSTFIX = ".mp4";
    private String INFO_POSTFIX = ".json";
    public static final int PAGE_NUM = 12;
    private boolean hasNext;
    private int mPage=1;
    private boolean canLoadMore = true;

    @Override
    public void attachView(TemplateView templateView) {
        super.attachView(templateView);
    }

    /**
     * 获取模板分类
     * <p></p>
     * Get template classification
     */
    public void getTemplateCategory() {
        CutSameNetApi.getTemplateCategory(this, new RequestCallback<TemplateCategory>() {
            @Override
            public void onSuccess(BaseResponse<TemplateCategory> response) {
                if (response != null && response.getData() != null) {
                    if (response.getData().categories != null && getView() != null) {
                        getView().onTemplateCategoryBack(response.getData().categories);
                    }
                }
            }

            @Override
            public void onError(BaseResponse<TemplateCategory> response) {
                getView().onTemplateCategoryBack(null);
            }
        });
    }

    /**
     * 获取模板对应分类的列表
     * <p></p>
     * Gets the list of categories corresponding to the template
     *
     * @param page       int 请求页数
     * @param categoryId String 模板分类id
     */
    public void getTemplateList(final int page, final String categoryId) {
        String sdkVersion = "";
        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        if (instance != null && instance.getSdkVersion() != null) {
            NvsStreamingContext.SdkVersion version = instance.getSdkVersion();
            sdkVersion = version.majorVersion + "." + version.minorVersion + "." + version.revisionNumber;
        }
        AssetType assetType=null;
        if ("1".equals(categoryId)){
             assetType=AssetType.MS_TEMPLATE_NOR;
        }else if ("2".equals(categoryId)){
            assetType=AssetType.MS_TEMPLATE_PHOTO;
        }
        int ratio =  1| 2|4|8|16|32|64|512|1024;
        CutSameNetApi.getMaterialList(null,assetType, 1,ratio ,
                null, sdkVersion, page, PAGE_NUM, new RequestCallback<BaseBean<BaseDataBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean>> response) {
                        if (response != null && response.getData() != null) {
                            if (response.getData().getElements() != null && getView() != null) {
                                List<Template> templates = TemplateUtils.getTemplate(response.getData().getElements(),categoryId);
                                canLoadMore = response.getData().getTotal() > page * PAGE_NUM;
                                //以前这么写的逻辑，不是很明白 有疑惑
                                if (page == 1) {
                                    mPage = 1;
                                    getView().onTemplateListBack(templates);
                                } else {
                                    mPage++;
                                    getView().onMoreTemplateBack(templates);
                                }
                                hasNext = true;
                            } else {
                                hasNext = false;
                            }
                        } else {
                            hasNext = false;
                        }
                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean>> response) {
                        getView().onTemplateListBack(null);

                    }
                });
    }

    /**
     * 加载更多模板
     * <p></p>
     * Loading more templates
     *
     * @param categoryId the category id 类别id
     * @return the more template 更多的模板
     */
    public boolean getMoreTemplate(String categoryId) {
        if (hasNext && canLoadMore) {
            getTemplateList((mPage + 1), categoryId);
            canLoadMore = false;
        }
        return hasNext;
    }

    /**
     * 下载对应的模板资源
     * <p></p>
     * download template resource
     *
     * @param url        String 下载链接
     * @param downDir    String 文件所在目录
     * @param fileName   String 文件名称
     * @param isTemplate boolean true则是下载模板文件，false则不然。
     */
    public void download(String url, String downDir, final String fileName, final boolean isTemplate) {
        CutSameNetApi.download(fileName, url, downDir, fileName, new SimpleDownListener(fileName) {
            @Override
            public void onFinish(final File file, Progress progress) {
                if (getView() != null) {
                    getView().onDownloadTemplateSuccess(file.getAbsolutePath(), isTemplate);
                }
            }

            @Override
            public void onError(Progress progress) {
                FileUtils.delete(PathUtils.getTemplateDir() + File.separator + fileName);
            }
        });
    }

    /**
     * 检查模板是否需要更新
     * <p></p>
     * Check if the template needs to be updated
     *
     * @param template the template 模板
     * @return the boolean
     */
    public boolean checkTemplateUpdate(Template template) {
        File file = getTemplatePath(template.getId());
        try {
            if (file != null) {
                String templateName = file.getName();
                String[] split = templateName.split("\\.");
                if (split.length == 3 && Integer.valueOf(split[1]) >= template.getVersion()
                        || split.length == 2 && template.getVersion() <= 0) {
                    /*
                     * 不用更新
                     * Don't need to update
                     * */
                    if (getView() != null) {
                        getView().onDownloadTemplateSuccess(file.getAbsolutePath(), true);
                    }
                    return false;
                } else {
                    /*
                     * 需要更新，则删除文件夹中的内容
                     * If an update is needed, the contents of the folder are deleted
                     * */
                    FileUtils.deleteFilesInDir(file.getParentFile());
                }
            } /*else {
                //第一次需要创建文件夹  The first time you need to create a folder
                FileUtils.createOrExistsDir(PathUtils.getTemplateDir() + File.separator + template.getId());
            }*/
            String fileName = template.getPackageUrl();
            String fileDir = PathUtils.getTemplateDir() + File.separator + template.getId();
            try {
                /*
                 * 截取文件名
                 * Intercept file name
                 * */
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            } catch (Exception e) {
                LogUtils.e(e);
                fileName = template.getId() + "." + template.getVersion() + TEMPLATE_POSTFIX;
            }
            download(template.getPackageUrl(), fileDir, fileName, true);
            fileName = template.getPreviewVideoUrl();
            if (!TextUtils.isEmpty(fileName)) {
                try {
                    /*
                     * 截取文件名
                     * Intercept file name
                     * */
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } catch (Exception e) {
                    LogUtils.e(e);
                    fileName = template.getId() + "." + template.getVersion() + VIDEO_POSTFIX;
                }
                download(template.getPreviewVideoUrl(), fileDir, fileName, false);
            }

            fileName = template.getInfoUrl();
            if (!TextUtils.isEmpty(fileName)) {
                try {
                    /*
                     * 截取文件名
                     * Intercept file name
                     * */
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } catch (Exception e) {
                    LogUtils.e(e);
                    fileName = template.getId() + "." + template.getVersion() + INFO_POSTFIX;
                }
                download(template.getInfoUrl(), fileDir, fileName, false);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return true;
    }

    /**
     * 获取下载后的模板预览视频路径
     * <p></p>
     * Get the template preview video path after downloading
     *
     * @param templateId the template id 模板编号
     * @return the video path 视频路径
     */
    public String getVideoPath(String templateId) {
        return getFilePath(templateId, VIDEO_POSTFIX);
    }

    /**
     * 获取信息文件路径
     * <p></p>
     * Get the information file path after downloading
     *
     * @param templateId the template id 模板编号
     * @return the information file path 视频路径
     */
    public String getInfoPath(String templateId) {
        return getFilePath(templateId, INFO_POSTFIX);
    }

    /**
     * 根据文件后缀获取文件路径
     * <p></p>
     * Get the file path by postfix
     *
     * @param templateId the template id 模板编号
     * @param postfix    the file postfix 文件后缀
     * @return the file path 文件路径
     */
    private String getFilePath(String templateId, String postfix) {
        if (TextUtils.isEmpty(postfix)) {
            return null;
        }
        String templatePath = PathUtils.getTemplateDir() + "/" + templateId;
        if (FileUtils.isDir(templatePath)) {
            List<File> files = FileUtils.listFilesInDir(templatePath);
            if (files != null && files.size() > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(postfix)) {
                        return file.getAbsolutePath();
                    }
                }
            }

        }
        return null;
    }


    /**
     * 获取下载后的模板文件
     * <p></p>
     * Get the downloaded template file
     */
    private File getTemplatePath(String templateId) {
        String templatePath = PathUtils.getTemplateDir() + "/" + templateId;
        if (FileUtils.isDir(templatePath)) {
            List<File> files = FileUtils.listFilesInDir(templatePath);
            if (files != null && files.size() > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(TEMPLATE_POSTFIX)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }
}
