package com.meishe.modulemakeupcompose;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.meishe.http.bean.CategoryInfo;
import com.meishe.modulemakeupcompose.makeup.BeautyData;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupCustomModel;
import com.meishe.modulemakeupcompose.makeup.MakeupData;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;
import com.meishe.modulemakeupcompose.makeup.NoneItem;
import com.meishe.utils.NvAsset;
import com.meishe.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author ms
 */
public class MakeupManager {
    public static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    public static final String ASSETS_VARIABLECOMPOSE_PATH = "beauty/variablecompose";
    /**
     * 单妆路径
     */
    private static final String ASSETS_MAKEUP_CUSTOM_PATH = "beauty/customcompose";
    /**
     * 妆容路径
     */
    private static final String ASSETS_MAKEUP_VARIABLECOMPOSE_PATH = ASSETS_MAKEUP_PATH + File.separator + "variablecompose";
    /**
     * 配置json name
     */
    private static final String ASSETS_MAKEUP_RECORD_NAME = File.separator + "info.json";
    /**
     * sdcard内置路径
     */
    private static final String SD_MAKEUP_COSTOM_PATH = "makeup/custom";
    private static final String SD_MAKEUP_COMPOSE_PATH = "makeup/compose";
    private static volatile MakeupManager sMakeupCache;
    /**
     * 记录应用的整妆index
     */
    private int mComposeIndex = 0;
    /**
     * 记录应用的单妆数据
     */
    private Map<String, MakeupData> mCustomMakeupArgsMap;
    private Map<String, List<MakeupData>> mCustomMakeupPackagesArgsMap = new TreeMap<>();

