package com.meishe.sdkdemo.repository;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/29 下午7:17
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AppRepository {

    private static AppRepository mInstance=new AppRepository();
    private Map<String, BaseBean<EffectInfo>> cacheMap ;

    public static class AppRepositoryHelper{
        public static AppRepository getInstance() {
           return mInstance;
        }

    }

    private AppRepository() {
        cacheMap = new HashMap<>();
    }



    /**
     * 获取滤镜数据
     * @param kindInfo
     * @return
     */
    public MutableLiveData<BaseBean<EffectInfo>> getFilterData(KindInfo kindInfo) {
        MutableLiveData<BaseBean<EffectInfo>> liveData = new MutableLiveData<>();
        if (kindInfo == null) {
            return null;
        }
        String cacheKey = getCacheKey(kindInfo);
        BaseBean<EffectInfo> filterInfos = cacheMap.get(cacheKey);
        if (filterInfos != null) {
            liveData.setValue(filterInfos);
            return liveData;
        }

        HttpManager.getMaterialList(null, String.valueOf(kindInfo.getMaterialType()),
                String.valueOf(kindInfo.getCategory()),
                String.valueOf(kindInfo.getId()), "",
                1, Constants.PAGE_SIZE, new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
                        if (response.getCode() == 1) {
                            List<EffectInfo> elements = response.getData().getElements();
                            if (elements != null) {
                                for (EffectInfo filterInfo : elements) {
                                    if (MSApplication.isZh()) {
                                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                                    } else {
                                        filterInfo.setName(filterInfo.getDisplayName());
                                    }
                                    String effectPath = getFilterPath(filterInfo, NvAsset.ASSET_FILTER);
                                    String effectId = (String) SharedPreferencesUtils.getParam(MSApplication.getAppContext(), effectPath, "");
                                    if (!TextUtils.isEmpty(effectId)) {
                                        filterInfo.setDownload(true);
                                    }
                                }

                            }
                            String cacheKey = getCacheKey(kindInfo);
                            cacheMap.put(cacheKey,response.getData());
                            liveData.setValue(response.getData());
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                        liveData.setValue(null);
                    }
                });
        return liveData;
    }

    /**
     * 获取道具 贴纸 组合字幕数据
     * @param categoryInfo
     * @return
     */
    public MutableLiveData<BaseBean<EffectInfo>> getEffectData(MutableLiveData<BaseBean<EffectInfo>> liveData,CategoryInfo categoryInfo) {
        if (categoryInfo == null) {
            return null;
        }
        String cacheKey = getCacheKey(categoryInfo);
        BaseBean<EffectInfo> filterInfos = cacheMap.get(cacheKey);
        if (filterInfos != null) {
            liveData.setValue(filterInfos);
            return liveData;
        }

        HttpManager.getMaterialList(null, String.valueOf(categoryInfo.getType()),
                String.valueOf(categoryInfo.getId()),
                "", "",
                categoryInfo.getPageNumber(), categoryInfo.getPageSize(), new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
                        if (response.getCode() == 1) {
                            BaseBean<EffectInfo> data = response.getData();
                            List<EffectInfo> elements = data.getElements();
                            if (elements != null) {
                                for (EffectInfo filterInfo : elements) {
                                    if (MSApplication.isZh()) {
                                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                                    } else {
                                        filterInfo.setName(filterInfo.getDisplayName());
                                    }
                                    String effectPath = getFilterPath(filterInfo, categoryInfo.getAssetType());
                                    String effectId = (String) SharedPreferencesUtils.getParam(MSApplication.getAppContext(), effectPath, "");
                                    if (!TextUtils.isEmpty(effectId)) {
                                        filterInfo.setDownload(true);
                                    }
                                }
                                String cacheKey = getCacheKey(categoryInfo);
                                cacheMap.put(cacheKey,data);
                                liveData.setValue(data);
                            }
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }


    /**
     * 下载特效包
     *
     * @param filterInfo
     */
    public MutableLiveData<String> downloadPackage(MutableLiveData<String> filePathLiveDate,EffectInfo filterInfo) {
        if (null == filterInfo) {
            return null;
        }
        String packageUrl = filterInfo.getPackageUrl();
        if (TextUtils.isEmpty(packageUrl)) {
            return null;
        }

        String effectPathDir = PathUtils.getAssetDownloadPath(filterInfo.getAssetType());
        String[] split = packageUrl.split("/");
        HttpManager.download(packageUrl, packageUrl, effectPathDir, split[split.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                filterInfo.setProgress((int) (progress.fraction * 360));
            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                filterInfo.setDownload(true);
                if (null != file) {
                    String absolutePath = file.getAbsolutePath();
                    filePathLiveDate.setValue(absolutePath);
                }
            }
        });
        return filePathLiveDate;
    }



    private String getCacheKey(KindInfo kindInfo) {
        StringBuilder cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder.append(kindInfo.getMaterialType());
        cacheKeyBuilder.append(kindInfo.getCategory());
        cacheKeyBuilder.append(kindInfo.getId());
        return cacheKeyBuilder.toString();
    }

    private String getCacheKey(CategoryInfo categoryInfo) {
        StringBuilder cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder.append(categoryInfo.getType());
        cacheKeyBuilder.append(categoryInfo.getId());
        cacheKeyBuilder.append(categoryInfo.getPageNumber());
        return cacheKeyBuilder.toString();
    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getAssetDownloadPath(assetType);
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }


}
