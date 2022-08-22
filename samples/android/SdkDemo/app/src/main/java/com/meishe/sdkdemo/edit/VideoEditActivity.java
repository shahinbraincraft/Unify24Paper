package com.meishe.sdkdemo.edit;

import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.modulemakeupcompose.MakeupManager;
import com.meishe.modulemakeupcompose.makeup.BeautyFxArgs;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.Caption.CaptionActivity;
import com.meishe.sdkdemo.edit.adapter.AssetRecyclerViewAdapter;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.anim.AnimationActivity;
import com.meishe.sdkdemo.edit.animatesticker.AnimatedStickerActivity;
import com.meishe.sdkdemo.edit.background.BackgroundActivity;
import com.meishe.sdkdemo.edit.clipEdit.EditActivity;
import com.meishe.sdkdemo.edit.compoundcaption.CompoundCaptionActivity;
import com.meishe.sdkdemo.edit.createPic.CreatePicActivity;
import com.meishe.sdkdemo.edit.data.AssetInfoDescription;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.data.BitmapData;
import com.meishe.sdkdemo.edit.filter.FilterActivity;
import com.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.makeup.BeautyMakeupActivity;
import com.meishe.sdkdemo.edit.mask.MaskActivity;
import com.meishe.sdkdemo.edit.music.MusicActivity;
import com.meishe.sdkdemo.edit.record.RecordActivity;
import com.meishe.sdkdemo.edit.theme.ThemeActivity;
import com.meishe.sdkdemo.edit.transition.TransitionActivity;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.edit.watermark.WaterMarkActivity;
import com.meishe.sdkdemo.edit.watermark.WaterMarkUtil;
import com.meishe.sdkdemo.interfaces.TipsButtonClickListener;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.NumberUtils;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.meishe.sdkdemo.utils.dataInfo.RecordAudioInfo;
import com.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.utils.dataInfo.VideoFx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑Activity
 * @Description :VideoEditActivity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class VideoEditActivity extends BaseActivity {
    private static final String TAG = "VideoEditActivity";
    public static final int REQUESTRESULT_THEME = 1001;
    public static final int REQUESTRESULT_EDIT = 1002;
    public static final int REQUESTRESULT_FILTER = 1003;
    public static final int REQUESTRESULT_STICKER = 1004;
    public static final int REQUESTRESULT_CAPTION = 1005;
    public static final int REQUESTRESULT_TRANSITION = 1006;
    public static final int REQUESTRESULT_MUSIC = 1007;
    public static final int REQUESTRESULT_RECORD = 1008;
    public static final int REQUESTRESULT_WATERMARK = 1009;
    public static final int REQUESTRESULT_COMPOUND_CAPTION = 1010;
    public static final int REQUESTRESULT_ANIMATION = 1011;
    public static final int REQUESTRESULT_MASK = 1013;
    public static final int REQUESTRESULT_BACKGROUND = 1012;
    public static final int REQUESTRESULT_CREATE_PIC = 1013;
    public static final int REQUESTRESULT_MAKEUP = 1014;
    private CustomTitleBar mTitleBar;

    private RelativeLayout mBottomLayout;
    private RecyclerView mAssetRecycleView;
    private AssetRecyclerViewAdapter mAssetRecycleAdapter;
    private ArrayList<AssetInfoDescription> mArrayAssetInfo;
    private LinearLayout mVolumeUpLayout;
    private SeekBar mVideoVoiceSeekBar;
    private SeekBar mMusicVoiceSeekBar;
    private SeekBar mDubbingSeekBarSeekBar;
    private TextView mVideoVoiceSeekBarValue;
    private TextView mMusicVoiceSeekBarValue;
    private TextView mDubbingSeekBarSeekBarValue;
    private ImageView mSetVoiceFinish;
    private RelativeLayout mCompilePage;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicTrack;
    private NvsAudioTrack mRecordAudioTrack;
    private VideoFragment mVideoFragment;
    private CompileVideoFragment mCompileVideoFragment;
    private boolean m_waitFlag = false;
    private long mThemeClipDuration;

    int[] videoEditImageId = {
            R.mipmap.icon_edit_theme,
            R.mipmap.icon_edit_edit,
            R.mipmap.icon_edit_filter,
            R.mipmap.icon_edit_sticker,
            R.mipmap.icon_edit_animation,
            R.mipmap.icon_edit_mask,
            R.mipmap.icon_edit_caption,
            R.mipmap.icon_compound_caption,
            R.mipmap.icon_edit_background,
            R.mipmap.icon_watermark,
            R.mipmap.icon_edit_transition,
            R.mipmap.icon_edit_music,
            R.mipmap.icon_edit_voice,
            R.mipmap.icon_edit_create_pic,
            R.mipmap.icon_edit_makeup
    };


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_video_edit;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAssetRecycleView = (RecyclerView) findViewById(R.id.assetRecycleList);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mVolumeUpLayout = (LinearLayout) findViewById(R.id.volumeUpLayout);
        mVideoVoiceSeekBar = (SeekBar) findViewById(R.id.videoVoiceSeekBar);
        mMusicVoiceSeekBar = (SeekBar) findViewById(R.id.musicVoiceSeekBar);
        mDubbingSeekBarSeekBar = (SeekBar) findViewById(R.id.dubbingSeekBar);
        mVideoVoiceSeekBarValue = (TextView) findViewById(R.id.videoVoiceSeekBarValue);
        mMusicVoiceSeekBarValue = (TextView) findViewById(R.id.musicVoiceSeekBarValue);
        mDubbingSeekBarSeekBarValue = (TextView) findViewById(R.id.dubbingSeekBarValue);
        mSetVoiceFinish = (ImageView) findViewById(R.id.finish);
        mCompilePage = (RelativeLayout) findViewById(R.id.compilePage);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.videoEdit);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }

        //系统自带的播放器，播放不了，测试咱们sdk是否支持 测试结果是支持的，这块暂时先保留
