package com.meishe.sdkdemo.capture;

import static com.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;
import static com.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_NONE;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CANCEL;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_CONFIRM;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_CLICK_TYPE;
import static com.meishe.sdkdemo.utils.MediaConstant.SINGLE_PICTURE_PATH;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectRenderCore;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaption;
import com.meicam.sdk.NvsCaptureAnimatedSticker;
import com.meicam.sdk.NvsCaptureAudioFx;
import com.meicam.sdk.NvsCaptureCompoundCaption;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoFrameRetriever;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.meishe.base.constants.AndroidOS;
import com.meishe.base.msbus.MSBus;
import com.meishe.base.msbus.MSSubscribe;
import com.meishe.base.utils.BarUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.view.MSLiveWindow;
import com.meishe.base.view.MSLiveWindowExt;
import com.meishe.http.bean.CategoryInfo;
import com.meishe.modulearscene.bean.ArBean;
import com.meishe.modulearscene.bean.DegreasingInfo;
import com.meishe.modulearscene.bean.ShapeBean;
import com.meishe.modulearscene.util.ArSceneUtils;
import com.meishe.modulemakeupcompose.MakeupHelper;
import com.meishe.modulemakeupcompose.MakeupManager;
import com.meishe.modulemakeupcompose.makeup.BeautyData;
import com.meishe.modulemakeupcompose.makeup.BeautyFxArgs;
import com.meishe.modulemakeupcompose.makeup.ColorData;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupCustomModel;
import com.meishe.modulemakeupcompose.makeup.MakeupData;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BasePermissionActivity;
import com.meishe.sdkdemo.bean.AssetLevelBean;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.capture.fragment.CaptureEffectFragment;
import com.meishe.sdkdemo.capture.fragment.filter.CaptureFilterFragment;
import com.meishe.sdkdemo.capture.viewmodel.CaptureViewModel;
import com.meishe.sdkdemo.dialog.CaptionEditPop;
import com.meishe.sdkdemo.dialog.TopMoreDialog;
import com.meishe.sdkdemo.download.AssetDownloadActivity;
import com.meishe.sdkdemo.edit.VideoEditActivity;
import com.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.meishe.sdkdemo.edit.compoundcaption.FontInfo;
import com.meishe.sdkdemo.edit.data.FilterItem;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.edit.data.Props;
import com.meishe.sdkdemo.edit.view.DrawRect;
import com.meishe.sdkdemo.edit.watermark.SingleClickActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.AssetFxUtil;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.MediaScannerUtil;
import com.meishe.sdkdemo.utils.NumberUtils;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.ScreenUtils;
import com.meishe.sdkdemo.utils.TimeFormatUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meishe.sdkdemo.utils.permission.PermissionDialog;
import com.meishe.sdkdemo.view.FilterView;
import com.meishe.sdkdemo.view.MagicProgress;
import com.meishe.sdkdemo.view.MakeUpSingleView;
import com.meishe.sdkdemo.view.MakeUpView;
import com.meishe.utils.ColorUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zd
 * @CreateDate : 2018-06-05
 * @Description :主题拍摄页。Theme theme shooting page
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureActivity extends BasePermissionActivity implements
        NvsStreamingContext.CaptureDeviceCallback,
        NvsStreamingContext.CaptureRecordingDurationCallback,
        NvsStreamingContext.CaptureRecordingStartedCallback, NvsStreamingContext.CapturedPictureCallback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final int REQUEST_FILTER_LIST_CODE = 110;
    private static final int ARFACE_LIST_REQUES_CODE = 111;

    private static int REQUEST_CODE_BACKGROUND_SEG = 100;

    private static final boolean OPEN_ALL_SWITCH = true; //打开所有开关,需要直接查看效果时，打开

    private View mLiveWindow;
    private LinearLayout mFunctionButtonLayout;
    private ImageView mIvExit, mIvMore, mIvChangeCamera;

    private TextView mTvMakeup, mTvBeauty, mTvFilter;
    private ImageView mIvMakeup, mIvBauty, mIvFilter;
    private LinearLayout mExposureLayout;
    private LinearLayout mLlMakeupLayout;
    private LinearLayout mBeautyLayout;
    private LinearLayout mFilterLayout;
    //    private LinearLayout fxMore;
    private FrameLayout mFlStartRecord;
    private TextView mStartText;
    private ImageView mDelete;
    private ImageView mNext;
    private TextView mRecordTime;

    private ImageView mImageAutoFocusRect;
    //private RelativeLayout mBottomLayout;
    private LinearLayout mAdjustColorLayout, mSharpenLayout;
    private Switch mAdjustColorSwitch, mSharpenSwitch;

    /**
     * 拍照or视频
     * Photo or video
     */
    private RelativeLayout mSelectLayout, mRlPhotosLayout;
    private LinearLayout mRecordTypeLayout;
    private FrameLayout mFlMiddleParent;
    private FrameLayout mFlBottomParent;
    private TextView mTvChoosePicture, mTvChooseVideo;
    private View mVideoTimeDot;
    private ImageView mIvTakePhotoBg;
    private Button mPictureCancel, mPictureOk;
    private int mRecordType = Constants.RECORD_TYPE_PICTURE;
    private ImageView mPictureImage;
    private Bitmap mPictureBitmap;

    /**
     * 录制
     * Record
     */
    private ArrayList<Long> mRecordTimeList = new ArrayList<>();
    private ArrayList<String> mRecordFileList = new ArrayList<>();
    private long mEachRecodingVideoTime = 0, mEachRecodingImageTime = 4000000;
    private long mAllRecordingTime = 0;
    private String mCurRecordVideoPath;
    private NvAssetManager mAssetManager;
    private int mCurrentDeviceIndex;
    private boolean mIsSwitchingCamera;
    NvsStreamingContext.CaptureDeviceCapability mCapability = null;
    private AlphaAnimation mFocusAnimation;

    /**
     * 变焦以及曝光dialog
     * Zoom and exposure dialog
     */
    private boolean m_supportAutoFocus;
    private TopMoreDialog mMoreDialog;
    /**
     * 美颜Dialog
     * Beauty Dialog
     */
    private AlertDialog mCaptureBeautyDialog;
    private View mBeautyView;
    private TextView mBeautyTabButton;
    //    private TextView mBeautyStyleTabButton;
//    private View mVSkinBeautyStyleLine;
    private View mVSkinBeautyLine, mVShapeBeautyLine, mVSmallBeautyLine;
    private TextView mShapeTabButton;
    private TextView mSmallTabButton;
    /**
     * 样式
     */
//    private RelativeLayout mBeautyStyleSelectRelativeLayout;
    private RelativeLayout mBeautySelectRelativeLayout;
    private RelativeLayout mShapeSelectRelativeLayout;
    private RelativeLayout mSmallSelectRelativeLayout;


    private MagicProgress mShapeSeekBar;

    /**
     * 变声Dialog
     * Beauty Dialog
     */
    private AlertDialog mVoiceDialog;
    private View mVoiceView;
    private TextView mTvVoice;
    private View mIvVoice;
    private RecyclerView mVoiceRecycler;
    //实时帧率显示
    private TextView mTvFrame;
    /**
     * 美颜
     * Beauty
     */
    private Switch mBeautySwitch;
    private TextView mBeauty_switch_text;
    private RecyclerView mBeautyRecyclerView;
//    private RecyclerView mBeautyStyleRecyclerView;
    /**
     * 美肤
     */
    private SkinAdapter mBeautyAdapter;
    /**
     * 微整形
     */
    private ShapeAdapter mSmallShapeAdapter;
    /*内建的美颜效果*/
    private NvsCaptureVideoFx mBeautyFx;

    private LinearLayout mLLBeautySeek;
    private MagicProgress mBeautySeekBar;
    private TextView tvBeautySb;

    /**
     * 美型id 检索数组
     * Retrieve an array of beauty ids
     */
    private String[] mShapeIdArray = {
            "Face Mesh Face Size Degree",
            "Face Mesh Nose Width Degree",
            "Face Mesh Eye Size Degree",
            "Face Mesh Eye Corner Stretch Degree"
    };
    List<String> mShapeIdList = new ArrayList<>(Arrays.asList(mShapeIdArray));

    /**
     * 是否初始化完成
     * Whether initialization is complete
     */
    private boolean initArScene;

    /**
     * 美颜的重置功能
     */
    private LinearLayout mBeautyResetLayout;

    /**
     * 美型
     */
    private Switch mBeautyShapeSwitch;
    private TextView mBeauty_shape_switch_text;
    /**
     * 美型重置
     */
    private LinearLayout mBeautyShapeResetLayout;
    private ImageView mBeautyShapeResetIcon;
    private TextView mBeautyShapeResetTxt;
    private RecyclerView mShapeRecyclerView;
    private ShapeAdapter mShapeAdapter;

    /**
     * 微整形
     */
    private Switch mSmallShapeSwitch;
    private TextView mSmallShapeSwitchText;
    /**
     * 微整形重置
     */
    private LinearLayout mSmallShapeResetLayout;
    private ImageView mSmallShapeResetIcon;
    private TextView mSmallShapeResetTxt;
    private RecyclerView mSmallShapeRecyclerView;

    private MagicProgress mSmallSeekBar;
    private TextView mSmallSeekText;
    private TextView mSmallSeekText2;
    private LinearLayout mRlSmallSeekRootView;


    private boolean mShapeSwitchIsOpen;
    /**
     * 滤镜
     * filter
     */
    private AlertDialog mFilterDialog, mMakeUpDialog;
    private FilterView mFilterBottomView;
    private MakeUpSingleView mMakeUpView;
    /**
     * 滤镜特效
     */
    private NvsCaptureVideoFx mCurCaptureVideoFx;
    private ArrayList<FilterItem> mFilterDataArrayList = new ArrayList<>();
    private int mFilterSelPos;
    private VideoClipFxInfo mVideoClipFxInfo = new VideoClipFxInfo();

    /**
     * 道具-默认普通版，不带人脸功能
     * Props-default normal version, without face function
     */
    private int mCanUseARFaceType = HUMAN_AI_TYPE_NONE;
    //private AlertDialog mFaceUPropDialog;
    // private FaceUPropView mFaceUPropView;
    private List<Props> mPropsList = new ArrayList<>();
    private int mFaceUPropSelPos;
    private String mArSceneId = "";
    private TextView mTvBeautyA;
    private TextView mTvBeautyB;
    private TextView mShapeText2;
    private TextView mShapeText;

    /**
     * 背景抠像特效
     */
    private NvsCaptureVideoFx mBgSegEffect;
    /**
     * 美颜 美型 美妆 锐度 道具
     */
    private NvsCaptureVideoFx mArSceneFaceEffect;
    /**
     * 校色
     */
    private NvsCaptureVideoFx mAdjustColorFx;

    //选中美妆的item，此时再切换到美型需要切换到美型1的默认值
    private boolean selectMarkUp;
    private List<ChangeVoiceData> voiceList;
    private NvsCaptureAudioFx audioVoiceFx;
    private DrawRect drawRect;
    private int drawRectModel;
    //    private MoreFxDialog dialogFxMore;
    private int LOAD_MORE_STICKER = 1001;
    private int LOAD_MORE_CAPTION = 1002;
    private NvsCaptureAnimatedSticker currentAnimatedSticker;
    private long downTime;
    //用来保存当前特效层级 贴纸和字幕同理，0为最上层
    List<AssetLevelBean> stickerVertices = new ArrayList<>();
    List<AssetLevelBean> captionVertices = new ArrayList<>();
    private NvsCaptureCompoundCaption currentCompoundCaption;
    private CaptionEditPop captionEditPop;
    private int selectCaptionIndex;
    private PointF downPointF;
    private long STICK_TIME_DURATION = 10 * 60 * 60 * Constants.NS_TIME_BASE;
    private int narHeight;
    private boolean showNavigation;
    private ParameterSettingValues parameterValues;
    private BeautyShapeDataKindItem currentSelectBeautyShapeItem;
    /**
     * 是否需要更改dialog中的颜色
     */
    private boolean needChangeMoreFxDialogPropsColor = false;
    private View mLlProps;
    private View mLlSticker;
    private View mLlComponentCaption;
    private View mLlBackgroundSeg;
    private View mLlRightContainer;
    private FragmentManager mFragmentManager;
    private TypeAndCategoryInfo mFilterTypeInfo;

    private TypeAndCategoryInfo mPropTypeInfo;
    /**
     * 组合字幕
     */
    private TypeAndCategoryInfo mComponentTypeInfo;
    /**
     * 贴纸
     */
    private TypeAndCategoryInfo mStickerTypeInfo;

    private MagicProgress mFilterMpProgress;
    //    private CommonRecyclerViewAdapter mBeautyStyleAdapter;
    private NvsEffectRenderCore mEffectRenderCore;
    /**
     * 开始预览时间
     */
    private long mStartPreviewTime;
    /**
     * 自然肤色id
     */
    public static StringBuilder mFxFilterPackageId;

    //    /**
//     * 内置的单状
//     */
//    private StringBuilder mFxComposeId;
    private CaptureViewModel mCaptureViewModel;
    @Inject
    ViewModelProvider.Factory mViewModelProvider;
    /**
     * 美颜数据
     */
    private List<ArBean> mBeautyList;
    /**
     * 磨皮数据
     */
    private List<ArBean> mBeautySkinList;
    /**
     * 美型数据
     */
    private List<ArBean> mShapeDataList;
    /**
     * 微整型数据
     */
    private List<ArBean> mSmallShapeDataList;

    private List<ArBean> mCurrentUseDataList = new ArrayList<>();

    private ImageView mIvCaptureConstrast;
    private Makeup preMakeUp;
    private MakeupEffectContent preEffectContent;
    private MagicProgress mBeautySubSeekBar;
    private View mLlSubSeekContainer;
    public static EffectInfo mNeedSelectEffectInfo;


    @Override
    protected int initRootView() {
        mAssetManager = NvAssetManager.sharedInstance();
        mEffectRenderCore = mNvsEffectSdkContext.createEffectRenderCore();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mCaptureViewModel = ViewModelProviders.of(this).get(CaptureViewModel.class);
        return R.layout.activity_capture;
    }

    @Override
    protected void initViews() {

        /*
         * 页面主要布局
         * Main page layout
         */
        mRecordTypeLayout = (LinearLayout) findViewById(R.id.ll_chang_pv);
        mFlMiddleParent = findViewById(R.id.fl_middle_parent);
        mFlBottomParent = findViewById(R.id.fl_bottom_parent);

        /*
         * 美颜
         * Beauty
         */
        mBeautyView = LayoutInflater.from(this).inflate(R.layout.beauty_view, null);
        //样式
//        mBeautyStyleTabButton = mBeautyView.findViewById(R.id.beauty_style_tab_btn);
//        mVSkinBeautyStyleLine = mBeautyView.findViewById(R.id.v_skin_beauty_style_line);
        //美肤
        mBeautyTabButton = mBeautyView.findViewById(R.id.beauty_tab_btn);
        mVSkinBeautyLine = mBeautyView.findViewById(R.id.v_skin_beauty_line);
        mBeautyResetLayout = mBeautyView.findViewById(R.id.beauty_reset_layout);

        mShapeTabButton = mBeautyView.findViewById(R.id.shape_tab_btn);
        mSmallTabButton = mBeautyView.findViewById(R.id.small_tab_btn);
        mVShapeBeautyLine = mBeautyView.findViewById(R.id.v_shape_beauty_line);
        mVSmallBeautyLine = mBeautyView.findViewById(R.id.v_small_change_line);

//        mBeautyStyleSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.beauty_style_rl);
        mBeautySelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.beauty_select_rl);
        mShapeSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.shape_select_rl);
        mSmallSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.small_select_rl);
        mTvBeautyA = mBeautyView.findViewById(R.id.tv_beauty_a);
        mTvBeautyB = mBeautyView.findViewById(R.id.tv_beauty_b);
        mLLBeautySeek = mBeautyView.findViewById(R.id.ll_beauty_seek);
        mBeautySeekBar = mBeautyView.findViewById(R.id.beauty_sb);
        mBeautySubSeekBar = mBeautyView.findViewById(R.id.beauty_sub_sb);
        tvBeautySb = mBeautyView.findViewById(R.id.tv_beauty_sb);
        mLlSubSeekContainer = mBeautyView.findViewById(R.id.ll_sub_seek_container);
        mBeautySeekBar.setMax(100);
        mBeautySeekBar.setPointEnable(true);
        mBeautySeekBar.setBreakProgress(0);

        mBeautySubSeekBar.setMax(100);
        mBeautySubSeekBar.setMin(0);
        mBeautySubSeekBar.setPointEnable(true);
        mBeautySubSeekBar.setBreakProgress(0);
        mShapeSeekBar = (MagicProgress) mBeautyView.findViewById(R.id.shape_sb);

        mShapeSeekBar.setMax(200);
        mShapeSeekBar.setPointEnable(true);


        mShapeText = (TextView) mBeautyView.findViewById(R.id.shape_text);
        mShapeText2 = (TextView) mBeautyView.findViewById(R.id.shape_text2);
        mBeautySwitch = (Switch) mBeautyView.findViewById(R.id.beauty_switch);
        mBeauty_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_switch_text);
        mBeautyRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_list_rv);
//        mBeautyStyleRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_style_rv);
        mShapeText.setVisibility(View.GONE);
        mShapeText2.setVisibility(View.GONE);


        /*
         * 变声
         */
        mTvVoice = findViewById(R.id.tv_voice);
        mIvVoice = findViewById(R.id.iv_voice);
        mTvFrame = findViewById(R.id.tv_frame);
        mVoiceView = LayoutInflater.from(this).inflate(R.layout.change_voice_view, null);
        mVoiceRecycler = mVoiceView.findViewById(R.id.recycler_voice);
        /*
         * 校色
         * School color
         */
        mAdjustColorLayout = (LinearLayout) mBeautyView.findViewById(R.id.adjust_color_layout);
        mAdjustColorSwitch = (Switch) mBeautyView.findViewById(R.id.adjust_color_switch);

        /*
         * 锐化
         * Sharpen
         */
        mSharpenLayout = (LinearLayout) mBeautyView.findViewById(R.id.sharpen_layout);
        mSharpenSwitch = (Switch) mBeautyView.findViewById(R.id.sharpen_switch);
        mSharpenSwitch.setChecked(false);

        mBeautyShapeSwitch = (Switch) mBeautyView.findViewById(R.id.beauty_shape_switch);
        mBeauty_shape_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_shape_switch_text);
        mBeautyShapeResetLayout = (LinearLayout) mBeautyView.findViewById(R.id.beauty_shape_reset_layout);
        mBeautyShapeResetIcon = (ImageView) mBeautyView.findViewById(R.id.beauty_shape_reset_icon);
        mBeautyShapeResetTxt = (TextView) mBeautyView.findViewById(R.id.beauty_shape_reset_txt);
        mShapeRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_shape_item_list);


        mSmallShapeSwitch = (Switch) mBeautyView.findViewById(R.id.small_shape_switch);
        mSmallShapeSwitchText = (TextView) mBeautyView.findViewById(R.id.small_shape_switch_text);
        mSmallShapeResetLayout = (LinearLayout) mBeautyView.findViewById(R.id.small_shape_reset_layout);
        mSmallShapeResetIcon = (ImageView) mBeautyView.findViewById(R.id.small_shape_reset_icon);
        mSmallShapeResetTxt = (TextView) mBeautyView.findViewById(R.id.small_shape_reset_txt);
        mSmallShapeRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.small_shape_item_list);

        mRlSmallSeekRootView = (LinearLayout) mBeautyView.findViewById(R.id.rl_small_seek_root_view);
        mSmallSeekBar = (MagicProgress) mBeautyView.findViewById(R.id.small_seek);
        mSmallSeekText = (TextView) mBeautyView.findViewById(R.id.small_seek_text);
        mSmallSeekText2 = (TextView) mBeautyView.findViewById(R.id.small_seek_text2);
        mSmallSeekText.setVisibility(View.GONE);
        mSmallSeekText2.setVisibility(View.GONE);
        mRecordTime = (TextView) findViewById(R.id.tv_timing_num);
        mImageAutoFocusRect = (ImageView) findViewById(R.id.iv_focus);
        mDelete = (ImageView) findViewById(R.id.iv_back_delete);
        mNext = (ImageView) findViewById(R.id.iv_confirm);
        // mStartLayout = (RelativeLayout) findViewById(R.id.startLayout);
        mFlStartRecord = findViewById(R.id.fl_take_photos);
        mStartText = (TextView) findViewById(R.id.tv_video_num);
        //兼容华为4A的 华为4A则使用liveWindow代替liveWindowExt
        if (Util.isHUAWEI4A()) {
            mLiveWindow = findViewById(R.id.lw_window);
        } else {
            mLiveWindow = findViewById(R.id.lw_windowExt);
        }
        mLiveWindow.setVisibility(View.VISIBLE);
        mIvExit = findViewById(R.id.iv_exit);
        mIvMore = findViewById(R.id.iv_more);
        mIvChangeCamera = findViewById(R.id.iv_rollover);

        mLlMakeupLayout = (LinearLayout) findViewById(R.id.ll_makeup);
        mIvCaptureConstrast = (ImageView) mBeautyView.findViewById(R.id.iv_capture_contrast);
        drawRect = findViewById(R.id.capture_draw_rect);
        mTvMakeup = findViewById(R.id.tv_makeup);
        mIvMakeup = findViewById(R.id.iv_makeup);
        mTvBeauty = findViewById(R.id.tv_beauty);
        mIvBauty = findViewById(R.id.iv_beauty);
        mTvFilter = findViewById(R.id.tv_filter);
        mIvFilter = findViewById(R.id.iv_filter);
        mBeautyLayout = (LinearLayout) findViewById(R.id.ll_beauty);
        mFilterLayout = (LinearLayout) findViewById(R.id.ll_filter);
