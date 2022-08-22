package com.meishe.fxplugin;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTrackVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.fxplugin.base.BasePermissionActivity;
import com.meishe.fxplugin.data.BackupData;
import com.meishe.fxplugin.data.ClipInfo;
import com.meishe.fxplugin.data.TimelineData;
import com.meishe.fxplugin.utils.Logger;
import com.meishe.fxplugin.utils.MediaConstant;
import com.meishe.fxplugin.utils.ScreenUtils;
import com.meishe.fxplugin.utils.TimelineUtil;
import com.meishe.fxplugin.utils.Util;
import com.meishe.fxplugin.view.PreviewFragment;

import java.util.ArrayList;
import java.util.List;

public class MediaEditActivity extends BasePermissionActivity {

    private static final String TAG = "MediaEditActivity";
    private PreviewFragment mPreviewFragment;
    private RadioButton mSaturationBtn, mStoryboardBtn;
    private CheckBox mCircleInBtn;
    private SeekBar mSeekBar;

    private NvsTimeline mTimeline;
    private NvsTrackVideoFx mSaturationVideoFx, mStoryboardVideoFx;
    private ArrayList<ClipInfo> mClipInfoArray = new ArrayList<>();

    private int mMediaType = MediaConstant.VIDEO;
    private boolean isVideoMedia = true;

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        mStreamingContext.stop();
        return R.layout.activity_media_edit;
    }

    @Override
    protected void initViews() {
        Intent intent = getIntent( );
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mMediaType = bundle.getInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                isVideoMedia = (mMediaType == MediaConstant.VIDEO);
                Logger.e(TAG,"mgj isVideoMedia -->" + isVideoMedia  + "  isVideoMedia:" +  isVideoMedia );
            }
        }
        mSaturationBtn = findViewById(R.id.rd_saturation);
        mCircleInBtn = findViewById(R.id.rd_circle_in);
        mStoryboardBtn = findViewById(R.id.rd_storyboard);
        mSeekBar = findViewById(R.id.seek_bar);
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void initData() {
        mTimeline = createTimeline();
        if(mTimeline == null)
            return;

        mClipInfoArray = TimelineData.instance().cloneClipInfoData();
        BackupData.instance().setClipIndex(0);
        BackupData.instance().setClipInfoData(mClipInfoArray);
        initVideoFragment();
    }

    private NvsTimeline createTimeline(){
        int imageWidth = 720;
        int imageHeight = 1280;
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        if(videoClipArray.size() > 0) {
            NvsAVFileInfo fileInfo = mStreamingContext.getAVFileInfo(videoClipArray.get(0).getFilePath());
            if(fileInfo == null){
                Logger.e(TAG,"import file Info is null" );
                return null;
            }
            NvsSize size = fileInfo.getVideoStreamDimension(0);
            double scale = 1;
            int minValue = Math.min(size.width, size.height);
            if(minValue > 2160) {
                scale = minValue / 2160.00d;
            }
            Logger.d(TAG,"mgj size -->" + size.width + "  " + size.height + " scale:" + scale + " rotation:" + fileInfo.getVideoStreamRotation(0));
            int tempWidth = size.width;
            int tempHeight = size.height;
            int rotation = fileInfo.getVideoStreamRotation(0);
            if( rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90 || fileInfo.getVideoStreamRotation(0) == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
                tempWidth = size.height;
                tempHeight = size.width;
            }
            imageWidth = TimelineUtil.alignedData(tempWidth, 4, scale);
            imageHeight = TimelineUtil.alignedData(tempHeight, 2, scale);
            BackupData.instance().setImageDimension(new Point(imageWidth, imageHeight));
        }
        NvsVideoResolution videoResolution = new NvsVideoResolution();
        videoResolution.imageWidth = imageWidth;
        videoResolution.imageHeight = imageHeight;
        Logger.d(TAG,"mgj videoResolution.imageWidth -->" + videoResolution.imageWidth + "  " + videoResolution.imageHeight);
        NvsTimeline timeline = TimelineUtil.newTimeline(videoResolution);

        if(timeline == null)
            return null;
        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null)
            return null;
        for (int i = 0;i < videoClipArray.size();i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            NvsVideoClip videoClip = videoTrack.appendClip(clipInfo.getFilePath());


            if(videoClip.getTrimOut() > 5000000) {
                videoClip.changeTrimOutPoint(5000000, true);
            }
            videoTrack.setBuiltinTransition(i, null);
            if (videoClip == null){
                Logger.e(TAG,"failed to add clip -->" + clipInfo.getFilePath());
            }
        }
        return timeline;
    }

    private void initVideoFragment() {
        mPreviewFragment = new PreviewFragment();
        mPreviewFragment.setFragmentLoadFinisedListener(new PreviewFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                long stamp = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                mPreviewFragment.seekTimeline(stamp,0);
            }
        });
        mPreviewFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", 0);
        bundle.putInt("bottomHeight", ScreenUtils.dip2px(MediaEditActivity.this, 200));
        bundle.putBoolean("playBarVisible",true);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mPreviewFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mPreviewFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mPreviewFragment);
    }


    /**
     * 清空数据
     * Clear data
     */
    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
    }

    private void removeTimeline(){
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    @Override
    protected void initListener() {

        mSaturationBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mStoryboardBtn.setChecked(false);
                    mSeekBar.setVisibility(View.VISIBLE);
                    setSaturationVideoFx(true);
                    mSeekBar.setProgress(0);
                } else {
                    mSeekBar.setVisibility(View.INVISIBLE);
                    setSaturationVideoFx(false);
                }
            }
        });
        mCircleInBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mStoryboardBtn.setChecked(false);
                    setCircleInTransition(true);
                } else {
                    setCircleInTransition(false);
                }
            }
        });
        mStoryboardBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSaturationBtn.setChecked(false);
                    setStoryboardVideoFx(true);
                } else {
                    setStoryboardVideoFx(false);
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mSaturationVideoFx != null) {
                    float value = progress / 100.00f;
                    mSaturationVideoFx.setFloatVal("Saturation Level", value);
                    mStreamingContext.seekTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSaturationVideoFx(boolean isSetSaturationVideoFx) {
        if(mTimeline == null) {
            Logger.e("timeline is null");
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            Logger.e("videoTrack is null");
            return;
        }
        if (isSetSaturationVideoFx) {
            mSaturationVideoFx = videoTrack.addBuiltinTrackVideoFx(0, mTimeline.getDuration(), "plugin:com.meishesdk.saturation");
            if(mSaturationVideoFx == null) {
                Logger.e("creat saturation video fx failed");
                return;
            }
            mSaturationVideoFx.setFloatVal("Saturation Level", 0);
        } else {
            videoTrack.removeTrackVideoFx(mSaturationVideoFx);
        }
        mStreamingContext.seekTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
    }

    private void setCircleInTransition(boolean isSetCircleInTransition) {
        if(mTimeline == null) {
            Logger.e("timeline is null");
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            Logger.e("videoTrack is null");
            return;
        }
        for (int i = 0; i < videoTrack.getClipCount() ; i++) {
            videoTrack.setBuiltinTransition(i, isSetCircleInTransition ? "plugin:com.meishesdk.circleIn" : null);
        }
    }

    private  void setStoryboardVideoFx(boolean isSetStoryboardVideoFx) {
        if(mTimeline == null) {
            Logger.e("timeline is null");
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            Logger.e("videoTrack is null");
            return;
        }
        if (isSetStoryboardVideoFx) {
            mStoryboardVideoFx = videoTrack.addBuiltinTrackVideoFx(0, mTimeline.getDuration(), "Storyboard");
            if(mStoryboardVideoFx == null) {
                Logger.e("creat storyboard video fx failed");
                return;
            }
            mStoryboardVideoFx.setStringVal("Description File", "assets:/test-plugin.xml");
        } else {
            videoTrack.removeTrackVideoFx(mStoryboardVideoFx);
        }
        mStreamingContext.seekTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeline != null) {
            if (mStreamingContext != null) {
                mStreamingContext.removeTimeline(mTimeline);
                mStreamingContext.clearCachedResources(true);
            }
            mTimeline = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ========================" + mStreamingContext.getTimelineCurrentPosition(mTimeline));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStreamingContext.stop();
    }
}
