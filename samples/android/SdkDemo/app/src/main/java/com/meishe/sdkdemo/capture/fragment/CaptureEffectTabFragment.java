package com.meishe.sdkdemo.capture.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.LogUtils;
import com.meishe.mvvm.BaseListMvvmModel;
import com.meishe.mvvm.IBaseModelListener;
import com.meishe.mvvm.PagingResult;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.viewmodel.CaptureDownloadModel;
import com.meishe.sdkdemo.capture.viewmodel.CaptureEffectTabViewModel;
import com.meishe.sdkdemo.capture.viewmodel.MainViewModelFactory;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class CaptureEffectTabFragment extends BaseFragment implements IBaseModelListener<List<EffectInfo>> {


    public static final String TAG = "CaptureEffectTabFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private CategoryInfo mCategoryInfo;
    private String mEffectPathDir;
    private String[] mSplit;
    private int mAssetType;
    private int mInstallType;
    private String mTag;
    private CaptureEffectTabViewModel mCaptureEffectTabViewModel;
    private CaptureDownloadModel mCaptureDownloadModel;
    private SmartRefreshLayout mRefreshLayout;

    /**
     * 是否还有下一页
     */
    private boolean mHasNext;
    private String mTitle;
    private List<EffectInfo> mData = new ArrayList<>();

    @Inject
    ViewModelProvider.Factory mViewModelProvider;

    public CaptureEffectTabFragment() {
        // Required empty public constructor
    }

    public static CaptureEffectTabFragment newInstance(CategoryInfo categoryInfo, String tag, String title) {
        CaptureEffectTabFragment fragment = new CaptureEffectTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, categoryInfo);
        args.putString(ARG_PARAM2, tag);
        args.putString(ARG_PARAM3, title);
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
            mCategoryInfo = (CategoryInfo) getArguments().getSerializable(ARG_PARAM1);
            mTag = (String) getArguments().getSerializable(ARG_PARAM2);
            mTitle = (String) getArguments().getSerializable(ARG_PARAM3);
        }

        if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_ANIMATED_STICKER;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER;
        } else if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_COMPOUND_CAPTION;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            mAssetType = NvAsset.ASSET_ARSCENE_FACE;
            mInstallType = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE;
        }

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mRecyclerView = findViewById(R.id.recyclerView);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        initRecyclerViewGrid(5, R.layout.capture_effect_tab_view, BR.filterInfo);
        initViewModel();
    }

    @Override
    protected void onLazyLoad() {

    }


    @Override
    protected void initData() {

    }

    private void initViewModel() {
        mCategoryInfo.setAssetType(mAssetType);
        mCaptureDownloadModel=ViewModelProviders.of(this,mViewModelProvider).get(CaptureDownloadModel.class);
        mCaptureEffectTabViewModel = new ViewModelProvider(this, new MainViewModelFactory(mCategoryInfo)).
                get(CaptureEffectTabViewModel.class);
        mCaptureEffectTabViewModel.register(this);
        mCaptureEffectTabViewModel.refresh();
        mBinding.setVariable(BR.isLoading, true);
        mCaptureDownloadModel.getFilePath().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                installEffect(s);
            }
        });
    }

    @Override
    public void initListener() {
        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.
                OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo effectInfo) {

                for (EffectInfo info : mData) {
                    info.setSelect(false);
                }
                effectInfo.setSelect(true);
                String packageUrl = effectInfo.getPackageUrl();
                mEffectPathDir = PathUtils.getAssetDownloadPath(mAssetType);
                mSplit = packageUrl.split("/");
                String effectPath = mEffectPathDir + File.separator + mSplit[mSplit.length - 1];
                String effectId = (String) SharedPreferencesUtils.getParam(mContext, effectPath, "");
                if (TextUtils.isEmpty(effectId)) {
                    effectInfo.setAssetType(mAssetType);
                    mCaptureDownloadModel.downloadPackage(effectInfo);
                } else {
                    setCaptureFilterByPath(effectId, effectPath);
                }
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mBinding.setVariable(BR.isLoading, false);
                mCaptureEffectTabViewModel.refresh();
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mHasNext) {
                    mCaptureEffectTabViewModel.loadNextPage();
                } else {
                    // 将不会再次触发加载更多事件
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }

            }
        });

    }


    private void setCaptureFilterByPath(String filterId, String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(filterId,
                mInstallType);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            installEffect(effectPackageFilePath);
        } else {
            applyEffect(filterId);
        }
    }


    private void applyEffect(String effectId) {
        if (Constants.FRAGMENT_COMPONENT_CAPTION_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE, effectId);
        } else if (Constants.FRAGMENT_STICKER_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE, effectId);
        } else if (Constants.FRAGMENT_PROP_TAG.equals(mTag)) {
            MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_PROP_TYPE, effectId);
        }
    }

    private void installEffect(String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        StringBuilder stringBuilder = new StringBuilder();
        int i = assetPackageManager.installAssetPackage(effectPackageFilePath, null,
                mInstallType, true, stringBuilder);

        NvsAssetPackageManager assetPackageManagerEffect = mNvsEffectSdkContext.getAssetPackageManager();
        assetPackageManagerEffect.installAssetPackage(effectPackageFilePath, null,
                mInstallType, false, null);

        LogUtils.d("installEffect code=" + i);
        SharedPreferencesUtils.setParam(mContext, effectPackageFilePath, stringBuilder.toString());
        applyEffect(stringBuilder.toString());
    }


    @MSSubscribe(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE)
    private void unSelectAll() {
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }

    }

    @MSSubscribe(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE)
    private void refreshData(String packageUrl) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Object> emitter) throws Exception {
                if (mData != null) {
                    for (EffectInfo filterInfo : mData) {
                        if (MSApplication.isZh()) {
                            filterInfo.setName(filterInfo.getDisplayNameZhCn());
                        } else {
                            filterInfo.setName(filterInfo.getDisplayName());
                        }

                        String effectPath = getFilterPath(filterInfo, mAssetType);
                        String effectId = (String) SharedPreferencesUtils.getParam(mContext, effectPath, "");
                        if (!TextUtils.isEmpty(effectId)) {
                            filterInfo.setDownload(true);
                        }

                        filterInfo.setSelect(false);
                        if (filterInfo.getPackageUrl().equals(packageUrl)) {
                            filterInfo.setSelect(true);
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        MSBus.getInstance().unregister(this);
    }

    @Override
    public void onLoadSuccess(BaseListMvvmModel model, List<EffectInfo> data, PagingResult... result) {
        if (data!=null){
            if(result != null && result.length > 0 && result[0].isFirstPage) {
                mData.clear();
            }
            mHasNext = result[0].hasNextPage;
            if (!checkoutIsContainer(mData, data)) {
                mData.addAll(data);
            }
            mBinding.setVariable(BR.isLoading, false);
            mCommonRecyclerViewAdapter.setData(mData);
        }
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.finishLoadMore();
        mRefreshLayout.finishRefresh();
    }

    private boolean checkoutIsContainer(List<EffectInfo> dataOrigin, List<EffectInfo> data) {
        for (EffectInfo datum : data) {
            for (EffectInfo effectInfo : dataOrigin) {
                if (effectInfo.getPackageUrl().equals(datum.getPackageUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onLoadFail(BaseListMvvmModel model, String message, PagingResult... result) {
        ToastUtil.showToast(mContext, message);
    }
}