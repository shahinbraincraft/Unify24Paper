package com.meicam.effectsdkdemo;

import static android.opengl.EGLExt.EGL_RECORDABLE_ANDROID;
import static android.opengl.GLES20.glGenTextures;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
import com.meicam.effect.sdk.NvsVideoEffectCaption;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.effect.sdk.NvsVideoEffectTransition;
import com.meicam.effectsdkdemo.data.EffectData;
import com.meicam.effectsdkdemo.data.makeup.BeautyData;
import com.meicam.effectsdkdemo.data.makeup.Makeup;
import com.meicam.effectsdkdemo.fragments.AdjustFragment;
import com.meicam.effectsdkdemo.interfaces.OnNvEffectSelectListener;
import com.meicam.effectsdkdemo.interfaces.OnSeekBarChangeListenerAbs;
import com.meicam.effectsdkdemo.media.MediaAudioEncoder;
import com.meicam.effectsdkdemo.media.MediaEncoder;
import com.meicam.effectsdkdemo.media.MediaMuxerWrapper;
import com.meicam.effectsdkdemo.media.MediaVideoEncoder;
import com.meicam.effectsdkdemo.view.AbstractCustomSeekBarListener;
import com.meicam.effectsdkdemo.view.BeautyShapeAdapter;
import com.meicam.effectsdkdemo.view.BeautyShapeDataItem;
import com.meicam.effectsdkdemo.view.CompoundCaptionListView;
import com.meicam.effectsdkdemo.view.DrawRect;
import com.meicam.effectsdkdemo.view.InputDialog;
import com.meicam.effectsdkdemo.view.MakeupAdapter;
import com.meicam.effectsdkdemo.view.StickerListView;
import com.meicam.effectsdkdemo.view.TraditionalCaptionListView;
import com.meicam.effectsdkdemo.view.TransitionListView;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.render.IMeisheRender;
import com.meishe.render.MeisheRender;
import com.meishe.render.draw.GLDrawer;
import com.meishe.render.entity.EffectRenderItem;
import com.meishe.render.entity.RenderEffectParams;
import com.meishe.render.utils.Accelerometer;
import com.meishe.render.utils.EGLHelper;
import com.meishe.render.utils.FileUtils;
import com.meishe.render.utils.GLUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author ms
 */
