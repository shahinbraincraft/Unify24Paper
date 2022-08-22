package com.meishe.sdkdemo.utils;

import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;
import static com.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARK_DYNAMICS_FXNAME;
import static com.meishe.sdkdemo.utils.Constants.ROTATION_Z;
import static com.meishe.sdkdemo.utils.Constants.SCALE_X;
import static com.meishe.sdkdemo.utils.Constants.SCALE_Y;
import static com.meishe.sdkdemo.utils.Constants.TRACK_OPACITY;
import static com.meishe.sdkdemo.utils.Constants.TRANS_X;
import static com.meishe.sdkdemo.utils.Constants.TRANS_Y;
import static com.meishe.sdkdemo.utils.dataInfo.CaptionInfo.ATTRIBUTE_USED_FLAG;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAnimatedSticker;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioFx;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsClipAnimatedSticker;
import com.meicam.sdk.NvsClipCaption;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsControlPointPair;
import com.meicam.sdk.NvsMaskRegionInfo;
import com.meicam.sdk.NvsPointD;
import com.meicam.sdk.NvsPosition2D;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.meishe.base.constants.NvsConstants;
import com.meishe.base.utils.LogUtils;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.CutData;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.edit.data.ChangeSpeedCurveInfo;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.data.mask.MaskInfoData;
import com.meishe.sdkdemo.edit.watermark.WaterMarkData;
import com.meishe.sdkdemo.edit.watermark.WaterMarkUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.AnimationInfo;
import com.meishe.sdkdemo.utils.dataInfo.BackGroundInfo;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.RecordAudioInfo;
import com.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.meishe.sdkdemo.utils.dataInfo.StoryboardInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.utils.dataInfo.VolumeKeyInfo;
import com.meishe.utils.ColorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :timelineUtil
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class TimelineUtil {
    private static String TAG = "TimelineUtil";
    public final static long TIME_BASE = 1000000;

    /**
     * 统一管理初始化，便于统一添加flag
     * Unified management initialization facilitates unified flag addition
     */
    public static NvsStreamingContext initStreamingContext(Context mContext, String licensePath) {
        return NvsStreamingContext.init(mContext, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT
                | NvsStreamingContext.STREAMING_CONTEXT_FLAG_ENABLE_HDR_DISPLAY_WHEN_SUPPORTED
                | NvsStreamingContext.STREAMING_CONTEXT_FLAG_INTERRUPT_STOP_FOR_INTERNAL_STOP);
    }

    public static NvsEffectSdkContext initEffectSdkContext(Context mContext, String licensePath) {
        // 初始化 effect sdk ，用到的时候在初始化，没用到没有必要初始化.
        return NvsEffectSdkContext.init(mContext, licensePath, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE
                | NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
    }

    /*
     * 主编辑页面时间线API
     * Main edit page timeline API
     * */
    public static NvsTimeline createTimeline() {
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if (!buildVideoTrack(timeline)) {
            return timeline;
        }

        /*
         * 音乐轨道
         * Music track
         * */
        timeline.appendAudioTrack();
        /*
         * 录音轨道
         * Recording track
         * */
        timeline.appendAudioTrack();

//        /**
//         * 用于贝塞尔曲线的音频轨道
//         */
//        NvsAudioTrack audioTrack = timeline.appendAudioTrack();
//        if (audioTrack != null) {
//            audioTrack.setAttachment(Constants.ATTACHMENT_AUDIO_CURVE, true);
//        }

        setTimelineData(timeline);

        return timeline;
    }


    /*
     * 片段编辑页面时间线API
     * Clip Edit Page Timeline API
     * */
    public static NvsTimeline createSingleClipTimeline(ClipInfo clipInfo, boolean isTrimClip) {
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrack(timeline, clipInfo, isTrimClip);
        return timeline;
    }

    /*
     * 片段编辑页面时间线扩展API
     * Clip Edit Page Timeline Extension API
     * */
    public static NvsTimeline createSingleClipTimelineExt(NvsVideoResolution videoEditRes, String filePath) {
        NvsTimeline timeline = newTimeline(videoEditRes);
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrackExt(timeline, filePath);
        return timeline;
    }

    public static boolean buildSingleClipVideoTrack(NvsTimeline timeline, ClipInfo clipInfo, boolean isTrimClip) {
        if (timeline == null || clipInfo == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        NvsVideoResolution videoRes = timeline.getVideoRes();
        addVideoClip(videoTrack, clipInfo, isTrimClip, videoRes);
        return true;
    }

    public static boolean buildSingleClipVideoTrackExt(NvsTimeline timeline, String filePath) {
        if (timeline == null || filePath == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return false;
        }
        videoClip.changeTrimOutPoint(8000000, true);
        return true;
    }

    public static void setTimelineData(NvsTimeline timeline) {
        if (timeline == null) {
            return;
        }
        /*
         * 此处注意是clone一份音乐数据，因为添加主题的接口会把音乐数据删掉
         * Note here is to clone a piece of music data, because the interface of adding a theme will delete the music data
         * */
        List<MusicInfo> musicInfoClone = TimelineData.instance().cloneMusicData();
        String themeId = TimelineData.instance().getThemeData();

        NvsVideoTrack videoTrackByIndex = timeline.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();

        applyTheme(timeline, themeId);

        int afclipCount = videoTrackByIndex.getClipCount();
        long themeClipDuration = 0;
        if (afclipCount > clipCount) {
            //存在片头主题
            NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(0);
            themeClipDuration = clipByIndex.getOutPoint() - clipByIndex.getInPoint();
        }


        if (musicInfoClone != null) {
            TimelineData.instance().setMusicList(musicInfoClone);
            buildTimelineMusic(timeline, musicInfoClone);
        }

        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        buildTimelineFilter(timeline, videoClipFxData);
        ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();
        setTransition(timeline, transitionInfoArray);
        ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
        setSticker(timeline, stickerArray);

        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        setCaption(timeline, captionArray, themeClipDuration);

        //compound caption
        ArrayList<CompoundCaptionInfo> compoundCaptionArray = TimelineData.instance().getCompoundCaptionArray();
        setCompoundCaption(timeline, compoundCaptionArray);

        ArrayList<RecordAudioInfo> recordArray = TimelineData.instance().getRecordAudioData();
        buildTimelineRecordAudio(timeline, recordArray);

        WaterMarkData waterMarkData = TimelineData.instance().getWaterMarkData();
        WaterMarkUtil.setWaterMark(timeline, waterMarkData);
        //设置动画 build animation
//        Map<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        buildTimelineAnimation(timeline, TimelineData.instance().getClipInfoData());

        //设置调节数据的效果 set adjust
        buildColorAdjustInfo(timeline, TimelineData.instance().getClipInfoData());

        //设置调整区域数据 set cropData
//        buildAdjustCutInfo(timeline, TimelineData.instance().getClipInfoData());

        //设置蒙版数据 set maskInfo
//        buildMaskClipInfo(timeline, TimelineData.instance().getClipInfoData());
    }

    /**
     * 处理蒙版数据
     *
     * @param timeline
     * @param clipInfoData
     */
    public static void buildTimelineMaskClipInfo(NvsTimeline timeline, List<ClipInfo> clipInfoData) {
        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null || clipInfoData == null || clipInfoData.size() == 0) {
            return;
        }
        int clipCount = videoTrack.getClipCount();
        for (int i = 0; i < clipCount; i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if (clip != null) {
                String filePath = clip.getFilePath();
                if (i >= 0 && i < clipInfoData.size()) {
                    applyMask(clip, clipInfoData.get(i).getMaskInfoData());
                }
            }
        }

    }

    private static void appendFilterFx(NvsVideoClip clip, VideoClipFxInfo videoClipFxData) {
        if (videoClipFxData == null) {
            return;
        }
        String name = videoClipFxData.getFxId();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        int mode = videoClipFxData.getFxMode();
        float fxIntensity = videoClipFxData.getFxIntensity();
        // 滤镜关键帧强度
        HashMap<Long, Double> keyFrameInfoMap = videoClipFxData.getKeyFrameInfoMap();
        if (mode == FilterItem.FILTERMODE_BUILTIN) { //内建特效
            NvsVideoFx builtInFx;
            if (videoClipFxData.getIsCartoon()) {
                builtInFx = clip.appendBuiltinFx("Cartoon");
                if (builtInFx != null) {
                    builtInFx.setBooleanVal("Stroke Only", videoClipFxData.getStrokenOnly());
                    builtInFx.setBooleanVal("Grayscale", videoClipFxData.getGrayScale());
                } else {
                    Logger.e(TAG, "Failed to append builtInFx-->" + "Cartoon");
                }
            } else {
                builtInFx = clip.appendBuiltinFx(name);
            }
            if (builtInFx != null) {
                builtInFx.setFilterIntensity(fxIntensity);
            } else {
                Logger.e(TAG, "Failed to append builtInFx-->" + name);
            }
            if (keyFrameInfoMap != null) {
                Set<Map.Entry<Long, Double>> entries = keyFrameInfoMap.entrySet();
                for (Map.Entry<Long, Double> entry : entries) {
                    if (builtInFx != null) {
                        builtInFx.setFloatValAtTime("Filter Intensity", entry.getValue(), entry.getKey());
                    } else {
                        Logger.e(TAG, "the fx is null when set keyFrameValue");
                    }
                }
            }
        } else {
            /*
             * 添加包裹特效
             * Add package effects
             * */
            NvsVideoFx packagedFx = clip.appendPackagedFx(name);
            if (packagedFx != null) {
                packagedFx.setFilterIntensity(fxIntensity);
            } else {
                Logger.e(TAG, "Failed to append packagedFx-->" + name);
            }
            if (keyFrameInfoMap != null) {
                Set<Map.Entry<Long, Double>> entries = keyFrameInfoMap.entrySet();
                for (Map.Entry<Long, Double> entry : entries) {
                    if (packagedFx != null) {
                        packagedFx.setFloatValAtTime("Filter Intensity", entry.getValue(), entry.getKey());
                    } else {
                        Logger.e(TAG, "the fx is null when set keyFrameValue");
                    }
                }
            }
        }
    }

    public static boolean removeTimeline(NvsTimeline timeline) {
        if (timeline == null){
            return false;
        }

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null){
            return false;
        }

        return context.removeTimeline(timeline);
    }

    public static boolean buildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }

        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0; i < videoClipArray.size(); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true, timeline.getVideoRes());
        }
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    public static boolean reBuildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        int videoTrackCount = timeline.videoTrackCount();
        NvsVideoTrack videoTrack = videoTrackCount == 0 ? timeline.appendVideoTrack() : timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        videoTrack.removeAllClips();
        timeline.removeCurrentTheme();
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0; i < videoClipArray.size(); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true, timeline.getVideoRes());
        }
        setTimelineData(timeline);
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    private static void addVideoClip(NvsVideoTrack videoTrack, ClipInfo clipInfo, boolean isTrimClip, NvsVideoResolution videoRes) {
        if (videoTrack == null || clipInfo == null)
            return;
        String filePath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(),
                MSApplication.CONTENT_FLAG, clipInfo.getFilePath());
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return;
        }
        boolean blurFlag = ParameterSettingValues.instance().isUseBackgroudBlur();
        if (blurFlag) {
            videoClip.setSourceBackgroundMode(NvsVideoClip.ClIP_BACKGROUNDMODE_BLUR);
        }

        //获取亮度 对比度 饱和度 高光 阴影 褪色
        float brightVal = clipInfo.getBrightnessVal();
        float contrastVal = clipInfo.getContrastVal();
        float saturationVal = clipInfo.getSaturationVal();
        float highLight = clipInfo.getmHighLight();
        float shadow = clipInfo.getmShadow();
        float fade = clipInfo.getFade();
        //暗角
        float vignette = clipInfo.getVignetteVal();
        //获取锐度
        float sharpen = clipInfo.getSharpenVal();
        //获取色温 色调
        float temperature = clipInfo.getTemperature();
        float tint = clipInfo.getTint();
        //噪点
        float density = clipInfo.getDensity();
        float denoiseDensity = clipInfo.getDenoiseDensity();
        //背景
        TimelineUtil.applyBackground(videoClip, clipInfo.getBackGroundInfo());
        NvsVideoFx mColorVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        mColorVideoFx.setAttachment(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST, Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
        mColorVideoFx.setFloatVal(Constants.ADJUST_BRIGHTNESS, brightVal);
        mColorVideoFx.setFloatVal(Constants.ADJUST_CONTRAST, contrastVal);
        mColorVideoFx.setFloatVal(Constants.ADJUST_SATURATION, saturationVal);
        mColorVideoFx.setFloatVal(Constants.ADJUST_HIGHTLIGHT, highLight);
        mColorVideoFx.setFloatVal(Constants.ADJUST_SHADOW, shadow);
        mColorVideoFx.setFloatVal(Constants.ADJUST_FADE, fade);

        NvsVideoFx mVignetteVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_VIGETTE);
        mVignetteVideoFx.setAttachment(Constants.ADJUST_TYPE_VIGETTE, Constants.ADJUST_TYPE_VIGETTE);
        mVignetteVideoFx.setFloatVal(Constants.ADJUST_DEGREE, vignette);

        NvsVideoFx mSharpenVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_SHARPEN);
        mSharpenVideoFx.setAttachment(Constants.ADJUST_TYPE_SHARPEN, Constants.ADJUST_TYPE_SHARPEN);
        mSharpenVideoFx.setFloatVal(Constants.ADJUST_AMOUNT, sharpen);

        NvsVideoFx mTintVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_TINT);
        mTintVideoFx.setAttachment(Constants.ADJUST_TYPE_TINT, Constants.ADJUST_TYPE_TINT);
        mTintVideoFx.setFloatVal(Constants.ADJUST_TEMPERATURE, temperature);
        mTintVideoFx.setFloatVal(Constants.ADJUST_TINT, tint);


        NvsVideoFx mDenoiseVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_DENOISE);
        mDenoiseVideoFx.setAttachment(Constants.ADJUST_TYPE_DENOISE, Constants.ADJUST_TYPE_DENOISE);
        mDenoiseVideoFx.setFloatVal(Constants.ADJUST_DENOISE, density);
        mDenoiseVideoFx.setFloatVal(Constants.ADJUST_DENOISE_DENSITY, denoiseDensity);

        int videoType = videoClip.getVideoType();
        if (videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE) {
            /*
             * 当前片段是图片
             * The current clip is a picture
             * */
            long trimIn = videoClip.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
            int imgDisplayMode = clipInfo.getImgDispalyMode();
            if (imgDisplayMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
                /*
                 * 区域显示
                 * Area display
                 * */
                videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                RectF normalStartRectF = clipInfo.getNormalStartROI();
                RectF normalEndRectF = clipInfo.getNormalEndROI();
                if (normalStartRectF != null && normalEndRectF != null) {
                    videoClip.setImageMotionROI(normalStartRectF, normalEndRectF);
                }
            } else {
                /*
                 * 全图显示
                 * Full image display
                 * */
                videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
            }

            boolean isOpenMove = clipInfo.isOpenPhotoMove();
            videoClip.setImageMotionAnimationEnabled(isOpenMove);
        } else {
            /*
             * 当前片段是视频
             * The current clip is a video
             * */
            float volumeGain = clipInfo.getVolume();
            videoClip.setVolumeGain(volumeGain, volumeGain);
            float pan = clipInfo.getPan();
            float scan = clipInfo.getScan();
            videoClip.setPanAndScan(pan, scan);
            float speed = clipInfo.getSpeed();
           /* if (speed > 0) {
                videoClip.changeSpeed(speed);
            }*/
            videoClip.changeSpeed(speed, clipInfo.isKeepAudioPitch());
            //曲线变速
            ChangeSpeedCurveInfo changeSpeedInfo = clipInfo.getmCurveSpeed();
            if (null != changeSpeedInfo) {
                boolean isSucess = videoClip.changeCurvesVariableSpeed(changeSpeedInfo.speed, true);
                Log.e("addVideoClip", "曲线变速设置" + (isSucess ? "成功" : "失败"));
            }
            videoClip.setExtraVideoRotation(clipInfo.getRotateAngle());
            int scaleX = clipInfo.getScaleX();
            int scaleY = clipInfo.getScaleY();
            if (scaleX >= -1 || scaleY >= -1) {
                NvsVideoFx videoFxTransform = videoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                if (videoFxTransform != null) {
                    if (scaleX >= -1){
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, scaleX);
                    }
                    if (scaleY >= -1){
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, scaleY);
                    }
                }
            }

            if (!isTrimClip) {
                /*
                 * 如果当前是裁剪页面，不裁剪片段
                 * If the page is currently cropped, the clip is not cropped
                 * */
                return;
            }
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if (trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
        }
        //蒙版 mask
        applyMask(videoClip, clipInfo.getMaskInfoData());
