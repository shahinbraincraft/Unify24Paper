package com.meishe.sdkdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.meishe.sdkdemo.main.MainWebViewActivity;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.ParameterSettingValues;
import com.meishe.sdkdemo.utils.SpUtil;
import com.meishe.sdkdemo.utils.ToastUtil;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.view.MagicProgress;

/**
 * Created by admin on 2018-5-25.
 */

public class SettingActivity extends BaseActivity {
    private String TAG = "ParameterSettingActivity";
    public static final int CompileVideoRes_2160 = 2160;
    public static final int CompileVideoRes_1080 = 1080;
    public static final int CompileVideoRes_720 = 720;
    public static final int CompileVideoRes_540 = 540;

    private CustomTitleBar mTitleBar;
    private RadioGroup mCapture_ratio_sex;
    private RadioButton mCapcture_ratio_1080;
    private RadioButton mCapcture_ratio_720;
    private RadioGroup mOut_ratio_sex;
    private RadioButton mOut_ratio_4k;
    private RadioButton mOut_ratio_1080;
    private RadioButton mOut_ratio_720;
    private RadioButton mOut_ratio_540;
    private EditText mOutput_bitrate_editText;
    private Switch mEncoder_support;
    private Switch mQuickPack;
    private Switch mBackgroud_blur;
    private Switch mSwitchAr;
    private int mCaptureResolutionGrade;
    private boolean mSingleBufferMode;
    private int mCompileVidoeRes;
    private double mCompileBitrate;
    private boolean mDisableDeviceEncoderSupport = false;
    private boolean mIsQuickPack = false;
    private boolean mIsStartNativeCamera = false;
    private boolean mIsUseBackgroundBlur = false;
    private boolean mIsUseDefaultAr = false;
    private SpUtil mSp;

    private TextView mServiceAgreement;
    private TextView mPrivacyPolicy;
    private TextView mSDKVersion;
    private int liveWindowModel = NvsLiveWindow.HDR_DISPLAY_MODE_SDR;
    private int bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT;
    private String exportConfig = "none";
    private boolean supportHevc = false;
    private Switch hevcExport;
    private RadioGroup rgCompile;
    private RadioGroup rgResolution;
    private RadioGroup rgLiveWindow;
    private MagicProgress magicProgress;
    private ScrollView mScrollview;
    private float colorGain;
    /*缓存控制*/
    private RadioGroup mCaptureCache;
    private Switch mStartNativeCamera;