//        fxMore = (LinearLayout) findViewById(R.id.ll_fx_more);

        mLlProps = findViewById(R.id.ll_props);
        mLlRightContainer = findViewById(R.id.ll_right_container);
        mLlSticker = findViewById(R.id.ll_sticker);
        mLlComponentCaption = findViewById(R.id.ll_com_caption);
        mLlBackgroundSeg = findViewById(R.id.ll_background_seg);
        mFilterMpProgress = findViewById(R.id.mp_filter);

        drawRect.setStickerMuteListenser(new DrawRect.onStickerMuteListenser() {
            @Override
            public void onStickerMute() {
                if (currentAnimatedSticker != null && drawRectModel == Constants.EDIT_MODE_STICKER) {
                    float volumeGain = currentAnimatedSticker.getVolumeGain().leftVolume;
                    volumeGain = volumeGain == 0 ? 1 : 0;
                    currentAnimatedSticker.setVolumeGain(volumeGain, volumeGain);
                    drawRect.setStickerMuteIndex((int) volumeGain);
                }
            }
        });
        mVideoTimeDot = findViewById(R.id.v_timing_dot);
        mIvTakePhotoBg = findViewById(R.id.iv_take_photo);
        mTvChoosePicture = findViewById(R.id.tv_take_photos);
        mTvChooseVideo = findViewById(R.id.tv_take_video);
        //mSelectLayout = (RelativeLayout) findViewById(R.id.select_layout);
        mRlPhotosLayout = (RelativeLayout) findViewById(R.id.rl_photos_container);
        mPictureCancel = findViewById(R.id.bt_delete_photos);
        mPictureOk = findViewById(R.id.bt_save_photos);
        mPictureImage = (ImageView) findViewById(R.id.iv_photos);
        initTopMoreView();
        mCaptureBeautyDialog = new AlertDialog.Builder(this).create();
        mCaptureBeautyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                mBeautyStyleTabButton.performClick();
                mBeautyTabButton.performLongClick();
            }
        });
        mCaptureBeautyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mCaptureBeautyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                MakeupManager.getInstacne().clearAllData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeCaptureDisplay(true);
                        closeCaptureDialogView(mCaptureBeautyDialog);
                    }
                });
            }
        });


        mLlProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showArSceneFx();
            }
        });

        mLlSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStickerFx();
            }
        });

        mLlComponentCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComponentCaptionFx();
            }
        });

        mLlBackgroundSeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBgSeg();
            }
        });


        initChangeVoiceView();
        drawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {

                PointF pre = null;
                PointF p = null;
                if (mLiveWindow instanceof MSLiveWindow) {
                    pre = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(prePointF);
                    p = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(nowPointF);
                } else if (mLiveWindow instanceof MSLiveWindowExt) {
                    p = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(nowPointF);
                    pre = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(prePointF);
                }
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (drawRectModel == Constants.EDIT_MODE_STICKER) {
                    if (currentAnimatedSticker != null) {
                        currentAnimatedSticker.translateAnimatedSticker(timeLinePointF);
                        updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                                , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
                    }
                } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (currentCompoundCaption != null) {
                        currentCompoundCaption.translateCaption(timeLinePointF);
                        List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                                false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
                        List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                        updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION,
                                (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL), captions);
                    }
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float rotation) {
                PointF assetAnchor = null;
                if (mLiveWindow instanceof MSLiveWindow) {
                    assetAnchor = ((MSLiveWindow) mLiveWindow).mapViewToCanonical(anchor);
                } else if (mLiveWindow instanceof MSLiveWindowExt) {
                    assetAnchor = ((MSLiveWindowExt) mLiveWindow).mapViewToCanonical(anchor);
                }
                //map方法没有计算liveWindow的偏移量
                if (drawRectModel == Constants.EDIT_MODE_STICKER) {
                    if (currentAnimatedSticker != null) {
                        currentAnimatedSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        /*
                         * 旋转贴纸
                         * Rotate stickers
                         * */
                        currentAnimatedSticker.rotateAnimatedSticker(rotation);
                        updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                                , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
                    }
                } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (currentCompoundCaption != null) {
                        currentCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        /*
                         * 旋转字幕
                         * Spin subtitles
                         * */
                        currentCompoundCaption.rotateCaption(rotation, assetAnchor);
//                        float scaleX = currentCompoundCaption.getScaleX();
//                        float scaleY = currentCompoundCaption.getScaleY();
//                        if (scaleX <= DEFAULT_SCALE_VALUE && scaleY <= DEFAULT_SCALE_VALUE) {
//                            currentCompoundCaption.setScaleX(DEFAULT_SCALE_VALUE);
//                            currentCompoundCaption.setScaleY(DEFAULT_SCALE_VALUE);
//                        }
                        List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                                false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
                        List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                        updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION,
                                (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL), captions);
                    }
                }
            }

            @Override
            public void onScaleXandY(float xScaleFactor, float yScaleFactor, PointF anchor) {

            }

            @Override
            public void onDel() {
                downPointF = null;
                deleteCaptionOrSticker();
            }

            @Override
            public void onTouchDown(PointF curPoint) {
                downPointF = new PointF(curPoint.x, curPoint.y);
                downTime = System.currentTimeMillis();
                Log.w(TAG, " onTouchDown point.x:" + curPoint.x + " y:" + curPoint.y);
            }

            @Override
            public void onAlignClick(boolean isHorizontal) {
                Log.w(TAG, " onAlignClick");

            }

            @Override
            public void onOrientationChange(boolean isHorizontal) {
                Log.w(TAG, " onOrientationChange");
            }

            @Override
            public void onHorizontalFlipClick() {
                Log.w(TAG, " onHorizontalFlipClick");
                downPointF = null;
                changeHorizontalFlip();

            }

            @Override
            public void onBeyondDrawRectClick() {
                Log.w(TAG, " onBeyondDrawRectClick");
            }

            @Override
            public void onTouchUp(PointF pointF) {
                long duration = System.currentTimeMillis() - downTime;
                //判断是否是点击事件 排除move drag等影响
                if (Util.isClickTouch(downPointF, pointF, duration)) {
                    clearSelectState();
                    if (!checkTouchAssetFrame(downPointF)) {
                        if (drawRect.getVisibility() == View.VISIBLE) {
                            drawRect.setVisibility(View.GONE);
                            currentAnimatedSticker = null;
                            currentCompoundCaption = null;
                        }
                    }
                }

            }
        });

        mFilterBottomView = new FilterView(mContext);
        initFilterProgress();
    }


    @MSSubscribe(Constants.SubscribeType.SUB_ADD_CUSTOM_STICKER_TYPE)
    public void onAddCustomSticker() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_CUSTOM_STICKER);
        AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), SingleClickActivity.class, bundle);
    }


    private void initFilterProgress() {
        mFilterMpProgress.setMax(100);
        mFilterMpProgress.setPointEnable(true);
        mFilterMpProgress.setBreakProgress(0);
        mFilterMpProgress.setShowBreak(false);
        mFilterMpProgress.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                changeFilterIntensity(progress, fromUser);
            }
        });
    }

    /**
     * 删除当前选中的贴纸或者复合字幕
     */
    private void deleteCaptionOrSticker() {
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            if (currentAnimatedSticker != null) {
                String kValue = (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL);
                int count = mStreamingContext.getCaptureAnimatedStickerCount();
                for (int i = 0; i < count; i++) {
                    NvsCaptureAnimatedSticker animatedSticker = mStreamingContext.getCaptureAnimatedStickerByIndex(i);
                    if (!TextUtils.isEmpty(kValue) && animatedSticker != null
                            && TextUtils.equals(kValue, (String) animatedSticker.getAttachment(Constants.KEY_LEVEL))) {
                        for (AssetLevelBean captionVertex : stickerVertices) {
                            if (TextUtils.equals(kValue, captionVertex.getTag())) {
                                stickerVertices.remove(captionVertex);
                                break;
                            }
                        }
                        mStreamingContext.removeCaptureAnimatedSticker(i);
                        drawRect.setVisibility(View.GONE);
                        currentAnimatedSticker = null;
                        break;
                    }
                }
                if (stickerVertices.size() > 0) {
                    checkSelectByAttachment(stickerVertices.get(0).getTag());
                }
            }

        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            if (currentCompoundCaption != null) {
                String kValue = (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL);
                int count = mStreamingContext.getCaptureCompoundCaptionCount();
                for (int i = 0; i < count; i++) {
                    NvsCaptureCompoundCaption compoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);
                    if (!TextUtils.isEmpty(kValue) && compoundCaption != null
                            && TextUtils.equals(kValue, (String) compoundCaption.getAttachment(Constants.KEY_LEVEL))) {
                        for (AssetLevelBean captionVertex : captionVertices) {
                            if (TextUtils.equals(kValue, captionVertex.getTag())) {
                                captionVertices.remove(captionVertex);
                                break;
                            }
                        }
                        mStreamingContext.removeCaptureCompoundCaption(i);
                        drawRect.setVisibility(View.GONE);
                        currentCompoundCaption = null;
                        break;
                    }
                }
                if (captionVertices.size() > 0) {
                    checkSelectByAttachment(captionVertices.get(0).getTag());
                }
            }
        }
    }

    /**
     * 清除选中信息
     */
    private void clearSelectState() {
        MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_SELECT_ITEM_TYPE);
    }

    /**
     * 点击翻转(目前仅贴纸生效)
     */
    private void changeHorizontalFlip() {
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            if (currentAnimatedSticker != null) {
                currentAnimatedSticker.setHorizontalFlip(!currentAnimatedSticker.getHorizontalFlip());
                updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices())
                        , Constants.EDIT_MODE_STICKER, (String) currentAnimatedSticker.getAttachment(Constants.KEY_LEVEL), null);
            }
        }
    }

    /**
     * 点击点检查是否点击到特效上
     *
     * @param curPoint
     */
    private boolean checkTouchAssetFrame(final PointF curPoint) {
        Point point = new Point((int) curPoint.x, (int) curPoint.y);
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            return checkSelectByAttachment(checkTouchAttachment(stickerVertices, point));
        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            return checkSelectCaption(captionVertices, point);
        }
        return false;
    }

    /**
     * 选中组合字幕的单个字幕进行修改
     *
     * @param vertices
     * @param point
     */
    private boolean checkSelectCaption(List<AssetLevelBean> vertices, Point point) {
        String oldKey = "";
        if (currentCompoundCaption != null) {
            oldKey = (String) currentCompoundCaption.getAttachment(Constants.KEY_LEVEL);
        }
        String attachment = checkTouchAttachment(vertices, point);
        if (TextUtils.isEmpty(attachment)) {
            return false;
        }
        checkSelectByAttachment(attachment);
        boolean showKeyboard = false;
        if (!TextUtils.isEmpty(oldKey) && TextUtils.equals(oldKey, attachment)) {
            showKeyboard = true;
        }
        selectCaptionIndex = -1;
        if (currentCompoundCaption != null) {
            int captionCount = currentCompoundCaption.getCaptionCount();
            for (int i = 0; i < captionCount; i++) {
                List<PointF> captionBoundingVertices = currentCompoundCaption.getCaptionBoundingVertices(i, NvsCaption.BOUNDING_TYPE_TEXT);
                List<PointF> assetViewVerticesList = getAssetViewVerticesList(false, captionBoundingVertices);
                if (checkTouchInPath(assetViewVerticesList, point)) {
                    selectCaptionIndex = i;
                    break;
                }
            }
        }
        if (showKeyboard && selectCaptionIndex >= 0) {
            showCaptionKeyBoard();
        }
        return true;
    }


    /**
     * 背景抠像
     */
    private void showBgSeg() {
        gotoSelectPictures();
    }


    /**
     * 跳转选择图片
     * jump to select the photos
     */
    private void gotoSelectPictures() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_PICTURE_FROM_BACKGROUND_SEG);
        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                SingleClickActivity.class, bundle, REQUEST_CODE_BACKGROUND_SEG);
    }


    /**
     * 更新贴纸drawRect位置
     *
     * @param pointFS
     * @param model
     */
    private void updateDrawRectPosition(List<PointF> pointFS, int model, String attachment, List<List<PointF>> captions) {
        if (model == Constants.EDIT_MODE_STICKER) {
            drawRect.setDrawRect(pointFS, model);
            for (AssetLevelBean stickerVertex : stickerVertices) {
                if (TextUtils.equals(stickerVertex.getTag(), attachment)) {
                    stickerVertex.setData(pointFS);
                    break;
                }
            }
        } else if (model == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            drawRect.setCompoundDrawRect(pointFS, captions, model);
            for (AssetLevelBean captionVertex : captionVertices) {
                if (TextUtils.equals(captionVertex.getTag(), attachment)) {
                    captionVertex.setData(pointFS);
                    break;
                }
            }
        }
        drawRect.setVisibility(View.VISIBLE);
    }

    /**
     * 转换点位
     *
     * @param horizontal   //数据翻转
     * @param verticesList
     * @return
     */
    private List<PointF> getAssetViewVerticesList(boolean horizontal, List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            if (mLiveWindow instanceof MSLiveWindow) {
                PointF pointF = ((MSLiveWindow) mLiveWindow).mapCanonicalToView(verticesList.get(i));
                newList.add(pointF);
            } else if (mLiveWindow instanceof MSLiveWindowExt) {
                PointF pointF = ((MSLiveWindowExt) mLiveWindow).mapCanonicalToView(verticesList.get(i));
                newList.add(pointF);
            }
        }
        if (horizontal) {
            //todo 贴纸坐标转换
            /*
             * 如果已水平翻转，需要对顶点数据进行处理
             * If flipped horizontally, you need to process the vertex data
             * */
            Collections.swap(newList, 0, 3);
            Collections.swap(newList, 1, 2);
        }
        return newList;
    }

    /**
     * 展示贴纸
     */
    private void showStickerFx() {
        drawRectModel = Constants.EDIT_MODE_STICKER;
        showCommonFragment(mStickerTypeInfo, Constants.FRAGMENT_STICKER_TAG);
        changeCaptureDisplay(false);
        if (stickerVertices.size() > 0) {
            checkSelectByAttachment(stickerVertices.get(0).getTag());
        }
    }

    private void closeStickerFx() {
        if (isEffectFragmentVisible(Constants.FRAGMENT_STICKER_TAG)) {
            hideEffectFragment(Constants.FRAGMENT_STICKER_TAG);
            changeCaptureDisplay(true);
            drawRectModel = -1;
        }
    }

    private void showStickerDialog() {
        showCommonFragment(mStickerTypeInfo, Constants.FRAGMENT_STICKER_TAG);
    }

    /**
     * 应用自定义贴纸
     *
     * @param nvCustomStickerInfo
     */
    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_CUSTOM_STICKER_TYPE)
    private void applyCustomSticker(NvAssetManager.NvCustomStickerInfo nvCustomStickerInfo) {
        /*
         * 添加自定义贴纸
         * Add custom stickers
         * */
        String imageSrcFilePath = nvCustomStickerInfo.imagePath;
        int lastPointPos = imageSrcFilePath.lastIndexOf(".");
        String fileSuffixName = imageSrcFilePath.substring(lastPointPos).toLowerCase();

        if (".gif".equals(fileSuffixName)) {//gif
            String targetCafPath = nvCustomStickerInfo.targetImagePath;
            File targetCafFile = new File(targetCafPath);
            if (targetCafFile.exists()) {
                /*
                 * 检测目标caf文件是否存在
                 * Detect the existence of the target caf file
                 * */
                addCustomAnimateSticker(nvCustomStickerInfo, targetCafPath);
            }
        } else {//image
            addCustomAnimateSticker(nvCustomStickerInfo, nvCustomStickerInfo.imagePath);
        }

    }

    private void addCustomAnimateSticker(NvAssetManager.NvCustomStickerInfo nvCustomStickerInfo, String filePath) {
        currentAnimatedSticker =
                mStreamingContext.addCustomCaptureAnimatedSticker(0, STICK_TIME_DURATION,
                        nvCustomStickerInfo.templateUuid, filePath);
        if (currentAnimatedSticker != null) {
            currentAnimatedSticker.setScale(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(),
                    currentAnimatedSticker.getBoundingRectangleVertices());
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            stickerVertices.add(0, levelBean);
            currentAnimatedSticker.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
            //todo 如果贴纸需要打开声音图标 放开此行代码
//            drawRect.setMuteVisible(currentAnimatedSticker.hasAudio());
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_STICKER, levelBean.getTag(), null);
        }
    }

    /**
     * 应用贴纸
     */
    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_STICKER_TYPE)
    private void applySticker(String uuid) {
        if (uuid == null) {
            return;
        }
        if (Util.isFastClick()) {
            return;
        }
        currentAnimatedSticker = mStreamingContext.appendCaptureAnimatedSticker(0, STICK_TIME_DURATION, uuid);
        if (currentAnimatedSticker != null) {
            currentAnimatedSticker.setScale(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices());
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            stickerVertices.add(0, levelBean);
            currentAnimatedSticker.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
            //todo 如果贴纸需要打开声音图标 放开此行代码
//            drawRect.setMuteVisible(currentAnimatedSticker.hasAudio());
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_STICKER, levelBean.getTag(), null);
        }
    }

    /**
     * 展示组合字幕
     */
    private void showComponentCaptionFx() {
        drawRectModel = Constants.EDIT_MODE_COMPOUND_CAPTION;
        showCommonFragment(mComponentTypeInfo, Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
        if (captionVertices.size() > 0) {
            checkSelectByAttachment(captionVertices.get(0).getTag());
        }
        changeCaptureDisplay(false);
    }

    private void closeCaptionFx() {
        if (isEffectFragmentVisible(Constants.FRAGMENT_COMPONENT_CAPTION_TAG)) {
            drawRectModel = -1;
            hideEffectFragment(Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
            changeCaptureDisplay(true);
        }
    }

    private boolean isEffectFragmentVisible(String tag) {
        if (null != mFragmentManager) {
            Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
            if (null != fragmentByTag) {
                return fragmentByTag.isVisible();
            }
        }
        return false;
    }

    /**
     * 组合字幕弹窗
     */
    private void showCaptionKeyBoard() {
        if (captionEditPop == null) {
            captionEditPop = CaptionEditPop.create(this);
            captionEditPop.setEventListener(new CaptionEditPop.EventListener() {
                @Override
                public void onConfirm(String text, String textColor, NvAsset fontAsset) {
                    if (currentCompoundCaption != null && selectCaptionIndex >= 0) {
                        if (textColor != null) {
                            currentCompoundCaption.setTextColor(selectCaptionIndex, ColorUtil.colorStringtoNvsColor(textColor));
                        }
                        if (fontAsset != null) {
                            currentCompoundCaption.setFontFamily(selectCaptionIndex, fontAsset.name);
                        }
                        currentCompoundCaption.setText(selectCaptionIndex, text);
                    }
                }
            });
        }
        if (captionEditPop != null && !captionEditPop.isShow()) {
            captionEditPop.resetSign();
            captionEditPop.setNarBar(showNavigation, narHeight);
            if (currentCompoundCaption != null) {
                captionEditPop.setCaptionText(currentCompoundCaption.getText(selectCaptionIndex));
                captionEditPop.setCaptionTextColor(currentCompoundCaption.getTextColor(selectCaptionIndex));
                captionEditPop.setCaptionFont(currentCompoundCaption.getFontFamily(selectCaptionIndex));
            }
            captionEditPop.show();
        }
    }

    /**
     * 应用字幕
     *
     * @param effectId
     */
    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_COMPONENT_CAPTION_TYPE)
    private void applyCaption(String effectId) {
        if (TextUtils.isEmpty(effectId)) {
            return;
        }

        if (Util.isFastClick()) {
            return;
        }
        currentCompoundCaption = mStreamingContext.appendCaptureCompoundCaption(0, STICK_TIME_DURATION, effectId);
        if (currentCompoundCaption != null) {
            currentCompoundCaption.setScaleX(0.5f);
            currentCompoundCaption.setScaleY(0.5f);
            List<PointF> assetViewVerticesList = getAssetViewVerticesList(
                    false, currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME));
            List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
            AssetLevelBean levelBean = new AssetLevelBean(assetViewVerticesList);
            captionVertices.add(0, levelBean);
            currentCompoundCaption.setAttachment(Constants.KEY_LEVEL, levelBean.getTag());
            drawRect.setMuteVisible(false);
            updateDrawRectPosition(assetViewVerticesList, Constants.EDIT_MODE_COMPOUND_CAPTION, levelBean.getTag(), captions);
        }
    }

    private List<List<PointF>> getCaptionList(NvsCaptureCompoundCaption compoundCaption) {
        List<List<PointF>> captions = new ArrayList<>();
        if (compoundCaption != null) {
            int captionCount = compoundCaption.getCaptionCount();
            for (int i = 0; i < captionCount; i++) {
                List<PointF> pointFS = compoundCaption.getCaptionBoundingVertices(i, NvsTimelineCompoundCaption.BOUNDING_TYPE_TEXT);
                captions.add(getAssetViewVerticesList(false, pointFS));
            }
        }
        return captions;
    }


    /**
     * 展示道具
     */
    private void showArSceneFx() {
        /*
         * 只有美摄道具才可以使用
         *  Only beauty photo props can be used
         */
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
            if (initArScene) {
                // showCaptureDialogView(mFaceUPropDialog, mFaceUPropView);
                changeCaptureDisplay(false);
//                showCaptureDialogView(mPropsDialog.getDialog(), null);
                showCommonFragment(mPropTypeInfo, Constants.FRAGMENT_PROP_TAG);
            } else {
                // 授权过期
                /*
                 * 授权过期
                 * License expired
                 */
                String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
            }
        } else {
            String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
            Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
        }
    }


    /**
     * 初始化变声View
     * init change voice view
     */
    private void initChangeVoiceView() {
        mVoiceDialog = new AlertDialog.Builder(this).create();
        mVoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mVoiceDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeCaptureDisplay(true);
                        closeCaptureDialogView(mVoiceDialog);
                    }
                });
            }
        });
        mVoiceRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        voiceList = mCaptureViewModel.getVoiceDatas();
        VoiceAdapter adapter = new VoiceAdapter(getApplicationContext(), voiceList);
        mVoiceRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new VoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ChangeVoiceData voiceData = voiceList.get(position);
                if (voiceData != null) {
                    if (TextUtils.equals(voiceData.getName(), getResources().getString(R.string.timeline_fx_none))) {
                        mTvVoice.setText(getResources().getString(R.string.change_voice));
                    } else {
                        mTvVoice.setText(voiceData.getName());
                    }
                    if (mStreamingContext != null) {
                        mStreamingContext.removeAllCaptureAudioFx();
                        mStreamingContext.appendBuiltinCaptureAudioFx(voiceData.getVoiceId());
                    }
                }
            }
        });
    }

    private void initTopMoreView() {
        if (mMoreDialog == null) {
            mMoreDialog = TopMoreDialog.create(this, mStreamingContext);
            mMoreDialog.setEventListener(new TopMoreDialog.EventListener() {
                @Override
                public void onDismiss() {

                }

                @Override
                public void onDialogCancel() {
                }

                @Override
                public void onFrameClick(boolean frameFlag) {
                    if (frameFlag) {
                        setCaptureFrame();
                    } else {
                        closeCaptureFrame();
                    }
                }
            });
        }
    }


    @MSSubscribe(value = {Constants.SubscribeType.SUB_APPLY_FILTER_TYPE})
    private void applyFilter(String packageId) {
        removeAllFilterFx();
        if (!TextUtils.isEmpty(packageId)) {
            showFilterSeekViewVisible();
            mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(packageId);
            if (mCurCaptureVideoFx != null) {
                mCurCaptureVideoFx.setFilterIntensity(1.0f);
            }
        }
    }


    private void showFilterSeekViewVisible() {
        mFilterMpProgress.setVisibility(View.VISIBLE);
        mFilterMpProgress.setProgress(100);
    }

    @MSSubscribe("hideFilterSeekView")
    private void hideFilterSeekView() {
        mFilterMpProgress.setVisibility(View.INVISIBLE);
    }

    private void changeFilterIntensity(int progress, boolean fromUser) {
        float strength = progress * 1.0f / 100;
        if (mCurCaptureVideoFx != null) {
            mCurCaptureVideoFx.setFilterIntensity(strength);
        }
    }

    private CaptureHandler mHandler;
    private static final int HANDLER_FRAME = 1001;


    public static class CaptureHandler extends Handler {
        private WeakReference<CaptureActivity> weakReference;

        public CaptureHandler(CaptureActivity captureActivity) {
            this.weakReference = new WeakReference<>(captureActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            CaptureActivity captureActivity = weakReference.get();
            switch (msg.what) {
                case HANDLER_FRAME:
                    captureActivity.setCaptureFrame();
                    break;
            }
        }
    }

    /**
     * 设置实时帧率
     */
    private void setCaptureFrame() {
        if (mStreamingContext != null) {
            float v = mStreamingContext.detectEngineRenderFramePerSecond();
            if (mTvFrame.getVisibility() != View.VISIBLE) {
                mTvFrame.setVisibility(View.VISIBLE);
            }
            mTvFrame.setText((int) v + "");
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(HANDLER_FRAME, 500);
        }
    }

    /**
     * 关闭实时帧率检测
     */
    private void closeCaptureFrame() {
        if (mHandler != null) {
            mHandler.removeMessages(HANDLER_FRAME);
        }
        mTvFrame.setVisibility(View.GONE);
    }

    private void searchAssetData() {
        String bundlePath = "filter";
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FILTER, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_FILTER);

        //初始化本地到具
        if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
            bundlePath = "msarface";
        } else {
            bundlePath = "arface";
        }
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ARSCENE_FACE, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ARSCENE_FACE);

    }

    /**
     * 滤镜数据初始化
     * Filter data initialization
     */
    private void initFilterList() {
        mFilterDataArrayList.clear();
        mFilterDataArrayList = AssetFxUtil.getFilterData(this,
                getLocalData(NvAsset.ASSET_FILTER),
                null,
                true,
                false);
    }

    private void initFilterDialog() {
        mFilterDialog = new AlertDialog.Builder(this).create();
        mFilterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (mFilterSelPos > 0) {
                    mFilterBottomView.setSelectedPos(mFilterSelPos);
                }
                if (mCurCaptureVideoFx != null) {
                    mFilterBottomView.setIntensityLayoutVisible(View.VISIBLE);
                    mFilterBottomView.setIntensitySeekBarProgress((int) (mCurCaptureVideoFx.getFilterIntensity() * 100));
                }
            }
        });
        mFilterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mFilterDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mFilterDialog);
            }
        });
        mFilterBottomView = new FilterView(this);
        mFilterBottomView.setBlackTheme(false);
        /*
         * 设置滤镜数据
         * Set filter data
         */
        mFilterBottomView.initFilterRecyclerView(this);
        mFilterBottomView.setFilterArrayList(mFilterDataArrayList);
        mFilterBottomView.setIntensityLayoutVisible(View.INVISIBLE);
        mFilterBottomView.setIntensityTextVisible(View.GONE);
        mFilterBottomView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, int position) {
                int count = mFilterDataArrayList.size();
                if (position < 0 || position >= count) {
                    return;
                }
                if (mFilterSelPos == position) {
                    return;
                }
                mFilterSelPos = position;
                removeAllFilterFx();
                mFilterBottomView.setIntensitySeekBarMaxValue(100);
                mFilterBottomView.setIntensitySeekBarProgress(100);
                if (position == 0) {
                    mFilterBottomView.setIntensityLayoutVisible(View.INVISIBLE);
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(null);
                    mCurCaptureVideoFx = null;
                    return;
                }
                mFilterBottomView.setIntensityLayoutVisible(View.VISIBLE);
                FilterItem filterItem = mFilterDataArrayList.get(position);
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    String filterName = filterItem.getFilterName();

                    if (!TextUtils.isEmpty(filterName) && filterItem.getIsCartoon()) {
                        mBeautySwitch.setChecked(false);
                        mBeautyShapeSwitch.setChecked(false);
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx("Cartoon");
                        mCurCaptureVideoFx.setBooleanVal("Stroke Only", filterItem.getStrokenOnly());
                        mCurCaptureVideoFx.setBooleanVal("Grayscale", filterItem.getGrayScale());
                    } else if (!TextUtils.isEmpty(filterName)) {
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx(filterName);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(filterName);
                } else {
                    String filterPackageId = filterItem.getPackageId();
                    if (!TextUtils.isEmpty(filterPackageId)) {
                        mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterPackageId);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                    mVideoClipFxInfo.setFxId(filterPackageId);
                }
                if (mCurCaptureVideoFx != null) {
                    mCurCaptureVideoFx.setFilterIntensity(1.0f);
                }
            }

            @Override
            public void onMoreFilter() {
                /*
                 * 拍摄进入下载，不作比例适配
                 * Shoot into download, no proportion adaptation
                 */
                TimelineData.instance().setMakeRatio(NvAsset.AspectRatio_NoFitRatio);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "capture_filter");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
                mFilterBottomView.setMoreFilterClickable(false);
            }

            @Override
            public void onIntensity(int value) {
                if (mCurCaptureVideoFx != null) {
                    float intensity = value / (float) 100;
                    mCurCaptureVideoFx.setFilterIntensity(intensity);
                }
            }
        });
    }

    @MSSubscribe(Constants.SubscribeType.SUB_APPLY_PROP_TYPE)
    private void applyPropEffect(String packageId) {
        if (mArSceneFaceEffect == null) {
            initBeautyAndShapeData(true);
        }
        if (TextUtils.isEmpty(packageId)) {
            mArSceneFaceEffect.setStringVal(Constants.AR_SCENE_ID_KEY, "");
            return;
        }
        String sceneId = packageId;
        showPropsToast(sceneId);
        mArSceneFaceEffect.setStringVal(Constants.AR_SCENE_ID_KEY, sceneId);
    }

    @MSSubscribe(Constants.SubscribeType.SUB_UN_USE_PROP_TYPE)
    private void removeAllProp() {
        if (mArSceneFaceEffect == null) {
            return;
        }
        mArSceneFaceEffect.setStringVal("Scene Id", "");
    }

    private void showPropsToast(String sceneId) {
        NvsAssetPackageManager manager = mStreamingContext.getAssetPackageManager();
        if (manager == null) {
            return;
        }
        String packagePrompt = manager.getARSceneAssetPackagePrompt(sceneId);
        if (!TextUtils.isEmpty(packagePrompt)) {
            ToastUtil.showToastCenter(this, packagePrompt);
        }
    }

    private void initMakeupDialog() {
        mMakeUpDialog = new AlertDialog.Builder(this).create();
        mMakeUpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mMakeUpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
        mMakeUpView = new MakeUpSingleView(this);
        //获取美妆-单妆数据
        ArrayList<MakeupCustomModel> makeupCustomData = MakeupManager.getInstacne().getCustomMakeupDataList(this, false);
        if (makeupCustomData == null) {
            return;
        }
//        mMakeUpView.setMakeupComposeData(makeupComposeData);
        mMakeUpView.setMakeupCustomData(makeupCustomData);
        mMakeUpView.setOnMakeUpEventListener(new MakeUpSingleView.MakeUpEventListener() {
            @Override
            public void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup) {
                // onMakeupComposeDataChanged(position, isClearMakeup);
                //选择了美妆，美型需要切换到美型1
                //When Beauty Makeup is selected, Beauty needs to switch to Beauty 1
                selectMarkUp = true;
                onMakeupDataChanged(tabPosition, position);
            }

            @Override
            public void onMakeupColorChanged(String makeupId, NvsColor color) {
                if (mArSceneFaceEffect == null) {
                    return;
                }
                mArSceneFaceEffect.setColorVal("Makeup " + makeupId + " Color", color);
            }

            @Override
            public void onMakeupIntensityChanged(String makeupId, float intensity) {
                if (mArSceneFaceEffect == null) {
                    return;
                }
                mArSceneFaceEffect.setFloatVal("Makeup " + makeupId + " Intensity", intensity);
            }

            @Override
            public void removeVideoFxByName(String name) {
                removeFilterFxByName(name);
            }

            @Override
            public void onMakeUpViewDismiss() {
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
    }

    /**
     * 初始化美颜-样式
     */
    private void initBeautyStyleRecyclerView() {
        List<BeautyShapeDataItem> datas = mCaptureViewModel.getStyleDataList(this);
//        mBeautyStyleAdapter = new CommonRecyclerViewAdapter(R.layout.capture_beauty_style_item, BR.beautyStyleInfo);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mBeautyStyleRecyclerView.setLayoutManager(layoutManager);
//        mBeautyStyleRecyclerView.setAdapter(mBeautyStyleAdapter);
//        mBeautyStyleAdapter.setData(datas);

//        mBeautyStyleAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.OnItemClickListener<BeautyShapeDataItem>() {
//
//            @Override
//            public void onItemClick(View view, int posotion, BeautyShapeDataItem beautyStyleInfo) {
//                if (posotion > 0) {
//                    mBeautyShapeSwitch.setChecked(true);
//                    mBeautySwitch.setChecked(true);
//                    mSmallShapeSwitch.setChecked(true);
//                    mCurCaptureVideoFx.setFilterIntensity(0.4f);
//                } else {
//                    mCurCaptureVideoFx.setFilterIntensity(0.0f);
//                }
//                doOnBeautyStyleItemClick(beautyStyleInfo, datas);
//                checkAllEffectIsClose();
//            }
//        });

        if (parameterValues.isDefaultArScene()) {
            if (datas != null && datas.size() > 1) {
                //默认使用风格1的数据
                BeautyShapeDataItem beautyStyleInfo = datas.get(1);
                doOnBeautyStyleItemClick(beautyStyleInfo, datas);
            }
        } else {
            BeautyShapeDataItem beautyShapeDataItem = datas.get(0);
            beautyShapeDataItem.setVisible(true);
        }
        mCaptureViewModel.getDefaultData(getApplicationContext()).setVisible(true);
    }

    private void doOnBeautyStyleItemClick(BeautyShapeDataItem beautyStyleInfo, List<BeautyShapeDataItem> datas) {
        if (null == beautyStyleInfo) {
            return;
        }
        for (BeautyShapeDataItem info : datas) {
            info.setVisible(false);
        }
        beautyStyleInfo.setVisible(true);
        List<BeautyShapeDataItem> dataItems = beautyStyleInfo.getDataItems();
        if (mArSceneFaceEffect == null) {
            return;
        }
        if (getString(R.string.no).equals(beautyStyleInfo.getName())) {
            mArSceneFaceEffect.setStringVal("Makeup " + "Shadow" + " Package Id", null);
        } else {
//            if (null != mFxComposeId) {
//                mArSceneFaceEffect.setStringVal("Makeup " + "Shadow" + " Package Id", mFxComposeId.toString());
//            }
        }
        updateBeautyData(dataItems);
    }

    /**
     * 更新上层数据
     */
    @SuppressLint("CheckResult")
    private void updateBeautyData(List<BeautyShapeDataItem> dataItems) {

        for (BeautyShapeDataItem beautyShapeDataItem : dataItems) {
            if (mBeautySwitch.isChecked()) {
                for (ArBean beautyDataItem : mBeautyList) {
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (beautyShapeDataItem.getName().equals(beautyDataItem.getName())) {
                        beautyDataItem.setStrength(beautyShapeDataItem.strength);
                        beautyDataItem.defaultStrength = beautyShapeDataItem.strength;
                        ArSceneUtils.getInstance().applyData(mStreamingContext, beautyDataItem);
//                        applyBeautyData(beautyDataItem);
                    }
                }

                for (ArBean beautyItem : mBeautySkinList) {
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (beautyShapeDataItem.getName().equals(beautyItem.getName())) {
                        beautyItem.setStrength(beautyShapeDataItem.strength);
                        beautyItem.defaultStrength = beautyShapeDataItem.strength;
                        ArSceneUtils.getInstance().applyData(mStreamingContext, beautyItem);
//                        applyBeautyData(beautyItem);
                    }
                }
            }

            if (mBeautyShapeSwitch.isChecked()) {
                for (ArBean shapeItem : mShapeDataList) {
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (beautyShapeDataItem.getName().equals(shapeItem.getName())) {
                        shapeItem.setStrength(beautyShapeDataItem.strength);
                        shapeItem.defaultStrength = beautyShapeDataItem.strength;
                        ArSceneUtils.getInstance().applyData(mStreamingContext, shapeItem);
//                        applyBeautyData(shapeItem);
                    }
                }
            }

            if (mSmallShapeSwitch.isChecked()) {
                for (ArBean smallItem : mSmallShapeDataList) {
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (beautyShapeDataItem.getName().equals(smallItem.getName())) {
                        smallItem.setStrength(beautyShapeDataItem.strength);
                        smallItem.defaultStrength = beautyShapeDataItem.strength;
                        ArSceneUtils.getInstance().applyData(mStreamingContext, smallItem);
//                        applyBeautyData(smallItem);
                    }
                }
            }
        }
    }

    private void applyBeautyStyleData(List<ArBean> dataItems) {
        if (null == dataItems) {
            return;
        }
        if (mArSceneFaceEffect == null) {
            return;
        }
        for (ArBean beautyShapeDataItem : dataItems) {
            if (getString(R.string.filter_natural_complexion).equals(beautyShapeDataItem.getName())) {
                if (null != mFxFilterPackageId.toString()) {
                    removeAllFilterFx();
                    mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(mFxFilterPackageId.toString());
                    if (mCurCaptureVideoFx != null) {
                        mCurCaptureVideoFx.setFilterIntensity((float) beautyShapeDataItem.getStrength());
                    }
                }
                break;
            }
        }
    }

    /**
     * 初始化美颜
     */
    private void initBeautyRecyclerView() {
        mBeautyAdapter = new SkinAdapter(this, mBeautySkinList, mBeautyList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBeautyRecyclerView.setLayoutManager(layoutManager);
        mBeautyRecyclerView.setAdapter(mBeautyAdapter);
        mBeautyAdapter.setEnable(OPEN_ALL_SWITCH);
        mBeautyAdapter.setSwitch(mBeautySwitch);
        mBeautyAdapter.setOnItemClickListener(new SkinAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                mLlSubSeekContainer.setVisibility(View.GONE);
                if (position == 0) {
                    mTvBeautyA.setVisibility(View.GONE);
                    mTvBeautyB.setVisibility(View.GONE);
                    mAdjustColorLayout.setVisibility(View.GONE);
                    mSharpenLayout.setVisibility(View.GONE);
                    changeBeautySeekViewVisible(View.GONE);
                    changeBeautySeekThreshVisible(View.GONE);
                    return;
                }
                ArBean selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "mBeautyAdapter selectItem is null");
                    return;
                }
                double defaultLevel = selectItem.defaultStrength;
                double level = selectItem.getStrength();
                mBeautySeekBar.setPointProgress((int) (defaultLevel * 100));
                mBeautySeekBar.setProgress((int) (level * 100));

                if (selectItem instanceof DegreasingInfo){
                    defaultLevel = ((DegreasingInfo) selectItem).defaultSubStrength;
                    level = ((DegreasingInfo) selectItem).getSubStrength();
                    mBeautySubSeekBar.setPointProgress((int) (defaultLevel));
                    mBeautySubSeekBar.setProgress((int) (level));
                }

                changeBeautySeekThreshVisible(View.GONE);
                if (getResources().getString(R.string.correctionColor).equals(name)) {
                    //校色
                    if (mAdjustColorSwitch.isChecked()) {
                        if (mAdjustColorFx != null) {
                            changeBeautySeekViewVisible(View.VISIBLE);
                        }
                    } else {
                        changeBeautySeekViewVisible(View.GONE);
                        if (mAdjustColorFx != null) {
                            mAdjustColorFx.setFilterIntensity(0);
                        }
                    }
                } else if (getResources().getString(R.string.sharpness).equals(name)) {
                    //锐度
                    changeBeautySeekViewVisible(View.GONE);
                }else if (getResources().getString(R.string.quyouguang).equals(name)) {
                    changeBeautySeekViewVisible(View.VISIBLE);
                    mLlSubSeekContainer.setVisibility(View.VISIBLE);
                } else {
                    changeBeautySeekViewVisible(View.VISIBLE);
                }
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mAdjustColorLayout.setVisibility(View.GONE);
                mSharpenLayout.setVisibility(View.GONE);
                if (name.startsWith(getResources().getString(R.string.whitening))) {
                    mTvBeautyA.setVisibility(View.VISIBLE);
                    mTvBeautyB.setVisibility(View.VISIBLE);
                } else if (getResources().getString(R.string.correctionColor).equals(name)) {
                    mAdjustColorLayout.setVisibility(View.VISIBLE);
                } else if (getResources().getString(R.string.sharpness).equals(name)) {
                    mSharpenLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void changeBeautySeekThreshVisible(int visible) {
        tvBeautySb.setVisibility(visible);
    }

    private void changeBeautySeekViewVisible(int visible) {
        mLLBeautySeek.setVisibility(visible);
        mBeautySeekBar.setVisibility(visible);
    }

    /**
     * 初始化微整形
     */
    private void initSmallRecyclerView() {
        mSmallShapeAdapter = new ShapeAdapter(this,
                mSmallShapeDataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSmallShapeRecyclerView.setLayoutManager(layoutManager);
        mSmallShapeRecyclerView.setAdapter(mSmallShapeAdapter);
        mSmallShapeAdapter.setEnable(OPEN_ALL_SWITCH);
        mSmallShapeAdapter.setSwitch(mSmallShapeSwitch);
        mSmallShapeAdapter.setOnItemClickListener(new ShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {

                ArBean selectItem = mSmallShapeAdapter.getSelectItem();
                if (selectItem != null && selectItem instanceof ShapeBean) {
                    boolean isShape = ((ShapeBean) selectItem).isShapeFlag();
                    if (isShape) {
                        //美型
                        mSmallSeekBar.setMax(200);
                        mSmallSeekBar.setPointEnable(true);
                        mSmallSeekBar.setBreakProgress(100);


                        mSmallSeekBar.setVisibility(View.VISIBLE);
                        double floatVal = mSmallShapeAdapter.getSelectItem().getStrength();
                        double defaultLevel = mSmallShapeAdapter.getSelectItem().defaultStrength;
                        //设置初始默认值，View层表现是一个凸起的一个点
                        mSmallSeekBar.setPointProgress((int) (defaultLevel * 100 + 100));
                        mSmallSeekBar.setProgress((int) (floatVal * 100 + 100));
                    } else {
                        //美颜
                        mSmallSeekBar.setMax(100);
                        mSmallSeekBar.setPointEnable(true);
                        mSmallSeekBar.setVisibility(View.VISIBLE);
                        mSmallSeekBar.setBreakProgress(0);

                        double level = mSmallShapeAdapter.getSelectItem().getStrength();
                        double defaultLevel = mSmallShapeAdapter.getSelectItem().defaultStrength;
                        //设置初始默认值，View层表现是一个凸起的一个点
                        mSmallSeekBar.setPointProgress((int) (defaultLevel * 100));
                        mSmallSeekBar.setProgress((int) (level * 100));
                    }
                }
            }
        });
    }


    private void initMakeupViewVisible() {
        if ((mArSceneFaceEffect == null) || !initArScene) {
//            mMakeupTabButton.setVisibility(View.GONE);
            if (mShapeAdapter != null) {
                mShapeAdapter.setEnable(false);
            }
        }
    }

    /**
     * 初始化美型的各个特效的列表
     * Initializes the list of various effects for the beauty type
     */
    private void initShapeRecyclerView() {

        mShapeAdapter = new ShapeAdapter(this, mShapeDataList);
        mShapeAdapter.setIsBeautyShape(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mShapeRecyclerView.setLayoutManager(linearLayoutManager);
        mShapeRecyclerView.setAdapter(mShapeAdapter);
        mShapeAdapter.setSwitch(mBeautyShapeSwitch);
        int space = ScreenUtils.dip2px(this, 8);
        mShapeRecyclerView.addItemDecoration(new SpaceItemDecoration(space, 0));
        mShapeAdapter.setOnItemClickListener(new ShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name) {
                if (position < 0 || position >= mShapeAdapter.getItemCount()) {
                    return;
                }
                mShapeSeekBar.setVisibility(View.VISIBLE);
                /*
                 * 美型程度
                 * Beauty degree
                 */
                double level = 0.0;
                ArBean selectItem = mShapeAdapter.getSelectItem();
                if (selectItem == null || !selectItem.isCanReplace()) {
                    if (selectItem != null) {
                        ToastUtils.showShort("can not replace");
                    }
                    return;
                }

                //这些不应该前端做兼容
//                if (getString(R.string.eye_enlarging).equals(name) ||
//                        getString(R.string.cheek_thinning).equals(name) ||
//                        getString(R.string.eye_corner).equals(name) ||
//                        getString(R.string.intensity_nose).equals(name)) {
//                    //大眼睛
//                    mShapeSeekBar.setMax(100);
//                    mShapeSeekBar.setPointEnable(true);
//                    mShapeSeekBar.setBreakProgress(0);
//
//                    //设置默认值
//                    double defaultLevel = selectItem.defaultStrength;
//                    mShapeSeekBar.setPointProgress((int) (defaultLevel * 100));
//                    mShapeSeekBar.setProgress((int) (selectItem.getStrength() * 100));
//                } else {
                mShapeSeekBar.setMax(200);
                mShapeSeekBar.setPointEnable(true);
                mShapeSeekBar.setBreakProgress(100);


                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    double floatVal = selectItem.getStrength();
                    if (floatVal >= 0) {
                        level = (Math.round(floatVal * 100)) * 0.01;
                    } else {
                        level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
                    }
                    /*
                     * 美型特效值的范围[-1,1]
                     * Range of American special effects [-1,1]
                     */
                    mShapeSeekBar.setProgress((int) (level * 100 + 100));
                    //设置默认值
                    double defaultLevel = selectItem.defaultStrength;
                    mShapeSeekBar.setPointProgress((int) (defaultLevel * 100 + 100));
                }
//                }

            }
        });
    }


    private void shapeLayoutEnabled(Boolean isEnabled) {
        mBeautyShapeResetLayout.setEnabled(isEnabled);
        mBeautyShapeResetLayout.setClickable(isEnabled);
        mShapeAdapter.setEnable(isEnabled);
        if (isEnabled) {
            mBeautyShapeResetIcon.setAlpha(1f);
            mBeautyShapeResetTxt.setTextColor(Color.BLACK);
        } else {
            mBeautyShapeResetIcon.setAlpha(0.5f);
            mBeautyShapeResetTxt.setTextColor(getResources().getColor(R.color.ms_disable_color));
        }
    }


    /**
     * 初始化美颜 美型特效对象
     * Initialize the beauty effect object
     */
    private void initBeautyAndShapeData(boolean isOpenArsceneEffect) {
        if (isOpenArsceneEffect && mArSceneFaceEffect == null) {
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                mArSceneFaceEffect = mStreamingContext.appendBuiltinCaptureVideoFx(Constants.AR_SCENE);
            } else {
                mBeautyFx = mStreamingContext.appendBeautyCaptureVideoFx();
            }
        }
        boolean singleBufferMode = parameterValues.isSingleBufferMode();
        if (mArSceneFaceEffect != null) {
            if (BuildConfig.FACE_MODEL == 240) {
                mArSceneFaceEffect.setBooleanVal("Use Face Extra Info", true);
            }
            //支持的人脸个数，是否需要使用最小的设置
            mArSceneFaceEffect.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);
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
//
//            //只有一个缩头是用的之前的，其他的都是用的新的，其他的不需要策略
//            mArSceneFaceEffect.setIntVal("Head Size Warp Strategy", 0);

            //大眼
//            mArSceneFaceEffect.setStringVal("Face Mesh Eye Size Custom Description File", "assets:/beauty/shape/dayan.xml");
            //颧骨增宽
//            mArSceneFaceEffect.setStringVal("Face Mesh Malar Width Custom Description File", "assets:/beauty/shape/quangu.xml");
            //宽脸
//            mArSceneFaceEffect.setStringVal("Face Mesh Face Width Custom Description File", "assets:/beauty/shape/shoulian.xml");
            //鼻子
//            mArSceneFaceEffect.setStringVal("Face Mesh Nose Width Custom Description File", "assets:/beauty/shape/shoubi.xml");
        }

        initDefaultVideoFx(isOpenArsceneEffect);

    }

    /**
     * 默认需要添加的特效
     */
    private void initDefaultVideoFx(boolean isOpenArsceneEffect) {
        if (isOpenArsceneEffect) {
            StringBuilder adjustPackageId = new StringBuilder();
            mFxFilterPackageId = new StringBuilder();
//            mFxComposeId = new StringBuilder();

            //自然肤色
            String fxFilterPath = "assets:/filter/D1C01CF7-CA73-4CB7-A6B7-630B5FF9EC74.1.videofx";

            //初始化校色
            String adjustFxPath = "assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx";

            //榛果修容 240 单状
//            String fxCompose240Path = "assets:/beauty/makeup/compose240/252D0D4B-58D7-46C6-A8A0-BCA9BDAB99AB.2.makeup";
//
//            mStreamingContext.getAssetPackageManager().installAssetPackage(fxCompose240Path, null,
//                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP, true, mFxComposeId);

            mStreamingContext.getAssetPackageManager().installAssetPackage(adjustFxPath, null,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, adjustPackageId);

            mStreamingContext.getAssetPackageManager().installAssetPackage(fxFilterPath, null,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, mFxFilterPackageId);


            mAdjustColorFx = mStreamingContext.appendPackagedCaptureVideoFx(adjustPackageId.toString());
            mAdjustColorFx.setFilterIntensity(0f);

            mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(mFxFilterPackageId.toString());
            if (mCurCaptureVideoFx != null) {
                mCurCaptureVideoFx.setFilterIntensity(0.8f);
            }

            mNvsEffectSdkContext.getAssetPackageManager().
                    installAssetPackage(adjustFxPath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, false, null);

            mNvsEffectSdkContext.getAssetPackageManager().
                    installAssetPackage(fxFilterPath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, false, null);

            mNeedSelectEffectInfo =new EffectInfo();
            mNeedSelectEffectInfo.setId(mFxFilterPackageId.toString());
            mNeedSelectEffectInfo.setDownload(true);
            mNeedSelectEffectInfo.setCoverUrl("file:///android_asset/filter/D1C01CF7-CA73-4CB7-A6B7-630B5FF9EC74.png");
            mNeedSelectEffectInfo.setName(getString(R.string.capture_filter_default));
        }

    }


    @Override
    protected void initTitle() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        MSBus.getInstance().register(this);
        mFragmentManager = getSupportFragmentManager();
        mHandler = new CaptureHandler(this);
        parameterValues = ParameterSettingValues.instance();
        BarUtils.isNavigationBarExist(this, new BarUtils.OnNavigationStateListener() {
            @Override
            public void onNavigationState(boolean isShowing, int nHeight) {
                showNavigation = isShowing;
                narHeight = nHeight;
            }
        });

