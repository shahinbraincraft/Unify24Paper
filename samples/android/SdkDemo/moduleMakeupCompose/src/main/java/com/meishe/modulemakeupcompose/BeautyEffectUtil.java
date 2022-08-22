package com.meishe.modulemakeupcompose;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.http.bean.CategoryInfo;
import com.meishe.modulemakeupcompose.makeup.BeautyFxArgs;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/5/30 下午12:19
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class BeautyEffectUtil {

    private static final String TAG = "BeautyEffectUtil";

    public static void setFilterContent(NvsVideoClip videoClip, List<FilterArgs> filter) {
        if (videoClip == null || filter == null) {
            return;
        }
//        removeAllFilterFx(nvsVideoFx);
        for (FilterArgs filterArgs : filter) {
            if (filterArgs == null) {
                continue;
            }
            String packageId = filterArgs.getUuid();
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
            MakeupManager.getInstacne().putFilterFx(packageId);
        }
    }

    public static void setCaptureFilterContent(NvsStreamingContext nvsStreamingContext, List<FilterArgs> filter) {
        if (nvsStreamingContext == null) {
            return;
        }
        if (filter == null) {
            return;
        }

        for (FilterArgs filterArgs : filter) {
            if (filterArgs == null) {
                continue;
            }
            String packageId = filterArgs.getUuid();
            NvsCaptureVideoFx nvsCaptureVideoFx;
            if (filterArgs.getIsBuiltIn() == 1) {
                nvsCaptureVideoFx = nvsStreamingContext.appendBuiltinCaptureVideoFx(packageId);
            } else {
                nvsCaptureVideoFx = nvsStreamingContext.appendBuiltinCaptureVideoFx(packageId);
            }
            if (nvsCaptureVideoFx != null) {
                nvsCaptureVideoFx.setFilterIntensity(NumberUtils.parseString2Float(filterArgs.getValue()));
                Log.d(TAG, "videoClip.setFilterContent id:" + packageId + " value:" + filterArgs.getValue());
            }
            MakeupManager.getInstacne().putFilterFx(packageId);
        }
    }



    public static void setMakeupBeautyArgs(NvsVideoFx nvsVideoFx, List<BeautyFxArgs> shape, boolean microFlag) {
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
                MakeupManager.getInstacne().putMapFx(className, value);
            }
        }
    }


    public static void changeBeautyWhiteMode(NvsFx videoEffect, boolean isOpen,
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


    /**
     * 删除单装和妆容
     */
    public static void clearAllCustomMakeup(Context context, NvsFx videoFx) {
        ArrayList<CategoryInfo> mAllMakeupId = getAllMakeupId(context);
        for (CategoryInfo categoryInfo : mAllMakeupId) {
            if (categoryInfo.getMaterialType() == 21) {
                continue;
            }
            resetCustomMakeup(videoFx, upperCaseName(categoryInfo.getDisplayName()));
        }
        MakeupManager.getInstacne().clearCustomData();
    }

    public static void resetCustomMakeup(NvsFx videoFx, String makupId) {
        if ((videoFx == null) || TextUtils.isEmpty(makupId)) {
            return;
        }
        videoFx.setStringVal("Makeup " + makupId + " Package Id", null);
        videoFx.setColorVal("Makeup " + makupId + " Color", new NvsColor(0, 0, 0, 0));
        videoFx.setFloatVal("Makeup " + makupId + " Intensity", 0.6);
    }


    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String upperCaseName(String name) {
        char[] cs = name.toCharArray();
        if ((97 <= cs[0]) && (cs[0] <= 122)) {
            cs[0] ^= 32;
        }
        return String.valueOf(cs);
    }

    private static ArrayList<CategoryInfo> getAllMakeupId(Context context) {
        return MakeupManager.getInstacne().getMakeupTab(context);
    }

    /**
     * 清除美妆中添加的美颜 美型 滤镜特效
     * @param streamingContext
     */
    public static void resetCaptureFilterFx(NvsStreamingContext
                                                    streamingContext) {
        //滤镜
        Set<String> fxSet = MakeupManager.getInstacne().getFilterFxSet();
        if (fxSet != null) {
            for (String fxName : fxSet) {
                int captureVideoFxCount = streamingContext.getCaptureVideoFxCount();
                for (int videoFxCount = captureVideoFxCount - 1; videoFxCount >= 0; videoFxCount--) {
                    NvsCaptureVideoFx nvsVideoFx = streamingContext.
                            getCaptureVideoFxByIndex(videoFxCount);
                    if (nvsVideoFx == null) {
                        continue;
                    }
                    String captureVideoFxPackageId = nvsVideoFx.getCaptureVideoFxPackageId();
                    if (fxName.equals(captureVideoFxPackageId)) {
                        streamingContext.removeCaptureVideoFx(videoFxCount);
                    }
                }
            }
        }
        MakeupManager.getInstacne().clearFilterData();
    }


}