    public MakeupData getMakeupPackageEffect(String effectId, String mMakeupUUID) {
        if (mCustomMakeupPackagesArgsMap != null && !TextUtils.isEmpty(effectId)) {
            List<MakeupData> makeupDataList = mCustomMakeupPackagesArgsMap.get(effectId);
            if (makeupDataList != null) {
                for (MakeupData makeupData : makeupDataList) {
                    if (!TextUtils.isEmpty(mMakeupUUID) && TextUtils.equals(mMakeupUUID, makeupData.getUuid())) {
                        return makeupData;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 记录应用的妆容index
     */
    private int mMakeupIndex = 0;

    /**
     * 记录使用的妆容数据
     */
    private Makeup mMakeup;
    /**
     * 记录所有应用的单状
     */
    private HashMap<String,Makeup> mSimpleMakeUp=new HashMap<>();

    /**
     * 滤镜效果
     */
    private Set<String> mFilterEffectSet = new HashSet<>();

    /**
     * 美妆效果中带的美颜，美型
     */
    private HashMap<String, String> mFxMap = new HashMap<>();


    /**
     * 记录整妆数据
     */
    private Makeup item;

    /**
     * 记录对单妆参数的调节内容
     */
    private HashMap<String, Object> makeupArgs = new HashMap<>();

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
     * 获取内置的单妆数据
     *
     * @param context
     * @return
     */
    public ArrayList<MakeupCustomModel> getCustomMakeupDataList(Context context, boolean isEdit) {
        List<CategoryInfo> jsonInfos = getMakeupTab(context);
        if (jsonInfos.isEmpty()) {
            return null;
        }
        ArrayList<MakeupCustomModel> models = new ArrayList<>();
        for (CategoryInfo jsonInfo : jsonInfos) {
            if ((jsonInfo == null) || TextUtils.isEmpty(jsonInfo.getDisplayName())) {
                continue;
            }
            if ((jsonInfo.getId() == 0) && (jsonInfo.getMaterialType() == 21)) {
                //获取妆容数据
                MakeupCustomModel makeupModel = getMakeupData(context, jsonInfo, isEdit);
                models.add(makeupModel);
                continue;
            }
            List<BeautyData> makeups = getCustomMakeupDataListByPath(context, jsonInfo.getDisplayName());
            NoneItem noneItem = new NoneItem();
            noneItem.setIsCompose(false);
            if (isEdit) {
                noneItem.ASSETS_MAKEUP_COMPOSE_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_edit_custom_none.png";
                noneItem.ASSETS_MAKEUP_CUSTOM_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_edit_custom_none.png";
            }
            makeups.add(0, noneItem);
            MakeupCustomModel customModel = new MakeupCustomModel();
            customModel.setId(jsonInfo.getId());
            customModel.setModelContent(makeups);
            models.add(customModel);
        }
        return models;
    }

    public List<BeautyData> getCustomMakeupDataListByPath(Context context, String disPlayName) {
        List<BeautyData> makeUps = new ArrayList<>();
        try {
            //1.asset 内置
            String makeupPath = ASSETS_MAKEUP_CUSTOM_PATH + File.separator + disPlayName;
            for (String filePath : context.getAssets().list(makeupPath)) {
                String readInfo = ParseJsonFile.readAssetJsonFile(context, makeupPath + File.separator + filePath + File.separator + "info.json");
                if (TextUtils.isEmpty(readInfo)) {
                    continue;
                }
                MakeupArgs makeupArg = ParseJsonFile.fromJson(readInfo, MakeupArgs.class);
                if (makeupArgs == null) {
                    continue;
                }
                Makeup makeup = new Makeup();
                makeup.setFolderPath(makeupPath + File.separator + filePath);
                makeup.setMakeupId(makeupArg.getMakeupId());
                makeup.setCustom(true);
                makeup.setIsBuildIn(true);
                List<MakeupArgs> makeupArgs = new ArrayList<>();
                MakeupEffectContent makeupEffectContent = new MakeupEffectContent();
                makeupEffectContent.setMakeupArgs(makeupArgs);
                makeup.setEffectContent(makeupEffectContent);
                makeup.setTranslation(makeupArg.getTranslation());
                makeupArg.setUuid(filePath);
                makeupArgs.add(makeupArg);
                makeup.setCover("cover.png");
                for (String singleFilePath : context.getAssets().list(makeupPath + File.separator + filePath)) {
                    if (singleFilePath.endsWith(".makeup")) {
                        makeup.setUrl(makeupPath + File.separator + filePath + File.separator + singleFilePath);
                    }
                }
                makeUps.add(makeup);
            }
            //2.local sd卡下的
//            String localPath = PathUtils.getLocalCustomPath(ASSETS_VARIABLECOMPOSE_PATH) + File.separator + disPlayName;
//            File localFile = new File(localPath);
//            if (localFile.exists()) {
//                for (String filePath : localFile.list()) {
//                    String readInfo = ParseJsonFile.readSDJsonFile(context, localPath + File.separator + filePath + File.separator + "info.json");
//                    if (TextUtils.isEmpty(readInfo)) {
//                        continue;
//                    }
//                    MakeupArgs makeupArg = ParseJsonFile.fromJson(readInfo, MakeupArgs.class);
//                    if (makeupArgs == null) {
//                        continue;
//                    }
//                    Makeup makeup = new Makeup();
//                    makeup.setFolderPath(localPath + File.separator + filePath);
//                    makeup.setMakeupId(makeupArg.getMakeupId());
//                    makeup.setCustom(true);
//                    makeup.setIsBuildIn(true);
//                    List<MakeupArgs> makeupArgs = new ArrayList<>();
//                    MakeupEffectContent makeupEffectContent = new MakeupEffectContent();
//                    makeupEffectContent.setMakeupArgs(makeupArgs);
//                    makeup.setEffectContent(makeupEffectContent);
//                    makeup.setTranslation(makeupArg.getTranslation());
//                    makeupArg.setUuid(filePath);
//                    makeupArgs.add(makeupArg);
//                    makeup.setCover("cover.png");
//                    File file = new File(localPath + File.separator + filePath);
//                    if (file.exists() && file.isDirectory()) {
//                        for (String singleFilePath : file.list()) {
//                            if (singleFilePath.endsWith(".makeup")) {
//                                makeup.setUrl(localPath + File.separator + filePath + File.separator + singleFilePath);
//                            }
//                        }
//                    }
//                    makeUps.add(makeup);
//                }
//            } else {
//                localFile.mkdirs();
//            }

        } catch (IOException e) {
            e.printStackTrace();
            return makeUps;
        }
        return makeUps;
    }

    /**
     * 获取内置的妆容数据
     *
     * @param context
     * @param categoryInfo
     * @return
     */
    public MakeupCustomModel getMakeupData(Context context, CategoryInfo categoryInfo, boolean isEdit) {
        NoneItem noneItem = new NoneItem();
        noneItem.setIsCompose(false);
        if (isEdit) {
            noneItem.ASSETS_MAKEUP_COMPOSE_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_edit_custom_none.png";
            noneItem.ASSETS_MAKEUP_CUSTOM_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_edit_custom_none.png";
        }
        MakeupCustomModel makeupModel = new MakeupCustomModel();
        makeupModel.setId(categoryInfo.getId());
        makeupModel.setMakeupId(categoryInfo.getDisplayName());
        ArrayList<BeautyData> makeupData = new ArrayList<>();
        makeupData.add(noneItem);
        makeupModel.setModelContent(makeupData);
        return makeupModel;
    }

    private List<BeautyData> sdMakeUps = new ArrayList<>();
    private List<BeautyData> localMakeUps = new ArrayList<>();


    /**
     * 获取本地的妆容数据
     *
     * @param context
     * @return
     */
    public List<BeautyData> getMakeupData(Context context, boolean clearFlag) {
        if (clearFlag) {
            sdMakeUps.clear();
        }
        return getMakeupData(context);
    }

    /**
     * 获取本地的妆容数据
     * asset妆容和local下妆容
     *
     * @param context
     * @return
     */
    public List<BeautyData> getMakeupData(Context context) {
        if (sdMakeUps != null && !sdMakeUps.isEmpty()) {
            return sdMakeUps;
        }
        sdMakeUps.addAll(getLocalMakeupData(context));
        sdMakeUps.addAll(getAssetMakeupData(context));
        // 等网络放开后再解开本地刷新
//        sdMakeUps.addAll(getDownloadMakeupData(context));
        return sdMakeUps;
    }

    /**
     * 获取已经下载的美妆
     *
     * @param context
     * @return
     */
    public List<BeautyData> getDownloadMakeupData(Context context) {
        String variablePath = PathUtils.getAssetDownloadPath(NvAsset.ASSET_MAKEUP);
        List<BeautyData> makeupData = new ArrayList<>();
        File file = new File(variablePath);
        if (!file.exists()) {
            file.mkdirs();
            return makeupData;
        }
        File[] files = file.listFiles();
        for (File singleFile : files) {
            if (singleFile.exists() && singleFile.isDirectory()) {
                BeautyData makeup = updateNewBeauty(context, false, null, singleFile.getAbsolutePath());
                if (makeup != null) {
                    makeupData.add(makeup);
                }
            }
        }
        return makeupData;
    }

    /**
     * 获取 用户自己存放的 妆容
     *
     * @param context
     * @return
     */
    private List<BeautyData> getLocalMakeupData(Context context) {
        List<BeautyData> makeupData = new ArrayList<>();
        File file = new File(PathUtils.getLocalCustomPath(ASSETS_VARIABLECOMPOSE_PATH));
        if (!file.exists()) {
            file.mkdirs();
            return makeupData;
        }
        File[] files = file.listFiles();
        for (File singleFile : files) {
            if (singleFile.exists() && singleFile.isDirectory()) {
                BeautyData makeup = updateNewBeauty(context, false, null, singleFile.getAbsolutePath());
                if (makeup != null) {
                    if (makeup instanceof Makeup) {
                        ((Makeup) makeup).setCustom(true);
                    }
                    makeupData.add(makeup);
                }
            }
        }
        return makeupData;
    }

    /**
     * 获取内置的
     *
     * @param context
     * @return
     */
    private List<BeautyData> getAssetMakeupData(Context context) {
        String variablePath = ASSETS_VARIABLECOMPOSE_PATH;
        List<BeautyData> assetsList = new ArrayList<>();
        if (context == null) {
            return assetsList;
        }
        try {
            for (String assetFilePath : context.getAssets().list(variablePath)) {
                BeautyData beautyData = updateNewBeauty(context, true, null, variablePath + File.separator + assetFilePath);
                if (beautyData != null) {
                    if (beautyData instanceof Makeup) {
                        ((Makeup) beautyData).setCustom(true);
                    }
                    assetsList.add(beautyData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return assetsList;
        }
        return assetsList;
    }

    private BeautyData updateNewBeauty(Context context, Makeup data, File singleFile, String assetSu) {
        if (singleFile == null || !singleFile.exists() || !singleFile.isDirectory()) {
            return null;
        }
        if (data == null) {
            File jsonFile = new File(singleFile, "info.json");
            // 解析并设置name等属性
            String readInfo = ParseJsonFile.readSDJsonFile(context, jsonFile.getAbsolutePath());
            data = ParseJsonFile.fromJson(readInfo, Makeup.class);
        }
        if (data == null) {
            return null;
        }
        data.setFolderPath(singleFile.getAbsolutePath());
        data.setMakeupId(singleFile.getName());
        data.setIsBuildIn(true);
        return data;
    }

    private BeautyData updateNewBeauty(Context context, boolean assetFlag, Makeup data, String filePath) {
        if (!assetFlag) {
            File singleFile = new File(filePath);
            if (singleFile == null || !singleFile.exists() || !singleFile.isDirectory()) {
                return null;
            }
        }
        if (data == null) {
            // 解析并设置name等属性
            String jsonPath = filePath + File.separator + "info.json";
            String readInfo = assetFlag ? ParseJsonFile.readAssetJsonFile(context, jsonPath) : ParseJsonFile.readSDJsonFile(context, jsonPath);
            try {
                data = ParseJsonFile.fromJson(readInfo, Makeup.class);
            } catch (Exception e) {
                return null;
            }
        }
        if (data == null) {
            return null;
        }
        data.setLocalFlag(true);
        data.setFolderPath(filePath);
        String[] split = filePath.split("/");
        data.setMakeupId(split[split.length - 1]);
        data.setUuid(split[split.length - 1]);
        data.setIsBuildIn(true);
        return data;
    }

    public ArrayList<CategoryInfo> getMakeupTab(Context context) {
        String path = ASSETS_MAKEUP_CUSTOM_PATH;
        String tabJsonStr = ParseJsonFile.readAssetJsonFile(context, path + ASSETS_MAKEUP_RECORD_NAME);
        if (TextUtils.isEmpty(tabJsonStr)) {
            return null;
        }
        //单妆
        ArrayList<CategoryInfo> customTab = ParseJsonFile.fromJson(tabJsonStr, new TypeToken<List<CategoryInfo>>() {
        }.getType());
        CategoryInfo makeupInfo = new CategoryInfo();
        makeupInfo.setId(0);
        makeupInfo.setMaterialType(21);
//        makeupInfo.setCategory((BuildConfig.FACE_MODEL == 106) ? 1 : 2);
        makeupInfo.setDisplayName("Makeup");
        makeupInfo.setDisplayNameZhCn("妆容");
        customTab.add(0, makeupInfo);
        return customTab;
    }


    public MakeupData getMakeupEffect(String effectId) {
        if (mCustomMakeupArgsMap == null || TextUtils.isEmpty(effectId)) {
            return null;
        }
        return mCustomMakeupArgsMap.get(effectId);
    }

    public void addMakeupEffect(String makeupId, MakeupData data) {
        if (TextUtils.isEmpty(makeupId)) {
            return;
        }
        if (mCustomMakeupArgsMap == null) {
            mCustomMakeupArgsMap = new HashMap<>();
        }
        mCustomMakeupArgsMap.put(makeupId, data);

    }

    public void addMakeupPackageEffect(String makeupId, MakeupData data) {
        if (TextUtils.isEmpty(makeupId)) {
            return;
        }
        if (!mCustomMakeupPackagesArgsMap.containsKey(makeupId)) {
            mCustomMakeupPackagesArgsMap.put(makeupId, new ArrayList<MakeupData>());
        }
        mCustomMakeupPackagesArgsMap.get(makeupId).add(data);

    }

    public void removeMakeupEffect(String makeupId) {
        if (TextUtils.isEmpty(makeupId) || (mCustomMakeupArgsMap == null)) {
            return;
        }
        mCustomMakeupArgsMap.remove(makeupId);

    }

    public Map<String, MakeupData> getCustomMakeupArgsMap() {
        return mCustomMakeupArgsMap;
    }

    public void clearCustomData() {
        if (mCustomMakeupArgsMap != null) {
            mCustomMakeupArgsMap.clear();
        }
        if (mCustomMakeupPackagesArgsMap != null) {
            mCustomMakeupPackagesArgsMap.clear();
        }
    }

    public int getComposeIndex() {
        return mComposeIndex;
    }

    public void setComposeIndex(int composeIndex) {
        this.mComposeIndex = composeIndex;
    }

    public int getMakeupIndex() {
        return mMakeupIndex;
    }

    public void setMakeupIndex(int makeupIndex) {
        mMakeupIndex = makeupIndex;
    }

    public void clearAllData() {
        mComposeIndex = 0;
        clearCustomData();
        mMakeupIndex = 0;
        clearMapFxData();
        clearMakeupArgs();
        clearFilterData();
        item = null;
    }

    public void putMapFx(String fxName, String value) {
        if (mFxMap != null) {
            mFxMap.put(fxName, value);
        }
    }


    public void removeMapFx(String fxName) {
        if (mFxMap != null) {
            mFxMap.remove(fxName);
        }
    }

    public HashMap<String, String> getMapFxMap() {
        return mFxMap;
    }

    public void clearMapFxData() {
        mFxMap.clear();
    }

    public void putFilterFx(String fxName) {
        if (!mFilterEffectSet.contains(fxName) && !TextUtils.isEmpty(fxName)) {
            mFilterEffectSet.add(fxName);
        }
    }

    public void removeFilterFx(String fxName) {
        mFilterEffectSet.remove(fxName);
    }

    public Set<String> getFilterFxSet() {
        return mFilterEffectSet;
    }

    public void clearFilterData() {
        mFilterEffectSet.clear();
    }


    public Makeup getItem() {
        return item;
    }

    public void setItem(Makeup item) {
        this.item = item;
    }


    /**
     * 给单妆添加
     *
     * @param key
     * @param value
     */
    public void putMakeupArgs(String key, Object value) {
        makeupArgs.put(key, value);
    }

    public void clearMakeupArgs() {
        if (makeupArgs != null) {
            makeupArgs.clear();
        }
    }

    public HashMap<String, Object> getMakeupArgs() {
        return makeupArgs;
    }


    public void installNewMakeUp(String downloadPath) {

    }

    public Makeup getMakeup() {
        return mMakeup;
    }

    public void setMakeup(Makeup makeup) {
        this.mMakeup = makeup;
    }

    public void addSimpleMakeupEffect(String makeupType, Makeup makeup) {
        if (mSimpleMakeUp == null) {
            mSimpleMakeUp = new HashMap<>();
        }
        mSimpleMakeUp.put(makeupType, makeup);
    }


    public Map<String, Makeup> getSimpleMakeupEffect(){
        return mSimpleMakeUp;
    }
}
