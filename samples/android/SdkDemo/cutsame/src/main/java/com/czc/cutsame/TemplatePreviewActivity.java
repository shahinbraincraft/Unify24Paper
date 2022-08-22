package com.czc.cutsame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.czc.cutsame.bean.ExportTemplateDescInfo;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.czc.cutsame.bean.TemplateClip;
import com.czc.cutsame.fragment.iview.TemplateView;
import com.czc.cutsame.fragment.presenter.TemplatePresenter;
import com.czc.cutsame.util.ConfigUtil;
import com.czc.cutsame.util.CustomConstants;
import com.czc.cutsame.util.RatioUtil;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meishe.base.model.BaseMvpActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.FileIOUtils;
import com.meishe.base.utils.FormatUtils;
import com.meishe.base.utils.GsonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.engine.editor.EditorController;
import com.meishe.http.bean.UserInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/4
 * desc   :模板预览页面
 * Template Preview Page
 */
public class TemplatePreviewActivity extends BaseMvpActivity<TemplatePresenter> implements TemplateView {
    public static boolean IS_DEBUG = false;
    private ImageView mIvBack, mIvStop;
    private VideoView mVideoView;
    private TextView mTvUserName;
    private TextView mTvDescription;
    private TextView mTvUsedNum;
    private TextView mTvClipSame;
    private Template mTemplate;
    private String mTemplatePath;
    private boolean firstLoad;
    private boolean mIsFromMine;
    private StringBuilder mTemplateId;
    private List<TemplateClip> mClipList = null;
    private boolean mIsFromLocal;

    @Override
    protected int bindLayout() {
        return R.layout.activity_template_preview;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        firstLoad = true;
        Intent intent = getIntent();
        if (intent != null) {
            mTemplate = intent.getParcelableExtra(CustomConstants.DATA_TEMPLATE);
            mIsFromMine = intent.getBooleanExtra(CustomConstants.TEMPLATE_IS_FROM_MINE, false);
            mIsFromLocal = intent.getBooleanExtra(CustomConstants.TEMPLATE_IS_FROM_LOCAL, false);
            if (mIsFromMine) {
                mTemplatePath = mTemplate.getPackageUrl();
            }
        }
    }

