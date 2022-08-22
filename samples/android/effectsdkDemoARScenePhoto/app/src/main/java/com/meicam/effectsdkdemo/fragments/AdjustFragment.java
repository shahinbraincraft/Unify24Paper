package com.meicam.effectsdkdemo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effectsdkdemo.Constants;
import com.meicam.effectsdkdemo.MainActivity;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.adapter.ColorListAdapter;
import com.meicam.effectsdkdemo.data.ColorTypeItem;
import com.meicam.effectsdkdemo.interfaces.OnSeekBarChangeListenerAbs;
import com.meicam.effectsdkdemo.utils.ScreenUtils;
import com.meicam.effectsdkdemo.view.CenterHorizontalView;
import com.meicam.sdk.NvsRational;

import com.meishe.libait.AiAdjust;
import com.meishe.libait.AitInfo;
import com.meishe.render.IMeisheRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjustFragment extends BaseFragment {


    private final static int[] mImageIds = {R.mipmap.icon_adjust_ai_unselect, R.mipmap.icon_adjust_exposure, R.mipmap.icon_adjust_highlight,
            R.mipmap.icon_adjust_shadow, R.mipmap.icon_adjust_brightness, R.mipmap.icon_adjust_contrast,
            R.mipmap.icon_adjust_blackdot, R.mipmap.icon_adjust_saturation, R.mipmap.icon_adjust_vibrance,
            R.mipmap.icon_adjust_color_temperature, R.mipmap.icon_adjust_tone, R.mipmap.icon_adjust_sharpen,
            R.mipmap.icon_adjust_vignette, R.mipmap.icon_adjust_definition, R.mipmap.icon_adjust_grain_killer};

    private final static String[] mColorTypeNames = {Constants.FX_COLOR_PROPERTY_BASIC, Constants.FX_ADJUST_KEY_EXPOSURE, Constants.FX_ADJUST_KEY_HIGHLIGHT,
            Constants.FX_ADJUST_KEY_SHADOW, Constants.FX_ADJUST_KEY_BRIGHTNESS, Constants.FX_ADJUST_KEY_CONTRAST,
            Constants.FX_ADJUST_KEY_BLACKPOINT, Constants.FX_ADJUST_KEY_SATURATION, Constants.FX_ADJUST_KEY_VIBRANCE,
            Constants.FX_ADJUST_KEY_TEMPERATURE, Constants.FX_ADJUST_KEY_TINT, Constants.FX_SHARPEN_AMOUNT,
            Constants.FX_VIGNETTE_DEGREE, Constants.FX_COLOR_PROPERTY_DEFINITION, Constants.FX_COLOR_PROPERTY_DENOISE};

    private final static String[] mAiAdjustKey = {Constants.FX_ADJUST_KEY_EXPOSURE, Constants.FX_ADJUST_KEY_HIGHLIGHT,
            Constants.FX_ADJUST_KEY_SHADOW, Constants.FX_ADJUST_KEY_BRIGHTNESS, Constants.FX_ADJUST_KEY_CONTRAST,
            Constants.FX_ADJUST_KEY_BLACKPOINT, Constants.FX_ADJUST_KEY_SATURATION, Constants.FX_ADJUST_KEY_VIBRANCE};

    private Map<String, NvsEffect> nvsVideoFxMap = new HashMap<>();

    private SeekBar mColorSeekBar;
    private CenterHorizontalView mColorTypeRv;
    private TextView functionName;

    private ColorListAdapter mColorListAdapter;

    private NvsEffect mCurrentVideoFx;

    private String mCurrentColorType;
    private String mCurrenColorName;


    private AitInfo mAitInfo;

    private IMeisheRender mRenderCoreWrapper;

    private Set<String> mColorFxSet = new HashSet<>();

    private NvsEffectSdkContext mEffectSdkContext;
    private OnEffectAddedListener onEffectAddedListener;

    public AdjustFragment() {
    }

    @SuppressLint("ValidFragment")
    public AdjustFragment(IMeisheRender effectRenderCoreWrapper) {
        this.mRenderCoreWrapper = effectRenderCoreWrapper;
    }

    @Override
    protected int initRootView() {
        mEffectSdkContext = NvsEffectSdkContext.getInstance();
        return R.layout.fragment_adjust;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {
        mColorSeekBar = (SeekBar) findViewById(R.id.colorSeekBar);
        mColorTypeRv = (CenterHorizontalView) findViewById(R.id.colorTypeRv);
        functionName = (TextView) findViewById(R.id.function_name);
        mRootView = findViewById(R.id.layout_root);
    }

    @Override
    protected void onLazyLoad() {
        mCurrenColorName = getResources().getString(R.string.no_use);
        updateClipColorVal();
        initColorTypeRv();
    }

    @Override
    protected void initListener() {
        mColorSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser || isSeekBarThumbAdsorb) {
                    Log.d("mgj", "currentProgess  isSeekBarThumbAdsorb:" + isSeekBarThumbAdsorb);
                    if(isSeekBarThumbAdsorb) {
                        isSeekBarThumbAdsorb = false;
                    }

                    if (mCurrentVideoFx != null && mCurrentColorType != null) {
                        float colorVal = 0;

                        switch (mCurrentColorType) {
                            case Constants.FX_COLOR_PROPERTY_DENOISE:
                            case Constants.FX_COLOR_PROPERTY_DEFINITION:
                                colorVal = getFloatColorVal2(progress);
                                mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_INTENSITY, colorVal);
                                seekBarThumbAdsorb(progress, 0, 3);
                                break;
                            case Constants.FX_VIGNETTE_DEGREE:
                            case Constants.FX_SHARPEN_AMOUNT:
                                colorVal = getFloatColorVal2(progress);
                                mCurrentVideoFx.setFloatVal(mCurrentColorType, colorVal);
                                seekBarThumbAdsorb(progress, 0, 3);
                                break;
                            case Constants.FX_ADJUST_KEY_TINT:
                            case Constants.FX_ADJUST_KEY_TEMPERATURE:
                            case Constants.FX_ADJUST_KEY_EXPOSURE:
                            case Constants.FX_ADJUST_KEY_HIGHLIGHT:
                            case Constants.FX_ADJUST_KEY_SHADOW:
                            case Constants.FX_ADJUST_KEY_BRIGHTNESS:
                            case Constants.FX_ADJUST_KEY_CONTRAST:
                            case Constants.FX_ADJUST_KEY_SATURATION:
                            case Constants.FX_ADJUST_KEY_VIBRANCE:
                                colorVal = get2UnitColorVal(progress);
                                mCurrentVideoFx.setFloatVal(mCurrentColorType, colorVal);
                                seekBarThumbAdsorb(progress, 100, 5);
                                break;
                            case Constants.FX_ADJUST_KEY_BLACKPOINT:
                                colorVal = get2UnitColorVal(progress) * 10;
                                mCurrentVideoFx.setFloatVal(mCurrentColorType, colorVal);
                                seekBarThumbAdsorb(progress, 100, 5);
                                break;
                            case Constants.FX_COLOR_PROPERTY_BASIC:
                                colorVal = getFloatColorVal3(progress);
                                aiUserAdjust(colorVal);
                            default:
                                break;
                        }
                        Log.d("mgj", "mCurrentVideoFx mCurrentColorType:" + mCurrentColorType + "  colorVal" + colorVal + "          progress" + progress);
                        functionName.setText(mCurrenColorName + " " + String.format("%.2f", colorVal));
                    }
                }
            }
        });
    }

    private volatile boolean isSeekBarThumbAdsorb = false;
    private void seekBarThumbAdsorb(int currentProgess, final int baseMark, final int maxDif) {
        int degree = currentProgess - baseMark;
        int absDegree = Math.abs(degree);
        Log.d("mgj", "currentProgess:" + currentProgess+ "  absDegree:" + absDegree + "  baseMark:" + baseMark);
        if(absDegree != 0  && absDegree < maxDif) {
            mColorSeekBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int curProgress = mColorSeekBar.getProgress();
                    Log.d("mgj", "currentProgess  curProgress:" + curProgress);
                    if(curProgress > (baseMark - maxDif) && curProgress < (baseMark + maxDif)) {
                        isSeekBarThumbAdsorb = true;
                        mColorSeekBar.setProgress(baseMark);
                    }
                }
            }, 100);
        }
    }

    private float getFloatColorVal2(int progress) {
        return progress / 100.0f;
    }

    private float getFloatColorVal4(int progress) {
        return progress / 200.0f;
    }

    private float getFloatColorVal3(int progress) {
        return progress / 100.0f;
    }

    private float get2UnitColorVal(int progress) {
        return progress / 100.0f - 1;
    }

    private void updateClipColorVal() {
        String[] tempKey = {Constants.FX_COLOR_PROPERTY_DENOISE, Constants.FX_COLOR_PROPERTY_DEFINITION, Constants.FX_COLOR_PROPERTY_BASIC};
        for (String key: tempKey) {
            if(!nvsVideoFxMap.containsKey(key)) {
                NvsEffect nvsVideoFx = builtNvsFx(key);
                if(nvsVideoFx != null) {
                    nvsVideoFxMap.put(key, nvsVideoFx);
                    if(key == Constants.FX_COLOR_PROPERTY_BASIC) {
                        for (String aiKey: mAiAdjustKey) {
                            nvsVideoFx.setFloatVal(aiKey, 0);
                        }
                    } else {
                        nvsVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_INTENSITY, 0);
                    }
                }
            }
        }

        if(!nvsVideoFxMap.containsKey(Constants.FX_VIGNETTE_DEGREE)) {
            NvsEffect vignetteVideoFx = builtNvsFx(Constants.FX_VIGNETTE);
            if(vignetteVideoFx != null) {
                vignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, 0);
                nvsVideoFxMap.put(Constants.FX_VIGNETTE_DEGREE, vignetteVideoFx);
            }
        }

        if(!nvsVideoFxMap.containsKey(Constants.FX_SHARPEN_AMOUNT)) {
            NvsEffect sharpenVideoFx = builtNvsFx(Constants.FX_SHARPEN);
            if(sharpenVideoFx != null) {
                sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, 0);
                nvsVideoFxMap.put(Constants.FX_SHARPEN_AMOUNT, sharpenVideoFx);
            }
        }

        if(!nvsVideoFxMap.containsKey(Constants.FX_ADJUST_KEY_TINT)) {
            NvsEffect tintVideoFx = builtNvsFx(Constants.FX_ADJUST_KEY_TINT);
            if(tintVideoFx != null) {
                tintVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_TINT, 0);
                tintVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_TEMPERATURE, 0);
                nvsVideoFxMap.put(Constants.FX_ADJUST_KEY_TINT, tintVideoFx);
            }
        }
    }

    private NvsEffect builtNvsFx(String nvsFxName) {

        NvsRational aspectRatio = new NvsRational(9, 16);
        NvsEffect nvsEffect = mEffectSdkContext.createVideoEffect(nvsFxName, aspectRatio);
        mColorFxSet.add(nvsFxName);
        if (onEffectAddedListener != null) {
            onEffectAddedListener.onEffectAdded(nvsEffect);
        }
        return nvsEffect;
    }

    private void initColorTypeRv() {
        List<ColorTypeItem> colorTypeItems = new ArrayList<>();
        String[] adjustChildMenu = getResources().getStringArray(R.array.adjust_child_menu);
        for (int i = 0; i < adjustChildMenu.length; i++) {
            ColorTypeItem colorTypeItem = new ColorTypeItem();
            colorTypeItem.setColorTypeName(mColorTypeNames[i]);
            colorTypeItem.setColorAtrubuteText(adjustChildMenu[i]);
            colorTypeItem.setmImageId(mImageIds[i]);
            colorTypeItems.add(colorTypeItem);
        }
        
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mColorTypeRv.setLayoutManager(linearLayoutManager);
        mColorListAdapter = new ColorListAdapter(mActivity, colorTypeItems);
        mColorTypeRv.setAdapter(mColorListAdapter);

        mColorListAdapter.setOnItemClickListener(new ColorListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ColorTypeItem colorTypeItem) {
                if (colorTypeItem == null || colorTypeItem.getColorTypeName() == null) {
                    return;
                }
                if(view != null) {
                    int centerX = ScreenUtils.getScreenWidth(mActivity) / 2;
                    int[] locationView = new int[2];
                    view.getLocationOnScreen(locationView);
                    int viewWidth = view.getWidth();
                    int distance = locationView[0] - centerX + viewWidth / 2;
                    mColorTypeRv.smoothScrollBy(distance, 0);
                }

                mCurrenColorName = colorTypeItem.getColorAtrubuteText();

                double colorVal = 0;

                mCurrentColorType = colorTypeItem.getColorTypeName();
                int scale = 1;
                switch (mCurrentColorType) {
                    case Constants.FX_COLOR_PROPERTY_DENOISE:
                    case Constants.FX_COLOR_PROPERTY_DEFINITION:
                        mCurrentVideoFx = nvsVideoFxMap.get(mCurrentColorType);
                        if(mCurrentColorType != null) {
                            mColorSeekBar.setMax(100);
                            colorVal = mCurrentVideoFx.getFloatVal(Constants.FX_ADJUST_KEY_INTENSITY);
                            mColorSeekBar.setProgress((int) (colorVal * 100));
                        }
                        break;
                    case Constants.FX_SHARPEN_AMOUNT:
                        scale = 2;
                    case Constants.FX_VIGNETTE_DEGREE:
                        mCurrentVideoFx = nvsVideoFxMap.get(mCurrentColorType);
                        if(mCurrentColorType != null) {
                            mColorSeekBar.setMax(100 * scale);
                            colorVal = mCurrentVideoFx.getFloatVal(mCurrentColorType);
                            mColorSeekBar.setProgress((int) (colorVal * 100 * scale));
                        }
                        break;
                    case Constants.FX_ADJUST_KEY_TINT:
                    case Constants.FX_ADJUST_KEY_TEMPERATURE:
                        mCurrentVideoFx = nvsVideoFxMap.get(Constants.FX_ADJUST_KEY_TINT);
                        if(mCurrentColorType != null) {
                            mColorSeekBar.setMax(200);
                            colorVal = mCurrentVideoFx.getFloatVal(mCurrentColorType);
                            mColorSeekBar.setProgress((int) ((colorVal + 1) * 100));
                        }
                        break;
                    case Constants.FX_ADJUST_KEY_EXPOSURE:
                    case Constants.FX_ADJUST_KEY_HIGHLIGHT:
                    case Constants.FX_ADJUST_KEY_SHADOW:
                    case Constants.FX_ADJUST_KEY_BRIGHTNESS:
                    case Constants.FX_ADJUST_KEY_CONTRAST:
                    case Constants.FX_ADJUST_KEY_SATURATION:
                    case Constants.FX_ADJUST_KEY_VIBRANCE:
                        mCurrentVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_BASIC);
                        if(mCurrentVideoFx != null && mCurrentColorType != null) {
                            mColorSeekBar.setMax(200);
                            colorVal = mCurrentVideoFx.getFloatVal(mCurrentColorType);
                            mColorSeekBar.setProgress((int) ((colorVal + 1) * 100));
                        }
                        break;
                    case Constants.FX_ADJUST_KEY_BLACKPOINT:
                        mCurrentVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_BASIC);
                        if(mCurrentVideoFx != null && mCurrentColorType != null) {
                            mColorSeekBar.setMax(200);
                            colorVal = mCurrentVideoFx.getFloatVal(mCurrentColorType);
                            mColorSeekBar.setProgress((int) ((colorVal / 10 + 1) * 100));
                        }
                        break;
                    default:
                        break;
                }

                if(mCurrentColorType == Constants.FX_COLOR_PROPERTY_BASIC) {
                    if(NvsEffectSdkContext.functionalityAuthorised("smartCC")) {
                        aiAdjust(colorTypeItem.isSelected());
                        String noUseTip = getResources().getString(R.string.no_use);
                        functionName.setText(colorTypeItem.isSelected() ? mCurrenColorName : noUseTip);
                    } else {
                        Toast.makeText(getActivity(), "未授权", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mColorSeekBar.setVisibility(View.VISIBLE);
                    functionName.setText(mCurrenColorName + " " + String.format("%.2f", colorVal));
                }

                Log.d("mgj", "onItemClick mCurrenColorName:" + mCurrenColorName + "  colorVal" + colorVal);

            }
        });
    }

    private void aiAdjust(boolean isSelectAiAdjust) {
        if(!isSelectAiAdjust) {
            mCurrentVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_BASIC);
            NvsEffect definitionVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_DEFINITION);
            NvsEffect sharpenVideoFx = nvsVideoFxMap.get(Constants.FX_SHARPEN_AMOUNT);
            if(mCurrentVideoFx == null || definitionVideoFx == null || sharpenVideoFx == null) {
                return;
            }
            for (String key: mAiAdjustKey) {
                mCurrentVideoFx.setFloatVal(key, 0);
            }
            definitionVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_INTENSITY, 0);
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, 0);
            mColorSeekBar.setVisibility(View.INVISIBLE);
            return;
        }
        if(mAitInfo == null) {
            ((MainActivity) listener).captureForAdjust();
            return;
        } else {
            mColorSeekBar.setVisibility(View.VISIBLE);
            mColorSeekBar.setMax(500);
            mColorSeekBar.setProgress(100);
            applyAiColorAdjust(mAitInfo);
        }
    }

    public void updateAitInfoAndView(Bitmap bitmap) {
        mAitInfo = AiAdjust.getAitInfoFromBitmap(bitmap);
        Log.d("TAG", "mAitInfo:" + mAitInfo);
        if (mAitInfo != null) {
            applyAiColorAdjust(mAitInfo);
            mColorSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    mColorSeekBar.setVisibility(View.VISIBLE);
                    mColorSeekBar.setMax(500);
                    mColorSeekBar.setProgress(100);
                }
            });
        }
    }

    private void applyAiColorAdjust(AitInfo aitInfo) {
        if (aitInfo == null) {
            Log.e("meicam", "aitInfo is null");
            return;
        }
        mCurrentVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_BASIC);
        NvsEffect definitionVideoFx = nvsVideoFxMap.get(Constants.FX_COLOR_PROPERTY_DEFINITION);
        NvsEffect sharpenVideoFx = nvsVideoFxMap.get(Constants.FX_SHARPEN_AMOUNT);
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_EXPOSURE, aitInfo.getExposure());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_HIGHLIGHT, aitInfo.getHighlight());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_SHADOW, aitInfo.getShadow());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_BRIGHTNESS, aitInfo.getBrightness());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_CONTRAST, aitInfo.getContrast());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_SATURATION, aitInfo.getSaturation());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_VIBRANCE, aitInfo.getVibrance());
        mCurrentVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_BLACKPOINT, aitInfo.getBlackPoint());
        //设置清晰度
        if(definitionVideoFx != null) {
            definitionVideoFx.setFloatVal(Constants.FX_ADJUST_KEY_INTENSITY, aitInfo.getDefinition());
        }
        //设置锐化
        if(sharpenVideoFx != null) {
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, aitInfo.getSharpness());
        }
    }

    private void aiUserAdjust(float colorVal) {
        if(mAitInfo == null) {
            return;
        }
        AitInfo tempInfo = AiAdjust.getAitInfoFromUser(mAitInfo, colorVal);
        applyAiColorAdjust(tempInfo);
    }


    public void changeVisible(int visible){
        if (mRootView!=null){
            mRootView.setVisibility(visible);
        }
    }
    public void setOnEffectAddedListener(OnEffectAddedListener onEffectAddedListener){
        this.onEffectAddedListener = onEffectAddedListener;
    }

    public interface OnEffectAddedListener{
        public void onEffectAdded(NvsEffect nvsEffect);
    }
}