//        //调整(裁剪) crop
//        applyCropData(videoClip, clipInfo.getCropInfo());
        //动画 animation
        applyAnimation(videoTrack, videoClip, clipInfo.getAnimationInfo());
        applyVolumeKeyInfo(videoClip, clipInfo.getVolumeKeyFrameInfoHashMap());
        applyClipAnimatedSticker(videoClip, clipInfo.getStickerInfoList());
        applyClipCaption(videoClip, clipInfo.getCaptionInfoList(), 0);
    }

    /**
     * 应用贴纸到videoClip
     *
     * @param videoClip
     * @param stickerInfoList
     */
    public static void applyClipAnimatedSticker(NvsVideoClip videoClip, List<StickerInfo> stickerInfoList) {
        if (videoClip == null) {
            return;
        }
        NvsClipAnimatedSticker deleteSticker = videoClip.getFirstAnimatedSticker();
        while (deleteSticker != null) {
            deleteSticker = videoClip.removeAnimatedSticker(deleteSticker);
        }
        if (stickerInfoList == null) {
            return;
        }
        for (StickerInfo sticker : stickerInfoList) {
            long duration = sticker.getOutPoint() - sticker.getInPoint();
            boolean isCutsomSticker = sticker.isCustomSticker();
            NvsClipAnimatedSticker animatedSticker = isCutsomSticker ?
                    videoClip.addCustomAnimatedSticker(sticker.getInPoint(), duration, sticker.getId(), sticker.getCustomImagePath())
                    : videoClip.addAnimatedSticker(sticker.getInPoint(), duration, sticker.getId());
            if (animatedSticker == null) {
                continue;
            }
            applyAnimatedStickerAnimation(animatedSticker, sticker);
            // 判断是否应用关键帧信息
            //Determine whether to apply key frame information
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = sticker.getKeyFrameInfoHashMap();
            if (keyFrameInfoHashMap.isEmpty()) {
                PointF translation = sticker.getTranslation();
                float scaleFactor = sticker.getScaleFactor();
                float rotation = sticker.getRotation();
                animatedSticker.setScale(scaleFactor);
                animatedSticker.setRotationZ(rotation);
                animatedSticker.setTranslation(translation);
            } else {
                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                    animatedSticker.setCurrentKeyFrameTime(entry.getKey() - animatedSticker.getInPoint());
                    KeyFrameInfo keyFrameInfo = entry.getValue();
                    animatedSticker.setRotationZ(keyFrameInfo.getRotationZ());
                    animatedSticker.setScale(keyFrameInfo.getScaleX());
                    animatedSticker.setTranslation(keyFrameInfo.getTranslation());
                }
            }
            float volumeGain = sticker.getVolumeGain();
            animatedSticker.setVolumeGain(volumeGain, volumeGain);
        }
    }

    /**
     * 设置音频关键帧以及添加贝塞尔曲线方法
     *
     * @param videoClip
     * @param volumeKeyInfoMap
     */
    public static void applyVolumeKeyInfo(NvsVideoClip videoClip, Map<Long, VolumeKeyInfo> volumeKeyInfoMap) {
        if (videoClip == null) {
            return;
        }
        NvsAudioFx audioFx = videoClip.getAudioVolumeFx();
        if (audioFx == null) {
            return;
        }
        //全部删除 重新生效
        audioFx.removeAllKeyframe(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN);
        audioFx.removeAllKeyframe(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN);
        if (volumeKeyInfoMap == null || volumeKeyInfoMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<Long, VolumeKeyInfo>> entries = volumeKeyInfoMap.entrySet();
        for (Map.Entry<Long, VolumeKeyInfo> entry : entries) {
            Long keyTime = entry.getKey();
            VolumeKeyInfo value = entry.getValue();
            audioFx.setFloatValAtTime(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN, value.getVolumeValue(), keyTime);
            audioFx.setFloatValAtTime(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_RIGHT_GAIN, value.getVolumeValue(), keyTime);
            if (value.getPreControlPoint() >= 0) {
                NvsPointD forwardPoint = new NvsPointD(value.getPreControlPoint(), value.getPreVolumeValue());
                NvsPointD backPoint = new NvsPointD(keyTime, value.getVolumeValue());
                NvsControlPointPair prePoint = new NvsControlPointPair(forwardPoint, backPoint);
                audioFx.setKeyFrameControlPoint(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN, keyTime, prePoint);
                audioFx.setKeyFrameControlPoint(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN, keyTime, prePoint);
            }
            if (value.getNextControlPoint() >= 0) {
                NvsPointD forwardPoint = new NvsPointD(keyTime, value.getVolumeValue());
                NvsPointD backPoint = new NvsPointD(value.getNextControlPoint(), value.getNextVolumeValue());
                NvsControlPointPair nextPoint = new NvsControlPointPair(forwardPoint, backPoint);
                audioFx.setKeyFrameControlPoint(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_RIGHT_GAIN, keyTime, nextPoint);
                audioFx.setKeyFrameControlPoint(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_RIGHT_GAIN, keyTime, nextPoint);
            }
        }
    }

    /**
     * 获取音频上的关键帧的声音大小
     *
     * @param videoClip
     * @param timeStamp
     * @return
     */
    public static double getVolumeByKeyTime(NvsVideoClip videoClip, long timeStamp) {
        if (videoClip == null || timeStamp < 0) {
            return -1;
        }
        NvsAudioFx audioFx = videoClip.getAudioVolumeFx();
        if (audioFx == null) {
            return -1;
        }
        return audioFx.getFloatValAtTime(NvsConstants.AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN, timeStamp);
    }

    public static boolean buildTimelineFilter(NvsTimeline timeline, VideoClipFxInfo videoClipFxData) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();

        int videoClipCount = videoTrack.getClipCount();
        for (int i = 0; i < videoClipCount; i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if (clip == null) {
                continue;
            }

            String clipFilPath = clip.getFilePath();
            String videoFilePath = "";
            ClipInfo clipInfo = null;
            for (int i1 = 0; i1 < clipInfos.size(); i1++) {
                videoFilePath = clipInfos.get(i1).getFilePath();
                videoFilePath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(), videoFilePath);
                if (videoFilePath.equals(clipFilPath)) {
                    clipInfo = clipInfos.get(i1);
                    break;
                }
            }
//            ClipInfo clipInfo = clipInfos.get(i);
            if (clipInfo == null) {
                continue;
            }

            removeAllVideoFx(clip);
            VideoClipFxInfo clipFxData = null;
            boolean isSrcVideoAsset = false;

            isSrcVideoAsset = false;
            /*
             * 过滤掉主题中自带片头或者片尾的视频
             * Filter out videos that have their own title or trailer in the theme
             * */
            if (MSApplication.CONTENT_FLAG) {
                clipFilPath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(), clipFilPath);
                videoFilePath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(), clipInfo.getFilePath());
            }
            if (clipFilPath.equals(videoFilePath)) {
                isSrcVideoAsset = true;
                clipFxData = clipInfo.getVideoClipFxInfo();
            }

            if (!isSrcVideoAsset) {
                continue;
            }
            /*
             * 添加片段滤镜特效
             * Add clip filter effects
             * */
            appendFilterFx(clip, clipFxData);
            /*
             * 添加TimeLine特效
             * Add TimeLine effects
             * */
            appendFilterFx(clip, videoClipFxData);
        }
        return true;
    }

    /**
     * Add effect for single clip.
     * Besides the clip effect,the effects in TimeLine is added by this method.
     * Remarkably, the method is used for showing effects during changing them and the {@code #clipFxData} is usually temporary.
     * Final building {@link #buildTimelineFilter(NvsTimeline, VideoClipFxInfo)}
     *
     * @param timeline   TimeLine data
     * @param clipInfo   Clip data
     * @param clipFxData Clip effect data.
     * @return
     */
    public static boolean buildSingleClipFilter(NvsTimeline timeline, ClipInfo clipInfo, VideoClipFxInfo clipFxData) {
        if (timeline == null || clipInfo == null) {
            return false;
        }
        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if (clip == null) {
                continue;
            }

            String clipFilPath = clip.getFilePath();
            String videoFilePath = clipInfo.getFilePath();

            if (MSApplication.CONTENT_FLAG) {
                clipFilPath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(), clipFilPath);
                videoFilePath = FileUtils.getAbsPathByContentUri(MSApplication.getContext(), clipInfo.getFilePath());
            }
            if (!clipFilPath.equals(videoFilePath)) {
                continue;
            }
            removeAllVideoFx(clip);

            /*
             * 添加片段滤镜特效
             * Add clip filter effects
             * */
            appendFilterFx(clip, clipFxData);
            // 添加TimeLine特效
            appendFilterFx(clip, TimelineData.instance().getVideoClipFxData());
        }
        return true;
    }

    public static boolean applyTheme(NvsTimeline timeline, String themeId) {
        if (timeline == null) {
            return false;
        }

        timeline.removeCurrentTheme();
        if (themeId == null || themeId.isEmpty()) {
            return false;
        }

        /*
         * 设置主题片头和片尾
         * Set theme title and trailer
         * */
        String themeCaptionTitle = TimelineData.instance().getThemeCptionTitle();
        if (!themeCaptionTitle.isEmpty()) {
            timeline.setThemeTitleCaptionText(themeCaptionTitle);
        }
        String themeCaptionTrailer = TimelineData.instance().getThemeCptionTrailer();
        if (!themeCaptionTrailer.isEmpty()) {
            timeline.setThemeTrailerCaptionText(themeCaptionTrailer);
        }

        if (!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        /*
         * 应用主题之后，要把已经应用的背景音乐去掉
         * After applying the theme, remove the background music that has been applied
         * */
        TimelineData.instance().setMusicList(null);
        TimelineUtil.buildTimelineMusic(timeline, null);
        return true;
    }

    private static boolean removeAllVideoFx(NvsVideoClip videoClip) {
        if (videoClip == null) {
            return false;
        }

        int fxCount = videoClip.getFxCount();
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx fx = videoClip.getFxByIndex(i);
            if (fx == null) {
                continue;
            }

            String name = fx.getBuiltinVideoFxName();
            Log.e("===>", "fx name: " + name);
            if (name.equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)
                    || name.equals(Constants.ADJUST_TYPE_SHARPEN)
                    || name.equals(Constants.ADJUST_TYPE_VIGETTE)
                    || name.equals(Constants.ADJUST_TYPE_TINT)
                    || name.equals(Constants.ADJUST_TYPE_DENOISE)
                    || name.equals(Constants.FX_TRANSFORM_2D)
                    || name.equals(StoryboardInfo.DESC_TYPE)) {
                continue;
            }
            videoClip.removeFx(i);
            i--;
        }
        return true;
    }

    /**
     * 添加全部转场特效
     * <p>
     * Add all transition effects
     *
     * @param timeline
     * @param transitionInfos
     * @return
     */
    public static boolean setTransition(NvsTimeline timeline, ArrayList<TransitionInfo> transitionInfos) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        if (transitionInfos == null) {
            return false;
        }

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }
        /*
         * 添加全部转场特效
         * Add all transition effects
         * */
        for (int i = 0, length = transitionInfos.size(); i < length; i++) {
            TransitionInfo transitionInfo = transitionInfos.get(i);
            NvsVideoTransition nvsVideoTransition = null;
            if (TextUtils.equals(transitionInfo.getTransitionId(), "theme")) {
                nvsVideoTransition = videoTrack.setPackagedTransition(i, transitionInfo.getTransitionId());
            } else {
                if (transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                    nvsVideoTransition = videoTrack.setBuiltinTransition(i, transitionInfo.getTransitionId());
                } else {
                    nvsVideoTransition = videoTrack.setPackagedTransition(i, transitionInfo.getTransitionId());
                }
            }
            if (nvsVideoTransition != null) {
                // FIXME: 2019/10/17 0017 临时修改转场
                if (transitionInfo.ismChangeTransitionDuration()) {

                    nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_STRETCH);
                } else {
                    nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);

                }
                //nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);
                transitionInfo.setVideoTransition(nvsVideoTransition);
