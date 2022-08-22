package com.meicam.effectsdkdemo;

import android.content.Context;
import android.text.TextUtils;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effectsdkdemo.data.NvAsset;
import com.meicam.effectsdkdemo.data.makeup.BeautyData;
import com.meicam.effectsdkdemo.data.makeup.Makeup;
import com.meicam.effectsdkdemo.data.makeup.MakeupManager;
import com.meicam.effectsdkdemo.data.makeup.NoneItem;
import com.meicam.effectsdkdemo.view.BeautyShapeDataItem;
import com.meicam.sdk.NvsAssetPackageManager;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    static int[] resIdArray = {
            R.mipmap.beauty_shape_face,
            R.mipmap.beauty_shape_eye,
            R.mipmap.beauty_shape_jaw,
            R.mipmap.beauty_shape_face_little,
            R.mipmap.beauty_shape_face_thin,
            R.mipmap.beauty_shape_forehead,
            R.mipmap.beauty_shape_nose,
            R.mipmap.beauty_shape_nose_long,
            R.mipmap.beauty_shape_eye_corner,
            R.mipmap.beauty_shape_mouse_shape,
            R.mipmap.beauty_shape_mouse_corner
    };
    static String[] nameArray = {
            "瘦脸",
            "大眼",
            "下巴",
            "小脸",
            "窄脸",
            "额头",
            "瘦鼻",
            "长鼻",
            "眼角",
            "嘴形",
            "嘴角"
    };
    static String[] beautyShapeId = {
            "Face Size Warp Degree",
            "Eye Size Warp Degree",
            "Chin Length Warp Degree",
            "Face Length Warp Degree",
            "Face Width Warp Degree",
            "Forehead Height Warp Degree",
            "Nose Width Warp Degree",
            "Nose Length Warp Degree",
            "Eye Corner Stretch Degree",
            "Mouth Size Warp Degree",
            "Mouth Corner Lift Degree"
    };

    /**
     * create beauty shape item list
     * @return list
     */
    public static ArrayList<BeautyShapeDataItem> initBeautyShapeData() {
        ArrayList<BeautyShapeDataItem> mDataList = new ArrayList<>();
        for (int index = 0; index < DataHelper.resIdArray.length; index++) {
            BeautyShapeDataItem shapeDataItem = new BeautyShapeDataItem( );
            shapeDataItem.resId = DataHelper.resIdArray[index];
            shapeDataItem.name = DataHelper.nameArray[index];
            shapeDataItem.beautyShapeId = DataHelper.beautyShapeId[index];
            mDataList.add(shapeDataItem);
        }
        return mDataList;
    }

    /**
     * create makeup data
     * @param context context
     * @return list of makeup
     */
    public static ArrayList<BeautyData> initMakeupData(Context context) {
        //获取美妆-整妆数据
        ArrayList<BeautyData> makeupComposeData = MakeupManager.getInstacne().getComposeMakeupDataList(context);
        if (makeupComposeData == null) {
            return null;
        }
        for (BeautyData beautyData : makeupComposeData) {
            if (beautyData instanceof NoneItem) {
                continue;
            }
            if (beautyData instanceof Makeup) {
                Makeup item = (Makeup) beautyData;
                if (TextUtils.isEmpty(item.getUrl())) {
                    continue;
                }
                createStickerItem("assets:/" + item.getUrl(), NvAsset.ASSET_MAKEUP);
            }
        }
        //获取美妆-单妆数据
        ArrayList<BeautyData> makeupCustomData = MakeupManager.getInstacne().getCustomMakeupDataList(context);
        for (BeautyData beautyData : makeupCustomData) {
            if (beautyData instanceof Makeup) {
                Makeup item = (Makeup) beautyData;
                if (TextUtils.isEmpty(item.getUrl())) {
                    continue;
                }
                createStickerItem("assets:/" + item.getUrl(), NvAsset.ASSET_MAKEUP);
            }
        }
        //单妆整妆放在一起，使用时区分
        if(null != makeupComposeData && null !=makeupCustomData && makeupCustomData.size()>0){
            makeupComposeData.addAll(makeupCustomData);
        }
        return makeupComposeData;
    }


    public static String createStickerItem(String itemPath, int type) {
        StringBuilder sceneId = new StringBuilder();
        int ret = NvsEffectSdkContext.getInstance().getAssetPackageManager().installAssetPackage(itemPath, null, type, true, sceneId);
        if (ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR
                || ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            return sceneId.toString();
        } else if (ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_UPGRADE_VERSION) {
            ret = NvsEffectSdkContext.getInstance().getAssetPackageManager().upgradeAssetPackage(itemPath, null, type, true, sceneId);
            if (ret != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) {
                return null;
            } else {
                return sceneId.toString();
            }
        } else {
            return null;
        }
    }
}
