package com.meishe.sdkdemo.edit.makeup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.http.bean.CategoryInfo;
import com.meishe.modulemakeupcompose.MakeupHelper;
import com.meishe.modulemakeupcompose.MakeupManager;
import com.meishe.modulemakeupcompose.makeup.BeautyData;
import com.meishe.modulemakeupcompose.makeup.BeautyFxArgs;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupCustomModel;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.VideoEditActivity;
import com.meishe.sdkdemo.edit.VideoFragment;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.view.EditMakeUpView;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.NumberUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.view.MakeUpView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2021/11/08.
 * @Description : 美妆
 * @Description : makeup activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyMakeupActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();

    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private ImageView mMakeupFinish;
    private VideoFragment mVideoFragment;

    private NvsTimeline mTimeline;
    private EditMakeUpView mMakeUpView;

    private AlertDialog mMakeUpDialog;
    private Makeup mMakeup;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        getNvAssetManager();
        return R.layout.activity_beauty_makeup;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mMakeupFinish = (ImageView) findViewById(R.id.iv_makeup_finish);
        mMakeUpView = (EditMakeUpView) findViewById(R.id.make_up_view);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.makeup);
        mTitleBar.setBackImageVisible(View.GONE);
        mTitleBar.setTextRightVisible(View.GONE);
    }

    @Override
    protected void initData() {
        if (initTimeline()) {
            return;
        }
        initVideoFragment();

        resetMakeUpData();

        //获取美妆-单妆数据
        ArrayList<MakeupCustomModel> makeupCustomData = MakeupManager.getInstacne().getCustomMakeupDataList(this, true);
        if (makeupCustomData == null) {
            return;
        }
        mMakeUpView.setMakeupCustomData(makeupCustomData);

        mMakeUpView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }
        }, 0);
    }

    private void resetMakeUpData() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        Makeup makeup = MakeupManager.getInstacne().getMakeup();
        int clips = videoTrack.getClipCount();
        if (makeup != null) {
            for (int i = 0; i < clips; i++) {
                NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
                if (videoClip == null) {
                    continue;
                }
                NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(videoClip);
                if (nvsVideoFx == null) {
                    continue;
                }
                VideoEditActivity.addMakeUp(makeup, videoClip, nvsVideoFx);
            }
        }

        Map<String, Makeup> simpleMakeupEffect = MakeupManager.getInstacne().getSimpleMakeupEffect();
        if (simpleMakeupEffect != null && !simpleMakeupEffect.isEmpty()) {
            Set<String> types = simpleMakeupEffect.keySet();
            for (String type : types) {
                Makeup makeupData = simpleMakeupEffect.get(type);
                if (makeupData == null) {
                    continue;
                }

                videoTrack = mTimeline.getVideoTrackByIndex(0);
                clips = videoTrack.getClipCount();
                for (int i = 0; i < clips; i++) {
                    NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
                    if (videoClip == null) {
                        continue;
                    }
                    NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(videoClip);
                    if (nvsVideoFx == null) {
                        continue;
                    }

                    MakeupEffectContent effectContent = makeupData.getEffectContent();
                    if (effectContent==null){
                        continue;
                    }

                    List<MakeupArgs> makeupArgs = effectContent.getMakeupArgs();
                    if (makeupArgs != null) {
                        for (MakeupArgs args : makeupArgs) {
                            if (args == null) {
                                continue;
                            }
                            if (nvsVideoFx != null) {
                                nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                                nvsVideoFx.setColorVal("Makeup " + args.getType() + " Color", makeupData.getNvsColor());
                                nvsVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
                                nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
                                Log.d("=====", "className:" + args.getClassName() + " value:" + args.getUuid());
                            }
                        }
                    }
                    Log.d("=====", "end set makeUp ");
                }

            }
        }

    }


    @Override
    protected void initListener() {
        mMakeupFinish.setOnClickListener(this);

        mMakeUpView.setOnMakeUpEventListener(new EditMakeUpView.MakeUpEventListener() {
            @Override
            public void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup) {
                onMakeupDataChanged(tabPosition, position, isClearMakeup);
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }

            @Override
            public void onMakeupColorChanged(String makeupId, NvsColor color) {
                makeupColorChanged(makeupId, color);
                if (mMakeup != null) {
                    String type = mMakeup.getType();
                    if (!TextUtils.isEmpty(type) && type.equals(makeupId)) {
                        mMakeup.setNvsColor(color);
                    }
                }
                MakeupManager.getInstacne().putMakeupArgs(makeupId, color);
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }

            @Override
            public void onMakeupIntensityChanged(String makeupId, float intensity) {
                makeupIntensityChanged(makeupId, intensity);
                if (mMakeup != null) {
                    String type = mMakeup.getType();
                    if (!TextUtils.isEmpty(type) && type.equals(makeupId)) {
                        mMakeup.setIntensity(intensity);
                    }
                }
                MakeupManager.getInstacne().putMakeupArgs(makeupId, intensity);
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }

            @Override
            public void removeVideoFxByName(String name) {
                deleteVideoFxByName(name);
            }

            @Override
            public void onMakeUpViewDismiss() {
            }
        });

    }

    /**
     * 根据名称删除特效
     *
     * @param name
     * @return
     */
    private void deleteVideoFxByName(String name) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
            if (clipByIndex == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(clipByIndex);
            if (nvsVideoFx == null) {
                continue;
            }

            String name1 = nvsVideoFx.getDescription().getName();
            if (name1.equals(name)) {
                int index = nvsVideoFx.getIndex();
                clipByIndex.removeFx(index);
            }
        }
    }


    /**
     * 美妆强度变化
     *
     * @param makeupId
     * @param intensity
     */
    private void makeupIntensityChanged(String makeupId, float intensity) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
            if (clipByIndex == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(clipByIndex);
            if (nvsVideoFx == null) {
                continue;
            }
            nvsVideoFx.setFloatVal("Makeup " + makeupId + " Intensity", intensity);
        }

    }

    /**
     * 颜色变化
     *
     * @param makeupId
     * @param color
     */
    private void makeupColorChanged(String makeupId, NvsColor color) {
        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
            if (clipByIndex == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(clipByIndex);
            if (nvsVideoFx == null) {
                continue;
            }
            nvsVideoFx.setColorVal("Makeup " + makeupId + " Color", color);
        }

    }

    private void onMakeupDataChanged(int tabPosition, int position, boolean isClearMakeup) {
        if (mTimeline == null) {
            return;
        }
        BeautyData selectItem = mMakeUpView.getSelectItem();
        if (selectItem instanceof Makeup) {
            mMakeup = (Makeup) selectItem;
            if (tabPosition > 0) {
                /*同一个类别单状 只存储一个美状*/
                MakeupManager.getInstacne().addSimpleMakeupEffect(mMakeup.getType(), mMakeup);
            } else {
                MakeupManager.getInstacne().setMakeup(mMakeup);
            }
            //单装或者妆容
            //数据结构上删除整妆数据
            MakeupManager.getInstacne().setItem(null);
            NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
            int clipCount = videoTrackByIndex.getClipCount();
            for (int i = 0; i < clipCount; i++) {
                NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(i);
                if (clipByIndex == null) {
                    continue;
                }
                NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(clipByIndex, tabPosition == 0);
                //针对clip上面的特效
                //这里整装和单妆互斥，所以需先移除整妆
                nvsVideoFx.setStringVal("Makeup Compound Package Id", null);
                if (tabPosition == 0) {
                    //妆容清理全部的单妆
                    clearAllCustomMakeup(nvsVideoFx);
                    //清理滤镜
                    resetMakeupFx(clipByIndex, nvsVideoFx);
                }
                if ((position == 0) && (tabPosition != 0)) {
                    //点击无清理之前的单装
                    resetCustomMakeup(nvsVideoFx, mMakeUpView.getSelectMakeupId());
                }

                if (tabPosition==0){
                    String makeupEffectDirPath = mMakeup.getFolderPath() + File.separator + mMakeup.getUuid();
                    File file=new File(makeupEffectDirPath);
                    try {
                        MakeupHelper.getInstance().applyMakeupEffect(mContext,clipByIndex,nvsVideoFx,file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    MakeupEffectContent makeupEffectContent = mMakeup.getEffectContent();
//                    if (makeupEffectContent == null) {
//                        return;
//                    }
//                    //妆容
//                    Log.d("=====", "start set makeUp ");
//                    //添加效果包中带的美颜
//                    setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getBeauty(), false);
//                    //添加效果包中带的美型
//                    setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getShape(), false);
//                    //添加效果包中带的微整形
//                    setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getMicroShape(), true);
//                    //添加效果包中带的滤镜
//                    List<FilterArgs> filter = makeupEffectContent.getFilter();
//                    setFilterContent(clipByIndex, filter);
//                    //添加美妆
//                    List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
//                    if (makeupArgs != null) {
//                        for (MakeupArgs args : makeupArgs) {
//                            if (args == null) {
//                                continue;
//                            }
//                            if (tabPosition == 0) {
//                                MakeupData makeupData = new MakeupData(-1, MakeUpView.DEFAULT_MAKEUP_INTENSITY, new ColorData());
//                                makeupData.setUuid(args.getUuid());
//                                MakeupManager.getInstacne().addMakeupEffect(args.getType(), makeupData);
//                            }
//                            if (nvsVideoFx != null) {
//                                nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
//                                nvsVideoFx.setColorVal("Makeup " + args.getType() + " Color", new NvsColor(0, 0, 0, 0));
//                                nvsVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
//                                nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
//                                Log.d("=====", "className:" + args.getClassName() + " value:" + args.getUuid());
//                            }
//                        }
//                    }
                    Log.d("=====", "end set makeUp ");
                }else {
                    //单状
                    MakeupEffectContent makeupEffectContent = mMakeup.getEffectContent();
                    if (makeupEffectContent == null) {
                        return;
                    }
                    List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
                    if (makeupArgs != null) {
                        for (MakeupArgs args : makeupArgs) {
                            if (args == null) {
                                continue;
                            }
                            if (nvsVideoFx != null) {
                                nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                                nvsVideoFx.setColorVal("Makeup " + args.getType() + " Color", new NvsColor(0, 0, 0, 0));
                                nvsVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
                                nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
                                Log.d("=====", "className:" + args.getClassName() + " value:" + args.getUuid());
                            }
                        }
                    }
                }
            }
        }
    }

    private void setFilterContent(NvsVideoClip videoClip, List<FilterArgs> filter) {
        if (videoClip == null || filter == null) {
            return;
        }
//        removeAllFilterFx(nvsVideoFx);
        for (FilterArgs filterArgs : filter) {
            if (filterArgs == null) {
                continue;
            }
            String packageId = filterArgs.getUuid();
            MakeupManager.getInstacne().putFilterFx(packageId);
            NvsVideoFx nvsCaptureVideoFx;
            if (filterArgs.getIsBuiltIn() == 1) {
                nvsCaptureVideoFx = videoClip.appendBuiltinFx(packageId);
            } else {
                nvsCaptureVideoFx = videoClip.appendPackagedFx(packageId);
            }
            if (nvsCaptureVideoFx != null) {
                nvsCaptureVideoFx.setFilterIntensity(NumberUtils.parseString2Float(filterArgs.getValue()));
                Log.d(TAG, "videoClip.setFilterContent id:" + packageId + " value:" + filterArgs.getValue());
            }
            MakeupManager.getInstacne().putFilterFx(packageId);
        }
    }


    private void setMakeupBeautyArgs(NvsVideoFx nvsVideoFx, List<BeautyFxArgs> shape, boolean microFlag) {
        if (nvsVideoFx == null) {
            return;
        }
        if ((shape != null) && (shape.size() > 0)) {
            for (BeautyFxArgs beautyFxArgs : shape) {
                if (beautyFxArgs == null) {
                    continue;
                }
                String className = beautyFxArgs.getClassName();
                String value = beautyFxArgs.getValue();
                Float floatValue = NumberUtils.parseString2Float(value);
                //Default Beauty Enabled   默认美颜Lut开启（美颜）
                //Default Sharpen Enabled
                if (TextUtils.equals(className, "Default Beauty Enabled") || TextUtils.equals(className, "Default Sharpen Enabled")) {
                    if ("1".equals(value)) {
                        nvsVideoFx.setBooleanVal(className, true);
                    } else {
                        nvsVideoFx.setBooleanVal(className, false);
                    }
                    Log.d("=====setMakeup", beautyFxArgs.getClassName() + " setBooleanVal :" + ("1".equals(value)));
                } else {
                    //json 判断是美白A还是美白B
                    if (TextUtils.equals(className, "Beauty Whitening")) {
                        changeBeautyWhiteMode(nvsVideoFx, beautyFxArgs.getWhiteningLutEnabled() <= 0, false);
                    }
                    if (nvsVideoFx != null) {
                        if (!TextUtils.isEmpty(beautyFxArgs.getDegreeName())) {
                            nvsVideoFx.setStringVal(beautyFxArgs.getClassName(), beautyFxArgs.getUuid());
                            nvsVideoFx.setFloatVal(beautyFxArgs.getDegreeName(), floatValue);
//                            MakeupManager.getInstacne().putMapFx(beautyFxArgs.getDegreeName(), value);
                            Log.d("=====setMakeup|||", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid() + " |" + beautyFxArgs.getDegreeName() + " :" + floatValue);
                        } else {
                            if (microFlag && !TextUtils.isEmpty(beautyFxArgs.getUuid())) {
                                nvsVideoFx.setStringVal(className, beautyFxArgs.getUuid());
                                Log.d("=====setMakeup", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid());
                            } else {
                                if (TextUtils.isEmpty(className)) {
                                    nvsVideoFx.setBooleanVal("Advanced Beauty Enable", beautyFxArgs.getAdvancedBeautyEnable() == 1);
                                    nvsVideoFx.setIntVal("Advanced Beauty Type", beautyFxArgs.getAdvancedBeautyType());
                                    Log.d("=====setMakeup", "Advanced Beauty Enable:" + beautyFxArgs.getAdvancedBeautyEnable() + " :Advanced Beauty Type " + beautyFxArgs.getAdvancedBeautyType());
                                } else {
                                    nvsVideoFx.setFloatVal(className, floatValue);
                                    Log.d("=====setMakeup", beautyFxArgs.getClassName() + " :" + floatValue);
                                }
                            }
                        }
                    }
                }
                MakeupManager.getInstacne().putMapFx(className, value);
            }
        }
    }

//    /**
//     * 保存数据结构
//     *
//     * @param shape
//     */
//    private void setMakeupBeautyArgs(List<BeautyFxArgs> shape) {
//        if ((shape != null) && (shape.size() > 0)) {
//            for (BeautyFxArgs beautyFxArgs : shape) {
//                String fxName = beautyFxArgs.getFxName();
//                String value = beautyFxArgs.getValue();
//                MakeupManager.getInstacne().putMapFx(fxName, value);
//            }
//        }
//    }


    /**
     * 清除美妆中添加的美颜 美型 滤镜特效
     *
     * @param videoClip
     * @param videoFx
     */
    private void resetMakeupFx(NvsVideoClip videoClip, NvsVideoFx videoFx) {
        //滤镜
        Set<String> fxSet = MakeupManager.getInstacne().getFilterFxSet();
        if (fxSet != null) {
            for (String fxName : fxSet) {
                int captureVideoFxCount = videoClip.getFxCount();
                for (int videoFxCount = captureVideoFxCount - 1; videoFxCount >= 0; videoFxCount--) {
                    NvsVideoFx nvsVideoFx = videoClip.getFxByIndex(videoFxCount);
                    if (nvsVideoFx == null) {
                        continue;
                    }
                    String captureVideoFxPackageId = nvsVideoFx.getVideoFxPackageId();
                    if (fxName.equals(captureVideoFxPackageId)) {
                        videoClip.removeFx(videoFxCount);
                    }
                }
            }
        }
//        //美妆里边的美颜美型
//        HashMap<String, String> mapFxMap = MakeupManager.getInstacne().getMapFxMap();
//        if (mapFxMap != null) {
//            Set<String> strings = mapFxMap.keySet();
//            for (String fxName : strings) {
//                if (TextUtils.equals(fxName, "Default Beauty Enabled") || TextUtils.equals(fxName, "Default Sharpen Enabled")) {
//                    videoFx.setBooleanVal(fxName, false);
//                } else {
//                    //json 判断是美白A还是美白B
//                    if (TextUtils.equals(fxName, "Beauty Whitening A") || TextUtils.equals(fxName, "Beauty Whitening B")) {
//                        fxName = "Beauty Whitening";
//                    }
//                    if (videoFx != null) {
//                        videoFx.setFloatVal(fxName, 0);
//                    }
//                }
//            }
//        }
        MakeupManager.getInstacne().clearFilterData();
        MakeupManager.getInstacne().clearMapFxData();
    }


    /**
     * 切换美白
     *
     * @param videoEffect 特效
     * @param isOpen      true 美白A false 美白B
     */
//    private void changeBeautyWhiteMode(NvsVideoFx videoEffect, boolean isOpen) {
//        if (isOpen) {
//            videoEffect.setStringVal("Default Beauty Lut File", "");
//            videoEffect.setStringVal("Whitening Lut File", "");
//            videoEffect.setBooleanVal("Whitening Lut Enabled", false);
//        } else {
//            videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
//            videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
//            videoEffect.setBooleanVal("Whitening Lut Enabled", true);
//            ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningB), "#CCFFFFFF", R.color.colorTranslucent);
//        }
//    }
    private void changeBeautyWhiteMode(NvsVideoFx videoEffect, boolean isOpen,
                                       boolean isExchange) {
        if (videoEffect == null) {
            return;
        }
        if (isExchange) {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
                ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningA), "#CCFFFFFF", R.color.colorTranslucent);
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
            }
        } else {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
            }
        }
    }

    /**
     * 删除单装和妆容
     */
    private void clearAllCustomMakeup(NvsVideoFx videoFx) {
        ArrayList<CategoryInfo> mAllMakeupId = mMakeUpView.getAllMakeupId();
        for (CategoryInfo categoryInfo : mAllMakeupId) {
            if (categoryInfo.getMaterialType() == 21) {
                continue;
            }
            resetCustomMakeup(videoFx, Util.upperCaseName(categoryInfo.getDisplayName()));
        }
        MakeupManager.getInstacne().clearCustomData();
    }


    public void resetCustomMakeup(NvsVideoFx videoFx, String makupId) {
        if ((videoFx == null) || TextUtils.isEmpty(makupId)) {
            return;
        }
        videoFx.setStringVal("Makeup " + makupId + " Package Id", null);
        videoFx.setColorVal("Makeup " + makupId + " Color", new NvsColor(0, 0, 0, 0));
        videoFx.setFloatVal("Makeup " + makupId + " Intensity", MakeUpView.DEFAULT_MAKEUP_INTENSITY);
    }


    private boolean removeFilterFxById(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getCaptureVideoFxPackageId();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_makeup_finish:
                makeupFinish();
                break;
            default:
                break;
        }
    }

    private boolean initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return true;
        }
        return false;
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", true);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }


    private void showCaptureDialogView(AlertDialog dialog, MakeUpView view) {
        showCaptureDialogView(dialog, view, false);
    }


    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(Dialog dialog, View view, boolean matchParent) {
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        /*
         * 动画时间500毫秒
         *The animation time is 500 ms
         * */
        translate.setDuration(200);
        translate.setFillAfter(false);
        //mStartLayout.startAnimation(translate);
        dialog.show();
        if (view != null) {
            dialog.setContentView(view);
        }
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (matchParent) {
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
        //  isShowCaptureButton(false);
    }


    private void makeupFinish() {
        mVideoFragment.stopEngine();
        removeTimeline();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }


    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }


}