//                transitionInfo.setTransitionInterval(nvsVideoTransition.getVideoTransitionDuration( ));
            }
        }

        return true;
    }

    /**
     * 指定索引位置添加转场特效
     * Add transition effects at specified index positions
     *
     * @param timeline
     * @param transitionInfo
     * @param index
     * @return
     */
    public static boolean setTransition(NvsTimeline timeline, TransitionInfo transitionInfo, int index) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        if (transitionInfo == null) {
            return false;
        }

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }
        // FIXME: 2019/10/17 0017 临时修改转场
        NvsVideoTransition nvsVideoTransition = null;
        if (TextUtils.equals(transitionInfo.getTransitionId(), "theme")) {
            videoTrack.setBuiltinTransition(index, "");
//          videoTrack.setPackagedTransition(index, null);
            nvsVideoTransition = videoTrack.setPackagedTransition(index, transitionInfo.getTransitionId());
        } else {
            if (transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                nvsVideoTransition = videoTrack.setBuiltinTransition(index, transitionInfo.getTransitionId());
            } else {
                nvsVideoTransition = videoTrack.setPackagedTransition(index, transitionInfo.getTransitionId());
            }
        }
        if (nvsVideoTransition != null) {
            Log.e("nvsVideoTransition", "pre duration=" + nvsVideoTransition.getVideoTransitionDuration() + " mode=" + nvsVideoTransition.getVideoTransitionDurationMatchMode());
            transitionInfo.setVideoTransition(nvsVideoTransition);
            if (transitionInfo.ismChangeTransitionDuration()) {

                nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_STRETCH);
            } else {
                nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);

            }
            Log.e("nvsVideoTransition", "duration=" + nvsVideoTransition.getVideoTransitionDuration() + " mode=" + nvsVideoTransition.getVideoTransitionDurationMatchMode());
            // FIXME: 2019/10/17 0017 临时修改转场
            // transitionInfo.setTransitionInterval(nvsVideoTransition.getVideoTransitionDuration( ));
        }
        return true;
    }

    public static boolean buildTimelineMusic(NvsTimeline timeline, List<MusicInfo> musicInfos) {
        if (timeline == null) {
            return false;
        }
        NvsAudioTrack audioTrack = timeline.getAudioTrackByIndex(0);
        if (audioTrack == null) {
            return false;
        }
        if (musicInfos == null || musicInfos.isEmpty()) {
            audioTrack.removeAllClips();

            /*
             * 去掉音乐之后，要把已经应用的主题中的音乐还原
             * After removing the music, you need to restore the music in the theme you have applied
             * */
            String pre_theme_id = TimelineData.instance().getThemeData();
            if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
                timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
            return false;
        }
        audioTrack.removeAllClips();
        for (MusicInfo oneMusic : musicInfos) {
            if (oneMusic == null) {
                continue;
            }
            NvsAudioClip audioClip = audioTrack.addClip(oneMusic.getFilePath(), oneMusic.getInPoint(), oneMusic.getTrimIn(), oneMusic.getTrimOut());
            if (audioClip != null) {
                audioClip.setFadeInDuration(oneMusic.getFadeDuration());
                if (oneMusic.getExtraMusic() <= 0 && oneMusic.getExtraMusicLeft() <= 0) {
                    audioClip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
            if (oneMusic.getExtraMusic() > 0) {
                for (int i = 0; i < oneMusic.getExtraMusic(); ++i) {
                    NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                            oneMusic.getOriginalOutPoint() + i * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                            oneMusic.getOriginalTrimIn(), oneMusic.getOriginalTrimOut());
                    if (extra_clip != null) {
                        extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                        if (i == oneMusic.getExtraMusic() - 1 && oneMusic.getExtraMusicLeft() <= 0) {
                            extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                            extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                        }
                    }
                }
            }
            if (oneMusic.getExtraMusicLeft() > 0) {
                NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                        oneMusic.getOriginalOutPoint() + oneMusic.getExtraMusic() * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                        oneMusic.getOriginalTrimIn(),
                        oneMusic.getOriginalTrimIn() + oneMusic.getExtraMusicLeft());
                if (extra_clip != null) {
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
        }
        float audioVolume = TimelineData.instance().getMusicVolume();
        audioTrack.setVolumeGain(audioVolume, audioVolume);

        /*
         * 去掉音乐之后，要把已经应用的主题中的音乐还原
         * After removing the music, you need to restore the music in the theme you have applied
         * */
        String pre_theme_id = TimelineData.instance().getThemeData();
        if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
            timeline.setThemeMusicVolumeGain(0, 0);
        }
        return true;
    }

    public static void buildTimelineRecordAudio(NvsTimeline timeline, ArrayList<RecordAudioInfo> recordAudioInfos) {
        if (timeline == null) {
            return;
        }
        NvsAudioTrack audioTrack = timeline.getAudioTrackByIndex(1);
        if (audioTrack != null) {
            audioTrack.removeAllClips();
            if (recordAudioInfos != null) {
                for (int i = 0; i < recordAudioInfos.size(); ++i) {
                    RecordAudioInfo recordAudioInfo = recordAudioInfos.get(i);
                    if (recordAudioInfo == null) {
                        continue;
                    }
                    NvsAudioClip audioClip = audioTrack.addClip(recordAudioInfo.getPath(), recordAudioInfo.getInPoint(), recordAudioInfo.getTrimIn(),
                            recordAudioInfo.getOutPoint() - recordAudioInfo.getInPoint() + recordAudioInfo.getTrimIn());
                    if (audioClip != null) {
                        audioClip.setVolumeGain(recordAudioInfo.getVolume(), recordAudioInfo.getVolume());
                        if (recordAudioInfo.getFxID() != null && !recordAudioInfo.getFxID().equals(Constants.NO_FX)) {
                            audioClip.appendFx(recordAudioInfo.getFxID());
                        }
                    }
                }
            }
            float audioVolume = TimelineData.instance().getRecordVolume();
            audioTrack.setVolumeGain(audioVolume, audioVolume);
        }
    }

    public static boolean setSticker(NvsTimeline timeline, List<StickerInfo> stickerArray) {
        if (timeline == null) {
            return false;
        }

        NvsTimelineAnimatedSticker deleteSticker = timeline.getFirstAnimatedSticker();
        while (deleteSticker != null) {
            deleteSticker = timeline.removeAnimatedSticker(deleteSticker);
        }

        for (StickerInfo sticker : stickerArray) {
            long duration = sticker.getOutPoint() - sticker.getInPoint();
            boolean isCutsomSticker = sticker.isCustomSticker();
            NvsTimelineAnimatedSticker timelineSticker = isCutsomSticker ?
                    timeline.addCustomAnimatedSticker(sticker.getInPoint(), duration, sticker.getId(), sticker.getCustomImagePath())
                    : timeline.addAnimatedSticker(sticker.getInPoint(), duration, sticker.getId());
            if (timelineSticker == null) {
                continue;
            }
            applyAnimatedStickerAnimation(timelineSticker, sticker);
            // 判断是否应用关键帧信息
            //Determine whether to apply key frame information
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = sticker.getKeyFrameInfoHashMap();
            if (keyFrameInfoHashMap.isEmpty()) {
                PointF translation = sticker.getTranslation();
                float scaleFactor = sticker.getScaleFactor();
                float rotation = sticker.getRotation();
                timelineSticker.setScale(scaleFactor);
                timelineSticker.setRotationZ(rotation);
                timelineSticker.setTranslation(translation);
            } else {
                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                    timelineSticker.setCurrentKeyFrameTime(entry.getKey() - timelineSticker.getInPoint());
                    KeyFrameInfo keyFrameInfo = entry.getValue();
                    timelineSticker.setRotationZ(keyFrameInfo.getRotationZ());
                    timelineSticker.setScale(keyFrameInfo.getScaleX());
                    timelineSticker.setTranslation(keyFrameInfo.getTranslation());
                }
            }
            float volumeGain = sticker.getVolumeGain();
            timelineSticker.setVolumeGain(volumeGain, volumeGain);
        }
        return true;
    }

    /**
     * 贴纸动画生效
     * AnimatedStickerAnimation effect
     *
     * @param animatedSticker
     * @param stickerInfo
     */
    public static void applyAnimatedStickerAnimation(NvsAnimatedSticker animatedSticker, StickerInfo stickerInfo) {
        if (animatedSticker == null || stickerInfo == null) return;
        animatedSticker.setZValue(stickerInfo.getAnimateStickerZVal());
        animatedSticker.setHorizontalFlip(stickerInfo.isHorizFlip());
        //动画 1判断组合 2 判断进出
        if (!TextUtils.isEmpty(stickerInfo.getPeriodAnimationId())) {
            animatedSticker.applyAnimatedStickerPeriodAnimation(stickerInfo.getPeriodAnimationId());
            animatedSticker.setAnimatedStickerAnimationPeriod((int) stickerInfo.getPeriodAnimationDuration());
        } else {
            //进动画
            animatedSticker.applyAnimatedStickerInAnimation(stickerInfo.getInAnimationId());
            animatedSticker.setAnimatedStickerInAnimationDuration((int) stickerInfo.getInAnimationDuration());
            //出动画
            animatedSticker.applyAnimatedStickerOutAnimation(stickerInfo.getOutAnimationId());
            animatedSticker.setAnimatedStickerOutAnimationDuration((int) stickerInfo.getOutAnimationDuration());
        }

    }

    public static boolean setCaption(NvsTimeline timeline, ArrayList<CaptionInfo> captionArray) {
        return setCaption(timeline, captionArray, 0, false);
    }

    public static boolean setCaption(NvsTimeline timeline, ArrayList<CaptionInfo> captionArray, long themeDuration) {
        return setCaption(timeline, captionArray, themeDuration, false);
    }

    public static boolean setCaption(NvsTimeline timeline, ArrayList<CaptionInfo> captionArray, long themeDuration, boolean userRecordingFlag) {
        if (timeline == null) {
            return false;
        }

        NvsTimelineCaption deleteCaption = timeline.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            Logger.e(TAG, "capCategory = " + capCategory);
            int roleTheme = deleteCaption.getRoleInTheme();
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作删除
                deleteCaption = timeline.getNextCaption(deleteCaption);
            } else {
                deleteCaption = timeline.removeCaption(deleteCaption);
            }
        }

        NvsTimelineCaption newCaption;
        for (CaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            if (caption.isTraditionCaption()) {
                //传统字幕
                newCaption = timeline.addCaption(caption.getText(), caption.getInPoint() + themeDuration,
                        duration, null);
            } else {
                //拼装字幕
                newCaption = timeline.addModularCaption(caption.getText(), caption.getInPoint() + themeDuration,
                        duration);
            }
            updateCaptionAttribute(newCaption, caption, userRecordingFlag);
        }
        return true;
    }

    //add compound caption
    public static boolean setCompoundCaption(NvsTimeline timeline, ArrayList<CompoundCaptionInfo> captionArray) {
        if (timeline == null) {
            return false;
        }

        NvsTimelineCompoundCaption deleteCaption = timeline.getFirstCompoundCaption();
        while (deleteCaption != null) {
            deleteCaption = timeline.removeCompoundCaption(deleteCaption);
        }

        for (CompoundCaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            NvsTimelineCompoundCaption newCaption = timeline.addCompoundCaption(caption.getInPoint(),
                    duration, caption.getCaptionStyleUuid());
            updateCompoundCaptionAttribute(newCaption, caption);
        }
        return true;
    }

    //update compound caption attribute
    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, CompoundCaptionInfo caption) {
        if (newCaption == null || caption == null) {
            return;
        }

        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttrList = caption.getCaptionAttributeList();
        int captionCount = newCaption.getCaptionCount();
        for (int index = 0; index < captionCount; ++index) {
            CompoundCaptionInfo.CompoundCaptionAttr captionAttr = captionAttrList.get(index);
            if (captionAttr == null) {
                continue;
            }
            //更新组合字幕item的属性
            //字幕颜色
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionColor());
            if (textColor != null) {
                newCaption.setTextColor(index, textColor);
            }
            //字体
            String fontName = captionAttr.getCaptionFontName();
            if (!TextUtils.isEmpty(fontName)) {
                newCaption.setFontFamily(index, fontName);
            }
            //文字内容
            String captionText = captionAttr.getCaptionText();
            if (!TextUtils.isEmpty(captionText)) {
                newCaption.setText(index, captionText);
            }
            //描边
            if (captionAttr.getM_usedOutlineFlag() == ATTRIBUTE_USED_FLAG) {
                NvsColor strokeColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionStrokeColor());
                int strokeWidth = captionAttr.getCaptionStrokeWidth();
                newCaption.setDrawOutline(true, index);
                newCaption.setOutlineColor(strokeColor, index);
                newCaption.setOutlineWidth(strokeWidth, index);
            } else {
                newCaption.setDrawOutline(false, index);
            }
            //背景色
            if (captionAttr.getM_usedBackgroundFlag() == ATTRIBUTE_USED_FLAG) {
                NvsColor backgroundColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionBackgroundColor());
                newCaption.setBackgroundColor(backgroundColor, index);
            }
        }

        /*
         * 放缩字幕
         * Shrink captions
         * */
        float scaleFactorX = caption.getScaleFactorX();
        float scaleFactorY = caption.getScaleFactorY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);
        float rotation = caption.getRotation();
        /*
         * 旋转字幕
         * Spin subtitles
         * */
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(caption.getCaptionZVal());
        PointF translation = caption.getTranslation();
        if (translation != null) {
            newCaption.setCaptionTranslation(translation);
        }
    }

    private static void updateCaptionAttribute(NvsCaption newCaption, CaptionInfo caption) {
        updateCaptionAttribute(newCaption, caption, false);
    }

    private static void updateCaptionAttribute(NvsCaption newCaption, CaptionInfo caption, boolean userRecordingFlag) {
        if (newCaption == null) {
            return;
        }
        if (caption == null) {
            return;
        }
        newCaption.setOpacity(caption.getOpacity());
        /*
         * 字幕StyleUuid需要首先设置，后面设置的字幕属性才会生效，因为字幕样式里面可能自带偏移，缩放，旋转等属性，最后设置会覆盖前面的设置的。
         *
         * The subtitle StyleUuid needs to be set first, and the subtitle properties set later will take effect,
         * because the subtitle style may have its own offset, zoom, rotation and other properties.
         * The last setting will override the previous settings.
         * */
//        newCaption.setRecordingUserOperation(false);
        if (caption.isTraditionCaption()) {
            //传统字幕 Traditional caption
            String styleUuid = caption.getCaptionStyleUuid();
            newCaption.applyCaptionStyle(styleUuid);
        } else {
            //拼装字幕 Assemble caption
            newCaption.applyModularCaptionRenderer(caption.getRichWordUuid());
            newCaption.applyModularCaptionContext(caption.getBubbleUuid());
            if (!TextUtils.isEmpty(caption.getCombinationAnimationUuid())) {
                //优先使用组合动画，和入场、出场动画互斥
                //Prioritize the use of combined animations, which are mutually exclusive with the entrance and exit animations
                newCaption.applyModularCaptionAnimation(caption.getCombinationAnimationUuid());
                if (caption.getCombinationAnimationDuration() >= 0) {
                    newCaption.setModularCaptionAnimationPeroid(caption.getCombinationAnimationDuration());
                }
            } else {
                newCaption.applyModularCaptionInAnimation(caption.getMarchInAnimationUuid());
                long outPoint = 0, inPoint = 0;
                if (newCaption instanceof NvsTimelineCaption) {
                    inPoint = ((NvsTimelineCaption) newCaption).getInPoint();
                    outPoint = ((NvsTimelineCaption) newCaption).getOutPoint();
                } else if (newCaption instanceof NvsClipCaption) {
                    inPoint = ((NvsClipCaption) newCaption).getInPoint();
                    outPoint = ((NvsClipCaption) newCaption).getOutPoint();
                }

                int maxDuration = (int) ((outPoint - inPoint) / 1000);
                //Log.d("lhz","in,duration="+caption.getMarchInAnimationDuration()+"**out duration="+caption.getMarchOutAnimationDuration());
                if (caption.getMarchInAnimationDuration() >= 0) {
                    if (maxDuration - caption.getMarchInAnimationDuration() < 500) {
                        //如果设置的入动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                        //If after setting the time of the incoming animation, the remaining default time is less than 500 milliseconds
                        // (the default time of the incoming and outgoing animation is 500ms, regardless of whether it is set or not)
                        newCaption.setModularCaptionOutAnimationDuration(maxDuration - caption.getMarchInAnimationDuration());
                    }
                    //先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                    //The order should not be chaotic, because the default duration of the in and out animation is 500ms, regardless of whether it is set or not.
                    newCaption.setModularCaptionInAnimationDuration(caption.getMarchInAnimationDuration());
                }
                newCaption.applyModularCaptionOutAnimation(caption.getMarchOutAnimationUuid());
                if (caption.getMarchOutAnimationDuration() >= 0) {
                    if (maxDuration - caption.getMarchOutAnimationDuration() < 500) {
                        //如果设置的出动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                        //If after setting the animation time, the remaining default time is less than 500 milliseconds
                        // (the default time of the in and out animation is 500ms, regardless of the setting or not setting the out animation)
                        newCaption.setModularCaptionInAnimationDuration(maxDuration - caption.getMarchOutAnimationDuration());
                    }
                    //先后顺序不可乱，//先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                    //The order should not be chaotic, because the default duration of the in and out animation is 500ms, regardless of whether it is set or not.

                    newCaption.setModularCaptionOutAnimationDuration(caption.getMarchOutAnimationDuration());
                }
            }
        }
        int alignVal = caption.getAlignVal();
        if (alignVal >= 0) {
            newCaption.setTextAlignment(alignVal);
        }
        //此处注意，默认不能设置setVerticalLayout，因为一旦设置过后，应用不同的字幕样式（横、竖）就无法自动适应。
//        //Note here that setVerticalLayout cannot be set by default, because once it is set,
//        the application of different subtitle styles (horizontal, vertical) cannot be automatically adapted.
        if (CaptionInfo.O_VERTICAL == caption.getOrientationType()) {
            newCaption.setVerticalLayout(true);
        } else if (CaptionInfo.O_HORIZONTAL == caption.getOrientationType()) {
            newCaption.setVerticalLayout(false);
        }

        if (caption.getList()!=null){
            newCaption.setTextSpanList(caption.getList());
        }

        int userColorFlag = caption.getUsedColorFlag();
        if (userColorFlag == ATTRIBUTE_USED_FLAG) {
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionColor());
            if (textColor != null) {
                textColor.a = caption.getCaptionColorAlpha() / 100.0f;
                newCaption.setTextColor(textColor);
            }
        }
        //需要设置为选中的背景色
        //Need to be set to the selected background color
        int userBackgroundFlag = caption.getUsedBackgroundFlag();
        if (userBackgroundFlag == ATTRIBUTE_USED_FLAG) {
            NvsColor backgroundColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionBackground());
            if (backgroundColor != null) {
                backgroundColor.a = caption.getCaptionBackgroundAlpha() / 100.0f;
                newCaption.setBackgroundColor(backgroundColor);
            }

        }
        //设置圆角 Set corner
        int userBackgroundRadiusFlag = caption.getUsedBackgroundRadiusFlag();
        if (userBackgroundRadiusFlag == ATTRIBUTE_USED_FLAG) {

            float radius = caption.getCaptionBackgroundRadius();
            newCaption.setBackgroundRadius(radius);
        }

        //设置边距 Set padding
        int userBackgroundPaddingFlag = caption.getUsedBackgroundPaddingFlag();
        if (userBackgroundPaddingFlag == ATTRIBUTE_USED_FLAG) {
            float padding = caption.getCaptionBackgroundPadding();
            newCaption.setBoundaryPaddingRatio(padding);
        }

        int usedScaleFlag = caption.getUsedScaleRotationFlag();
        if (usedScaleFlag == ATTRIBUTE_USED_FLAG) {
            /*
             * 放缩字幕
             * Shrink captions
             * */
            float scaleFactorX = caption.getScaleFactorX();
            float scaleFactorY = caption.getScaleFactorY();
            newCaption.setScaleX(scaleFactorX);
            newCaption.setScaleY(scaleFactorY);
            float rotation = caption.getRotation();
            /*
             * 旋转字幕
             * Spin subtitles
             * */
            newCaption.setRotationZ(rotation);
        }

        newCaption.setZValue(caption.getCaptionZVal());
        int usedOutlineFlag = caption.getUsedOutlineFlag();
        if (usedOutlineFlag == ATTRIBUTE_USED_FLAG) {
            boolean hasOutline = caption.isHasOutline();
            newCaption.setDrawOutline(hasOutline);
            if (hasOutline) {
                NvsColor outlineColor = ColorUtil.colorStringtoNvsColor(caption.getOutlineColor());
                if (outlineColor != null) {
                    outlineColor.a = caption.getOutlineColorAlpha() / 100.0f;
                    newCaption.setOutlineColor(outlineColor);
                    newCaption.setOutlineWidth(caption.getOutlineWidth());
                }
            }
        }

        String fontPath = caption.getCaptionFont();
        if (!fontPath.isEmpty()) {
            newCaption.setFontByFilePath(fontPath);
        }

        int usedBold = caption.getUsedIsBoldFlag();
        if (usedBold == ATTRIBUTE_USED_FLAG) {
            boolean isBold = caption.isBold();
            newCaption.setBold(isBold);
        }

        int usedItalic = caption.getUsedIsItalicFlag();
        if (usedItalic == ATTRIBUTE_USED_FLAG) {
            boolean isItalic = caption.isItalic();
            newCaption.setItalic(isItalic);
        }
        int usedShadow = caption.getUsedShadowFlag();
        if (usedShadow == ATTRIBUTE_USED_FLAG) {
            boolean isShadow = caption.isShadow();
            newCaption.setDrawShadow(isShadow);
            if (isShadow) {
                PointF offset = new PointF(7, -7);
                NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
                /*
                 * 字幕阴影偏移量
                 * Subtitle shadow offset
                 * */
                newCaption.setShadowOffset(offset);
                /*
                 * 字幕阴影颜色
                 * Subtitle shadow color
                 * */
                newCaption.setShadowColor(shadowColor);
            }
        }
        int usedUnderline = caption.getUsedUnderlineFlag();
        if (usedUnderline == ATTRIBUTE_USED_FLAG) {
            boolean isUnderline = caption.isUnderline();
            newCaption.setUnderline(isUnderline);
        }

        //        float fontSize = caption.getCaptionSize();