//        mBeautyList = mCaptureViewModel.getBeautyDataList(mContext);
//        mBeautySkinList = mCaptureViewModel.getBeautyData(mContext);
//        mShapeDataList = mCaptureViewModel.getShapeDataList(mContext);
//        mSmallShapeDataList = mCaptureViewModel.getSmallShapeDataList(mContext);
        mBeautyList = ArSceneUtils.getInstance().getBeautyData(mContext);
        mBeautySkinList = ArSceneUtils.getInstance().getSkinData(mContext);
        mShapeDataList = ArSceneUtils.getInstance().getShapeData(mContext);
        mSmallShapeDataList = ArSceneUtils.getInstance().getMicroShapeData(mContext);

        changeAspectRatio();
        initCaptureData();
        initCapture();

        searchAssetData();

        boolean defaultArScene = parameterValues.isDefaultArScene();
        initBeautyAndShapeData(defaultArScene);

        /*
         * 滤镜初始化
         * Filter initialization
         */
        initFilterList();
        initFilterDialog();


        /**
         * 初始化美状弹窗
         */
        initMakeupDialog();


        /*
         * 美型初始化
         */
        initShapeRecyclerView();

        initBeautyRecyclerView();
        initSmallRecyclerView();
        initMakeupViewVisible();


//        mBeautyStyleTabButton.setSelected(true);
        mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
        mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//        mVSkinBeautyStyleLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
        mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));

        initCaptionFontInfoList();

        //初始化美颜几个开关按钮的状态
        if (parameterValues != null) {
//            setBeautyShapeSwitchChecked(defaultArScene);
//            setBeautySwitchChecked(defaultArScene);
//            setSmallBeautyShapeSwitchChecked(defaultArScene);
            mShapeSwitchIsOpen = defaultArScene;
            mBeautyShapeSwitch.setChecked(defaultArScene);
            mBeautySwitch.setChecked(defaultArScene);
            mSmallShapeSwitch.setChecked(defaultArScene);
        }

        initViewModelObserver();

        /*
         * 美型美颜样式
         */
        initBeautyStyleRecyclerView();

    }

    private void initViewModelObserver() {
        mCaptureViewModel.getFilterTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mFilterTypeInfo = info;
                showFilterFragment();
            }
        });
        mCaptureViewModel.getPropTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mPropTypeInfo = info;
                showCommonFragment(mPropTypeInfo, Constants.FRAGMENT_PROP_TAG);
            }
        });
        mCaptureViewModel.getStickerTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mStickerTypeInfo = info;
                showCommonFragment(mStickerTypeInfo, Constants.FRAGMENT_STICKER_TAG);
            }
        });
        mCaptureViewModel.getComponentTypeInfo().observe(this, new Observer<TypeAndCategoryInfo>() {
            @Override
            public void onChanged(TypeAndCategoryInfo info) {
                mComponentTypeInfo = info;
                showCommonFragment(mComponentTypeInfo, Constants.FRAGMENT_COMPONENT_CAPTION_TAG);
            }
        });
    }

    /**
     * 设置选中的素材
     *
     * @param attachment
     */
    private boolean checkSelectByAttachment(String attachment) {
        if (drawRectModel < 0 || TextUtils.isEmpty(attachment)) {
            return false;
        }
        if (drawRectModel == Constants.EDIT_MODE_STICKER) {
            int count = mStreamingContext.getCaptureAnimatedStickerCount();
            for (int i = 0; i < count; i++) {
                NvsCaptureAnimatedSticker animatedSticker = mStreamingContext.getCaptureAnimatedStickerByIndex(i);
                if (animatedSticker != null && TextUtils.equals(attachment, (String) animatedSticker.getAttachment(Constants.KEY_LEVEL))) {
                    currentAnimatedSticker = animatedSticker;
                    updateDrawRectPosition(getAssetViewVerticesList(currentAnimatedSticker.getHorizontalFlip(), currentAnimatedSticker.getBoundingRectangleVertices()), drawRectModel, attachment, null);
                    // 将选中的素材提到第一个
                    AssetLevelBean addBean = null;
                    for (int j = 0; j < stickerVertices.size(); j++) {
                        if (TextUtils.equals(stickerVertices.get(j).getTag(), attachment)) {
                            addBean = stickerVertices.get(j);
                            stickerVertices.remove(j);
                            break;
                        }
                    }
                    if (addBean != null) {
                        stickerVertices.add(0, addBean);
                    }
                    return true;
                }
            }
        } else if (drawRectModel == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            int count = mStreamingContext.getCaptureCompoundCaptionCount();
            for (int i = 0; i < count; i++) {
                NvsCaptureCompoundCaption compoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);
                if (compoundCaption != null && TextUtils.equals(attachment, (String) compoundCaption.getAttachment(Constants.KEY_LEVEL))) {
                    currentCompoundCaption = compoundCaption;
                    List<PointF> vertices = currentCompoundCaption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
                    List<List<PointF>> captions = getCaptionList(currentCompoundCaption);
                    updateDrawRectPosition(getAssetViewVerticesList(false, vertices), drawRectModel, attachment, captions);
                    // 将选中的素材提到第一个
                    AssetLevelBean addBean = null;
                    for (int j = 0; j < captionVertices.size(); j++) {
                        if (TextUtils.equals(captionVertices.get(j).getTag(), attachment)) {
                            addBean = captionVertices.get(j);
                            captionVertices.remove(j);
                            break;
                        }
                    }
                    if (addBean != null) {
                        captionVertices.add(0, addBean);
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 检查选中的Attachment
     *
     * @param vertices
     * @param pointF
     * @return
     */
    private String checkTouchAttachment(List<AssetLevelBean> vertices, Point pointF) {
        if (vertices != null) {
            for (int i = 0; i < vertices.size(); i++) {
                List<PointF> pointFS = vertices.get(i).getData();
                if (checkTouchInPath(pointFS, pointF)) {
                    return vertices.get(i).getTag();
                }
            }
        }
        return "";
    }

    /**
     * 检查点是否在坐标区域内
     *
     * @param pointFS
     * @param point
     * @return
     */
    private boolean checkTouchInPath(List<PointF> pointFS, Point point) {
        Path path = new Path();
        if (pointFS != null && pointFS.size() >= 4) {
            path.reset();
            path.moveTo(pointFS.get(0).x, pointFS.get(0).y);
            path.lineTo(pointFS.get(1).x, pointFS.get(1).y);
            path.lineTo(pointFS.get(2).x, pointFS.get(2).y);
            path.lineTo(pointFS.get(3).x, pointFS.get(3).y);
            path.lineTo(pointFS.get(0).x, pointFS.get(0).y);
        }
        if (Util.isPointInPath(point, path)) {
            return true;
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        initBeautyClickListener();
        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideEffectFragment(Constants.FRAGMENT_FILTER_TAG);
                hideEffectFragment(Constants.FRAGMENT_PROP_TAG);
                if (drawRectModel >= 0) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        checkTouchAssetFrame(new PointF(event.getX(), event.getY()));
                        clearSelectState();
                        if (currentAnimatedSticker == null && currentCompoundCaption == null) {
                            closeCaptionFx();
                            closeStickerFx();
                        }
                    }
                }
                float rectHalfWidth = mImageAutoFocusRect.getWidth() / 2;
                if (event.getX() - rectHalfWidth >= 0 && event.getX() + rectHalfWidth <= mLiveWindow.getWidth()
                        && event.getY() - rectHalfWidth >= 0 && event.getY() + rectHalfWidth <= mLiveWindow.getHeight()) {
                    mImageAutoFocusRect.setX(event.getX() - rectHalfWidth);
                    mImageAutoFocusRect.setY(event.getY() - rectHalfWidth);
                    RectF rectFrame = new RectF();
                    rectFrame.set(mImageAutoFocusRect.getX(), mImageAutoFocusRect.getY(),
                            mImageAutoFocusRect.getX() + mImageAutoFocusRect.getWidth(),
                            mImageAutoFocusRect.getY() + mImageAutoFocusRect.getHeight());
                    /*
                     * 启动自动聚焦
                     * Start autofocus
                     */
                    mImageAutoFocusRect.startAnimation(mFocusAnimation);
                    if (m_supportAutoFocus) {
                        mStreamingContext.startAutoFocus(new RectF(rectFrame));
                    }
                }

                return false;
            }
        });

        mIvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
         * 切换摄像头开关
         * Toggle camera switch
         */
        mIvChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSwitchingCamera) {
                    return;
                }
                if (mCurrentDeviceIndex == 0) {
                    mCurrentDeviceIndex = 1;
                } else {
                    mCurrentDeviceIndex = 0;
                }
                changeSegmentModel();
                mIsSwitchingCamera = true;
                startCapturePreview(true);
                if (mMoreDialog != null) {
                    mMoreDialog.setFlashEnable(mCurrentDeviceIndex != 1);
                }
            }
        });
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog.isShowing()) {
                    mMoreDialog.dismiss();
                } else {
                    mMoreDialog.show();
                }
            }
        });
        /*
         * 美颜
         * Beauty
         */
        mBeautyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mCaptureBeautyDialog, mBeautyView);
            }
        });

        //美肤重置
        mBeautyResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeautySeekBar.setVisibility(View.INVISIBLE);
                mLlSubSeekContainer.setVisibility(View.INVISIBLE);
                mBeautyAdapter.setSelectPos(Integer.MAX_VALUE);
                for (ArBean beautyShapeDataItem : mBeautyList) {
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    ArSceneUtils.getInstance().applyData(mStreamingContext, beautyShapeDataItem);
//                    applyBeautyData(beautyShapeDataItem);
                }
                for (ArBean beautyShapeDataItem : mBeautySkinList) {
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    if (beautyShapeDataItem.getStrength() == 0) {
                        continue;
                    }
                    ArSceneUtils.getInstance().applyData(mStreamingContext, beautyShapeDataItem);
//                    applyBeautyData(beautyShapeDataItem);
                }
            }
        });

        //美型重置
        mBeautyShapeResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                for (ArBean beautyShapeDataItem : mShapeDataList) {
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    ArSceneUtils.getInstance().applyData(mStreamingContext, beautyShapeDataItem);
//                    applyBeautyData(beautyShapeDataItem);
                }
            }
        });

        //微整型重置
        mSmallShapeResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmallSeekBar.setVisibility(View.INVISIBLE);
                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                for (ArBean beautyShapeDataItem : mSmallShapeDataList) {
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    ArSceneUtils.getInstance().applyData(mStreamingContext, beautyShapeDataItem);
//                    applyBeautyData(beautyShapeDataItem);
                }
            }
        });

        /*
         * 变声
         */
        mIvVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeCaptureDisplay(false);
                showCaptureDialogView(mVoiceDialog, mVoiceView);
            }
        });
        /*
         * 美妆
         * Makeup
         */
        mLlMakeupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mMakeUpDialog, mMakeUpView);
            }
        });


        /*
         * 滤镜
         * Filter
         */
        mFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