//        NvsAudioTrack nvsAudioTrack = mTimeline.appendAudioTrack();
//        nvsAudioTrack.appendClip("/storage/emulated/0/Download/12.wma");
//
//        NvsAVFileInfo avInfoFromFile = NvsStreamingContext.getAVInfoFromFile("/storage/emulated/0/Download/12.wma", 0);
//        long duration1 = avInfoFromFile.getDuration();
//        Log.e("lpf","duration1:"+duration1);

        initVideoFragment();
        initCompileVideoFragment();
        initAssetInfo();
        initAssetRecycleAdapter();
        initVoiceSeekBar();
        loadVideoClipFailTips();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
        if (mTimeline != null) {
            mMusicTrack = mTimeline.getAudioTrackByIndex(0);
            mRecordAudioTrack = mTimeline.getAudioTrackByIndex(1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        clearData();
        AppManager.getInstance().finishActivity();
    }

    private void loadVideoClipFailTips() {
        /*
         * 导入视频无效，提示
         * The imported video is invalid
         * */
        if (mTimeline == null || (mTimeline.getDuration() <= 0)) {
            String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
            Util.showDialog(VideoEditActivity.this, versionName[0], versionName[1], new TipsButtonClickListener() {
                @Override
                public void onTipsButtoClick(View view) {
                    removeTimeline();
                    AppManager.getInstance().finishActivity();
                }
            });
        }
    }

    /**
     * 清空数据
     * Clear data
     */
    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
        BitmapData.instance().clear();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    /**
     * 初始化声音调节view
     * Initialize the sound adjustment view
     */
    private void initVoiceSeekBar() {
        mVideoVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mMusicVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mDubbingSeekBarSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        if (mVideoTrack == null) {
            return;
        }
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateVideoVoiceSeekBar(volumeVal);
        updateMusicVoiceSeekBar(volumeVal);
        updateDubbingVoiceSeekBar(volumeVal);
    }

    private void updateVideoVoiceSeekBar(int volumeVal) {
        mVideoVoiceSeekBar.setProgress(volumeVal);
        mVideoVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateMusicVoiceSeekBar(int volumeVal) {
        mMusicVoiceSeekBar.setProgress(volumeVal);
        mMusicVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateDubbingVoiceSeekBar(int volumeVal) {
        mDubbingSeekBarSeekBar.setProgress(volumeVal);
        mDubbingSeekBarSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void setVideoVoice(int voiceVal) {
        if (mVideoTrack == null) {
            return;
        }
        updateVideoVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mVideoTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setOriginVideoVolume(volumeVal);
    }

    private void setMusicVoice(int voiceVal) {
        if (mMusicTrack == null) {
            return;
        }
        updateMusicVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mMusicTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setMusicVolume(volumeVal);
    }

    /**
     * 设置配音音量
     * setDubbingVoice
     *
     * @param voiceVal
     */
    private void setDubbingVoice(int voiceVal) {
        if (mRecordAudioTrack == null) {
            return;
        }
        updateDubbingVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mRecordAudioTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setRecordVolume(volumeVal);
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
        mVideoFragment.setAutoPlay(true);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", true);
        bundle.putBoolean("voiceButtonVisible", true);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.video_layout, mVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
        mVideoFragment.setPlayFlag(NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
                if (timeline.getDuration() - mStreamingContext.getTimelineCurrentPosition(mTimeline) <= 40000) {
                    mVideoFragment.updateCurPlayTime(0);
                    mVideoFragment.seekTimeline(0, 0);
                }
            }

            @Override
            public void playStopped(NvsTimeline timeline) {

            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {

            }

            @Override
            public void streamingEngineStateChanged(int state) {

            }
        });

    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction().add(R.id.compilePage, mCompileVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void initAssetInfo() {
        mArrayAssetInfo = new ArrayList<>();
        String[] assetName = getResources().getStringArray(R.array.videoEdit);
        for (int i = 0; i < assetName.length; i++) {
            mArrayAssetInfo.add(new AssetInfoDescription(assetName[i], videoEditImageId[i]));
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mAssetRecycleView.setLayoutManager(layoutManager);
        mAssetRecycleAdapter = new AssetRecyclerViewAdapter(VideoEditActivity.this);
        mAssetRecycleAdapter.updateData(mArrayAssetInfo);
        mAssetRecycleView.setAdapter(mAssetRecycleAdapter);
        mAssetRecycleView.addItemDecoration(new SpaceItemDecoration(8, 8));
        mAssetRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (m_waitFlag) {
                    return;
                }
                mStreamingContext.stop();
                String tag = (String) view.getTag();
                if (tag.equals(getStringResourse(R.string.theme))) {
                    onItemClickToActivity(ThemeActivity.class, VideoEditActivity.REQUESTRESULT_THEME);
                } else if (tag.equals(getStringResourse(R.string.edit))) {
                    onItemClickToActivity(EditActivity.class, VideoEditActivity.REQUESTRESULT_EDIT);
                } else if (tag.equals(getStringResourse(R.string.filter))) {
                    onItemClickToActivity(FilterActivity.class, VideoEditActivity.REQUESTRESULT_FILTER);
                } else if (tag.equals(getStringResourse(R.string.animatedSticker))) {
                    onItemClickToActivity(AnimatedStickerActivity.class, VideoEditActivity.REQUESTRESULT_STICKER);
                } else if (tag.equals(getStringResourse(R.string.animation))) {
                    onItemClickToActivity(AnimationActivity.class, VideoEditActivity.REQUESTRESULT_ANIMATION);
                } else if (tag.equals(getStringResourse(R.string.mask))) {
                    onItemClickToActivity(MaskActivity.class, VideoEditActivity.REQUESTRESULT_MASK);
                } else if (tag.equals(getStringResourse(R.string.caption))) {
                    onItemClickToActivity(CaptionActivity.class, VideoEditActivity.REQUESTRESULT_CAPTION);
                } else if (tag.equals(getStringResourse(R.string.comcaption))) {
                    onItemClickToActivity(CompoundCaptionActivity.class, VideoEditActivity.REQUESTRESULT_COMPOUND_CAPTION);
                } else if (tag.equals(getStringResourse(R.string.background))) {
                    onItemClickToActivity(BackgroundActivity.class, VideoEditActivity.REQUESTRESULT_BACKGROUND);
                } else if (tag.equals(getStringResourse(R.string.watermark))) {
                    onItemClickToActivity(WaterMarkActivity.class, VideoEditActivity.REQUESTRESULT_WATERMARK);
                } else if (tag.equals(getStringResourse(R.string.createPic))) {
                    onItemClickToActivity(CreatePicActivity.class, VideoEditActivity.REQUESTRESULT_CREATE_PIC);
                } else if (tag.equals(getStringResourse(R.string.makeup))) {
                    onItemClickToActivity(BeautyMakeupActivity.class, VideoEditActivity.REQUESTRESULT_MAKEUP);
                } else if (tag.equals(getStringResourse(R.string.transition))) {
                    if (mTimeline != null) {
                        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                        if (videoTrack != null) {
                            int clipCount = videoTrack.getClipCount();
                            if (clipCount <= 1) {
                                String[] transitionTipsInfo = getResources().getStringArray(R.array.transition_tips);
                                Util.showDialog(VideoEditActivity.this, transitionTipsInfo[0], transitionTipsInfo[1]);
                                return;
                            }
                        }
                    }
                    onItemClickToActivity(TransitionActivity.class, VideoEditActivity.REQUESTRESULT_TRANSITION);
                } else if (tag.equals(getStringResourse(R.string.music))) {
                    onItemClickToActivity(MusicActivity.class, VideoEditActivity.REQUESTRESULT_MUSIC);
                } else if (tag.equals(getStringResourse(R.string.dub))) {
                    onItemClickToActivity(RecordActivity.class, VideoEditActivity.REQUESTRESULT_RECORD);
                } else {
                    String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                    Util.showDialog(VideoEditActivity.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
                }
            }
        });
    }

    private void onItemClickToActivity(Class<? extends Activity> cls, int requstcode) {
        m_waitFlag = true;
        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), cls, null, requstcode);
    }

    private String getStringResourse(int id) {
        return getApplicationContext().getResources().getString(id);
    }

    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
                clearData();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                mCompilePage.setVisibility(View.VISIBLE);
                mCompileVideoFragment.compileVideo();
            }
        });
        mVideoVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVideoVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMusicVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setMusicVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDubbingSeekBarSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setDubbingVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSetVoiceFinish.setOnClickListener(this);
        mCompilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (mCompileVideoFragment != null) {
            mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
                @Override
                public void compileProgress(NvsTimeline timeline, int progress) {

                }

                @Override
                public void compileFinished(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileFailed(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileVideoCancel() {
                    mCompilePage.setVisibility(View.GONE);
                }
            });
        }

        if (mVideoFragment != null) {
            mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
                @Override
                public void onVideoVolume() {
                    mVolumeUpLayout.setVisibility(View.VISIBLE);
                }
            });
        }

        mVolumeUpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.finish:
                mVolumeUpLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUESTRESULT_THEME:
                String themeId = TimelineData.instance().getThemeData();

                NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
                int clipCount = videoTrackByIndex.getClipCount();
                TimelineUtil.applyTheme(mTimeline, themeId);
                int afclipCount = videoTrackByIndex.getClipCount();
                mThemeClipDuration = 0;
                if (afclipCount > clipCount) {
                    //存在片头主题
                    NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(0);
                    mThemeClipDuration = clipByIndex.getOutPoint() - clipByIndex.getInPoint();
                }

                /*
                 * 重新添加字幕，防止某些主题会删除字幕
                 * Add subtitles again to prevent some topics from deleting them
                 * */
                updateCaption(mThemeClipDuration);
                mVideoFragment.playVideoButtonClick();
                break;
            case REQUESTRESULT_EDIT:
                TimelineUtil.reBuildVideoTrack(mTimeline);
                //设置调节的数据,从clipInfo中取出调节的值
                //有关顺序问题，由于操作蒙版时，没有把调整的效果代入，所以蒙版要加在调整裁剪之前，同理背景
                TimelineUtil.buildColorAdjustInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                TimelineUtil.buildAdjustCutInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                TimelineUtil.buildTimelineBackground(mTimeline, TimelineData.instance().getClipInfoData());
                TimelineUtil.buildTimelineMaskClipInfo(mTimeline, TimelineData.instance().getClipInfoData());
                //设置动画
                TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getClipInfoData());
                mVideoFragment.refreshLiveWindowFrame();
                break;
            case REQUESTRESULT_FILTER:
                VideoClipFxInfo videoClipFxInfo = TimelineData.instance().getVideoClipFxData();
                TimelineUtil.buildTimelineFilter(mTimeline, videoClipFxInfo);
                break;
            case REQUESTRESULT_STICKER:
                ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
                TimelineUtil.setSticker(mTimeline, stickerArray);
                break;
            case REQUESTRESULT_CAPTION:
                updateCaption(mThemeClipDuration);
                break;
            case REQUESTRESULT_COMPOUND_CAPTION:
                updateCompoundCaption();
                break;
            case REQUESTRESULT_TRANSITION:
                ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();
                if ((transitionInfoArray != null) && !transitionInfoArray.isEmpty()) {
                    TimelineUtil.setTransition(mTimeline, transitionInfoArray);
                }
                break;
            case REQUESTRESULT_MUSIC:
                List<MusicInfo> musicInfos = TimelineData.instance().getMusicData();
                TimelineUtil.buildTimelineMusic(mTimeline, musicInfos);
                break;
            case REQUESTRESULT_RECORD:
                Logger.e(TAG, "录音界面");
                ArrayList<RecordAudioInfo> audioInfos = TimelineData.instance().getRecordAudioData();
                TimelineUtil.buildTimelineRecordAudio(mTimeline, audioInfos);
                break;
            case REQUESTRESULT_WATERMARK:
                Logger.e(TAG, "水印界面");
                TimelineUtil.checkAndDeleteExitFX(mTimeline);
                boolean cleanWaterMark = data.getBooleanExtra(WaterMarkActivity.WATER_CLEAN, true);
                if (cleanWaterMark) {
                    mTimeline.deleteWatermark();
                } else {
                    WaterMarkUtil.setWaterMark(mTimeline, TimelineData.instance().getWaterMarkData());
                }
                //添加临时解决水印中效果不能去除问题，导致问题的原因大概率为每次操作的都不是同一个timeline
                boolean hasEffect = data.getBooleanExtra(WaterMarkActivity.EFFECT_CLEAN, true);
                if (!hasEffect) {
                    NvsTimelineVideoFx lastFx = mTimeline.getLastTimelineVideoFx();
                    while (lastFx != null) {
                        String fxName = lastFx.getBuiltinTimelineVideoFxName();
                        if (TextUtils.equals(fxName, "Mosaic")
                                || TextUtils.equals(fxName, "Gaussian Blur")) {
                            mTimeline.removeTimelineVideoFx(lastFx);
                            break;
                        }
                        lastFx = mTimeline.getPrevTimelineVideoFx(lastFx);
                    }
                }
                VideoFx videoFx = TimelineData.instance().getVideoFx();
                mVideoFragment.setEffectByData(videoFx);
                mVideoFragment.refreshLiveWindowFrame();

                break;
            case REQUESTRESULT_ANIMATION:
                //  2020/8/25 设置动画
                TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getClipInfoData());
                break;
            case REQUESTRESULT_MASK:
                // 设置蒙版
                TimelineUtil.buildTimelineMaskClipInfo(mTimeline, TimelineData.instance().getClipInfoData());
                break;
            case REQUESTRESULT_BACKGROUND:
                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                TimelineUtil.buildTimelineBackground(mTimeline, clipInfoData);
                break;

            case REQUESTRESULT_MAKEUP: //添加美妆

                TimelineUtil.reBuildVideoTrack(mTimeline);
                //设置调节的数据,从clipInfo中取出调节的值
                //有关顺序问题，由于操作蒙版时，没有把调整的效果代入，所以蒙版要加在调整裁剪之前，同理背景
                TimelineUtil.buildColorAdjustInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                TimelineUtil.buildAdjustCutInfo(mTimeline, TimelineData.instance().cloneClipInfoData());
                TimelineUtil.buildTimelineBackground(mTimeline, TimelineData.instance().getClipInfoData());
                TimelineUtil.buildTimelineMaskClipInfo(mTimeline, TimelineData.instance().getClipInfoData());
                //设置动画
                TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getClipInfoData());
                mVideoFragment.refreshLiveWindowFrame();
                resetMakeUpData();
