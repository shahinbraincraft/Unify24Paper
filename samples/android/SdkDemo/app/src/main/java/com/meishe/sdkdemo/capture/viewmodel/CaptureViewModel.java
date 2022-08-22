package com.meishe.sdkdemo.capture.viewmodel;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.Utils;
import com.meishe.http.AssetType;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.bean.voice.ChangeVoiceData;
import com.meishe.sdkdemo.capture.BeautyShapeDataItem;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.repository.AppRepository;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午3:53
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptureViewModel extends ViewModel {


    private MutableLiveData<TypeAndCategoryInfo> mFilterTypeInfo = new MutableLiveData<>();
    private MutableLiveData<TypeAndCategoryInfo> mPropTypeInfo = new MutableLiveData<>();
    private MutableLiveData<TypeAndCategoryInfo> mComponentTypeInfo = new MutableLiveData<>();
    private MutableLiveData<TypeAndCategoryInfo> mStickerTypeInfo = new MutableLiveData<>();

    private AppRepository mAppRepository;

    public CaptureViewModel() {
        this.mAppRepository = AppRepository.AppRepositoryHelper.getInstance();
    }

    /**
     * 得到类别信息
     *
     * @param tag
     */
    public void getEffectTypeData(@NonNull String tag) {
        String type = "";
        if (tag.equals(Constants.FRAGMENT_FILTER_TAG)) {
            type = AssetType.FILTER_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_PROP_TAG)) {
            type = AssetType.AR_SCENE_PRE_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_COMPONENT_CAPTION_TAG)) {
            type = AssetType.COM_CAPTION_ALL.getType();
        } else if (tag.equals(Constants.FRAGMENT_STICKER_TAG)) {
            type = AssetType.STICKER_ALL.getType();
        }
        String finalType = type;
        HttpManager.getMaterialTypeAndCategory(null, type, "", "",
                new RequestCallback<List<TypeAndCategoryInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<TypeAndCategoryInfo>> response) {
                        if (response.getCode() == 1) {
                            if (null != response.getData()) {
                                TypeAndCategoryInfo typeAndCategoryInfo =
                                        response.getData().get(0);
                                CategoryInfo categoryInfo = typeAndCategoryInfo.getCategories().get(0);

                                if (finalType.equals(AssetType.FILTER_ALL.getType())) {
                                    mFilterTypeInfo.setValue(typeAndCategoryInfo);
                                    KindInfo kindInfo = categoryInfo.getKinds().get(0);
                                    mAppRepository.getFilterData(kindInfo);
                                } else if (finalType.equals(AssetType.AR_SCENE_PRE_ALL.getType())) {
                                    mPropTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_ARSCENE_FACE);
                                } else if (finalType.equals(AssetType.COM_CAPTION_ALL.getType())) {
                                    mComponentTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_COMPOUND_CAPTION);
                                } else if (finalType.equals(AssetType.STICKER_ALL.getType())) {
                                    mStickerTypeInfo.setValue(typeAndCategoryInfo);
                                    categoryInfo.setAssetType(NvAsset.ASSET_ANIMATED_STICKER);
                                }

                            }

                        }
                    }

                    @Override
                    public void onError(BaseResponse<List<TypeAndCategoryInfo>> response) {
                        LogUtils.e(response.getMessage());
                    }
                });
    }

    public BeautyShapeDataItem getDefaultData(Context context) {
        BeautyShapeDataItem beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.no));
        beautyStyleInfo.setResId(R.mipmap.ic_beauty_style_no);
        beautyStyleInfo.setDataItems(getStyleNoData(context));
        return beautyStyleInfo;
    }
    /**
     * @param context
     * @return 美颜样式数据;Beauty Data Collection
     */
    public List<BeautyShapeDataItem> getStyleDataList(Context context) {
        List<BeautyShapeDataItem> dataList = new ArrayList<>();
        BeautyShapeDataItem beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.no));
        beautyStyleInfo.setResId(R.mipmap.ic_beauty_style_no);
        beautyStyleInfo.setDataItems(getStyleNoData(context));
        dataList.add(beautyStyleInfo);

        beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.capture_beauty_style_1));
        beautyStyleInfo.setResId(R.mipmap.icon_style_1);
        beautyStyleInfo.setDataItems(parseBeautyStyleData(context, 0.9f, 0.6f, 0.15f, 0.5f, 0.4f, 0f, 0.1f));
        dataList.add(beautyStyleInfo);

        beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.capture_beauty_style_2));
        beautyStyleInfo.setResId(R.mipmap.icon_style_2);
        beautyStyleInfo.setDataItems(parseBeautyStyleData(context, 0.40f, 0.35f, 0.10f, 0.40f, 0.10f, 0f, 0f));
        dataList.add(beautyStyleInfo);

        beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.capture_beauty_style_3));
        beautyStyleInfo.setResId(R.mipmap.icon_style_3);
        beautyStyleInfo.setDataItems(parseBeautyStyleData(context, 0.40f, 0.50f, 0.15f, 0.40f, 0.20f, 0f, 0f));
        dataList.add(beautyStyleInfo);

        beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.capture_beauty_style_4));
        beautyStyleInfo.setResId(R.mipmap.icon_style_4);
        beautyStyleInfo.setDataItems(parseBeautyStyleData(context, 0.70f, 0.60f, 0.15f, 0.40f, 0.30f, 0f, 0f));
        dataList.add(beautyStyleInfo);

        beautyStyleInfo = new BeautyShapeDataItem();
        beautyStyleInfo.setName(context.getString(R.string.capture_beauty_style_5));
        beautyStyleInfo.setResId(R.mipmap.icon_style_5);
        beautyStyleInfo.setDataItems(parseBeautyStyleData(context, 0.90f, 0.60f, 0.15f, 0.50f, 0f, 0.10f, 0.10f));
        dataList.add(beautyStyleInfo);
        return dataList;
    }

    /**
     * @param context
     * @return 美颜数据集合;Beauty Data Collection
     */
    public List<BeautyShapeDataItem> getBeautyDataList(Context context) {
        List<BeautyShapeDataItem> list = new ArrayList<>();
        /*
         * 磨皮
         * strength
         * */
        BeautyShapeDataItem strength = new BeautyShapeDataItem();
        strength.resId = R.mipmap.ic_strength;
        strength.name = context.getResources().getString(R.string.strength);
        list.add(strength);


        /*
         * 美白
         * whitening
         * */
        BeautyShapeDataItem beauty_whitening = new BeautyShapeDataItem();
        beauty_whitening.name = context.getResources().getString(R.string.whitening_B);
        beauty_whitening.resId = R.drawable.beauty_white_selector;
        beauty_whitening.beautyShapeId = "Beauty Whitening";
        list.add(beauty_whitening);
        /*
         * 红润
         * reddening
         * */
        BeautyShapeDataItem beauty_reddening = new BeautyShapeDataItem();
        beauty_reddening.name = context.getResources().getString(R.string.ruddy);
        beauty_reddening.resId = R.drawable.beauty_reddening_selector;
        beauty_reddening.beautyShapeId = "Beauty Reddening";
        list.add(beauty_reddening);
        /*
         * 校色
         * School color
         * */
        BeautyShapeDataItem adjustColor = new BeautyShapeDataItem();
        adjustColor.name = context.getResources().getString(R.string.correctionColor);
        adjustColor.resId = R.drawable.beauty_adjust_selector;
        adjustColor.setPath("assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx");
        list.add(adjustColor);
        /*
         * 锐度
         * sharpness
         * */
        BeautyShapeDataItem sharpen = new BeautyShapeDataItem();
        sharpen.name = context.getResources().getString(R.string.sharpness);
        sharpen.resId = R.drawable.beauty_sharpen_selector;
        sharpen.beautyShapeId = "Default Sharpen Enabled";
        list.add(sharpen);
        return list;
    }

    /**
     * 获取美颜数据
     *
     * @param context
     * @return
     */
    public List<BeautyShapeDataItem> getBeautyData(Context context) {

        List<BeautyShapeDataItem> list = new ArrayList<>();

        /*
         * 磨皮
         * strength
         * */
        BeautyShapeDataItem beauty_strength = new BeautyShapeDataItem();
        beauty_strength.name = context.getResources().getString(R.string.strength_1);
        beauty_strength.resId = R.drawable.beauty_strength_selector;
        beauty_strength.beautyShapeId = "Beauty Strength";
        list.add(beauty_strength);

        /*
         * 高级磨皮1   Advanced Beauty Type  0
         * strength
         * */
        BeautyShapeDataItem beauty_strength_1 = new BeautyShapeDataItem();
        beauty_strength_1.name = context.getResources().getString(R.string.advanced_strength_1);
        beauty_strength_1.resId = R.drawable.beauty_strength_selector;
        beauty_strength_1.beautyShapeId = "Advanced Beauty Intensity";
        list.add(beauty_strength_1);

        /*
         * 高级磨皮2   Advanced Beauty Type  1
         * strength
         * */
        BeautyShapeDataItem beauty_strength_4 = new BeautyShapeDataItem();
        beauty_strength_4.name = context.getResources().getString(R.string.advanced_strength_2);
        beauty_strength_4.resId = R.drawable.beauty_strength_selector;
        beauty_strength_4.beautyShapeId = "Advanced Beauty Intensity";
        list.add(beauty_strength_4);

        /*
         * 点
         * */
        BeautyShapeDataItem point = new BeautyShapeDataItem();
        point.name = context.getResources().getString(R.string.blackPoint);
        point.isPoint = true;
        list.add(point);
        return list;
    }


    /**
     * 获取微整形数据
     *
     * @param context
     * @return
     */
    public List<BeautyShapeDataItem> getSmallShapeDataList(Context context) {
        List<BeautyShapeDataItem> list = new ArrayList<>();

        /*
         * 缩头（小头）
         * */
        BeautyShapeDataItem headItem = new BeautyShapeDataItem();
        headItem.name = context.getResources().getString(R.string.head_size);
        headItem.resId = R.drawable.beauty_shape_head_width_selector;
        headItem.isShape = true;
        headItem.beautyShapeId = "Head Size Warp Degree";
        headItem.setWarpPath("assets:/beauty/shapePackage/BE88A090-8CF2-4A5E-8FF5-0A06464CEF8C.1.warp");
        headItem.setWarpId("Warp Head Size Custom Package Id");
        headItem.setWarpUUID("BE88A090-8CF2-4A5E-8FF5-0A06464CEF8C");
        headItem.setWarpDegree("Head Size Warp Degree");
        list.add(headItem);
//        /*
//         * 颧骨宽
//         * */
        BeautyShapeDataItem malarItem = new BeautyShapeDataItem();
        malarItem.name = context.getResources().getString(R.string.malar_size);
        malarItem.resId = R.drawable.beauty_shape_malar_selector;
        malarItem.isShape = true;
        malarItem.beautyShapeId = "Face Mesh Malar Width Degree";
        malarItem.setFaceMeshPath("assets:/beauty/shapePackage/C1C83B8B-8086-49AC-8462-209E429C9B7A.1.facemesh");
        malarItem.setFaceMeshId("Face Mesh Malar Width Custom Package Id");
        malarItem.setFaceDegree("Face Mesh Malar Width Degree");
        malarItem.setFaceUUID("C1C83B8B-8086-49AC-8462-209E429C9B7A");
        list.add(malarItem);
//        /*
//         * 下颌宽
//         * */
        BeautyShapeDataItem jawItem = new BeautyShapeDataItem();
        jawItem.name = context.getResources().getString(R.string.jaw_size);
        jawItem.resId = R.drawable.beauty_shape_jaw_width_selector;
        jawItem.isShape = true;
        jawItem.beautyShapeId = "Face Mesh Jaw Width Degree";
        jawItem.setFaceMeshPath("assets:/beauty/shapePackage/E903C455-8E23-4539-9195-816009AFE06A.1.facemesh");
        jawItem.setFaceMeshId("Face Mesh Jaw Width Custom Package Id");
        jawItem.setFaceDegree("Face Mesh Jaw Width Degree");
        jawItem.setFaceUUID("E903C455-8E23-4539-9195-816009AFE06A");
        list.add(jawItem);
//        /*
//         * 太阳穴宽
//         * */
        BeautyShapeDataItem templeItem = new BeautyShapeDataItem();
        templeItem.name = context.getResources().getString(R.string.temple_width);
        templeItem.resId = R.drawable.beauty_shape_temple_width_selector;
        templeItem.type = "Custom";
        templeItem.isShape = true;
        templeItem.beautyShapeId = "Face Mesh Temple Width Degree";
        templeItem.setFaceMeshPath("assets:/beauty/shapePackage/E4790833-BB9D-4EFC-86DF-D943BDC48FA4.1.facemesh");
        templeItem.setFaceMeshId("Face Mesh Temple Width Custom Package Id");
        templeItem.setFaceDegree("Face Mesh Temple Width Degree");
        templeItem.setFaceUUID("E4790833-BB9D-4EFC-86DF-D943BDC48FA4");
        list.add(templeItem);


        /*
         * 法令纹
         *
         * */
        BeautyShapeDataItem beauty_nasolabial = new BeautyShapeDataItem();
        beauty_nasolabial.name = context.getResources().getString(R.string.beauty_nasolabial);
        beauty_nasolabial.resId = R.drawable.beauty_nasolabial_selector;
        beauty_nasolabial.beautyShapeId = "Advanced Beauty Remove Nasolabial Folds Intensity";
        list.add(beauty_nasolabial);
        /*
         * 黑眼圈
         *
         * */
        BeautyShapeDataItem beauty_dark = new BeautyShapeDataItem();
        beauty_dark.name = context.getResources().getString(R.string.beauty_dark_circles);
        beauty_dark.resId = R.drawable.beauty_dark_circles_selector;
        beauty_dark.beautyShapeId = "Advanced Beauty Remove Dark Circles Intensity";
        list.add(beauty_dark);
        /*
         * 亮眼
         *
         * */
        BeautyShapeDataItem beauty_brighten = new BeautyShapeDataItem();
        beauty_brighten.name = context.getResources().getString(R.string.beauty_brighten_eye);
        beauty_brighten.resId = R.drawable.beauty_bright_eye_selector;
        beauty_brighten.beautyShapeId = "Advanced Beauty Brighten Eyes Intensity";
        list.add(beauty_brighten);
        /*
         * 美牙
         *
         * */
        BeautyShapeDataItem beauty_tooth = new BeautyShapeDataItem();
        beauty_tooth.name = context.getResources().getString(R.string.beauty_tooth);
        beauty_tooth.resId = R.drawable.beauty_tooth_selector;
        beauty_tooth.beautyShapeId = "Advanced Beauty Whiten Teeth Intensity";
        list.add(beauty_tooth);


        /*
         * 眼角距离
         * */
        BeautyShapeDataItem eye_angle = new BeautyShapeDataItem();
        eye_angle.name = context.getResources().getString(R.string.eye_angel);
        eye_angle.resId = R.drawable.eye_angel_selector;
        eye_angle.isShape = true;
        eye_angle.beautyShapeId = "Face Mesh Eye Angle Degree";
        eye_angle.setFaceMeshPath("assets:/beauty/shapePackage/69D5BADE-A363-4CE0-B269-F146A851932B.1.facemesh");
        eye_angle.setFaceMeshId("Face Mesh Eye Angle Custom Package Id");
        eye_angle.setFaceDegree("Face Mesh Eye Angle Degree");
        eye_angle.setFaceUUID("69D5BADE-A363-4CE0-B269-F146A851932B");
        list.add(eye_angle);


        /*
         * 眼距
         * */
        BeautyShapeDataItem eye_distance = new BeautyShapeDataItem();
        eye_distance.name = context.getResources().getString(R.string.eye_distance);
        eye_distance.resId = R.drawable.eye_distance_selector;
        eye_distance.isShape = true;
        eye_distance.beautyShapeId = "Face Mesh Eye Distance Degree";
        eye_distance.setFaceMeshPath("assets:/beauty/shapePackage/80329F14-8BDB-48D1-B30B-89A33438C481.1.facemesh");
        eye_distance.setFaceMeshId("Face Mesh Eye Distance Custom Package Id");
        eye_distance.setFaceDegree("Face Mesh Eye Distance Degree");
        eye_distance.setFaceUUID("80329F14-8BDB-48D1-B30B-89A33438C481");
        list.add(eye_distance);



        /*
         * 人中
         * */
        BeautyShapeDataItem philtrum_length = new BeautyShapeDataItem();
        philtrum_length.name = context.getResources().getString(R.string.philtrum_length);
        philtrum_length.resId = R.drawable.philtrum_selector;
        philtrum_length.isShape = true;
        philtrum_length.beautyShapeId = "Face Mesh Philtrum Length Degree";
        philtrum_length.setFaceMeshPath("assets:/beauty/shapePackage/37552044-E743-4A60-AC6E-7AADBA1E5B3B.1.facemesh");
        philtrum_length.setFaceMeshId("Face Mesh Philtrum Length Custom Package Id");
        philtrum_length.setFaceDegree("Face Mesh Philtrum Length Degree");
        philtrum_length.setFaceUUID("37552044-E743-4A60-AC6E-7AADBA1E5B3B");
        list.add(philtrum_length);


        /*
         * 宽鼻梁
         * */
        BeautyShapeDataItem nose_bridge = new BeautyShapeDataItem();
        nose_bridge.name = context.getResources().getString(R.string.nose_bridge);
        nose_bridge.resId = R.drawable.philtrum_selector;
        nose_bridge.isShape = true;
        nose_bridge.beautyShapeId = "Face Mesh Nose Bridge Width Degree";
        nose_bridge.setFaceMeshPath("assets:/beauty/shapePackage/23A40970-CE6F-4684-AF57-F78A0CBB53D1.1.facemesh");
        nose_bridge.setFaceMeshId("Face Mesh Nose Bridge Width Custom Package Id");
        nose_bridge.setFaceDegree("Face Mesh Nose Bridge Width Degree");
        nose_bridge.setFaceUUID("23A40970-CE6F-4684-AF57-F78A0CBB53D1");
        list.add(nose_bridge);

        return list;
    }

    /**
     * @param context
     * @return 美型数据集合；Beauty data collection
     */
    public List<BeautyShapeDataItem> getShapeDataList(Context context) {
        ArrayList<BeautyShapeDataItem> list = new ArrayList<>();

        /*
         * 窄脸
         * face width
         * */
        BeautyShapeDataItem intensity_zhailian = new BeautyShapeDataItem();
        intensity_zhailian.name = context.getResources().getString(R.string.face_thin);
        intensity_zhailian.resId = R.drawable.beauty_narrow_face_selector;
        intensity_zhailian.beautyShapeId = "Face Mesh Face Width Degree";
        intensity_zhailian.type = "Custom";
        intensity_zhailian.setFaceMeshPath("assets:/beauty/shapePackage/96550C89-A5B8-42F0-9865-E07263D0B20C.1.facemesh");
        intensity_zhailian.setFaceMeshId("Face Mesh Face Width Custom Package Id");
        intensity_zhailian.setFaceUUID("96550C89-A5B8-42F0-9865-E07263D0B20C");
        intensity_zhailian.setFaceDegree("Face Mesh Face Width Degree");
        list.add(intensity_zhailian);
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
        BeautyShapeDataItem intensity_xiaolian = new BeautyShapeDataItem();
        intensity_xiaolian.name = context.getResources().getString(R.string.face_small);
        intensity_xiaolian.resId = R.drawable.beauty_little_face_selector;
        intensity_xiaolian.beautyShapeId = "Face Mesh Face Length Degree";
        intensity_xiaolian.setFaceMeshPath("assets:/beauty/shapePackage/B85D1520-C60F-4B24-A7B7-6FEB0E737F15.1.facemesh");
        intensity_xiaolian.setFaceMeshId("Face Mesh Face Length Custom Package Id");
        intensity_xiaolian.setFaceUUID("B85D1520-C60F-4B24-A7B7-6FEB0E737F15");
        intensity_xiaolian.setFaceDegree("Face Mesh Face Length Degree");
        list.add(intensity_xiaolian);

        /*
         * 瘦脸
         * Thin face
         * */
        BeautyShapeDataItem cheek_thinning = new BeautyShapeDataItem();
        cheek_thinning.name = context.getResources().getString(R.string.cheek_thinning);
        cheek_thinning.resId = R.drawable.beauty_thin_face_selector;
        cheek_thinning.beautyShapeId = "Face Mesh Face Size Degree";
        cheek_thinning.setFaceMeshPath("assets:/beauty/shapePackage/63BD3F32-D01B-4755-92D5-0DE361E4045A.1.facemesh");
        cheek_thinning.setFaceMeshId("Face Mesh Face Size Custom Package Id");
        cheek_thinning.setFaceUUID("63BD3F32-D01B-4755-92D5-0DE361E4045A");
        cheek_thinning.setFaceDegree("Face Mesh Face Size Degree");
        list.add(cheek_thinning);

        /*
         * 点
         * */
        BeautyShapeDataItem point = new BeautyShapeDataItem();
        point.name = context.getResources().getString(R.string.blackPoint);
        point.isPoint = true;
        list.add(point);

        /*
         * 额头
         * forehead
         * */
        BeautyShapeDataItem intensity_forehead = new BeautyShapeDataItem();
        intensity_forehead.name = context.getResources().getString(R.string.intensity_forehead);
        intensity_forehead.resId = R.drawable.beauty_forehead_selector;
        intensity_forehead.beautyShapeId = "Forehead Height Warp Degree";
        intensity_forehead.setWarpPath("assets:/beauty/shapePackage/A351D77A-740D-4A39-B0EA-393643159D99.1.warp");
        intensity_forehead.setWarpId("Warp Forehead Height Custom Package Id");
        intensity_forehead.setWarpUUID("A351D77A-740D-4A39-B0EA-393643159D99");
        intensity_forehead.setWarpDegree("Forehead Height Warp Degree");
        list.add(intensity_forehead);
        /*
         * 下巴
         * Chin
         * */
        BeautyShapeDataItem intensity_chin = new BeautyShapeDataItem();
        intensity_chin.name = context.getResources().getString(R.string.intensity_chin);
        intensity_chin.resId = R.drawable.beauty_chin_selector;
        intensity_chin.beautyShapeId = "Face Mesh Chin Length Degree";
        intensity_chin.setFaceMeshPath("assets:/beauty/shapePackage/FF2D36C5-6C91-4750-9648-BD119967FE66.1.facemesh");
        intensity_chin.setFaceMeshId("Face Mesh Chin Length Custom Package Id");
        intensity_chin.setFaceDegree("Face Mesh Chin Length Degree");
        intensity_chin.setFaceUUID("FF2D36C5-6C91-4750-9648-BD119967FE66");
        list.add(intensity_chin);

        /*
         * 点
         * */
        point = new BeautyShapeDataItem();
        point.name = context.getResources().getString(R.string.blackPoint);
        point.isPoint = true;
        list.add(point);

        // 大眼
        /*
         * 大眼
         * Eye enlarging
         * */
        BeautyShapeDataItem eye_enlarging = new BeautyShapeDataItem();
        eye_enlarging.name = context.getResources().getString(R.string.eye_enlarging);
        eye_enlarging.setFaceMeshId("Face Mesh Eye Size Custom Package Id");
        eye_enlarging.setFaceMeshPath("assets:/beauty/shapePackage/71C4CF51-09D7-4CB0-9C24-5DE9375220AE.1.facemesh");
        eye_enlarging.setWarpId("Wrap Eye Size Custom Package Id");
        eye_enlarging.setFaceUUID("71C4CF51-09D7-4CB0-9C24-5DE9375220AE");
        eye_enlarging.setFaceDegree("Face Mesh Eye Size Degree");
        eye_enlarging.resId = R.drawable.beauty_big_eye_selector;
        eye_enlarging.beautyShapeId = "Face Mesh Eye Size Degree";
        list.add(eye_enlarging);

        BeautyShapeDataItem yanjiao = new BeautyShapeDataItem();
        yanjiao.name = context.getResources().getString(R.string.eye_corner);
        yanjiao.resId = R.drawable.beauty_eye_corner_selector;
        yanjiao.beautyShapeId = "Face Mesh Eye Corner Stretch Degree";
        yanjiao.setFaceMeshPath("assets:/beauty/shapePackage/54B2B9B4-5A7A-484C-B602-39A4730115A0.1.facemesh");
        yanjiao.setFaceMeshId("Face Mesh Eye Corner Stretch Custom Package Id");
        yanjiao.setFaceDegree("Face Mesh Eye Corner Stretch Degree");
        yanjiao.setFaceUUID("54B2B9B4-5A7A-484C-B602-39A4730115A0");
        list.add(yanjiao);


        /*
         * 点
         * */
        point = new BeautyShapeDataItem();
        point.name = context.getResources().getString(R.string.blackPoint);
        point.isPoint = true;
        list.add(point);

        // 瘦鼻
        /*
         * 瘦鼻
         * Nose width
         * */
        BeautyShapeDataItem intensity_nose = new BeautyShapeDataItem();
        intensity_nose.name = context.getResources().getString(R.string.intensity_nose);
        intensity_nose.resId = R.drawable.beauty_thin_nose_selector;
        intensity_nose.beautyShapeId = "Face Mesh Nose Width Degree";
        intensity_nose.setFaceMeshPath("assets:/beauty/shapePackage/8D676A5F-73BD-472B-9312-B6E1EF313A4C.1.facemesh");
        intensity_nose.setFaceMeshId("Face Mesh Nose Width Custom Package Id");
        intensity_nose.setFaceDegree("Face Mesh Nose Width Degree");
        intensity_nose.setFaceUUID("8D676A5F-73BD-472B-9312-B6E1EF313A4C");
        list.add(intensity_nose);


        BeautyShapeDataItem changbi_nose = new BeautyShapeDataItem();
        changbi_nose.name = context.getResources().getString(R.string.nose_long);
        changbi_nose.resId = R.drawable.beauty_long_nose_selector;
        changbi_nose.beautyShapeId = "Face Mesh Nose Length Degree";
        changbi_nose.setFaceMeshPath("assets:/beauty/shapePackage/3632E2FF-8760-4D90-A2B6-FFF09C117F5D.1.facemesh");
        changbi_nose.setFaceMeshId("Face Mesh Nose Length Custom Package Id");
        changbi_nose.setFaceDegree("Face Mesh Nose Length Degree");
        changbi_nose.setFaceUUID("3632E2FF-8760-4D90-A2B6-FFF09C117F5D");
        list.add(changbi_nose);

        /*
         * 点
         * */
        point = new BeautyShapeDataItem();
        point.name = context.getResources().getString(R.string.blackPoint);
        point.isPoint = true;
        list.add(point);

        /*
         * 嘴形
         * Mouth size
         * */
        BeautyShapeDataItem intensity_mouth = new BeautyShapeDataItem();
        intensity_mouth.name = context.getResources().getString(R.string.intensity_mouth);
        intensity_mouth.resId = R.drawable.beauty_mouth_selector;
        intensity_mouth.beautyShapeId = "Face Mesh Mouth Size Degree";
        intensity_mouth.setFaceMeshPath("assets:/beauty/shapePackage/A80CC861-A773-4B8F-9CFA-EE63DB23EEC2.1.facemesh");
        intensity_mouth.setFaceMeshId("Face Mesh Mouth Size Custom Package Id");
        intensity_mouth.setFaceDegree("Face Mesh Mouth Size Degree");
        intensity_mouth.setFaceUUID("A80CC861-A773-4B8F-9CFA-EE63DB23EEC2");
        list.add(intensity_mouth);
        /*
         * 嘴角
         * Mouth corner
         * */
        BeautyShapeDataItem intensity_zuijiao = new BeautyShapeDataItem();
        intensity_zuijiao.name = context.getResources().getString(R.string.mouse_corner);
        intensity_zuijiao.resId = R.drawable.beauty_mouth_corner_selector;
        intensity_zuijiao.beautyShapeId = "Face Mesh Mouth Corner Lift Degree";
        intensity_zuijiao.setFaceMeshPath("assets:/beauty/shapePackage/CD69D158-9023-4042-AEAD-F8E9602FADE9.1.facemesh");
        intensity_zuijiao.setFaceMeshId("Face Mesh Mouth Corner Lift Custom Package Id");
        intensity_zuijiao.setFaceDegree("Face Mesh Mouth Corner Lift Degree");
        intensity_zuijiao.setFaceUUID("CD69D158-9023-4042-AEAD-F8E9602FADE9");
        list.add(intensity_zuijiao);
        return list;
    }


    private List<BeautyShapeDataItem> parseBeautyStyleData(Context context, float... strengths) {
        List<BeautyShapeDataItem> dataItems = new ArrayList<>();
        /*
         * 瘦脸
         * Thin face
         * */
        BeautyShapeDataItem beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.cheek_thinning);
        beautyShapeDataItem.resId = R.drawable.beauty_thin_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Size Degree";
        beautyShapeDataItem.strength = strengths[0];
        dataItems.add(beautyShapeDataItem);

        /*
         * 大眼
         * Eye enlarging
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.eye_enlarging);
        beautyShapeDataItem.resId = R.drawable.beauty_big_eye_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Eye Size Degree";
        beautyShapeDataItem.strength = strengths[1];
        dataItems.add(beautyShapeDataItem);

        /*
         * 颧骨宽
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.malar_size);
        beautyShapeDataItem.resId = R.drawable.beauty_shape_malar_selector;
        beautyShapeDataItem.isShape = true;
        beautyShapeDataItem.strength = strengths[2];
        beautyShapeDataItem.beautyShapeId = "Face Mesh Malar Width Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 瘦鼻
         * Nose width
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.intensity_nose);
        beautyShapeDataItem.resId = R.drawable.beauty_thin_nose_selector;
        beautyShapeDataItem.type = "Custom";
        beautyShapeDataItem.beautyShapeId = "Face Mesh Nose Width Degree";
        beautyShapeDataItem.strength = strengths[3];
        beautyShapeDataItem.defaultValue = beautyShapeDataItem.strength;
        dataItems.add(beautyShapeDataItem);

        /*
         * 小脸
         * face length
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.face_small);
        beautyShapeDataItem.resId = R.drawable.beauty_little_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Length Degree";
        beautyShapeDataItem.type = "Custom";
        beautyShapeDataItem.strength = strengths[4];
        beautyShapeDataItem.defaultValue = beautyShapeDataItem.strength;
        dataItems.add(beautyShapeDataItem);


        /*
         * 窄脸
         * face width
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.face_thin);
        beautyShapeDataItem.resId = R.drawable.beauty_narrow_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Width Degree";
        beautyShapeDataItem.strength = strengths[5];
        dataItems.add(beautyShapeDataItem);

        /*
         * 下颌宽
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.jaw_size);
        beautyShapeDataItem.resId = R.drawable.beauty_shape_jaw_width_selector;
        beautyShapeDataItem.isShape = true;
        beautyShapeDataItem.strength = strengths[6];
        beautyShapeDataItem.beautyShapeId = "Face Mesh Jaw Width Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 亮眼
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_brighten_eye);
        beautyShapeDataItem.resId = R.drawable.beauty_bright_eye_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Brighten Eyes Intensity";
        beautyShapeDataItem.strength = 0.6f;
        dataItems.add(beautyShapeDataItem);

        /*
         * 黑眼圈
         *
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_dark_circles);
        beautyShapeDataItem.resId = R.drawable.beauty_dark_circles_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Remove Dark Circles Intensity";
        beautyShapeDataItem.strength = 1.0f;
        dataItems.add(beautyShapeDataItem);

        /*
         * 法令纹
         *
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_nasolabial);
        beautyShapeDataItem.resId = R.drawable.beauty_nasolabial_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Remove Nasolabial Folds Intensity";
        beautyShapeDataItem.strength = 0f;
        dataItems.add(beautyShapeDataItem);

        /*
         * 棒果 （单妆）
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.makeup_stick_fruit);
        beautyShapeDataItem.resId = R.drawable.beauty_nasolabial_selector;
        beautyShapeDataItem.strength = 0f;
        beautyShapeDataItem.effectType = BeautyShapeDataItem.EFFECT_TYPE_MAKEUP;
        dataItems.add(beautyShapeDataItem);

        /*
         * 高级磨皮1   Advanced Beauty Type  0
         * strength
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.advanced_strength_1);
        beautyShapeDataItem.resId = R.drawable.beauty_strength_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Intensity";
        beautyShapeDataItem.strength = 0.8f;
        dataItems.add(beautyShapeDataItem);

        /*
         * 美白B
         * whitening
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.whitening_B);
        beautyShapeDataItem.resId = R.drawable.beauty_white_selector;
        beautyShapeDataItem.beautyShapeId = "Beauty Whitening";
        beautyShapeDataItem.strength = 0;
        dataItems.add(beautyShapeDataItem);

        /*
         * 自然肤色
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.filter_natural_complexion);
        beautyShapeDataItem.resId = R.drawable.beauty_adjust_selector;
        beautyShapeDataItem.setPath("assets:/filter/D1C01CF7-CA73-4CB7-A6B7-630B5FF9EC74.1.videofx");
        beautyShapeDataItem.strength = 0.8f;
        dataItems.add(beautyShapeDataItem);

        return dataItems;
    }

    private List<BeautyShapeDataItem> getStyleNoData(Context context) {
        List<BeautyShapeDataItem> dataItems = new ArrayList<>();
        /*
         * 瘦脸
         * Thin face
         * */
        BeautyShapeDataItem beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.cheek_thinning);
        beautyShapeDataItem.resId = R.drawable.beauty_thin_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Size Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 大眼
         * Eye enlarging
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.eye_enlarging);
        beautyShapeDataItem.resId = R.drawable.beauty_big_eye_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Eye Size Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 颧骨宽
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.malar_size);
        beautyShapeDataItem.resId = R.drawable.beauty_shape_malar_selector;
        beautyShapeDataItem.isShape = true;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Malar Width Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 瘦鼻
         * Nose width
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.intensity_nose);
        beautyShapeDataItem.resId = R.drawable.beauty_thin_nose_selector;
        beautyShapeDataItem.type = "Custom";
        beautyShapeDataItem.beautyShapeId = "Face Mesh Nose Width Degree";
        beautyShapeDataItem.defaultValue = beautyShapeDataItem.strength;
        dataItems.add(beautyShapeDataItem);

        /*
         * 小脸
         * face length
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.face_small);
        beautyShapeDataItem.resId = R.drawable.beauty_little_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Length Degree";
        beautyShapeDataItem.type = "Custom";
        beautyShapeDataItem.defaultValue = beautyShapeDataItem.strength;
        dataItems.add(beautyShapeDataItem);


        /*
         * 窄脸
         * face width
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.face_thin);
        beautyShapeDataItem.resId = R.drawable.beauty_narrow_face_selector;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Face Width Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 下颌宽
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.jaw_size);
        beautyShapeDataItem.resId = R.drawable.beauty_shape_jaw_width_selector;
        beautyShapeDataItem.isShape = true;
        beautyShapeDataItem.beautyShapeId = "Face Mesh Jaw Width Degree";
        dataItems.add(beautyShapeDataItem);

        /*
         * 亮眼
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_brighten_eye);
        beautyShapeDataItem.resId = R.drawable.beauty_bright_eye_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Brighten Eyes Intensity";
        dataItems.add(beautyShapeDataItem);

        /*
         * 黑眼圈
         *
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_dark_circles);
        beautyShapeDataItem.resId = R.drawable.beauty_dark_circles_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Remove Dark Circles Intensity";
        dataItems.add(beautyShapeDataItem);

        /*
         * 法令纹
         *
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.beauty_nasolabial);
        beautyShapeDataItem.resId = R.drawable.beauty_nasolabial_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Remove Nasolabial Folds Intensity";
        dataItems.add(beautyShapeDataItem);

        /*
         * 棒果 （单妆）
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.makeup_stick_fruit);
        beautyShapeDataItem.resId = R.drawable.beauty_nasolabial_selector;
        beautyShapeDataItem.effectType = BeautyShapeDataItem.EFFECT_TYPE_MAKEUP;
        dataItems.add(beautyShapeDataItem);

        /*
         * 高级磨皮1   Advanced Beauty Type  0
         * strength
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.advanced_strength_1);
        beautyShapeDataItem.resId = R.drawable.beauty_strength_selector;
        beautyShapeDataItem.beautyShapeId = "Advanced Beauty Intensity";
        dataItems.add(beautyShapeDataItem);

        /*
         * 美白B
         * whitening
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.whitening_B);
        beautyShapeDataItem.resId = R.drawable.beauty_white_selector;
        beautyShapeDataItem.beautyShapeId = "Beauty Whitening";
        dataItems.add(beautyShapeDataItem);

        /*
         * 自然肤色
         * */
        beautyShapeDataItem = new BeautyShapeDataItem();
        beautyShapeDataItem.name = context.getResources().getString(R.string.filter_natural_complexion);
        beautyShapeDataItem.resId = R.drawable.beauty_adjust_selector;
        beautyShapeDataItem.setPath("assets:/filter/D1C01CF7-CA73-4CB7-A6B7-630B5FF9EC74.1.videofx");
        dataItems.add(beautyShapeDataItem);

        return dataItems;
    }


    public List<ChangeVoiceData> getVoiceDatas() {
        List<ChangeVoiceData> dataList = new ArrayList<>();
        dataList.add(ChangeVoiceData.noneData());
        dataList.addAll(getAssetsChangeVoices());
        return dataList;
    }

    private List<ChangeVoiceData> getAssetsChangeVoices() {
        List<ChangeVoiceData> assList = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(MSApplication.getContext().getAssets().open(Utils.isZh() ? "voice/info_Zh.txt" : "voice/info.txt"));
            if (inputStreamReader != null) {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    //todo readLine 每一行读取背景图等信息 处理assList
                    if (!TextUtils.isEmpty(line) && !line.startsWith("#")) {
                        String[] split = line.split(",");
                        String voiceId = split.length > 0 ? split[0] : "";
                        String name = split.length > 1 ? split[1] : "";
                        String bgColor = split.length > 2 ? split[2] : "";
                        String imgUrl = split.length > 3 ? split[3] : "";
                        ChangeVoiceData changeVoiceData = new ChangeVoiceData();
                        changeVoiceData.setName(name);
                        changeVoiceData.setVoiceId(voiceId);
                        changeVoiceData.setBgUrl(imgUrl);
                        changeVoiceData.setBgColor(bgColor);
                        assList.add(changeVoiceData);
                    }
                }
                bufferedReader.close();
                inputStreamReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return assList;
        }

        return assList;
    }


    public MutableLiveData<TypeAndCategoryInfo> getFilterTypeInfo() {
        return mFilterTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getPropTypeInfo() {
        return mPropTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getComponentTypeInfo() {
        return mComponentTypeInfo;
    }

    public MutableLiveData<TypeAndCategoryInfo> getStickerTypeInfo() {
        return mStickerTypeInfo;
    }
}