//        if(fontSize >= 0) {
//            newCaption.setFontSize(fontSize);
//        }
        if (userRecordingFlag) {
            newCaption.setRecordingUserOperation(false);
        }
        int usedTranslationFlag = caption.getUsedTranslationFlag();
        if (usedTranslationFlag == ATTRIBUTE_USED_FLAG) {
            PointF translation = caption.getTranslation();
            if (translation != null) {
                newCaption.setCaptionTranslation(translation);
            }
        }

        /*
         * 应用字符间距
         * Apply character spacing
         * */
        int usedLetterSpacingFlag = caption.getUsedLetterSpacingFlag();
        if (usedLetterSpacingFlag == ATTRIBUTE_USED_FLAG) {
            float letterSpacing = caption.getLetterSpacing();
//            if (letterSpacing == 0.0f) {
//                //字间距上层数据 如果是0.0f 给设置标准数据  目前定义的标准数据是100.0f
//                //The upper layer data of the word spacing If it is 0.0f, set the standard data. The currently defined standard data is 100.0f
//                newCaption.setLetterSpacing(100.0f);
//            } else {
//                newCaption.setLetterSpacing(letterSpacing);
//            }
            newCaption.setLetterSpacingType(NvsCaption.LETTER_SPACING_TYPE_ABSOLUTE);
            newCaption.setLetterSpacing(letterSpacing);
            float lineSpacing = caption.getLineSpacing();
            newCaption.setLineSpacing(lineSpacing);
        }
        long outPoint = 0, inPoint = 0;
        if (newCaption instanceof NvsTimelineCaption) {
            inPoint = ((NvsTimelineCaption) newCaption).getInPoint();
            outPoint = ((NvsTimelineCaption) newCaption).getOutPoint();
        } else if (newCaption instanceof NvsClipCaption) {
            inPoint = ((NvsClipCaption) newCaption).getInPoint();
            outPoint = ((NvsClipCaption) newCaption).getOutPoint();
        }
        if (userRecordingFlag) {
            newCaption.setRecordingUserOperation(true);
        }
        Map<Long, KeyFrameInfo> keyFrameInfoMap = caption.getKeyFrameInfo();
        if (!keyFrameInfoMap.isEmpty()) {
//            if (keyFrameInfoMap.isEmpty()) {
//                newCaption.setRotationZ(caption.getRotation());
//                newCaption.setCaptionTranslation(caption.getTranslation());
//                newCaption.setScaleX(caption.getScaleFactorX());
//                newCaption.setScaleY(caption.getScaleFactorY());
//            } else {
            Set<Long> keySet = keyFrameInfoMap.keySet();
            for (long currentTime : keySet) {
                KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);

                long duration = currentTime - inPoint;
                newCaption.removeKeyframeAtTime(TRANS_X, duration);
                newCaption.removeKeyframeAtTime(TRANS_Y, duration);
                newCaption.removeKeyframeAtTime(SCALE_X, duration);
                newCaption.removeKeyframeAtTime(SCALE_Y, duration);
                newCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                newCaption.removeKeyframeAtTime(TRACK_OPACITY, duration);

                newCaption.setCurrentKeyFrameTime(duration);
                newCaption.setScaleX(keyFrameInfo.getScaleX());
                newCaption.setScaleY(keyFrameInfo.getScaleY());
                newCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
                newCaption.setRotationZ(keyFrameInfo.getRotationZ());
                newCaption.setFloatValAtTime(TRACK_OPACITY, keyFrameInfo.getOpacity(), duration);

            }
            //set caption bezier adjust function

            for (long currentTime : keySet) {
                KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
                double forwardControlPointX = keyFrameInfo.getForwardControlPointX();
                double backwardControlPointX = keyFrameInfo.getBackwardControlPointX();
                if (forwardControlPointX == -1 && backwardControlPointX == -1) {
                    continue;
                }
                long duration = currentTime - inPoint;
                newCaption.setCurrentKeyFrameTime(duration);
                NvsControlPointPair pairX = newCaption.getControlPoint(TRANS_X);
                NvsControlPointPair pairY = newCaption.getControlPoint(TRANS_Y);
                if (pairX == null || pairY == null) {
                    continue;
                }
                if (backwardControlPointX != -1) {
                    pairX.backwardControlPoint.x = backwardControlPointX - inPoint;
                    pairX.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransX();
                    pairY.backwardControlPoint.x = backwardControlPointX - inPoint;
                    pairY.backwardControlPoint.y = keyFrameInfo.getBackwardControlPointYForTransY();
                }
                if (forwardControlPointX != -1) {
                    pairX.forwardControlPoint.x = forwardControlPointX - inPoint;
                    pairX.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransX();
                    pairY.forwardControlPoint.x = forwardControlPointX - inPoint;
                    pairY.forwardControlPoint.y = keyFrameInfo.getForwardControlPointYForTransY();
                }
                newCaption.setControlPoint(TRANS_X, pairX);
                newCaption.setControlPoint(TRANS_Y, pairY);
            }
        }
