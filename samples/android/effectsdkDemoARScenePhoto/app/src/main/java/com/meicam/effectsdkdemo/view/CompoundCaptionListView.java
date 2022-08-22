package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
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
public class CompoundCaptionListView extends BaseRecycleListView{

    private static final String COMPOUNDCAPTIONPATHParent = "/storage/emulated/0/NvStreamingSdk/Asset/TestCompoundCaption";

    public CompoundCaptionListView(Context context) {
        super(context);
    }

    public CompoundCaptionListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initAssetsList() {
        mAssetsList.clear();
        try {
            String[] name_list = mContext.getAssets().list("compoundcaption");
            if (name_list != null) {
                for (int i = 0; i < name_list.length; ++i) {
                    String name = name_list[i];
                    if (name.endsWith(".compoundcaption")) {
                        String assetPackageFilePath = "assets:/compoundcaption/" + name;
                        StringBuilder packageUuid = new StringBuilder();
                        boolean installSuccess = installAssetsPackage(assetPackageFilePath, packageUuid, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION);
                        if (!installSuccess) {
                            continue;
                        }
                        NvAsset asset = new NvAsset();
                        asset.uuid = packageUuid.toString();
                        asset.coverUrl = "file:///android_asset/compoundcaption/" + asset.uuid + ".png";
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
        this.getLocalAssetsList(COMPOUNDCAPTIONPATHParent, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION);
    }

    @Override
    public void onAssetItemSelected(String uuid) {
        NvsRational nvsRational = new NvsRational(16, 9);
        NvsVideoEffectCompoundCaption compoundCaptionFilter = mEffectSdkContext.createCompoundCaption(0, Long.MAX_VALUE, uuid, nvsRational);
        if (mOnNvEffectSelectListener != null) {
            mOnNvEffectSelectListener.onNvEffectSelected(uuid, compoundCaptionFilter, Constants.EDIT_MODE_COMPOUND_CAPTION);
        }
    }
}
