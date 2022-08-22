package com.meishe.sdkdemo.capturescene.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.GsonUtils;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capturescene.adapter.CaptureSceneAdapter;
import com.meishe.sdkdemo.capturescene.data.CaptureSceneOnlineData;
import com.meishe.sdkdemo.capturescene.data.Constants;
import com.meishe.sdkdemo.capturescene.httputils.NetWorkUtil;
import com.meishe.sdkdemo.capturescene.interfaces.OnItemClickListener;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.PathNameUtil;
import com.meishe.sdkdemo.utils.PathUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.meishe.net.utils.HttpUtils.runOnUiThread;
import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_LOCAL;
import static com.meishe.sdkdemo.capturescene.data.Constants.CAPTURE_SCENE_ONLINE;
import static com.meishe.sdkdemo.capturescene.data.Constants.RESOURCE_NEW_PATH;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author : LiuPanFeng
 * @CreateDate : 2022/1/18 13:09
 * @Description :
 * @Copyright : www.meishesdk.com Inc. All rights reserved.
 */
public class CaptureSceneEffectView extends LinearLayout {

    private static final String TAG = "CaptureSceneEffectView";
    private String FILENAME_SUFFIX = "capturescene";
    private CaptureSceneAdapter captureSceneAdapter;
    private RecyclerView recyclerView;
    private TextView csTextReset;
    private Context mContext;
    private LinkedList<CaptureSceneOnlineData.CaptureSceneDetails> captureSceneDetails = new LinkedList<>();
    private Map<String, String> downloadingURL = new HashMap<>();
    private NvsStreamingContext mStreamingContext;
    private String mUrl;
    private int mType;

    public CaptureSceneEffectView(Context context) {
        this(context, null);
    }