//        newCaption.setRecordingUserOperation(true);
    }

    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution) {
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }
        ParameterSettingValues settingValues = ParameterSettingValues.instance();
        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        int bitDepth = settingValues.getBitDepth();
        videoEditRes.bitDepth = bitDepth;
        Logger.d("bitDepth=" + bitDepth);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);
        return timeline;
    }

    public static NvsSize getTimelineSize(NvsTimeline timeline) {
        NvsSize size = new NvsSize(0, 0);
        if (timeline != null) {
            NvsVideoResolution resolution = timeline.getVideoRes();
            size.width = resolution.imageWidth;
            size.height = resolution.imageHeight;
            return size;
        }
        return null;
    }

    public static void checkAndDeleteExitFX(NvsTimeline mTimeline) {
        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.getFirstTimelineVideoFx();
        while (nvsTimelineVideoFx != null) {
            String name = nvsTimelineVideoFx.getBuiltinTimelineVideoFxName();
            if (name.equals(WATERMARK_DYNAMICS_FXNAME)) {
                mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
                break;
            } else {
                nvsTimelineVideoFx = mTimeline.getNextTimelineVideoFx(nvsTimelineVideoFx);
            }
        }
    }

    /**
     * 构建时间线上的clip的动画特效
     * Animated special effects for the clip on the timeline
     *
     * @param mTimeline
     * @param clipInfoArrayList
     */
    public static void buildTimelineAnimation(NvsTimeline
                                                      mTimeline, ArrayList<ClipInfo> clipInfoArrayList) {
        if (null == clipInfoArrayList || null == mTimeline) {
            return;
        }
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }

        for (int i = 0; i < clipInfoArrayList.size(); i++) {
            //拿到videoClip
            NvsVideoClip videoClip = nvsVideoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            ClipInfo clipInfo = clipInfoArrayList.get(i);
            if (null == clipInfo) {
                return;
            }
            applyCropData(videoClip, clipInfo.getCropInfo());
            applyAnimation(nvsVideoTrack, videoClip, clipInfo.getAnimationInfo());
        }
    }

    /**
     * 应用动画
     * apply Animation
     *
     * @param videoClip
     * @param animationInfo
     */
    public static NvsVideoFx applyAnimation(NvsVideoClip videoClip, AnimationInfo animationInfo) {
        if (null == animationInfo || videoClip == null) {
            return null;
        }
        videoClip.enablePropertyVideoFx(true);
        NvsVideoFx videoFx = videoClip.getPropertyVideoFx();
        if (videoFx == null) {
            return null;
        }
        long in = animationInfo.getmAnimationIn();
        long out = animationInfo.getmAnimationOut();
        // 由服务器控制是否使用post
        videoFx.setStringVal(NvsConstants.ANIMATION_PACKAGE_ID, "");
        videoFx.setStringVal(NvsConstants.ANIMATION_POST_PACKAGE_ID, "");
        if (animationInfo.isPostPackage()) {
            videoFx.setStringVal(NvsConstants.ANIMATION_POST_PACKAGE_ID, animationInfo.getmPackageId());
        } else {
            videoFx.setStringVal(NvsConstants.ANIMATION_PACKAGE_ID, animationInfo.getmPackageId());
        }
        videoFx.setBooleanVal(NvsConstants.ANIMATION_IS_POST_STORYBOARD_3D, false);
        //设置锯齿
        videoFx.setBooleanVal(NvsConstants.ANIMATION_ENABLE_MUTLISAMPLE, true);
           /* //设置背景是否旋转
            mPositionerVideoFx.setBooleanVal("Enable Background Rotation", true);

            //模糊背景
            mPositionerVideoFx.setMenuVal("Background Mode", "Blur");
            mPositionerVideoFx.setFloatVal("Background Blur Radius", 40);*/
        Log.e(TAG, "---in:" + in + "   out:" + out);
        videoFx.setFloatVal(NvsConstants.ANIMATION_PACKAGE_EFFECT_IN, in);
        videoFx.setFloatVal(NvsConstants.ANIMATION_PACKAGE_EFFECT_OUT, out);
        double amplitude = (out - in) * 1f / Constants.NS_TIME_BASE;
        Log.d(TAG, "amplitude:" + amplitude);
        videoFx.setExprVar("amplitude", amplitude);
        return videoFx;
    }

    /**
     * 应用动画
     * apply Animation
     *
     * @param videoClip
     * @param animationInfo
     */
    public static NvsVideoFx applyAnimation(NvsVideoTrack nvsVideoTrack, NvsVideoClip videoClip, AnimationInfo animationInfo) {
        if (null == nvsVideoTrack) {
            return null;
        }

        if (null == animationInfo || videoClip == null) {
            return null;
        }

        //前置转场
        NvsVideoTransition preTrans = nvsVideoTrack.getTransitionBySourceClipIndex(videoClip.getIndex() - 1);
        NvsVideoTransition transition = nvsVideoTrack.getTransitionBySourceClipIndex(videoClip.getIndex());


        videoClip.enablePropertyVideoFx(true);
        NvsVideoFx videoFx = videoClip.getPropertyVideoFx();
        if (videoFx == null) {
            return null;
        }
        long in = animationInfo.getmAnimationIn();
        long out = animationInfo.getmAnimationOut();

        int assetType = animationInfo.getmAssetType();
        if (assetType == NvAsset.ASSET_ANIMATION_OUT) {
            if (null != preTrans && null != transition) {
                in += preTrans.getVideoTransitionDuration();
                out += preTrans.getVideoTransitionDuration() + transition.getVideoTransitionDuration() * 0.5;
            }
        } else {
            if (null != transition) {
                out += 0.5 * transition.getVideoTransitionDuration();
            }
        }

        // 由服务器控制是否使用post
        videoFx.setStringVal(NvsConstants.ANIMATION_PACKAGE_ID, "");
        videoFx.setStringVal(NvsConstants.ANIMATION_POST_PACKAGE_ID, "");
        if (animationInfo.isPostPackage()) {
            videoFx.setStringVal(NvsConstants.ANIMATION_POST_PACKAGE_ID, animationInfo.getmPackageId());
        } else {
            videoFx.setStringVal(NvsConstants.ANIMATION_PACKAGE_ID, animationInfo.getmPackageId());
        }
        videoFx.setBooleanVal(NvsConstants.ANIMATION_IS_POST_STORYBOARD_3D, false);
        //设置锯齿
        videoFx.setBooleanVal(NvsConstants.ANIMATION_ENABLE_MUTLISAMPLE, true);
           /* //设置背景是否旋转
            mPositionerVideoFx.setBooleanVal("Enable Background Rotation", true);

            //模糊背景
            mPositionerVideoFx.setMenuVal("Background Mode", "Blur");
            mPositionerVideoFx.setFloatVal("Background Blur Radius", 40);*/
        Log.e(TAG, "---in:" + in + "   out:" + out);
        videoFx.setFloatVal(NvsConstants.ANIMATION_PACKAGE_EFFECT_IN, in);
        videoFx.setFloatVal(NvsConstants.ANIMATION_PACKAGE_EFFECT_OUT, out);
        double amplitude = (out - in) * 1f / Constants.NS_TIME_BASE;
        Log.d(TAG, "amplitude:" + amplitude);
        videoFx.setExprVar("amplitude", amplitude);
        return videoFx;
    }


    /**
     * 编辑之后重新设置值
     * Reset the value after editing
     *
     * @param mTimeline
     * @param clipInfoArrayList
     */
    public static void buildColorAdjustInfo(NvsTimeline
                                                    mTimeline, ArrayList<ClipInfo> clipInfoArrayList) {
        if (null == mTimeline || null == clipInfoArrayList || clipInfoArrayList.size() == 0) {
            return;
        }
        //遍历clipInfo 给每个clip设置调节的数据
        //Traverse clipInfo to set adjusted data for each clip
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipInfoCount = clipInfoArrayList.size();
        for (int i = 0; i < clipInfoCount; i++) {
            //拿到videoClip
            //Get videoClip
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (null == videoClip) {
                continue;
            }
            ClipInfo clipInfo = clipInfoArrayList.get(i);
            if (null == clipInfo) {
                return;
            }
            //找到videoClip上对应的调节特效 然后设置值
            //Find the corresponding adjustment effect on the videoClip and set the value
            int fxCount = videoClip.getFxCount();
            NvsVideoFx mColorVideoFx = null;
            NvsVideoFx mVignetteVideoFx = null;
            NvsVideoFx mSharpenVideoFx = null;
            NvsVideoFx mTintVideoFx = null;
            NvsVideoFx mDenoiseVideoFx = null;
            for (int index = 0; index < fxCount; ++index) {
                NvsVideoFx videoFx = videoClip.getFxByIndex(index);
                if (videoFx == null) {
                    continue;
                }

                String fxName = videoFx.getBuiltinVideoFxName();
                if (fxName == null || TextUtils.isEmpty(fxName)) {
                    continue;
                }
                if (fxName.equals(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST)) {
                    mColorVideoFx = videoFx;
                } else if (fxName.equals(Constants.ADJUST_TYPE_VIGETTE)) {
                    mVignetteVideoFx = videoFx;
                } else if (fxName.equals(Constants.ADJUST_TYPE_SHARPEN)) {
                    mSharpenVideoFx = videoFx;
                } else if (fxName.equals(Constants.ADJUST_TYPE_TINT)) {
                    mTintVideoFx = videoFx;
                } else if (fxName.equals(Constants.ADJUST_TYPE_DENOISE)) {
                    mDenoiseVideoFx = videoFx;
                }
            }
            //没有特效则初始化创建对应的特效
            //If there is no special effect, initialize and create the corresponding special effect
            if (mColorVideoFx == null) {
                mColorVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
                mColorVideoFx.setAttachment(Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST, Constants.ADJUST_TYPE_BASIC_IMAGE_ADJUST);
            }

            if (mVignetteVideoFx == null) {
                mVignetteVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_VIGETTE);
                mVignetteVideoFx.setAttachment(Constants.ADJUST_TYPE_VIGETTE, Constants.ADJUST_TYPE_VIGETTE);
            }

            if (mSharpenVideoFx == null) {
                mSharpenVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_SHARPEN);
                mSharpenVideoFx.setAttachment(Constants.ADJUST_TYPE_SHARPEN, Constants.ADJUST_TYPE_SHARPEN);
            }

            if (mTintVideoFx == null) {
                mTintVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_TINT);
                mTintVideoFx.setAttachment(Constants.ADJUST_TYPE_TINT, Constants.ADJUST_TYPE_TINT);
            }

            if (null == mDenoiseVideoFx) {
                mDenoiseVideoFx = videoClip.appendBuiltinFx(Constants.ADJUST_TYPE_DENOISE);
                mDenoiseVideoFx.setAttachment(Constants.ADJUST_TYPE_DENOISE, Constants.ADJUST_TYPE_DENOISE);
            }

            //设置特效的值
            mColorVideoFx.setFloatVal(Constants.ADJUST_BRIGHTNESS, clipInfo.getBrightnessVal());
            mColorVideoFx.setFloatVal(Constants.ADJUST_CONTRAST, clipInfo.getContrastVal());
            mColorVideoFx.setFloatVal(Constants.ADJUST_SATURATION, clipInfo.getSaturationVal());
            mColorVideoFx.setFloatVal(Constants.ADJUST_HIGHTLIGHT, clipInfo.getmHighLight());
            mColorVideoFx.setFloatVal(Constants.ADJUST_SHADOW, clipInfo.getmShadow());
            mTintVideoFx.setFloatVal(Constants.ADJUST_TEMPERATURE, clipInfo.getTemperature());
            mTintVideoFx.setFloatVal(Constants.ADJUST_TINT, clipInfo.getTint());
            mColorVideoFx.setFloatVal(Constants.ADJUST_FADE, clipInfo.getFade());
            mVignetteVideoFx.setFloatVal(Constants.ADJUST_DEGREE, clipInfo.getVignetteVal());
            mSharpenVideoFx.setFloatVal(Constants.ADJUST_AMOUNT, clipInfo.getSharpenVal());
            mDenoiseVideoFx.setFloatVal(Constants.ADJUST_DENOISE, clipInfo.getDensity());
            mDenoiseVideoFx.setFloatVal(Constants.ADJUST_DENOISE_DENSITY, clipInfo.getDenoiseDensity());
        }
    }

    /**
     * 构建时间线上的背景特效
     * Build background effects on the timeline
     *
     * @param mTimeline
     * @param clipInfoData
     */
    public static void buildTimelineBackground(NvsTimeline
                                                       mTimeline, ArrayList<ClipInfo> clipInfoData) {
        if (null == clipInfoData || null == mTimeline) {
            return;
        }

        int size = clipInfoData.size();
        for (int i = 0; i < size; i++) {
            ClipInfo clipInfo = clipInfoData.get(i);
            updateBackground(clipInfo, i, mTimeline);
        }
    }

    /**
     * 更新背景相关
     * updateBackground
     *
     * @param clipInfo
     * @param position
     * @param timeline
     */
    public static void updateBackground(ClipInfo clipInfo, int position, NvsTimeline timeline) {
        if (clipInfo == null) {
            return;
        }
        NvsVideoTrack videoTrackByIndex = timeline.getVideoTrackByIndex(0);
        NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(position);

//        NvsVideoResolution videoRes = timeline.getVideoRes();
//        StoryboardInfo backgroundInfo = clipInfo.getBackgroundInfo();
//        if (backgroundInfo == null) {
//            return;
//        }
//        Map<String, Float> clipTrans = backgroundInfo.getClipTrans();
//        String backgroundStory = null;
//        int type = backgroundInfo.getBackgroundType();
//        if (type == BackgroundActivity.TYPE_BACKGROUND_BLUR) {
//            String filePath = clipInfo.getFilePath();
//            backgroundStory = StoryboardUtil.getBlurBackgroundStory(videoRes.imageWidth, videoRes.imageHeight, filePath, backgroundInfo.getIntensity(), clipTrans);
//        } else {
//            backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), videoRes.imageWidth, videoRes.imageHeight, clipTrans);
//        }
//        backgroundInfo.setStringVal("Description String", backgroundStory);
//        backgroundInfo.bindToTimelineByType(clipByIndex, backgroundInfo.getSubType());
        applyBackground(clipByIndex, clipInfo.getBackGroundInfo());

    }

    /**
     * 构建画面裁剪区域信息
     * Build picture cropping area information
     *
     * @param mTimeline
     * @param clipInfoArrayList
     */
    public static void buildAdjustCutInfo(NvsTimeline
                                                  mTimeline, ArrayList<ClipInfo> clipInfoArrayList) {
        if (null == mTimeline || null == clipInfoArrayList || clipInfoArrayList.size() == 0) {
            return;
        }
        //遍历clipInfo 给每个clip设置调节的数据
        //Traverse clipInfo to set adjusted data for each clip
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipInfoCount = clipInfoArrayList.size();
        for (int i = 0; i < clipInfoCount; i++) {
            NvsVideoClip mVideoClip = videoTrack.getClipByIndex(i);
            if (null == mVideoClip) {
                continue;
            }
            ClipInfo clipInfo = clipInfoArrayList.get(i);
            if (null == clipInfo) {
                continue;
            }
            applyCropData(mVideoClip, clipInfo.getCropInfo());
        }
    }

    public static NvsVideoResolution getVideoEditResolutionByClip(String path, int compileRes) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(path);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int streamRotation = avFileInfo.getVideoStreamRotation(0);
        int imageWidth = dimension.width;
        int imageHeight = dimension.height;
        if (streamRotation == 1 || streamRotation == 3) {
            imageWidth = dimension.height;
            imageHeight = dimension.width;
        }
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        float iamgeToTimelineSatio = 1.0F;
        if (imageWidth > imageHeight) {
            iamgeToTimelineSatio = compileRes * 1.0F / imageWidth;
            size.set(compileRes, (int) (imageHeight * iamgeToTimelineSatio));
        } else {
            iamgeToTimelineSatio = compileRes * 1.0F / imageHeight;
            size.set((int) (imageWidth * iamgeToTimelineSatio), compileRes);
        }
        videoEditRes.imageWidth = alignedData(size.x, 4);
        videoEditRes.imageHeight = alignedData(size.y, 2);
        return videoEditRes;
    }

    /**
     * 整数对齐
     * Integer alignment
     *
     * @param data，源数据
     * @param num      对齐的数据
     * @return
     */
    private static int alignedData(int data, int num) {
        return data - data % num;
    }

    /**
     * 蒙版生效
     * applyMaskInfo
     *
     * @param videoClip
     * @param infoData
     */
    public static void applyMask(NvsVideoClip videoClip, MaskInfoData infoData) {
        if (videoClip == null) return;
        boolean remove = infoData == null ? true :
                (infoData.getMaskType() == MaskInfoData.MaskType.NONE ? true : false);
        int rawFxCount = videoClip.getRawFxCount();
        NvsVideoFx maskFx = null;
        for (int i = 0; i < rawFxCount; i++) {
            NvsVideoFx fawFx = videoClip.getRawFxByIndex(i);
            if (fawFx != null) {
                String type = (String) fawFx.getAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE);
                if (TextUtils.equals(fawFx.getBuiltinVideoFxName(), NvsConstants.KEY_MASK_GENERATOR)
                        && TextUtils.equals(type, NvsConstants.KEY_MASK_GENERATOR_SIGN_MASK)) {
                    maskFx = fawFx;
                    if (remove) {
                        videoClip.removeRawFx(i);
                        return;
                    }
                }
            }
        }
        if (infoData == null) {
            return;
        }
        videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
        videoClip.setImageMotionAnimationEnabled(false);
        if (maskFx == null && infoData.getMaskType() != 0) {
            maskFx = videoClip.appendRawBuiltinFx(NvsConstants.KEY_MASK_GENERATOR);
            //蒙版和裁剪都使用的”Mask Generator“ 所以加标记区分一下
            //"Mask Generator" is used for both masking and cropping, so mark it to distinguish
            maskFx.setAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE, NvsConstants.KEY_MASK_GENERATOR_SIGN_MASK);
        }
        if (maskFx != null) {
            maskFx.setRegionalFeatherWidth(infoData.getFeatherWidth());
            maskFx.setInverseRegion(infoData.isReverse());
            maskFx.setIgnoreBackground(true);
            maskFx.setRegional(true);
            if (infoData.getMaskType() == MaskInfoData.MaskType.TEXT) {
                maskFx.setStringVal(NvsConstants.KEY_MASK_STORYBOARD_DESC, infoData.getTextStoryboard());
                maskFx.setRegionInfo(null);
            } else {
                maskFx.setStringVal(NvsConstants.KEY_MASK_STORYBOARD_DESC, "");
                maskFx.setRegionInfo(infoData.getMaskRegionInfo());
            }
        }
