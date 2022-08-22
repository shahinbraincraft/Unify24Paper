package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
import com.meicam.effect.sdk.NvsVideoEffectTransition;
import com.meicam.effectsdkdemo.Constants;
import com.meicam.effectsdkdemo.data.AssetItem;
import com.meicam.effectsdkdemo.data.NvAsset;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsRational;

import java.io.IOException;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Meng Guijun
 * @CreateDate: 2021/2/3 10:06
 * @Description:
 * @Copyright:2021 www.meishesdk.com Inc. All rights reserved.
 */
public class TransitionListView extends BaseRecycleListView{

    private static final String TransitionPATHParent = "/storage/emulated/0/NvStreamingSdk/Asset/TestTransition";

    public TransitionListView(Context context) {
        super(context);
    }

    public TransitionListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initAssetsList() {
        mAssetsList.clear();
        try {
            String[] name_list = mContext.getAssets().list("transition");
            if (name_list != null) {
                for (int i = 0; i < name_list.length; ++i) {
                    String name = name_list[i];
                    if (name.endsWith(".videotransition")) {
                        String assetPackageFilePath = "assets:/transition/" + name;
                        StringBuilder packageUuid = new StringBuilder();
                        boolean installSuccess = installAssetsPackage(assetPackageFilePath, packageUuid, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION);
                        if (!installSuccess) {
                            continue;
                        }
                        NvAsset asset = new NvAsset();
                        asset.uuid = packageUuid.toString();
                        asset.coverUrl = "file:///android_asset/transition/" + asset.uuid + ".png";
                        AssetItem assetItem = new AssetItem();
                        assetItem.setAsset(asset);
                        assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
                        mAssetsList.add(assetItem);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLocalAssetsList(TransitionPATHParent, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION);
    }

    @Override
    public void onAssetItemSelected(String uuid) {
        NvsRational nvsRational = new NvsRational(16, 9);
        NvsVideoEffectTransition transitionFilter = mEffectSdkContext.createVideoTransition(uuid, nvsRational);
        if (transitionFilter != null) {
            transitionFilter.setVideoTransitionDuration(3000000);
        }
        if (mOnNvEffectSelectListener != null) {
            mOnNvEffectSelectListener.onNvEffectSelected(uuid, transitionFilter, Constants.EDIT_MODE_TRANSITION);
        }
    }
}
