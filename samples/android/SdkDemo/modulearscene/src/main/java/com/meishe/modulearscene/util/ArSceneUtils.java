package com.meishe.modulearscene.util;

import android.content.Context;
import android.text.TextUtils;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.modulearscene.bean.AdJustBean;
import com.meishe.modulearscene.bean.ArBean;
import com.meishe.modulearscene.inter.IArHelper;

import java.util.List;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class ArSceneUtils {
    private static final String AR_SCENE = "AR Scene";

    private IArHelper helper;
    private StringBuilder adjustPackageId;

    private ArSceneUtils() {
        helper = new ArHelper();
        initDefaultVideoFx();
    }

    public static ArSceneUtils getInstance() {
        return ArManagerHolder.instance;
    }

    public List<ArBean> getSkinData(Context mContext) {
        return helper.getBeautySkinData(mContext);
    }

    public static class ArManagerHolder {
        private static ArSceneUtils instance = new ArSceneUtils();
    }

    public List<ArBean> getBeautyData(Context context) {
        return helper.getBeautyData(context);
    }

    public List<ArBean> getShapeData(Context context) {
        return helper.getShapeData(context);
    }

    public List<ArBean> getMicroShapeData(Context context) {
        return helper.getMicroShapeData(context);
    }

    /**
     * 应用于视频预览
     *
     * @param streamingContext
     * @param arBean
     */
    public void applyData(NvsStreamingContext streamingContext, ArBean arBean) {
        if (streamingContext == null || arBean == null) {
            return;
        }
        NvsCaptureVideoFx applyFx = null;
        for (int i = 0; i < streamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx videoFx = streamingContext.getCaptureVideoFxByIndex(i);
            if (arBean instanceof AdJustBean) {
                if (TextUtils.equals(adjustPackageId.toString(), videoFx.getCaptureVideoFxPackageId())) {
                    applyFx = videoFx;
                }
            } else if (videoFx != null && TextUtils.equals(videoFx.getBuiltinCaptureVideoFxName(), AR_SCENE)) {
                applyFx = videoFx;
            }
        }
        if (arBean instanceof AdJustBean) {
            if (applyFx == null) {
                applyFx = streamingContext.appendPackagedCaptureVideoFx(adjustPackageId.toString());
            }
        } else {
            if (applyFx == null) {
                applyFx = streamingContext.appendBuiltinCaptureVideoFx(AR_SCENE);
                initBeautyAndShapeData(applyFx, true);
            }
        }
        helper.applyData(applyFx, arBean);
    }

    /**
     * 应用于VideClip
     *
     * @param videoClip
     * @param arBean
     */
    public void applyData(NvsVideoClip videoClip, ArBean arBean) {
        if (videoClip != null) {
            if (arBean instanceof AdJustBean) {
                helper.applyData(findOrCreateAdjustColorFx(videoClip), arBean);
            } else {
                helper.applyData(findOrCreateArSceneByVideoClip(videoClip), arBean);
            }
        }
    }

    /**
     * 应用于 timeline
     *
     * @param timeline
     * @param arBean
     */
    public void applyData(NvsTimeline timeline, ArBean arBean) {
        if (timeline != null) {
            int i = 0;
            NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(i);
            while (videoTrack != null) {
                for (int j = 0; j < videoTrack.getClipCount(); j++) {
                    applyData(videoTrack.getClipByIndex(j), arBean);
                }
                i++;
                videoTrack = timeline.getVideoTrackByIndex(i);
            }
        }
    }


    private NvsVideoFx findOrCreateArSceneByVideoClip(NvsVideoClip videoClip) {
        if (videoClip == null) {
            return null;
        }
        for (int i = 0; i < videoClip.getFxCount(); i++) {
            NvsVideoFx fxByIndex = videoClip.getFxByIndex(i);
            if (TextUtils.equals(fxByIndex.getBuiltinVideoFxName(), AR_SCENE)) {
                return fxByIndex;
            }
        }
        NvsVideoFx videoFx = videoClip.appendBuiltinFx(AR_SCENE);
        initBeautyAndShapeData(videoFx, true);
        return videoFx;
    }

    private NvsVideoFx findOrCreateAdjustColorFx(NvsVideoClip videoClip) {
        if (videoClip == null) {
            return null;
        }
        for (int i = 0; i < videoClip.getFxCount(); i++) {
            if (TextUtils.equals(videoClip.getFxByIndex(i).getVideoFxPackageId(), adjustPackageId.toString())) {
                return videoClip.getFxByIndex(i);
            }
        }
        return videoClip.appendPackagedFx(adjustPackageId.toString());
    }

    /**
     * 初始化美颜 美型特效对象
     * Initialize the beauty effect object
     */
    private void initBeautyAndShapeData(NvsFx mArSceneFaceEffect, boolean isOpenArSceneEffect) {
        initBeautyAndShapeData(mArSceneFaceEffect, isOpenArSceneEffect, true, true);
    }

    /**
     * 初始化美颜 美型特效对象
     * Initialize the beauty effect object
     */
    private void initBeautyAndShapeData(NvsFx mArSceneFaceEffect, boolean isOpenArSceneEffect, boolean ar240Flag, boolean singleBufferMode) {
        if (mArSceneFaceEffect != null) {
            if (ar240Flag) {
                mArSceneFaceEffect.setBooleanVal("Use Face Extra Info", true);
            }
            //支持的人脸个数，是否需要使用最小的设置
            mArSceneFaceEffect.setBooleanVal("Max Faces Respect Min", true);
            //美颜开关
            mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
            //美型开关
            mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
            //美型开关
            mArSceneFaceEffect.setBooleanVal("Face Mesh Internal Enabled", true);
//            //高级美颜开关
            mArSceneFaceEffect.setBooleanVal("Advanced Beauty Enable", true);
//            //高级磨皮的强度设置值
            mArSceneFaceEffect.setFloatVal("Advanced Beauty Intensity", 0);
//            //设置缓存
            mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", singleBufferMode);
            if (mArSceneFaceEffect.getARSceneManipulate() != null) {
                mArSceneFaceEffect.getARSceneManipulate().setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
            }
        }

    }

    /**
     * 默认需要添加的特效
     */
    private void initDefaultVideoFx() {
        adjustPackageId = new StringBuilder();
        //初始化校色
        String adjustFxPath = "assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx";
        NvsStreamingContext.getInstance().getAssetPackageManager().installAssetPackage(adjustFxPath, null,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, adjustPackageId);
    }
}
