package com.meicam.effectsdkdemo.view;

import static com.meicam.effectsdkdemo.MainActivity.STICKER_DURATION;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
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
public class StickerListView extends BaseRecycleListView{

    private static final String STICKERPATHParent = "/storage/emulated/0/NvStreamingSdk/Asset/TestSticker";

    public StickerListView(Context context) {
        super(context);
    }

    public StickerListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initAssetsList() {
        mAssetsList.clear();
        try {
            String[] name_list = mContext.getAssets().list("sticker");
            if (name_list != null) {
                for (int i = 0; i < name_list.length; ++i) {
                    String name = name_list[i];
                    if (name.endsWith(".animatedsticker")) {
                        String assetPackageFilePath = "assets:/sticker/" + name;
                        StringBuilder packageUuid = new StringBuilder();
                        boolean installSuccess = installAssetsPackage(assetPackageFilePath, packageUuid, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER);
                        if (!installSuccess) {
                            continue;
                        }
                        NvAsset asset = new NvAsset();
                        asset.uuid = packageUuid.toString();
                        asset.coverUrl = "file:///android_asset/sticker/" + asset.uuid + ".png";
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
        getLocalAssetsList(STICKERPATHParent, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER);
    }

    @Override
    public void onAssetItemSelected(String uuid) {
        NvsRational nvsRational = new NvsRational(16, 9);
        NvsVideoEffectAnimatedSticker stickerFilter = null;
        if(TextUtils.equals(uuid,"5D9FA998-7600-492F-9DF4-BC2FA5E869BD")){
            //贴纸模板包,custom sticker
            String path = "assets:/bg.png";
            //stickerFilter = mEffectSdkContext.createCustomAnimatedSticker(0,STICKER_DURATION /*Long.MAX_VALUE*/, false, uuid, path,nvsRational);
        }else{
            stickerFilter = mEffectSdkContext.createAnimatedSticker(0,STICKER_DURATION /*Long.MAX_VALUE*/, false, uuid, nvsRational);
        }
        if (mOnNvEffectSelectListener != null) {
            mOnNvEffectSelectListener.onNvEffectSelected(uuid, stickerFilter, Constants.EDIT_MODE_STICKER);
        }
    }
}