//                showCaptureDialogView(mFilterDialog, mFilterBottomView);
//                mFilterBottomView.setVisibility(View.VISIBLE);
//                showBottomSheetDialog(mFilterBottomView);
                showFilterFragment();
            }
        });

        /*
         * 开始录制
         *Start recording
         */
        mFlStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean startNativeCamera = parameterValues.isStartNativeCamera();
                if (startNativeCamera) {
                    if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                        takePhoto();
                        changeRecordDisplay(RECORD_DEFAULT, true);
                        return;
                    }
                }

                /*
                 * 当前在录制状态，可停止视频录制
                 * Currently in recording state, you can stop video recording
                 */
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    stopRecording();
                } else {
                    mCurRecordVideoPath = PathUtils.getRecordVideoPath();
                    if (mCurRecordVideoPath == null) {
                        return;
                    }
                    mFlStartRecord.setEnabled(false);
                    if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
                        mEachRecodingVideoTime = 0;
                        /*
                         * 当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                         * If video recording is not currently in progress, start video recording. Use the recording method with special effects here
                         */
                        if (!mStreamingContext.startRecording(mCurRecordVideoPath)) {
                            return;
                        }
                        changeRecordDisplay(RECORDING, false);
                        mRecordFileList.add(mCurRecordVideoPath);
                    } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                        mStreamingContext.startRecording(mCurRecordVideoPath);
                        changeRecordDisplay(RECORDING, true);
                    }
                }
            }
        });
        /*
         * 删除视频
         * Delete video
         */
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordTimeList.size() != 0 && mRecordFileList.size() != 0) {
                    mAllRecordingTime -= mRecordTimeList.get(mRecordTimeList.size() - 1);
                    mRecordTimeList.remove(mRecordTimeList.size() - 1);
                    PathUtils.deleteFile(mRecordFileList.get(mRecordFileList.size() - 1));
                    mRecordFileList.remove(mRecordFileList.size() - 1);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));

                    if (mRecordTimeList.size() == 0) {
                        changeRecordDisplay(RECORD_DEFAULT, mRecordType == Constants.RECORD_TYPE_PICTURE);
                    } else {
                        mStartText.setText(mRecordTimeList.size() + "");
                        mRecordTime.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        /*
         * 下一步，进入编辑
         * Next, enter edit
         */
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * mRecordFileList，视频存储列表。将拍摄的视频传到下一个页面
                 * mRecordFileList, video storage list. Send the captured video to the next page.
                 */
                ArrayList<ClipInfo> pathList = new ArrayList<>();
                for (int i = 0; i < mRecordFileList.size(); i++) {
                    ClipInfo clipInfo = new ClipInfo();
                    clipInfo.setFilePath(mRecordFileList.get(i));
                    pathList.add(clipInfo);
                }
                if (pathList.size() <= 0) {
                    return;
                }
                NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(pathList.get(0).getFilePath());
                if (avFileInfo == null) {
                    return;
                }
                /*
                 * 数据清空
                 * Data clear
                 */
                TimelineData.instance().clear();
                NvsSize size = avFileInfo.getVideoStreamDimension(0);
                int rotation = avFileInfo.getVideoStreamRotation(0);
                if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                        || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
                    int tmp = size.width;
                    size.width = size.height;
                    size.height = tmp;
                }
                int makeRatio = size.width > size.height ? NvAsset.AspectRatio_16v9 : NvAsset.AspectRatio_9v16;
                TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
                TimelineData.instance().setMakeRatio(makeRatio);
                TimelineData.instance().setClipInfoData(pathList);
                mNext.setClickable(false);

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.START_ACTIVITY_FROM_CAPTURE, true);
                AppManager.getInstance().jumpActivity(CaptureActivity.this, VideoEditActivity.class, bundle);
            }
        });

        mTvChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(true);
            }
        });
        mTvChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(false);
            }
        });
        mPictureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurRecordVideoPath != null) {
                    File file = new File(mCurRecordVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                showPictureLayout(false);
                if (mRecordTimeList.isEmpty()) {
                    mDelete.setVisibility(View.INVISIBLE);
                    mNext.setVisibility(View.INVISIBLE);
                    mStartText.setVisibility(View.INVISIBLE);
                }
            }
        });

        mPictureOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * 拍照片
                 * Take a photo
                 */
                if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                    mAllRecordingTime += mEachRecodingImageTime;
                    mRecordTimeList.add(mEachRecodingImageTime);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));
                    mStartText.setText(String.format("%d", mRecordTimeList.size()));
                    changeRecordDisplay(RECORD_FINISH, true);
                }
                String jpgPath = PathUtils.getTakePhotoDirectory();
                boolean save_ret = Util.saveBitmapToSD(mPictureBitmap, jpgPath);
                MediaScannerUtil.scanFile(jpgPath, "image/jpg");
                if (save_ret) {
                    mRecordFileList.add(jpgPath);
                }
                if (mCurRecordVideoPath != null) {
                    File file = new File(mCurRecordVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                showPictureLayout(false);
            }
        });

        mIvCaptureConstrast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        closeAllBeautyEffect();
                        return true;
                    case MotionEvent.ACTION_UP:
                        resetAllBeautyData();
                        break;
                }
                return false;
            }
        });

    }

    private void closeAllBeautyEffect() {
        //美颜开关
        mArSceneFaceEffect.setBooleanVal("Beauty Effect", false);
        //美型开关
        mArSceneFaceEffect.setBooleanVal("Beauty Shape", false);
        //美型开关
        mArSceneFaceEffect.setBooleanVal("Face Mesh Internal Enabled", false);
        //高级美颜开关
        mArSceneFaceEffect.setBooleanVal("Advanced Beauty Enable", false);
    }

    private void resetAllBeautyData() {
        //美颜开关
        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
        //美型开关
        mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
        //美型开关
        mArSceneFaceEffect.setBooleanVal("Face Mesh Internal Enabled", true);
        //高级美颜开关
        mArSceneFaceEffect.setBooleanVal("Advanced Beauty Enable", true);
    }

    private void showFilterFragment() {
        Makeup captureMakeupInfo = MakeupHelper.getInstance().getCaptureMakeupInfo();
        int isCanReplace = 1;
        if (captureMakeupInfo != null) {
            MakeupEffectContent effectContent = captureMakeupInfo.getEffectContent();
            if (effectContent != null) {
                List<FilterArgs> filter = effectContent.getFilter();
                for (FilterArgs filterArgs : filter) {
                    if (filterArgs.getCanReplace() == 0) {
                        isCanReplace = filterArgs.getCanReplace();
                        break;
                    }
                }
            }
        }

        if (isCanReplace == 0) {
            ToastUtil.showToast(mContext, getString(R.string.makeup_not_allow_change_this));
            return;
        }

        if (mFilterTypeInfo == null) {
            mCaptureViewModel.getEffectTypeData(Constants.FRAGMENT_FILTER_TAG);
        } else {
            Fragment fragment = mFragmentManager.
                    findFragmentByTag(Constants.FRAGMENT_FILTER_TAG);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (fragment == null) {
                fragment = CaptureFilterFragment.newInstance(mFilterTypeInfo);
                fragmentTransaction.replace(R.id.bottom_container, fragment, Constants.FRAGMENT_FILTER_TAG);
            }
            showFragment(fragment, fragmentTransaction, Constants.FRAGMENT_FILTER_TAG);
        }

    }


    private void showCommonFragment(TypeAndCategoryInfo info, String tag) {
        if (info == null) {
            mCaptureViewModel.getEffectTypeData(tag);
            return;
        }
        Fragment fragment = mFragmentManager.
                findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (fragment == null) {
            fragment = CaptureEffectFragment.newInstance(info, tag);
            fragmentTransaction.replace(R.id.bottom_container_high, fragment, tag);
        }
        showFragment(fragment, fragmentTransaction, tag);
    }


    private void hideEffectFragment(String tag) {
        Fragment fragment = mFragmentManager.
                findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (Constants.FRAGMENT_FILTER_TAG.equals(tag)) {
            hideFilterSeekView();
        }
        hideFragment(fragment, fragmentTransaction, tag);
        if (Constants.FRAGMENT_PROP_TAG.equals(tag)
                || Constants.FRAGMENT_FILTER_TAG.equals(tag)
                || Constants.FRAGMENT_BEAUTY_TAG.equals(tag)) {
            changeCaptureDisplay(true);
        }
    }

    private void showFragment(Fragment fragment, FragmentTransaction
            fragmentTransaction, String tag) {
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_bottom_in,
                R.anim.slide_bottom_out
        );
        fragmentTransaction.show(fragment);
