package com.meishe.sdkdemo.capture.viewmodel;

import android.text.TextUtils;

import com.meishe.http.bean.BaseBean;
import com.meishe.mvvm.BaseListMvvmModel;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/28 下午3:04
 * @Description : 负责分页数据
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureEffectTabViewModel extends BaseListMvvmModel<List<EffectInfo>> {


    private CategoryInfo mCategoryInfo;

    public CaptureEffectTabViewModel(CategoryInfo categoryInfo) {
        super(true, 1);
        mCategoryInfo = categoryInfo;
    }


    @Override
    public void load() {
        HttpManager.getMaterialList(null, String.valueOf(mCategoryInfo.getType()),
                String.valueOf(mCategoryInfo.getId()),
                "", "",
                mPage, PAGE_SIZE, new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
//                        {"code":1,"enMsg":"success","msg":"成功","data":{"total":103,"elements":[],"pageNum":7,"pageSize":21}}
                        if (response.getCode() == 1) {
                            BaseBean<EffectInfo> data = response.getData();
                            List<EffectInfo> elements = data.getElements();
                            if (elements != null && elements.size() > 0) {
                                for (EffectInfo filterInfo : elements) {
                                    if (MSApplication.isZh()) {
                                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                                    } else {
                                        filterInfo.setName(filterInfo.getDisplayName());
                                    }
                                    String effectPath = getFilterPath(filterInfo, mCategoryInfo.getAssetType());
                                    String effectId = (String) SharedPreferencesUtils.getParam(MSApplication.getAppContext(), effectPath, "");
                                    if (!TextUtils.isEmpty(effectId)) {
                                        filterInfo.setDownload(true);
                                    }
                                }
                                notifyResult(elements);
                            }else{
                                notifyResult(null);
                            }
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                        loadFail(response.getMessage());
                    }
                });

    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getAssetDownloadPath(assetType);
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }


}
