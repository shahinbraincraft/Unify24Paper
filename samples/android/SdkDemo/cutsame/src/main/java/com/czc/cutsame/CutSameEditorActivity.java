package com.czc.cutsame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateClip;
import com.czc.cutsame.fragment.BaseVideoFragment;
import com.czc.cutsame.fragment.CutEditorVpFragment;
import com.czc.cutsame.fragment.TemplateListFragment;
import com.czc.cutsame.fragment.adapter.CommonFragmentCustomTabAdapter;
import com.czc.cutsame.fragment.presenter.CutEditorVpPresenter;
import com.czc.cutsame.util.CustomConstants;
import com.czc.cutsame.view.ResolutionBottomDialog;
import com.czc.cutsame.view.TailorView;
import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.manager.AppManager;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.view.PlayControlView;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.meishe.engine.editor.EditorController;
import com.meishe.engine.interf.VideoFragmentListenerWithClick;
import com.meishe.engine.util.TimelineUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : CaoZhiChao
 * @CreateDate : 2020/11/27 16:29
 * @Description : 剪同款的编辑 edit activity
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CutSameEditorActivity extends BaseActivity implements VideoFragmentListenerWithClick, CutEditorVpFragment.SeekPositionListener {
    public final static String BUNDLE_KEY = "CUT_MODEL_KEY";
    public final static String BUNDLE_VALUE_HEAD = "CUT_HEAD";
    public final static String BUNDLE_VALUE_TAIL = "CUT_TAIL";
    public static final int INTENT_FLAG = 10;
    public static final String INTENT_TRAM = "intentTrim";
    public static final String INTENT_TRAM_IN = "intent.trim.in";
    public static final String INTENT_TRAM_OUT = "intent.trim.out";
    public static final String INTENT_EDIT_CLIP_INDEX = "intent.trim.index";
    private BaseVideoFragment mBaseVideoFragment;
    private TabLayout mTableLayout;
    private ViewPager mCutEditorViewPager;
    private List<CutEditorVpFragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();
    private PlayControlView mPlayControlView;
    private int mState = -1;
    private List<TemplateClip> mTemplateClips = new ArrayList<>();
    private String mTemplateId;
    private NvsTimeline mBeforeTimeLine;
    private ResolutionBottomDialog mResolutionBottomDialog;
    private long mJumpTime = 0;
    public static NvsTimeline cutTimeline;
    /**
     * 模板类别
     */
    private String mTemplateType = Template.TYPE_TEMPLATE_STANDER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EditorController.getInstance().destroyAuxiliaryStreamingContext();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_cut_same_editor;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mTemplateClips = intent.getParcelableArrayListExtra(CustomConstants.TEMPLATE_CLIP_LIST);
            mTemplateId = intent.getStringExtra(CustomConstants.TEMPLATE_ID);
            mTemplateType = intent.getStringExtra(CustomConstants.TEMPLATE_TYPE);
        }
        if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)) {
            mBeforeTimeLine = createTimeline(EditorController.getInstance().getStreamingContext());
            if (mBeforeTimeLine == null) {
                LogUtils.e("failed to create timeline");
                finish();
                return;
            }
            //创建辅助context 辅助timeline 用于grab图标
            NvsTimeline auxiliaryTimeline = createTimeline(EditorController.getInstance().createAuxiliaryStreamingContext());
            EditorController.getInstance().setAuxiliaryTimeline(auxiliaryTimeline);
        } else {
            int ratio = getIntent().getIntExtra(CustomConstants.TEMPLATE_RATIO,1);
            mBeforeTimeLine = TimelineUtil.newTimeline(EditorController.getInstance().getVideoResolutionByRatio(ratio));
            if (mBeforeTimeLine == null) {
                LogUtils.e("failed to create timeline");
                finish();
                return;
            }
            NvsVideoTrack nvsVideoTrack = mBeforeTimeLine.appendVideoTrack();
            if (nvsVideoTrack == null) {
                LogUtils.e("failed to append nvsVideoTrack");
                return;
            }
            nvsVideoTrack.setVolumeGain(0,0);
            for (TemplateClip templateClip : mTemplateClips) {
                NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(templateClip.getFilePath());
                parseNvsVideoClip(nvsVideoClip, templateClip);
            }
            mBeforeTimeLine.applyThemeTemplate(mTemplateId);
            checkClipHeadAndTail();
            //创建辅助context 辅助timeline 用于grab图标

            NvsTimeline auxiliaryTimeline = createAuxiliaryTimeline( EditorController.getInstance().createAuxiliaryStreamingContext(),EditorController.getInstance().getVideoResolutionByRatio(ratio));
            NvsVideoTrack auxiliaryTrack = auxiliaryTimeline.appendVideoTrack();
            if (auxiliaryTrack == null) {
                LogUtils.e("failed to append nvsVideoTrack");
                return;
            }
            for (TemplateClip templateClip : mTemplateClips) {
                NvsVideoClip nvsVideoClip = auxiliaryTrack.appendClip(templateClip.getFilePath());
                parseNvsVideoClip(nvsVideoClip, templateClip);
            }
            auxiliaryTimeline.applyThemeTemplate(mTemplateId);
            EditorController.getInstance().setAuxiliaryTimeline(auxiliaryTimeline);
        }
        EditorController.getInstance().setNvsTimeline(mBeforeTimeLine);

    }


    private NvsTimeline createAuxiliaryTimeline(NvsStreamingContext context,NvsVideoResolution nvsVideoResolution){
        if(null == context){
            return null;
        }
        nvsVideoResolution.imagePAR = new NvsRational(1, 1);
        NvsAudioResolution nvsAudioResolution = new NvsAudioResolution();
        nvsAudioResolution.sampleRate = 44100;                            //采样率
        nvsAudioResolution.channelCount = 2;
        NvsTimeline nvsTimeline = context.createTimeline(nvsVideoResolution,new NvsRational(25, 1),nvsAudioResolution);
        return nvsTimeline;
    }

    private void checkClipHeadAndTail() {
        if (mBeforeTimeLine == null) {
            return;
        }
        NvsVideoTrack videoTrackByIndex = mBeforeTimeLine.getVideoTrackByIndex(0);
        int clipCount = videoTrackByIndex.getClipCount();
        if (clipCount<=0){
            return;
        }
        if (clipCount == mTemplateClips.size()) {
            return;
        }
        Log.e("lpf", "clipCount" + clipCount+ "mTemplateClips.size()"+mTemplateClips.size());
        NvsVideoClip clipFirst = videoTrackByIndex.getClipByIndex(0);
        NvsVideoClip clipLast =videoTrackByIndex.getClipByIndex(clipCount-1);
        if (clipCount-mTemplateClips.size()==2){
            //有片头和片尾
            TemplateClip templateClipHead=new TemplateClip();
            templateClipHead.setReplaceMode(TemplateClip.TYPE_CAN_NOT_REPLACE);
            TemplateClip templateClipTail=new TemplateClip();
            templateClipTail.setReplaceMode(TemplateClip.TYPE_CAN_NOT_REPLACE);
            parseNvsVideoClip(clipFirst,templateClipHead);
            parseNvsVideoClip(clipLast,templateClipTail);
            mTemplateClips.add(0,templateClipHead);
            mTemplateClips.add(templateClipTail);

        }else if (clipCount-mTemplateClips.size()==1){
            //有片头或者片尾
            if (!clipFirst.getFilePath().equals(mTemplateClips.get(0).getFilePath())) {
                //有片头
                TemplateClip templateClipHead=new TemplateClip();
                templateClipHead.setReplaceMode(TemplateClip.TYPE_CAN_NOT_REPLACE);
                parseNvsVideoClip(clipFirst,templateClipHead);
                mTemplateClips.add(0,templateClipHead);
            }else if (!clipLast.getFilePath().equals(mTemplateClips.get(mTemplateClips.size()-1).getFilePath())){
                //有片尾部
                TemplateClip templateClipTail=new TemplateClip();
                templateClipTail.setReplaceMode(TemplateClip.TYPE_CAN_NOT_REPLACE);
                parseNvsVideoClip(clipLast,templateClipTail);
                mTemplateClips.add(templateClipTail);
            }
        }


    }

    private void parseNvsVideoClip(NvsVideoClip nvsVideoClip, TemplateClip templateClip) {
        if (nvsVideoClip != null) {
            templateClip.setInPoint(nvsVideoClip.getInPoint());
            templateClip.setOutPoint(nvsVideoClip.getOutPoint());
            templateClip.setTrimIn(nvsVideoClip.getTrimIn());
            templateClip.setTrimOut(nvsVideoClip.getTrimOut());
            templateClip.setDuration(nvsVideoClip.getTrimOut() - nvsVideoClip.getTrimIn());
            templateClip.setFilePath(nvsVideoClip.getFilePath());
        }
    }

    private NvsTimeline createTimeline(NvsStreamingContext context) {
        List<NvsStreamingContext.templateFootageInfo> footageInfoList = new ArrayList<>();
        for (TemplateClip templateClip : mTemplateClips) {
            NvsStreamingContext.templateFootageInfo footageInfo = new NvsStreamingContext.templateFootageInfo();
            footageInfo.footageId = templateClip.getFootageId();
            footageInfo.reverseFilePath = templateClip.getReversePath();
            footageInfo.filePath = templateClip.getFilePath();
            footageInfoList.add(footageInfo);
        }
        NvsTimeline timeline = EditorController.getInstance().createTimeline(context, mTemplateId, footageInfoList);

        if (timeline == null) {
            return null;
        }
//        timeline.applyThemeTemplate("");


        NvsVideoResolution videoRes = timeline.getVideoRes();
        int imageWidth = videoRes.imageWidth;
        int imageHeight = videoRes.imageHeight;

        LogUtils.d("imageWidth->" + imageWidth + " imageHeight->" + imageHeight);
        changeVideoClipPath(timeline);
        TimelineUtil.loadTimelineToTemplate(timeline);
        return timeline;
    }

    /**
     * 改变videoClipPath
     * change VideoClip Path
     */
    private void changeVideoClipPath(NvsTimeline timeLine) {
        for (int i = 0; i < timeLine.videoTrackCount(); i++) {
            NvsVideoTrack nvsVideoTrack = timeLine.getVideoTrackByIndex(i);
            for (int j = 0; j < nvsVideoTrack.getClipCount(); j++) {
                NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(j);
                for (TemplateClip clip : mTemplateClips)
                    if (clip.getTrackIndex() == i && clip.getInPoint() == nvsVideoClip.getInPoint()) {
                        if (clip.getNeedReverse()) {
                            nvsVideoClip.changeFilePath(clip.getReversePath());
                        } else {
                            nvsVideoClip.changeFilePath(clip.getFilePath());
                        }
                        break;
                    }
            }
        }
    }

    @Override
    protected void initView() {
        addVideoFragment();
        Button cutEditorImport = findViewById(R.id.cut_editor_import);
        Drawable drawable = CommonUtils.getRadiusDrawable(-1, -1, getResources().getDimensionPixelOffset(R.dimen.dp_px_165),
                getResources().getColor(R.color.activity_tailor_button_background));
        if (!TextUtils.isEmpty(TemplateListFragment.cut_model)) {
            cutEditorImport.setText(getResources().getString(R.string.activity_cut_export_template_confirm));
            Drawable drawable2 = CommonUtils.getRadiusDrawable(-1, -1, getResources().getDimensionPixelOffset(R.dimen.dp_px_165),
                    Color.parseColor("#63ABFF"));
            cutEditorImport.setBackground(drawable2);
        } else {
            cutEditorImport.setBackground(drawable);
        }
        cutEditorImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(TemplateListFragment.cut_model, CutSameEditorActivity.BUNDLE_VALUE_HEAD)) {
                    // 添加头部
                    cutTimeline = EditorController.getInstance().getNvsTimeline();
                    startActivity(new Intent("com.meishe.sdkdemo.sequenceNesting.sequenceNesting.SequenceNestingActivity"));
                } else if (TextUtils.equals(TemplateListFragment.cut_model, CutSameEditorActivity.BUNDLE_VALUE_TAIL)) {
                    // 添加尾部
                    cutTimeline = EditorController.getInstance().getNvsTimeline();
                    startActivity(new Intent("com.meishe.sdkdemo.sequenceNesting.sequenceNesting.SequenceNestingActivity"));
                } else {
                    if (mResolutionBottomDialog == null) {
                        //正常剪同款
                        mResolutionBottomDialog = new ResolutionBottomDialog(CutSameEditorActivity.this);
                        mResolutionBottomDialog.setResolutionBottomDialogListener(new ResolutionBottomDialog.ResolutionBottomDialogListener() {

                            @Override
                            public void onDone(int resolution) {
                                Bundle bundle = new Bundle();
                                bundle.putString(CutCompileActivity.TEMPLATE_ID, mTemplateId);
                                bundle.putInt(CutCompileActivity.COMPILE_RESOLUTION, resolution);
                                AppManager.getInstance().jumpActivity(CutSameEditorActivity.this, CutCompileActivity.class, bundle);
                            }
                        });
                    }
                    mResolutionBottomDialog.show();
                }
            }
        });
        mTableLayout = findViewById(R.id.cut_editor_tab);
        mCutEditorViewPager = findViewById(R.id.cut_editor_view_pager);
        RelativeLayout realTitleBar = findViewById(R.id.real_titlebar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) realTitleBar.getLayoutParams();
        layoutParams.topMargin = ScreenUtils.getStatusBarHeight();
        realTitleBar.setLayoutParams(layoutParams);
        initViewPager();
        ImageView cutEditorClose = findViewById(R.id.cut_editor_close);
        cutEditorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPlayControlView = findViewById(R.id.cut_editor_play_view);
        initPlayView();
    }

    private void initPlayView() {
        int duration = (int) (EditorController.getInstance().getTimelineDuration() / 1000f);
        mPlayControlView.setMax(duration);
        String nvStringTime = FormatUtils.microsecond2Time(EditorController.getInstance().getTimelineDuration());
        mPlayControlView.setStartText("00:00");
        mPlayControlView.setCurrentText(nvStringTime);
        mPlayControlView.setListener(new PlayControlView.OnSeekBarListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditorController.getInstance().stop();
                mState = TailorView.FROM_USER;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mState = TailorView.FROM_VIDEO;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long nvTime = progress * 1000;
                String nvStringTime = FormatUtils.microsecond2Time(nvTime);
                mPlayControlView.setStartText(nvStringTime);
                if (mState != TailorView.FROM_VIDEO) {
                    EditorController.getInstance().seekTimeline(nvTime);
                }
            }
        });
        mPlayControlView.setOnPlayClickListener(new PlayControlView.OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                if (EditorController.getInstance().isPlaying()) {
                    EditorController.getInstance().stop();
                } else {
                    EditorController.getInstance().playNow();
                }
            }
        });
    }

    private void initViewPager() {
        boolean addCaption = true;
        if(mTemplateType.equalsIgnoreCase(Template.TYPE_TEMPLATE_STANDER)){

            if (EditorController.getInstance().getTemplateCaptions(mTemplateId).isEmpty()) {
                addCaption = false;
            }
        }else{
            if(!EditorController.getInstance().hasTemplateCaptions(mBeforeTimeLine)){
                addCaption = false;
            }
        }

        mTabTitleList.clear();
        mTabTitleList.add(getResources().getString(R.string.activity_cut_editor_video));
        if (addCaption) {
            mTabTitleList.add(getResources().getString(R.string.activity_cut_editor_text));
        }

        mFragmentList.clear();
        mFragmentList.add(CutEditorVpFragment.newInstance(CutEditorVpPresenter.VIDEO, mTemplateId, mTemplateType, mTemplateClips, this));
        if (addCaption) {
            mFragmentList.add(CutEditorVpFragment.newInstance(CutEditorVpPresenter.CAPTION, mTemplateId, mTemplateType, mTemplateClips, this));
        }

        mCutEditorViewPager.setOffscreenPageLimit(mFragmentList.size());
        CommonFragmentCustomTabAdapter commonFragmentCustomTabAdapter = new CommonFragmentCustomTabAdapter(getSupportFragmentManager(), getApplicationContext(), R.layout.cut_same_tablayout_tab_layout, mFragmentList, mTabTitleList);
        mCutEditorViewPager.setAdapter(commonFragmentCustomTabAdapter);
        mTableLayout.setupWithViewPager(mCutEditorViewPager);
        for (int i = 0; i < mTableLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTableLayout.getTabAt(i);
            if (tab == null) {
                return;
            }
            tab.setCustomView(commonFragmentCustomTabAdapter.getTabView(i));
            if (tab.getCustomView() == null) {
                return;
            }
            TextView textView = tab.getCustomView().findViewById(R.id.cut_editor_tab_title);
            ImageView imageView = tab.getCustomView().findViewById(R.id.cut_editor_tab_img);
            if (i == 0) {
                textView.setText(R.string.activity_cut_editor_video);
                textView.setTextColor(getResources().getColor(R.color.activity_tailor_button_background));
                imageView.setBackgroundResource(R.drawable.cut_editor_tab_video_select);
            } else {
                textView.setText(R.string.activity_cut_editor_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                imageView.setBackgroundResource(R.drawable.cut_editor_tab_text_unselect);
            }
        }
        mTableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() == null) {
                    return;
                }
                int position = tab.getPosition();
                if (position == 0) {
                    tab.getCustomView().findViewById(R.id.cut_editor_tab_img).setBackgroundResource(R.drawable.cut_editor_tab_video_select);
                    if(null != mBaseVideoFragment){
                        mBaseVideoFragment.notShowCaptionBox();
                    }
                } else if (position == 1) {
                    tab.getCustomView().findViewById(R.id.cut_editor_tab_img).setBackgroundResource(R.drawable.cut_editor_tab_text_select);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.cut_editor_tab_title);
                textView.setTextColor(getResources().getColor(R.color.activity_tailor_button_background));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() == null) {
                    return;
                }
                int position = tab.getPosition();
                if (position == 0) {
                    tab.getCustomView().findViewById(R.id.cut_editor_tab_img).setBackgroundResource(R.drawable.cut_editor_tab_video_unselect);
                } else if (position == 1) {
                    tab.getCustomView().findViewById(R.id.cut_editor_tab_img).setBackgroundResource(R.drawable.cut_editor_tab_text_unselect);
                }
                TextView textView = tab.getCustomView().findViewById(R.id.cut_editor_tab_title);
                textView.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void addVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mBaseVideoFragment = BaseVideoFragment.newInstance(false);
        mBaseVideoFragment.setVideoFragmentListener(this);
        fragmentManager.beginTransaction().add(R.id.cut_editor_fragment_container, mBaseVideoFragment).commit();
        fragmentManager.beginTransaction().show(mBaseVideoFragment);
    }

    @Override
    public void playBackEOF(NvsTimeline timeline) {
        if (mBaseVideoFragment != null) {
            notShowCaptionBox();
            mBaseVideoFragment.changePlayButtonState(true);
            mPlayControlView.setProgress((int) (EditorController.getInstance().getTimelineDuration() / 1000f));
        }
    }

    @Override
    public void playStopped(NvsTimeline timeline) {
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(true);
        }
    }

    @Override
    public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
        mState = TailorView.FROM_VIDEO;
        mPlayControlView.setProgress((int) (stamp / 1000f));
    }

    @Override
    public void streamingEngineStateChanged(int state) {
        boolean playing = EditorController.getInstance().isPlaying();
        if (mBaseVideoFragment != null) {
            mBaseVideoFragment.changePlayButtonState(!playing);
            if (playing) {
                mBaseVideoFragment.notShowCaptionBox();
            }
        }
        mPlayControlView.changPlayState(playing);
        if (playing) {
            mState = TailorView.FROM_VIDEO;
            for (Fragment fragment : mFragmentList) {
                CutEditorVpFragment cutEditorVpFragment = (CutEditorVpFragment) fragment;
                cutEditorVpFragment.clearSelectOnPlay();
            }
        }
    }

    @Override
    public void onSeekingTimelinePosition(NvsTimeline timeline, long position) {
//        if (mState != TailorView.FROM_USER){
//            mPlayControlView.setProgress(TimeFormatUtil.formatMsToMillisecond(position));
//        }
    }

    @Override
    public boolean clickPlayButtonByOthers() {
        return false;
    }

    @Override
    public boolean clickLiveWindowByOthers() {
        return false;
    }

    @Override
    public void connectTimelineWithLiveWindow() {

    }

    public TemplateClip getTemplateClip(int trackIndex, long inPoint) {
        for (TemplateClip templateClip : mTemplateClips) {
            if (templateClip.getTrackIndex() == trackIndex && templateClip.getInPoint() == inPoint) {
                return templateClip;
            }
        }
        return null;
    }

    public void showCaptionBox(List<PointF> list) {
        mBaseVideoFragment.showCaptionBox(list);
    }

    public void notShowCaptionBox() {
        mBaseVideoFragment.notShowCaptionBox();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJumpTime = EditorController.getInstance().nowTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == INTENT_FLAG) { //返回的结果是来自于Activity B
            if (resultCode == Activity.RESULT_OK) {
                if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)) {
                    EditorController.getInstance().setNvsTimeline(mBeforeTimeLine);
                    mBaseVideoFragment.connectTimelineWithLiveWindow();
                    long newTrim = data != null ? data.getLongExtra(INTENT_TRAM, 0) : 0;
                    CutEditorVpFragment cutEditorVpFragment = mFragmentList.get(mTableLayout.getSelectedTabPosition());
                    cutEditorVpFragment.changClipTrim(newTrim);
                } else if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)) {
                    EditorController.getInstance().setNvsTimeline(mBeforeTimeLine);
                    mBaseVideoFragment.connectTimelineWithLiveWindow();

                    NvsVideoTrack nvsVideoTrack = mBeforeTimeLine.getVideoTrackByIndex(0);
                    int curIndex = data.getIntExtra(INTENT_EDIT_CLIP_INDEX,0);
                    long newTrimIn = data != null ? data.getLongExtra(INTENT_TRAM_IN, 0) : 0;
                    long newTrimOut = data != null ? data.getLongExtra(INTENT_TRAM_OUT, 0) : 0;
                    NvsVideoClip clipByIndex = nvsVideoTrack.getClipByIndex(curIndex);
                    if(null != clipByIndex){
                        clipByIndex.changeTrimInPoint(newTrimIn,true);
                        clipByIndex.changeTrimOutPoint(newTrimOut,true);
                    }
                    //修改了视频长度，后面的素材的入点发生了改变,
                    for(int i=0 ;i<mTemplateClips.size(); i++){
                        TemplateClip templateClip = mTemplateClips.get(i);
                        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(i);
                        parseVideoClip(templateClip, nvsVideoClip);
                    }

                    CutEditorVpFragment cutEditorVpFragment = mFragmentList.get(mTableLayout.getSelectedTabPosition());
                    cutEditorVpFragment.changClipTrimInfo(mTemplateClips,newTrimIn,newTrimOut);

                    //重新获取字幕的相关信息，片段发生变化，出入点改变了
                    List<TemplateCaptionDesc> templateCaptions = EditorController.getInstance().getTemplateCaptions(mBeforeTimeLine);
                    if(null != mFragmentList && mFragmentList.size()>1){
                        mFragmentList.get(1).onCaptionDataUpDate(templateCaptions);
                    }
//                    Log.d("lpf",mBeforeTimeLine.getDuration()+"");
                    String nvStringTime = FormatUtils.microsecond2Time(mBeforeTimeLine.getDuration());
                    mPlayControlView.setCurrentText(nvStringTime);
                    int duration = (int) (mBeforeTimeLine.getDuration() / 1000f);
                    mPlayControlView.setMax(duration);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                EditorController.getInstance().setNvsTimeline(mBeforeTimeLine);
                TimelineUtil.loadTimelineToTemplate(EditorController.getInstance().getNvsTimeline());
                mBaseVideoFragment.connectTimelineWithLiveWindow();
            }
            EditorController.getInstance().seekTimeline(mJumpTime);
            setPlayControlViewProgress(mJumpTime);
        } else if (requestCode == CustomConstants.REQUEST_CODE_1) {
            if (data != null) {
                if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)) {
                    TemplateClip templateClip = data.getParcelableExtra(CustomConstants.TEMPLATE_CLIP);
                    if (templateClip != null) {
                        TemplateClip oldClip = getTemplateClip(templateClip.getTrackIndex(), templateClip.getInPoint());
                        oldClip.update(templateClip);
                    }
                } else if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)) {
                    TemplateClip newTemplateClip = data.getParcelableExtra(CustomConstants.TEMPLATE_CLIP);
                    TemplateClip oldClip = getTemplateClip(newTemplateClip.getTrackIndex(), newTemplateClip.getInPoint());
                    oldClip.update(newTemplateClip);

                    int replaceIndex = data.getIntExtra(CustomConstants.TEMPLATE_REPLACE_CLIP_INDEX,-1);
                    if (mBeforeTimeLine == null) {
                        int ratio = EditorController.getInstance().getRatio();
                        mBeforeTimeLine = TimelineUtil.newTimeline(EditorController.getInstance().getVideoResolutionByRatio(ratio));
                        LogUtils.e("failed to create timeline");
                        return;
                    }
                    NvsVideoTrack nvsVideoTrack = mBeforeTimeLine.getVideoTrackByIndex(0);
                    if (nvsVideoTrack == null) {
                        LogUtils.e("failed to append nvsVideoTrack");
                        return;
                    }
                    nvsVideoTrack.setVolumeGain(0,0);
                    //需要替換的素材
                    NvsVideoClip clipByIndex = nvsVideoTrack.getClipByIndex(replaceIndex);
                    if(null != clipByIndex){
                        //changeFilePath 只修改文件路径，原来的trim都不变
                        clipByIndex.changeFilePath(newTemplateClip.getFilePath());
                        clipByIndex.changeTrimInPoint(0,true);
                        clipByIndex.changeTrimOutPoint(newTemplateClip.getDuration(),true);
                    }
                    if(mTemplateClips.size() == nvsVideoTrack.getClipCount()){

                        for(int i=0 ;i<mTemplateClips.size(); i++){
                            TemplateClip templateClip = mTemplateClips.get(i);
                            NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(i);
                            parseVideoClip(templateClip, nvsVideoClip);
                        }
                    }
                    CutEditorVpFragment cutEditorVpFragment = mFragmentList.get(mTableLayout.getSelectedTabPosition());
                    cutEditorVpFragment.changClipTrimInfo(mTemplateClips,0,newTemplateClip.getDuration());
                    EditorController.getInstance().setNvsTimeline(mBeforeTimeLine);

                    //辅助timeline也需要对应修改
                    NvsTimeline auxiliaryTimeline = EditorController.getInstance().getAuxiliaryTimeline();
                    if(null != auxiliaryTimeline){
                        NvsVideoTrack videoTrackByIndex = auxiliaryTimeline.getVideoTrackByIndex(0);
                        if(null != videoTrackByIndex){
                            NvsVideoClip auxiliaryClip = videoTrackByIndex.getClipByIndex(replaceIndex);
                            if(null != auxiliaryClip){
                                //changeFilePath 只修改文件路径，原来的trim都不变
                                auxiliaryClip.changeFilePath(newTemplateClip.getFilePath());
                                auxiliaryClip.changeTrimInPoint(0,true);
                                auxiliaryClip.changeTrimOutPoint(newTemplateClip.getDuration(),true);
                            }
                        }
                    }
                    //重新获取字幕的相关信息，片段发生变化，出入点改变了
                    List<TemplateCaptionDesc> templateCaptions = EditorController.getInstance().getTemplateCaptions(mBeforeTimeLine);
                    if(null != mFragmentList && mFragmentList.size()>1){
                        mFragmentList.get(1).onCaptionDataUpDate(templateCaptions);
                    }
                    String nvStringTime = FormatUtils.microsecond2Time(mBeforeTimeLine.getDuration());
                    mPlayControlView.setCurrentText(nvStringTime);
                    int duration = (int) (mBeforeTimeLine.getDuration() / 1000f);
                    mPlayControlView.setMax(duration);
                    mBaseVideoFragment.connectTimelineWithLiveWindow();
                }

            }
        }
        if(mTemplateType.equalsIgnoreCase(Template.TYPE_TEMPLATE_FREE)){

           /* if(null != mFragmentList){
                for (Fragment fragment : mFragmentList) {
                    CutEditorVpFragment cutEditorVpFragment = (CutEditorVpFragment) fragment;
                    cutEditorVpFragment.updateVideoData(mTemplateClips);
                }
            }*/
        }else{

            for (Fragment fragment : mFragmentList) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 避免加两次片头，删除后面加的这个
     * @param mBeforeTimeLine 当前的timeline
     * @param mTemplateClips 当前选择的素材，包含首次创建timeline后添加的片头片尾
     */
    private void deleteAddedHeadAndTail(NvsTimeline mBeforeTimeLine, List<TemplateClip> mTemplateClips) {
        if(null == mBeforeTimeLine){
            return;
        }
        if(CommonUtils.isEmpty(mTemplateClips)){
            return;
        }
        NvsVideoTrack videoTrackByIndex = mBeforeTimeLine.getVideoTrackByIndex(0);
        if(null == videoTrackByIndex){
            return;
        }
        int clipCount = videoTrackByIndex.getClipCount();
        //说明又加了一次片头
        if(clipCount>mTemplateClips.size()){
            videoTrackByIndex.removeClip(clipCount-1,false);
            videoTrackByIndex.removeClip(0,false);
        }
    }

    private void parseVideoClip(TemplateClip templateClip, NvsVideoClip nvsVideoClip) {
        if (nvsVideoClip != null) {
            templateClip.setInPoint(nvsVideoClip.getInPoint());
            templateClip.setOutPoint(nvsVideoClip.getOutPoint());
            templateClip.setTrimIn(nvsVideoClip.getTrimIn());
            templateClip.setTrimOut(nvsVideoClip.getTrimOut());
            templateClip.setDuration(nvsVideoClip.getTrimOut() - nvsVideoClip.getTrimIn());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mResolutionBottomDialog != null) {
            mResolutionBottomDialog.dismiss();
        }
    }

    @Override
    public void seekPosition(long position) {
        setPlayControlViewProgress(position);
    }

    private void setPlayControlViewProgress(long position) {
        if (mState != TailorView.FROM_USER) {
            mPlayControlView.setProgress((int) (position / 1000f));
        }
    }
}