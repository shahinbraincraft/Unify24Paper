package com.meishe.fxplugin.utils;

import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.meishe.fxplugin.data.ClipInfo;
import com.meishe.fxplugin.data.TimelineData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2018/5/29.
 */

public class TimelineUtil {
    private static String TAG = "TimelineUtil";
    public final static long TIME_BASE = 1000000;

    /*
    * 主编辑页面时间线API
    * Main edit page timeline API
    * */
    public static NvsTimeline createTimeline() {
        NvsTimeline timeline = newTimeline(TimelineData.instance( ).getVideoResolution( ));
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
        timeline.appendAudioTrack( );
        /*
        * 录音轨道
        * Recording track
        * */
        timeline.appendAudioTrack( );

//        setTimelineData(timeline);

        return timeline;
    }

    /*
    * 片段编辑页面时间线API
    * Clip Edit Page Timeline API
    * */
    public static NvsTimeline createSingleClipTimeline(ClipInfo clipInfo, boolean isTrimClip) {
        NvsTimeline timeline = newTimeline(TimelineData.instance( ).getVideoResolution( ));
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

        NvsVideoTrack videoTrack = timeline.appendVideoTrack( );
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        addVideoClip(videoTrack, clipInfo, isTrimClip);
        return true;
    }

    public static boolean buildSingleClipVideoTrackExt(NvsTimeline timeline, String filePath) {
        if (timeline == null || filePath == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack( );
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


    public static boolean removeTimeline(NvsTimeline timeline) {
        if (timeline == null)
            return false;

        NvsStreamingContext context = NvsStreamingContext.getInstance( );
        if (context == null)
            return false;

        return context.removeTimeline(timeline);
    }

    public static boolean buildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack( );
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }

        ArrayList<ClipInfo> videoClipArray = TimelineData.instance( ).getClipInfoData( );
        for (int i = 0; i < videoClipArray.size( ); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true);
        }
        float videoVolume = TimelineData.instance( ).getOriginVideoVolume( );
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    public static boolean reBuildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        int videoTrackCount = timeline.videoTrackCount( );
        NvsVideoTrack videoTrack = videoTrackCount == 0 ? timeline.appendVideoTrack( ) : timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        videoTrack.removeAllClips( );
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance( ).getClipInfoData( );
        for (int i = 0; i < videoClipArray.size( ); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true);
        }