//                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
//                int clips = videoTrack.getClipCount();
//                for (int i = 0; i < clips; i++) {
//                    NvsVideoClip clipByIndex = videoTrack.getClipByIndex(i);
//                    if (clipByIndex == null) {
//                        continue;
//                    }
//                    NvsVideoFx nvsVideoFx = TimelineUtil.findVideoFxFromVideoClip(clipByIndex);
//                    if (nvsVideoFx == null) {
//                        continue;
//                    }
//                    int index = nvsVideoFx.getIndex();
//                    clipByIndex.removeFx(index);
//                }

//                Makeup item = MakeupManager.getInstacne().getMakeup();
//
//            if (item != null) {//添加了整妆
//                videoTrack = mTimeline.getVideoTrackByIndex(0);
//                clips = videoTrack.getClipCount();
//                for (int i = 0; i < clips; i++) {
//                    NvsVideoClip clipByIndex = videoTrack.getClipByIndex(i);
//                    if (clipByIndex == null) {
//                        continue;
//                    }
//                    NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(clipByIndex);
//                    if (nvsVideoFx == null) {
//                        continue;
//                    }
//                    nvsVideoFx.setBooleanVal("Beauty Effect", true);   //开启美颜开关，不开的话，如果美妆里边包含美颜效果可能应用不上
//                    nvsVideoFx.setFloatVal("Makeup Intensity", 1.0f);  //设置美妆的强度
//                    nvsVideoFx.setStringVal("Makeup Compound Package Id", item.getUuid());
//                }
//            } else { //单妆或者妆容

