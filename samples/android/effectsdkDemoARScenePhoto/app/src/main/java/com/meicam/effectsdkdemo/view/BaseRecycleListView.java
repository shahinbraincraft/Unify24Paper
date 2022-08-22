package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.effectsdkdemo.Constants;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.adapter.BaseAdaper;
import com.meicam.effectsdkdemo.adapter.SpaceItemDecoration;
import com.meicam.effectsdkdemo.data.AssetItem;
import com.meicam.effectsdkdemo.data.NvAsset;
import com.meicam.effectsdkdemo.interfaces.OnItemClickListener;
import com.meicam.effectsdkdemo.interfaces.OnNvEffectSelectListener;
import com.meicam.effectsdkdemo.utils.ScreenUtils;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsRational;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: Meng Guijun
 * @CreateDate: 2021/2/1 15:04
 * @Description:
 * @Copyright:2021 www.meishesdk.com Inc. All rights reserved.
 */
public abstract class BaseRecycleListView extends RelativeLayout {

    private static final String TAG = "CompoundCaptionView";



    private NvsVideoEffectCompoundCaption mCompoundCaptionFilter;
    private RecyclerView mRecyclerView;
    private BaseAdaper mBaseAdaper;

    protected Context mContext;
    protected NvsEffectSdkContext mEffectSdkContext;
    protected OnNvEffectSelectListener mOnNvEffectSelectListener;
    protected ArrayList<AssetItem> mAssetsList = new ArrayList<>();

    public BaseRecycleListView(Context context) {
        super(context);
        initView(context);
    }

    public BaseRecycleListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mEffectSdkContext = NvsEffectSdkContext.getInstance();
        View rootView = LayoutInflater.from(context).inflate(R.layout.compound_caption_view, this);
        mRecyclerView = rootView.findViewById(R.id.comCaptionRecycler);
//        initCompoundCaptionStyleList();
        initAssetsList();
        initRecycleAdapter();
    }

    public void setmOnNvEffectSelectListener(OnNvEffectSelectListener nvEffectSelectListener) {
        this.mOnNvEffectSelectListener = nvEffectSelectListener;
    }

    public abstract void initAssetsList();
    public abstract void onAssetItemSelected(String uuid);

    public boolean installAssetsPackage(String assetPackageFilePath, StringBuilder packageUuid, int type) {
        int retResult = mEffectSdkContext.getAssetPackageManager().installAssetPackage(assetPackageFilePath, null,
                type, true, packageUuid);
        if (retResult != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR && retResult != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            Log.e(TAG, "failed to install package = " + assetPackageFilePath);
            return false;
        }
        return true;
    }

    private void initRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mBaseAdaper = new BaseAdaper(mContext);
        mBaseAdaper.setAssetList(mAssetsList);
        mRecyclerView.setAdapter(mBaseAdaper);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(0, ScreenUtils.dip2px(mContext, 8)));
        mBaseAdaper.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                int captiontStyleCount = mAssetsList.size();
                if (pos < 0 || pos >= captiontStyleCount) {
                    return;
                }

                AssetItem assetItem = mAssetsList.get(pos);
                if (assetItem == null) {
                    return;
                }
                NvAsset asset = assetItem.getAsset();
                if (asset == null) {
                    return;
                }

                onAssetItemSelected(asset.uuid);
            }
        });
    }

    public void getLocalAssetsList(String folderPath, int type) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] fileList = file.listFiles();
        if (fileList != null ) {
            for (int i = 0; i <fileList.length ; i++) {
                String filePath = fileList[i].getAbsolutePath();
                StringBuilder packageUuid = new StringBuilder();
                boolean installSuccess = installAssetsPackage(filePath, packageUuid, type);
                if (!installSuccess) {
                    continue;
                }
                NvAsset asset = new NvAsset();
                asset.uuid = packageUuid.toString();
                asset.coverUrl = folderPath + asset.uuid + ".png";
                AssetItem assetItem = new AssetItem();
                assetItem.setAsset(asset);
                assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
                mAssetsList.add(assetItem);
            }
        }
    }
}