//        setTimelineData(timeline);
        float videoVolume = TimelineData.instance( ).getOriginVideoVolume( );
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    private static void addVideoClip(NvsVideoTrack videoTrack, ClipInfo clipInfo, boolean isTrimClip) {
        if (videoTrack == null || clipInfo == null)
            return;
        String filePath = clipInfo.getFilePath( );
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return;
        }

        float brightVal = clipInfo.getBrightnessVal( );
        float contrastVal = clipInfo.getContrastVal( );
        float saturationVal = clipInfo.getSaturationVal( );
        float vignette = clipInfo.getVignetteVal( );
        float sharpen = clipInfo.getSharpenVal( );
        if (brightVal >= 0 || contrastVal >= 0 || saturationVal >= 0) {
            NvsVideoFx videoFxColor = videoClip.appendBuiltinFx(Constants.FX_COLOR_PROPERTY);
            if (videoFxColor != null) {
                if (brightVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS, brightVal);
                if (contrastVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST, contrastVal);
                if (saturationVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION, saturationVal);
            }
        }
        if (vignette >= 0) {
            NvsVideoFx vignetteVideoFx = videoClip.appendBuiltinFx(Constants.FX_VIGNETTE);
            vignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, vignette);
        }
        if (sharpen >= 0) {
            NvsVideoFx sharpenVideoFx = videoClip.appendBuiltinFx(Constants.FX_SHARPEN);
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, sharpen);
        }
        int videoType = videoClip.getVideoType( );
        if (videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE) {
            /*
            * 当前片段是图片
            * The current clip is a picture
            * */
            long trimIn = videoClip.getTrimIn( );
            long trimOut = clipInfo.getTrimOut( );
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
            int imgDisplayMode = clipInfo.getImgDispalyMode( );
            if (imgDisplayMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
                /*
                * 区域显示
                * Area display
                * */
                videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                RectF normalStartRectF = clipInfo.getNormalStartROI( );
                RectF normalEndRectF = clipInfo.getNormalEndROI( );
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

            boolean isOpenMove = clipInfo.isOpenPhotoMove( );
            videoClip.setImageMotionAnimationEnabled(isOpenMove);
        } else {
            /*
            * 当前片段是视频
            * The current clip is a video
            * */
            float volumeGain = clipInfo.getVolume( );
            videoClip.setVolumeGain(volumeGain, volumeGain);
            float pan = clipInfo.getPan( );
            float scan = clipInfo.getScan( );
            videoClip.setPanAndScan(pan, scan);
            float speed = clipInfo.getSpeed( );
            if (speed > 0) {
                videoClip.changeSpeed(speed);
            }
            videoClip.setExtraVideoRotation(clipInfo.getRotateAngle( ));
            int scaleX = clipInfo.getScaleX( );
            int scaleY = clipInfo.getScaleY( );
            if (scaleX >= -1 || scaleY >= -1) {
                NvsVideoFx videoFxTransform = videoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                if (videoFxTransform != null) {
                    if (scaleX >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, scaleX);
                    if (scaleY >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, scaleY);
                }
            }

            if (!isTrimClip) {
                /*
                * 如果当前是裁剪页面，不裁剪片段
                * If the page is currently cropped, the clip is not cropped
                * */
                return;
            }
            long trimIn = clipInfo.getTrimIn( );
            long trimOut = clipInfo.getTrimOut( );
            if (trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
        }
    }

    public static boolean applyTheme(NvsTimeline timeline, String themeId) {
        if (timeline == null)
            return false;

        timeline.removeCurrentTheme( );
        if (themeId == null || themeId.isEmpty( ))
            return false;

        /*
        * 设置主题片头和片尾
        * Set theme title and trailer
        * */
        String themeCaptionTitle = TimelineData.instance( ).getThemeCptionTitle( );
        if (!themeCaptionTitle.isEmpty( )) {
            timeline.setThemeTitleCaptionText(themeCaptionTitle);
        }
        String themeCaptionTrailer = TimelineData.instance( ).getThemeCptionTrailer( );
        if (!themeCaptionTrailer.isEmpty( )) {
            timeline.setThemeTrailerCaptionText(themeCaptionTrailer);
        }

        if (!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        return true;
    }

    private static boolean removeAllVideoFx(NvsVideoClip videoClip) {
        if (videoClip == null)
            return false;

        int fxCount = videoClip.getFxCount( );
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx fx = videoClip.getFxByIndex(i);
            if (fx == null)
                continue;

            String name = fx.getBuiltinVideoFxName( );
            Log.e("===>", "fx name: " + name);
            if (name.equals(Constants.FX_COLOR_PROPERTY) || name.equals(Constants.FX_VIGNETTE)
                    || name.equals(Constants.FX_SHARPEN)
                    || name.equals(Constants.FX_ADJUST_KEY_TINT)
                    || name.equals(Constants.FX_COLOR_PROPERTY_DENOISE)
                    || name.equals(Constants.FX_COLOR_PROPERTY_DEFINITION)
                    || name.equals(Constants.FX_COLOR_PROPERTY_BASIC)
                    || name.equals(Constants.FX_TRANSFORM_2D)) {
                continue;
            }
            videoClip.removeFx(i);
            i--;
        }
        return true;
    }


    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution) {
        NvsStreamingContext context = NvsStreamingContext.getInstance( );
        if (context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }

        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(25, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution( );
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);
        return timeline;
    }

    public static NvsSize getTimelineSize(NvsTimeline timeline) {
        NvsSize size = new NvsSize(0, 0);
        if (timeline != null) {
            NvsVideoResolution resolution = timeline.getVideoRes( );
            size.width = resolution.imageWidth;
            size.height = resolution.imageHeight;
            return size;
        }
        return null;
    }

//    public static void checkAndDeleteExitFX(NvsTimeline mTimeline) {
//        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.getFirstTimelineVideoFx( );
//        while (nvsTimelineVideoFx != null) {
//            String name = nvsTimelineVideoFx.getBuiltinTimelineVideoFxName( );
//            if (name.equals(WATERMARK_DYNAMICS_FXNAME)) {
//                mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
//                break;
//            } else {
//                nvsTimelineVideoFx = mTimeline.getNextTimelineVideoFx(nvsTimelineVideoFx);
//            }
//        }
//    }

    /**
     * 整数对齐
     * @param data，源数据
     * @param num 对齐的数据
     * @return
     */
    public static int alignedData(int data, int num, double scale) {
        int newDada = data;
        if(scale > 1) {
            newDada = (int)(data / scale);
        }
        if (newDada % num == 0) {
            return newDada;
        }
        return newDada - newDada % num;//向下
    }
}
