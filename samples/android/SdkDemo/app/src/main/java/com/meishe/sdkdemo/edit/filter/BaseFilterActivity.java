package com.meishe.sdkdemo.edit.filter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.dialog.TipsDialog;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.view.FilterView;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate : 2019/10/29.
 * @Description :视频编辑-滤镜-BaseActivity
 * @Description :VideoEdit-Filter-BaseActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseFilterActivity extends BaseActivity {
    private static final String TAG = "BaseFilterActivity";

    private static final int REQUEST_FILTER_LIST_CODE = 102;
    protected VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;

    private ImageView mFilterAssetFinish;

    private ArrayList<FilterItem> mFilterItemArrayList;
    private int mAssetType = NvAsset.ASSET_FILTER;
    private NvAssetManager mAssetManager;
    protected FilterView mFilterView;
    protected NvsTimeline mTimeline;
    protected VideoClipFxInfo mVideoClipFxInfo;
    protected int mSelectedPos = 0;
    NvsStreamingContext mStreamingContext;
    private TipsDialog mTipsDialog;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_filter;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mFilterView = (FilterView) findViewById(R.id.filterView);
        mFilterAssetFinish = (ImageView) findViewById(R.id.filterAssetFinish);
        mTipsDialog = new TipsDialog(this);
        mFilterView.setBlackTheme(true);
        initSubViews();
    }

    protected void initSubViews() {

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.filter);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        init();
        initFilterDataList();
        initVideoFragment();
        initFilterView();
        afterIntentInit();
    }

    @Override
    protected void initListener() {
        mFilterAssetFinish.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_FILTER_LIST_CODE:
                initFilterDataList();
                mFilterView.setFilterArrayList(mFilterItemArrayList);
                mSelectedPos = AssetFxUtil.getSelectedFilterPos(mFilterItemArrayList, mVideoClipFxInfo);
                mFilterView.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    protected void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    protected void quitActivity() {
        AppManager.getInstance().finishActivity();
    }

    private void init() {
        mTimeline = initTimeLine();
        mVideoClipFxInfo = initClipFxInfo();
        mAssetManager = getNvAssetManager();
        mAssetManager.searchLocalAssets(mAssetType);
        String bundlePath = "filter";
        mAssetManager.searchReservedAssets(mAssetType, bundlePath);
    }

    protected void afterIntentInit() {
    }

    protected abstract VideoClipFxInfo initClipFxInfo();

    protected abstract NvsTimeline initTimeLine();

    private void initFilterDataList() {
        mFilterItemArrayList = AssetFxUtil.getFilterData(this,
                getLocalData(),
                null,
                true,
                true);
    }

    private ArrayList<NvAsset> getBundleData() {
        return mAssetManager.getReservedAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private ArrayList<NvAsset> getLocalData() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putBoolean("playBarVisible", true);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {

            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                playbackTimelinePositionFromParent(timeline, stamp);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
                streamingEngineStateChangedFromParent(state);
            }
        });
    }

    protected void streamingEngineStateChangedFromParent(int state) {
        //
    }

    protected void playbackTimelinePositionFromParent(NvsTimeline timeline, long stamp) {
        //
    }

    private void initFilterView() {
        mFilterView.setFilterArrayList(mFilterItemArrayList);
        mFilterView.initFilterRecyclerView(this);
        mSelectedPos = AssetFxUtil.getSelectedFilterPos(mFilterItemArrayList, mVideoClipFxInfo);
        // 只更改界面 不触发点击
        mFilterView.setSelectedPos(mSelectedPos);
        mFilterView.setIntensityLayoutVisible(mSelectedPos <= 0 ? View.INVISIBLE : View.VISIBLE);
        mFilterView.setIntensityTextVisible(View.VISIBLE);
        mFilterView.setIntensitySeekBarMaxValue(100);
        float intensity = mVideoClipFxInfo.getFxIntensity();
        mFilterView.setIntensitySeekBarProgress((int) (intensity * 100));
        mFilterView.setFilterFxListBackgroud("#00000000");
        mFilterView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, final int position) {
                final int currentSelectedPos = mFilterView.getSelectedPos();
                int count = mFilterItemArrayList.size();
                if (position < 0 || position >= count) {
                    return;
                }
                if (mSelectedPos == position) {
                    mVideoFragment.playVideoButtonClick();
                    return;
                }
                if (mVideoClipFxInfo.getKeyFrameInfoMap().isEmpty()) {
                    // 没有关键帧 直接替换  No key frame direct replacement
                    mSelectedPos = position;
                    mFilterView.setIntensitySeekBarProgress(100);
                    if (position == 0) {
                        mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                        mVideoClipFxInfo.setFxId(null);
                    } else {
                        if (isNeedShowSeekBarWhenChangeFilterFromParent()) {
                            mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                        }
                        FilterItem filterItem = mFilterItemArrayList.get(position);
                        int filterMode = filterItem.getFilterMode();
                        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                            String filterName = filterItem.getFilterName();
                            mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                            mVideoClipFxInfo.setFxId(filterName);
                            mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                            mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                            mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
                        } else {
                            String packageId = filterItem.getPackageId();
                            mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                            mVideoClipFxInfo.setFxId(packageId);
                        }
                        mVideoClipFxInfo.setFxIntensity(1.0f);
                    }
                    onFilterChanged(position);
                    onFilterChanged(mTimeline, mVideoClipFxInfo);
                    if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                        mVideoFragment.playVideoButtonClick();
                    }
                } else {
                    // 有关键帧 进行dialog提示
                    //There is a key frame for dialog prompt
                    if ((mTipsDialog != null) && !mTipsDialog.isShowing()) {
                        mTipsDialog.setOnBtnClickListener(new TipsDialog.OnBtnClickListener() {
                            @Override
                            public void OnConfirmBtnClicked() {
                                mSelectedPos = position;
                                mFilterView.setIntensitySeekBarProgress(100);
                                if (position == 0) {
                                    mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                                    mVideoClipFxInfo.setFxId(null);
                                } else {
                                    if (isNeedShowSeekBarWhenChangeFilterFromParent()) {
                                        mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                                    }
                                    FilterItem filterItem = mFilterItemArrayList.get(position);
                                    int filterMode = filterItem.getFilterMode();
                                    if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                                        String filterName = filterItem.getFilterName();
                                        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                                        mVideoClipFxInfo.setFxId(filterName);
                                        mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                                        mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                                        mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
                                    } else {
                                        String packageId = filterItem.getPackageId();
                                        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                                        mVideoClipFxInfo.setFxId(packageId);
                                    }
                                    mVideoClipFxInfo.setFxIntensity(1.0f);
                                }
                                onFilterChanged(position);
                                onFilterChanged(mTimeline, mVideoClipFxInfo);
                                if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                                    mVideoFragment.playVideoButtonClick();
                                }
                                mTipsDialog.dismiss();
                            }

                            @Override
                            public void OnCancelBtnClicked() {
                                mFilterView.setSelectedPos(currentSelectedPos);
                                mTipsDialog.dismiss();
                            }
                        });
                        mTipsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mFilterView.setSelectedPos(currentSelectedPos);
                            }
                        });
                        mTipsDialog.show();
                        // 添加切换滤镜 移除关键帧的文案提示
                        // Add switching filter, remove key frame copywriting tips
                        mTipsDialog.setTipsText(R.string.replace_keyFrame_ffects);
                    }
                }
            }

            @Override
            public void onMoreFilter() {
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "edit_filter");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
                mFilterView.setMoreFilterClickable(false);
            }

            @Override
            public void onIntensity(int value) {
                float intensity = value / (float) 100;
                mVideoClipFxInfo.setFxIntensity(intensity);
                updateFxIntensity(intensity);
                if (mStreamingContext.getStreamingEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                }
            }
        });
    }

    protected void onFilterChanged(int position) {
        //
    }

    // 子类用来判断是不是有关键帧数据 来判断是否显示默认强度 seekbar
    // The subclass is used to determine whether there is key frame data to determine whether to display the default strength seekbar
    protected boolean isNeedShowSeekBarWhenChangeFilterFromParent() {
        return true;
    }

    /**
     * 滤镜改变时调用
     * Called when the filter changes
     *
     * @param timeline          timeline
     * @param changedClipFilter 更改后的Filter；Changed Filter
     */
    protected abstract void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter);

    private void updateFxIntensity(float value) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }

        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }

            int fxCount = videoClip.getFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsVideoFx fx = videoClip.getFxByIndex(j);
                if (fx == null) {
                    continue;
                }

                String name = fx.getBuiltinVideoFxName();
                if (name == null) {
                    continue;
                }
                if (name.equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)
                        || name.equals(Constants.ADJUST_TYPE_SHARPEN)
                        || name.equals(Constants.ADJUST_TYPE_VIGETTE)
                        || name.equals(Constants.ADJUST_TYPE_TINT)
                        || name.equals(Constants.ADJUST_TYPE_DENOISE)
                        ||name.equals(Constants.FX_TRANSFORM_2D)) {
                    continue;
                }
                fx.setFilterIntensity(value);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFilterView.setMoreFilterClickable(true);
    }
}