//
//                Makeup makeup = MakeupManager.getInstacne().getMakeup();
//                if (makeup != null) {
//                    for (int i = 0; i < clips; i++) {
//                        NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
//                        if (videoClip == null) {
//                            continue;
//                        }
//                        NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(videoClip);
//                        if (nvsVideoFx == null) {
//                            continue;
//                        }
//                        addMakeUp(makeup,videoClip,nvsVideoFx);
//                    }
//                }


//                Map<String, Makeup> simpleMakeupEffect = MakeupManager.getInstacne().getSimpleMakeupEffect();
//                if (simpleMakeupEffect != null && !simpleMakeupEffect.isEmpty()) {
//                    Set<String> types = simpleMakeupEffect.keySet();
//                    for (String type : types) {
//                        Makeup makeupData = simpleMakeupEffect.get(type);
//                        if (makeupData == null) {
//                            continue;
//                        }
//
//                        videoTrack = mTimeline.getVideoTrackByIndex(0);
//                        clips = videoTrack.getClipCount();
//                        for (int i = 0; i < clips; i++) {
//                            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
//                            if (videoClip == null) {
//                                continue;
//                            }
//                            NvsVideoFx nvsVideoFx = TimelineUtil.findOrCrateVideoFxFromVideoClip(videoClip);
//                            if (nvsVideoFx == null) {
//                                continue;
//                            }
//
//                            MakeupEffectContent effectContent = makeupData.getEffectContent();
//                            if (effectContent==null){
//                                continue;
//                            }
//
//                            List<MakeupArgs> makeupArgs = effectContent.getMakeupArgs();
//                            if (makeupArgs != null) {
//                                for (MakeupArgs args : makeupArgs) {
//                                    if (args == null) {
//                                        continue;
//                                    }
//                                    if (nvsVideoFx != null) {
//                                        nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
//                                        nvsVideoFx.setColorVal("Makeup " + args.getType() + " Color", makeupData.getNvsColor());
//                                        nvsVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
//                                        nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
//                                        Log.d("=====", "className:" + args.getClassName() + " value:" + args.getUuid());
//                                    }
//                                }
//                            }
//                            Log.d("=====", "end set makeUp ");
//                        }
//
//                    }
//                }

