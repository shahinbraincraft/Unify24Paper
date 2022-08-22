package com.meishe.modulemakeupcompose;

import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.modulemakeupcompose.makeup.BeautyFxArgs;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/6/10 上午10:47
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureBeautyEffectUtil {

    private static final String BG_SEG_EFFECT_ATTACH_KEY="BgSegEffect";
    /**
     * 美型id 检索数组
     * Retrieve an array of beauty ids
     */
    private static String[] mShapeIdArray = {
            "Face Mesh Face Size Degree",
            "Face Mesh Nose Width Degree",
            "Face Mesh Eye Size Degree",
            "Face Mesh Eye Corner Stretch Degree"
    };
    private static List<String> mShapeIdList = new ArrayList<>(Arrays.asList(mShapeIdArray));

    public static void setMakeupBeautyArgs(NvsCaptureVideoFx nvsCaptureVideoFx,List<BeautyFxArgs> shape, boolean microFlag, boolean clearFlag) {
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
                        nvsCaptureVideoFx.setBooleanVal(className, true);
                    } else {
                        nvsCaptureVideoFx.setBooleanVal(className, false);
                    }
                    Log.d("=====setMakeup", beautyFxArgs.getClassName() + " setBooleanVal :" + ("1".equals(value)));
                } else {
                    //json 判断是美白A还是美白B
                    if (TextUtils.equals(className, "Beauty Whitening")) {
                        changeBeautyWhiteMode(nvsCaptureVideoFx, beautyFxArgs.getWhiteningLutEnabled() <= 0, false);
                    }
                    if (nvsCaptureVideoFx != null) {
                        if (!TextUtils.isEmpty(beautyFxArgs.getDegreeName())) {
                            nvsCaptureVideoFx.setStringVal(beautyFxArgs.getClassName(), beautyFxArgs.getUuid());
                            nvsCaptureVideoFx.setFloatVal(beautyFxArgs.getDegreeName(), floatValue);
//                            MakeupManager.getInstacne().putMapFx(beautyFxArgs.getDegreeName(), value);
                            Log.d("=====setMakeup|||", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid() + " |" + beautyFxArgs.getDegreeName() + " :" + floatValue);
                        } else {
                            if (microFlag && !TextUtils.isEmpty(beautyFxArgs.getUuid())) {
                                nvsCaptureVideoFx.setStringVal(className, beautyFxArgs.getUuid());
                                Log.d("=====setMakeup", beautyFxArgs.getClassName() + " :" + beautyFxArgs.getUuid());
                            } else {
                                if (TextUtils.isEmpty(className)) {
                                    nvsCaptureVideoFx.setBooleanVal("Advanced Beauty Enable", beautyFxArgs.getAdvancedBeautyEnable() == 1);
                                    nvsCaptureVideoFx.setIntVal("Advanced Beauty Type", beautyFxArgs.getAdvancedBeautyType());
                                    Log.d("=====setMakeup", "Advanced Beauty Enable:" + beautyFxArgs.getAdvancedBeautyEnable() + " :Advanced Beauty Type " + beautyFxArgs.getAdvancedBeautyType());
                                    if (beautyFxArgs.getAdvancedBeautyEnable() == 1) {
                                        nvsCaptureVideoFx.setFloatVal("Advanced Beauty Intensity", floatValue);
                                    }
                                } else {
                                    nvsCaptureVideoFx.setFloatVal(className, (mShapeIdList.contains(className))
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

    private static void changeBeautyWhiteMode(NvsCaptureVideoFx videoEffect, boolean isOpen,
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


    public static void setFilterContent(NvsStreamingContext nvsStreamingContext,List<FilterArgs> filter) {
        if (filter == null) {
            return;
        }
        removeAllFilterFx(nvsStreamingContext);
        for (FilterArgs filterArgs : filter) {
            if (filterArgs == null) {
                continue;
            }
            String packageId = filterArgs.getUuid();
            MakeupManager.getInstacne().putFilterFx(packageId);
            NvsCaptureVideoFx nvsCaptureVideoFx;
            if (filterArgs.getIsBuiltIn() == 1) {
                nvsCaptureVideoFx = nvsStreamingContext.appendBuiltinCaptureVideoFx(packageId);
            } else {
                nvsCaptureVideoFx = nvsStreamingContext.appendPackagedCaptureVideoFx(packageId);
            }
            if (nvsCaptureVideoFx != null) {
                nvsCaptureVideoFx.setFilterIntensity(NumberUtils.parseString2Float(filterArgs.getValue()));
            }
        }
    }

    private static void removeAllFilterFx(NvsStreamingContext nvsStreamingContext) {
        List<Integer> remove_list = new ArrayList<>();
        for (int i = 0; i < nvsStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = nvsStreamingContext.getCaptureVideoFxByIndex(i);
            if (fx == null) {
                continue;
            }
            if (fx.getAttachment(BG_SEG_EFFECT_ATTACH_KEY) != null) {
                boolean isBgSet = (boolean) fx.getAttachment(BG_SEG_EFFECT_ATTACH_KEY);
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
                    if (remove) {
                        remove_list.add(i);
                    }
                }
            }
        }
        if (!remove_list.isEmpty()) {
            //这里倒着删，否则会出现移除错误的问题。
            for (int i = remove_list.size() - 1; i >= 0; i--) {
                nvsStreamingContext.removeCaptureVideoFx(remove_list.get(i));
            }
        }
    }

}
