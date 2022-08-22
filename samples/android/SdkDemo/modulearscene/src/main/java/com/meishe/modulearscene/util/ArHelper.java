package com.meishe.modulearscene.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.modulearscene.R;
import com.meishe.modulearscene.bean.AdJustBean;
import com.meishe.modulearscene.bean.ArBean;
import com.meishe.modulearscene.bean.BeautyBean;
import com.meishe.modulearscene.bean.DegreasingInfo;
import com.meishe.modulearscene.bean.MicroShapeBean;
import com.meishe.modulearscene.bean.PointBean;
import com.meishe.modulearscene.bean.ShapeBean;
import com.meishe.modulearscene.bean.SharpenBean;
import com.meishe.modulearscene.bean.StrengthBean;
import com.meishe.modulearscene.bean.WhiteningBean;
import com.meishe.modulearscene.inter.IArHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class ArHelper implements IArHelper {
    public static final String TAG = "ArHelper";
    List<ArBean> mCurrentUseDataList = new ArrayList();

    @Override
    public void init() {

    }

    @Override
    public void applyData(NvsFx arSceneFx, ArBean arBean) {
        if (arBean == null || arSceneFx == null) {
            return;
        }
        if (arBean instanceof BeautyBean) {
            applyBeauty(arSceneFx, (BeautyBean) arBean);
        } else if (arBean instanceof MicroShapeBean) {
            applyMicroShape(arSceneFx, (MicroShapeBean) arBean);
        } else if (arBean instanceof ShapeBean) {
            applyShape(arSceneFx, (ShapeBean) arBean);
        }
    }

    /**
     * 应用微整形
     *
     * @param arSceneFx
     * @param arBean
     */
    private void applyMicroShape(NvsFx arSceneFx, MicroShapeBean arBean) {
        applyShape(arSceneFx, arBean);
    }

    /**
     * 应用美型
     *
     * @param arSceneFx
     * @param arBean
     * @return
     */
    private boolean applyShape(NvsFx arSceneFx, ShapeBean arBean) {
        if (arSceneFx == null || arBean == null || TextUtils.isEmpty(arBean.getArId())) {
            return false;
        }
        if (arBean.isPackageShapeFlag()) {
            //1.安装
            String assetPath = arBean.isWrapFlag() ? arBean.getWarpPath() : arBean.getFaceMeshPath();
            int assetType = arBean.isWrapFlag() ? NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP : NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH;
            StringBuilder sb = new StringBuilder();
            int i = NvsStreamingContext.getInstance().getAssetPackageManager().installAssetPackage(assetPath, null, assetType, false, sb);
            if (i != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR && i != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
                return false;
            }
            arSceneFx.setStringVal(arBean.isWrapFlag() ? arBean.getWarpId() : arBean.getFaceMeshId(), sb.toString());
            arSceneFx.setFloatVal(arBean.isWrapFlag() ? arBean.getWarpDegree() : arBean.getFaceDegree(), arBean.getStrength());
            Log.d("=====", "shape installCode:" + i + " |||" + (arBean.isWrapFlag() ? arBean.getWarpId() : arBean.getFaceMeshId()) + " wrapFlag:" + arBean.isWrapFlag() + " id:" + sb.toString());
        } else {
            arSceneFx.setFloatVal(arBean.getArId(), arBean.getStrength());
        }
        return true;
    }

    private void applyBeauty(NvsFx mArSceneFaceEffect, BeautyBean arBean) {
        Log.d(TAG, "==|==applyBeauty" + arBean.getStrength());
        if (null == arBean) {
            return;
        }
        if (!mCurrentUseDataList.contains(arBean)) {
            mCurrentUseDataList.add(arBean);
        }
        Log.d(TAG, arBean.getName() + "====" + arBean.getArId() + "---" + arBean.getStrength());
        //美白
        if (arBean instanceof WhiteningBean) {
            if (((WhiteningBean) arBean).getWhiteningType() == 0) {
                //美白A
                double strength = arBean.getStrength();
                if (strength > 0) {
                    //美白A
                    changeBeautyWhiteMode(mArSceneFaceEffect, true, true);
                } else {
                    mArSceneFaceEffect.setStringVal("Default Beauty Lut File", "");
                    mArSceneFaceEffect.setStringVal("Whitening Lut File", "");
                    mArSceneFaceEffect.setBooleanVal("Whitening Lut Enabled", false);
                }
                mArSceneFaceEffect.setFloatVal(arBean.getArId(), strength);
            } else {

                double strength = arBean.getStrength();
                if (strength > 0) {
                    //美白B
                    changeBeautyWhiteMode(mArSceneFaceEffect, false, true);
                } else {
                    mArSceneFaceEffect.setStringVal("Default Beauty Lut File", "");
                    mArSceneFaceEffect.setStringVal("Whitening Lut File", "");
                    mArSceneFaceEffect.setBooleanVal("Whitening Lut Enabled", false);
                }
                mArSceneFaceEffect.setFloatVal(arBean.getArId(), strength);
            }
        } else if (arBean instanceof StrengthBean) {
            //磨皮
            if (((StrengthBean) arBean).isAdvancedBeauty()) {
                mArSceneFaceEffect.setFloatVal("Advanced Beauty Intensity", arBean.getStrength());
            } else {
                mArSceneFaceEffect.setIntVal("Advanced Beauty Type", ((StrengthBean) arBean).getAdvancedBeautyType());
                mArSceneFaceEffect.setFloatVal("Beauty Strength", arBean.getStrength());
            }
        } else if (arBean instanceof SharpenBean) {
            //锐度
            mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", arBean.isOpenEnable());
        } else if (arBean instanceof AdJustBean) {
            //校色 此时非ArScene是校色packageId
            mArSceneFaceEffect.setFilterIntensity((float) arBean.getStrength());
        } else if (arBean instanceof DegreasingInfo){
            mArSceneFaceEffect.setFloatVal(arBean.getArId(),arBean.getStrength());
            mArSceneFaceEffect.setFloatVal(((DegreasingInfo) arBean).getArSubId(),((DegreasingInfo) arBean).getSubStrength());
            Log.e("lpf","arBean.getArId()="+arBean.getArId()+" arBean.getStrength()="+arBean.getStrength());
            Log.e("lpf","arBean.getArSubId()="+ ((DegreasingInfo) arBean).getArSubId()+" arBean.getSubStrength()="+ ((DegreasingInfo) arBean).getSubStrength());
        }else{
            //红润
            mArSceneFaceEffect.setFloatVal(arBean.getArId(), arBean.getStrength());
        }
    }

    private void changeBeautyWhiteMode(NvsFx videoEffect, boolean isOpen, boolean isExchange) {
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

    @Override
    public List<ArBean> getBeautyData(Context context) {
        List<ArBean> beautyList = new ArrayList<>();
        /*
         * 磨皮
         * strength
         * */
        StrengthBean strength = new StrengthBean();
        strength.setResId(R.mipmap.ic_strength);
        strength.setName(context.getResources().getString(R.string.strength));
        beautyList.add(strength);


        /*
         * 美白
         * whitening
         * */
        WhiteningBean beauty_whitening = new WhiteningBean();
        beauty_whitening.setName(context.getResources().getString(R.string.whitening_B));
        beauty_whitening.setResId(R.drawable.beauty_white_selector);
        beauty_whitening.setArId("Beauty Whitening");
        beautyList.add(beauty_whitening);


        /*
         * 去油光
         * reddening
         * */
        DegreasingInfo beauty_quyouguang = new DegreasingInfo();
        beauty_quyouguang.setName(context.getResources().getString(R.string.quyouguang));
        beauty_quyouguang.setResId(R.drawable.beauty_quyouguang_selector);
        beauty_quyouguang.setArId("Advanced Beauty Matte Intensity");
        beauty_quyouguang.setArSubId("Advanced Beauty Matte Fill Radius");
        beauty_quyouguang.setDefaultSubStrength(44.44);
        beauty_quyouguang.setSubStrength(44.44);
        beautyList.add(beauty_quyouguang);

        /*
         * 红润
         * reddening
         * */
        BeautyBean beauty_reddening = new BeautyBean();
        beauty_reddening.setName(context.getResources().getString(R.string.ruddy));
        beauty_reddening.setResId(R.drawable.beauty_reddening_selector);
        beauty_reddening.setArId("Beauty Reddening");
        beautyList.add(beauty_reddening);
        /*
         * 校色
         * School color
         * */
        AdJustBean adjustColor = new AdJustBean();
        adjustColor.setName(context.getResources().getString(R.string.correctionColor));
        adjustColor.setResId(R.drawable.beauty_adjust_selector);
        adjustColor.setAdjustPath("assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx");
        beautyList.add(adjustColor);
        /*
         * 锐度
         * sharpness
         * */
        SharpenBean sharpen = new SharpenBean();
        sharpen.setName(context.getResources().getString(R.string.sharpness));
        sharpen.setResId(R.drawable.beauty_sharpen_selector);
        sharpen.setArId("Default Sharpen Enabled");
        beautyList.add(sharpen);
        return beautyList;
    }

    @Override
    public List<ArBean> getShapeData(Context context) {
        List<ArBean> shapeList = new ArrayList<>();
        /*
         * 窄脸
         * face width
         * */
        ShapeBean intensity_zhailian = new ShapeBean();
        intensity_zhailian.setName(context.getResources().getString(R.string.face_thin));
        intensity_zhailian.setResId(R.drawable.beauty_narrow_face_selector);
        intensity_zhailian.setArId("Face Mesh Face Width Degree");
//        intensity_zhailian.type = "Custom";
        intensity_zhailian.setFaceMeshPath("assets:/beauty/shapePackage/96550C89-A5B8-42F0-9865-E07263D0B20C.1.facemesh");
        intensity_zhailian.setFaceMeshId("Face Mesh Face Width Custom Package Id");
        intensity_zhailian.setFaceUUID("96550C89-A5B8-42F0-9865-E07263D0B20C");
        intensity_zhailian.setFaceDegree("Face Mesh Face Width Degree");
        shapeList.add(intensity_zhailian);
//        one.setFaceMeshPath("assets:/beauty/shape/");
//        one.setFaceMeshId("Face Mesh Face Length Custom Package Id");
//        one.setFaceDegree("Face Mesh Face Width Degree");
//        one.setFaceUUID("");
//        one.setWarpPath("assets:/beauty/shape/");
//        one.setWarpId("Warp Face Width Custom Package Id");
//        one.setWarpUUID("");
//        one.setWarpDegree("Face Width Warp Degree");
        /*
         * 小脸
         * face length
         * */
        ShapeBean intensity_xiaolian = new ShapeBean();
        intensity_xiaolian.setName(context.getResources().getString(R.string.face_small));
        intensity_xiaolian.setResId(R.drawable.beauty_little_face_selector);
        intensity_xiaolian.setArId("Face Mesh Face Length Degree");
        intensity_xiaolian.setFaceMeshPath("assets:/beauty/shapePackage/B85D1520-C60F-4B24-A7B7-6FEB0E737F15.1.facemesh");
        intensity_xiaolian.setFaceMeshId("Face Mesh Face Length Custom Package Id");
        intensity_xiaolian.setFaceUUID("B85D1520-C60F-4B24-A7B7-6FEB0E737F15");
        intensity_xiaolian.setFaceDegree("Face Mesh Face Length Degree");
        shapeList.add(intensity_xiaolian);

        /*
         * 瘦脸
         * Thin face
         * */
        ShapeBean cheek_thinning = new ShapeBean();
        cheek_thinning.setName(context.getResources().getString(R.string.cheek_thinning));
        cheek_thinning.setResId(R.drawable.beauty_thin_face_selector);
        cheek_thinning.setArId("Face Mesh Face Size Degree");
        cheek_thinning.setFaceMeshPath("assets:/beauty/shapePackage/63BD3F32-D01B-4755-92D5-0DE361E4045A.1.facemesh");
        cheek_thinning.setFaceMeshId("Face Mesh Face Size Custom Package Id");
        cheek_thinning.setFaceUUID("63BD3F32-D01B-4755-92D5-0DE361E4045A");
        cheek_thinning.setFaceDegree("Face Mesh Face Size Degree");
        shapeList.add(cheek_thinning);

        /*
         * 点
         * */
        PointBean point = new PointBean();
        point.setName(context.getResources().getString(R.string.blackPoint));
        shapeList.add(point);

        /*
         * 额头
         * forehead
         * */
        ShapeBean intensity_forehead = new ShapeBean();
        intensity_forehead.setName(context.getResources().getString(R.string.intensity_forehead));
        intensity_forehead.setResId(R.drawable.beauty_forehead_selector);
        intensity_forehead.setArId("Forehead Height Warp Degree");
        intensity_forehead.setWarpPath("assets:/beauty/shapePackage/A351D77A-740D-4A39-B0EA-393643159D99.1.warp");
        intensity_forehead.setWarpId("Warp Forehead Height Custom Package Id");
        intensity_forehead.setWarpUUID("A351D77A-740D-4A39-B0EA-393643159D99");
        intensity_forehead.setWarpDegree("Forehead Height Warp Degree");
        shapeList.add(intensity_forehead);
        /*
         * 下巴
         * Chin
         * */
        ShapeBean intensity_chin = new ShapeBean();
        intensity_chin.setName(context.getResources().getString(R.string.intensity_chin));
        intensity_chin.setResId(R.drawable.beauty_chin_selector);
        intensity_chin.setArId("Face Mesh Chin Length Degree");
        intensity_chin.setFaceMeshPath("assets:/beauty/shapePackage/FF2D36C5-6C91-4750-9648-BD119967FE66.1.facemesh");
        intensity_chin.setFaceMeshId("Face Mesh Chin Length Custom Package Id");
        intensity_chin.setFaceDegree("Face Mesh Chin Length Degree");
        intensity_chin.setFaceUUID("FF2D36C5-6C91-4750-9648-BD119967FE66");
        shapeList.add(intensity_chin);

        /*
         * 点
         * */
        point = new PointBean();
        point.setName(context.getResources().getString(R.string.blackPoint));
        shapeList.add(point);

        // 大眼
        /*
         * 大眼
         * Eye enlarging
         * */
        ShapeBean eye_enlarging = new ShapeBean();
        eye_enlarging.setName(context.getResources().getString(R.string.eye_enlarging));
        eye_enlarging.setFaceMeshId("Face Mesh Eye Size Custom Package Id");
        eye_enlarging.setFaceMeshPath("assets:/beauty/shapePackage/71C4CF51-09D7-4CB0-9C24-5DE9375220AE.1.facemesh");
        eye_enlarging.setWarpId("Wrap Eye Size Custom Package Id");
        eye_enlarging.setFaceUUID("71C4CF51-09D7-4CB0-9C24-5DE9375220AE");
        eye_enlarging.setFaceDegree("Face Mesh Eye Size Degree");
        eye_enlarging.setResId(R.drawable.beauty_big_eye_selector);
        eye_enlarging.setArId("Face Mesh Eye Size Degree");
        shapeList.add(eye_enlarging);

        ShapeBean yanjiao = new ShapeBean();
        yanjiao.setName(context.getResources().getString(R.string.eye_corner));
        yanjiao.setResId(R.drawable.beauty_eye_corner_selector);
        yanjiao.setArId("Face Mesh Eye Corner Stretch Degree");
        yanjiao.setFaceMeshPath("assets:/beauty/shapePackage/54B2B9B4-5A7A-484C-B602-39A4730115A0.1.facemesh");
        yanjiao.setFaceMeshId("Face Mesh Eye Corner Stretch Custom Package Id");
        yanjiao.setFaceDegree("Face Mesh Eye Corner Stretch Degree");
        yanjiao.setFaceUUID("54B2B9B4-5A7A-484C-B602-39A4730115A0");
        shapeList.add(yanjiao);


        /*
         * 点
         * */
        point = new PointBean();
        point.setName(context.getResources().getString(R.string.blackPoint));
        shapeList.add(point);

        // 瘦鼻
        /*
         * 瘦鼻
         * Nose width
         * */
        ShapeBean intensity_nose = new ShapeBean();
        intensity_nose.setName(context.getResources().getString(R.string.intensity_nose));
        intensity_nose.setResId(R.drawable.beauty_thin_nose_selector);
        intensity_nose.setArId("Face Mesh Nose Width Degree");
        intensity_nose.setFaceMeshPath("assets:/beauty/shapePackage/8D676A5F-73BD-472B-9312-B6E1EF313A4C.1.facemesh");
        intensity_nose.setFaceMeshId("Face Mesh Nose Width Custom Package Id");
        intensity_nose.setFaceDegree("Face Mesh Nose Width Degree");
        intensity_nose.setFaceUUID("8D676A5F-73BD-472B-9312-B6E1EF313A4C");
        shapeList.add(intensity_nose);


        ShapeBean changbi_nose = new ShapeBean();
        changbi_nose.setName(context.getResources().getString(R.string.nose_long));
        changbi_nose.setResId(R.drawable.beauty_long_nose_selector);
        changbi_nose.setArId("Face Mesh Nose Length Degree");
        changbi_nose.setFaceMeshPath("assets:/beauty/shapePackage/3632E2FF-8760-4D90-A2B6-FFF09C117F5D.1.facemesh");
        changbi_nose.setFaceMeshId("Face Mesh Nose Length Custom Package Id");
        changbi_nose.setFaceDegree("Face Mesh Nose Length Degree");
        changbi_nose.setFaceUUID("3632E2FF-8760-4D90-A2B6-FFF09C117F5D");
        shapeList.add(changbi_nose);

        /*
         * 点
         * */
        point = new PointBean();
        point.setName(context.getResources().getString(R.string.blackPoint));
        shapeList.add(point);

        /*
         * 嘴形
         * Mouth size
         * */
        ShapeBean intensity_mouth = new ShapeBean();
        intensity_mouth.setName(context.getResources().getString(R.string.intensity_mouth));
        intensity_mouth.setResId(R.drawable.beauty_mouth_selector);
        intensity_mouth.setArId("Face Mesh Mouth Size Degree");
        intensity_mouth.setFaceMeshPath("assets:/beauty/shapePackage/A80CC861-A773-4B8F-9CFA-EE63DB23EEC2.1.facemesh");
        intensity_mouth.setFaceMeshId("Face Mesh Mouth Size Custom Package Id");
        intensity_mouth.setFaceDegree("Face Mesh Mouth Size Degree");
        intensity_mouth.setFaceUUID("A80CC861-A773-4B8F-9CFA-EE63DB23EEC2");
        shapeList.add(intensity_mouth);
        /*
         * 嘴角
         * Mouth corner
         * */
        ShapeBean intensity_zuijiao = new ShapeBean();
        intensity_zuijiao.setName(context.getResources().getString(R.string.mouse_corner));
        intensity_zuijiao.setResId(R.drawable.beauty_mouth_corner_selector);
        intensity_zuijiao.setArId("Face Mesh Mouth Corner Lift Degree");
        intensity_zuijiao.setFaceMeshPath("assets:/beauty/shapePackage/CD69D158-9023-4042-AEAD-F8E9602FADE9.1.facemesh");
        intensity_zuijiao.setFaceMeshId("Face Mesh Mouth Corner Lift Custom Package Id");
        intensity_zuijiao.setFaceDegree("Face Mesh Mouth Corner Lift Degree");
        intensity_zuijiao.setFaceUUID("CD69D158-9023-4042-AEAD-F8E9602FADE9");
        shapeList.add(intensity_zuijiao);
        return shapeList;
    }

    @Override
    public List<ArBean> getMicroShapeData(Context context) {
        List<ArBean> microShapeList = new ArrayList<>();
        /*
         * 缩头（小头）
         * */
        MicroShapeBean headItem = new MicroShapeBean();
        headItem.setName(context.getResources().getString(R.string.head_size));
        headItem.setResId(R.drawable.beauty_shape_head_width_selector);
        headItem.setShapeFlag(true);
        headItem.setArId("Head Size Warp Degree");
        headItem.setWarpPath("assets:/beauty/shapePackage/BE88A090-8CF2-4A5E-8FF5-0A06464CEF8C.1.warp");
        headItem.setWarpId("Warp Head Size Custom Package Id");
        headItem.setWarpUUID("BE88A090-8CF2-4A5E-8FF5-0A06464CEF8C");
        headItem.setWarpDegree("Head Size Warp Degree");
        microShapeList.add(headItem);
//        /*
//         * 颧骨宽
//         * */
        MicroShapeBean malarItem = new MicroShapeBean();
        malarItem.setName(context.getResources().getString(R.string.malar_size));
        malarItem.setResId(R.drawable.beauty_shape_malar_selector);
        malarItem.setShapeFlag(true);
        malarItem.setArId("Face Mesh Malar Width Degree");
        malarItem.setFaceMeshPath("assets:/beauty/shapePackage/C1C83B8B-8086-49AC-8462-209E429C9B7A.1.facemesh");
        malarItem.setFaceMeshId("Face Mesh Malar Width Custom Package Id");
        malarItem.setFaceDegree("Face Mesh Malar Width Degree");
        malarItem.setFaceUUID("C1C83B8B-8086-49AC-8462-209E429C9B7A");
        microShapeList.add(malarItem);
//        /*
//         * 下颌宽
//         * */
        MicroShapeBean jawItem = new MicroShapeBean();
        jawItem.setName(context.getResources().getString(R.string.jaw_size));
        jawItem.setResId(R.drawable.beauty_shape_jaw_width_selector);
        jawItem.setShapeFlag(true);
        jawItem.setArId("Face Mesh Jaw Width Degree");
        jawItem.setFaceMeshPath("assets:/beauty/shapePackage/E903C455-8E23-4539-9195-816009AFE06A.1.facemesh");
        jawItem.setFaceMeshId("Face Mesh Jaw Width Custom Package Id");
        jawItem.setFaceDegree("Face Mesh Jaw Width Degree");
        jawItem.setFaceUUID("E903C455-8E23-4539-9195-816009AFE06A");
        microShapeList.add(jawItem);
//        /*
//         * 太阳穴宽
//         * */
        MicroShapeBean templeItem = new MicroShapeBean();
        templeItem.setName(context.getResources().getString(R.string.temple_width));
        templeItem.setShapeFlag(true);
        templeItem.setResId(R.drawable.beauty_shape_temple_width_selector);
        templeItem.setArId("Face Mesh Temple Width Degree");
        templeItem.setFaceMeshPath("assets:/beauty/shapePackage/E4790833-BB9D-4EFC-86DF-D943BDC48FA4.1.facemesh");
        templeItem.setFaceMeshId("Face Mesh Temple Width Custom Package Id");
        templeItem.setFaceDegree("Face Mesh Temple Width Degree");
        templeItem.setFaceUUID("E4790833-BB9D-4EFC-86DF-D943BDC48FA4");
        microShapeList.add(templeItem);


        /*
         * 法令纹
         *
         * */
        MicroShapeBean beauty_nasolabial = new MicroShapeBean();
        beauty_nasolabial.setName(context.getResources().getString(R.string.beauty_nasolabial));
        beauty_nasolabial.setResId(R.drawable.beauty_nasolabial_selector);
        beauty_nasolabial.setArId("Advanced Beauty Remove Nasolabial Folds Intensity");
        microShapeList.add(beauty_nasolabial);
        /*
         * 黑眼圈
         *
         * */
        MicroShapeBean beauty_dark = new MicroShapeBean();
        beauty_dark.setName(context.getResources().getString(R.string.beauty_dark_circles));
        beauty_dark.setResId(R.drawable.beauty_dark_circles_selector);
        beauty_dark.setArId("Advanced Beauty Remove Dark Circles Intensity");
        microShapeList.add(beauty_dark);
        /*
         * 亮眼
         *
         * */
        MicroShapeBean beauty_brighten = new MicroShapeBean();
        beauty_brighten.setName(context.getResources().getString(R.string.beauty_brighten_eye));
        beauty_brighten.setResId(R.drawable.beauty_bright_eye_selector);
        beauty_brighten.setArId("Advanced Beauty Brighten Eyes Intensity");
        microShapeList.add(beauty_brighten);
        /*
         * 美牙
         *
         * */
        MicroShapeBean beauty_tooth = new MicroShapeBean();
        beauty_tooth.setName(context.getResources().getString(R.string.beauty_tooth));
        beauty_tooth.setResId(R.drawable.beauty_tooth_selector);
        beauty_tooth.setArId("Advanced Beauty Whiten Teeth Intensity");
        microShapeList.add(beauty_tooth);


        /*
         * 眼角距离
         * */
        MicroShapeBean eye_angle = new MicroShapeBean();
        eye_angle.setName(context.getResources().getString(R.string.eye_angel));
        eye_angle.setResId(R.drawable.eye_angel_selector);
        eye_angle.setShapeFlag(true);
        eye_angle.setArId("Face Mesh Eye Angle Degree");
        eye_angle.setFaceMeshPath("assets:/beauty/shapePackage/69D5BADE-A363-4CE0-B269-F146A851932B.1.facemesh");
        eye_angle.setFaceMeshId("Face Mesh Eye Angle Custom Package Id");
        eye_angle.setFaceDegree("Face Mesh Eye Angle Degree");
        eye_angle.setFaceUUID("69D5BADE-A363-4CE0-B269-F146A851932B");
        microShapeList.add(eye_angle);


        /*
         * 眼距
         * */
        MicroShapeBean eye_distance = new MicroShapeBean();
        eye_distance.setName(context.getResources().getString(R.string.eye_distance));
        eye_distance.setResId(R.drawable.eye_distance_selector);
        eye_distance.setShapeFlag(true);
        eye_distance.setArId("Face Mesh Eye Distance Degree");
        eye_distance.setFaceMeshPath("assets:/beauty/shapePackage/80329F14-8BDB-48D1-B30B-89A33438C481.1.facemesh");
        eye_distance.setFaceMeshId("Face Mesh Eye Distance Custom Package Id");
        eye_distance.setFaceDegree("Face Mesh Eye Distance Degree");
        eye_distance.setFaceUUID("80329F14-8BDB-48D1-B30B-89A33438C481");
        microShapeList.add(eye_distance);



        /*
         * 人中
         * */
        MicroShapeBean philtrum_length = new MicroShapeBean();
        philtrum_length.setName(context.getResources().getString(R.string.philtrum_length));
        philtrum_length.setResId(R.drawable.philtrum_selector);
        philtrum_length.setShapeFlag(true);
        philtrum_length.setArId("Face Mesh Philtrum Length Degree");
        philtrum_length.setFaceMeshPath("assets:/beauty/shapePackage/37552044-E743-4A60-AC6E-7AADBA1E5B3B.1.facemesh");
        philtrum_length.setFaceMeshId("Face Mesh Philtrum Length Custom Package Id");
        philtrum_length.setFaceDegree("Face Mesh Philtrum Length Degree");
        philtrum_length.setFaceUUID("37552044-E743-4A60-AC6E-7AADBA1E5B3B");
        microShapeList.add(philtrum_length);


        /*
         * 宽鼻梁
         * */
        MicroShapeBean nose_bridge = new MicroShapeBean();
        nose_bridge.setName(context.getResources().getString(R.string.nose_bridge));
        nose_bridge.setResId(R.drawable.philtrum_selector);
        nose_bridge.setShapeFlag(true);
        nose_bridge.setArId("Face Mesh Nose Bridge Width Degree");
        nose_bridge.setFaceMeshPath("assets:/beauty/shapePackage/23A40970-CE6F-4684-AF57-F78A0CBB53D1.1.facemesh");
        nose_bridge.setFaceMeshId("Face Mesh Nose Bridge Width Custom Package Id");
        nose_bridge.setFaceDegree("Face Mesh Nose Bridge Width Degree");
        nose_bridge.setFaceUUID("23A40970-CE6F-4684-AF57-F78A0CBB53D1");
        microShapeList.add(nose_bridge);

        return microShapeList;
    }

    @Override
    public List<ArBean> getBeautySkinData(Context context) {
        List<ArBean> list = new ArrayList<>();

        /*
         * 磨皮
         * strength
         * */
        StrengthBean beauty_strength = new StrengthBean();
        beauty_strength.setName(context.getResources().getString(R.string.strength_1));
        beauty_strength.setResId(R.drawable.beauty_strength_selector);
        beauty_strength.setAdvancedBeauty(true);
        beauty_strength.setArId("Beauty Strength");
        list.add(beauty_strength);

        /*
         * 高级磨皮1   Advanced Beauty Type  0
         * strength
         * */
        StrengthBean beauty_strength_1 = new StrengthBean();

        beauty_strength_1.setName(context.getResources().getString(R.string.advanced_strength_1));
        beauty_strength_1.setResId(R.drawable.beauty_strength_selector);
        beauty_strength_1.setAdvancedBeauty(true);
        beauty_strength_1.setArId("Advanced Beauty Intensity");
        list.add(beauty_strength_1);

        /*
         * 高级磨皮2   Advanced Beauty Type  1
         * strength
         * */
        StrengthBean beauty_strength_2 = new StrengthBean();
        beauty_strength_2.setName(context.getResources().getString(R.string.advanced_strength_2));
        beauty_strength_2.setResId(R.drawable.beauty_strength_selector);
        beauty_strength_2.setAdvancedBeauty(true);
        beauty_strength_2.setAdvancedBeautyType(1);
        beauty_strength_2.setArId("Advanced Beauty Intensity");
        list.add(beauty_strength_2);

        /*
         * 高级磨皮3   Advanced Beauty Type  2
         * strength
         * */
        StrengthBean beauty_strength_3 = new StrengthBean();
        beauty_strength_3.setName(context.getResources().getString(R.string.advanced_strength_3));
        beauty_strength_3.setResId(R.drawable.beauty_strength_selector);
        beauty_strength_3.setAdvancedBeauty(true);
        beauty_strength_3.setAdvancedBeautyType(2);
        beauty_strength_3.setArId("Advanced Beauty Intensity");
        list.add(beauty_strength_3);

        /*
         * 点
         * */
        PointBean point = new PointBean();
        point.setName(context.getResources().getString(R.string.blackPoint));
        list.add(point);
        return list;
    }
}
