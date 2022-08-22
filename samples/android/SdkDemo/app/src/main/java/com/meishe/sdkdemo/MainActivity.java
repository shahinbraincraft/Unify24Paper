package com.meishe.sdkdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.constants.AndroidOS;
import com.meishe.base.utils.GsonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.base.view.MSLiveWindow;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.boomrang.BoomRangActivity;
import com.meishe.sdkdemo.capture.CaptureActivity;
import com.meishe.sdkdemo.capturescene.CaptureSceneActivity;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.cutsame.CutSameActivity;
import com.meishe.sdkdemo.dialog.PrivacyPolicyDialog;
import com.meishe.sdkdemo.douvideo.DouVideoCaptureActivity;
import com.meishe.sdkdemo.edit.data.BackupData;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.feedback.FeedBackActivity;
import com.meishe.sdkdemo.glitter.GlitterEffectActivity;
import com.meishe.sdkdemo.main.MainViewPagerFragment;
import com.meishe.sdkdemo.main.MainViewPagerFragmentData;
import com.meishe.sdkdemo.main.MainWebViewActivity;
import com.meishe.sdkdemo.main.OnItemClickListener;
import com.meishe.sdkdemo.main.SpannerViewpagerAdapter;
import com.meishe.sdkdemo.main.bean.AdBeansFormUrl;
import com.meishe.sdkdemo.mimodemo.MimoActivity;
import com.meishe.sdkdemo.mimodemo.common.utils.MeicamContextWrap;
import com.meishe.sdkdemo.monitor.LogPrinterListener;
import com.meishe.sdkdemo.monitor.MSPrinter;
import com.meishe.sdkdemo.musicLyrics.MultiVideoSelectActivity;
import com.meishe.sdkdemo.particle.ParticleCaptureActivity;
import com.meishe.sdkdemo.photoalbum.PhotoAlbumActivity;
import com.meishe.sdkdemo.selectmedia.SelectMediaActivity;
import com.meishe.sdkdemo.superzoom.SuperZoomActivity;
import com.meishe.sdkdemo.themeshoot.ThemeSelectActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaConstant;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.SharedPreferencesUtils;
import com.meishe.sdkdemo.utils.SpUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.license.LicenseInfo;
import com.meishe.utils.SystemUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meishe.sdkdemo.BuildConfig.UMENG_KEY;
import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_FU;
import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS_ST;
import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS_ST_SUPER;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_NONE;


/**
 * MainActivity class
 * 主页面
 *
 * @author gexinyu
 * @date 2018-05-24
 */
public class MainActivity extends BasePermissionActivity implements OnItemClickListener, LogPrinterListener {
    private static final String TAG = "MainActivity";
    /**
     * 首页tab网络地址
     * Activity tab url
     */
    public static final String AD_SPANNER_URL = "https://vsapi.meishesdk.com/app/index.php?command=listAdvertisement&page=0&pageSize=10";
    public static final int REQUEST_CAMERA_PERMISSION_CODE = 200;
    public static final int INIT_ARSCENE_COMPLETE_CODE = 201;
    public static final int INIT_ARSCENE_FAILURE_CODE = 202;
    public static final int AD_SPANNER_CHANGE_CODE = 203;
    private ImageView mIvSetting;
    private ImageView mIvFeedBack;
    private RelativeLayout layoutVideoCapture;
    private RelativeLayout layoutVideoEdit;
    private ViewPager mainViewPager;
    private RadioGroup radioGroup;
    private TextView mainVersionNumber;
    private int spanCount = 8;
    private View clickedView = null;

    /*
     * 人脸初始化完成的标识
     * Face initialization completed logo
     * */
    private boolean arSceneFinished = false;
    /*
     * 记录人脸模块正在初始化
     * Recording face module is initializing
     * */
    private boolean initARSceneing = true;
    /*
     * 防止页面重复点击标识
     * Prevent pages from repeatedly clicking on logos
     * */
    private boolean isClickRepeat = false;

    /**
     * SDK普通版
     * <p>
     * SDK Normal Edition
     */
    private int mCanUseARFaceType = HUMAN_AI_TYPE_NONE;

    private HandlerThread mHandlerThread;

    private MainActivityHandler mHandler = new MainActivityHandler(this);
    private ViewPager mBannerViewPager;
    private SpannerViewpagerAdapter mSpannerViewpagerAdapter;
    private List<AdBeansFormUrl.AdInfo> mAdInfos = new ArrayList<>();
    private Runnable mAdRunnable;


    class MainActivityHandler extends Handler {
        WeakReference<MainActivity> mWeakReference;

        public MainActivityHandler(MainActivity mainActivityContext) {
            mWeakReference = new WeakReference<>(mainActivityContext);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case INIT_ARSCENE_COMPLETE_CODE:
                        /*
                         *  初始化ARScene 完成
                         * Initialization of ARScene completed
                         * */
                        arSceneFinished = true;
                        initARSceneing = false;
                        break;
                    case INIT_ARSCENE_FAILURE_CODE:
                        /*
                         *  初始化ARScene 失败
                         * Initializing ARScene failed
                         * */
                        arSceneFinished = false;
                        initARSceneing = false;
                        break;
                    case AD_SPANNER_CHANGE_CODE:
                        /*
                         * 广告切换
                         * Advertising switch
                         * */
                        if ((mBannerViewPager != null) && (mAdInfos != null) && (mAdInfos.size() > 1)) {
                            mBannerViewPager.setCurrentItem(mBannerViewPager.getCurrentItem() + 1);
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }

    @Override
    protected int initRootView() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return R.layout.activity_main;
        }
        return R.layout.activity_main;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initViews() {
        mIvSetting = (ImageView) findViewById(R.id.iv_main_setting);
        mIvFeedBack = (ImageView) findViewById(R.id.iv_main_feedback);
        layoutVideoCapture = (RelativeLayout) findViewById(R.id.layout_video_capture);
        layoutVideoEdit = (RelativeLayout) findViewById(R.id.layout_video_edit);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        radioGroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        mainVersionNumber = (TextView) findViewById(R.id.main_versionNumber);
        mBannerViewPager = (ViewPager) findViewById(R.id.banner_viewpager);
    }