    @Override
    protected void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mIvStop = findViewById(R.id.iv_stop_hint);
        mVideoView = findViewById(R.id.video_view);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvDescription = findViewById(R.id.tv_description);
        mTvUsedNum = findViewById(R.id.tv_used_num);
        mTvClipSame = findViewById(R.id.tv_clip);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvBack.getLayoutParams();
        layoutParams.topMargin = (int) (ScreenUtils.getStatusBarHeight() + getResources().getDimension(R.dimen.title_margin_top));
        mIvBack.setLayoutParams(layoutParams);
        initVideoViewSize();
        initListener();
    }

    private void initVideoViewSize() {
        ViewGroup.LayoutParams videoParams = mVideoView.getLayoutParams();
        if (videoParams != null && mTemplate != null) {
            float ratio = mTemplate.getRatio();
            int screenWidth = ScreenUtils.getScreenWidth();
            int screenHeight = ScreenUtils.getScreenHeight();
            int height = (int) (screenWidth * ratio);
            int width = (int) (screenHeight / ratio);
            float viewRatio = screenWidth * 1.0F / screenHeight;
            if(ratio > viewRatio) {//宽对齐
                videoParams.width = screenWidth;
                videoParams.height = height;
            } else {
                videoParams.width = width;
                videoParams.height = screenHeight;
            }
            mVideoView.setLayoutParams(videoParams);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //VideoView的点击事件无效，而且只有Down事件,所以用onTouch
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mVideoView.isPlaying()) {
                        mVideoView.pause();
                        mIvStop.setVisibility(View.VISIBLE);
                    } else {
                        mIvStop.setVisibility(View.GONE);
                        mVideoView.start();
                    }
                }
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    // mVideoView.setZOrderOnTop(true);//解决有时候因为透明，能看到上个页面的问题。但是一些view会被覆盖。
                    //目前的解决办法是给VideoView加黑色背景，然后在实际播放的时候再把背景祛除。
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START && firstLoad) {
                            firstLoad = false;
                            mVideoView.setBackgroundColor(0);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        });
        mTvClipSame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mTemplatePath) && mClipList != null && mTemplateId != null) {
                    Intent intent = new Intent(TemplatePreviewActivity.this, MaterialSelectActivity.class);
                    intent.putExtra(CustomConstants.TEMPLATE_ID, mTemplateId.toString());
                    intent.putExtra(CustomConstants.DATA_TEMPLATE, mTemplate);
                    Bundle bundle=new Bundle();
                    bundle.putString(CustomConstants.TEMPLATE_TYPE, mTemplate.getType());
                    intent.putParcelableArrayListExtra(CustomConstants.TEMPLATE_CLIP_LIST, (ArrayList<? extends Parcelable>) mClipList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //finish();
                } else {
                    ToastUtils.showShort(R.string.template_not_ready);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    @Override
    protected void requestData() {
        if (mTemplate != null) {
            if (IS_DEBUG) {
                mTemplatePath = mTemplate.getPackageUrl();
                startInstallAssets(mTemplatePath);
            } else {
                String videoPath = null;

                UserInfo userInfo = mTemplate.getUserInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getNickname())) {
                    String name = "@" + userInfo.getNickname();
                    mTvUserName.setText(name);
                } else {
                    mTvUserName.setText("@" + getString(R.string.cut_ms_template));
                }
                mTvDescription.setText(mTemplate.getDescription());
                if (mTemplate.getType().equals(Template.TYPE_TEMPLATE_FREE)){
                    if (mTemplate.getUseNum() == -1) {
                        mTvUsedNum.setText(String.format(getString(R.string.template_duration),
                                FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), mTemplate.getShotsNumber()));
                    } else {
                        mTvUsedNum.setText(String.format(getString(R.string.template_duration_used_num_free),
                                formatNumber(mTemplate.getUseNum())));
                    }
                }else if (mTemplate.getType().equals(Template.TYPE_TEMPLATE_STANDER)){
                    if (mTemplate.getUseNum() == -1) {
                        mTvUsedNum.setText(String.format(getString(R.string.template_duration),
                                FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), mTemplate.getShotsNumber()));
                    } else {
                        mTvUsedNum.setText(String.format(getString(R.string.template_duration_used_num),
                                FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), formatNumber(mTemplate.getUseNum()), mTemplate.getShotsNumber()));
                    }
                }