    @Override
    protected int initRootView() {
        return R.layout.activity_parameter_setting;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mCapture_ratio_sex = (RadioGroup) findViewById(R.id.capture_ratio_sex);
        mCaptureCache = (RadioGroup) findViewById(R.id.capture_cache);
        mCapcture_ratio_1080 = (RadioButton) findViewById(R.id.capture_ratio_1080);
        mCapcture_ratio_720 = (RadioButton) findViewById(R.id.capture_ratio_720);
        mOut_ratio_sex = (RadioGroup) findViewById(R.id.out_ratio_sex);
        mOut_ratio_4k = (RadioButton) findViewById(R.id.output_ratio_4k);
        mOut_ratio_1080 = (RadioButton) findViewById(R.id.output_ratio_1080);
        mOut_ratio_720 = (RadioButton) findViewById(R.id.output_ratio_720);
        mOut_ratio_540 = (RadioButton) findViewById(R.id.output_ratio_540);
        mOutput_bitrate_editText = (EditText) findViewById(R.id.output_bitrate_editText);
        mEncoder_support = (Switch) findViewById(R.id.encoder_support);
        mStartNativeCamera = (Switch) findViewById(R.id.switch_start_native_camera);
        mQuickPack = (Switch) findViewById(R.id.quick_pack);
        mBackgroud_blur = (Switch) findViewById(R.id.backgroud_blur);
        mSwitchAr = (Switch) findViewById(R.id.switch_arSet);
        mServiceAgreement = (TextView) findViewById(R.id.serviceAgreement);
        mPrivacyPolicy = (TextView) findViewById(R.id.privacyPolicy);
        mSDKVersion = (TextView) findViewById(R.id.sdkVersion);
        rgLiveWindow = findViewById(R.id.rg_liveWindow);
        magicProgress = findViewById(R.id.hdr_color_gain_progress);
        magicProgress.setMax(90);
        magicProgress.setOnTouchStateChangeListener(new MagicProgress.OnTouchStateChangeListener() {
            @Override
            public void onTouchUp(boolean touchUpFlag) {
                mScrollview.requestDisallowInterceptTouchEvent(!touchUpFlag);
            }
        });
        magicProgress.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                colorGain = 1 + progress / 10f;
                Log.d(TAG, "setting colorGain:" + colorGain + " pro:" + progress);
            }
        });

        magicProgress.setBreakProgress(-10);
        magicProgress.setAnimalEnable(false);
        magicProgress.setBaseProgress(10f);
        magicProgress.setPointEnable(false);
        mScrollview = findViewById(R.id.scrollview_main);


        rgLiveWindow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_live_sdr:
                        liveWindowModel = NvsLiveWindow.HDR_DISPLAY_MODE_SDR;
                        break;
                    case R.id.rb_live_device:
                        liveWindowModel = NvsLiveWindow.HDR_DISPLAY_MODE_DEPEND_DEVICE;
                        break;
                    case R.id.rb_live_if:
                        liveWindowModel = NvsLiveWindow.HDR_DISPLAY_MODE_TONE_MAP_SDR;
                        break;
                }
                Log.d(TAG, "set liveWindow Hdr model liveWindowModel:" + liveWindowModel);
            }
        });
        rgResolution = findViewById(R.id.rg_resolution);
        rgResolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_resolution_8:
                        bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT;
                        break;
                    case R.id.rb_resolution_16:
                        bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_16_BIT_FLOAT;
                        break;
                    case R.id.rb_resolution_auto:
                        bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_AUTO;
                        break;
                }
                Log.d(TAG, "set rgResolution model bitDepth:" + bitDepth);
            }
        });
        rgCompile = findViewById(R.id.rg_export_config);
        rgCompile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_export_none:
                        exportConfig = Constants.HDR_EXPORT_CONFIG_NONE;
                        break;
                    case R.id.rb_export_2084:
                        exportConfig = Constants.HDR_EXPORT_CONFIG_2084;
                        break;
                    case R.id.rb_export_hlg:
                        exportConfig = Constants.HDR_EXPORT_CONFIG_HLG;
                        break;
                    case R.id.rb_export_hdr_10_plus:
                        exportConfig = Constants.HDR_EXPORT_CONFIG_HDR10PLUS;
                        break;
                }
                Log.d(TAG, "set rgCompile model compileConfig:" + exportConfig);
            }
        });
        hevcExport = findViewById(R.id.compile_hevc);
        hevcExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                supportHevc = b;
                rgCompile.setVisibility(supportHevc ? View.VISIBLE : View.GONE);
                Log.d(TAG, "set rgCompile support HEVC:" + b);
            }
        });
        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        int engineHDRCaps = 0;
        if (instance != null) {
            engineHDRCaps = instance.getEngineHDRCaps();
            findViewById(R.id.hdr_setting).setVisibility(engineHDRCaps > 2 ? View.VISIBLE : View.GONE);
            if (engineHDRCaps > 2) {
                rgResolution.setVisibility(View.VISIBLE);
            }
            if (engineHDRCaps > 4) {
                findViewById(R.id.compile_hdr).setVisibility(View.VISIBLE);
            }
            if (engineHDRCaps > 8) {
                rgLiveWindow.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.setting);
        mTitleBar.setMainLayoutResource(R.color.white);
        mTitleBar.setBackImageIcon(R.drawable.main_webview_back);
        mTitleBar.setTextCenterColor(ContextCompat.getColor(SettingActivity.this, R.color.ff333333));
    }

    @Override
    public void initData() {
        ParameterSettingValues parameterValues = ParameterSettingValues.instance();
        mCaptureResolutionGrade = parameterValues.getCaptureResolutionGrade();
        mSingleBufferMode = parameterValues.isSingleBufferMode();
        mCompileVidoeRes = parameterValues.getCompileVideoRes();
        mCompileBitrate = parameterValues.getCompileBitrate();
        mIsUseBackgroundBlur = parameterValues.isUseBackgroudBlur();
        mIsUseDefaultAr = parameterValues.isDefaultArScene();
        mDisableDeviceEncoderSupport = parameterValues.disableDeviceEncorder();
        mIsQuickPack = parameterValues.isQuickPack();
        mIsStartNativeCamera = parameterValues.isStartNativeCamera();
        colorGain = parameterValues.getColorGain();
        magicProgress.setProgress((int) ((colorGain - 1) * 10));
        bitDepth = parameterValues.getBitDepth();
        if (mSingleBufferMode){
            mCaptureCache.check(R.id.capture_cache_single);
        }else{
            mCaptureCache.check(R.id.capture_cache_double);
        }
        switch (bitDepth) {
            case NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT:
                rgResolution.check(R.id.rb_resolution_8);
                break;
            case NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_16_BIT_FLOAT:
                rgResolution.check(R.id.rb_resolution_16);
                break;
            case NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_AUTO:
                rgResolution.check(R.id.rb_resolution_auto);
                break;
        }

        exportConfig = parameterValues.getExportConfig();
        if (TextUtils.equals(Constants.HDR_EXPORT_CONFIG_NONE, exportConfig)) {
            rgCompile.check(R.id.rb_export_none);
        } else if (TextUtils.equals(Constants.HDR_EXPORT_CONFIG_2084, exportConfig)) {
            rgCompile.check(R.id.rb_export_2084);
        } else if (TextUtils.equals(Constants.HDR_EXPORT_CONFIG_HLG, exportConfig)) {
            rgCompile.check(R.id.rb_export_hlg);
        } else if (TextUtils.equals(Constants.HDR_EXPORT_CONFIG_HDR10PLUS, exportConfig)) {
            rgCompile.check(R.id.rb_export_hdr_10_plus);
        }

        supportHevc = parameterValues.isSupportHEVC();
        hevcExport.setChecked(supportHevc);

        liveWindowModel = parameterValues.getLiveWindowModel();
        switch (liveWindowModel) {
            case NvsLiveWindow.HDR_DISPLAY_MODE_SDR:
                rgLiveWindow.check(R.id.rb_live_sdr);
                break;
            case NvsLiveWindow.HDR_DISPLAY_MODE_DEPEND_DEVICE:
                rgLiveWindow.check(R.id.rb_live_device);
                break;
            case NvsLiveWindow.HDR_DISPLAY_MODE_TONE_MAP_SDR:
                rgLiveWindow.check(R.id.rb_live_if);
                break;
        }

        if (mCompileBitrate > 0)
            mOutput_bitrate_editText.setText(String.valueOf(mCompileBitrate));
        switch (mCaptureResolutionGrade) {
            case NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH:
                mCapcture_ratio_1080.setChecked(true);
                mCapcture_ratio_720.setChecked(false);
                break;
            case NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_HIGH:
                mCapcture_ratio_1080.setChecked(false);
                mCapcture_ratio_720.setChecked(true);
                break;
            default:
                mCapcture_ratio_1080.setChecked(false);
                mCapcture_ratio_720.setChecked(true);
                break;
        }

        switch (mCompileVidoeRes) {
            case CompileVideoRes_2160:
                mOut_ratio_4k.setChecked(true);
                break;
            case CompileVideoRes_1080:
                mOut_ratio_1080.setChecked(true);
                break;
            case CompileVideoRes_720:
                mOut_ratio_720.setChecked(true);
                break;
            case CompileVideoRes_540:
                mOut_ratio_540.setChecked(true);
                break;
            default:
                mOut_ratio_720.setChecked(true);
                break;
        }

        setEditTextHint(mCompileVidoeRes);
        if (mIsUseBackgroundBlur) {
            mBackgroud_blur.setChecked(true);
        } else {
            mBackgroud_blur.setChecked(false);
        }
        if (mIsUseDefaultAr) {
            mSwitchAr.setChecked(true);
        } else {
            mSwitchAr.setChecked(false);
        }

        if (mDisableDeviceEncoderSupport) {
            mEncoder_support.setChecked(true);
        } else {
            mEncoder_support.setChecked(false);
        }

        if (mIsQuickPack) {
            mQuickPack.setChecked(true);
        } else {
            mQuickPack.setChecked(false);
        }

        if (mIsStartNativeCamera) {
            mStartNativeCamera.setChecked(true);
        } else {
            mStartNativeCamera.setChecked(false);
        }
       /* NvsStreamingContext.SdkVersion sdkVersion = mStreamingContext.getSdkVersion();
        StringBuilder stringBuilder = new StringBuilder("V ");
        stringBuilder.append(sdkVersion.majorVersion);
        stringBuilder.append(".");
        stringBuilder.append(sdkVersion.minorVersion);
        stringBuilder.append(".");
        stringBuilder.append(sdkVersion.revisionNumber);
        mSDKVersion.setText(stringBuilder.toString());*/
        String appVersion = "V" + getVersionName(this);
        mSDKVersion.setText(appVersion);
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                setParameterSettingValues();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
            }
        });

        mCapture_ratio_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.capture_ratio_1080:
                        /*
                         * 拍摄1080P
                         * Shooting 1080P
                         * */
                        mCaptureResolutionGrade = NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH;
                        break;
                    case R.id.capture_ratio_720:
                        /*
                         * 拍摄720
                         * Shooting 720P
                         * */
                        mCaptureResolutionGrade = NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_HIGH;
                        break;
                }
            }
        });

        mCaptureCache.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.capture_cache_single:
                        /*
                         * 单buffer
                         * */
                        mSingleBufferMode = true;
                        break;
                    case R.id.capture_cache_double:
                        /*
                         * 双buffer
                         * */
                        mSingleBufferMode = false;
                        break;
                }
            }
        });

        mOut_ratio_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.output_ratio_4k:
                        /*
                         * 输出4K
                         * 4K output
                         * */
                        mCompileVidoeRes = CompileVideoRes_2160;
                        break;
                    case R.id.output_ratio_1080:
                        /*
                         * 输出1080
                         * Output 1080
                         * */
                        mCompileVidoeRes = CompileVideoRes_1080;
                        break;
                    case R.id.output_ratio_720:
                        /*
                         * 输出720
                         * Output 720
                         * */
                        mCompileVidoeRes = CompileVideoRes_720;
                        break;
                    case R.id.output_ratio_540:
                        /*
                         * 输出480
                         * Output 480
                         * */
                        mCompileVidoeRes = CompileVideoRes_540;
                        break;
                }
                setEditTextHint(mCompileVidoeRes);
            }
        });

        mOutput_bitrate_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                double value = Util.convertToDouble(text, 0);
                if (value < 0 || value > 200) {
                    String inputError = getResources().getString(R.string.input_error);
                    ToastUtil.showToast(SettingActivity.this, inputError);
                    return;
                }
                mCompileBitrate = Util.convertToDouble(text, 0);
            }
        });

        mEncoder_support.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDisableDeviceEncoderSupport = isChecked;
            }
        });

        mQuickPack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsQuickPack = isChecked;
            }
        });
        mStartNativeCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsStartNativeCamera = isChecked;
            }
        });
        mBackgroud_blur.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsUseBackgroundBlur = isChecked;
            }
        });
        mSwitchAr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsUseDefaultAr = isChecked;
            }
        });


        mServiceAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("URL", Constants.SERVICE_AGREEMENT_URL);
                AppManager.getInstance().jumpActivity(SettingActivity.this, MainWebViewActivity.class, bundle);
            }
        });
        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("URL", Constants.PRIVACY_POLICY_URL);
                AppManager.getInstance().jumpActivity(SettingActivity.this, MainWebViewActivity.class, bundle);
            }
        });
    }

    private void setEditTextHint(int compileResolutionGrade) {
        String strHint;
        String[] settingSuggest = getResources().getStringArray(R.array.settingSuggest);
        switch (compileResolutionGrade) {
            case CompileVideoRes_2160:
                strHint = settingSuggest[0];
                break;
            case CompileVideoRes_1080:
                strHint = settingSuggest[1];
                break;
            case CompileVideoRes_720:
                strHint = settingSuggest[2];
                break;
            case CompileVideoRes_540:
                strHint = settingSuggest[3];
                break;
            default:
                strHint = settingSuggest[2];
                break;
        }
        mOutput_bitrate_editText.setHint(strHint);
    }

    @Override
    public void onBackPressed() {
        setParameterSettingValues();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

    }

    private void setParameterSettingValues() {
        ParameterSettingValues values = ParameterSettingValues.instance();
        mSp = SpUtil.getInstance(getApplicationContext());
        values.setCaptureResolutionGrade(mCaptureResolutionGrade);
        values.setSingleBufferMode(mSingleBufferMode);
        values.setCompileVideoRes(mCompileVidoeRes);
        values.setCompileBitrate(mCompileBitrate);
        values.setUseBackgroudBlur(mIsUseBackgroundBlur);
        values.setDefaultArScene(mIsUseDefaultAr);
        values.setDisableDeviceEncorder(mDisableDeviceEncoderSupport);
        values.setQuickPack(mIsQuickPack);
        values.setStartNativeCamera(mIsStartNativeCamera);
        values.setBitDepth(bitDepth);
        values.setSupportHEVC(supportHevc);
        values.setColorGain(colorGain);
        values.setLiveWindowModel(liveWindowModel);
        values.setExportConfig(exportConfig);
        mSp.setObjectToShare(getApplicationContext(), values, Constants.KEY_PARAMTER);
    }
}