//                MakeupManager.getInstacne().clearAllData();

                break;
            default:
                break;
        }
        mVideoFragment.updateTotalDurationText();
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


    public static boolean addMakeUp(Makeup makeup, NvsVideoClip videoClip, NvsVideoFx nvsVideoFx) {
        MakeupEffectContent makeupEffectContent = makeup.getEffectContent();
        if (makeupEffectContent == null) {
            return true;
        }
        Log.d("=====", "start set makeUp ");
        //添加效果包中带的美颜
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getBeauty(), false);
        //添加效果包中带的美型
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getShape(), false);
        //添加效果包中带的微整形
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getMicroShape(), true);
        //添加效果包中带的滤镜
        List<FilterArgs> filter = makeupEffectContent.getFilter();
        setFilterContent(videoClip, filter);
        //添加美妆
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
        return false;
    }

    public static boolean addSimpleMakeUp(Makeup makeup, NvsVideoClip videoClip, NvsVideoFx nvsVideoFx) {
        MakeupEffectContent makeupEffectContent = makeup.getEffectContent();
        if (makeupEffectContent == null) {
            return true;
        }
        Log.d("=====", "start set makeUp ");
//        //添加效果包中带的美颜
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getBeauty(), false);
        //添加效果包中带的美型
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getShape(), false);
        //添加效果包中带的微整形
        setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getMicroShape(), true);
        //添加效果包中带的滤镜
        List<FilterArgs> filter = makeupEffectContent.getFilter();
        setFilterContent(videoClip, filter);
        //添加美妆