//        Log.d(TAG, "applyMask data" + infoData);
    }


    /**
     * 背景生效(属性特技)
     * Background effect (PropertyVideoFx)
     *
     * @param videoClip
     * @param backGroundInfo
     */
    public static void applyBackground(NvsVideoClip videoClip, BackGroundInfo backGroundInfo) {
        if (videoClip == null || backGroundInfo == null) return;
        int type = backGroundInfo.getType();
        videoClip.enablePropertyVideoFx(true);
        NvsVideoFx bgFx = videoClip.getPropertyVideoFx();
        if (bgFx == null) return;
//        removeKeyFrameProperty(videoClip.getInPoint(),bgFx);
//        removeKeyFrameProperty(videoClip.getOutPoint(),bgFx);
        if (type == BackGroundInfo.BackgroundType.BACKGROUND_COLOR) {
            bgFx.setMenuVal(NvsConstants.PROPERTY_KEY_BACKGROUND_MODE, NvsConstants.PROPERTY_VALUE_BACKGROUND_COLOR_SOLID);
            bgFx.setStringVal(NvsConstants.PROPERTY_KEY_BACKGROUND_IMAGE, "");
            bgFx.setColorVal(NvsConstants.PROPERTY_KEY_BACKGROUND_COLOR, ColorUtil.colorStringtoNvsColor(backGroundInfo.getColorValue()));
        } else if (type == BackGroundInfo.BackgroundType.BACKGROUND_TYPE) {
            if (!TextUtils.isEmpty(backGroundInfo.getFilePath())) {
                bgFx.setMenuVal(NvsConstants.PROPERTY_KEY_BACKGROUND_MODE, NvsConstants.PROPERTY_VALUE_BACKGROUND_IMAGE_FILE);
                bgFx.setStringVal(NvsConstants.PROPERTY_KEY_BACKGROUND_IMAGE, backGroundInfo.getFilePath());
            } else {
                bgFx.setMenuVal(NvsConstants.PROPERTY_KEY_BACKGROUND_MODE, NvsConstants.PROPERTY_VALUE_BACKGROUND_COLOR_SOLID);
                bgFx.setColorVal(NvsConstants.PROPERTY_KEY_BACKGROUND_COLOR, new NvsColor(0, 0, 0, 0));
            }
        } else if (type == BackGroundInfo.BackgroundType.BACKGROUND_BLUR) {
            bgFx.setMenuVal(NvsConstants.PROPERTY_KEY_BACKGROUND_MODE, NvsConstants.PROPERTY_VALUE_BACKGROUND_BLUR);
            bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_BACKGROUND_BLUR_RADIUS, backGroundInfo.getValue());
        }
        bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_SCALE_X, backGroundInfo.getScaleX());
        bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_SCALE_Y, backGroundInfo.getScaleY());
        bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_TRANS_X, backGroundInfo.getTransX());
        bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_TRANS_Y, backGroundInfo.getTransY());
        bgFx.setFloatVal(NvsConstants.PROPERTY_KEY_ROTATION, backGroundInfo.getRotation());