//        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment, FragmentTransaction
            fragmentTransaction, String tag) {
        fragmentTransaction.hide(fragment);
//        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }


    private void changeCaptureDisplay(boolean display) {
        if (display) {
            if (!mRecordTimeList.isEmpty()) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mIvExit.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mTvVoice.setVisibility(mRecordType == Constants.RECORD_TYPE_PICTURE ? View.GONE : View.VISIBLE);
            mIvVoice.setVisibility(mRecordType == Constants.RECORD_TYPE_PICTURE ? View.GONE : View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);
            mFlBottomParent.setVisibility(View.VISIBLE);
            drawRect.setVisibility(View.GONE);
            mIvCaptureConstrast.setVisibility(View.GONE);
        } else {
            mIvVoice.setVisibility(View.GONE);
            mTvVoice.setVisibility(View.GONE);
            mIvExit.setVisibility(View.INVISIBLE);
            mIvMore.setVisibility(View.INVISIBLE);
            mIvChangeCamera.setVisibility(View.INVISIBLE);
            mLlRightContainer.setVisibility(View.INVISIBLE);
            mFlBottomParent.setVisibility(View.INVISIBLE);
            mFlMiddleParent.setVisibility(View.INVISIBLE);
            mIvCaptureConstrast.setVisibility(View.VISIBLE);
        }
        mTvFrame.setVisibility(mIvVoice.getVisibility());
    }


    /**
     * 美颜dialog 动作监听
     * Beauty dialog action monitoring
     */
    private void initBeautyClickListener() {
        /*
         *美颜控制开关
         *Beauty control switch
         */
        mBeautyTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeSeekBar.setVisibility(View.INVISIBLE);
//                mShapeText.setVisibility(View.INVISIBLE);
//                mShapeText2.setVisibility(View.INVISIBLE);
//                if (mBeautySwitchIsOpend) {
//                    adjustColorOrBeauty();
//                }
                mBeautyTabButton.setSelected(true);
                mShapeTabButton.setSelected(false);
//                mBeautyStyleTabButton.setSelected(false);
                mSmallTabButton.setSelected(false);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//                mVSkinBeautyStyleLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mBeautySelectRelativeLayout.setVisibility(View.VISIBLE);
                mShapeSelectRelativeLayout.setVisibility(View.GONE);
//                mBeautyStyleSelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.GONE);
                mRlSmallSeekRootView.setVisibility(View.GONE);
                mSmallSeekBar.setVisibility(View.GONE);
                mBeautyAdapter.setSelectPos(Integer.MAX_VALUE);
                mBeautyAdapter.notifyDataSetChanged();
            }
        });

//        mBeautyStyleTabButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mShapeSeekBar.setVisibility(View.INVISIBLE);
//                mBeautySeekBar.setVisibility(View.GONE);
//                mSmallSeekBar.setVisibility(View.GONE);
//                mBeautyStyleTabButton.setSelected(true);
//                mBeautyTabButton.setSelected(false);
//                mShapeTabButton.setSelected(false);
//                mSmallTabButton.setSelected(false);
//                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//                mVSkinBeautyStyleLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
//                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//
////                mBeautyStyleSelectRelativeLayout.setVisibility(View.VISIBLE);
//                mShapeSelectRelativeLayout.setVisibility(View.GONE);
//                mBeautySelectRelativeLayout.setVisibility(View.GONE);
//                mSmallSelectRelativeLayout.setVisibility(View.GONE);
//
//                mRlSmallSeekRootView.setVisibility(View.GONE);
//
//            }
//        });
        //美颜模式A
        //Beauty mode A
        mTvBeautyA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeautyAdapter == null) {
                    return;
                }
                NvsCaptureVideoFx videoEffect = (mCanUseARFaceType == HUMAN_AI_TYPE_MS) ? mArSceneFaceEffect : mBeautyFx;
                changeBeautyWhiteMode(videoEffect, true, true);
            }
        });
        //美颜模式B
        //Beauty mode B
        mTvBeautyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeautyAdapter == null) {
                    return;
                }
                NvsCaptureVideoFx videoEffect = (mCanUseARFaceType == HUMAN_AI_TYPE_MS) ? mArSceneFaceEffect : mBeautyFx;
                changeBeautyWhiteMode(videoEffect, false, true);
            }
        });

        mShapeTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBeautySeekViewVisible(View.GONE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mBeautySeekBar.setVisibility(View.GONE);
                mLlSubSeekContainer.setVisibility(View.GONE);
                changeBeautySeekThreshVisible(View.GONE);
//                mShapeText.setVisibility(View.INVISIBLE);
//                mShapeText2.setVisibility(View.INVISIBLE);
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mBeautyTabButton.setSelected(false);
//                mBeautyStyleTabButton.setSelected(false);
                mSmallTabButton.setSelected(false);
                mShapeTabButton.setSelected(true);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//                mVSkinBeautyStyleLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mBeautySelectRelativeLayout.setVisibility(View.GONE);
//                mBeautyStyleSelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.GONE);
                mRlSmallSeekRootView.setVisibility(View.GONE);
                mSmallSeekBar.setVisibility(View.GONE);
                mShapeSelectRelativeLayout.setVisibility(View.VISIBLE);

                //此时切换到美型 如果之前选择过美妆，切换过来需要显示默认选中美型1效果
                if (selectMarkUp) {

                    selectMarkUp = false;
                }

                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                mShapeAdapter.notifyDataSetChanged();
            }
        });

        mSmallTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeBeautySeekViewVisible(View.GONE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mBeautySeekBar.setVisibility(View.GONE);
                mLlSubSeekContainer.setVisibility(View.GONE);
                changeBeautySeekThreshVisible(View.GONE);
//                mSmallSeekText.setVisibility(View.INVISIBLE);
//                mSmallSeekText2.setVisibility(View.INVISIBLE);
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mBeautyTabButton.setSelected(false);
//                mBeautyStyleTabButton.setSelected(false);
                mShapeTabButton.setSelected(false);

                mSmallTabButton.setSelected(true);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
//                mVSkinBeautyStyleLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVSmallBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));

                mBeautySelectRelativeLayout.setVisibility(View.GONE);
//                mBeautyStyleSelectRelativeLayout.setVisibility(View.GONE);
                mShapeSelectRelativeLayout.setVisibility(View.GONE);
                mSmallSelectRelativeLayout.setVisibility(View.VISIBLE);
                mRlSmallSeekRootView.setVisibility(View.VISIBLE);

                mSmallShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                mSmallShapeAdapter.notifyDataSetChanged();
            }
        });


        mSharpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    if (mArSceneFaceEffect == null) {
                        return;
                    }
                    if (!isChecked) {
                        mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", false);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_close), "#CCFFFFFF", R.color.colorTranslucent);
                    } else {
                        mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", true);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_open), "#CCFFFFFF", R.color.colorTranslucent);
                    }
                } else {
                    if (!isChecked) {
                        mBeautyFx.setBooleanVal("Default Sharpen Enabled", false);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_close), "#CCFFFFFF", R.color.colorTranslucent);
                    } else {
                        mBeautyFx.setBooleanVal("Default Sharpen Enabled", true);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_open), "#CCFFFFFF", R.color.colorTranslucent);
                    }
                }
            }
        });

        mAdjustColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean defaultArScene = parameterValues.isDefaultArScene();
                    if (!defaultArScene) {
                        parameterValues.setDefaultArScene(true);
                        parameterValues.setParameterSettingValues();
                    }
                    changeBeautySeekViewVisible(View.VISIBLE);
                    ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.default_beauty_open), "#CCFFFFFF", R.color.colorTranslucent);
                    /*double tempLevel = mArSceneFaceEffect.getFloatVal("Default Intensity");
                    mBeautySeekBar.setProgress((int) (tempLevel * 100));*/
                    if (mAdjustColorFx != null) {
                        mAdjustColorFx.setFilterIntensity(mBeautySeekBar.getProgress() * 1.0f / 100);
//                        mBeautyText2.setText((int) (mBeautySeekBar.getProgress() * 1.0f / 100) + "%");
                    }
                } else {
                    //z关闭校色
//                    if (mAdjustColorFx != null) {
//                        mStreamingContext.removeCaptureVideoFx(mAdjustColorFx.getIndex());
//                    }
//                    mAdjustColorFx = null;
                    if (mAdjustColorFx != null) {
                        mAdjustColorFx.setFilterIntensity(0);
                    }
                    changeBeautySeekViewVisible(View.GONE);
                    ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.default_beauty_close), "#CCFFFFFF", R.color.colorTranslucent);
                }
            }
        });

        //美颜
        mBeautySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    boolean defaultArScene = parameterValues.isDefaultArScene();
                    if (!defaultArScene) {
                        parameterValues.setDefaultArScene(true);
                        parameterValues.setParameterSettingValues();
                    }
                    if (mArSceneFaceEffect == null) {
                        initBeautyAndShapeData(true);
                    }
                } else {
                    mStreamingContext.clearCachedResources(true);
                }
                setBeautySwitchChecked(isChecked);
                checkAllEffectIsClose();
            }
        });

        //美型
        mBeautyShapeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShapeSwitchIsOpen = isChecked;
                if (isChecked) {
                    boolean defaultArScene = parameterValues.isDefaultArScene();
                    if (!defaultArScene) {
                        parameterValues.setDefaultArScene(true);
                        parameterValues.setParameterSettingValues();
                    }
                    if (mArSceneFaceEffect == null) {
                        initBeautyAndShapeData(true);
                    }
                } else {
                    mStreamingContext.clearCachedResources(true);
                }
                if (mCanUseARFaceType != HUMAN_AI_TYPE_MS) {
                    mBeautyShapeSwitch.setChecked(false);
                    String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
                    Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                } else {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                        setBeautyShapeSwitchChecked(isChecked);
                    } else {
                        /*
                         * 授权过期
                         * License expired
                         * */
                        String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                        Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                        mBeautyShapeSwitch.setChecked(false);
                    }
                }
                checkAllEffectIsClose();
            }
        });

        //微整形
        mSmallShapeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean defaultArScene = parameterValues.isDefaultArScene();
                    if (!defaultArScene) {
                        parameterValues.setDefaultArScene(true);
                        parameterValues.setParameterSettingValues();
                    }
                    if (mArSceneFaceEffect == null) {
                        initBeautyAndShapeData(true);
                    }
                } else {
                    mStreamingContext.clearCachedResources(true);
                }
                if (mCanUseARFaceType != HUMAN_AI_TYPE_MS) {
                    String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
                    Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                } else {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                        setSmallBeautyShapeSwitchChecked(isChecked);
                    } else {
                        /*
                         * 授权过期
                         * License expired
                         * */
                        String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                        Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                        mSmallShapeSwitch.setChecked(false);
                    }
                }
                checkAllEffectIsClose();
            }
        });

        //美型滑杆操作
        mShapeSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (mShapeAdapter.getSelectPos() >= 0 && mShapeAdapter.getSelectPos() <= mShapeAdapter.getItemCount()) {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        ArBean selectItem = mShapeAdapter.getSelectItem();
                        boolean containsShapeId = mShapeIdList.contains(selectItem.getArId());
//                        float strength = containsShapeId ? ((float) (progress) / 100) : ((float) (progress - 100) / 100);
                        selectItem.setStrength((float) (progress - 100) / 100);


                        //（1）、大眼：界面上滑杆不需要小眼效果（不需要当前的负值）
                        //（2）、瘦鼻子：界面上不需要宽鼻子效果（不需要当前的正值）  应用的时候反向
                        //（3）、瘦脸：界面上不需要胖脸效果（不需要当前的正值）应用的时候反向
                        //（4）、眼角：名称改完眼睑下置，界面上不需要向上的效果   应用的时候反向
                        ArSceneUtils.getInstance().applyData(mStreamingContext, selectItem);
//                        applyBeautyData(selectItem);
                    }
                }
            }
        });

        //微整形滑杆操作
        mSmallSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                ArBean selectItem = mSmallShapeAdapter.getSelectItem();
                if (TextUtils.isEmpty(selectItem.getArId())) {
                    return;
                }

                if (selectItem != null && selectItem instanceof ShapeBean) {
                    boolean isShape = ((ShapeBean) selectItem).isShapeFlag();
                    selectItem.setStrength(isShape ? ((float) (progress - 100) * 1.0f / 100) : (progress * 1.0 / 100));
//                    applyBeautyData(selectItem);
                    ArSceneUtils.getInstance().applyData(mStreamingContext, selectItem);
                }
            }
        });
        mBeautySeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                ArBean selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "onProgressChange selectItem is null");
                    return;
                }
                double strength = progress * 1.0 / 100;
                selectItem.setStrength(strength);
                ArSceneUtils.getInstance().applyData(mStreamingContext, selectItem);
//                applyBeautyData(selectItem);
            }
        });

        mBeautySubSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                ArBean selectItem = mBeautyAdapter.getSelectItem();
                if (selectItem == null) {
                    Log.e(TAG, "onProgressChange selectItem is null");
                    return;
                }
                double strength = progress * 0.27+3;
                if (selectItem instanceof DegreasingInfo){
                    ((DegreasingInfo) selectItem).setSubStrength(strength);
                }
                ArSceneUtils.getInstance().applyData(mStreamingContext, selectItem);
//                applyBeautyData(selectItem);
            }
        });

    }

    private void checkAllEffectIsClose() {
        boolean checked = mBeautySwitch.isChecked();
        boolean checkedShape = mBeautyShapeSwitch.isChecked();
        boolean checkedSmall = mSmallShapeSwitch.isChecked();
//        int currentSelectPosition = mBeautyStyleAdapter.getCurrentSelectPosition();
//        Log.d("meicam","----------------------------checkAllEffectIsClose-------------------------------------------------------");
//        Log.d("meicam","-------------------------------checked="+checked+" checkedShape="+checkedShape+" checkedSmall="+
//                checkedSmall+" currentSelectPosition="+currentSelectPosition);
//        if (!checked && !checkedShape && !checkedSmall && currentSelectPosition == 0) {
        if (!checked && !checkedShape && !checkedSmall) {
            mArSceneFaceEffect.setStringVal("Default Beauty Lut File", "");
            mArSceneFaceEffect.setStringVal("Whitening Lut File", "");
            mArSceneFaceEffect.setBooleanVal("Whitening Lut Enabled", false);
        }
    }

    private void changeBeautyWhiteMode(NvsCaptureVideoFx videoEffect, boolean isOpen,
                                       boolean isExchange) {
        if (videoEffect == null) {
            return;
        }
        if (isExchange) {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
                ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningA), "#CCFFFFFF", R.color.colorTranslucent);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
                ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningB), "#CCFFFFFF", R.color.colorTranslucent);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_B));

                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_B));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
