package com.meishe.sdkdemo.edit.clipEdit.adjust;

import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.meishe.engine.util.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsPanAndScan;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.constants.NvsConstants;
import com.meishe.engine.bean.CommonData;
import com.meishe.engine.bean.CutData;
import com.meishe.engine.util.StoryboardUtil;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.bean.AdjustRation;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.view.MagicProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate : 2021/3/4.
 * @Description :调整 裁剪页面。Adjust Crop Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class AdjustActivity extends BaseActivity {
    private static final String TAG = "AdjustActivity";
    private CustomTitleBar mTitleBar;

    private CutVideoFragment mVideoFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoFx mVideoFx;
    private NvsVideoClip mVideoClip;
    private ArrayList<ClipInfo> mClipArrayList;
    private CutData mCutData;
    private int mOriginalTimelineWidth;
    private int mOriginalTimelineHeight;


    private int mCurClipIndex = 0;
    private int mScaleX = 1;
    private int mScaleY = 1;
    private int mRotateAngle = 0;
    private CuttingMenuView mCuttingMenuView;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_adjust;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mCuttingMenuView = findViewById(R.id.edit_cutting_menu_view);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.adjust);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        mOriginalTimelineWidth = TimelineData.instance().getVideoResolution().imageWidth;
        mOriginalTimelineHeight = TimelineData.instance().getVideoResolution().imageHeight;
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        int scaleX = clipInfo.getScaleX();
        int scaleY = clipInfo.getScaleY();
        if (scaleX >= -1) {
            mScaleX = scaleX;
        }
        if (scaleY >= -1) {
            mScaleY = scaleY;
        }
        mRotateAngle = clipInfo.getRotateAngle();
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        //注意顺序 Pay attention to the order
        TimelineUtil.buildTimelineBackground(mTimeline, mClipArrayList);
        TimelineUtil.buildTimelineMaskClipInfo(mTimeline, mClipArrayList);
        NvsVideoResolution resolution = TimelineUtil.getVideoEditResolutionByClip(clipInfo.getFilePath(), 1080);
        mTimeline.changeVideoSize(resolution.imageWidth, resolution.imageHeight);
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        mVideoClip = videoTrack.getClipByIndex(0);
        if (mVideoClip == null) {
            return;
        }
        TimelineUtil.removeRawBuildInFx(mVideoClip, NvsConstants.CUT_KEY_MASK_GENERATOR_TRANSFORM_2D);
        mCutData = getInitCutData();
        initVideoFragment();
        if (mCutData != null) {
            mCuttingMenuView.setProgress(mCutData.getTransformData(StoryboardUtil.STORYBOARD_KEY_ROTATION_Z));
            mCuttingMenuView.setSelectRatio(mCutData.getRatio());
        }
        adjustClip();
    }

    /**
     * 获取裁剪信息
     * Get cropping information
     *
     * @return
     */
    private CutData getInitCutData() {
        CutData cutData = mClipArrayList.get(mCurClipIndex).getCropInfo();
        if (cutData == null) {
            cutData = new CutData();
        }
        return cutData;

//        NvsVideoClip nvsVideoClip = TimelineUtil.nvsTimeline.getVideoTrackByIndex(0).getClipByIndex(mCurClipIndex);
//        NvsVideoFx cropperVideoFx = null;
//        NvsVideoFx transformVideoFx = null;
//        int rawFxCount = nvsVideoClip.getRawFxCount();
//        for (int i = 0; i < rawFxCount; i++) {
//            NvsVideoFx rawVideoFx = nvsVideoClip.getRawFxByIndex(i);
//            if (rawVideoFx != null) {
//                String attachment = (String) rawVideoFx.getAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE);
//                if (TextUtils.equals(rawVideoFx.getBuiltinVideoFxName(), NvsConstants.CUT_KEY_MASK_GENERATOR)
//                        && TextUtils.equals(attachment, NvsConstants.KEY_MASK_GENERATOR_SIGN_CROP)) {
//                    cropperVideoFx = rawVideoFx;
//                    break;
//                }
//            }
//        }
//        for (int i = 0; i < rawFxCount; i++) {
//            NvsVideoFx rawVideoFx = mVideoClip.getRawFxByIndex(i);
//            if (rawVideoFx != null && TextUtils.equals(rawVideoFx.getBuiltinVideoFxName(), NvsConstants.CUT_KEY_MASK_GENERATOR_TRANSFORM_2D)) {
//                transformVideoFx = rawVideoFx;
//                break;
//            }
//        }
//        if (cropperVideoFx != null && transformVideoFx != null) {
//            CutData cutData = new CutData();
//            cutData.putTransformData(StoryboardUtil.STORYBOARD_KEY_TRANS_X, (float) transformVideoFx.getFloatVal(NvsConstants.KEY_CROPPER_TRANS_X));
//            cutData.putTransformData(StoryboardUtil.STORYBOARD_KEY_TRANS_Y, (float) -transformVideoFx.getFloatVal(NvsConstants.KEY_CROPPER_TRANS_Y));
//            cutData.putTransformData(STORYBOARD_KEY_SCALE_X, (float) transformVideoFx.getFloatVal(NvsConstants.KEY_CROPPER_SCALE_X));
//            cutData.putTransformData(STORYBOARD_KEY_SCALE_Y, (float) transformVideoFx.getFloatVal(NvsConstants.KEY_CROPPER_SCALE_Y));
//            cutData.putTransformData(STORYBOARD_KEY_ROTATION_Z, (float) -transformVideoFx.getFloatVal(NvsConstants.KEY_CROPPER_ROTATION));
//            cutData.setIsOldData(false);
//            float cropperAssetAspectRatio = cutData.getRatio();
//            String cropperValue = cutData.getRatioValue() + "";
//            if ("free".equals(cropperValue)) {
//                cutData.setRatio(NvsConstants.AspectRatio.AspectRatio_NoFitRatio);
//            } else {
//                cutData.setRatio(CommonData.AspectRatio.getAspect(cropperAssetAspectRatio));
//            }
//            cutData.setRatioValue(cropperAssetAspectRatio);
//            return cutData;
//        }
//        return new CutData();
    }

    private void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mVideoFragment = CutVideoFragment.newInstance(0L);
        mVideoFragment.setCutData(mCutData);
        mVideoFragment.setTimeLine(mTimeline);
        mTitleBar.post(new Runnable() {
            @Override
            public void run() {
                mVideoFragment.initData();
                seekTimeline(0);
            }
        });
        fragmentManager.beginTransaction().add(R.id.spaceLayout, mVideoFragment).commit();
        fragmentManager.beginTransaction().show(mVideoFragment);
        mVideoFragment.setOnCutRectChangeListener(new CutVideoFragment.OnCutRectChangedListener() {
            @Override
            public void onScaleAndRotate(float scale, float degree) {
                mCuttingMenuView.setProgress(degree);
            }

            @Override
            public void onSizeChanged(Point size) {
                handleCutData(size);
                mVideoFragment.setCutData(mCutData);
            }
        });
    }


    @Override
    protected void initListener() {
        mCuttingMenuView.setOnSeekBarListener(new MYSeekBarView.OnSeekBarListener() {
            @Override
            public void onStopTrackingTouch(int progress, String name) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoFragment.rotateVideo(progress - 45);
                }
            }
        });
        mCuttingMenuView.setOnConfrimListener(new CuttingMenuView.OnConfirmListener() {
            @Override
            public void onConfirm() {
                Map<String, Float> transFromData = mVideoFragment.getTransFromData(mOriginalTimelineWidth, mOriginalTimelineHeight);
                parseTransToTimeline(mOriginalTimelineWidth, mOriginalTimelineHeight, mVideoClip.getFilePath(),
                        mVideoFragment.getRectViewSize(), transFromData);

                NvsPanAndScan panAndScan = mVideoClip.getPanAndScan();
                mClipArrayList.get(mCurClipIndex).setPan(panAndScan.pan);
                mClipArrayList.get(mCurClipIndex).setScan(panAndScan.scan);
                mCutData.setTransformData(transFromData);
                mCutData.setRatio(mVideoFragment.getRatio());
                mCutData.setRatioValue(mVideoFragment.getRatioValue());
                mCutData.setmRegionData(mVideoFragment.getRegionData(new float[]{1F, 1F}));
                mClipArrayList.get(mCurClipIndex).setCropInfo(mCutData);
                BackupData.instance().setClipInfoData(mClipArrayList);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });
        mCuttingMenuView.setOnRatioSelectListener(new CuttingMenuView.OnRatioSelectListener() {
            @Override
            public void onItemClicked(int ratio) {
                mVideoFragment.changeCutRectView(ratio);
            }

            @Override
            public void onReset() {
                mScaleX = 1;
                mScaleY = 1;
                mRotateAngle = 0;
                rotateClip();
                mVideoFragment.reset();
                mCuttingMenuView.setProgress(0);
                mVideoClip.setPanAndScan(0.0f, 0.0f);
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
                mClipArrayList.get(mCurClipIndex).setScaleX(mScaleX);
                mClipArrayList.get(mCurClipIndex).setScaleY(mScaleY);
                mClipArrayList.get(mCurClipIndex).setRotateAngle(mRotateAngle);
                if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }

            @Override
            public void onVertical() {
                mScaleY = mScaleY > 0 ? -1 : 1;
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
                mClipArrayList.get(mCurClipIndex).setScaleY(mScaleY);
                if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }

            @Override
            public void onRotation() {
                int rotateAngle = mVideoClip.getExtraVideoRotation();
                switch (rotateAngle) {
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_90;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_90:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_180;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_180:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_270;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_270:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0;
                        break;
                    default:
                        break;
                }
                rotateClip();
                mClipArrayList.get(mCurClipIndex).setRotateAngle(mRotateAngle);
                if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }

            @Override
            public void onHorizontal() {
                mScaleX = mScaleX > 0 ? -1 : 1;
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
                mClipArrayList.get(mCurClipIndex).setScaleX(mScaleX);
                if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    /**
     * 旋转VideoClip
     * rotate the VideoClip
     */
    private void rotateClip() {
        mVideoClip.setExtraVideoRotation(mRotateAngle);
    }

    private void adjustClip() {
        rotateClip();
        int fxCount = mVideoClip.getFxCount();
        for (int index = 0; index < fxCount; ++index) {
            NvsVideoFx videoFx = mVideoClip.getFxByIndex(index);
            if (videoFx == null) {
                continue;
            }
            if (videoFx.getBuiltinVideoFxName().compareTo(Constants.FX_TRANSFORM_2D) == 0) {
                mVideoFx = videoFx;
                break;
            }
        }
        if (mVideoFx == null) {
            mVideoFx = mVideoClip.appendRawBuiltinFx(Constants.FX_TRANSFORM_2D);
        }
        if (mVideoFx == null) {
            return;
        }
        if (mScaleX >= -1) {
            mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
        }
        if (mScaleY >= -1) {
            mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
        }
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, 0);
    }

    /**
     * 转换transform 数据为timeline范围内的transfrom
     * Convert the transform data to the transform within the timeline range
     *
     * @param timelineWidth  时间线的宽
     * @param timelineHeight 时间线的高
     * @param filePath       文件路径
     * @param rectSize       裁剪区域的宽高
     * @param transFormData  转换数据
     * @return
     */
    public Map<String, Float> parseTransToTimeline(int timelineWidth, int timelineHeight, String filePath, int[] rectSize, Map<String, Float> transFormData) {
        float transXInView = transFormData.get(StoryboardUtil.STORYBOARD_KEY_TRANS_X);
        float transYInView = transFormData.get(StoryboardUtil.STORYBOARD_KEY_TRANS_Y);
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(filePath);
        int videoStreamRotation = avFileInfo.getVideoStreamRotation(0);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int height;
        int width;
        if (videoStreamRotation % 2 == 0) {
            height = dimension.height;
            width = dimension.width;
        } else {
            width = dimension.height;
            height = dimension.width;
        }

        float fileRatio = width * 1F / height;
        float timelineRatio = timelineWidth * 1F / timelineHeight;

        float fileWidthInTimeline;
        float fileHeightInTimeline;
        if (fileRatio > timelineRatio) {
            //文件宽对齐 File width alignment
            fileWidthInTimeline = timelineWidth;
            fileHeightInTimeline = fileWidthInTimeline / fileRatio;
        } else {//高对齐 High alignment
            fileHeightInTimeline = timelineHeight;
            fileWidthInTimeline = fileHeightInTimeline * fileRatio;
        }
        float rectWidthInTimeline;
        float rectHeightInTimeline;
        float rectRatio = rectSize[0] * 1F / rectSize[1];
        if (rectRatio > fileRatio) {
            //裁剪区域宽对齐 Cropped area width alignment
            rectWidthInTimeline = fileWidthInTimeline;
            rectHeightInTimeline = rectWidthInTimeline / rectRatio;
        } else {
            rectHeightInTimeline = fileHeightInTimeline;
            rectWidthInTimeline = rectHeightInTimeline * rectRatio;
        }

        float transXInTimeline = transXInView / rectSize[0] * rectWidthInTimeline;
        float transYInTimeline = transYInView / rectSize[1] * rectHeightInTimeline;
        //Mask Generator方案需要归一化
        transFormData.put(StoryboardUtil.STORYBOARD_KEY_TRANS_X, transXInTimeline / fileWidthInTimeline * 2);
        transFormData.put(StoryboardUtil.STORYBOARD_KEY_TRANS_Y, -transYInTimeline / fileHeightInTimeline * 2);//Timeline 坐标轴反向
        return transFormData;
    }

    private void handleCutData(Point size) {
        if ((mCutData != null) && (!mCutData.isOldData())) {//需要考虑老的草稿数据 Old draft data needs to be considered
            Map<String, Float> transFromData = parseTransToView(mOriginalTimelineWidth, mOriginalTimelineHeight, mVideoClip.getFilePath(),
                    new int[]{size.x, size.y}, mCutData.getTransformData());
            mCutData.setTransformData(transFromData);
        }
    }

    /**
     * 转换transform 数据为view范围内的transfrom
     * Convert transform data to transfrom within view scope
     *
     * @param timelineWidth  时间线的宽
     * @param timelineHeight 时间线的高
     * @param filePath       文件路径
     * @param rectSize       裁剪区域的宽高
     * @param transFormData  转换数据
     * @return view坐标系下的转换数据
     */
    private Map<String, Float> parseTransToView(int timelineWidth, int timelineHeight, String filePath,
                                                int[] rectSize, Map<String, Float> transFormData) {
        float transXInTimeline = transFormData.get(StoryboardUtil.STORYBOARD_KEY_TRANS_X);
        float transYInTimeline = transFormData.get(StoryboardUtil.STORYBOARD_KEY_TRANS_Y);
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(filePath);
        int videoStreamRotation = avFileInfo.getVideoStreamRotation(0);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int height;
        int width;
        if (videoStreamRotation % 2 == 0) {
            height = dimension.height;
            width = dimension.width;
        } else {
            width = dimension.height;
            height = dimension.width;
        }

        float fileRatio = width * 1F / height;
        float timelineRatio = timelineWidth * 1F / timelineHeight;

        float fileWidthInTimeline;
        float fileHeightInTimeline;
        if (fileRatio > timelineRatio) {//文件宽对齐 File width alignment
            fileWidthInTimeline = timelineWidth;
            fileHeightInTimeline = fileWidthInTimeline / fileRatio;
        } else {//高对齐 High alignment
            fileHeightInTimeline = timelineHeight;
            fileWidthInTimeline = fileHeightInTimeline * fileRatio;
        }
        float rectWidthInTimeline;
        float rectHeightInTimeline;
        float rectRatio = rectSize[0] * 1F / rectSize[1];
        if (rectRatio > fileRatio) {//裁剪区域宽对齐 Cropped area width alignment
            rectWidthInTimeline = fileWidthInTimeline;
            rectHeightInTimeline = rectWidthInTimeline / rectRatio;
        } else {
            rectHeightInTimeline = fileHeightInTimeline;
            rectWidthInTimeline = rectHeightInTimeline * rectRatio;
        }

        float transXInView = transXInTimeline / rectWidthInTimeline * rectSize[0];
        float transYInView = transYInTimeline / rectHeightInTimeline * rectSize[1];
        transFormData.put(StoryboardUtil.STORYBOARD_KEY_TRANS_X, transXInView * fileWidthInTimeline / 2F);
        transFormData.put(StoryboardUtil.STORYBOARD_KEY_TRANS_Y, transYInView * fileHeightInTimeline / 2F);
        return transFormData;
    }

    @Override
    public void onClick(View v) {

    }
}