//        if (videoClip.getImageMotionAnimationEnabled()) {
//            NvsVideoResolution videoResolution = TimelineData.instance().getVideoResolution();
//            PointF pointF = new PointF(videoResolution.imageWidth, videoResolution.imageHeight);
//            //todo ROI转成关键帧
//            RectF startROI = videoClip.getStartROI();
//            applyKeyFrameProperty(videoClip.getInPoint(), pointF, bgFx, startROI);
//            RectF endROI = videoClip.getEndROI();
//            applyKeyFrameProperty(videoClip.getOutPoint(), pointF, bgFx, endROI);
//        }
    }
//
//    private static void applyKeyFrameProperty(long roiTime, PointF size, NvsVideoFx bgFx, RectF roiRectF) {
//        if (size == null || bgFx == null || roiRectF == null) return;
//        float width = roiRectF.right - roiRectF.left;
//        float height = roiRectF.top - roiRectF.bottom;
//        float scaleX = 2 / width;
//        float scaleY = 2 / height;
//        float scale = scaleX > scaleY ? scaleX : scaleY;
//        float transX = (-(roiRectF.right + roiRectF.left) / 2) * size.x / 2;
//        float transY = (-(roiRectF.top + roiRectF.bottom) / 2) * size.y / 2;
//        bgFx.setFloatValAtTime(NvsConstants.PROPERTY_KEY_SCALE_X, scale, roiTime);
//        bgFx.setFloatValAtTime(NvsConstants.PROPERTY_KEY_SCALE_Y, scale, roiTime);
//        bgFx.setFloatValAtTime(NvsConstants.PROPERTY_KEY_TRANS_X, transX, roiTime);
//        bgFx.setFloatValAtTime(NvsConstants.PROPERTY_KEY_TRANS_Y, transY, roiTime);
//        Log.d(TAG, "applyKeyFrameProperty roiTime:" + roiTime + " scale X:" + scaleX + " Y:" + scaleY + " tX:" + transX + "tY:" + transY);
//    }
//
//    private static void removeKeyFrameProperty(long roiTime, NvsVideoFx bgFx) {
//        bgFx.removeKeyframeAtTime(NvsConstants.PROPERTY_KEY_SCALE_X, roiTime);
//        bgFx.removeKeyframeAtTime(NvsConstants.PROPERTY_KEY_SCALE_Y, roiTime);
//        bgFx.removeKeyframeAtTime(NvsConstants.PROPERTY_KEY_TRANS_X, roiTime);
//        bgFx.removeKeyframeAtTime(NvsConstants.PROPERTY_KEY_TRANS_Y, roiTime);
//        Log.d(TAG, "removeKeyFrameProperty roiTime:" + roiTime);
//    }

    public static void applyCropData(NvsVideoClip videoClip, CutData cropInfo) {
        if (videoClip == null || cropInfo == null) {
            return;
        }
        NvsVideoFx cropperVideoFx = null;
        NvsVideoFx transformVideoFx = null;
        int rawFxCount = videoClip.getRawFxCount();
        for (int i = 0; i < rawFxCount; i++) {
            NvsVideoFx rawVideoFx = videoClip.getRawFxByIndex(i);
            if (rawVideoFx != null) {
                String attachment = (String) rawVideoFx.getAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE);
                if (TextUtils.equals(rawVideoFx.getBuiltinVideoFxName(), NvsConstants.CUT_KEY_MASK_GENERATOR)
                        && TextUtils.equals(attachment, NvsConstants.KEY_MASK_GENERATOR_SIGN_CROP)) {
                    cropperVideoFx = rawVideoFx;
                    break;
                }
            }
        }
        for (int i = 0; i < rawFxCount; i++) {
            NvsVideoFx rawVideoFx = videoClip.getRawFxByIndex(i);
            if (rawVideoFx != null && TextUtils.equals(rawVideoFx.getBuiltinVideoFxName(), NvsConstants.CUT_KEY_MASK_GENERATOR_TRANSFORM_2D)) {
                transformVideoFx = rawVideoFx;
                break;
            }
        }

        if (transformVideoFx == null) {
            transformVideoFx = videoClip.appendRawBuiltinFx(NvsConstants.CUT_KEY_MASK_GENERATOR_TRANSFORM_2D);
        }
        if (transformVideoFx != null) {
            transformVideoFx.setBooleanVal(NvsConstants.CUT_KEY_IS_NORMALIZED_COORD, true);
            //转换数据 transformData
            Map<String, Float> transFromData = cropInfo.getTransformData();
            LogUtils.d("onConfirm: transFromData = " + transFromData);
            transformVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_TRANS_X, transFromData.get(STORYBOARD_KEY_TRANS_X));
            transformVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_TRANS_Y, transFromData.get(STORYBOARD_KEY_TRANS_Y));
            transformVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_SCALE_X, transFromData.get(STORYBOARD_KEY_SCALE_X));
            transformVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_SCALE_Y, transFromData.get(STORYBOARD_KEY_SCALE_Y));
            transformVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_ROTATION, -transFromData.get(STORYBOARD_KEY_ROTATION_Z));