public class MainActivity extends AppCompatActivity implements
        SurfaceTexture.OnFrameAvailableListener,
        GLSurfaceView.Renderer {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 0;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private static final int RECORD_TYPE_PICTURE = 3001;
    private static final int RECORD_TYPE_VIDEO = 3002;

    private Context mContext;
    private LinearLayout mBeautyShapeView;
    private Button mButtonRecord;
    private Button mTypePictureBtn, mTypeVideoBtn;
    private View mTypeLeftView;
    private LinearLayout mRecordTypeLayout;
    private AlertDialog mCaptureDialog;
    private TraditionalCaptionListView mTraditionalCaptionView;
    private CompoundCaptionListView mCompoundCaptionView;
    private StickerListView mStickerListView;
    private TransitionListView mTransitionListView;
    private View mBeautyView;
    private View mMakeupView;
    private View mWhitingView;
    private SeekBar mWhiteSeekBar;
    private SeekBar mReddingSeekBar;
    private SeekBar mStrengthSeekBar;
    private RecyclerView mMakeupRecyclerView;
    private SeekBar mShapeSeekBar;
    private RecyclerView mShapeRecyclerView;
    private GLSurfaceView mGlView;
    private LinearLayout mFlashView;
    private ImageView mAutoFocusRectView;
    private LinearLayout mSwitchView;
    private LinearLayout mExposeView;
    private LinearLayout mZoomView;
    private LinearLayout mCompoundCaptionBtn;
    private LinearLayout mMakeUpLayout;
    private LinearLayout mSegLayout;
    private LinearLayout mBeautyLayout;

    private View mZoomAndExposeContainer;
    private RelativeLayout mStartLayout;
    private AlphaAnimation mFocusAnimation;
    private Button mNineToSixteenBtn;
    private Button mThreeToFourBtn;
    private DrawRect mDrawRect;
    private RelativeLayout mFragmentLayout;
    private AdjustFragment mAdjustFragment;
    private List<NvsEffect>mAdjustEffectList;
    private boolean isFragmentShow;

    private Accelerometer mAccelerometer = null;
    private NvsVideoEffect mArSceneFaceEffect;
    private NvsEffect mFilter;
    private PhotoImageProcessor mPhotoImageProcessor;
    private HandlerThread mPhotoThread;
    private Handler mPhotoHandler;
    private CameraProxy mCameraProxy;
    private SurfaceTexture mCameraPreviewTexture;
    private NvsVideoResolution mCurrentVideoResolution;
    private NvsEffectSdkContext mEffectSdkContext;
    private IMeisheRender meisheRender;
    private OrientationEventListener mOrientationEventListener;
    //美型数据
    private ArrayList<BeautyShapeDataItem> mDataList = new ArrayList<>( );
    //美妆数据
    private ArrayList<BeautyData> makeupComposeData;
    private int mRecordType = RECORD_TYPE_PICTURE;
    private int mSelectedBeautyShapePos = 0;
    private boolean initArScene = false;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean mPermissionGranted;
    private boolean mIsPreviewing = false;
    private int mOrientation;
    private int mDisplayWidth, mDisplayHeight;
    private float[] mVideoTextureTransform = new float[16];
    private FloatBuffer mTextureBuffer;
    private float[] mTextureCoords = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private HandlerThread mSurfaceAvailableThread;
    private Handler mSurfaceAvailableHandler;
    private long mTimeStamp;
    private boolean mFlipHorizontal = false;
    private final Object mGLThreadSyncObject = new Object( );
    private boolean mFrameAvailable = false;
    private String mSceneId;
    private String mFilterId;
    private String mTransformId;
    private boolean mFlashToggle = false;
    private int mDeviceOrientation = 0;
    private EGLContext mEglContext;

    private AtomicBoolean mIsCaptureAtomicBoolean = new AtomicBoolean(false);

    private int[] mCameraPreviewTextures = new int[1];

    private boolean mIsRecording = false;
    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder mVideoEncoder;
    private boolean mNeedResetEglContext = false;
    private NvsVideoEffectCaption mCurVideoEffectCaption;
    private NvsVideoEffectCompoundCaption mCurCompoundCaption;
    private NvsVideoEffectAnimatedSticker mCurAnimatedSticker;
    private NvsVideoEffectTransition mCurTranslationEffect;
    //贴纸动画时长 单位毫秒
    private static final int ANIMATION_DEFAULT_DURATION = 1200;
    //贴纸持续时长 单位 微秒
    public static final int STICKER_DURATION = 10*1000*1000;
    private String mCompanyStickerAnimationUUID;
    private String mCompanyStickerAnimationPeriodUUID;
    private String mCompanyStickerAnimationOutUUID;
    private VisibleRunnable visibleRunnable;
    private int mEditMode = 0;
    private String mCurNvsEffectUuid;
    private float mRatio = 1.00f;

    /**
     * 构建类型
     * Build type
     */
    public static final String BUILD_HUMAN_AI_TYPE_NONE = "NONE";//SDK regular version
    public static final String BUILD_HUMAN_AI_TYPE_MS_ST = "MS_ST";//SDK MS_ST
    public static final String BUILD_HUMAN_AI_TYPE_MS = "MS";//SDK MS
    public static final String BUILD_HUMAN_AI_TYPE_FU = "FaceU";//FU
    /**
     *记录已经渲染的内容。因为pause的时候会清理掉，resume恢复时这个作为数据来源
     */
    private ArrayList<EffectData> mSavedRenderEffectArray = new ArrayList<>();

    private CheckBox bufferBox;
    //是否在使用整妆
    private boolean isUserComposeMakeup = false;
    //背景分割
    private String mSegmentation = "Segmentation Background Fill";
    private NvsVideoEffect mSegBackgroundEffect;
    private boolean isSegBackground;
    private GLDrawer mGlDrawer;
    //current need to render effect list
    private CopyOnWriteArrayList<EffectRenderItem> mCurrentRenderEffectList = null;
    //need to clear cache in sdk
    private CopyOnWriteArrayList<EffectRenderItem> mClearEffectList = new CopyOnWriteArrayList<>();
    private Object mArraySyncObject = new Object();

    private OnNvEffectSelectListener mOnNvEffectSelectListener = new OnNvEffectSelectListener() {
        @Override
        public void onNvEffectSelected(String uuid, NvsEffect effect, int mode) {
            if (effect == null) {
                return;
            }
            mEditMode = mode;
            mCurNvsEffectUuid = uuid;
            switch (mode) {
                case Constants.EDIT_MODE_COMPOUND_CAPTION:
                    if(null != mCurCompoundCaption){
                        removeRenderEffect(mCurCompoundCaption.getCaptionStylePackageId());
                        mDrawRect.setDrawRect(null, 0);
                    }
                    if (effect instanceof NvsVideoEffectCompoundCaption) {
                        mCurCompoundCaption = (NvsVideoEffectCompoundCaption)effect;
                        mCurCompoundCaption.setVideoResolution(mCurrentVideoResolution);
                    }
                    break;
                case Constants.EDIT_MODE_STICKER:
                    //移除当前已有的特技
                    if(null !=  mCurAnimatedSticker){
                        removeRenderEffect(mCurAnimatedSticker.getAnimatedStickerPackageId());
                        mDrawRect.setDrawRect(null, 0);
                    }
                    if (effect instanceof NvsVideoEffectAnimatedSticker) {
                        mCurAnimatedSticker = (NvsVideoEffectAnimatedSticker)effect;
                        mCurAnimatedSticker.setVideoResolution(mCurrentVideoResolution);
                        //5D9FA998-7600-492F-9DF4-BC2FA5E869BD这个id 对应的是自定义贴纸模板包
                       /* if(!TextUtils.equals(uuid,"5D9FA998-7600-492F-9DF4-BC2FA5E869BD")){
                            //添加动画演示效果
                            //sticker add in or out or period animation
                            if(TextUtils.equals(uuid,"4DE6FFE2-B7E7-482C-A487-6CC62232448A")){
                                boolean added = mCurAnimatedSticker.applyAnimatedStickerInAnimation(mCompanyStickerAnimationUUID);
                                mCurAnimatedSticker.setAnimatedStickerInAnimationDuration(ANIMATION_DEFAULT_DURATION);

                                boolean addeded = mCurAnimatedSticker.applyAnimatedStickerOutAnimation(mCompanyStickerAnimationOutUUID);
                                mCurAnimatedSticker.setAnimatedStickerOutAnimationDuration(ANIMATION_DEFAULT_DURATION);
                            }else{
                                boolean added = mCurAnimatedSticker.applyAnimatedStickerPeriodAnimation(mCompanyStickerAnimationPeriodUUID);
                                mCurAnimatedSticker.setAnimatedStickerAnimationPeriod(ANIMATION_DEFAULT_DURATION);
                            }
                        }*/
                        if(null != visibleRunnable){
                            mDrawRect.removeCallbacks(visibleRunnable);
                        }
                        visibleRunnable = new VisibleRunnable();
                        //贴纸设置了默认时长，边框同步隐藏
                        mDrawRect.postDelayed(visibleRunnable,STICKER_DURATION/1000);
                    }
                    break;
                case Constants.EDIT_MODE_CAPTION:
                    if(null != mCurVideoEffectCaption){
                        removeRenderEffect(mCurVideoEffectCaption.getCaptionStylePackageId());
                        mDrawRect.setDrawRect(null, 0);
                    }
                    if (effect instanceof NvsVideoEffectCaption) {
                        mCurVideoEffectCaption = (NvsVideoEffectCaption)effect;
                        mCurVideoEffectCaption.setVideoResolution(mCurrentVideoResolution);
                    }
                    break;
                case Constants.EDIT_MODE_TRANSITION:
                    //移除上一个转场
                    if(!TextUtils.isEmpty(mTransformId)){
                        removeRenderEffect(mTransformId);
                    }
                    resetEffectRenderTime(mTransformId);
                    mTransformId = uuid;
                    mCurTranslationEffect = (NvsVideoEffectTransition) effect;
                    break;
            }
            addNewRenderEffect(effect, uuid);
            updateDrawRect();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        mContext = this;
        NvsStreamingContext.init(this, "assets:/effectsdkdemo.lic");
        mEffectSdkContext = NvsEffectSdkContext.init(this, "assets:/effectsdkdemo.lic", 0);
        meisheRender = new MeisheRender();
        meisheRender.init();
        initView( );
        initData( );
        checkPermission( );
        setControllerListener( );
    }

    private void initView() {
        mRecordTypeLayout = findViewById(R.id.record_type_layout);
        mTypeLeftView = findViewById(R.id.leftView);
        mTypePictureBtn = findViewById(R.id.type_picture_btn);
        mTypeVideoBtn = findViewById(R.id.type_video_btn);
        mButtonRecord = findViewById(R.id.buttonRecord);
        // 拍摄录制按钮
        mStartLayout = findViewById(R.id.start_layout);
        // 变焦以及曝光dialog
        mZoomAndExposeContainer = LayoutInflater.from(this).inflate(R.layout.zoom_and_expose_view, null);
        // 摄像头
        mSwitchView = findViewById(R.id.switch_lv);
        // 美型
        mBeautyShapeView = findViewById(R.id.beauty_shape_lv);
        // 闪光灯
        mFlashView = findViewById(R.id.flash_lv);
        // 变焦
        mZoomView = findViewById(R.id.zoom_lv);
        // 曝光
        mExposeView = findViewById(R.id.expose_lv);
        // 手动对焦
        mAutoFocusRectView = findViewById(R.id.auto_focus_rect_view);
        // 画幅
        mThreeToFourBtn = findViewById(R.id.three_to_four_button);
        mDrawRect = findViewById(R.id.draw_rect);
        mNineToSixteenBtn = findViewById(R.id.nine_to_sixteen_button);
        // 组合字幕
        mCompoundCaptionBtn = findViewById(R.id.compound_caption_lv);
        mFragmentLayout = findViewById(R.id.fragment_container);
        mGlView = findViewById(R.id.GLView);
        //设置使用OpenGl ES2.0
        mGlView.setEGLContextClientVersion(2);
        mGlView.setRenderer(this);
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mAccelerometer = new Accelerometer(getApplicationContext( ));
        //美型
        mCaptureDialog = new AlertDialog.Builder(this).create( );
        mCaptureDialog.setOnCancelListener(new DialogInterface.OnCancelListener( ) {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeCaptureDialogView(mCaptureDialog);
            }
        });
        mBeautyView = LayoutInflater.from(this).inflate(R.layout.beauty_view, null);
        mShapeSeekBar = mBeautyView.findViewById(R.id.shape_sb);
        mShapeSeekBar.setMax(200);
        mShapeRecyclerView = mBeautyView.findViewById(R.id.beauty_shape_item_list);
        //美妆
        mMakeUpLayout = findViewById(R.id.markup_lv);
        mMakeupView = LayoutInflater.from(this).inflate(R.layout.makeup_view, null);
        mMakeupRecyclerView = mMakeupView.findViewById(R.id.makeup_item_list);
        //美颜
        mWhitingView =  LayoutInflater.from(this).inflate(R.layout.beauty_white_view, null);
        mWhiteSeekBar = mWhitingView.findViewById(R.id.white_sb);
        mReddingSeekBar = mWhitingView.findViewById(R.id.redding_sb);
        mStrengthSeekBar = mWhitingView.findViewById(R.id.strength_sb);
        mSegLayout = findViewById(R.id.seg_lv);
        mBeautyLayout = findViewById(R.id.beauty_lv);

        updateTypeLeftView( );
        initTraditionalCaptionView();
        initCompoundCaptionView();
        initStickerListView();
        initTransitionListView();

        bufferBox = findViewById(R.id.bufferMode);

    }

    private void initData() {
        mCameraProxy = new CameraProxy(MainActivity.this);
        initArSceneEffect( );
        // 安装资源包
        mFilterId = DataHelper.createStickerItem("assets:/1AACCE79-7EAB-4B2E-AE9B-E53A02AFC055.3.videofx", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);
        //mSceneId =  DataHelper.createStickerItem("assets:/9C917EE3-A1B0-4B5D-B50F-9624A6824A6B.arscene", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE);
        mSceneId =  DataHelper.createStickerItem("assets:/9C917EE3-A1B0-4B5D-B50F-9624A6824A6B.arscene", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE);
        mCompanyStickerAnimationPeriodUUID =  DataHelper.createStickerItem("assets:/stickeranimation/period/CD0E020E-FE12-4536-8C88-C480AF92F4B7.4.animatedstickeranimation",
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_ANIMATION);
        mCompanyStickerAnimationUUID =  DataHelper.createStickerItem("assets:/stickeranimation/in/CC2B7B7D-39D2-4432-898E-AC62C66188B8.1.animatedstickerinanimation",
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_IN_ANIMATION);
        mCompanyStickerAnimationOutUUID =  DataHelper.createStickerItem("assets:/stickeranimation/out/6B243A23-A3FB-4709-A16A-CF0E0292616F.1.animatedstickeroutanimation",
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_OUT_ANIMATION);
        mPermissionGranted = false;
        initShapeRecycleView( );
        initMakeRecycleView();
        mFocusAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFocusAnimation.setDuration(1000);
        mFocusAnimation.setFillAfter(true);

        //创建渲染图片的线程和handler
        if (mPhotoThread == null) {
            mPhotoThread = new HandlerThread("ProcessImageThread");
            mPhotoThread.start( );
            mPhotoHandler = new Handler(mPhotoThread.getLooper( )) {
                @Override
                public void handleMessage(Message msg) {
                    if(null != msg && msg.what ==1){
                        byte[] data = (byte[]) msg.obj;
                        final Bitmap srcBmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap resBmp = srcBmp;
                        if (android.os.Build.MODEL.equalsIgnoreCase("vivo X21A")) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(0.5f, 0.5f);
                            resBmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth() / 2, srcBmp.getHeight() / 2, matrix,true);
                        }
                        final Bitmap bmp = resBmp;
                        synchronized (mGLThreadSyncObject) {
                            mIsPreviewing = false;
                        }
                        /*mGlView.queueEvent(new Runnable( ) {
                            @Override
                            public void run() {

                            }
                        });*/
                        final int width = bmp.getWidth( );
                        final int height = bmp.getHeight( );

                        RenderEffectParams renderEffectParams = new RenderEffectParams()
                                .setRenderEffectList(mCurrentRenderEffectList)
                                .setClearEffectList(mClearEffectList)
                                .isOesTexture(false)
                                .setWidth(width)
                                .setHeight(height)
                                .setCurrentTimeStamp(System.currentTimeMillis() - mTimeStamp)
                                .setCameraOrientation(mCameraProxy.getOrientation())
                                .setDisplayOrientation(mOrientation)
                                .isFlipHorizontal(mFlipHorizontal)
                                .setDeviceOrientation(mDeviceOrientation)
                                .isImageMode(true)
                                .build();
                        mPhotoImageProcessor.startPhotoProcessor(renderEffectParams);
                        mPhotoImageProcessor.addRenderImageData(bmp);
                        String filePath = mPhotoImageProcessor.processEffects( );
//                        if (filePath != null) {
//                            Intent intent = new Intent(MainActivity.this, RecordFinishActivity.class);
//                            intent.putExtra("file_path", filePath);
//                            startActivity(intent);
//                        }
                        runOnUiThread(new Runnable( ) {
                            @Override
                            public void run() {
                                setCaptureEnabled(true);
                                startCapturePreview();
                            }
                        });
                    }
                }
            };
        }
    }

    private void initShapeRecycleView() {
        mDataList = DataHelper.initBeautyShapeData( );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        BeautyShapeAdapter beautyShapeAdapter = new BeautyShapeAdapter(mContext, mDataList);
        mShapeRecyclerView.setLayoutManager(linearLayoutManager);
        mShapeRecyclerView.setAdapter(beautyShapeAdapter);
        beautyShapeAdapter.setOnItemClickListener(new BeautyShapeAdapter.OnItemClickListener( ) {
            @Override
            public void onItemClick(View view, int position) {
                int dataCount = mDataList.size( );
                if (position < 0 || position >= dataCount) {
                    return;
                }
                BeautyShapeDataItem shapeDataItem = mDataList.get(position);
                if (shapeDataItem == null) {
                    return;
                }
                mSelectedBeautyShapePos = position;
                //更新item的progress

                mShapeSeekBar.setProgress(0);

                double level;
                double floatVal = mArSceneFaceEffect.getFloatVal(shapeDataItem.beautyShapeId);
                if (floatVal >= 0) {
                    level = (Math.round(floatVal * 100)) * 0.01;
                } else {
                    level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
                }
                mShapeSeekBar.setProgress((int) (level * 100 + 100));
            }
        });
        mShapeSeekBar.setOnSeekBarChangeListener(new AbstractCustomSeekBarListener( ) {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mArSceneFaceEffect != null) {
                        if (mSelectedBeautyShapePos >= 0 && mSelectedBeautyShapePos < mDataList.size( )) {
                            BeautyShapeDataItem shapeDataItem = mDataList.get(mSelectedBeautyShapePos);
                            float val = (float) (progress - 100) / 100;

                            mArSceneFaceEffect.setFloatVal(shapeDataItem.beautyShapeId, val);
                        }
                    }
                }
            }
        });
    }

    private void initMakeRecycleView() {
        makeupComposeData = DataHelper.initMakeupData(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        MakeupAdapter makeupAdapter = new MakeupAdapter(mContext, makeupComposeData);
        mMakeupRecyclerView.setLayoutManager(linearLayoutManager);
        mMakeupRecyclerView.setAdapter(makeupAdapter);
        makeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BeautyData selectItem = makeupComposeData.get(position);
                if (selectItem instanceof Makeup) {
                    Makeup item = (Makeup) selectItem;
                    if (position == 0) {
                        mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", 0);
                        if(isUserComposeMakeup){
                            mArSceneFaceEffect.setStringVal("Makeup Compound Package Id", null);
                        }else{
                            mArSceneFaceEffect.setStringVal("Makeup Eyeshadow Package Id",null);
                        }
                        return;
                    }
                    if (item.isIsCompose()) {
                        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
                        //如果上次用的是单妆，先取消上次使用的单妆
                        if(!isUserComposeMakeup){
                            mArSceneFaceEffect.setStringVal("Makeup Eyeshadow Package Id",null);
                        }
                        mArSceneFaceEffect.setFloatVal("Makeup Intensity", 1.0f);
                        mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_NONE);
                        mArSceneFaceEffect.setStringVal("Makeup Compound Package Id", item.getUuid());
                        isUserComposeMakeup = true;
                    }else{
                        //选择的是单妆
                        //如果上次用的是整，先取消
                        if(isUserComposeMakeup){
                            mArSceneFaceEffect.setStringVal("Makeup Compound Package Id", null);
                        }
                        mArSceneFaceEffect.setStringVal("Makeup Eyeshadow Package Id",item.getUuid());
                        isUserComposeMakeup = false;
                    }
                }
            }
        });
    }

    private void showCaptureDialogView(AlertDialog dialog, View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mStartLayout, "translationY", mStartLayout.getHeight( ));
        objectAnimator.setDuration(500);
        objectAnimator.start( );
        dialog.show( );
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow( ).getAttributes( );
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow( ).setGravity(Gravity.BOTTOM);
        params.dimAmount = 0.0f;
        dialog.getWindow( ).setAttributes(params);
        dialog.getWindow( ).setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow( ).setWindowAnimations(R.style.fx_dlg_style);
    }

    private void closeCaptureDialogView(AlertDialog dialog) {
        dialog.dismiss( );
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mStartLayout, "translationY", 0);
        objectAnimator.setDuration(500);
        objectAnimator.start( );
    }

    private void initTraditionalCaptionView() {
        mTraditionalCaptionView = new TraditionalCaptionListView(this);
        mTraditionalCaptionView.setmOnNvEffectSelectListener(mOnNvEffectSelectListener);
    }

    private void initCompoundCaptionView() {
        mCompoundCaptionView = new CompoundCaptionListView(this);
        mCompoundCaptionView.setmOnNvEffectSelectListener(mOnNvEffectSelectListener);
    }

    private void initStickerListView() {
        mStickerListView = new StickerListView(this);
        mStickerListView.setmOnNvEffectSelectListener(mOnNvEffectSelectListener);
    }

    private void initTransitionListView() {
        mTransitionListView = new TransitionListView(this);
        mTransitionListView.setmOnNvEffectSelectListener(mOnNvEffectSelectListener);
    }

    private void initArSceneEffect() {
        if (!initArScene) {
            String modelPath = null;
            String licensePath = null;
            String faceModelName = null;
            String className = null;
            if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
                modelPath = "/facemodel/facemodel_ms/ms_face_v1.2.2.model";
                faceModelName = "ms_face_v1.2.2.model";
                className = "facemodel/facemodel_ms";
                licensePath = "";
            } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST)) {
                modelPath = "/facemodel/facemodel_st/NvFace2Data.model";
                faceModelName = "NvFace2Data.model";
                className = "facemodel/facemodel_st";
                licensePath = "assets:/facemodel/facemodel_st/NvFace2Common.lic";
            } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_FU)) {
                modelPath = "/facemodel/facemodel_fu/fu_face_v3.model";
                faceModelName = "fu_face_v3.model";
                className = "facemodel/facemodel_fu";
                licensePath = "assets:/facemodel/facemodel_fu/fu_face_v3.license";
            }
            //初始化人脸识别
            boolean isCopy = FileUtils.copyFileIfNeed(this, faceModelName, className);
            File rootDir = getApplicationContext().getExternalFilesDir(null);
            String destModelDir = rootDir + modelPath;
            Log.d(TAG, "model path:" + destModelDir + "\n   lic path:" + licensePath);
            boolean suc = NvsEffectSdkContext.initHumanDetection(this, destModelDir,
                    licensePath,
                    NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK | NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                            NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE| NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE );
            initArScene = true;
            // 测试假脸 需要这些dat
            Log.d(TAG, " isCopy:" + isCopy + "   suc :" + suc);
            String fakefacePath = "assets:/facemodel/fakeface.dat";
            boolean fakefaceSuccess = NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakefacePath);
            Log.e(TAG, "fakefaceSuccess-->" + fakefaceSuccess);
            //美妆模型初始化
            String makeupPath = "assets:/facemodel/makeup.dat";
            boolean makeupSuccess = NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP, makeupPath);
            Log.e(TAG, "makeupSuccess-->" + makeupSuccess);
            //全身背景分割初始化
            String segPath = "assets:/facemodel/facemodel_ms/ms_humanseg_v1.0.7.model";
            boolean segSuccess = NvsEffectSdkContext.initHumanDetectionExt(getApplicationContext(),
                    segPath, null, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
            Log.e(TAG, "ms segSuccess-->" + segSuccess);
            //半身背景分割模型，前置摄像头拍摄建议使用半身
            String halfBodyPath = "assets:/facemodel/facemodel_ms/ms_halfbodyseg_v1.0.6.model";
            boolean halfBodySuccess = NvsEffectSdkContext.initHumanDetectionExt(getApplicationContext(),
                    halfBodyPath, null, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_HALF_BODY);
            Log.e(TAG, "ms halfBodySuccess-->" + halfBodySuccess);
        }
    }

    /**
     * 计算 预览的数据展示旋转了多大角度，(目标方向顺时针朝上) app没开启手机横竖屏的旋转所以只计算一次即可
     * @param context 上下文
     * @param cameraId 相机id
     * @return 旋转角度
     * 计算说明
     * 1.前置摄像头相机旋转角度 270，（前置摄像头画面实际上是镜像效果）
     * 2.后置摄像头相机旋转角度 90,
     * 3.camera角度+屏幕的旋转角度 计算出旋转多大度数到正角度。
     */
    public int getDisplayOrientation(Context context, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo( );
        Camera.getCameraInfo(cameraId, info);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay( );
        int rotation = display.getRotation( );
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void setupCamera() {
        // 判断当前引擎状态是否为采集预览状态
        if (mCameraProxy.getCamera( ) == null) {
            if (mCameraProxy.getNumberOfCameras( ) == 1) {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }
        mCameraProxy.stopPreview( );
        mCameraProxy.openCamera(mCameraId);
        mCameraProxy.setPreviewSize(1280, 720);
        mOrientation = getDisplayOrientation(this, mCameraProxy.getCameraId( ));
    }

    private void switchFlash() {
        if (mCameraProxy.cameraOpenFailed( )) {
            return;
        }
        if (mFlashView.isEnabled( )) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Toast.makeText(MainActivity.this, getString(R.string.open_flash_after_open_rear_camera), Toast.LENGTH_SHORT).show( );
                return;
            }
            mFlashToggle = !mFlashToggle;
            mCameraProxy.toggleFlash(mFlashToggle);
        }
    }

    private void switchCamera() {
        if (mCameraProxy.cameraOpenFailed( )) {
            return;
        }
        synchronized (mGLThreadSyncObject) {
            mIsPreviewing = false;
        }
        mCameraId = 1 - mCameraId;
        startCapturePreview( );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setControllerListener() {
        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int angle) {
                mDeviceOrientation = angle;
                if (mCameraProxy != null) {
                    mCameraProxy.setPictureRotate(angle);
                }
            }
        };
        mThreeToFourBtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
            }
        });
        mNineToSixteenBtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

            }
        });
        mFlashView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                switchFlash( );
            }
        });
        mSwitchView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                switchCamera( );
            }
        });
        mExposeView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                boolean supportExpose = mCameraProxy.isSupportExpose( );
                if (!supportExpose) {
                    Toast.makeText(MainActivity.this, getString(R.string.exposure_is_not_supported), Toast.LENGTH_SHORT).show( );
                    return;
                }
                SeekBar adjustSeekBar = mZoomAndExposeContainer.findViewById(R.id.adjust_seekbar);
                final int exposureCompensationRange = mCameraProxy.getExposureCompensationRange( );
                adjustSeekBar.setMax(exposureCompensationRange);
                adjustSeekBar.setOnSeekBarChangeListener(new AbstractCustomSeekBarListener( ) {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mCameraProxy.setExposureCompensation(progress - exposureCompensationRange / 2);
                        }
                    }
                });
                showCaptureDialogView(mCaptureDialog, mZoomAndExposeContainer);
            }
        });
        mBeautyShapeView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mBeautyView);
            }
        });
        mZoomView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                boolean supportZoom = mCameraProxy.isSupportZoom( );
                if (!supportZoom) {
                    Toast.makeText(MainActivity.this, getString(R.string.zoom_is_not_supported), Toast.LENGTH_SHORT).show( );
                    return;
                }
                SeekBar adjustSeekBar = mZoomAndExposeContainer.findViewById(R.id.adjust_seekbar);
                final int zoomRange = mCameraProxy.getZoomRange( );
                adjustSeekBar.setMax(zoomRange);
                adjustSeekBar.setOnSeekBarChangeListener(new AbstractCustomSeekBarListener( ) {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mCameraProxy.setZoom(progress);
                        }
                    }
                });
                showCaptureDialogView(mCaptureDialog, mZoomAndExposeContainer);
            }
        });
        mTypeVideoBtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                selectRecordType(false);
            }
        });
        mTypeLeftView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                selectRecordType(true);
            }
        });
        // 拍照或者录制
        mButtonRecord.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mRecordType == RECORD_TYPE_PICTURE) {
                    takePhoto( );
                } else {
                    if (mIsRecording) {
                        stopRecordVideo( );
                    } else {
                        startRecordVideo( );
                    }
                }

            }
        });

        mGlView.setOnTouchListener(new View.OnTouchListener( ) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isFragmentShow) {
                    dismissFragment();
                    mDrawRect.setVisibility(View.VISIBLE);
                }
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return false;
                }
                float rectHalfWidth = mAutoFocusRectView.getWidth( ) >> 1;
                boolean isInClickableRange = (event.getX( ) - rectHalfWidth >= 0) && (event.getX( ) + rectHalfWidth <= mGlView.getWidth( ))
                        && (event.getY( ) - rectHalfWidth >= 0) && (event.getY( ) + rectHalfWidth <= mGlView.getHeight( ));
                if (isInClickableRange) {
                    mAutoFocusRectView.setX(event.getX( ) - rectHalfWidth);
                    mAutoFocusRectView.setY(event.getY( ) - rectHalfWidth);
                    mAutoFocusRectView.startAnimation(mFocusAnimation);
                    mCameraProxy.autoFocus(mGlView, event);
                }
                return false;
            }
        });

        findViewById(R.id.layout_adjust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdjustFragment == null) {
                    mAdjustFragment = new AdjustFragment(meisheRender);
                }
                if(!isFragmentShow) {
                    mAdjustFragment.changeVisible(View.VISIBLE);
                    showFragment(mAdjustFragment);
                    mDrawRect.setVisibility(View.GONE);
                }
                mAdjustFragment.setOnEffectAddedListener(new AdjustFragment.OnEffectAddedListener() {
                    @Override
                    public void onEffectAdded(NvsEffect nvsEffect) {
                        //这里只有在校色功能有用。
                        if(null == mAdjustEffectList){
                            mAdjustEffectList = new ArrayList<>();
                        }
                        mAdjustEffectList.add(nvsEffect);
                        addNewRenderEffect(nvsEffect);
                    }
                });
            }
        });

        mCompoundCaptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mCompoundCaptionView);
            }
        });

        findViewById(R.id.traditional_caption_lv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mTraditionalCaptionView);
            }
        });

        findViewById(R.id.sticker_lv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mStickerListView);
            }
        });

        findViewById(R.id.transition_lv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mTransitionListView);
            }
        });

        mDrawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {
                PointF pre = mapViewToCanonical(prePointF);
                PointF p = mapViewToCanonical(nowPointF);
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.translateCaption(timeLinePointF);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurVideoEffectCaption != null) {
                        mCurVideoEffectCaption.translateCaption(timeLinePointF);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimatedSticker != null) {
                        mCurAnimatedSticker.translateAnimatedSticker(timeLinePointF);
                    }
                }
                updateDrawRect();
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float angle) {
                PointF assetAnchor = mapViewToCanonical(anchor);
//                PointF assetAnchor = new PointF(0, 0);
                if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        mCurCompoundCaption.rotateCaption(angle, assetAnchor);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurVideoEffectCaption != null) {
                        mCurVideoEffectCaption.scaleCaption(scaleFactor, assetAnchor);
                        mCurVideoEffectCaption.rotateCaption(angle, assetAnchor);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimatedSticker != null) {
                        mCurAnimatedSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        mCurAnimatedSticker.rotateAnimatedSticker(angle, assetAnchor);
                    }
                }
                updateDrawRect();
            }

            @Override
            public void onScaleXandY(float xScaleFactor, float yScaleFactor, PointF anchor) {

            }

            @Override
            public void onDel() {
                removeRenderEffect(mCurNvsEffectUuid);
                mDrawRect.setDrawRect(null, 0);
            }

            @Override
            public void onTouchDown(PointF curPoint) {

            }

            @Override
            public void onAlignClick(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION
                        && mCurVideoEffectCaption != null) {
                    switch (mCurVideoEffectCaption.getTextAlignment()) {
                        case NvsVideoEffectCaption.TEXT_ALIGNMENT_LEFT:
                            mCurVideoEffectCaption.setTextAlignment(NvsVideoEffectCaption.TEXT_ALIGNMENT_CENTER);  //居中对齐
                            mDrawRect.setAlignIndex(1);
                            break;
                        case NvsVideoEffectCaption.TEXT_ALIGNMENT_CENTER:
                            mCurVideoEffectCaption.setTextAlignment(NvsVideoEffectCaption.TEXT_ALIGNMENT_RIGHT);  //居右/上对齐
                            mDrawRect.setAlignIndex(2);
                            break;
                        case NvsVideoEffectCaption.TEXT_ALIGNMENT_RIGHT:
                            mCurVideoEffectCaption.setTextAlignment(NvsVideoEffectCaption.TEXT_ALIGNMENT_LEFT);  //左或者下对齐
                            mDrawRect.setAlignIndex(0);
                            break;
                    }
                    updateDrawRect();
                }
            }

            @Override
            public void onOrientationChange(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    //切换横竖字幕
                    if (mCurVideoEffectCaption != null) {
                        mCurVideoEffectCaption.setVerticalLayout(!isHorizontal);
                        updateDrawRect();
                    }
                }
            }

            @Override
            public void onHorizontalFlipClick() {
                if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    //切换横竖字幕
                    if (mCurAnimatedSticker != null) {
                        mCurAnimatedSticker.setHorizontalFlip(!mCurAnimatedSticker.getHorizontalFlip());
                        updateDrawRect();
                    }
                }
            }

            @Override
            public void onBeyondDrawRectClick() {

            }
        });

        mDrawRect.setDrawRectClickListener(new DrawRect.onDrawRectClickListener() {
            @Override
            public void onDrawRectClick(int captionIndex) {
                final int compoundCaptionIndex = captionIndex;
                InputDialog inputDialog = new InputDialog(MainActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean ok) {
                        if (ok) {
                            InputDialog d = (InputDialog) dialog;
                            String userInputText = d.getUserInputText();
                            if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                                if (mCurVideoEffectCaption != null) {
                                    mCurVideoEffectCaption.setText(userInputText);
                                }
                            } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                                if (mCurCompoundCaption != null) {
                                    mCurCompoundCaption.setText(compoundCaptionIndex, userInputText);
                                }
                            }
                            updateDrawRect();
                        }
                    }
                });
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurVideoEffectCaption != null) {
                        inputDialog.setUserInputText(mCurVideoEffectCaption.getText());
                    }
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if(compoundCaptionIndex < 0) {
                        return;
                    }
                    if (mCurCompoundCaption != null) {
                        inputDialog.setUserInputText(mCurCompoundCaption.getText(compoundCaptionIndex));
                    }
                }
                inputDialog.show();
            }
        });
        mMakeUpLayout.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mMakeupView);
            }
        });
        mSegLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSegBackground){
                    if (mPermissionGranted && meisheRender != null) {
                        if(null == mSegBackgroundEffect){
                            mSegBackgroundEffect = createSegmentation();
                        }
                        addNewRenderEffect(mSegBackgroundEffect);
                    }
                }else{
                    removeRenderEffect(mSegmentation);
                    //mSegBackgroundEffect = null;
                }
                isSegBackground = !isSegBackground;
            }
        });
        mBeautyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureDialogView(mCaptureDialog, mWhitingView);
            }
        });
        mWhiteSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null != mArSceneFaceEffect) {
                    mArSceneFaceEffect.setFloatVal("Beauty Whitening", progress*1.0f/100);

                }
            }
        });
        mReddingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mArSceneFaceEffect.setFloatVal("Beauty Reddening", progress*1.0f/100);
            }
        });
        mStrengthSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mArSceneFaceEffect.setFloatVal("Beauty Strength", progress*1.0f/100);
            }
        });
        bufferBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(null != mArSceneFaceEffect){
                    mArSceneFaceEffect.setBooleanVal("Single Buffer Mode",!isChecked);
                }
            }
        });
    }

    private void showFragment(final Fragment targetFragment) {
        isFragmentShow = true;
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        /*
         * 动画时间500毫秒
         *The animation time is 500 ms
         * */
        translate.setDuration(200);
        translate.setFillAfter(true);
        mStartLayout.startAnimation(translate);

        mFragmentLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (!targetFragment.isAdded()) {
                    transaction
                            .add(R.id.fragment_container, targetFragment)
                            .commit();
                } else {
                    transaction
                            .show(targetFragment)
                            .commit();
                }
                TranslateAnimation topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                topTranslate.setDuration(200);
                topTranslate.setFillAfter(true);
                mFragmentLayout.startAnimation(topTranslate);
            }
        }, 200);
    }

    private void dismissFragment() {
        isFragmentShow = false;
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        /*
         * 动画时间500毫秒
         *The animation time is 500 ms
         * */
        translate.setDuration(200);
        translate.setFillAfter(true);
        mFragmentLayout.startAnimation(translate);
        mAdjustFragment.changeVisible(View.GONE);
        mStartLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                topTranslate.setDuration(200);
                topTranslate.setFillAfter(true);
                mStartLayout.startAnimation(topTranslate);
            }
        }, 200);

    }

    private void updateDrawRect() {
        mDrawRect.setVisibility(View.VISIBLE);
        List<PointF> list = null;
        if (mEditMode == Constants.EDIT_MODE_CAPTION) {
            if(mCurVideoEffectCaption == null) {
                return;
            }
            list = mCurVideoEffectCaption.getCaptionBoundingVertices(NvsVideoEffectCompoundCaption.BOUNDING_TYPE_FRAME);
        } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            if(mCurCompoundCaption != null) {
                list = mCurCompoundCaption.getCompoundBoundingVertices(NvsVideoEffectCompoundCaption.BOUNDING_TYPE_FRAME);
                List<List<PointF>> newSubCaptionList = new ArrayList<>();
                int subCaptionCount = mCurCompoundCaption.getCaptionCount();
                for (int index = 0; index < subCaptionCount; index++) {
                    List<PointF> subList = mCurCompoundCaption.getCaptionBoundingVertices(index, NvsVideoEffectCompoundCaption.BOUNDING_TYPE_TEXT);
                    if (subList == null || subList.size() < 4) {

                        continue;
                    }
                    List<PointF> newSubList = getAssetViewVerticesList(subList);
                    newSubCaptionList.add(newSubList);
                }
                mDrawRect.setCompoundDrawRect(getAssetViewVerticesList(list), newSubCaptionList, Constants.EDIT_MODE_COMPOUND_CAPTION);
                return;
            }
        } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
            if(mCurAnimatedSticker == null) {
                return;
            }
            list = mCurAnimatedSticker.getBoundingRectangleVertices();
        }
        mDrawRect.setDrawRect(getAssetViewVerticesList(list), mEditMode);

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPermissionGranted = true;
                        startCapturePreview( );
                    } else {
                        setCaptureEnabled(false);
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    setCaptureEnabled(false);
                    requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
            } else {
                setCaptureEnabled(false);
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            mPermissionGranted = true;
            startCapturePreview( );
        }
    }

    private void setCaptureEnabled(boolean enabled) {
        mButtonRecord.setEnabled(enabled);
    }

    private void startCapturePreview() {
        if (!mPermissionGranted || mIsPreviewing || mCameraPreviewTexture == null) {
            return;
        }
        if (mOrientationEventListener.canDetectOrientation( )) {
            mOrientationEventListener.enable( );
        }
        setCaptureEnabled(true);
        setupCamera( );
        mCameraProxy.startPreview(mCameraPreviewTexture, mPreviewCallback);
        Camera.Size size = mCameraProxy.getPreviewSize( );
        mCurrentVideoResolution = new NvsVideoResolution( );
        if (mOrientation == 90 || mOrientation == 270) {
            mCurrentVideoResolution.imageWidth = size.height;
            mCurrentVideoResolution.imageHeight = size.width;
        } else {
            mCurrentVideoResolution.imageWidth = size.width;
            mCurrentVideoResolution.imageHeight = size.height;
        }
        mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);
        mFlipHorizontal = mCameraProxy.isFlipHorizontal( );
        synchronized (mGLThreadSyncObject) {
            mIsPreviewing = true;
        }
        mTimeStamp = System.currentTimeMillis( );
        resetRenderedEffectTime();
        setCaptureEnabled(true);
    }


    private void setLevel(Button button) {
        button.getBackground( ).setLevel(1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPermissionGranted = true;
                        startCapturePreview( );
                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mPermissionGranted = true;
                    startCapturePreview( );
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                mPermissionGranted = true;
                startCapturePreview( );
                break;
            default:
                break;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            mFrameAvailable = true;
        }
        mGlView.requestRender( );
    }

    @Override
    protected void onDestroy() {
        //effectContext销毁
        stopRenderThread();
//        destroyGlResource( );
        if (mPhotoImageProcessor != null) {
            mPhotoImageProcessor.destroy( );
        }
        mAccelerometer = null;
        mEffectSdkContext = null;
        mCameraProxy = null;
        NvsEffectSdkContext.closeHumanDetection();
        NvsEffectSdkContext.close( );
        super.onDestroy( );
    }

    @Override
    protected void onPause() {
        setLevel(mButtonRecord);
        if (mRecordType == RECORD_TYPE_PICTURE) {
            mButtonRecord.setText(R.string.record);
        } else {
            mButtonRecord.setText(R.string.start_record);
        }

        mCameraProxy.stopPreview( );
        mCameraProxy.releaseCamera( );
        if(mIsRecording){
            stopRecordVideo();
        }
        //停止引擎
        synchronized (mGLThreadSyncObject) {
            mIsPreviewing = false;
        }
        saveEffectData();
        final CountDownLatch count = new CountDownLatch(1);
        mGlView.queueEvent(new Runnable( ) {
            @Override
            public void run() {
                destroyGlResource( );
                mCameraPreviewTexture.release();
                mCameraPreviewTexture = null;
                if (mPhotoImageProcessor != null) {
                    mPhotoImageProcessor.destroy( );
                }
                count.countDown( );
            }
        });

        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace( );
        }

        mAccelerometer.stop( );
        super.onPause( );
    }

    private void saveEffectData() {
        mSavedRenderEffectArray.clear();
        if(null == mCurrentRenderEffectList){
            return;
        }
        for (int i = 0, count = mCurrentRenderEffectList.size(); i < count; i++) {
            EffectRenderItem effectRenderItem = mCurrentRenderEffectList.get(i);
            EffectData effectData = new EffectData();
            effectData.setMarkTag(effectRenderItem.markTag);
            effectData.setStartTimeStamp(effectRenderItem.startTimeStamp);
            if (effectRenderItem.effect instanceof NvsVideoEffectCaption){
                effectData.setType(Constants.EDIT_MODE_CAPTION);
                effectData.setCaptionText(((NvsVideoEffectCaption) effectRenderItem.effect).getText());
                effectData.setScale(((NvsVideoEffectCaption) effectRenderItem.effect).getScaleX());
                effectData.setTranslatePoint(((NvsVideoEffectCaption) effectRenderItem.effect).getCaptionTranslation());
                effectData.setRotation(((NvsVideoEffectCaption) effectRenderItem.effect).getRotationZ());
                mSavedRenderEffectArray.add(effectData);
            }else if (effectRenderItem.effect instanceof NvsVideoEffectCompoundCaption){
                effectData.setType(Constants.EDIT_MODE_COMPOUND_CAPTION);
                effectData.setScale(((NvsVideoEffectCompoundCaption) effectRenderItem.effect).getScaleX());
                effectData.setTranslatePoint(((NvsVideoEffectCompoundCaption) effectRenderItem.effect).getCaptionTranslation());
                effectData.setRotation(((NvsVideoEffectCompoundCaption) effectRenderItem.effect).getRotationZ());
                mSavedRenderEffectArray.add(effectData);
            }else if (effectRenderItem.effect instanceof NvsVideoEffectAnimatedSticker) {
                effectData.setType(Constants.EDIT_MODE_STICKER);
                effectData.setScale(((NvsVideoEffectAnimatedSticker) effectRenderItem.effect).getScale());
                effectData.setTranslatePoint(((NvsVideoEffectAnimatedSticker) effectRenderItem.effect).getTranslation());
                effectData.setRotation(((NvsVideoEffectAnimatedSticker) effectRenderItem.effect).getRotationZ());
                mSavedRenderEffectArray.add(effectData);
            }else if (effectRenderItem.effect instanceof NvsVideoEffectTransition){
                effectData.setType(Constants.EDIT_MODE_TRANSITION);
                mSavedRenderEffectArray.add(effectData);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume( );
        mAccelerometer.start( );
        if(null == mGlDrawer){
            mGlDrawer = new GLDrawer();
        }
        mGlView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGlDrawer.setupVertexBuffer();
                setupTexture();
                //SDK中绝大部分函数都应在UI线程调用
                runOnUiThread(new Runnable( ) {
                    @Override
                    public void run() {
                        //在onPause中销毁，
                        if(mPermissionGranted){
                            if (!mIsPreviewing && mCameraPreviewTexture != null) {
                                startCapturePreview( );
                            }
                            createRenderCore( );
                        }
                    }
                });

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mEglContext = mGlDrawer.createEglContext();
        mGlDrawer.setupGraphics(this);
        //android.opengl.Matrix.setIdentityM(mVideoTextureTransform,0);
    }

    private void setupTexture() {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(mTextureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder( ));

        mTextureBuffer = texturebb.asFloatBuffer( );
        mTextureBuffer.put(mTextureCoords);
        mTextureBuffer.position(0);
        mGlDrawer.setTextureBuffer(mTextureBuffer);
        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        glGenTextures(1, mCameraPreviewTextures, 0);
        EGLHelper.checkGlError("Texture generate");

        if (mSurfaceAvailableThread == null) {
            mSurfaceAvailableThread = new HandlerThread("ProcessImageThread");
            mSurfaceAvailableThread.start( );
            mSurfaceAvailableHandler = new Handler(mSurfaceAvailableThread.getLooper( )) {
                @Override
                public void handleMessage(Message msg) {
                }
            };
        }

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraPreviewTextures[0]);
        //创建摄像机需要的Preview Texture
        mCameraPreviewTexture = new SurfaceTexture(mCameraPreviewTextures[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraPreviewTexture.setOnFrameAvailableListener(this, mSurfaceAvailableHandler);
        } else {
            mCameraPreviewTexture.setOnFrameAvailableListener(this);
        }
        mCameraPreviewTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        mGlDrawer.setupFrameInfo(mDisplayWidth,mDisplayHeight);
        mRatio = Math.max(mDisplayWidth / 720.00f, mDisplayHeight / 1280.00f);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDrawFrame(GL10 gl) {
        if (mIsCaptureAtomicBoolean.compareAndSet(true, false)) {
            Bitmap bitmap = GLUtils.createBitmapFromGLSurface(mGlView.getWidth(), mGlView.getHeight(), gl);
            if (mAdjustFragment != null && bitmap != null) {
                mAdjustFragment.updateAitInfoAndView(bitmap);
            }
        }
        drawFrameToGlView( );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void drawFrameToGlView() {
        if (!mIsPreviewing) {
            return;
        }
        synchronized (this) {
            if (mFrameAvailable) {
                mCameraPreviewTexture.updateTexImage( );
                mCameraPreviewTexture.getTransformMatrix(mVideoTextureTransform);
                mFrameAvailable = false;
            }
        }
        if (mGlDrawer == null) {
            mGlDrawer = new GLDrawer();
        }
        mGlDrawer.setTransformMatrix(mVideoTextureTransform);
        //计算当前的时间戳
        long currentTimeStamp = (System.currentTimeMillis( ) - mTimeStamp);
        boolean isInPreview = false;
        synchronized (mGLThreadSyncObject) {
            isInPreview = mIsPreviewing;
        }

        int texWidth = 0;
        int texHeight = 0;
        int displayTex = mCameraPreviewTextures[0];
        if (isInPreview && (meisheRender != null)) {
            texWidth = mCurrentVideoResolution.imageWidth;
            texHeight = mCurrentVideoResolution.imageHeight;
            List<EffectRenderItem>renderEffectList = new ArrayList<>();
            List<EffectRenderItem>removeEffectList = new ArrayList<>();
            synchronized (mArraySyncObject){
                if(null != mCurrentRenderEffectList && mCurrentRenderEffectList.size()>0){
                    renderEffectList.addAll(mCurrentRenderEffectList);
                }
                if(null != mClearEffectList && mClearEffectList.size()>0){
                    removeEffectList.addAll(mClearEffectList);
                }
            }
            RenderEffectParams renderEffectParams = new RenderEffectParams()
                    .setRenderEffectList(renderEffectList)
                    .setClearEffectList(removeEffectList)
                    .setTexture(mCameraPreviewTextures[0])
                    .isOesTexture(true)
                    .setWidth(texWidth)
                    .setHeight(texHeight)
                    .setCurrentTimeStamp(currentTimeStamp)
                    .setCameraOrientation(mCameraProxy.getOrientation())
                    .setDisplayOrientation(mOrientation)
                    .isFlipHorizontal(mFlipHorizontal)
                    .setDeviceOrientation(mDeviceOrientation)
                    .isImageMode(false)
                    .isBufferMode(bufferBox.isChecked())
                    .build();
            // renderCore 导入纹理后 渲染出新的纹理
            displayTex = meisheRender.renderVideoEffect(renderEffectParams);
            if(null != mGlDrawer){
                mGlDrawer.setBufferMode(bufferBox.isChecked());
            }

            mClearEffectList.clear();
        }
        if (mGlDrawer != null) {
            mGlDrawer.setupFrameInfo(mDisplayWidth,mDisplayHeight);

            if (displayTex == mCameraPreviewTextures[0]) {
                mGlDrawer.drawTextureOES(mCameraPreviewTextures[0], texWidth, texHeight, mDisplayWidth, mDisplayHeight);
                if (mIsRecording)
                    //displayTex = mGlDrawer.drawTextureToTexture(mCameraPreviewTextures[0],texWidth, texHeight, mDisplayWidth, mDisplayHeight);
                    displayTex = meisheRender.preProcessOesToTexture2D(mCameraPreviewTextures[0],texWidth, texHeight, mCameraProxy.getOrientation(),mFlipHorizontal);
            } else {
                mGlDrawer.drawTexture(displayTex, texWidth, texHeight, mDisplayWidth, mDisplayHeight);
            }
        }

        if (mIsRecording) {
            long timestamp = mCameraPreviewTexture.getTimestamp( );
            GLES20.glFinish( );
            synchronized (this) {
                if (mVideoEncoder != null) {
                    if (mNeedResetEglContext) {
                        mEglContext = mGlDrawer.createEglContext();
                        mVideoEncoder.setEglContext(mEglContext);
                        mNeedResetEglContext = false;
                    }
                    mVideoEncoder.frameAvailableSoon(displayTex, timestamp);
                }
            }
        } else {
            if (displayTex != mCameraPreviewTextures[0]) {
                GLUtils.destroyGlTexture(displayTex);
            }
        }
    }


    private void createRenderCore() {
        if (meisheRender == null) {
            meisheRender = new MeisheRender();
            meisheRender.init();
        }

        NvsRational aspectRatio = new NvsRational(9, 16);
        if (mArSceneFaceEffect == null) {
            mArSceneFaceEffect = mEffectSdkContext.createVideoEffect("AR Scene", aspectRatio);
        }
        if (mArSceneFaceEffect != null) {
            //这里初始化改为false，
            //single buffer mode true表示渲染时会把buffer上传到纹理，这样会导致之前渲染的效果被当前的buffer覆盖
            //
            mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", false);
            mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
            mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
            mArSceneFaceEffect.setStringVal("Scene Id", mSceneId);
            NvsARSceneManipulate arSceneManipulate = mArSceneFaceEffect.getARSceneManipulate( );
            arSceneManipulate.setARSceneCallback(new NvsARSceneManipulate.NvsARSceneManipulateCallback() {
                @Override
                public void notifyFaceBoundingRect(List<NvsARSceneManipulate.NvsFaceBoundingRectInfo> list) {
                }

                @Override
                public void notifyFaceFeatureInfos(List<NvsARSceneManipulate.NvsFaceFeatureInfo> list) {
                }

                @Override
                public void notifyCustomAvatarRealtimeResourcesPreloaded(boolean b) {
                }

                @Override
                public void notifyDetectionTimeCost(float v) {
                }
                @Override
                public void notifyTotalTimeCost(float v) {

                }
            });

            // 添加美颜默认参数
          /*  Beauty Effect 是否开启美颜,默认值是false。
            Beauty Strength 磨皮。具体描述如下：

            Parameter Type: Floating Point
            Default Value: 0.5
            Minimum value: 0
            Maximum value: 1

            Beauty Whitening 美白。具体描述如下：

            Parameter Type: Floating Point
            Default Value: 0
            Minimum value: 0
            Maximum value: 1

            Beauty Reddening 红润。具体描述如下：

            Parameter Type: Floating Point
            Default Value: 0
            Minimum value: 0
            Maximum value: 1

            Default Beauty Enabled 默认美颜滤镜是否开启，默认值是true。
            Default Intensity 默认美颜滤镜强度值。具体描述如下：
            Parameter Type: Floating Point
            Default Value: 1
            Minimum value: 0
            Maximum value: 1

            Default Sharpen Enabled 是否开启锐化，默认值是false。*/
            mArSceneFaceEffect.setFloatVal("Beauty Strength", 0.5f);
            mArSceneFaceEffect.setFloatVal("Beauty Whitening", 0.5f);
            mArSceneFaceEffect.setFloatVal("Beauty Reddening", 0.5f);
            mArSceneFaceEffect.setBooleanVal("Default Beauty Enabled", true);
            mArSceneFaceEffect.setFloatVal("Default Intensity", 1.0f);
            mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", true);
        }
        if (mPermissionGranted ) {
            addNewRenderEffect(mArSceneFaceEffect);
        }
        if (mFilter == null) {
            NvsRational nvsRational = new NvsRational(16,9);
            mFilter = mEffectSdkContext.createVideoEffect(mFilterId, nvsRational);
        }
        if(null == mSegBackgroundEffect){
            mSegBackgroundEffect = createSegmentation();
        }
        addNewRenderEffect(mFilter);
        resumeRenderArray();
        //updateDrawRect();
    }

    private NvsVideoEffect createSegmentation(){
        NvsRational nvsRational = new NvsRational(16,9);
        mSegBackgroundEffect =mEffectSdkContext.createVideoEffect(mSegmentation, nvsRational);
        if(null == mSegBackgroundEffect){
            return null;
        }
        mSegBackgroundEffect.setStringVal("Tex File Path","assets:/bg.png");
        //0 不拉伸 1拉伸填满
        mSegBackgroundEffect.setIntVal("Stretch Mode",1);
        //半身
        mSegBackgroundEffect.setMenuVal("Segment Type", "Half Body");
        //背景图不拉伸 填充的背景色
        mSegBackgroundEffect.setColorVal("Background Color",new NvsColor(0,0.5f,1.0f,0.5f));
        return mSegBackgroundEffect;
    }

    private void resumeRenderArray() {
        for (int i = 0; i < mSavedRenderEffectArray.size(); i++) {
            EffectData effectRenderItem = mSavedRenderEffectArray.get(i);
            switch (effectRenderItem.getType()){
                case Constants.EDIT_MODE_CAPTION:
                    mTraditionalCaptionView.setShowDialog(false);
                    mTraditionalCaptionView.setCaptionText(effectRenderItem.getCaptionText());
                    mTraditionalCaptionView.onAssetItemSelected(effectRenderItem.getMarkTag());
                    if (mCurVideoEffectCaption != null){
                        mCurVideoEffectCaption.setCaptionTranslation(effectRenderItem.getTranslatePoint());
                        mCurVideoEffectCaption.setScaleX(effectRenderItem.getScale());
                        mCurVideoEffectCaption.setScaleY(effectRenderItem.getScale());
                        mCurVideoEffectCaption.setRotationZ(effectRenderItem.getRotation());
                    }
                    break;
                case Constants.EDIT_MODE_COMPOUND_CAPTION:
                    mCompoundCaptionView.onAssetItemSelected(effectRenderItem.getMarkTag());
                    if (mCurCompoundCaption != null){
                        mCurCompoundCaption.setCaptionTranslation(effectRenderItem.getTranslatePoint());
                        mCurCompoundCaption.setScaleX(effectRenderItem.getScale());
                        mCurCompoundCaption.setRotationZ(effectRenderItem.getRotation());
                    }
                    break;
                case Constants.EDIT_MODE_STICKER:
                    mStickerListView.onAssetItemSelected(effectRenderItem.getMarkTag());
                    if (mCurAnimatedSticker != null){
                        mCurAnimatedSticker.translateAnimatedSticker(effectRenderItem.getTranslatePoint());
                        mCurAnimatedSticker.setScale(effectRenderItem.getScale());
                        mCurAnimatedSticker.setRotationZ(effectRenderItem.getRotation());
                    }
                    break;
                case Constants.EDIT_MODE_TRANSITION:
                    mTransitionListView.onAssetItemSelected(effectRenderItem.getMarkTag());
                    break;
                default:
                    Log.e(TAG, "createRenderCore: 未知类型" );
                    break;
            }
        }
        if(null !=mSavedRenderEffectArray && mSavedRenderEffectArray.size()>0){
            updateDrawRect();
        }
    }

    private void destroyGlResource() {
        if (meisheRender != null) {
            meisheRender.release(mCurrentRenderEffectList,mClearEffectList );
        }
        if(null != mCurrentRenderEffectList){

            mCurrentRenderEffectList.clear();
        }
        if(null != mClearEffectList){
            mClearEffectList.clear();
        }
        if(null != mGlDrawer){
            mGlDrawer.release();
        }
        //当前页面中创建的所有特技都需要 析构释放
        if(null != mCurAnimatedSticker){
            mCurAnimatedSticker.release();
            mCurAnimatedSticker = null;
        }
        if(null != mCurVideoEffectCaption){
            mCurVideoEffectCaption.release();
            mCurVideoEffectCaption = null;
        }
        if(null != mCurCompoundCaption){
            mCurCompoundCaption.release();
            mCurCompoundCaption = null;
        }
        if(null != mCurTranslationEffect){
            mCurTranslationEffect.release();
            mCurTranslationEffect = null;
        }
        if(null != mSegBackgroundEffect){
            mSegBackgroundEffect.release();
            mSegBackgroundEffect = null;
        }
        if(null != mFilter){
            mFilter.release();
            mFilter = null;
        }
        if(null != mArSceneFaceEffect){
            mArSceneFaceEffect.release();
            mArSceneFaceEffect = null;
        }
        if(null != mAdjustEffectList && mAdjustEffectList.size()>0){
            for(NvsEffect nvsEffect : mAdjustEffectList){
                nvsEffect.release();
                nvsEffect = null;
            }
        }
        mAdjustFragment = null;
        meisheRender = null;
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback( ) {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            if (meisheRender != null) {
                meisheRender.sendPreviewBuffer(data);
            }
            //强制刷新glSurfaceView
            mGlView.requestRender( );
        }
    };

    private void takePhoto() {
        if (mCameraProxy == null) {
            return;
        }
        if (!mCameraProxy.isCameraOpen( )) {
            return;
        }
        if(null == mPhotoImageProcessor){
            mPhotoImageProcessor = new PhotoImageProcessor( );
        }
        setCaptureEnabled(false);
        Camera.PictureCallback jpgPictureCallback = new Camera.PictureCallback( ) {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                if (data == null) {
                    return;
                }
                // main thread
                setCaptureEnabled(false);
                Message msg = Message.obtain();
                msg.what =1;
                msg.obj = data;
                mPhotoHandler.sendMessage(msg);

            }
        };

        mCameraProxy.takePicture(null, null, jpgPictureCallback);
    }

    private void selectRecordType(boolean left_to_right) {
        TranslateAnimation ani;
        if (left_to_right) {
            if (mRecordType == RECORD_TYPE_PICTURE) {
                return;
            }
            ani = new TranslateAnimation(-mTypePictureBtn.getX( ), mTypeLeftView.getX( ), 0, 0);
            mTypePictureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTypeVideoBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mButtonRecord.setText(R.string.record);
            mRecordType = RECORD_TYPE_PICTURE;
        } else {
            ani = new TranslateAnimation(mTypeLeftView.getX( ), -mTypePictureBtn.getX( ), 0, 0);
            mTypePictureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mTypeVideoBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mButtonRecord.setText(R.string.start_record);
            mRecordType = RECORD_TYPE_VIDEO;
        }
        ani.setDuration(300);
        ani.setFillAfter(true);
        mRecordTypeLayout.startAnimation(ani);
    }

    private void updateTypeLeftView() {
        mTypeLeftView.post(new Runnable( ) {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = mTypeLeftView.getLayoutParams( );
                layoutParams.width = mTypeVideoBtn.getWidth( );
                mTypeLeftView.setLayoutParams(layoutParams);
            }
        });
    }

    private void startRecordVideo() {
        mNeedResetEglContext = true;
        try {
            mMuxer = new MediaMuxerWrapper(".mp4");
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCurrentVideoResolution.imageWidth, mCurrentVideoResolution.imageHeight, 0);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            mMuxer.prepare( );
            mMuxer.startRecording( );
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
        mIsRecording = true;
        mButtonRecord.setText("");
        mButtonRecord.setBackgroundResource(R.mipmap.particle_capture_recording);
    }

    private void stopRecordVideo() {
        mIsRecording = false;
        if (mMuxer != null) {
            mMuxer.stopRecording( );
        }
        mButtonRecord.setText(R.string.start_record);
        mButtonRecord.setBackgroundResource(R.drawable.record_button_list);
        Intent intent = new Intent(this, RecordFinishActivity.class);
        intent.putExtra("file_path", mMuxer.getOutputPath( ));
        startActivity(intent);
        mMuxer = null;
    }

    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener( ) {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                setVideoEncoder((MediaVideoEncoder) encoder);
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                setVideoEncoder(null);
            }
        }
    };

    public void setVideoEncoder(final MediaVideoEncoder encoder) {
        mGlView.queueEvent(new Runnable( ) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                synchronized (this) {
                    if ((encoder != null)) {
                        encoder.setEglContext(mEglContext);
                    }
                    mVideoEncoder = encoder;
                }
            }
        });
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        if(null != verticesList){
            for (int i = 0; i < verticesList.size(); i++) {
                PointF pointf = verticesList.get(i);
                float x = mDisplayWidth / 2.00f + pointf.x * mRatio;
                float y = (mDisplayHeight / 2.00f) - pointf.y * mRatio;
                newList.add(new PointF(x, y));
            }
        }

        return newList;
    }

    private PointF mapViewToCanonical(PointF screenPointF) {
        PointF pointf = new PointF();
        pointf.x = (screenPointF.x - mDisplayWidth / 2.00f) / mRatio;
        pointf.y = (mDisplayHeight / 2.00f - screenPointF.y) / mRatio;
        return pointf;
    }

    private void stopRenderThread() {
        try {
            if (mSurfaceAvailableHandler != null) {
                mSurfaceAvailableHandler.removeCallbacksAndMessages(null);
                mSurfaceAvailableHandler = null;
            }
            if(mSurfaceAvailableThread != null){
                if( Build.VERSION.SDK_INT >=  Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mSurfaceAvailableThread.quitSafely();
                }
                mSurfaceAvailableThread.join();
                mSurfaceAvailableThread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void captureForAdjust() {
        mIsCaptureAtomicBoolean.set(true);
    }

    //设置贴纸动画做移除效果使用，测试贴纸的出入动画周期动画
    public class VisibleRunnable implements Runnable{
        @Override
        public void run() {
            mDrawRect.setVisibility(View.GONE);
        }
    }

    public void addNewRenderEffect(NvsEffect effect) {
        if (effect == null) {
            return;
        }
        if(null == mCurrentRenderEffectList) {
            mCurrentRenderEffectList = new CopyOnWriteArrayList<>();
        }
        EffectRenderItem item = new EffectRenderItem( );
        item.effect = effect;
        item.startTimeStamp = -1;
        synchronized (mArraySyncObject) {
            //这里对渲染顺序进行重新排序，把分割放在第一个渲染，避免分割导致的其他特技异常

            if( (effect instanceof NvsVideoEffect) &&((NvsVideoEffect)effect).getBuiltinVideoFxName().equalsIgnoreCase(mSegmentation)){
                mCurrentRenderEffectList.add(0,item);
            }
            else mCurrentRenderEffectList.add(item);
            //mRenderArray.add(item);
        }
    }

    public void addNewRenderEffect(NvsEffect effect, String markTag) {
        if (effect == null) {
            return;
        }
        if(null == mCurrentRenderEffectList){
            mCurrentRenderEffectList = new CopyOnWriteArrayList<>();
        }

        EffectRenderItem item = new EffectRenderItem( );
        item.effect = effect;
        item.startTimeStamp = -1;
        item.markTag = markTag;
        synchronized (mArraySyncObject) {
            mCurrentRenderEffectList.add(item);
        }
    }

    public void removeRenderEffect(String effectId) {
        synchronized (mArraySyncObject) {
            for (EffectRenderItem ef : mCurrentRenderEffectList) {
                if(ef.effect instanceof NvsVideoEffect) {
                    NvsVideoEffect videoEffect = (NvsVideoEffect) ef.effect;
                    String strPackageId = videoEffect.getVideoFxPackageId( );
                    String strBuildInName = videoEffect.getBuiltinVideoFxName( );
                    if (strPackageId.equalsIgnoreCase(effectId) || strBuildInName.equalsIgnoreCase(effectId)) {
                        mCurrentRenderEffectList.remove(ef);
                        mClearEffectList.add(ef);
                        break;
                    }
                } else if(ef.effect instanceof NvsVideoEffectCaption) {
                    NvsVideoEffectCaption videoEffect = (NvsVideoEffectCaption) ef.effect;
                    String strPackageId = videoEffect.getCaptionStylePackageId();
                    if(TextUtils.isEmpty(strPackageId)) {
                        strPackageId = ef.markTag;
                    }
                    if (strPackageId.equalsIgnoreCase(effectId)) {
                        mCurrentRenderEffectList.remove(ef);
                        mClearEffectList.add(ef);
                        break;
                    }
                } else if(ef.effect instanceof NvsVideoEffectCompoundCaption) {
                    NvsVideoEffectCompoundCaption videoEffect = (NvsVideoEffectCompoundCaption) ef.effect;
                    String strPackageId = videoEffect.getCaptionStylePackageId();
                    if (strPackageId.equalsIgnoreCase(effectId)) {
                        mCurrentRenderEffectList.remove(ef);
                        mClearEffectList.add(ef);
                        break;
                    }
                } else if(ef.effect instanceof NvsVideoEffectAnimatedSticker) {
                    NvsVideoEffectAnimatedSticker videoEffect = (NvsVideoEffectAnimatedSticker) ef.effect;
                    String strPackageId = videoEffect.getAnimatedStickerPackageId();
                    if (strPackageId.equalsIgnoreCase(effectId)) {
                        mCurrentRenderEffectList.remove(ef);
                        mClearEffectList.add(ef);
                        break;
                    }
                }else if(ef.effect instanceof NvsVideoEffectTransition){
                    NvsVideoEffectTransition videoEffect = (NvsVideoEffectTransition) ef.effect;
                    String strPackageId = videoEffect.getVideoTransitionPackageId();
                    if (strPackageId.equalsIgnoreCase(effectId)) {
                        mCurrentRenderEffectList.remove(ef);
                        mClearEffectList.add(ef);
                        break;
                    }
                }

            }
        }
    }

    public void resetEffectRenderTime() {
        if(null == mCurrentRenderEffectList || mCurrentRenderEffectList.size()==0){
            return;
        }
        synchronized (mArraySyncObject) {
            for (EffectRenderItem ef : mCurrentRenderEffectList) {
                ef.startTimeStamp = -1;
            }
        }
    }

    public void resetEffectRenderTime(String effectId) {
        if(null == mCurrentRenderEffectList || mCurrentRenderEffectList.size()==0){
            return;
        }
        synchronized (mArraySyncObject) {
            for (EffectRenderItem ef : mCurrentRenderEffectList) {
                if(TextUtils.equals(effectId,ef.markTag)){
                    ef.startTimeStamp = -1;
                    if(ef.effect instanceof NvsVideoEffectTransition){
                        meisheRender.reSetTransformStart();
                    }
                }
            }
        }
    }

    /**
     * 重置所有特技的开始渲染时间
     */
    private void resetRenderedEffectTime() {
        if(null == mCurrentRenderEffectList || mCurrentRenderEffectList.size()==0){
            return;
        }
        synchronized (mArraySyncObject) {
            for (EffectRenderItem ef : mCurrentRenderEffectList) {
                ef.startTimeStamp = -1;
            }
        }
    }

}