    @Override
    protected void initData() {
        MeicamContextWrap.getInstance().setContext(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //不允许开启开发者选项中的不保留活动，SDK中有些类是单例，这个功能会影响正常使用。
            //The unreserved activity in the developer option is not allowed to be enabled. Some classes in the SDK are singletons. This feature can interfere with normal use.
            int alwaysFinish = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
            if (alwaysFinish == 1) {
                Dialog dialog = null;
                dialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.no_back_activity_message)
                        .setNegativeButton(R.string.no_back_activity_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MainActivity.this.finish();
                            }
                        }).setPositiveButton(R.string.no_back_activity_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            }
        }
        initFragmentAndView();
        NvsStreamingContext.SdkVersion sdkVersion = NvsStreamingContext.getInstance().getSdkVersion();
        String sdkVersionNum = String.valueOf(sdkVersion.majorVersion) + "." + String.valueOf(sdkVersion.minorVersion) + "." + String.valueOf(sdkVersion.revisionNumber);
        mainVersionNumber.setText(String.format(getResources().getString(R.string.versionNumber), sdkVersionNum));
        initSpannerViewData();
        mAdRunnable = new AdRunnable();

        String model = Build.MODEL;
        HttpManager.getSettingData(null, model, Constants.APP_ID, "2", new SettingCallback());
    }

    /**
     * 商汤授权采取获取线上授权的方法，每次进入检查是否需要更新。
     * SenseTime authorization to take access to the method of online authorization, each entry check whether the need to update
     */
    private void updateLicenseFile() {
        if (!NetWorkUtil.isNetworkConnected(mContext)) {
            ToastUtil.showToast(MainActivity.this, R.string.network_not_available);
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "licUrl:" + Constants.LICENSE_FILE_URL);
        }
        HttpManager.getOldObjectGet(Constants.LICENSE_FILE_URL, new TempStringCallBack() {

            @Override
            public void onResponse(String stringResponse) {
                final LicenseInfo licenseInfo = GsonUtils.fromJson(stringResponse, LicenseInfo.class);
                if (licenseInfo == null) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int code = licenseInfo.getCode();
                        if (code != 1) {
                            return;
                        }
                        LicenseInfo.LicInfo data = licenseInfo.getData();
                        if (data == null) {
                            return;
                        }

                        SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_START_TIMESTAMP, data.getStartTimestamp());
                        SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_END_TIMESTAMP, data.getEndTimestamp());
                        SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_URL, data.getAuthorizationFileUrl());

                        Log.d(TAG, "授权文件数据更新成功");

                        String authorizationFileUrl = data.getAuthorizationFileUrl();
                        if (TextUtils.isEmpty(authorizationFileUrl)) {
                            return;
                        }
                        downloadAuthorFile(authorizationFileUrl);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.e(throwable);
            }
        });

    }

    /**
     * 授权文件下载
     * downloadAuthorFile
     */
    private void downloadAuthorFile(String fileUrl) {
        String[] split = fileUrl.split("/");
        //String filePath = FileUtils.getExternalFilePath(getApplicationContext(), "NvStreamingSdk" + File.separator + "License");
        String filePath = PathUtils.getLicenseFileFolder();
        HttpManager.download(fileUrl, fileUrl, filePath, split[split.length - 1], new SimpleDownListener(fileUrl) {
            @Override
            public void onError(Progress progress) {
                super.onError(progress);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                Log.d(TAG, "授权文件下载成功 " + "onResponse------------------------------" + file.getName());
                if (file == null || !file.isFile()) {
                    return;
                }
                SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_PATH, "" + file.getAbsoluteFile());
                //下载成功之后进行授权
                Log.d(TAG, "开始授权 " + "response------------------------------" + file.getAbsoluteFile());
                initARSceneEffect(file.getAbsolutePath());
            }
        });

    }

    /**
     * 轮播图数据初始化
     * Rotating map data initialization
     */
    private void initSpannerViewData() {
        SystemUtils.isZh(MainActivity.this);
        mSpannerViewpagerAdapter = new SpannerViewpagerAdapter(MainActivity.this, mAdInfos);
        mBannerViewPager.setAdapter(mSpannerViewpagerAdapter);
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % (mAdInfos.size() == 0 ? 1 : mAdInfos.size()));
        mBannerViewPager.setCurrentItem(item);
        if (NetWorkUtil.isNetworkConnected(this)) {
            HttpManager.getOldObjectGet(AD_SPANNER_URL, new TempStringCallBack() {
                @Override
                public void onResponse(String stringResponse) {
                    if (stringResponse != null) {
                        final AdBeansFormUrl adBean = GsonUtils.fromJson(stringResponse, AdBeansFormUrl.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adBean != null && adBean.getErrNo() == 0) {
                                    mAdInfos = adBean.getList();
                                    setSpannerListener();
                                    if (mSpannerViewpagerAdapter != null) {
                                        mSpannerViewpagerAdapter.setAdapterData(mAdInfos);
                                        /*
                                         * 开启广告定时轮播
                                         * Turn on ad timing rotation
                                         * */
                                        if ((mAdRunnable != null) && (mAdInfos.size() > 1)) {
                                            mHandler.removeCallbacks(mAdRunnable);
                                            mHandler.postDelayed(mAdRunnable, 5000);
                                        }
                                    }
                                }
                            }
                        });

                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable != null) {

                    }
                }
            });
        }
    }

    class AdRunnable implements Runnable {
        @Override
        public void run() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(AD_SPANNER_CHANGE_CODE);
                mHandler.postDelayed(this, 5000);
            }
        }
    }

    private void setSpannerListener() {
        if (mSpannerViewpagerAdapter != null) {
            mSpannerViewpagerAdapter.setSpannerClickCallback(new SpannerViewpagerAdapter.SpannerClickCallback() {
                @Override
                public void spannerClick(int position, AdBeansFormUrl.AdInfo adInfo) {
                    if (Util.isFastClick()) {
                        return;
                    }
                    boolean notNull = (adInfo != null) && (!TextUtils.isEmpty(adInfo.getAdvertisementUrl())) && (!TextUtils.isEmpty(adInfo.getAdvertisementUrlEn()));
                    if (notNull) {
                        Bundle bundle = new Bundle();
                        bundle.putString("URL", SystemUtils.isZh(getApplicationContext()) ? adInfo.getAdvertisementUrl() : adInfo.getAdvertisementUrlEn());
                        Intent intent = new Intent(MainActivity.this, MainWebViewActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent, bundle);
                    }
                }
            });
        }
    }

    private void initFragmentAndView() {
        /*
         * 按照每页个数，生成索引和名称，关系映射
         * According to the number of pages, generate the index and name, and the relationship mapping
         * */
        Map<Integer, List<String>> map = subListByItemCount();
        List<Fragment> mFragmentList = getSupportFragmentManager().getFragments();
        if (mFragmentList == null || mFragmentList.size() == 0) {
            mFragmentList = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                List<String> nameList = map.get(i);
                MainViewPagerFragment mediaFragment = new MainViewPagerFragment();
                Bundle bundle = new Bundle();
                /*
                 *  功能图标，实体类集合
                 * Function icon, entity class collection
                 * */
                ArrayList<MainViewPagerFragmentData> list = initFragmentDataById(nameList, i);
                bundle.putParcelableArrayList("list", list);
//              bundle.putInt("span", spanCount);
                mediaFragment.setArguments(bundle);
                mFragmentList.add(mediaFragment);
            }
        }
        for (int i = 0; i < map.size(); i++) {
            addRadioButton(i);
        }
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, null);
        mainViewPager.setAdapter(fragmentPagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setRadioButtonState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 生成每页显示的功能标题集合
     * <p>
     * Generate a collection of feature titles displayed per page
     *
     * @return 索引和对应索引页的标题集合；Index and title set of corresponding index pages
     */
    private Map<Integer, List<String>> subListByItemCount() {
        String[] fragmentItems = getResources().getStringArray(R.array.main_fragment_item);
        Map<Integer, List<String>> map = new HashMap<>();
        List<String> list = Arrays.asList(fragmentItems);
        int count = (list.size() - 1) / spanCount + 1;
        for (int i = 0; i < count; i++) {
            int endTime = list.size() <= (i + 1) * spanCount ? list.size() : (i + 1) * spanCount;
            int startTime = i == 0 ? i : i * spanCount;
            List<String> childList = list.subList(startTime, endTime);
            map.put(i, childList);
        }
        return map;
    }

    private void addRadioButton(int i) {
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ScreenUtils.dip2px(this, 5), ScreenUtils.dip2px(this, 5));
        lp.setMargins(0, 0, ScreenUtils.dip2px(this, 5), 0);
        radioGroup.addView(initRadioButton(i), lp);
    }

    private RadioButton initRadioButton(int i) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(getResources().getIdentifier("main_radioButton" + i, "id", getPackageName()));
        radioButton.setBackground(getResources().getDrawable(R.drawable.activity_main_checkbox_background));
        radioButton.setButtonDrawable(null);
        radioButton.setChecked(i == 0);
        return radioButton;
    }

    /**
     * @param names
     * @param fragmentCount 当前功能页索引；Index of current feature page
     * @return
     */
    private ArrayList<MainViewPagerFragmentData> initFragmentDataById(List<String> names, int fragmentCount) {
        /*
         * 当前页功能模块背景
         * Function page background of current page
         * */
        String[] fragmentItemsBackGround = getResources().getStringArray(R.array.main_fragment_background);
        List<String> listBackground = Arrays.asList(fragmentItemsBackGround);

        /*
         * 当前页功能模块图标
         * Current page function module icon
         * */
        String[] fragmentItemsImage = getResources().getStringArray(R.array.main_fragment_image);
        List<String> listImage = Arrays.asList(fragmentItemsImage);

        /*
         * 生成当前页面功能模块，实体类集合
         * Generate current page function module, entity class collection
         * */
        ArrayList<MainViewPagerFragmentData> list1 = new ArrayList<>();
        for (int i = 0, size = names.size(); i < size; i++) {
            int backGroundId = getResources().getIdentifier(listBackground.get(fragmentCount * 8 + i), "drawable", getPackageName());
            int imageId = getResources().getIdentifier(listImage.get(fragmentCount * 8 + i), "drawable", getPackageName());
            if (backGroundId != 0 && imageId != 0) {
                list1.add(new MainViewPagerFragmentData(backGroundId, names.get(i), imageId));
            }
        }
        return list1;
    }

    private void setRadioButtonState(int position) {
        RadioButton radioButton = (RadioButton) findViewById(getResources().getIdentifier("main_radioButton" + position, "id", getPackageName()));
        radioButton.setChecked(true);
    }

    @Override
    protected void initListener() {
        mIvSetting.setOnClickListener(this);
        mIvFeedBack.setOnClickListener(this);
        layoutVideoCapture.setOnClickListener(this);
        layoutVideoEdit.setOnClickListener(this);
        showPrivacyDialog();
        if (hasAllPermission()) {
            checkAuthorization();
        }
    }

    /**
     * 检查授权的方法，不同类型使用不同的授权。
     * 商汤的授权使用同一个授权文件，不区分普通或者高级
     */
    private void checkAuthorization() {
        //美摄自研人脸的人脸粒子需要商汤授权，加上一个判断
        if (BuildConfig.HUMAN_AI_TYPE.contains(BUILD_HUMAN_AI_TYPE_MS_ST)
                || BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
            //商汤授权
            long param = (long) SharedPreferencesUtils.getParam(mContext, Constants.KEY_SHARED_END_TIMESTAMP, 0L);
            long currentTimeMillis = System.currentTimeMillis();
            if (param == 0) {
                Log.d(TAG, "需要更新,更新完直接下载 param=" + param);
                updateLicenseFile();
            } else {
                if (currentTimeMillis < param) {
                    String licenseFilePath = (String) SharedPreferencesUtils.getParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_PATH, "");
                    Log.d(TAG, "不需要更新授权文件 直接进行授权 licenseFilePath:" + licenseFilePath);
                    if (TextUtils.isEmpty(licenseFilePath)) {
                        updateLicenseFile();
                        return;
                    }
                    File file = new File(licenseFilePath);
                    if (!file.exists()) {
                        updateLicenseFile();
                        return;
                    }
                    initARSceneEffect(licenseFilePath);
                } else {
                    Log.d(TAG, "需要更新,更新完直接下载 param=" + param);
                    updateLicenseFile();
                }
            }
        } else {
            //非商汤授权
            /*
             * 初始化人脸Model
             * Initialize Face Model
             * */
            initARSceneEffect();
        }
    }

    @Override
    public void onClick(View view) {
        if (isClickRepeat) {
            return;
        }
        isClickRepeat = true;
        switch (view.getId()) {
            /*
             * 设置
             * Set up
             * */
            case R.id.iv_main_setting:
                AppManager.getInstance().jumpActivity(this, SettingActivity.class, null);
                return;
            /*
             * 反馈
             * Feedback
             * */
            case R.id.iv_main_feedback:
                AppManager.getInstance().jumpActivity(this, FeedBackActivity.class, null);
                return;
            default:
                break;
        }
        /*
         * 没有权限，则请求权限
         * No permission, request permission
         * */
        if (!hasAllPermission()) {
            clickedView = view;
            checkPermissions();
        } else {
            doClick(view);
        }
    }

    private void initARSceneEffect() {
        initARSceneEffect("");
    }

    private void doClick(View view) {
        if (view == null)
            return;
        switch (view.getId()) {
            case R.id.iv_main_setting://setting
                AppManager.getInstance().jumpActivity(this, SettingActivity.class, null);
                break;

            case R.id.layout_video_capture://Shoot
                if (!initARSceneing) {
                    Bundle captureBundle = new Bundle();
                    captureBundle.putBoolean("initArScene", arSceneFinished);
                    AppManager.getInstance().jumpActivity(this, CaptureActivity.class, captureBundle);
                } else {
                    isClickRepeat = false;
                    ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                }
                break;

            case R.id.layout_video_edit://Edit
                Bundle editBundle = new Bundle();
                editBundle.putInt("visitMethod", Constants.FROMMAINACTIVITYTOVISIT);
                editBundle.putInt("limitMediaCount", -1);//-1表示无限可选择素材
                AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, editBundle);
                break;
            default:
                String tag = (String) view.getTag();
                if (tag.equals(getResources().getString(R.string.douYinEffects))) {
                    if (!initARSceneing) {
                        Bundle douyinBundle = new Bundle();
                        douyinBundle.putBoolean("initArScene", arSceneFinished);
                        douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_STRENGTH, 75);
                        if (arSceneFinished) {
                            douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_CHEEK, 150);
                            douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_EYE, 150);
                        }
                        AppManager.getInstance().jumpActivity(this, DouVideoCaptureActivity.class, douyinBundle);
                    } else {
                        isClickRepeat = false;
                        ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                    }
                } else if (tag.equals(getResources().getString(R.string.particleEffects))) {
                    AppManager.getInstance().jumpActivity(this, ParticleCaptureActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.captureScene))) {
                    AppManager.getInstance().jumpActivity(this, CaptureSceneActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.picInPic))) {
                    Bundle pipBundle = new Bundle();
                    pipBundle.putInt("visitMethod", Constants.FROMPICINPICACTIVITYTOVISIT);
                    /*
                     * 2表示选择两个素材
                     * 2 means select two materials
                     * */
                    pipBundle.putInt("limitMediaCount", 2); //
                    AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, pipBundle);
                } else if (tag.equals(getResources().getString(R.string.makingCover))) {
                    Bundle makeCoverBundle = new Bundle();
                    makeCoverBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_MAKE_COVER);
                    AppManager.getInstance().jumpActivity(this, SingleClickActivity.class, makeCoverBundle);
                } else if (tag.equals(getResources().getString(R.string.flipSubtitles))) {
                    Bundle flipBundle = new Bundle();
                    flipBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_FLIP_CAPTION);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    flipBundle.putInt("limitMediaCount", -1);
                    flipBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, flipBundle);
                } else if (tag.equals(getResources().getString(R.string.musicLyrics))) {
                    Bundle musicBundle = new Bundle();
                    musicBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    musicBundle.putInt("limitMediaCount", -1);
                    musicBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, musicBundle);
                } else if (tag.equals(getResources().getString(R.string.boomRang))) {
                    AppManager.getInstance().jumpActivity(this, BoomRangActivity.class);
                } else if (tag.equals(getResources().getString(R.string.pushMirrorFilm))) {
                    if (!initARSceneing) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            AppManager.getInstance().jumpActivity(this, SuperZoomActivity.class);
                        } else {
                            String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                            Util.showDialog(MainActivity.this, tipsInfo[0], getString(R.string.versionBelowTip));
                        }
                    }else {
                        isClickRepeat = false;
                        ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                    }
                } else if (tag.equals(getResources().getString(R.string.photosAlbum))) {
                    AppManager.getInstance().jumpActivity(this, PhotoAlbumActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.flashEffect))) {
                    AppManager.getInstance().jumpActivity(this, GlitterEffectActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.memo))) {
                    AppManager.getInstance().jumpActivity(this, MimoActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.theme_shoot))) {
                    AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), ThemeSelectActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.cutSame))) {
                    AppManager.getInstance().jumpActivity(this, CutSameActivity.class);
                } else if (tag.equals(getResources().getString(R.string.sequence_nesting))) {
                    //序列嵌套
                    Bundle sequenceBundle = new Bundle();
                    sequenceBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_SEQUENCE_NESTING);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    sequenceBundle.putInt("limitMediaCount", -1);
                    sequenceBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, sequenceBundle);
                } else if (tag.equals(getResources().getString(R.string.audio_equalizer))) {
                    //音频均衡器
                    Bundle audioBundle = new Bundle();
                    audioBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.FROM_MAIN_ACTIVITY_TO_AUDIO_EQUALIZER);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    audioBundle.putInt("limitMediaCount", -1);
                    audioBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, audioBundle);
                } else if (tag.equals(getResources().getString(R.string.quick_splicing))) {
                    //快速拼接
                    editBundle = new Bundle();
                    editBundle.putInt("visitMethod", Constants.FROM_QUICK_SPLICING_ACTIVITY);
                    editBundle.putInt("limitMediaCount", -1);//-1表示无限可选择素材
                    editBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, editBundle);
                } else {
                    String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                    Util.showDialog(MainActivity.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
                }
                break;
        }
    }

    /**
     * 初始化人脸相关
     * Initialize face correlation
     */
    private void initARSceneEffect(final String stLicenseFilePath) {
      /*  SenseArMaterialService.setServerType(SenseArServerType.DomesticServer);
        SenseArMaterialService.shareInstance().fetchAllGroups(new SenseArMaterialService.FetchGroupsListener() {
            @Override
            public void onSuccess(List<SenseArMaterialGroupId> list) {
                Log.e(TAG,"onSuccess  == s"+list.size());
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG,"onFailure  == s"+s);
            }
        });
        SenseArMaterialService.shareInstance().
        SenseArMaterialService.shareInstance().initialize(this);
        byte[] licData = SenseArMaterialService.shareInstance().getLicenseData();
        Log.e(TAG,"licData  =="+licData);*/
        /**
         * 检测SDK包是否有人脸模块
         *Detects whether the SDK package has a face module
         */
        mCanUseARFaceType = NvsStreamingContext.hasARModule();
        /*
         *  初始化AR Scene，全局只需一次
         * Initialize AR Scene, only once globally
         * */
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && !arSceneFinished) {
            if (mHandlerThread == null) {
                mHandlerThread = new HandlerThread("handlerThread");
                mHandlerThread.start();
            }
            Handler initHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String modelPath = null;
                    String licensePath = null;
                    String faceModelName = null;
                    String className = null;
                    /**
                     * 基础人脸模型文件
                     *Initialize the base face model file
                     */
                    if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
                        modelPath = "/facemode/ms/ms_face_v1.2.2.model";
                        faceModelName = "ms_face_v1.2.2.model";
                        className = "facemode/ms";
                        if (BuildConfig.FACE_MODEL == 240) {
                            modelPath = "/facemode/ms/240/ms_face240_v2.0.1.model";
                            faceModelName = "ms_face240_v2.0.1.model";
                            className = "facemode/ms/240";
                        }
                        licensePath = "";
                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST)) {
                        modelPath = "/facemode/st/106/M_SenseME_Face_Video_5.3.4.model";
                        faceModelName = "M_SenseME_Face_Video_5.3.4.model";
                        className = "facemode/st/106";
                        licensePath = stLicenseFilePath;

                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_FU)) {
                        modelPath = "/facemode/fu/fu_face_v3.model";
                        faceModelName = "fu_face_v3.model";
                        className = "facemode/fu";
                        licensePath = "assets:/facemode/fu/fu_face_v3.license";
                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST_SUPER)) {
                        modelPath = "/facemode/st/advance/M_SenseME_Face_Video_7.1.0.model";
                        faceModelName = "M_SenseME_Face_Video_7.1.0.model";
                        className = "facemode/st/advance";
                        licensePath = stLicenseFilePath;
                    }

                    /**
                     * 模型文件需要是本地文件路径，所以从assert内置拷贝到本地
                     *The model file needs to be a local file path, so copy it from assert built-in to local
                     */
                    boolean copySuccess = FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, className);
                    Logger.e(TAG, "copySuccess-->" + copySuccess);
                    File rootDir = getApplicationContext().getExternalFilesDir("");
                    if (AndroidOS.USE_SCOPED_STORAGE) {
                        rootDir = getApplicationContext().getFilesDir();
                    }


                    /**
                     * Streaming sdk 初始化基础人脸点位模型
                     * Streaming sdk initializes the basic face point model
                     */
                    String destModelDir = rootDir + modelPath;
                    boolean initSuccess = NvsStreamingContext.initHumanDetection(MSApplication.getContext(),
                            destModelDir, licensePath,
                            NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
                    Logger.e(TAG, "initSuccess-->" + initSuccess);

                    /**
                     * 对应的effect sdk的部分（如果没用到可以不填加）
                     * The part of the corresponding effect sdk (if not used, you can leave it blank)
                     */
                    NvsEffectSdkContext.initHumanDetection(MSApplication.getContext(),
                            destModelDir, licensePath,
                            NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE |
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE);


                    /**
                     * 假脸模型初始化，类似面具效果等，特效只跟随脸部动
                     * Fake face model initialization, similar to mask effects, etc., the special effects only follow the movement of the face
                     */
                    String fakeFacePath = "assets:/facemode/common/fakeface.dat";
                    boolean fakefaceSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakeFacePath);
                    Logger.e(TAG, "fakefaceSuccess-->" + fakefaceSuccess);

                    /**
                     * 对应的effect sdk的部分（如果没用到可以不填加）
                     * The part of the corresponding effect sdk (if not used, you can leave it blank)
                     */
                    NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakeFacePath);


                    /**
                     * 美妆1 模型初始化
                     * beauty 1 model_initialization
                     */
                    String makeUpPath = "assets:/facemode/common/makeup106_v1.0.3.dat";
                    if (BuildConfig.FACE_MODEL == 240) {
                        makeUpPath = "assets:/facemode/common/makeup240_v1.0.4.dat";
                    }
                    boolean makeupSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP, makeUpPath);
                    Logger.e(TAG, BuildConfig.FACE_MODEL + "makeupSuccess-->" + makeupSuccess);

                    /**
                     * 对应的effect sdk的部分（如果没用到可以不填加）
                     * The part of the corresponding effect sdk (if not used, you can leave it blank)
                     */
                    NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP, makeUpPath);


                    /**
                     * 美妆2模型初始化
                     *The makeup 2 model is initialized
                     */
                    String makeUpPath2 = "assets:/facemode/common/makeup2_106_v1.0.0.dat";
                    if (BuildConfig.FACE_MODEL == 240) {
                        makeUpPath2 = "assets:/facemode/common/makeup2_240_v1.0.0.dat";
                        if (BuildConfig.DEBUG) {//本地可替换dat 后面可以删，加debug保证生产环境没问题
                            File file = new File(PathUtils.getFolderDirPath("faceModelDat"));
                            if (file != null) {
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                for (String s : file.list()) {
                                    if (!TextUtils.isEmpty(s) && s.endsWith(".dat")) {
                                        makeUpPath2 = file.getAbsolutePath() + File.separator + s;
                                        Logger.e(TAG, "makeUpPath2-->" + makeUpPath2);
                                    }
                                }
                            }
                        }
                    }
                    boolean makeupSuccess2 = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP2, makeUpPath2);
                    Logger.e(TAG, BuildConfig.FACE_MODEL + "makeupSuccess-->" + makeupSuccess2);


                    /**
                     * 对应的effect sdk的部分（如果没用到可以不填加）
                     * The part of the corresponding effect sdk (if not used, you can leave it blank)
                     */
                    NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP2, makeUpPath2);


                    /**
                     * 人脸又分美摄人脸和商汤人脸,在这个基础上又分106点位和240点位，根据编译环境选择不同的初始化文件
                     *Face model is divided by meicam face and Shang Tang face,On this basis, 106 points and 240 points are distinguished,
                     * according to the compilation environment to choose different initialization files
                     */
                    if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {

                        if (BuildConfig.FACE_MODEL == 240) {
                            /**
                             * 这个跟人脸240点位相关，如果使用240点位，必须加这个否则可能导致不生效
                             * This is related to the 240 points of the face.
                             * If 240 points are used, this must be added, otherwise it may not take effect.
                             */
                            String pePath = "assets:/facemode/ms/240/pe240_ms_v1.0.2.dat";
                            if (BuildConfig.DEBUG) {//本地可替换dat 后面可以删，加debug保证生产环境没问题
                                File file = new File(PathUtils.getFolderDirPath("peDat"));
                                if (file != null) {
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    for (String s : file.list()) {
                                        if (!TextUtils.isEmpty(s) && s.endsWith(".dat")) {
                                            pePath = file.getAbsolutePath() + File.separator + s;
                                            Logger.e(TAG, "pePath-->" + pePath);
                                        }
                                    }
                                }
                            }
                            boolean peSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240, pePath);
                            Logger.e(TAG, "ms240 peSuccess-->" + peSuccess);

                            /**
                             * 对应的effect sdk的部分（如果没用到可以不填加）
                             * The part of the corresponding effect sdk (if not used, you can leave it blank)
                             */
                            NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240, pePath);
                        }

                        /**
                         * 人像背景分割模型
                         * Portrait Background Segmentation Model
                         */
                        String segPath = "assets:/facemode/ms/ms_humanseg_v1.0.7.model";
                        boolean segSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                segPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
                        Logger.e(TAG, "ms segSuccess-->" + segSuccess);

                        /**
                         * 对应的effect sdk的部分（如果没用到可以不填加）
                         * The part of the corresponding effect sdk (if not used, you can leave it blank)
                         */
                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                segPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);


                        /**
                         * 半身人像背景分割模型，半身的时候效果比较好
                         * Half-length portrait background segmentation model, the effect is better when half-length
                         */
                        String halfBodyPath = "assets:/facemode/ms/ms_halfbodyseg_v1.0.6.model";
                        boolean halfBodySuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                halfBodyPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_HALF_BODY);
                        Logger.e(TAG, "ms halfBodySuccess-->" + halfBodySuccess);

                        /**
                         * 对应的effect sdk的部分（如果没用到可以不填加）
                         * The part of the corresponding effect sdk (if not used, you can leave it blank)
                         */
                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                halfBodyPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_HALF_BODY);


                        /**
                         * 天空分割模型
                         * SKY SEGMENTATION MODEL
                         */
                        String segSkyPath = "assets:/facemode/ms/ms_skyseg_v1.0.0.model";
                        boolean segSkySuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                segSkyPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_SKY);
                        Logger.e(TAG, "ms segSkySuccess-->" + segSkySuccess);

                        /**
                         * 对应的effect sdk的部分（如果没用到可以不填加）
                         * The part of the corresponding effect sdk (if not used, you can leave it blank)
                         */
                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                segSkyPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_SKY);


                        /**
                         * 手势点位模型，比心等效果会使用到这个模型
                         * Gesture point model, heart and other effects will use this model
                         */
                        String handPath = "assets:/facemode/ms/ms_hand_v1.0.0.model";
                        boolean handSuccess = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                handPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK | NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);
                        Logger.e(TAG, "ms handSuccess-->" + handSuccess);

                        /**
                         * 对应的effect sdk的部分（如果没用到可以不填加）
                         * The part of the corresponding effect sdk (if not used, you can leave it blank)
                         */
                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                handPath, null, NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK | NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);


                    } else if (BuildConfig.HUMAN_AI_TYPE.contains(BUILD_HUMAN_AI_TYPE_MS_ST)) {
                        if (BuildConfig.FACE_MODEL == 240) {
                            modelPath = rootDir + "/facemode/st/240/M_SenseME_Face_Extra_Advanced_6.0.8.model";
                            faceModelName = "M_SenseME_Face_Extra_Advanced_6.0.8.model";
                            String className240 = "facemode/st/240";
                            FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, className240);
                            boolean initHumanDetectionExt = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                    modelPath,
                                    null,
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_EXTRA);
                            Log.e(TAG, "handleMessage: initHumanDetectionExt " + initHumanDetectionExt);

                            NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                    modelPath,
                                    null,
                                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_EXTRA);

                            //240设置的
                            String pePath = "assets:/facemode/st/240/pe240_st_v1.0.0.dat";
                            boolean peSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240, pePath);
                            Logger.e(TAG, "st" + BuildConfig.FACE_MODEL + "peSuccess-->" + peSuccess);

                            NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240, pePath);
                        }

                        /**
                         * 背景分割
                         * Background segmentation
                         */
                        modelPath = rootDir + "/facemode/st/common/M_SenseME_Segment_4.12.11.model";
                        faceModelName = "M_SenseME_Segment_4.12.11.model";
                        String segmentModel = "facemode/st/common";
                        boolean copySuccess2 = FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, segmentModel);
                        Logger.e(TAG, "st copy Segment Success-->" + copySuccess2);
                        boolean stSegmentInit = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                modelPath,
                                null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
                        Logger.e(TAG, "st init Segment Success-->" + stSegmentInit);


                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                modelPath,
                                null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);


                        /**
                         * 商汤手势检测
                         * Shang Tang Signal detection
                         */
                        String handModelPath = rootDir + "/facemode/st/hand/M_SenseME_Hand_6.0.8.model";
                        String handModelName = "M_SenseME_Hand_6.0.8.model";
                        String handModel = "facemode/st/hand";
                        boolean copySuccessHand = FileUtils.copyFileIfNeed(MainActivity.this, handModelName, handModel);
                        Logger.e(TAG, "st handCopySuccessHand-->" + copySuccessHand);
                        boolean handInit = NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                                handModelPath, null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK |
                                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);
                        Logger.e(TAG, "st handInitSuccess-->" + handInit);


                        NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                                handModelPath, null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_LANDMARK |
                                        NvsStreamingContext.HUMAN_DETECTION_FEATURE_HAND_ACTION);
                    }
                    /**
                     * avatar模型初始化，小狐狸效果等，根据表情动的特效果类别会使用这个模型
                     * avatar model initialization, little fox effect, etc.,
                     * this model will be used according to the special effect category of facial expressions
                     */
                    modelPath = rootDir + "/facemode/common/ms_avatar_v1.0.2.model";
                    faceModelName = "ms_avatar_v1.0.2.model";
                    String expressionModel = "facemode/common";
                    FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, expressionModel);
                    NvsStreamingContext.initHumanDetectionExt(MSApplication.getContext(),
                            modelPath,
                            null,
                            NvsStreamingContext.HUMAN_DETECTION_FEATURE_AVATAR_EXPRESSION);

                    /**
                     * 对应的effect sdk的部分（如果没用到可以不填加）
                     * The part of the corresponding effect sdk (if not used, you can leave it blank)
                     */
                    NvsEffectSdkContext.initHumanDetectionExt(MSApplication.getContext(),
                            modelPath,
                            null,
                            NvsStreamingContext.HUMAN_DETECTION_FEATURE_AVATAR_EXPRESSION);


                    /**
                     * 高级人脸
                     *Senior human face
                     */
                    if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST_SUPER)) {
                        String pePath = "assets:/facemode/st/advance/pe106_advanced_st_v1.0.1.dat";
                        boolean peSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE106, pePath);
                        Logger.e(TAG, "peSuccess-->" + peSuccess);


                        NvsEffectSdkContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE106, pePath);
                    }
                    if (initSuccess) {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_COMPLETE_CODE);
                    } else {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_FAILURE_CODE);
                    }
                    return false;
                }
            });
            initHandler.sendEmptyMessage(1);
        } else {
            initARSceneing = false;
            Logger.e(TAG, "initARScene false 没有人脸模块");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (hasAllPermission()) {
            Util.clearRecordAudioData();
        }
        // 退出清理
        if (mStreamingContext != null) {
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                NvsStreamingContext.closeHumanDetection();
            }
            mStreamingContext = null;
            TimelineData.instance().clear();
            BackupData.instance().clear();
        }
        stopMonitor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickRepeat = false;
        ParameterSettingValues parameterValues = ParameterSettingValues.instance();
        if (parameterValues != null) {
            //!!!统一处理liveWindow的显示模式
            MSLiveWindow.setLiveModel(parameterValues.getLiveWindowModel());
            if (mStreamingContext != null) {
                mStreamingContext.setColorGainForSDRToHDR(parameterValues.getColorGain());
            }
        }
    }

    /**
     * 获取activity需要的权限列表
     * Get the list of permissions required by the activity
     *
     * @return 权限列表;Permission list
     */
    @Override
    protected List<String> initPermissions() {
        return Util.getStoragePermission();
    }

    /**
     * 获取权限
     * Get permission
     */
    @Override
    protected void hasPermission() {
        Log.e(TAG, "hasPermission: 所有权限都有了");
        checkAuthorization();
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doClick(clickedView);//有权限的时候，activity还没有处于onResume状态，稍后在跳转
            }
        },500);
    }

    /**
     * 没有允许权限
     * No permission
     */
    @Override
    protected void nonePermission() {
        Log.e(TAG, "hasPermission: 没有允许权限");
    }

    /**
     * 用户选择了不再提示
     * The user chose not to prompt again
     */
    @Override
    protected void noPromptPermission() {
        Log.e(TAG, "hasPermission: 用户选择了不再提示");
        startAppSettings();
    }

    /*
     * 启动应用的设置
     * Launch app settings
     * */
    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MobclickAgent.onKillProcess(this);
    }

    /**
     * 隐私协议提示
     * Privacy Protocol Tips
     */
    private void showPrivacyDialog() {
        final SpUtil spUtil = SpUtil.getInstance(getApplicationContext());
        boolean isAgreePrivacy = spUtil.getBoolean(Constants.KEY_AGREE_PRIVACY, false);
        if (!isAgreePrivacy) {
            PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(MainActivity.this, R.style.dialog);
            privacyPolicyDialog.setOnButtonClickListener(new PrivacyPolicyDialog.OnPrivacyClickListener() {
                @Override
                public void onButtonClick(boolean isAgree) {
                    spUtil.putBoolean(Constants.KEY_AGREE_PRIVACY, isAgree);
                    if (!isAgree) {
                        AppManager.getInstance().finishActivity();
                    } else {
                        initUmConfig();
                    }
                }

                @Override
                public void pageJumpToWeb(String clickTextContent) {
                    String serviceAgreement = getString(R.string.service_agreement);
                    String privacyPolicy = getString(R.string.privacy_policy);
                    String visitUrl = "";
                    if (clickTextContent.contains(serviceAgreement)) {
                        visitUrl = Constants.SERVICE_AGREEMENT_URL;
                    } else if (clickTextContent.contains(privacyPolicy)) {
                        visitUrl = Constants.PRIVACY_POLICY_URL;
                    }
                    if (TextUtils.isEmpty(visitUrl)) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", visitUrl);
                    AppManager.getInstance().jumpActivity(MainActivity.this, MainWebViewActivity.class, bundle);
                }
            });
            privacyPolicyDialog.show();
        }
    }
    private void initUmConfig(){
        UMConfigure.init(getApplicationContext(),UMENG_KEY, AnalyticsConfig.getChannel(getApplicationContext()),UMConfigure.DEVICE_TYPE_PHONE,"");
    }

    @Override
    public void startMonitor() {
        Looper mainLooper = getMainLooper();
        mainLooper.setMessageLogging(new MSPrinter(this));
    }

    @Override
    public void stopMonitor() {
        Looper mainLooper = getMainLooper();
        mainLooper.setMessageLogging(null);
    }

    @Override
    public void onEndLoop(String info, int level) {

    }

    private static class SettingCallback extends RequestCallback<SettingInfo> {

        @Override
        public void onSuccess(BaseResponse<SettingInfo> response) {
            SettingInfo data = response.getData();
            if (null != data) {
                Log.d(TAG, "data.result=" + data.result);
                ParameterSettingValues parameterValues = ParameterSettingValues.instance();
                /**
                 * 服务端 返回true 需要开启双buffer  返回false 需要开启单
                 */
                parameterValues.setSingleBufferMode(!data.result);
            }

        }

        @Override
        public void onError(BaseResponse<SettingInfo> response) {
            Log.e(TAG, "onError:" + response);
        }
    }

    private class SettingInfo {
        private boolean result;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }
}