    public CaptureSceneEffectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureSceneEffectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_capture_scene_layout, this, true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        csTextReset = (TextView) findViewById(R.id.tv_text_reset);
        initListener();
    }

    private void initListener() {
        csTextReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCaptureScene();
            }
        });
    }

    public void setStreamingContext(NvsStreamingContext streamingContext,int type) {
        mType=type;
        mStreamingContext = streamingContext;
        if (type== Constants.CAPTURE_SCENE_TYPE_IMAGE){
            mUrl=Constants.CAPTURE_SCENE_PATH_IMAGE;
        }else{
            mUrl=Constants.CAPTURE_SCENE_PATH_VIDEO;
        }

        initData();
    }


    private void initData() {
//        getMessageFormAssetsFile();
        captureSceneAdapter = new CaptureSceneAdapter(mContext,
                captureSceneDetails, new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetails =
                        (CaptureSceneOnlineData.CaptureSceneDetails) view.getTag();
                if (captureSceneDetails.getPackageUrl().contains(RESOURCE_NEW_PATH) &&
                        !downloadingURL.containsKey(captureSceneDetails.getPackageUrl())) {
                    CircleBarView circleBarView = (CircleBarView) view.findViewById(R.id.item_cs_download);
                    downloadImage(captureSceneDetails.getCoverUrl(), position);
                    downloadPackage(captureSceneDetails.getPackageUrl(), circleBarView, position);
                } else {
                    setCaptureSceneByPath(captureSceneDetails.getId(), captureSceneDetails.getPackageUrl());
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(captureSceneAdapter);


        if (NetWorkUtil.isNetworkConnected(mContext)) {
            /*
             * 有权限，则删除本地拍摄的视频文件
             * Have permission to delete locally captured video files
             * */
            HttpManager.getOldObjectGet(mUrl, new TempStringCallBack() {
                @Override
                public void onResponse(final String stringResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CaptureSceneOnlineData onlineData = GsonUtils.fromJson(stringResponse, CaptureSceneOnlineData.class);
                            if (onlineData == null) return;
                            List<CaptureSceneOnlineData.CaptureSceneDetails> captureSceneDetailsList = onlineData.getList();
                            int oldSize = captureSceneDetails.size();
                            for (CaptureSceneOnlineData.CaptureSceneDetails sceneDetail : captureSceneDetailsList) {
                                sceneDetail.setType(CAPTURE_SCENE_ONLINE);
                                sceneDetail.setCoverUrl(sceneDetail.getCoverUrl().replace(Constants.RESOURCE_OLD_PATH, RESOURCE_NEW_PATH));
                                String packageUrl = sceneDetail.getPackageUrl();
                                sceneDetail.setPackageUrl(packageUrl.replaceAll(Constants.RESOURCE_OLD_PATH, RESOURCE_NEW_PATH));
                                if (!captureSceneDetails.contains(sceneDetail)) {
                                    captureSceneDetails.add(sceneDetail);
//                                    final String path = PathUtils.getCaptureSceneVideoLocalFilePath();
//                                    downloadingURL.put(packageUrl, path + File.separator + PathNameUtil.getPathNameWithSuffix(packageUrl));
                                }
                            }
                            if (oldSize != captureSceneDetails.size()) {
                                captureSceneAdapter.setDataList(captureSceneDetails);
                            }
                        }
                    });
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        }

    }

    private void setCaptureSceneByPath(String sceneId, String scenePackageFilePath) {
        /*
         * 检查改拍摄场景的资源包是否已经安装
         * Check if the resource pack for the shooting scene has been installed
         * */
        NvsAssetPackageManager assetPackageManager = mStreamingContext.getAssetPackageManager();
        int packageStatus = assetPackageManager.getAssetPackageStatus(sceneId, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE);
        if (packageStatus == NvsAssetPackageManager.ASSET_PACKAGE_STATUS_NOTINSTALLED) {
            /*
             * 该拍摄场景的资源包尚未安装，则安装该资源包，由于拍摄场景的资源包尺寸较大，为了不freeze UI，我们采用异步安装模式
             * If the resource pack of the shooting scene has not been installed, install the resource pack. Due to the large size of the resource pack of the shooting scene, in order not to freeze the UI, we use an asynchronous installation mode
             * */
            assetPackageManager.installAssetPackage(scenePackageFilePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE, false, null);
        } else {
            /*
             * 若拍摄场景的资源包已经安装，应用其效果
             * If the resource pack for the shooting scene is already installed, apply its effects
             * */
            mStreamingContext.applyCaptureScene(sceneId);
        }
    }


    private void downloadImage(String coverUrl, final int position) {
         String path="";
        if (mType==Constants.CAPTURE_SCENE_TYPE_IMAGE){
            path= PathUtils.getCaptureSceneImageLocalFilePath();
        }else{
            path = PathUtils.getCaptureSceneVideoLocalFilePath();
        }

        String[] split = coverUrl.split("/");
        HttpManager.download(coverUrl, coverUrl, path, split[split.length - 1], new SimpleDownListener(coverUrl) {
            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                CaptureSceneOnlineData.CaptureSceneDetails data = captureSceneDetails.get(position);
                data.setCoverUrl(file.getAbsolutePath());
                captureSceneAdapter.setDataList(position, data, false);
            }
        });
    }

    private void downloadPackage(final String packageUrl, final CircleBarView circleBarView, final int position) {
        final String path = PathUtils.getCaptureSceneVideoLocalFilePath();
        String[] split = packageUrl.split("/");
//        downloadingURL.put(packageUrl, path + File.separator + PathNameUtil.getPathNameWithSuffix(packageUrl));
        HttpManager.download(packageUrl, packageUrl, path, split[split.length - 1], new SimpleDownListener(packageUrl) {
            @Override
            public void onProgress(Progress progress) {
                super.onProgress(progress);
                captureSceneAdapter.setmProgress((int) (progress.fraction * 100), position);

            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                Logger.e(TAG, "downloadPackageOnError: " + progress.exception.toString());
                downloadingURL.remove(packageUrl);
                deleteFiles(path);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                super.onFinish(file, progress);
                circleBarView.setVisibility(View.GONE);
                downloadingURL.remove(packageUrl);
                CaptureSceneOnlineData.CaptureSceneDetails data = captureSceneDetails.get(position);
                data.setPackageUrl(file.getAbsolutePath());
                data.setType(CAPTURE_SCENE_LOCAL);
                captureSceneAdapter.setDataList(position, data, true);
                if (captureSceneAdapter.getSelectPosition() == position) {
                    setCaptureSceneByPath(PathNameUtil.getPathNameNoSuffix(file.getAbsolutePath()), file.getAbsolutePath());
                }
            }
        });
    }


    private void getMessageFormAssetsFile() {
        String path="";
        if (mType==Constants.CAPTURE_SCENE_TYPE_IMAGE){
            path= PathUtils.getCaptureSceneImageLocalFilePath();
        }else{
            path = PathUtils.getCaptureSceneVideoLocalFilePath();
        }
        if (!path.isEmpty()) {
            File file = new File(path);
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isFile()) {
                    String suffix = PathNameUtil.getPathSuffix(file2.getAbsolutePath());
                    if (suffix.equals(FILENAME_SUFFIX)) {
                        CaptureSceneOnlineData.CaptureSceneDetails captureSceneDetails = new CaptureSceneOnlineData.CaptureSceneDetails();
                        String pathName = file2.getAbsolutePath();
                        captureSceneDetails.setPackageUrl(pathName);
                        captureSceneDetails.setId(PathNameUtil.getPathNameNoSuffix(pathName));
                        String imagePath = PathNameUtil.getOutOfPathSuffix(pathName) + "png";
                        File imageFile = new File(imagePath);
                        if (!imageFile.exists()) {
                            imagePath = "";
                        }
                        captureSceneDetails.setCoverUrl(imagePath);
                        captureSceneDetails.setType(CAPTURE_SCENE_LOCAL);
                        this.captureSceneDetails.add(captureSceneDetails);
                    }
                }
            }
        }
    }


    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }


    public void clearCaptureScene() {
        mStreamingContext.removeCurrentCaptureScene();
        mStreamingContext.removeAllCaptureVideoFx();
        captureSceneAdapter.setSelectPosition(-1);
    }

    public void onDestroy(){
        for (String url : downloadingURL.keySet()) {
            Logger.e(TAG, "onDestroy下载地址: " + url);
            HttpManager.cancelRequest(url);
        }
        for (String filePath : downloadingURL.values()) {
            Logger.e(TAG, "onDestroy文件地址: " + filePath);
            Logger.e(TAG, "onDestroy文件地址: " + PathNameUtil.getOutOfPathSuffix(filePath) + "png");
            deleteFiles(filePath);
            deleteFiles(PathNameUtil.getOutOfPathSuffix(filePath) + "png");
        }
        downloadingURL.clear();
    }
}