//


                if (!mIsFromMine) {
                    if (mPresenter.checkTemplateUpdate(mTemplate)) {
                        //需要更新 need to be updated
                        videoPath = mTemplate.getPreviewVideoUrl();
                    } else {
                        //不需要更新 not update
                        videoPath = mPresenter.getVideoPath(mTemplate.getId());
                    }
                } else {
                    if (ConfigUtil.isToC() || mIsFromLocal) {
                        videoPath = mTemplate.getPreviewVideoUrl();
                        startInstallAssets(mTemplatePath);
                    } else {
                        if (!mPresenter.checkTemplateUpdate(mTemplate)) {
                            //bu需要更新 need to be updated
                            videoPath = mPresenter.getVideoPath(mTemplate.getId());
                        }
                    }
                }
                if (!TextUtils.isEmpty(videoPath)) {
                    mVideoView.setVideoPath(videoPath);
                    mVideoView.start();
                }
            }
        }
    }

    private void updateDataFromConfig(String infoPath) {
        if (TextUtils.isEmpty(infoPath)) {
            return;
        }
        String jsonStr = FileIOUtils.readFile2String(infoPath, "utf-8");
        ExportTemplateDescInfo exportTemplateDescInfo = GsonUtils.getGson().fromJson(jsonStr, ExportTemplateDescInfo.class);
        if (exportTemplateDescInfo != null) {
            mTemplate.setDuration(exportTemplateDescInfo.getDuration() / 1000);
            //todo 暂时不读json文件
//            mTemplate.setDescription(exportTemplateDescInfo.getDescription());
            int aspectRatio = RatioUtil.getAspectRatio(exportTemplateDescInfo.getDefaultAspectRatio());
            mTemplate.setRatio(aspectRatio);
            mTemplate.setDefaultAspectRatio(aspectRatio);
            if (mIsFromMine) {
                mTvUsedNum.setText(String.format(getString(R.string.template_duration),
                        FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), mTemplate.getShotsNumber()));
            }
            mTvDescription.setText(mTemplate.getDescription());
        }
        initVideoViewSize();
        String videoPath = mTemplate.getPreviewVideoUrl();
        if (!TextUtils.isEmpty(videoPath)) {
            mVideoView.setVideoPath(videoPath);
            mVideoView.start();
        }
    }

    @Override
    public void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList) {

    }

    @Override
    public void onTemplateListBack(List<Template> templateList) {

    }

    @Override
    public void onMoreTemplateBack(List<Template> templateList) {

    }

    @Override
    public void onDownloadTemplateSuccess(String templatePath, boolean isTemplate) {
        if (isTemplate) {
            mTemplatePath = templatePath;
            startInstallAssets(mTemplatePath);
        } else {
            if (templatePath.endsWith("json")) {
                updateDataFromConfig(templatePath);
            }
        }
    }

    private void startInstallAssets(String templatePath) {
        mTemplateId = new StringBuilder();
        try {
            String packageId = templatePath;
            packageId = packageId.substring(packageId.lastIndexOf("/") + 1);
            packageId = packageId.split("\\.")[0];
            //由于升级的存在，这里取巧了，先卸载后安装。 Because of the existence of upgrade, here take advantage of, install after uninstall first
            if (!mIsFromMine) {
                EditorController.getInstance().uninstallAssetPackage(packageId, EditorController.ASSET_PACKAGE_TYPE_TEMPLATE);
            }
        } catch (Exception e) {
            LogUtils.e("Exception=" + e);
        }
        EditorController.getInstance().installAssetPackagedNoSynchronous(templatePath, EditorController.ASSET_PACKAGE_TYPE_TEMPLATE, mTemplateId,
                new NvsAssetPackageManager.AssetPackageManagerCallback() {
                    @Override
                    public void onFinishAssetPackageInstallation(String packageId, String filePath, int packageType, int error) {
                        if ((error == EditorController.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) || (error == EditorController.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED)) {
                            int groupIndex = -1;
                            List<NvsAssetPackageManager.NvsTemplateFootageDesc> templateList = EditorController.getInstance().getTemplateFootageDescVideo(mTemplateId.toString());
                            if (templateList != null) {
                                mClipList = new ArrayList<>();
                                for (NvsAssetPackageManager.NvsTemplateFootageDesc footage : templateList) {
                                    if (!footage.canReplace) {
                                        //audio类型无用。

                                        continue;
                                    }
                                    ArrayList<NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo> correspondingClipInfos = footage.correspondingClipInfos;
                                    boolean hasGroup = false;
                                    if (!CommonUtils.isEmpty(correspondingClipInfos) && correspondingClipInfos.size() >= 2) {
                                        hasGroup = true;
                                        groupIndex += 1;
                                    }
                                    for (NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo clipInfo : correspondingClipInfos) {
                                        mClipList.add(new TemplateClip()
                                                .setDuration(clipInfo.outpoint - clipInfo.inpoint)
                                                .setInPoint(clipInfo.inpoint)
                                                .setNeedReverse(clipInfo.needReverse)
                                                .setTrackIndex(clipInfo.trackIndex)
                                                .setType(footage.type)
                                                .setHasGroup(hasGroup)
                                                .setGroupIndex(groupIndex)
                                                .setFootageId(footage.id));
                                    }
                                }
                                Collections.sort(mClipList);
                                mTemplate.setShotsNumber(mClipList == null ? 0 : mClipList.size());
//                                if (mTemplate.getUseNum() == -1) {
//                                    mTvUsedNum.setText(String.format(getString(R.string.template_duration),
//                                            FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), mTemplate.getShotsNumber()));
//                                } else {
//                                    mTvUsedNum.setText(String.format(getString(R.string.template_duration_used_num),
//                                            FormatUtils.sec2Time((int) (mTemplate.getDuration() / 1000)), formatNumber(mTemplate.getUseNum()), mClipList.size()));
//                                }
                            }
                        }
                    }

                    @Override
                    public void onFinishAssetPackageUpgrading(String s, String s1, int i, int i1) {

                    }
                });
    }

    private DecimalFormat mDecimalFormat;

    /**
     * Format number string.
     * 格式字符串数
     *
     * @param number the number
     * @return the string
     */
    public String formatNumber(int number) {
        if (mDecimalFormat == null) {
            mDecimalFormat = new DecimalFormat("0.0");
        }
        if (number < 10000) {
            return number + "";
        }
        return mDecimalFormat.format(number / 10000f) + getString(R.string.num_unit_w);
    }
}
