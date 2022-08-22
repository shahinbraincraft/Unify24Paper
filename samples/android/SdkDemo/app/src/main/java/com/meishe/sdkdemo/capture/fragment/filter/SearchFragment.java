package com.meishe.sdkdemo.capture.fragment.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.LogUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.BR;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.fragment.BaseFragment;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.KeyBoardUtil;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment {

    private EditText mEtSearch;
    private TextView mTvCancel;
    private boolean searchContentEmpty;
    private boolean isSearching;
    private String mPackageUrl;
    private List<EffectInfo> mData = new ArrayList<>();

    public SearchFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static SearchFragment newInstance(String param1) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mEtSearch = view.findViewById(R.id.et_search);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        initRecyclerView(LinearLayoutManager.HORIZONTAL,R.layout.capture_filter_item_view, BR.filterInfo);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreamingContext = NvsStreamingContext.getInstance();
    }

    @Override
    protected int initRootView() {
        return 0;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initListener() {

        mCommonRecyclerViewAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.
                OnItemClickListener<EffectInfo>() {
            @Override
            public void onItemClick(View view, int posotion, EffectInfo filterInfo) {
                for (EffectInfo info : mData) {
                    info.setSelect(false);
                }
                filterInfo.setSelect(true);
                ///storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/NvStreamingSdk/Asset/Filter/26073681-6655-4C77-9A49-3C00565C05AA.1.videofx
                String effectPath = getFilterPath(filterInfo,NvAsset.ASSET_FILTER);
                String effectId = (String) SharedPreferencesUtils.getParam(mContext, effectPath, "");
                mPackageUrl = filterInfo.getPackageUrl();
                if (TextUtils.isEmpty(effectId)) {
                    downloadPackage(filterInfo);
                } else {
                    setCaptureFilterByPath(effectId, effectPath);
                }
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //关闭软键盘
                    KeyBoardUtil.hideSoftKeyBroad(mEtSearch, mEtSearch.getContext());
                    //do something
                    //doSearch();
                    String searchContent = mEtSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchContent)) {
                        searchDataByContent(searchContent);
                        isSearching = true;
                    }
                    return true;
                }
                return false;
            }
        });

        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTvCancel.setVisibility(View.VISIBLE);
                } else {
                    mTvCancel.setVisibility(View.GONE);
                }
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchContent = s.toString().trim();
                if (TextUtils.isEmpty(searchContent)) {
                    //隐藏清空搜索懒得按钮

                    mEtSearch.setCompoundDrawables(null, null, null, null);
                    searchContentEmpty = true;
                    isSearching = false;
                } else {
                    //如果当前不是空，但是之前是空的，此时显示出清空图标
                    if (searchContentEmpty) {
                        Drawable drawable = getResources().getDrawable(
                                R.mipmap.filter_search_close);
                        // / 这一步必须要做,否则不会显示.
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());
                        mEtSearch.setCompoundDrawables(null, null, drawable, null);
                    }
                    mTvCancel.setVisibility(View.VISIBLE);
                    searchContentEmpty = false;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //显示光标
                mEtSearch.setCursorVisible(true);
                //拿到drawableRight
                Drawable drawable = mEtSearch.getCompoundDrawables()[2];
                if (null == drawable) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }

                if (event.getX() > mEtSearch.getWidth() - mEtSearch.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    mEtSearch.setText("");
                }
                return false;
            }
        });
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

        final String path = PathUtils.getAssetDownloadPath(NvAsset.ASSET_FILTER);
        String[] split = packageUrl.split("/");
        HttpManager.download(packageUrl, packageUrl, path, split[split.length - 1], new SimpleDownListener(packageUrl) {
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
                    installEffect(filterInfo,absolutePath);
                }
            }
        });
    }

    private void installEffect(EffectInfo filterInfo, String effectPackageFilePath) {
        if (null==filterInfo){
            return;
        }
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        StringBuilder stringBuilder = new StringBuilder();
        int i = assetPackageManager.installAssetPackage(effectPackageFilePath, null,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, stringBuilder);
        LogUtils.d("installEffect code=" + i);
        SharedPreferencesUtils.setParam(mContext, effectPackageFilePath, stringBuilder.toString());
        applyEffect(stringBuilder.toString());

    }


    private void applyEffect(String effectId) {
        MSBus.getInstance().post(Constants.SubscribeType.SUB_REFRESH_DATA_TYPE,mPackageUrl);
        MSBus.getInstance().post(Constants.SubscribeType.SUB_APPLY_FILTER_TYPE, effectId);
    }

    private void setCaptureFilterByPath(String filterId, String effectPackageFilePath) {
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(filterId,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            assetPackageManager.installAssetPackage(effectPackageFilePath, null,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, false, null);
        } else {
            applyEffect(filterId);
        }
    }

    /**
     * 搜索的方法
     *
     * @param searchContent
     */
    private void searchDataByContent(String searchContent) {
        getFilterData(searchContent);
    }

    private void getFilterData(String searchContent) {
        HttpManager.getMaterialList(null, AssetType.FILTER_ALL.getType(), searchContent,
                1, Constants.PAGE_SIZE, new RequestCallback<BaseBean<EffectInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<EffectInfo>> response) {
                        if (response.getCode() == 1) {
                            mData=new ArrayList<>();
                            List<EffectInfo> elements = response.getData().getElements();
                            if (elements != null) {
                                for (EffectInfo filterInfo : elements) {
                                    if (MSApplication.isZh()) {
                                        filterInfo.setName(filterInfo.getDisplayNameZhCn());
                                    } else {
                                        filterInfo.setName(filterInfo.getDisplayName());
                                    }
                                    String effectPath = getFilterPath(filterInfo,NvAsset.ASSET_FILTER);
                                    String effectId = (String) SharedPreferencesUtils.getParam(mContext, effectPath, "");
                                    if (!TextUtils.isEmpty(effectId)) {
                                        filterInfo.setDownload(true);
                                    }else{
                                        filterInfo.setDownload(false);
                                    }
                                }
                                mData.addAll(elements);
                                mCommonRecyclerViewAdapter.setData(mData);
                            }
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<EffectInfo>> response) {
                    }
                });
    }


    @MSSubscribe("unSelectAll")
    private void unSelectAll() {
        for (EffectInfo info : mData) {
            info.setSelect(false);
        }
        MSBus.getInstance().post("hideFilterSeekView");
        MSBus.getInstance().post(Constants.SubscribeType.SUB_REMO_ALL_FILTER_TYPE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MSBus.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MSBus.getInstance().register(this);
    }
}