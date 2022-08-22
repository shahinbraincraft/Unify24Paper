package com.meishe.fxplugin.view;

import android.app.Fragment;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.fxplugin.R;
import com.meishe.fxplugin.data.BackupData;
import com.meishe.fxplugin.utils.Logger;
import com.meishe.fxplugin.utils.ScreenUtils;
import com.meishe.fxplugin.utils.asset.NvAsset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyj on 2018/5/29 0029.
 * VideoFragment，封装liveWindow,供多个页面使用，避免代码重复
 * VideoFragment, encapsulate liveWindow for multiple pages, avoid code duplication
 */

public class PreviewFragment extends Fragment {
    private final String TAG = "VideoFragment";
    private static final float DEFAULT_SCALE_VALUE = 1.0f;
    private static final long BASE_VALUE = 100000;

    private static final int RESETPLATBACKSTATE = 100;
    private RelativeLayout mPlayerLayout;
    private NvsLiveWindow mLiveWindow;
    private LinearLayout mPlayBarLayout;
    private RelativeLayout mPlayButton;
    private ImageView mPlayImage;
    private TextView mCurrentPlayTime;
    private SeekBar mPlaySeekBar;
    private TextView mTotalDuration;
    private RelativeLayout mVoiceButton;

    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private NvsTimeline mTimeline;
    private boolean mPlayBarVisibleState = true, mVoiceButtonVisibleState = false, mAutoPlay = false, mRecording = false;
    private OnFragmentLoadFinisedListener mFragmentLoadFinisedListener;
    private VideoFragmentListener mVideoFragmentCallBack;
    private AssetEditListener mAssetEditListener;
    private WaterMarkChangeListener waterMarkChangeListener;
    private VideoVolumeListener mVideoVolumeListener;
    private OnLiveWindowClickListener mLiveWindowClickListener;
    private OnStickerMuteListener mStickerMuteListener;
    private VideoCaptionTextEditListener mCaptionTextEditListener;
    private OnThemeCaptionSeekListener mThemeCaptionSeekListener;
    private NvsTimelineCaption mCurCaption;
    private int mEditMode = 0;
    private NvsTimelineAnimatedSticker mCurAnimateSticker;
    private int mStickerMuteIndex = 0;
    private NvsTimelineCompoundCaption mCurCompoundCaption;
    private OnCompoundCaptionListener mCompoundCaptionListener;
    /*
    * 播放开始标识
    * Play start identification
    * */
    private long mPlayStartFlag = -1;
    private boolean mShowSeekbar = true;
    //liveWindow 实际view中坐标点
    /*
    * liveWindow 实际view中坐标点
    * coordinate point in the actual view of liveWindow
    * */
    private List<PointF> pointFListLiveWindow;

    /*
    * 第一次添加水印时的原始坐标列表，用于计算偏移量
    * A list of the original coordinates when the watermark was first added, used to calculate the offset
    * */
    private List<PointF> pointFListToFirstAddWaterMark;

    /**
     * 裁剪菜单功能使用字段
     */
    private enum MODE {NONE, DRAG, ZOOM}
    private PointF mPrePoint = new PointF();
    private PointF midPoint = new PointF();
    private int mode = MODE.NONE.ordinal();
    private float oriDis = 1f;
    private float tempDis;
    private double mTransX = 0, mTransY = 0;
    private double mScaleValue = 1.0D, mRotateAngle = 0;
    private boolean isClipMode = false;

    /*
    * Fragment加载完成回调
    * Fragment loading completion callback
    * */
    public interface OnFragmentLoadFinisedListener {
        void onLoadFinished();
    }

    /*
    * 视频播放相关回调
    * Video playback related callbacks
    * */
    public interface VideoFragmentListener {
        //video play
        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }

    /*
    * 贴纸和字幕编辑对应的回调，其他素材不用
    * Callbacks for stickers and subtitle editing, other materials are not used
    * */
    public interface AssetEditListener {
        void onAssetDelete();

        void onAssetSelected(PointF curPoint);

        void onAssetTranstion();

        void onAssetScale();

        /*
        * 字幕专用
        * Subtitle only
        * */
        void onAssetAlign(int alignVal);
        /*
         * 贴纸使用
         * Stickers only
         * */
        void onAssetHorizFlip(boolean isHorizFlip);
    }

    public interface WaterMarkChangeListener {
        void onDrag(List<PointF> list);

        void onScaleAndRotate(List<PointF> curPoint);
    }