//            Log.d(TAG, "apply " + " transX:" + transX + " transY:" + transY + " scaleX:" +
//                    scaleX + " scaleY:" + scaleY + " cropData:" + cropInfo);
        }
        float[] regionData = cropInfo.getmRegionData();
        if (regionData != null && regionData.length >= 8) {

            int ratio = cropInfo.getRatio();
            float ratioValue = cropInfo.getRatioValue();
            CommonData.AspectRatio aspectRatio = CommonData.AspectRatio.getAspectRatio(ratio);
            String cropperRatio;
            if (aspectRatio == null) {
                cropperRatio = com.meishe.engine.constant.NvsConstants.VALUE_CROPPER_FREE;
            } else {
                cropperRatio = aspectRatio.getStringValue();
            }

            if (cropperVideoFx == null) {
                cropperVideoFx = videoClip.appendRawBuiltinFx(NvsConstants.KEY_MASK_GENERATOR);
                cropperVideoFx.setAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE, NvsConstants.KEY_MASK_GENERATOR_SIGN_CROP);
            }
            if (cropperVideoFx == null) {
                LogUtils.e("PropertyVideoFx is null");
                return;
            }

            cropperVideoFx.setStringVal(NvsConstants.KEY_CROPPER_RATIO, cropperRatio);
            cropperVideoFx.setFloatVal(NvsConstants.KEY_CROPPER_ASSET_ASPECT_RATIO, ratioValue);

            //局部特效区域信息
            NvsMaskRegionInfo nvsMaskRegionInfo = new NvsMaskRegionInfo();
            NvsMaskRegionInfo.RegionInfo regionInfo = new NvsMaskRegionInfo.RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON);
            List<NvsPosition2D> positions = new ArrayList<>();
            for (int i = 0; i < regionData.length; i++) {
                positions.add(new NvsPosition2D(regionData[i], regionData[++i]));
            }
            regionInfo.setPoints(positions);
            nvsMaskRegionInfo.addRegionInfo(regionInfo);
            cropperVideoFx.setArbDataVal(com.meishe.engine.constant.NvsConstants.KEY_CROPPER_REGION_INFO, nvsMaskRegionInfo);
            cropperVideoFx.setBooleanVal(com.meishe.engine.constant.NvsConstants.KEY_CROPPER_KEEP_RGB, true);
        }
    }


    /**
     * 删除指定的Raw内建特效
     * Delete the specified Raw built-in special effects
     *
     * @param videoClip
     * @param rawFxName
     */
    public static void removeRawBuildInFx(NvsVideoClip videoClip, String rawFxName) {
        if (videoClip == null) return;
        int rawFxCount = videoClip.getRawFxCount();
        for (int i = 0; i < rawFxCount; i++) {
            NvsVideoFx rawFx = videoClip.getRawFxByIndex(i);
            if (rawFx != null && TextUtils.equals(rawFx.getBuiltinVideoFxName(), rawFxName)) {
                videoClip.removeRawFx(i);
                return;
            }
        }
    }

    public static void applyClipCaption(NvsVideoClip
                                                videoClip, List<CaptionInfo> captionInfo, long clipDuration) {
        if (videoClip == null) {
            return;
        }
        NvsClipCaption deleteCaption = videoClip.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            Logger.e(TAG, "capCategory = " + capCategory);
            int roleTheme = deleteCaption.getRoleInTheme();
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作删除
                deleteCaption = videoClip.getNextCaption(deleteCaption);
            } else {
                deleteCaption = videoClip.removeCaption(deleteCaption);
            }
        }
        if (captionInfo == null) {
            return;
        }
        NvsClipCaption newCaption;
        for (CaptionInfo caption : captionInfo) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            if (caption.isTraditionCaption()) {
                //传统字幕
                newCaption = videoClip.addCaption(caption.getText(), caption.getInPoint() + clipDuration,
                        duration, null);
            } else {
                //拼装字幕
                newCaption = videoClip.addModularCaption(caption.getText(), caption.getInPoint() + clipDuration,
                        duration);
            }
            updateCaptionAttribute(newCaption, caption);
        }
    }


    /**
     * 在视频片段上查找特效
     *
     * @param videoClip 要操作的视频片段
     * @param remove    删除并添加新的AR特效
     * @return
     */
    public static NvsVideoFx findOrCrateVideoFxFromVideoClip(NvsVideoClip videoClip, boolean remove) {
        if (videoClip == null) {
            return null;
        }
        int fxCount = videoClip.getFxCount();
        for (int count = fxCount-1; count >=0 ; count--) {
            NvsVideoFx fxByIndex = videoClip.getFxByIndex(count);
            if (fxByIndex == null) {
                continue;
            }
            if (fxByIndex.getBuiltinVideoFxName().equals(Constants.AR_SCENE)) {
                if (remove) {
                    videoClip.removeFx(count);
                } else {
                    return fxByIndex;
                }
            }
        }
        NvsVideoFx nvsVideoFx = videoClip.insertBuiltinFx(Constants.AR_SCENE, 0);
        if (BuildConfig.FACE_MODEL == 240) {
            nvsVideoFx.setBooleanVal("Use Face Extra Info", true);
        }
        //支持的人脸个数，是否需要使用最小的设置
        nvsVideoFx.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);
        NvsARSceneManipulate arSceneManipulate = nvsVideoFx.getARSceneManipulate();
        if (arSceneManipulate!=null){
            arSceneManipulate.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);
        }
        return nvsVideoFx;
    }

    /**
     * 在视频片段上查找特效
     *
     * @param clipByIndex
     * @return
     */
    public static NvsVideoFx findOrCrateVideoFxFromVideoClip(NvsVideoClip clipByIndex) {
        return findOrCrateVideoFxFromVideoClip(clipByIndex, false);
    }

    /**
     * 在视频片段上查找特效
     *
     * @param clipByIndex
     * @return
     */
    public static NvsVideoFx findVideoFxFromVideoClip(NvsVideoClip clipByIndex) {
        if (clipByIndex == null) {
            return null;
        }
        int fxCount = clipByIndex.getFxCount();
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx fxByIndex = clipByIndex.getFxByIndex(i);
            if (fxByIndex == null) {
                continue;
            }
            if (fxByIndex.getBuiltinVideoFxName().equals(Constants.AR_SCENE)) {
                return fxByIndex;
            }
        }
        return null;
    }

}