//        List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
        if (makeup != null) {
            if (nvsVideoFx != null) {
                nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                nvsVideoFx.setColorVal("Makeup " + makeup.getType() + " Color", new NvsColor(0, 0, 0, 0));
                nvsVideoFx.setFloatVal("Makeup " + makeup.getType() + " Intensity", makeup.getIntensity());
                nvsVideoFx.setStringVal(makeup.getClassName(), makeup.getUuid());
                Log.d("=====", "className:" + makeup.getClassName() + " value:" + makeup.getUuid());
            }
        }
        return false;
    }

    private static void setMakeupBeautyArgs(NvsVideoFx nvsVideoFx, List<BeautyFxArgs> shape, boolean microFlag) {
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
            }
        }
    }


    private static void setFilterContent(NvsVideoClip videoClip, List<FilterArgs> filter) {
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
        }
    }

    private static void changeBeautyWhiteMode(NvsVideoFx videoEffect, boolean isOpen,
                                       boolean isExchange) {
        if (videoEffect == null) {
            return;
        }
        if (isExchange) {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
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

    private void updateCaption(long themeDuration) {
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(mTimeline, captionArray, themeDuration);
    }

    private void updateCompoundCaption() {
        ArrayList<CompoundCaptionInfo> captionArray = TimelineData.instance().getCompoundCaptionArray();
        TimelineUtil.setCompoundCaption(mTimeline, captionArray);
    }
}


