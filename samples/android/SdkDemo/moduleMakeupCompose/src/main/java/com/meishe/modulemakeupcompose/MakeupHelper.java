package com.meishe.modulemakeupcompose;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meishe.modulemakeupcompose.makeup.ColorData;
import com.meishe.modulemakeupcompose.makeup.FilterArgs;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupData;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/6/9 上午10:15
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MakeupHelper {

    /*默认单状的强度*/
    public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private Makeup mCaptureMakeupInfo;
    private Makeup mMakeupInfo;

    private static class Helper {
        private static final MakeupHelper instance = new MakeupHelper();
    }

    public static MakeupHelper getInstance() {
        return Helper.instance;
    }


    /**
     * 应用妆容特效
     *
     * @param context
     * @param nvsVideoClip    视频片段
     * @param nvsVideoFx      视频特效
     * @param makeupEffectDir 妆容特效路径
     */
    public void applyMakeupEffect(Context context, NvsVideoClip nvsVideoClip,
                                  NvsVideoFx nvsVideoFx, File makeupEffectDir) throws FileNotFoundException {

        if (makeupEffectDir.exists()) {
            NvsStreamingContext nvsStreamingContext = NvsStreamingContext.getInstance();
            if (nvsStreamingContext == null) {
                return;
            }

            NvsAssetPackageManager assetPackageManager = nvsStreamingContext.getAssetPackageManager();
            if (assetPackageManager == null) {
                return;
            }

            mMakeupInfo = parseAndInstallEffects(makeupEffectDir, assetPackageManager);
            addMakeUpEffect(context, nvsVideoClip, nvsVideoFx, mMakeupInfo);
        } else {
            mMakeupInfo = null;
        }

    }


    public void applyCaptureMakeupEffect(
            NvsCaptureVideoFx nvsCaptureVideoFx, File makeupEffectDir) throws FileNotFoundException {

        if (makeupEffectDir.exists()) {
            NvsStreamingContext nvsStreamingContext = NvsStreamingContext.getInstance();
            if (nvsStreamingContext == null) {
                return;
            }

            NvsAssetPackageManager assetPackageManager = nvsStreamingContext.getAssetPackageManager();
            if (assetPackageManager == null) {
                return;
            }

            mCaptureMakeupInfo = parseAndInstallEffects(makeupEffectDir, assetPackageManager);
            addCaptureMakeUpEffect(nvsStreamingContext, nvsCaptureVideoFx, mCaptureMakeupInfo);
        } else {
            mCaptureMakeupInfo = null;
        }

    }


    /**
     * 解析并安装特效
     *
     * @param makeupEffectDir
     * @param nvsAssetPackageManager
     */
    private Makeup parseAndInstallEffects(File makeupEffectDir,
                                          NvsAssetPackageManager nvsAssetPackageManager) {
        if (nvsAssetPackageManager == null) {
            return null;
        }
        Makeup makeupInfo = null;
        if (makeupEffectDir != null) {
            File[] files = makeupEffectDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file == null) {
                    continue;
                }
                String name = file.getName();
                if (name.equals("info.json")) {
                    String infoJsonFilePath = makeupEffectDir.getAbsolutePath() + File.separator + name;
                    String infoJsonStr = ParseJsonFile.readSdCardJsonFile(infoJsonFilePath);
                    //得到数据bean
                    makeupInfo = ParseJsonFile.fromJson(infoJsonStr, Makeup.class);
                } else {
                    String infoJsonFilePath = makeupEffectDir.getAbsolutePath()
                            + File.separator + name;
                    String parseFileName = parseFileName(name);
                    if (!TextUtils.isEmpty(parseFileName)) {
                        String licPath = makeupEffectDir.getAbsolutePath()
                                + File.separator + parseFileName + ".lic";
                        installNewMakeup(nvsAssetPackageManager, infoJsonFilePath, licPath);
                    }
                }
            }
        }

        return makeupInfo;
    }

    /**
     * 特效安装
     *
     * @param nvsAssetPackageManager
     * @param filePath               特效文件
     * @param licPath                授权文件
     */
    private void installNewMakeup(NvsAssetPackageManager
                                          nvsAssetPackageManager,
                                  String filePath,
                                  String licPath) {
        StringBuilder uuid = new StringBuilder();
        if (filePath.endsWith(".makeup")) {
            int makeupResult = nvsAssetPackageManager.installAssetPackage(
                    filePath, licPath,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP, true, uuid
            );
        } else if (filePath.endsWith(".warp")) {
            int warpResult = nvsAssetPackageManager.installAssetPackage(
                    filePath, licPath,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP, true, uuid
            );
        } else if (filePath.endsWith(".facemesh")) {
            int facemeshResult = nvsAssetPackageManager.installAssetPackage(
                    filePath, licPath,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH, true, uuid
            );
        } else if (filePath.endsWith(".videofx")) {
            int filterResult = nvsAssetPackageManager.installAssetPackage(
                    filePath, licPath,
                    NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, uuid
            );
        }
    }


    /**
     * 添加美状特效
     *
     * @param context
     * @param nvsVideoClip
     * @param nvsVideoFx
     * @param makeupInfo
     */
    private void addMakeUpEffect(Context context,
                                 NvsVideoClip nvsVideoClip,
                                 NvsVideoFx nvsVideoFx,
                                 Makeup makeupInfo) {
        if (makeupInfo == null) {
            return;
        }
        MakeupEffectContent makeupEffectContent = makeupInfo.getEffectContent();
        if (makeupEffectContent == null) {
            return;
        }
        Log.d("=====", "start set makeUp ");
        //添加效果包中带的美颜
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getBeauty(), false);
        //添加效果包中带的美型
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getShape(), false);
        //添加效果包中带的微整形
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.getMicroShape(), true);
        //添加效果包中带的滤镜
        List<FilterArgs> filter = makeupEffectContent.getFilter();
        BeautyEffectUtil.setFilterContent(nvsVideoClip, filter);
        //添加美妆
        List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
        if (makeupArgs != null) {
            for (MakeupArgs args : makeupArgs) {
                if (args == null) {
                    continue;
                }
                /*记录妆容里边应用的所有的单妆*/
                MakeupData makeupData = new MakeupData(-1, DEFAULT_MAKEUP_INTENSITY, new ColorData());
                makeupData.setUuid(args.getUuid());
                MakeupManager.getInstacne().addMakeupEffect(args.getType(), makeupData);

                if (nvsVideoFx != null) {
                    nvsVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
                    nvsVideoFx.setColorVal("Makeup " + args.getType() + " Color", new NvsColor(0, 0, 0, 0));
                    nvsVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
                    nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
                    Log.d("=====", "className:" + args.getClassName() + " value:" + args.getUuid());
                }
            }
        }
        Log.d("=====", "end set makeUp ");

    }


    private void addCaptureMakeUpEffect(
            NvsStreamingContext nvsStreamingContext,
            NvsCaptureVideoFx nvsCaptureVideoFx,
            Makeup makeupInfo) {
        if (makeupInfo == null) {
            return;
        }
        MakeupEffectContent makeupEffectContent = makeupInfo.getEffectContent();
        if (makeupEffectContent == null) {
            return;
        }
        Log.d("=====", "start set makeUp ");

        //添加效果包中带的美颜
        CaptureBeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.getBeauty(), false, false);
        //添加效果包中带的美型
        CaptureBeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.getShape(), false, false);
        //添加效果包中带的微整形
        CaptureBeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.getMicroShape(), true, false);
        //添加效果包中带的滤镜
        List<FilterArgs> filter = makeupEffectContent.getFilter();
        CaptureBeautyEffectUtil.setFilterContent(nvsStreamingContext, filter);
        //添加美妆
        List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
        if (makeupArgs != null) {
            for (MakeupArgs args : makeupArgs) {
                if (args == null) {
                    continue;
                }
                MakeupData makeupData = new MakeupData(-1, DEFAULT_MAKEUP_INTENSITY, new ColorData());
                makeupData.setUuid(args.getUuid());
                MakeupManager.getInstacne().addMakeupEffect(args.getType(), makeupData);
                if (nvsCaptureVideoFx != null) {
                    nvsCaptureVideoFx.setIntVal("Makeup Custom Enabled Flag", NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL);
//                    NvsColor nvsColor = new NvsColor(0, 0, 0, 0);
//                        mArSceneFaceEffect.setColorVal("Makeup " + args.getType() + " Color", nvsColor);
                    nvsCaptureVideoFx.setFloatVal("Makeup " + args.getType() + " Intensity", args.getValue());
                    nvsCaptureVideoFx.setStringVal(args.getClassName(), args.getUuid());
                }
            }
        }

        Log.d("=====", "end set makeUp ");

    }


    private void setMakeupEffect(
            MakeupEffectContent makeupEffectContent,
            NvsFx nvsVideoFx
    ) {
        //添加美妆
        List<MakeupArgs> makeupArgs = makeupEffectContent.getMakeupArgs();
        if (makeupArgs != null) {
            for (MakeupArgs args : makeupArgs) {
                if (args == null) {
                    continue;
                }
                if (nvsVideoFx != null) {
                    nvsVideoFx.setIntVal(
                            "Makeup Custom Enabled Flag",
                            NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL
                    );
                    nvsVideoFx.setColorVal(
                            "Makeup " + args.getType().toString() + " Color",
                            new NvsColor(0f, 0f, 0f, 0f)
                    );
                    nvsVideoFx.setFloatVal(
                            "Makeup " + args.getType().toString() + " Intensity",
                            args.getValue()
                    );
                    nvsVideoFx.setStringVal(args.getClassName(), args.getUuid());
                    Log.d(
                            "=====",
                            "className:" + args.getClassName().toString() + " value:" + args.getUuid()
                    );
                }
            }
        }
    }


    private String parseFileName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            String[] split = filename.split("\\.");
            return split[0];
        }
        return null;
    }


    public Makeup getCaptureMakeupInfo() {
        return mCaptureMakeupInfo;
    }

    public Makeup getMakeupInfo() {
        return mMakeupInfo;
    }

    public void clear() {
        mCaptureMakeupInfo = null;
        mMakeupInfo = null;
    }
}