//                mBeautyAdapter.setWittenName(getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            }
        }
    }

    /**
     * 切换美颜开关
     *
     * @param isChecked
     */
    private void setBeautySwitchChecked(boolean isChecked) {
        if (isChecked) { //启动美颜
            List<ArBean> items = mBeautyAdapter.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (i == 0) { //处理磨皮
                    //同时只能使用同一种磨皮效果，选中的磨皮效果
                    ArBean selectedBeautyTempData = mBeautyAdapter.getSelectedBeautyTempData();
                    if (selectedBeautyTempData == null) {
                        continue;
                    }
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (null != mArSceneFaceEffect) {
                            String name = selectedBeautyTempData.getName();
                            if (getResources().getString(R.string.advanced_strength_1).equals(name)) {
                                mArSceneFaceEffect.setIntVal("Advanced Beauty Type", Constants.BeautyType.ADVANCE_1);
                                mArSceneFaceEffect.setFloatVal("Beauty Strength", 0);
                            } else if (getResources().getString(R.string.advanced_strength_2).equals(name)) {
                                mArSceneFaceEffect.setIntVal("Advanced Beauty Type", Constants.BeautyType.ADVANCE_2);
                                mArSceneFaceEffect.setFloatVal("Beauty Strength", 0);
                            } else if (getResources().getString(R.string.strength_1).equals(name)) {
                                mArSceneFaceEffect.setFloatVal("Advanced Beauty Intensity", 0);
                            }
                            mArSceneFaceEffect.setFloatVal(selectedBeautyTempData.getArId(), selectedBeautyTempData.getStrength());
                        }
                    } else {
                        if (null != mBeautyFx) {
                            String name = selectedBeautyTempData.getName();
                            if (getResources().getString(R.string.advanced_strength_1).equals(name)) {
                                mBeautyFx.setIntVal("Advanced Beauty Type", Constants.BeautyType.ADVANCE_1);
                                mBeautyFx.setFloatVal("Beauty Strength", 0);
                            } else if (getResources().getString(R.string.advanced_strength_2).equals(name)) {
                                mBeautyFx.setIntVal("Advanced Beauty Type", Constants.BeautyType.ADVANCE_2);
                                mBeautyFx.setFloatVal("Beauty Strength", 0);
                            } else if (getResources().getString(R.string.strength_1).equals(name)) {
                                mBeautyFx.setFloatVal("Advanced Beauty Intensity", 0);
                            }
                            mBeautyFx.setFloatVal(selectedBeautyTempData.getArId(), selectedBeautyTempData.getStrength());
                        }
                    }

                } else {
                    ArBean beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    String beautyShapeId = beautyShapeDataItem.getArId();
                    if (TextUtils.isEmpty(beautyShapeId)) {
                        continue;
                    }
                    String beautyName = beautyShapeDataItem.getName();
                    //除了磨皮，其他情况
                    if (beautyName.equals(mContext.getResources().getString(R.string.strength_1))
                            || beautyName.equals(mContext.getResources().getString(R.string.advanced_strength_1))
                            || beautyName.equals(mContext.getResources().getString(R.string.advanced_strength_2))) {
                        continue;//其他的磨皮直接循环下一个
                    } else if (mContext.getResources().getString(R.string.whitening_A).equals(beautyName)) {
                        //美白A
                        changeBeautyWhiteMode(mArSceneFaceEffect, true, true);
                    } else if (mContext.getResources().getString(R.string.whitening_B).equals(beautyName)) {
                        //美白B
                        changeBeautyWhiteMode(mArSceneFaceEffect, false, true);
                    } else if (mContext.getResources().getString(R.string.correctionColor).equals(beautyName)) {
                        //校色
                        if (mAdjustColorSwitch.isChecked()) { //校色开关打开状态
                            if (mAdjustColorFx != null) {
                                mAdjustColorFx.setFilterIntensity((float) beautyShapeDataItem.getStrength());
                            }
                        } else {//校色开关是关闭状态
                            if (mAdjustColorFx != null) {
                                mAdjustColorFx.setFilterIntensity(0);
                            }
                        }
                    } else if (mContext.getResources().getString(R.string.sharpness).equals(beautyName)) {
                        //锐度
                        boolean isSharpenChecked = mSharpenSwitch.isChecked();
                        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                            if (mArSceneFaceEffect == null) {
                                return;
                            }
                            mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", isSharpenChecked);
                        } else {
                            mBeautyFx.setBooleanVal("Default Sharpen Enabled", isSharpenChecked);
                        }

                    } else if (mContext.getResources().getString(R.string.ruddy).equals(beautyName)) {
                        //红润
                        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                            mArSceneFaceEffect.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                        } else {
                            mBeautyFx.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                        }
                    }
                }
            }
            mBeauty_switch_text.setText(R.string.beauty_close);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.black));
        } else {  //关闭美颜
            List<ArBean> items = mBeautyAdapter.getItems();
//            int strengthSelectPos = mBeautyAdapter.getStrengthSelectPos();
            for (int i = 0; i < items.size(); i++) {
                ArBean beautyShapeDataItem = items.get(i);
                if (beautyShapeDataItem == null) {
                    continue;
                }
                String beautyShapeId = beautyShapeDataItem.getArId();
                if (TextUtils.isEmpty(beautyShapeId)) {
                    continue;
                }
                String beautyName = beautyShapeDataItem.getName();
                if (mContext.getResources().getString(R.string.sharpness).equals(beautyName)) {
                    //锐度
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", false);
                    } else {
                        mBeautyFx.setBooleanVal("Default Sharpen Enabled", false);
                    }
                } else if (mContext.getResources().getString(R.string.correctionColor).equals(beautyName)) {
                    //校色
                    if (mAdjustColorFx != null) {
                        mAdjustColorFx.setFilterIntensity(0);
                    }
                } else {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        mArSceneFaceEffect.setFloatVal(beautyShapeId, 0);
                    } else {
                        mBeautyFx.setFloatVal(beautyShapeId, 0);
                    }
                }

            }

            //由于磨皮的双态显示，导致有一种情况磨皮循环数据不存在，暂时加在外边
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                mArSceneFaceEffect.setFloatVal("Beauty Strength", 0);
                mArSceneFaceEffect.setFloatVal("Advanced Beauty Intensity", 0);
            } else {
                mBeautyFx.setFloatVal("Beauty Strength", 0);
                mBeautyFx.setFloatVal("Advanced Beauty Intensity", 0);
            }

            mBeauty_switch_text.setText(R.string.beauty_open);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.ms_disable_color));
        }
        mBeautyAdapter.setEnable(isChecked);
        mBeautySwitch.setChecked(isChecked);
    }

    private void setBeautyShapeSwitchChecked(boolean isChecked) {
        if (mArSceneFaceEffect == null) {
            return;
        }

        List<ArBean> items = mShapeAdapter.getItems();
        if (isChecked) {
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_close);
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    ArBean beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(beautyShapeDataItem.getArId())) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        mArSceneFaceEffect.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    } else {
                        mBeautyFx.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    }
                }
            }
        } else {
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_open);
            mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            mShapeSeekBar.setVisibility(View.INVISIBLE);
            //特效通过参数关闭
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    ArBean beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(0);
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        mArSceneFaceEffect.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    } else {
                        mBeautyFx.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    }
                }
            }


        }
        mShapeAdapter.setDataList(items);
        mBeautyShapeSwitch.setChecked(isChecked);
        shapeLayoutEnabled(isChecked);
    }

    /**
     * 切换微整形
     *
     * @param isChecked
     */
    private void setSmallBeautyShapeSwitchChecked(boolean isChecked) {
        List<ArBean> items = mSmallShapeAdapter.getItems();
        if (isChecked) {
            mSmallShapeSwitchText.setText(R.string.small_shape_close);
            mSmallShapeSwitchText.setText(R.string.small_shape_close);
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    ArBean beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(beautyShapeDataItem.getArId())) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(beautyShapeDataItem.defaultStrength);
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        mArSceneFaceEffect.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    } else {
                        mBeautyFx.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    }
                }
            }
        } else {
            mSmallShapeSwitchText.setText(R.string.small_shape_open);
            mSmallShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            mSmallSeekBar.setVisibility(View.INVISIBLE);
//            mSmallSeekText.setVisibility(View.GONE);
//            mSmallSeekText2.setVisibility(View.GONE);
            //特效通过参数关闭
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    ArBean beautyShapeDataItem = items.get(i);
                    if (beautyShapeDataItem == null) {
                        continue;
                    }
                    beautyShapeDataItem.setStrength(0);
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        mArSceneFaceEffect.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    } else {
                        mBeautyFx.setFloatVal(beautyShapeDataItem.getArId(), beautyShapeDataItem.getStrength());
                    }
                }
            }
        }
        mSmallShapeAdapter.setDataList(items);
        mSmallShapeSwitch.setChecked(isChecked);
        mSmallShapeAdapter.setEnable(isChecked);
    }


    /**
     * 美妆应用
     * 注意：
     * 1.整妆和单妆都是一个makeup包，但是妆容是多个单妆包用json组合起来的整妆
     * 2.整妆和单妆是互斥的，整妆的优先级高于单妆。
     * 即在应用单妆的时候需要将已添加的整妆移除。如果先添加的是单妆，添加整妆时则因为优先级，SDK会将单妆置为无效，只有整妆效果
     *
     * @param tabPosition
     * @param position
     */
    private void onMakeupDataChanged(int tabPosition, int position) {
        if (mArSceneFaceEffect == null) {
            initBeautyAndShapeData(true);
        }
        if (mArSceneFaceEffect == null) {
            return;
        }
        if (tabPosition == 0 && position == 0) {
            //妆容且选中为无的情况下
            //todo 其他情况下是否需要记录美妆或者恢复某些数据
            tempFilterList.clear();
            for (ArBean beautyShapeDataItem : mShapeDataList) {
                beautyShapeDataItem.setCanReplace(true);
            }
            for (ArBean beautyShapeDataItem : mSmallShapeDataList) {
                beautyShapeDataItem.setCanReplace(true);
            }
        }
        BeautyData selectItem = mMakeUpView.getSelectItem();
        if (selectItem instanceof Makeup) {
            Makeup item = (Makeup) selectItem;
            if (tabPosition == 0) {
                clearMicroShape();
                clearShape();
                clearAllCustomMakeup();
                List<BeautyShapeDataItem> datas = mCaptureViewModel.getStyleDataList(this);
                clearMakeupFx();
                if (position == 0) {
                    if (datas != null && datas.size() > 1) {
                        //默认使用风格1的数据
                        BeautyShapeDataItem beautyStyleInfo = datas.get(1);
                        doOnBeautyStyleItemClick(beautyStyleInfo, datas);
                    }
                    boolean addFlag = true;
                    for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
                        if (TextUtils.equals(mFxFilterPackageId.toString(), mStreamingContext.getCaptureVideoFxByIndex(i).getCaptureVideoFxPackageId())) {
                            addFlag = false;
                        }
                    }
                    if (addFlag) {
                        mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(mFxFilterPackageId.toString());
                        if (mCurCaptureVideoFx != null) {
                            mCurCaptureVideoFx.setFilterIntensity(0.4f);
                        }
                    }
                } else {
                    if (datas != null && datas.size() > 0) {
                        //默认使用风格1的数据
                        BeautyShapeDataItem beautyStyleInfo = datas.get(0);
                        doOnBeautyStyleItemClick(beautyStyleInfo, datas);
                    }
                }
            } else {
                //单状无的情况
                if ((position == 0)) {
                    resetCustomMakeup(mMakeUpView.getSelectMakeupId());
                }
            }
            if (tabPosition == 0) {
                if (preMakeUp != null) {
                    preEffectContent = preMakeUp.getEffectContent();
                }
                preMakeUp = item;
                resetEffectContent(preEffectContent);
            }

            if (tabPosition == 0) {
                //妆容
//                MakeupEffectContent makeupEffectContent = item.getEffectContent();
//                if (makeupEffectContent == null) {
//                    return;
//                }
//                Log.d("=====", "start set makeUp ");
//                //添加效果包中带的美颜
//                setMakeupBeautyArgs(makeupEffectContent.getBeauty(), false, false);
//                //添加效果包中带的美型
//                setMakeupBeautyArgs(makeupEffectContent.getShape(), false, false);
//                //添加效果包中带的微整形
//                setMakeupBeautyArgs(makeupEffectContent.getMicroShape(), true, false);
//                //添加效果包中带的滤镜
//                List<FilterArgs> filter = makeupEffectContent.getFilter();
//                setFilterContent(filter);
//                //添加美妆
//                List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
//                if (makeupArgs != null) {
//                    for (MakeupArgs args : makeupArgs) {
//                        if (args == null) {
//                            continue;
//                        }
//                        if (tabPosition == 0) {
//                            MakeupData makeupData = new MakeupData(-1, MakeUpView.DEFAULT_MAKEUP_INTENSITY, new ColorData());
//                            makeupData.setUuid(args.getUuid());
//                            MakeupManager.getInstacne().addMakeupEffect(args.getType(), makeupData);
//                        }
//                        if (mArSceneFaceEffect != null) {
//                            mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
//                            NvsColor nvsColor = new NvsColor(0, 0, 0, 0);
//                            if (tabPosition != 0) {
//                                MakeupData makeupData = MakeupManager.getInstacne().getMakeupPackageEffect(args.getType(), args.getUuid());
//                                //单妆展示
//                                if (makeupData != null && makeupData.getColorData() != null) {
//                                    int color = makeupData.getColorData().color;
//                                    float alphaF = (Color.alpha(color) * 1.0f / 255f);
//                                    float red = (Color.red(color) * 1.0f / 255f);
//                                    float green = (Color.green(color) * 1.0f / 255f);
//                                    float blue = (Color.blue(color) * 1.0f / 255f);
//                                    nvsColor = new NvsColor(red, green, blue, alphaF);
//                                }
//                            }
////                        mArSceneFaceEffect.setColorVal("Makeup " + args.getType() + " Color", nvsColor);
//                            mArSceneFaceEffect.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
//                            mArSceneFaceEffect.setStringVal(args.getClassName(), args.getUuid());
//                        }
//                    }
//                }
                String effectPath = item.getFolderPath() + File.separator + item.getUuid();
                if (item.isCustom()) {//存放在本地的folderPath为当前素材目录，网络的与本地folderPath有不同，暂时不清楚
                    effectPath = item.getFolderPath();
                }
                File file = new File(effectPath);
                try {
                    MakeupHelper.getInstance().applyCaptureMakeupEffect(mArSceneFaceEffect, file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //添加美妆
                MakeupEffectContent makeupEffectContent = item.getEffectContent();
                if (makeupEffectContent == null) {
                    return;
                }
                List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
                if (makeupArgs != null) {
                    for (MakeupArgs args : makeupArgs) {
                        if (args == null) {
                            continue;
                        }
                        if (tabPosition == 0) {
                            MakeupData makeupData = new MakeupData(-1, MakeUpView.DEFAULT_MAKEUP_INTENSITY, new ColorData());
                            makeupData.setUuid(args.getUuid());
                            MakeupManager.getInstacne().addMakeupEffect(args.getType(), makeupData);
                        }
                        if (mArSceneFaceEffect != null) {
                            mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                            NvsColor nvsColor = new NvsColor(0, 0, 0, 0);
                            if (tabPosition != 0) {
                                MakeupData makeupData = MakeupManager.getInstacne().getMakeupPackageEffect(args.getType(), args.getUuid());
                                //单妆展示
                                if (makeupData != null && makeupData.getColorData() != null) {
                                    int color = makeupData.getColorData().color;
                                    float alphaF = (Color.alpha(color) * 1.0f / 255f);
                                    float red = (Color.red(color) * 1.0f / 255f);
                                    float green = (Color.green(color) * 1.0f / 255f);
                                    float blue = (Color.blue(color) * 1.0f / 255f);
                                    nvsColor = new NvsColor(red, green, blue, alphaF);
                                }
                            }
                            mArSceneFaceEffect.setColorVal("Makeup " + args.getType() + " Color", nvsColor);
                            mArSceneFaceEffect.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
                            mArSceneFaceEffect.setStringVal(args.getClassName(), args.getUuid());
                        }
                    }
                }
            }
            Log.d("=====", "end set makeUp ");
        }
    }

    private void resetEffectContent(MakeupEffectContent preEffectContent) {
        if (preEffectContent == null) {
            return;
        }
        Log.d("=====", "start reset makeUp ");
        //添加效果包中带的美颜
//        setMakeupBeautyArgs(preEffectContent.getBeauty(), false, true);
        //添加效果包中带的美型
        setMakeupBeautyArgs(preEffectContent.getShape(), false, true);
        //添加效果包中带的微整形
        setMakeupBeautyArgs(preEffectContent.getMicroShape(), true, true);
        //添加美妆
        Log.d("=====", "end reset makeUp ");
    }

    private void clearShape() {
        if (mShapeDataList != null) {
            for (ArBean beautyShapeDataItem : mShapeDataList) {
                if (beautyShapeDataItem instanceof ShapeBean && ((ShapeBean) beautyShapeDataItem).isPackageShapeFlag()) {
                    NvsCaptureVideoFx captureVideoFx;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        captureVideoFx = mArSceneFaceEffect;
                    } else {
                        captureVideoFx = mBeautyFx;
                    }
                    captureVideoFx.setStringVal(((ShapeBean) beautyShapeDataItem).isWrapFlag() ?
                            ((ShapeBean) beautyShapeDataItem).getWarpId() : ((ShapeBean) beautyShapeDataItem).getFaceMeshId(), null);
                    Log.d(TAG, "clearShape setNull" + (((ShapeBean) beautyShapeDataItem).isWrapFlag() ?
                            ((ShapeBean) beautyShapeDataItem).getWarpId() : ((ShapeBean) beautyShapeDataItem).getFaceMeshId()));
                }
            }
        }
    }

    private void clearMicroShape() {
        if (mSmallShapeDataList != null) {
            for (ArBean beautyShapeDataItem : mSmallShapeDataList) {
                if (beautyShapeDataItem instanceof ShapeBean && ((ShapeBean) beautyShapeDataItem).isPackageShapeFlag()) {
                    NvsCaptureVideoFx captureVideoFx;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        captureVideoFx = mArSceneFaceEffect;
                    } else {
                        captureVideoFx = mBeautyFx;
                    }
                    captureVideoFx.setStringVal(((ShapeBean) beautyShapeDataItem).isWrapFlag() ?
                            ((ShapeBean) beautyShapeDataItem).getWarpId() : ((ShapeBean) beautyShapeDataItem).getFaceMeshId(), null);
                    Log.d(TAG, "clearMicroShape setNull" + (((ShapeBean) beautyShapeDataItem).isWrapFlag() ?
                            ((ShapeBean) beautyShapeDataItem).getWarpId() : ((ShapeBean) beautyShapeDataItem).getFaceMeshId()));
                }
            }
        }
    }

    private void setFilterContent(List<FilterArgs> filter) {
        if (filter == null) {
            return;
        }
        tempFilterList.clear();
        removeAllFilterFx();
        for (FilterArgs filterArgs : filter) {
            if (filterArgs == null) {
                continue;
            }
            String packageId = filterArgs.getUuid();
            MakeupManager.getInstacne().putFilterFx(packageId);
            NvsCaptureVideoFx nvsCaptureVideoFx;
            if (filterArgs.getIsBuiltIn() == 1) {
                nvsCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx(packageId);
            } else {
                nvsCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(packageId);
            }
            if (nvsCaptureVideoFx != null) {
                nvsCaptureVideoFx.setFilterIntensity(NumberUtils.parseString2Float(filterArgs.getValue()));
                Log.d(TAG, "setFilterContent id:" + packageId + " value:" + filterArgs.getValue());
            } else {
                Log.e(TAG, "nvsCaptureVideoFx == null");
            }
        }
    }

    //记录当前美妆使用的滤镜
    List<FilterArgs> tempFilterList = new ArrayList<>();

    /**
     * 美妆联动滤镜相关
     *
     * @param filter
     */
    private void upDateFilter(List<FilterArgs> filter) {
        if (filter == null) {
            return;
        }
        //todo 滤镜联动只有一个，滤镜要注意多个的时候不要删除其他的
        tempFilterList.addAll(filter);
    }

    /**
     * 联动微整形相关
     *
     * @param microShape
     */
    private void upDateMicroShape(List<BeautyFxArgs> microShape) {
        if (microShape == null || mSmallShapeDataList == null) {
            return;
        }
        //todo 微整形联动
        for (ArBean beautyShapeDataItem : mSmallShapeDataList) {
            //先恢复
            beautyShapeDataItem.setCanReplace(true);
            //是否是美型包
            if (beautyShapeDataItem instanceof ShapeBean && ((ShapeBean) beautyShapeDataItem).isPackageShapeFlag()) {
                for (BeautyFxArgs shape : microShape) {
                    if (shape == null) {
                        continue;
                    }
                    if ((!TextUtils.isEmpty(((ShapeBean) beautyShapeDataItem).getFaceMeshPath())
                            && TextUtils.equals(((ShapeBean) beautyShapeDataItem).getFaceMeshId(), shape.getUuid()))
                            || (!TextUtils.isEmpty(((ShapeBean) beautyShapeDataItem).getWarpPath())
                            && TextUtils.equals(((ShapeBean) beautyShapeDataItem).getWarpId(), shape.getUuid()))) {
                        beautyShapeDataItem.setCanReplace(shape.isCanReplace());
                        beautyShapeDataItem.setStrength(NumberUtils.parseString2Float(shape.getValue()));
                        break;
                    }
                }
            }
        }
        mSmallShapeAdapter.notifyDataSetChanged();
    }

    /**
     * 美妆联动美型相关
     *
     * @param shapes
     */
    private void updateShape(List<BeautyFxArgs> shapes) {
        if (shapes == null || mShapeDataList == null) {
            return;
        }
        //todo 美型联动
//        for (BeautyShapeDataItem beautyShapeDataItem : mShapeDataList) {
//            //先恢复
//            beautyShapeDataItem.setCanReplace(true);
//            //是否是美型包
//            if (beautyShapeDataItem.isPackageShapeFlag()) {
//                for (BeautyFxArgs shape : shapes) {
//                    if (shape == null) {
//                        continue;
//                    }
//                    if ((!TextUtils.isEmpty(beautyShapeDataItem.getFaceMeshPath())
//                            && TextUtils.equals(beautyShapeDataItem.getFaceMeshId(), shape.getUuid()))
//                            || (!TextUtils.isEmpty(beautyShapeDataItem.getWarpPath())
//                            && TextUtils.equals(beautyShapeDataItem.getWarpId(), shape.getUuid()))) {
//                        beautyShapeDataItem.setCanReplace(shape.isCanReplace());
//                        beautyShapeDataItem.getStrength() = NumberUtils.parseString2Float(shape.getValue());
//                        break;
//                    }
//                }
//            }
//        }
//        mShapeAdapter.notifyDataSetChanged();
    }


    /**
     * 美妆联动美颜相关
     *
     * @param beautyFxArgs
     */
    private void updateBeauty(List<BeautyFxArgs> beautyFxArgs) {
        if (beautyFxArgs == null) {
            return;
        }
        for (BeautyFxArgs beautyFxArg : beautyFxArgs) {
            String className = beautyFxArg.getClassName();
            String value = beautyFxArg.getValue();
            //锐度
            if (TextUtils.equals(className, "Default Sharpen Enabled")) {
                mSharpenSwitch.setChecked(("1".equals(value)));
            } else if (TextUtils.equals(className, "Default Beauty Enabled")) {
                Log.d("=====setMakeup", className + " setBooleanVal :" + ("1".equals(value)));
            } else {
                //美白A还是美白B 已经联动
                //todo 美颜联动

            }
        }

    }

    /**
     * 清除美妆中添加的美颜 美型 滤镜特效
     */
    private void clearMakeupFx() {
        //滤镜
        Set<String> fxSet = MakeupManager.getInstacne().getFilterFxSet();
        if (fxSet != null && fxSet.size() > 0) {
            for (String fxName : fxSet) {
                int captureVideoFxCount = mStreamingContext.getCaptureVideoFxCount();
                for (int i = 0; i < captureVideoFxCount; i++) {
                    NvsCaptureVideoFx captureVideoFxByIndex = mStreamingContext.getCaptureVideoFxByIndex(i);
                    if (captureVideoFxByIndex == null) {
                        continue;
                    }
                    String captureVideoFxPackageId = captureVideoFxByIndex.getCaptureVideoFxPackageId();
                    if (!TextUtils.isEmpty(fxName) && fxName.equals(captureVideoFxPackageId)) {
                        mStreamingContext.removeCaptureVideoFx(i);
                    }
                }
            }
        }
        MakeupManager.getInstacne().clearFilterData();
        //美妆里边的美颜美型
        HashMap<String, String> mapFxMap = MakeupManager.getInstacne().getMapFxMap();
        if (mapFxMap != null && !mapFxMap.isEmpty()) {
            Set<String> strings = mapFxMap.keySet();
            for (String fxName : strings) {
                String value = mapFxMap.get(fxName);

                if (TextUtils.equals(fxName, "Default Beauty Enabled") || TextUtils.equals(fxName, "Default Sharpen Enabled")) {
                    mArSceneFaceEffect.setBooleanVal(fxName, false);
                } else {
                    //json 判断是美白A还是美白B
                    if (TextUtils.equals(fxName, "Beauty Whitening")) {
                        fxName = "Beauty Whitening";
                    }
                    if (!TextUtils.isEmpty(fxName) && mArSceneFaceEffect != null) {
                        if (fxName.startsWith("Face") || fxName.startsWith("Warp")) {
                            mArSceneFaceEffect.setStringVal(fxName, null);
                        } else {
                            mArSceneFaceEffect.setFloatVal(fxName, (mShapeIdList.contains(fxName))
                                    ? -Float.parseFloat(value) : 0);
                        }
                    }
                }
            }
        }
        MakeupManager.getInstacne().clearMapFxData();
    }

    //清理所有的单装
    private void clearAllCustomMakeup() {
        MakeupManager.getInstacne().clearCustomData();
        ArrayList<CategoryInfo> mAllMakeupId = mMakeUpView.getAllMakeupId();
        for (CategoryInfo categoryInfo : mAllMakeupId) {
            if (categoryInfo.getMaterialType() == 21) {
                continue;
            }
            resetCustomMakeup(Util.upperCaseName(categoryInfo.getDisplayName()));
        }
    }

    /**
     * 应用单状
     *
     * @param makupId
     */
    public void resetCustomMakeup(String makupId) {
        if ((mArSceneFaceEffect == null) || TextUtils.isEmpty(makupId)) {
            return;
        }
        mArSceneFaceEffect.setStringVal("Makeup " + makupId + " Package Id", null);
        mArSceneFaceEffect.setColorVal("Makeup " + makupId + " Color", new NvsColor(0, 0, 0, 0));
        mArSceneFaceEffect.setFloatVal("Makeup " + makupId + " Intensity", MakeUpView.DEFAULT_MAKEUP_INTENSITY);
        Log.d(TAG, "resetCustomMakeup setNull " + makupId);
    }

    private void setMakeupBeautyArgs(List<BeautyFxArgs> shape, boolean microFlag, boolean clearFlag) {
        if ((shape != null) && (shape.size() > 0)) {
            for (BeautyFxArgs beautyFxArgs : shape) {
                if (beautyFxArgs == null) {
                    continue;
                }
                String className = beautyFxArgs.getClassName();
                String value = beautyFxArgs.getValue();
                Float floatValue = clearFlag ? 0 : NumberUtils.parseString2Float(value);
                //Default Beauty Enabled   默认美颜Lut开启（美颜）
                //Default Sharpen Enabled
                if (TextUtils.equals(className, "Default Beauty Enabled") || TextUtils.equals(className, "Default Sharpen Enabled")) {
                    if ("1".equals(value)) {
                        mArSceneFaceEffect.setBooleanVal(className, true);
                    } else {
                        mArSceneFaceEffect.setBooleanVal(className, false);
                    }
                    Log.d("=====setMakeup", beautyFxArgs.getClassName() + " setBooleanVal :" + ("1".equals(value)));
                } else {
                    //json 判断是美白A还是美白B
                    if (TextUtils.equals(className, "Beauty Whitening")) {
                        changeBeautyWhiteMode(mArSceneFaceEffect, beautyFxArgs.getWhiteningLutEnabled() <= 0, false);
                    }
                    if (mArSceneFaceEffect != null) {
                        if (!TextUtils.isEmpty(beautyFxArgs.getDegreeName())) {
                            mArSceneFaceEffect.setStringVal(beautyFxArgs.getClassName(), beautyFxArgs.getUuid());
                            mArSceneFaceEffect.setFloatVal(beautyFxArgs.getDegreeName(), floatValue);
//                            MakeupManager.getInstacne().putMapFx(beautyFxArgs.getDegreeName(), value);
                            Log.d("=====setMakeup|||", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid() + " |" + beautyFxArgs.getDegreeName() + " :" + floatValue);
                        } else {
                            if (microFlag && !TextUtils.isEmpty(beautyFxArgs.getUuid())) {
                                mArSceneFaceEffect.setStringVal(className, beautyFxArgs.getUuid());
                                Log.d("=====setMakeup", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid());
                            } else {
                                if (TextUtils.isEmpty(className)) {
                                    mArSceneFaceEffect.setBooleanVal("Advanced Beauty Enable", beautyFxArgs.getAdvancedBeautyEnable() == 1);
                                    mArSceneFaceEffect.setIntVal("Advanced Beauty Type", beautyFxArgs.getAdvancedBeautyType());
                                    Log.d("=====setMakeup", "Advanced Beauty Enable:" + beautyFxArgs.getAdvancedBeautyEnable() + " :Advanced Beauty Type " + beautyFxArgs.getAdvancedBeautyType());
                                } else {
                                    mArSceneFaceEffect.setFloatVal(className, (mShapeIdList.contains(className))
                                            ? -floatValue : floatValue);
                                    Log.d("=====setMakeup", beautyFxArgs.getClassName() + " :" + ((mShapeIdList.contains(className))
                                            ? -floatValue : floatValue));
                                }
                            }
                        }
                    }
                }
                MakeupManager.getInstacne().putMapFx(className, value);
            }
        }
    }


    private void stopRecording() {
        mStreamingContext.stopRecording();
        // mStartRecordingImage.setBackgroundResource(R.mipmap.capture_recording_stop);
        /*
         * 拍视频
         * Take a video
         * */
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            Observable.just(1).delay(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    mAllRecordingTime += mEachRecodingVideoTime;
                    mRecordTimeList.add(mEachRecodingVideoTime);
                    mStartText.setText(mRecordTimeList.size() + "");
                    changeRecordDisplay(RECORD_FINISH, false);
                }
            });
        } else {
            changeRecordDisplay(RECORD_FINISH, true);
        }
    }

    @MSSubscribe(Constants.SubscribeType.SUB_REMO_ALL_FILTER_TYPE)
    private void removeAllFilterFx() {
        List<Integer> remove_list = new ArrayList<>();
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (fx == null) {
                continue;
            }
            if (fx.getAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY) != null) {
                boolean isBgSet = (boolean) fx.getAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY);
                if (isBgSet) {
                    continue;
                }
            }
            String name = fx.getBuiltinCaptureVideoFxName();
            String packageId = fx.getCaptureVideoFxPackageId();
            if (!"Beauty".equals(name) && !"Face Effect".equals(name) && !"AR Scene".equals(name)) {
                if (TextUtils.isEmpty(packageId) && !TextUtils.isEmpty(name)) {
                    remove_list.add(i);
                } else {
                    boolean remove = true;
                    for (FilterArgs filterArgs : tempFilterList) {
                        if (TextUtils.equals(packageId, filterArgs.getUuid())) {
                            remove = filterArgs.isCanReplace();
                        }
                    }
                    if (remove) {
                        remove_list.add(i);
                    }
                }
            }
        }
        if (!remove_list.isEmpty()) {
            //这里倒着删，否则会出现移除错误的问题。
            for (int i = remove_list.size() - 1; i >= 0; i--) {
                mStreamingContext.removeCaptureVideoFx(remove_list.get(i));
            }
        }
    }

    private boolean removeFilterFxByName(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getDescription().getName();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }

    private boolean removeFilterFxById(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getCaptureVideoFxPackageId();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(Dialog dialog, View view) {
        showCaptureDialogView(dialog, view, false);
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(Dialog dialog, View view, boolean matchParent) {
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        /*
         * 动画时间500毫秒
         *The animation time is 500 ms
         * */
        translate.setDuration(200);
        translate.setFillAfter(false);
        //mStartLayout.startAnimation(translate);
        dialog.show();
        if (view != null) {
            dialog.setContentView(view);
        }
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (matchParent) {
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
        //  isShowCaptureButton(false);
    }

    /**
     * 关闭对话框窗口
     * Close dialog window
     */
    private void closeCaptureDialogView(Dialog dialog) {
        dialog.dismiss();
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        /*
         * 动画时间300毫秒
         *The animation time is 300 ms
         * */
        translate.setDuration(300);
        translate.setFillAfter(false);
    }


    /**
     * 改动拍摄屏幕比例
     * Change the capture screen ratio
     **/
    private void changeAspectRatio() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int ratioHeight = dm.widthPixels / 9 * 16;
        // Log.d("lhz",  "ratioHeight=" + ratioHeight + "**screenHeight=" + screenHeight);
        if (ratioHeight < screenHeight) {
            RelativeLayout.LayoutParams lvWindowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lvWindowParams.height = ratioHeight;

            int bottomHeight = ScreenUtils.dip2px(this, 161);
            int largeHeight = screenHeight - ratioHeight;

            //Log.d("lhz", "largeHeight=" + largeHeight + "**bottomHeight=" + bottomHeight+"**");
            if (largeHeight > bottomHeight) {
                mFlBottomParent.setBackgroundColor(getResources().getColor(R.color.white));
                mTvChoosePicture.setTextColor(getResources().getColor(R.color.black));
                mTvChooseVideo.setTextColor(getResources().getColor(R.color.black));
                mIvMakeup.setImageResource(R.mipmap.capture_makeup_black);
                mTvMakeup.setTextColor(getResources().getColor(R.color.black));

                mIvBauty.setImageResource(R.mipmap.capture_beauty_black);
                mTvBeauty.setTextColor(getResources().getColor(R.color.black));
                needChangeMoreFxDialogPropsColor = true;
                mIvFilter.setImageResource(R.mipmap.capture_filter_black);
                mTvFilter.setTextColor(getResources().getColor(R.color.black));
            } else {
                RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                int tempHeight = bottomHeight - largeHeight;
                if (tempHeight < 10) {
                    //如果底部固定的高度比剩余的高度略低，底部布局高度降低并设置白色背景
                    //If the bottom fixed height is slightly lower than the remaining height, lower the bottom layout height and set the white background
                    mFlBottomParent.setBackgroundColor(getResources().getColor(R.color.white));
                    mTvChoosePicture.setTextColor(getResources().getColor(R.color.black));
                    mTvChooseVideo.setTextColor(getResources().getColor(R.color.black));
                    bottomParams.height = bottomHeight - tempHeight;
                    mIvMakeup.setImageResource(R.mipmap.capture_makeup_black);
                    mTvMakeup.setTextColor(getResources().getColor(R.color.black));

                    mIvBauty.setImageResource(R.mipmap.capture_beauty_black);
                    mTvBeauty.setTextColor(getResources().getColor(R.color.black));
                    needChangeMoreFxDialogPropsColor = true;
                    mIvFilter.setImageResource(R.mipmap.capture_filter_black);
                    mTvFilter.setTextColor(getResources().getColor(R.color.black));
                } else {
                    RelativeLayout.LayoutParams middleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    middleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    int maxTopMargin = ScreenUtils.dip2px(this, 20);
                    bottomParams.height = bottomHeight;
                    if (maxTopMargin >= largeHeight / 3) {
                        //顶部最多往下移动maxTopMargin，为了不遮盖上部的返回、切换摄像头图标
                        middleParams.bottomMargin = largeHeight / 3 * 2 + ScreenUtils.dip2px(this, 159);
                        bottomParams.bottomMargin = largeHeight / 3 * 2;
                        lvWindowParams.topMargin = largeHeight / 3;
                    } else {
                        middleParams.bottomMargin = largeHeight - maxTopMargin + ScreenUtils.dip2px(this, 159);
                        bottomParams.bottomMargin = largeHeight - maxTopMargin;
                        lvWindowParams.topMargin = maxTopMargin;
                    }
                    mFlMiddleParent.setLayoutParams(middleParams);
                }
                mFlBottomParent.setLayoutParams(bottomParams);
            }
            mLiveWindow.setLayoutParams(lvWindowParams);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(drawRect.getLayoutParams());
            if (params != null) {
                params.addRule(RelativeLayout.ALIGN_TOP, Util.isHUAWEI4A() ? R.id.lw_window : R.id.lw_windowExt);
                params.addRule(RelativeLayout.ALIGN_BOTTOM, Util.isHUAWEI4A() ? R.id.lw_window : R.id.lw_windowExt);
                drawRect.setLayoutParams(params);
            }
        }
    }

    private void initCaptionFontInfoList() {
        String fontJsonPath = "font/info.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        ArrayList<FontInfo> fontInfoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoList == null) {
            return;
        }
        int fontCount = fontInfoList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            FontInfo fontInfo = fontInfoList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
            mStreamingContext.registerFontByFilePath(fontAssetPath);
        }

        //Sd
//        String fontJsonPathSD = "storage/NvStreamingSdk/Asset/Font/info.json";
        String fontJsonPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/info.json";
        if (AndroidOS.USE_SCOPED_STORAGE) {
            fontJsonPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/info.json";
        }
        String fontJsonTextSD = ParseJsonFile.readSDJsonFile(this, fontJsonPathSD);
        if (TextUtils.isEmpty(fontJsonTextSD)) {
            return;
        }
        ArrayList<FontInfo> fontInfoListSD = ParseJsonFile.fromJson(fontJsonTextSD, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (fontInfoListSD == null) {
            return;
        }
        int fontCountSD = fontInfoListSD.size();
        for (int idx = 0; idx < fontCountSD; idx++) {
            FontInfo fontInfo = fontInfoListSD.get(idx);
            if (fontInfo == null) {
                continue;
            }

            String fontAssetPathSD = Environment.getExternalStorageDirectory() + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
            if (AndroidOS.USE_SCOPED_STORAGE) {
                fontAssetPathSD = getExternalFilesDir("") + "/NvStreamingSdk/Asset/Font/" + fontInfo.getFontFileName();
            }
            mStreamingContext.registerFontByFilePath(fontAssetPathSD);
        }
    }

    private void initCaptureData() {
        mStreamingContext.removeAllCaptureVideoFx();
        mFocusAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFocusAnimation.setDuration(1000);
        mFocusAnimation.setFillAfter(true);
        mCanUseARFaceType = NvsStreamingContext.hasARModule();
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        /*
         *给Streaming Context设置回调接口
         *Set callback interface for Streaming Context
         * */
        setStreamingCallback(false);
        if (mStreamingContext.getCaptureDeviceCount() == 0) {
            return;
        }

        /*
         * 将采集预览输出连接到LiveWindow控件
         * Connect the capture preview output to the LiveWindow control
         * */
        if (mLiveWindow instanceof MSLiveWindow) {
            if (!mStreamingContext.connectCapturePreviewWithLiveWindow((MSLiveWindow) mLiveWindow)) {
                Log.e(TAG, "Failed to connect capture preview with livewindow!");
                return;
            }

        } else if (mLiveWindow instanceof MSLiveWindowExt) {
            if (!mStreamingContext.connectCapturePreviewWithLiveWindowExt((MSLiveWindowExt) mLiveWindow)) {
                Log.e(TAG, "Failed to connect capture preview with livewindow!");
                return;
            }
        } else {
            return;
        }

        mCurrentDeviceIndex = 0;
        /*
         * 采集设备数量判定
         * Judging the count of collection equipment
         * */
        if (mStreamingContext.getCaptureDeviceCount() > 1) {
            mIvChangeCamera.setEnabled(true);
        } else {
            mIvChangeCamera.setEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            try {
                startCapturePreview(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "startCapturePreviewException: initCapture failed,under 6.0 device may has no access to camera");
                PermissionDialog.noPermissionDialog(CaptureActivity.this);
                setCaptureViewEnable(false);
            }
        }
        setCaptureViewEnable(true);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                initArScene = bundle.getBoolean("initArScene");
            }
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        mStartPreviewTime = System.currentTimeMillis();
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         * */
        int captureResolutionGrade = ParameterSettingValues.instance().getCaptureResolutionGrade();
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            m_supportAutoFocus = false;
            changeSegmentModel();
            if (!mStreamingContext.startCapturePreview(mCurrentDeviceIndex, captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_ENABLE_TAKE_PICTURE |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE, null)) {
                Log.e(TAG, "Failed to start capture preview!");
                return false;
            }
        }
        return true;
    }

    private void changeSegmentModel() {
        if (mBgSegEffect == null) {
            return;
        }
        if (mCurrentDeviceIndex == 0) {
            ToastUtil.showToast(mContext, getResources().getString(R.string.segment_whole_body_model));
            mBgSegEffect.setMenuVal(Constants.KEY_SEGMENT_TYPE, Constants.SEGMENT_TYPE_BACKGROUND);
        } else if (mCurrentDeviceIndex == 1) {
            ToastUtil.showToast(mContext, getResources().getString(R.string.segment_half_body_model));
            mBgSegEffect.setMenuVal(Constants.KEY_SEGMENT_TYPE, Constants.SEGMENT_TYPE_HALF_BODY);
        }
    }


    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    private void updateSettingsWithCapability(int deviceIndex) {
        /*
         * 获取采集设备能力描述对象，设置自动聚焦，曝光补偿，缩放
         * Get acquisition device capability description object, set auto focus, exposure compensation, zoom
         * */
        mCapability = mStreamingContext.getCaptureDeviceCapability(deviceIndex);
        if (null == mCapability) {
            return;
        }
        m_supportAutoFocus = mCapability.supportAutoFocus;
        if (mMoreDialog == null) {
            initTopMoreView();
        }
        mMoreDialog.checkCapability(mCapability);
    }

    private final int RECORD_DEFAULT = 0;
    private final int RECORDING = 1;
    private final int RECORD_FINISH = 2;

    private void changeRecordDisplay(int recordState, boolean isPicture) {
        //Log.d("lhz", "recordState=" + recordState);
        if (RECORD_DEFAULT == recordState) {
            //默认显示
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mTvVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            mIvVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            if (isPicture) {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);  //视频类型拍摄按钮背景
            }
            mStartText.setVisibility(View.INVISIBLE);

            mLlMakeupLayout.setVisibility(View.VISIBLE);
            mBeautyLayout.setVisibility(View.VISIBLE);
            mFilterLayout.setVisibility(View.VISIBLE);
            mLlProps.setVisibility(View.VISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        } else if (RECORDING == recordState) {
            //拍摄中
            mIvExit.setVisibility(View.GONE);
            mIvChangeCamera.setVisibility(View.GONE);
            mLlRightContainer.setVisibility(View.GONE);
            mIvMore.setVisibility(View.GONE);
            mTvVoice.setVisibility(View.GONE);
            mIvVoice.setVisibility(View.GONE);
            if (isPicture) {
                mVideoTimeDot.setVisibility(View.INVISIBLE);
                mRecordTime.setVisibility(View.INVISIBLE);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_stop_video);
                if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                    mFlMiddleParent.setVisibility(View.VISIBLE);
                }
                mVideoTimeDot.setVisibility(View.VISIBLE);
                mRecordTime.setVisibility(View.VISIBLE);
            }
            mStartText.setVisibility(View.INVISIBLE);

            mLlMakeupLayout.setVisibility(View.INVISIBLE);
            mBeautyLayout.setVisibility(View.INVISIBLE);
            mFilterLayout.setVisibility(View.INVISIBLE);
            mLlProps.setVisibility(View.INVISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.INVISIBLE);
            mTvChooseVideo.setVisibility(View.INVISIBLE);
        } else if (RECORD_FINISH == recordState) {
            //拍摄完毕
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mLlRightContainer.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mTvVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            mIvVoice.setVisibility(isPicture ? View.GONE : View.VISIBLE);
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mStartText.setVisibility(View.VISIBLE);

            mLlMakeupLayout.setVisibility(View.VISIBLE);
            mBeautyLayout.setVisibility(View.VISIBLE);
            mFilterLayout.setVisibility(View.VISIBLE);
            mLlProps.setVisibility(View.VISIBLE);

            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mDelete.setVisibility(View.VISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mRecordTime.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.VISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        }
        if (mRecordTimeList.isEmpty()) {
            mRecordTime.setVisibility(View.INVISIBLE);
        }
    }


    private ArrayList<NvAsset> getLocalData(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCaptureDeviceCapsReady(int captureDeviceIndex) {
        if (captureDeviceIndex != mCurrentDeviceIndex) {
            return;
        }
        updateSettingsWithCapability(captureDeviceIndex);
    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {
    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        mIsSwitchingCamera = false;
    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {
        Log.e(TAG, "onCaptureDeviceError: initCapture failed,under 6.0 device may has no access to camera");
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
        setCaptureViewEnable(false);
    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        /*
         *  保存到媒体库
         * Save to media library
         * */
        if (mRecordFileList != null && !mRecordFileList.isEmpty()) {
            for (String path : mRecordFileList) {
                if (path == null) {
                    continue;
                }
                if (path.endsWith(".mp4")) {
                    MediaScannerUtil.scanFile(path, "video/mp4");
                } else if (path.endsWith(".jpg")) {
                    MediaScannerUtil.scanFile(path, "image/jpg");
                }
            }
        }
    }

    @Override
    public void onCaptureRecordingError(int i) {

    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        /*
         * 拍视频or照片
         * Take a video or a photo
         * */
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            if (l >= Constants.MIN_RECORD_DURATION) {
                mFlStartRecord.setEnabled(true);
            }
            mEachRecodingVideoTime = l;
            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mRecordTime.setVisibility(View.VISIBLE);
            String totalTime = TimeFormatUtil.formatUsToString2(mAllRecordingTime + mEachRecodingVideoTime);
            mRecordTime.setText(totalTime);
        } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
            if (l > 40000) {
                stopRecording();
                takePhoto(l);
            }
        }
    }

    @Override
    public void onCaptureRecordingStarted(int i) {

    }

    @Override
    public void onCapturedPictureArrived(final ByteBuffer byteBuffer, NvsVideoFrameInfo
            nvsVideoFrameInfo) {
        runOnUiThread(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                NvsVideoResolution mCurrentVideoResolution = new NvsVideoResolution();
                mCurrentVideoResolution.imageWidth = 720;
                mCurrentVideoResolution.imageHeight = 1280;
                mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);

                int makeRatio = NvAsset.AspectRatio_9v16;
                NvsVideoResolution videoEditResolution = Util.getVideoEditResolution(makeRatio);

                int captureVideoFxCount = mStreamingContext.getCaptureVideoFxCount();

                List<NvsEffect> effects = new ArrayList<>();
                NvsRational nvsRational = new NvsRational(9, 16);


                for (int i = 0; i < captureVideoFxCount; i++) {
                    /**
                     * 道具
                     */
                    NvsCaptureVideoFx captureVideoFxByIndex = mStreamingContext.getCaptureVideoFxByIndex(i);
                    if (captureVideoFxByIndex.getBuiltinCaptureVideoFxName().equals(Constants.AR_SCENE)) {
                        String stringVal = captureVideoFxByIndex.getStringVal(Constants.AR_SCENE_ID_KEY);
                        NvsVideoEffect videoEffect = mNvsEffectSdkContext.createVideoEffect(Constants.AR_SCENE, nvsRational);

                        if (videoEffect != null) {
                            if (BuildConfig.FACE_MODEL == 240) {
                                videoEffect.setBooleanVal("Use Face Extra Info", true);
                            }
                            NvsARSceneManipulate arSceneManipulate = videoEffect.getARSceneManipulate();
                            //支持的人脸个数，是否需要使用最小的设置
                            videoEffect.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);

                            //美颜开关
                            videoEffect.setBooleanVal("Beauty Effect", true);
                            //美型开关
                            videoEffect.setBooleanVal("Beauty Shape", true);
                            //美型开关
                            videoEffect.setBooleanVal("Face Mesh Internal Enabled", true);
                            //高级美颜开关
                            videoEffect.setBooleanVal("Advanced Beauty Enable", true);

                            arSceneManipulate.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
                            arSceneManipulate.setDetectionAutoProbe(true);

                            if (!TextUtils.isEmpty(stringVal)) {
                                videoEffect.setStringVal(Constants.AR_SCENE_ID_KEY, stringVal);
                            }

                            double advanced_beauty_intensity = captureVideoFxByIndex.getFloatVal("Advanced Beauty Intensity");
                            int advanced_beauty_type = captureVideoFxByIndex.getIntVal("Advanced Beauty Type");
                            double beautyWhitening = captureVideoFxByIndex.getFloatVal("Beauty Whitening");
                            double beautyRedding = captureVideoFxByIndex.getFloatVal("Beauty Reddening");
                            double beautyStrength = captureVideoFxByIndex.getFloatVal("Beauty Strength");

                            videoEffect.setIntVal("Advanced Beauty Type", advanced_beauty_type);
                            videoEffect.setFloatVal("Advanced Beauty Intensity", advanced_beauty_intensity);
                            videoEffect.setFloatVal("Beauty Whitening", beautyWhitening);
                            videoEffect.setFloatVal("Beauty Reddening", beautyRedding);
                            videoEffect.setFloatVal("Beauty Strength", beautyStrength);

                            if (mBeautySkinList != null && mBeautySkinList.size() > 0) {
                                for (ArBean beautyShapeDataItem : mBeautySkinList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getArId();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeId);
                                    videoEffect.setFloatVal(beautyShapeId, floatVal);
                                }
                            }

                            if (mSmallShapeDataList != null && mSmallShapeDataList.size() > 0) {
                                for (ArBean beautyShapeDataItem : mSmallShapeDataList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getArId();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeDataItem.getArId());
                                    videoEffect.setFloatVal(beautyShapeDataItem.getArId(), floatVal);
                                }
                            }

                            if (mShapeDataList != null && mShapeDataList.size() > 0) {
                                for (ArBean beautyShapeDataItem : mShapeDataList) {
                                    if (beautyShapeDataItem == null) {
                                        continue;
                                    }
                                    String beautyShapeId = beautyShapeDataItem.getArId();
                                    if (TextUtils.isEmpty(beautyShapeId)) {
                                        continue;
                                    }
                                    double floatVal = captureVideoFxByIndex.getFloatVal(beautyShapeDataItem.getArId());
                                    videoEffect.setFloatVal(beautyShapeDataItem.getArId(), floatVal);
                                }
                            }

                            effects.add(0, videoEffect);
                        }

                    }

                    /**
                     * 滤镜
                     */
                    String captureVideoFxPackageId = captureVideoFxByIndex.getCaptureVideoFxPackageId();
                    float filterIntensity = captureVideoFxByIndex.getFilterIntensity();
                    if (!TextUtils.isEmpty(captureVideoFxPackageId)) {
                        NvsVideoEffect videoEffect = mNvsEffectSdkContext.createVideoEffect(captureVideoFxPackageId, nvsRational);
                        videoEffect.setFilterIntensity(filterIntensity);
                        effects.add(videoEffect);
                    }
                }


                /**
                 * 组合字幕
                 */
                int captureCompoundCaptionCount = mStreamingContext.getCaptureCompoundCaptionCount();
                for (int i = 0; i < captureCompoundCaptionCount; i++) {
                    NvsCaptureCompoundCaption captureCompoundCaption = mStreamingContext.getCaptureCompoundCaptionByIndex(i);

                    NvsVideoEffectCompoundCaption compoundCaptionFilter = mNvsEffectSdkContext.
                            createCompoundCaption(0, Long.MAX_VALUE, captureCompoundCaption.
                                    getCaptionStylePackageId(), nvsRational);

                    int captionCount = captureCompoundCaption.getCaptionCount();
                    for (int j = 0; j < captionCount; j++) {
                        String text = captureCompoundCaption.getText(j);
                        String fontFamily = captureCompoundCaption.getFontFamily(j);
                        NvsColor textColor = captureCompoundCaption.getTextColor(j);

                        compoundCaptionFilter.setTextColor(j, textColor);
                        compoundCaptionFilter.setFontFamily(j, fontFamily);
                        compoundCaptionFilter.setText(j, text);
                    }

                    PointF captionTranslation = captureCompoundCaption.getCaptionTranslation();
                    float scaleX = captureCompoundCaption.getScaleX();
                    float scaleY = captureCompoundCaption.getScaleY();
                    float rotationZ = captureCompoundCaption.getRotationZ();
                    float opacity = captureCompoundCaption.getOpacity();

                    compoundCaptionFilter.setScaleX(scaleX);
                    compoundCaptionFilter.setScaleY(scaleY);
                    compoundCaptionFilter.setRotationZ(rotationZ);
                    compoundCaptionFilter.setOpacity(opacity);
                    compoundCaptionFilter.setCaptionTranslation(captionTranslation);

                    if (compoundCaptionFilter != null) {
                        effects.add(compoundCaptionFilter);
                    }
                }

                /**
                 * 贴纸
                 */
                int captureAnimatedStickerCount = mStreamingContext.getCaptureAnimatedStickerCount();
                for (int i = 0; i < captureAnimatedStickerCount; i++) {
                    NvsCaptureAnimatedSticker captureAnimatedStickerByIndex = mStreamingContext.
                            getCaptureAnimatedStickerByIndex(i);
                    PointF translation = captureAnimatedStickerByIndex.getTranslation();
                    String animatedStickerPackageId = captureAnimatedStickerByIndex.getAnimatedStickerPackageId();
                    if (!TextUtils.isEmpty(animatedStickerPackageId)) {
                        NvsVideoEffectAnimatedSticker stickerFilter = mNvsEffectSdkContext.
                                createAnimatedSticker(0, STICK_TIME_DURATION /*Long.MAX_VALUE*/, false,
                                        animatedStickerPackageId, nvsRational);
                        stickerFilter.setScale(0.5f);
                        double v = translation.x * 0.5;
                        double v1 = translation.y * 0.5;
                        translation.x = (float) v;
                        translation.y = (float) v1;
                        stickerFilter.translateAnimatedSticker(translation);
                        if (stickerFilter != null) {
                            effects.add(stickerFilter);
                        }
                    }

                }

                Observable.just(effects).map(nvsEffects -> {
                    Bitmap pictureBitmap = Bitmap.createBitmap(nvsVideoFrameInfo.frameWidth,
                            nvsVideoFrameInfo.frameHeight, Bitmap.Config.ARGB_8888);
                    if (effects.size() > 0) {
                        mEffectRenderCore.initialize(NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K |
                                NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_CREATE_GLCONTEXT_IF_NEED);
                        ByteBuffer byteBufferResult = mEffectRenderCore.renderEffects(effects.toArray(new NvsEffect[effects.size()]),
                                byteBuffer.array(), nvsVideoFrameInfo, 0,
                                NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, false,
                                (System.currentTimeMillis() - mStartPreviewTime) * 1000, 0);

                        for (int i = 0; i < effects.size(); i++) {
                            NvsEffect nvsEffect = effects.get(i);
                            mEffectRenderCore.clearEffectResources(nvsEffect);
                            nvsEffect = null;
                        }
                        effects.clear();
                        mEffectRenderCore.clearCacheResources();
                        mEffectRenderCore.cleanUp();
                        mEffectRenderCore.release();
                        if (null != byteBufferResult) {
                            pictureBitmap.copyPixelsFromBuffer(byteBufferResult);
                            byteBufferResult.clear();
                            byteBuffer.clear();
                        } else {
                            pictureBitmap.copyPixelsFromBuffer(byteBuffer);
                        }
                    } else {
                        pictureBitmap.copyPixelsFromBuffer(byteBuffer);
                    }
                    return pictureBitmap;
                }).observeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        mPictureBitmap = bitmap;
                        if (mPictureBitmap != null) {
                            mPictureImage.setImageBitmap(mPictureBitmap);
                            showPictureLayout(true);
                        } else {
                            changeRecordDisplay(RECORD_DEFAULT, true);
                        }
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable.getMessage());
                    }
                }).subscribe();

            }
        });
    }


    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
            bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUEST_FILTER_LIST_CODE) {
                    initFilterList();
                    mFilterBottomView.setFilterArrayList(mFilterDataArrayList);
                    mFilterSelPos = AssetFxUtil.getSelectedFilterPos(mFilterDataArrayList, mVideoClipFxInfo);
                    mFilterBottomView.setSelectedPos(mFilterSelPos);
                    mFilterBottomView.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_BACKGROUND_SEG) {
                    String clickType = data.getStringExtra(SINGLE_PICTURE_CLICK_TYPE);
                    setBgSeg(data, clickType);
                }
                break;
            default:
                break;
        }
    }

    private void setBgSeg(Intent intent, String clickType) {
        if (SINGLE_PICTURE_CLICK_CANCEL.equals(clickType)) {
            if (mBgSegEffect != null) {
                int index = mBgSegEffect.getIndex();
                mStreamingContext.removeCaptureVideoFx(index);
                mBgSegEffect = null;
            }
        } else if (SINGLE_PICTURE_CLICK_CONFIRM.equals(clickType)) {
            if (mBgSegEffect == null) {
                mBgSegEffect = mStreamingContext.appendBuiltinCaptureVideoFx("Segmentation Background Fill");
                mBgSegEffect.setAttachment(Constants.BG_SEG_EFFECT_ATTACH_KEY, true);
            }
            if (intent != null) {
                String filePath = intent.getStringExtra(SINGLE_PICTURE_PATH);
                mBgSegEffect.setStringVal(Constants.KEY_SEGMENT_TEX_FILE_PATH, filePath);
                /*1:铺满（可能会被拉伸）  0：自适应*/
                mBgSegEffect.setIntVal(Constants.KEY_SEGMENT_STRETCH_MODE, 1);
                changeSegmentModel();
            }
        }

    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {
        /*
         * 初始化拍摄
         * Initial shooting
         * */
        startCapturePreview(false);
    }

    @Override
    protected void nonePermission() {
        Log.d(TAG, "initCapture failed,above 6.0 device may has no access to camera");
        setCaptureViewEnable(false);
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void noPromptPermission() {
        Logger.e(TAG, "initCapture failed,above 6.0 device has no access from user");
        setCaptureViewEnable(false);
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void onDestroy() {
        closeCaptureFrame();
        if (mMoreDialog != null) {
            mMoreDialog.dismiss();
        }
        mStreamingContext.removeAllCaptureCompoundCaption();
        mStreamingContext.removeAllCaptureAnimatedSticker();
        destroy();
        MakeupManager.getInstacne().clearAllData();
        MSBus.getInstance().unregister(this);
        mFxFilterPackageId = null;
        MakeupHelper.getInstance().clear();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNext != null) {
            mNext.setClickable(true);
        }
        if (mFilterBottomView != null) {
            mFilterBottomView.setMoreFilterClickable(true);
        }
        // mFaceUPropView.setMoreFaceUPropClickable(true);
        startCapturePreview(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW){
            mStreamingContext.stop();
        }
        if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
            stopRecording();
        }
        if (mMoreDialog != null) {
            mMoreDialog.setFlashEnable(false);
        }
    }

    private void destroy() {
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.stop();
            setStreamingCallback(true);
            mStreamingContext = null;
        }
        mRecordTimeList.clear();
        mRecordFileList.clear();
        mFilterDataArrayList.clear();
        mPropsList.clear();
    }

    private void takePhoto(long time) {
        if (mCurRecordVideoPath != null) {
            NvsVideoFrameRetriever videoFrameRetriever = mStreamingContext.createVideoFrameRetriever(mCurRecordVideoPath);
            if (videoFrameRetriever != null) {
                int screenHeight = ScreenUtils.getScreenHeight(this);
                screenHeight = (int) (screenHeight / 16) * 16;
                //videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight 需要传入被16整除的数字
                mPictureBitmap = videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight(time, screenHeight);
                Log.d("takePhoto", " 被16整除的height" + screenHeight + "  screen: " + ScreenUtils.getScreenWidth(this) + " " + ScreenUtils.getScreenHeight(this) + "**bitmap=" + mPictureBitmap);
                if (mPictureBitmap != null) {
                    mPictureImage.setImageBitmap(mPictureBitmap);
                    showPictureLayout(true);
                } else {
                    changeRecordDisplay(RECORD_DEFAULT, true);
                }
                videoFrameRetriever.release();
            }
        }
    }

    private void setStreamingCallback(boolean isDestroyCallback) {
        mStreamingContext.setCaptureDeviceCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingDurationCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingStartedCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCapturedPictureCallback(isDestroyCallback ? null : this);
    }

    /**
     * 拍摄照片
     */
    private void takePhoto() {
        mStreamingContext.takePicture(0);
    }

    private void selectRecordType(boolean ivPicture) {
        int[] location = new int[2];
        mFlStartRecord.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        float middleX = location[0] + mFlStartRecord.getWidth() / 2f;
        float targetX;
        if (ivPicture) {
            if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                return;
            }
            targetX = middleX;
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mRecordType = Constants.RECORD_TYPE_PICTURE;
            mIvVoice.setVisibility(View.GONE);
            mTvVoice.setVisibility(View.GONE);
        } else {
            mIvVoice.setVisibility(View.VISIBLE);
            mTvVoice.setVisibility(View.VISIBLE);
            mTvChooseVideo.getLocationInWindow(location);
            targetX = location[0] + mTvChooseVideo.getWidth() / 2f;
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);
            mRecordType = Constants.RECORD_TYPE_VIDEO;
        }
        // Log.d("lhz","middleX="+middleX+"**targetX="+targetX+"**ex="+(middleX-targetX));
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRecordTypeLayout, "translationX", middleX - targetX);
        animator.setDuration(300);
        animator.start();
    }

    private void showPictureLayout(boolean show) {
        TranslateAnimation topTranslate;
        if (show) {
            mRlPhotosLayout.setVisibility(View.INVISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mIvExit.setVisibility(View.GONE);
                    mRlPhotosLayout.setVisibility(View.VISIBLE);
                    mRlPhotosLayout.setClickable(true);
                    mRlPhotosLayout.setFocusable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            mFlStartRecord.setEnabled(true);
            mIvExit.setVisibility(View.VISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mRlPhotosLayout.setVisibility(View.GONE);
                    mRlPhotosLayout.setClickable(false);
                    mRlPhotosLayout.setFocusable(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        topTranslate.setDuration(300);
        topTranslate.setFillAfter(true);
        mRlPhotosLayout.setAnimation(topTranslate);
    }

    public void setCaptureViewEnable(boolean enable) {
        //  mBottomLayout.setEnabled(enable);
        // mBottomLayout.setClickable(enable);
        // mFunctionButtonLayout.setEnabled(enable);
        //  mFunctionButtonLayout.setClickable(enable);
        // mRecordTypeLayout.setEnabled(enable);
        // mRecordTypeLayout.setClickable(enable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getInstance().finishActivity();
    }
}
