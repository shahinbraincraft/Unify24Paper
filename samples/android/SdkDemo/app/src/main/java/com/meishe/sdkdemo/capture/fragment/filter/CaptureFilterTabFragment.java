package com.meishe.sdkdemo.capture.fragment.filter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.fragment.BaseFragment;
import com.meishe.sdkdemo.capture.viewmodel.FilterViewModel;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CaptureFilterTabFragment extends BaseFragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    /**
     * 特效类型数据结构
     */
    private KindInfo mKindInfo;
    private String mEffectPathDir;
    private String[] mSplit;
    private FilterViewModel mFilterViewModel;
    private List<EffectInfo> mData = new ArrayList<>();

    @Inject
    ViewModelProvider.Factory mViewModelProvider;
    private EffectInfo mDefaultInfo;
    private boolean isFirst;

    public CaptureFilterTabFragment() {
        // Required empty public constructor
    }

    public static CaptureFilterTabFragment newInstance(KindInfo mKindInfo) {
        CaptureFilterTabFragment fragment = new CaptureFilterTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, mKindInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mKindInfo = (KindInfo) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MSBus.getInstance().register(this);
    }


    @Override
    protected int initRootView() {
        return R.layout.fragment_capture_filter_item;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {
        mBinding.setVariable(BR.isLoading, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerView(LinearLayoutManager.HORIZONTAL, R.layout.capture_filter_item_view, BR.filterInfo);
    }

    @Override
    protected void onLazyLoad() {
        initViewModel();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.
                OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo filterInfo) {
                for (EffectInfo info : mData) {
                    info.setSelect(false);
                }
                filterInfo.setSelect(true);
                ///storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/NvStreamingSdk/Asset/Filter/26073681-6655-4C77-9A49-3C00565C05AA.1.videofx
                if (mDefaultInfo != null && filterInfo.getId().equals(mDefaultInfo.getId())) {
                    applyEffect(filterInfo.getId());
                    return;
                }
                String packageUrl = filterInfo.getPackageUrl();
                mEffectPathDir = PathUtils.getAssetDownloadPath(NvAsset.ASSET_FILTER);
                mSplit = packageUrl.split("/");
                String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
                String effectId = (String) SharedPreferencesUtils.getParam(mContext, effectPath, "");
                if (TextUtils.isEmpty(effectId)) {
                    downloadPackage(filterInfo);
                } else {
                    setCaptureFilterByPath(effectId, effectPath);
                }
            }
        });
    }

    private void initViewModel() {
        mFilterViewModel = ViewModelProviders.of(this, mViewModelProvider).get(FilterViewModel.class);
        mFilterViewModel.requestFilterData(mKindInfo);
        mFilterViewModel.getFilterLiveData().observeForever(new Observer<BaseBean<EffectInfo>>() {
            @Override
            public void onChanged(BaseBean<EffectInfo> filterInfos) {
                mBinding.setVariable(BR.isLoading, false);
                updateFilterInfo(filterInfos.getElements());
            }
        });
    }

    public void setDefaultFilterInfo(EffectInfo effectInfo) {
        if (effectInfo == null) {
            return;
        }
        isFirst = true;
        mDefaultInfo = effectInfo;
        String id = effectInfo.getId();
        if (mData.size() == 0) {
            mData.add(effectInfo);
        } else {
            EffectInfo effectInfo1 = mData.get(0);
            if (!effectInfo1.getId().equals(id)) {
                mData.add(0, mDefaultInfo);
            }
        }
    }

    private void updateFilterInfo(List<EffectInfo> filterInfos) {
        if (filterInfos != null) {
            for (EffectInfo filterInfo : filterInfos) {
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
        mData.addAll(mData.size(), filterInfos);
        if (mDefaultInfo != null) {
            for (int i = 1; i < mData.size(); i++) {
                EffectInfo effectInfo = mData.get(i);
                if (effectInfo.getId().equals(mDefaultInfo.getId())) {
                    mData.remove(effectInfo);
                }
            }
        }
        mCommonRecyclerViewAdapter.setData(mData);
        if (isFirst) {
            isFirst = false;
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder viewHolderForAdapterPosition = mRecyclerView.findViewHolderForAdapterPosition(0);
                    viewHolderForAdapterPosition.itemView.performClick();
                }
            });

        }
    }


    /**
     * 下载特效包
     *
     * @param filterInfo
     */
    private void downloadPackage(EffectInfo filterInfo) {
        if (null == filterInfo) {
            return;
        }
        String packageUrl = filterInfo.getPackageUrl();
        if (TextUtils.isEmpty(packageUrl)) {
            return;
        }

        HttpManager.download(packageUrl, packageUrl, mEffectPathDir, mSplit[mSplit.length - 1], new SimpleDownListener(packageUrl) {
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
                    installEffect(absolutePath);
                }
            }
        });
    }

    private void setCaptureFilterByPath(String filterId, String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(filterId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            installEffect(effectPackageFilePath);
        } else {
            applyEffect(filterId);
        }
    }

    private void applyEffect(String effectId) {
        MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_FILTER_TYPE, effectId);
    }

    private void installEffect(String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        StringBuilder stringBuilder = new StringBuilder();
        int i = assetPackageManager.installAssetPackage(effectPackageFilePath, null,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, stringBuilder);

        NvsAssetPackageManager assetPackageManagerEffect = mNvsEffectSdkContext.getAssetPackageManager();
        assetPackageManagerEffect.installAssetPackage(effectPackageFilePath, null,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, false, null);

        LogUtils.d("installEffect code=" + i);
        SharedPreferencesUtils.setParam(mContext, effectPackageFilePath, stringBuilder.toString());
        applyEffect(stringBuilder.toString());
    }

    @MSSubscribe(Constants.SubscribeType.SUB_UN_USE_FILTER_TYPE)
    private void unUseFilter() {
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }
        MSBus.getInstance().post("hideFilterSeekView");
        MSBus.getInstance().post(Constants.SubscribeType.SUB_REMO_ALL_FILTER_TYPE);
    }

    @MSSubscribe(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE)
    private void refreshData(String packageUrl) {
        mFilterViewModel.refreshData(mData, packageUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        MSBus.getInstance().unregister(this);
    }
}