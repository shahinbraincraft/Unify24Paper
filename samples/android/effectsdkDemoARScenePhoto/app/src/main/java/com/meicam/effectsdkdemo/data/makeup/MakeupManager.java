package com.meicam.effectsdkdemo.data.makeup;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ms
 */
public class MakeupManager {
    public static final String ASSETS_MAKEUP_PATH = "makeup";
    /**
     * 整装路径
     */
    private static final String ASSETS_MAKEUP_COMPOSE_PATH = ASSETS_MAKEUP_PATH + File.separator + "compose";
    private static final String ASSETS_MAKEUP_CUSTOM_PATH = ASSETS_MAKEUP_PATH + File.separator + "custom/eyeshadow";
    /**
     * 配置json name
     */
    private static final String ASSETS_MAKEUP_RECORD_NAME = File.separator + "info.json";
    private static volatile MakeupManager sMakeupCache;


    private MakeupManager() {
    }

    public static MakeupManager getInstacne() {
        if (sMakeupCache == null) {
            synchronized (MakeupManager.class) {
                if (sMakeupCache == null) {
                    sMakeupCache = new MakeupManager();
                }
            }
        }
        return sMakeupCache;
    }

    /**
     * 获取内置的整装数据
     *
     * @param context
     * @return
     */
    public ArrayList<BeautyData> getComposeMakeupDataList(Context context) {
        String path = ASSETS_MAKEUP_COMPOSE_PATH;
        String readInfo = ParseJsonFile.readAssetJsonFile(context, path + ASSETS_MAKEUP_RECORD_NAME);
        if (TextUtils.isEmpty(readInfo)) {
            return null;
        }
        NoneItem noneItem = new NoneItem();
        noneItem.setIsCompose(true);
        ArrayList<BeautyData> makeups = ParseJsonFile.fromJson(readInfo, new TypeToken<List<Makeup>>() {
        }.getType());
        if (makeups != null && !makeups.isEmpty()) {
            for (BeautyData makeup : makeups) {
                if (makeup == null) {
                    continue;
                }
                makeup.setFolderPath(path);
                makeup.setIsBuildIn(true);
            }
        }
        if (makeups != null) {
            makeups.add(0, noneItem);
        }
        return makeups;
    }


    /**
     * 获取内置的单妆数据
     * @param context
     * @return
     */
    public ArrayList<BeautyData> getCustomMakeupDataList(Context context) {
        String path = ASSETS_MAKEUP_CUSTOM_PATH;
        String readInfo = ParseJsonFile.readAssetJsonFile(context, path + ASSETS_MAKEUP_RECORD_NAME);
        if (TextUtils.isEmpty(readInfo)) {
            return null;
        }
        ArrayList<BeautyData> makeups = ParseJsonFile.fromJson(readInfo, new TypeToken<List<Makeup>>() {
        }.getType());
        if (makeups != null && !makeups.isEmpty()) {
            for (BeautyData makeup : makeups) {
                if (makeup == null) {
                    continue;
                }
                makeup.setFolderPath(path);
                makeup.setIsBuildIn(true);
            }
        }
        return makeups;
    }
}
