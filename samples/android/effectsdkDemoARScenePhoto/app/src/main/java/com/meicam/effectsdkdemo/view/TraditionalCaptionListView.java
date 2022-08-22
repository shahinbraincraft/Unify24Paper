package com.meicam.effectsdkdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.meicam.effect.sdk.NvsVideoEffectCaption;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.effectsdkdemo.Constants;
import com.meicam.effectsdkdemo.MainActivity;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.data.AssetItem;
import com.meicam.effectsdkdemo.data.NvAsset;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsRational;

import java.io.IOException;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Meng Guijun
 * @CreateDate: 2021/2/3 10:06
 * @Description:
 * @Copyright:2021 www.meishesdk.com Inc. All rights reserved.
 */
public class TraditionalCaptionListView extends BaseRecycleListView{

    private static final String CAPTION_STYLE_PATH_PARENT = "/storage/emulated/0/NvStreamingSdk/Asset/TestCaptionStyle";
    private boolean showDialog = true;
    private String captionText = "";
    private String mUuid;
    public TraditionalCaptionListView(Context context) {
        super(context);
    }

    public TraditionalCaptionListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initAssetsList() {
        mAssetsList.clear();
        NvAsset emptyAsset = new NvAsset();
        emptyAsset.uuid = null;
        emptyAsset.coverUrl = "file:///android_asset/captionstyle/captionstyle_no.png";
        AssetItem emptyAssetItem = new AssetItem();
        emptyAssetItem.setAsset(emptyAsset);
        emptyAssetItem.setAssetMode(AssetItem.ASSET_LOCAL);
        mAssetsList.add(emptyAssetItem);
        try {
            String[] name_list = mContext.getAssets().list("captionstyle");
            if (name_list != null) {
                for (int i = 0; i < name_list.length; ++i) {
                    String name = name_list[i];
                    if (name.endsWith(".captionstyle")) {
                        String assetPackageFilePath = "assets:/captionstyle/" + name;
                        StringBuilder packageUuid = new StringBuilder();
                        boolean installSuccess = installAssetsPackage(assetPackageFilePath, packageUuid, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTIONSTYLE);
                        if (!installSuccess) {
                            continue;
                        }
                        NvAsset asset = new NvAsset();
                        asset.uuid = packageUuid.toString();
                        asset.coverUrl = "file:///android_asset/captionstyle/" + asset.uuid + ".png";
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
        getLocalAssetsList(CAPTION_STYLE_PATH_PARENT, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION);
    }

    @Override
    public void onAssetItemSelected(String uuid) {
        mUuid = uuid;
        if (showDialog){
            new InputDialog(mContext, R.style.dialog, new InputDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean ok) {
                    if (ok) {
                        InputDialog d = (InputDialog) dialog;
                        String userInputText = d.getUserInputText();
                        addCaption(userInputText);
                    }
                }
            }).show();
        } else {
            addCaption(captionText);
            showDialog = true;
        }
    }

    private void addCaption(String caption) {
        NvsRational nvsRational = new NvsRational(16, 9);
        NvsVideoEffectCaption mCurVideoEffectCaption = mEffectSdkContext.createCaption(caption, 0, Long.MAX_VALUE, mUuid, nvsRational);
        String markTag = System.currentTimeMillis() + "";
        if (mOnNvEffectSelectListener != null) {
            mOnNvEffectSelectListener.onNvEffectSelected(TextUtils.isEmpty(mUuid) ?  markTag : mUuid, mCurVideoEffectCaption, Constants.EDIT_MODE_CAPTION);
        }
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public void setCaptionText(String captionText) {
        this.captionText = captionText;
    }
}
