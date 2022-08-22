package com.meishe.engine;

import com.meishe.base.utils.Utils;
import com.meishe.engine.asset.bean.TemplateUploadParam;
import com.meishe.engine.bean.CommonData;
import com.meishe.net.NvsServerClient;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.server.download.DownloadTask;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights reserved,Designed by www.meishesdk.com
 * 版权所有:www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2020/12/7 16:24
 * @Description : the net api of engine model
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class EngineNetApi {
    /**
     * 获取特效素材资源列表
     * Get a list of effects material resources
     *
     * @param tag         Object 请求标识 tag
     * @param type        int  资源类型 resource type
     * @param aspectRatio int 资源比例 resource aspect ratio
     * @param categoryId  int 资源分类id category id
     * @param page        int 资源页数 page num
     * @param pageSize    int 资源页数大小 page Size
     * @param callback    RequestCallback 请求回调 callback
     */
    public static void getMaterialList(Object tag, int type, int aspectRatio, int categoryId,
                                       int page, int pageSize, RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(2);
        params.put("command", "listMaterial");
        params.put("acceptAspectRatio", String.valueOf(aspectRatio));
        params.put("category", String.valueOf(categoryId));
        params.put("page", String.valueOf(page));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("type", String.valueOf(type));
        params.put("lang", Utils.isZh() ? "zh_CN" : "en");
        String apiName = "materialinfo/index.php";
        NvsServerClient.get().requestGet(tag, apiName, params, callback);

    }


    /**
     * 获取特效素材资源列表
     * Get a list of effects material resources
     *
     * @param tag         Object 请求标识 tag
     * @param type        int  资源类型 resource type
     * @param aspectRatio int 资源比例 resource aspect ratio
     * @param categoryId  int 资源分类id category id
     * @param page        int 资源页数 page num
     * @param pageSize    int 资源页数大小 page Size
     * @param callback    RequestCallback 请求回调 callback
     */
    public static void getMaterialList(Object tag, String token, int type, int subType, int categoryId, int kind, int aspectRatio, int ratioFlag,
                                       int page, int pageSize, RequestCallback<?> callback) {
        Map<String, String> params = new HashMap<>(2);

        if (aspectRatio > 0) {
            params.put("ratio", String.valueOf(aspectRatio));
        }
        params.put("pageNum", String.valueOf(page+1));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("type", String.valueOf(type));
        params.put("ratioFlag", String.valueOf(ratioFlag));
        if (categoryId > 0) {
            params.put("category", String.valueOf(categoryId));
        }
        if (kind > 0) {
            params.put("kind", String.valueOf(kind));
        }
        params.put("lang", Utils.isZh() ? "zh_CN" : "en");
        String apiName;
        if (subType == CommonData.UserAssetType.CUSTOM) {
            apiName = "materialcenter/myvideo/material/listPrivate";
        } else if (subType == CommonData.UserAssetType.PURCHASED) {
            apiName = "materialcenter/myvideo/material/listAuthed";
        } else {
            apiName = "materialcenter/myvideo/material/listAll";
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);
        NvsServerClient.get().getWithHeader(tag, NvsServerClient.getAssetsHost(), apiName, headerMap, params, callback);

    }

    /**
     * Check unavailable assets
     * <p></>
     * 检查无效资源
     *
     * @param token        用户token user token
     * @param assetsIdList 素材id数组  List of assets id
     * @param callback     回调 callback
     */
    public static void checkUnavailableAssets(String token, List<String> assetsIdList, RequestCallback<?> callback) {
        String apiName = "materialcenter/myvideo/material/arrearage";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);
        NvsServerClient.get().postWithHeader(null, NvsServerClient.getAssetsHost(), apiName, headerMap, assetsIdList, callback);
    }

    /**
     * Check unavailable assets
     * <p></>
     * 检查无效资源
     *
     * @param token        用户token user token
     * @param assetsIdList 素材id数组  List of assets id
     * @param callback     回调 callback
     */
    public static void commitUnavailableAssets(String token, List<String> assetsIdList, RequestCallback<?> callback) {
        String apiName = "materialcenter/myvideo/material/submitArrearage";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);
        NvsServerClient.get().postWithHeader(null, NvsServerClient.getAssetsHost(), apiName, headerMap, assetsIdList, callback);
    }

    /**
     * 下载实现方法
     * Download implementation method
     *
     * @param tag      下载标识
     * @param url      下载地址
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param listener 下载监听器
     */
    public static void download(String tag, String url, String filePath, String fileName, SimpleDownListener listener) {
        NvsServerClient.get().download(tag, url, filePath, fileName, listener);
    }

    /**
     * 获取正在下载的任务
     * Gets the task being downloaded
     *
     * @param tag the task tag
     */
    public static DownloadTask getDownloadTask(String tag) {
        return NvsServerClient.get().getDownloadTask(tag);
    }

    /**
     * 取消正在下载的任务
     * Cancel the task being downloaded
     *
     * @param tag the task tag
     */
    public static void cancelDownloadTask(String tag) {
        NvsServerClient.get().cancelDownloadTask(tag);
    }

    /**
     * 取消任务
     * Cancel the task
     *
     * @param tag the task tag
     */
    public static void cancelTask(String tag) {
        NvsServerClient.get().cancelRequest(tag);
    }


    public static void uploadTemplate(String tag, String token, TemplateUploadParam param, RequestCallback<Object> callback) {
        String apiName = "materialcenter/myvideo/material/upload";

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);

        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("materialFile", param.materialFile);
        fileMap.put("coverFile", param.coverFile);
        fileMap.put("previewVideoFile", param.previewVideoFile);

        Map<String, String> map = new HashMap<>();
        map.put("descriptionZhCn", param.descriptinZhCn);
        map.put("description", param.description);
        map.put("ratioFlag", "1");
        map.put("materialType", "19");
        map.put("customDisplayName", param.customDisplayName);
        map.put("category", 1 + "");
        NvsServerClient.get().postWithHeaderAndFile(tag, NvsServerClient.getAssetsHost(), apiName, headerMap, map, fileMap, callback);
    }
}