    /*
    * 音量回调
    * Volume callback
    * */
    public interface VideoVolumeListener {
        void onVideoVolume();
    }
    /*
    * 字幕文本修改回调
    * Subtitle text modification callback
    * */
    public interface VideoCaptionTextEditListener {
        void onCaptionTextEdit();
    }

    /*
    * 组合字幕索引回调
    * Combined subtitle index callback
    * */
    public interface OnCompoundCaptionListener {
        void onCaptionIndex(int captionIndex);
    }
    /*
    * LiveWindowd点击回调
    * LiveWindowd click callback
    * */
    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }
    /*
    * 贴纸静音点击回调
    * Sticker mute click callback
    * */
    public interface OnStickerMuteListener {
        void onStickerMute();
    }

    public interface OnThemeCaptionSeekListener {
        void onThemeCaptionSeek(long stamp);
    }

    public void setThemeCaptionSeekListener(OnThemeCaptionSeekListener themeCaptionSeekListener) {
        mThemeCaptionSeekListener = themeCaptionSeekListener;
    }

    public void setLiveWindowClickListener(OnLiveWindowClickListener liveWindowClickListener) {
        this.mLiveWindowClickListener = liveWindowClickListener;
    }

    public void setCaptionTextEditListener(VideoCaptionTextEditListener captionTextEditListener) {
        this.mCaptionTextEditListener = captionTextEditListener;
    }

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinisedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public void setVideoFragmentCallBack(VideoFragmentListener videoFragmentCallBack) {
        this.mVideoFragmentCallBack = videoFragmentCallBack;
    }

    public void setAssetEditListener(AssetEditListener assetEditListener) {
        this.mAssetEditListener = assetEditListener;
    }

    public void setWaterMarkChangeListener(WaterMarkChangeListener waterMarkChangeListener) {
        this.waterMarkChangeListener = waterMarkChangeListener;
    }

    public void setVideoVolumeListener(VideoVolumeListener videoVolumeListener) {
        this.mVideoVolumeListener = videoVolumeListener;
    }

    public void setStickerMuteListener(OnStickerMuteListener stickerMuteListener) {
        this.mStickerMuteListener = stickerMuteListener;
    }

    public void setCompoundCaptionListener(OnCompoundCaptionListener compoundCaptionListener) {
        this.mCompoundCaptionListener = compoundCaptionListener;
    }

    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case RESETPLATBACKSTATE:
                    updateCurPlayTime(0);
                    seekTimeline(0, 0);
                    /*
                    * 播放进度条显示
                    * Play progress bar display
                    * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                    }
                    break;
            }
            return false;
        }
    });

    private CountDownTimer m_hidePlayBarTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            /*
             * 播放进度条显示
             * Play progress bar display
             * */
            if (mPlayBarVisibleState && !mShowSeekbar) {
                mPlayStartFlag = -1;
                mPlayBarLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        mPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.player_layout);
        mLiveWindow = (NvsLiveWindow) rootView.findViewById(R.id.liveWindow);
        mPlayBarLayout = (LinearLayout) rootView.findViewById(R.id.playBarLayout);
        mPlayButton = (RelativeLayout) rootView.findViewById(R.id.playLayout);
        mPlayImage = (ImageView) rootView.findViewById(R.id.playImage);
        mCurrentPlayTime = (TextView) rootView.findViewById(R.id.currentPlaytime);
        mPlaySeekBar = (SeekBar) rootView.findViewById(R.id.play_seekBar);
        mTotalDuration = (TextView) rootView.findViewById(R.id.totalDuration);
        mVoiceButton = (RelativeLayout) rootView.findViewById(R.id.voiceLayout);
        controllerOperation();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        initData();
        //
        mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
        mVoiceButton.setVisibility(mVoiceButtonVisibleState ? View.VISIBLE : View.GONE);
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
    }

    private void initData() {
        setLivewindowRatio();
        updateTotalDuarationText();
        updateCurPlayTime(0);
    }

    private void setLivewindowRatio() {
        Bundle bundle = getArguments();
        int ratio = 0, titleHeight = 0, bottomHeight = 0;
        if (bundle != null) {
            ratio = bundle.getInt("ratio", NvAsset.AspectRatio_16v9);
            titleHeight = bundle.getInt("titleHeight");
            bottomHeight = bundle.getInt("bottomHeight");
            mPlayBarVisibleState = bundle.getBoolean("playBarVisible", true);
            mVoiceButtonVisibleState = bundle.getBoolean("voiceButtonVisible", false);
        }

        if (null == mTimeline) {
            Log.e(TAG, "mTimeline is null!");
            return;
        }
        setLiveWindowRatio(ratio, titleHeight, bottomHeight);
        connectTimelineWithLiveWindow();
    }

    public void updateCurPlayTime(long time) {
        mCurrentPlayTime.setText(formatTimeStrWithUs(time));
        mPlaySeekBar.setProgress((int) (time / BASE_VALUE));
    }

    public void updateTotalDuarationText() {
        if (mTimeline != null) {
            mTotalDuration.setText(formatTimeStrWithUs(mTimeline.getDuration()));
            mPlaySeekBar.setMax((int) (mTimeline.getDuration() / BASE_VALUE));
        }
    }

    public void setTimeline(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public Point getLiveWindowSize() {
        Logger.e(TAG, "mLiveWindow宽高获取  " + mLiveWindow.getWidth() + "    " + mLiveWindow.getHeight());
        return new Point(mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    public void setEditMode(int mode) {
        mEditMode = mode;
    }

    /*
    * 设置字幕
    * Set subtitles
    * */
    public void setCurCaption(NvsTimelineCaption caption) {
        mCurCaption = caption;
    }

    public NvsTimelineCaption getCurCaption() {
        return mCurCaption;
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList){
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        return newList;
    }

    /**
     * 根据宽高获取livewindow的四个角坐标
     * Get the four corner coordinates of livewindow according to the width and height
     */
    private void setPointFListLiveWindow(int w, int h) {
//        int x0 = Math.abs(w - h) / 2;
        int x0 = 0;
        int x1 = w;
        int y0 = 0;
        int y1 = h;
        Logger.e(TAG, "liveWindow的四个角坐标  " + x0 + "  " + x1 + "  " + y0 + "  " + y1);
        pointFListLiveWindow = setFourPointToList(x0, x1, y0, y1);
    }

    /**
     * 四个点坐标转化到list，从左上逆时针
     * Four point coordinates converted to list, counterclockwise from top left
     */
    private List<PointF> setFourPointToList(float x0, float x1, float y0, float y1) {
        List<PointF> newList = new ArrayList<>();
        newList.add(new PointF(x0, y0));
        newList.add(new PointF(x0, y1));
        newList.add(new PointF(x1, y1));
        newList.add(new PointF(x1, y0));
        return newList;
    }

    private boolean checkInLiveWindow(List<PointF> newList) {
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            for (PointF pointF : newList) {
                if (pointF.x < minX || pointF.x > maxX || pointF.y < minY || pointF.y > maxY) {
                    Logger.e(TAG, "checkInLiveWindow " + minX + "       " + pointF.x + "      " + maxX);
                    Logger.e(TAG, "checkInLiveWindow " + minY + "       " + pointF.y + "      " + maxY);
                    return false;
                }
            }
        }
        return true;
    }

    public void setAutoPlay(boolean flag) {
        mAutoPlay = flag;
    }

    public void setRecording(boolean record_state) {
        mRecording = record_state;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setCurAnimateSticker(NvsTimelineAnimatedSticker animateSticker) {
        mCurAnimateSticker = animateSticker;
    }

    public NvsTimelineAnimatedSticker getCurAnimateSticker() {
        return mCurAnimateSticker;
    }

    /*
    * 连接时间线跟liveWindow
    * Connect timeline with liveWindow
    * */
    public void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null)
            return;

        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                seekTimeline(0, 0);
                updateCurPlayTime(0);
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updateCurPlayTime(cur_position);
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_pause);
                    startHidePlayBarTimer(false);
                } else {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_play);
                    mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
                    startHidePlayBarTimer(true);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.streamingEngineStateChanged(i);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
    }

    private boolean isSelectedCompoundCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCompoundCaption != null
                && curPosition >= mCurCompoundCaption.getInPoint()
                && curPosition <= mCurCompoundCaption.getOutPoint()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCaption != null
                && curPosition >= mCurCaption.getInPoint()
                && curPosition <= mCurCaption.getOutPoint()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedAnimateSticker() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurAnimateSticker != null
                && curPosition >= mCurAnimateSticker.getInPoint()
                && curPosition <= mCurAnimateSticker.getOutPoint()) {
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        connectTimelineWithLiveWindow();
        long stamp = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        updateCurPlayTime(stamp);
        Log.e(TAG, "onResume");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAutoPlay && mPlayImage != null) {
                    playVideoButtonCilck();
                }
            }
        }, 100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mVideoFragmentCallBack = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
    }

    /*
    * 播放视频
    * Play video
    * */
    public void playVideo(long startTime, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    /*
    * 预览
    * Seek
    * */
    public void seekTimeline(long timestamp, int seekShowMode) {
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    /*
    * 获取当前引擎状态
    * Get the current engine status
    * */
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    /*
    * 停止引擎
    * Stop the engine
    * */
    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    public void playVideoButtonCilck() {
        if(mTimeline == null){
            return;
        }
        long endTime = mTimeline.getDuration();
        playVideoButtonCilck(0, endTime);
    }

    public void playVideoButtonCilck(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
        /*
        * 更新播放进度条显示标识
        * Update the playback progress bar indicator
        * */
        if (mPlayBarVisibleState) {
            mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            mPlayBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setLiveWindowRatio(int ratio, int titleHeight, int bottomHeight) {
        ViewGroup.LayoutParams layoutParams = mPlayerLayout.getLayoutParams();
        int statusHeight = ScreenUtils.getStatusBarHeight(getActivity());
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        int screenHeight = ScreenUtils.getScreenHeight(getActivity());
        int newHeight = screenHeight - titleHeight - bottomHeight - statusHeight;
        switch (ratio) {
            case NvAsset.AspectRatio_16v9: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
            case NvAsset.AspectRatio_1v1: //1:1
                layoutParams.width = screenWidth;
                layoutParams.height = screenWidth;
                if (newHeight < screenWidth) {
                    layoutParams.width = newHeight;
                    layoutParams.height = newHeight;
                }
                break;
            case NvAsset.AspectRatio_9v16: //9:16
                layoutParams.width = (int) (newHeight * 9.0 / 16);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_3v4: // 3:4
                layoutParams.width = (int) (newHeight * 3.0 / 4);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_4v3: //4:3
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 3.0 / 4);
                break;
            case NvAsset.AspectRatio_NoFitRatio:
                layoutParams.height = newHeight;
                Point dimension = BackupData.instance().getImageDimension();
                if(dimension != null) {
                    layoutParams.width = newHeight * dimension.x / dimension.y;
                    if(layoutParams.width > screenWidth) {
                        layoutParams.width = screenWidth;
                        layoutParams.height = screenWidth * dimension.y / dimension.x;
                    }
                } else {
                    layoutParams.width = (int) (newHeight * 9.0 / 16);
                }

                break;
            default: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
        }
        Logger.d("TAG", "mgj layoutParams: " + layoutParams.width + "  " + layoutParams.height);


        mPlayerLayout.setLayoutParams(layoutParams);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
        Logger.d("TAG", "mgj mLiveWindow: " + mLiveWindow.getLayoutParams().width + "  " + mLiveWindow.getLayoutParams().height);
    }

    //formate time
    private String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    private void controllerOperation() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                    }
                } else {
                    if(mTimeline == null){
                        return;
                    }
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    long endTime = mTimeline.getDuration();
                    playVideo(startTime, endTime);
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    }
                }
            }
        });

        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekTimeline(progress * BASE_VALUE, 0);
                    updateCurPlayTime(progress * BASE_VALUE);
                    if (mThemeCaptionSeekListener != null) {
                        mThemeCaptionSeekListener.onThemeCaptionSeek(progress * BASE_VALUE);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = true;
                startHidePlayBarTimer(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = false;
                startHidePlayBarTimer(true);
            }
        });
        mVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoVolumeListener != null) {
                    mVideoVolumeListener.onVideoVolume();
                }
            }
        });

        mLiveWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPlayBarVisibleState || isClipMode) {
                    return;
                }
                if (mLiveWindowClickListener != null) {
                    mLiveWindowClickListener.onLiveWindowClick();
                }
                /*
                * 如果正在录音，禁止操作
                * Prohibit operation if recording
                * */
                if (mRecording) {
                    return;
                }

                /*
                * 播放进度条显示
                * Play progress bar display
                * */
                if (mPlayBarVisibleState) {
                    if (mPlayBarLayout.getVisibility() == View.INVISIBLE) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                        return;
                    }
                }
                mPlayButton.callOnClick();
            }
        });

        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isClipMode) {
                    return false;
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    /*
                     * 单指
                     * Single finger
                     * */
                    case MotionEvent.ACTION_DOWN:
                        mPrePoint.set(event.getX(), event.getY());
                        mode = MODE.DRAG.ordinal();
                        Log.d(TAG, "ACTION_DOWN: " + mPrePoint.toString());
                        break;
                    /*
                     * 双指
                     * Two fingers
                     * */
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oriDis = distance(event);
                        if (oriDis > 15f) {
                            midPoint = middle(event);
                            mode = MODE.ZOOM.ordinal();
                            tempDis = oriDis;
                        }
                        Log.d(TAG, "ACTION_POINTER_DOWN: " + oriDis + ":" + midPoint);
                        break;
                    /*
                     * 手指放开
                     * Release your finger
                     * */
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = MODE.NONE.ordinal();
                        break;
                    /*
                     * 单指滑动事件
                     * One-finger swipe event
                     * */
                    case MotionEvent.ACTION_MOVE:
                        if (mode == MODE.DRAG.ordinal()) {
                            float transX = event.getX() - mPrePoint.x;
                            float transY = mPrePoint.y - event.getY();
                            mTransX += transX;
                            mTransY += transY;
                            updateTransform2DFx(mScaleValue, mRotateAngle, mTransX, mTransY);
                            mPrePoint.set(event.getX(), event.getY());
                            Log.d(TAG, "ACTION_MOVE_SINGLE: " + mTransX + ":" + mTransY);
                        } else if (mode == MODE.ZOOM.ordinal()) {
                            float newDist = distance(event);
                            if ((newDist - tempDis) > 2f) {
                                mScaleValue = mScaleValue * 1.1;
                                updateTransform2DFx(mScaleValue, mRotateAngle, mTransX, mTransY);
                            } else if ((tempDis - newDist) >= 2f) {
                                mScaleValue = mScaleValue * 0.9;
                                updateTransform2DFx(mScaleValue, mRotateAngle, mTransX, mTransY);
                            }
                            tempDis = newDist;
                            Log.d(TAG, "ACTION_MOVE_DOUBLE: " + newDist + ":" + mScaleValue);
                        }
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                        break;
                }
                return true;
            }
        });
    }

    public void resetAppearance() {
        mScaleValue = 1.0D;
        mRotateAngle = 0;
        mTransX = 0;
        mTransY = 0;
        updateTransform2DFx(mScaleValue, mRotateAngle, mTransX, mTransY);
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }

    public void updateTransform2DFx(double scaleValue, double rotateAngle, double transX, double transY) {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        int clipCount = videoTrack.getClipCount();
        for (int clipIindex = 0; clipIindex < clipCount; ++clipIindex) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(clipIindex);
            int fxCount = videoClip.getFxCount();
            for (int fxIndex = 0; fxIndex < fxCount; ++fxIndex) {
                NvsVideoFx videoFx = videoClip.getFxByIndex(fxIndex);
                String videoFxName = "Transform 2D";
                if (videoFx.getVideoFxType() == NvsVideoFx.VIDEOFX_TYPE_BUILTIN
                        && videoFx.getBuiltinVideoFxName().equals(videoFxName)) {
                    Logger.d(TAG, "updateTransform2DFx: " + "apply:updateTransform2DFx");
                    /*
                     * 缩放
                     * Zoom
                     * */
                    videoFx.setFloatVal("Scale X", scaleValue);
                    videoFx.setFloatVal("Scale Y", scaleValue);
                    /*
                     * 旋转
                     * rotate
                     * */
                    videoFx.setFloatVal("Rotation", rotateAngle);
                    /*
                     * 片段偏移
                     * Fragment offset
                     * */
                    videoFx.setFloatVal("Trans X", transX);
                    videoFx.setFloatVal("Trans Y", transY);
                    break;
                }
            }
        }
    }

    /*
     * 计算两个触摸点之间的距离
     * Calculate the distance between two touch points
     * */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * 计算两个触摸点的中点
     * Calculate the midpoint of two touch points
     * */
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    public NvsLiveWindow getLiveWindow() {
        return mLiveWindow;
    }

    public List<PointF> getPointFListToFirstAddWaterMark() {
        if (pointFListToFirstAddWaterMark == null) {
            return new ArrayList<>();
        }
        return pointFListToFirstAddWaterMark;
    }

    public void setPointFListToFirstAddWaterMark(List<PointF> pointFListToFirstAddWaterMark) {
        this.pointFListToFirstAddWaterMark = pointFListToFirstAddWaterMark;
    }

    public void setPlaySeekVisiable(boolean visiable) {
        if (visiable) {
            mPlayBarLayout.setVisibility(View.VISIBLE);
        } else {
            mPlayBarLayout.setVisibility(View.INVISIBLE);
        }
        mPlayBarVisibleState = visiable;
    }

    public void startHidePlayBarTimer(boolean start) {
        if (mPlayBarVisibleState) {
            m_hidePlayBarTimer.cancel();
            if (start) {
                m_hidePlayBarTimer.start();
            }
        }
    }
}
