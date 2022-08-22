package com.meishe.net;

import android.app.Application;
import android.text.TextUtils;

import com.example.net.BuildConfig;
import com.google.gson.Gson;
import com.meishe.net.callback.AbsCallback;
import com.meishe.net.callback.Callback;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.https.HttpsUtils;
import com.meishe.net.model.Progress;
import com.meishe.net.model.Response;
import com.meishe.net.request.GetRequest;
import com.meishe.net.request.PostRequest;
import com.meishe.net.request.base.Request;
import com.meishe.net.server.OkDownload;
import com.meishe.net.server.download.DownloadListener;
import com.meishe.net.server.download.DownloadTask;
import com.meishe.net.temp.TempStringCallBack;
import com.meishe.net.utils.OkLogger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


/**
 * http网络请求客户端、实现者,不建议直接使用
 */
public final class NvsServerClient {

    public static final String DEFAULT_URL_DEBUG = "http://123.57.47.107:18080/";
    public static final String DEFAULT_URL = "https://creative.meishesdk.com/api/";
    /**
     * 默认服务器地址
     */
    private String defaultUrl = "https://vsapi.meishesdk.com";
    /**
     * 云剪辑服务器地址
     * 测试地址：https://testeapi.meishesdk.com:8443
     * 线上地址：https://editor.meishesdk.com:8888
     */
    private String cloudClipUrl = "https://editor.meishesdk.com:8888";
    private Gson mDefaultGson;

    private NvsServerClient() {
        mDefaultGson = new Gson();
    }

    private final static class Holder {
        private static NvsServerClient INSTANCE = new NvsServerClient();
    }

    public static NvsServerClient get() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化配置
     */
    public void initConfig(Application application, String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl)) {
            defaultUrl = baseUrl;
        }
        initOkGo(application);
    }

    public static String getAssetsHost() {
        return DEFAULT_URL;
    }

    /**
     * 初始化第网络请求框架（包含日志开关）
     */
    private void initOkGo(Application application) {
        if (BuildConfig.DEBUG) {
            OkGo.getInstance().init(application);//默认的有日志输出，但是release版不需要有日志输出注释。
            OkLogger.debug(true);
        } else {
            OkLogger.debug(false);//关闭okGo的小部分日志输出
            //关闭okGo的大部分日志输出
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
            builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
            builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
            OkGo.getInstance().init(application).setOkHttpClient(builder.build());
        }
    }

    /**
     * 带header的post请求
     * 一般用于含有特殊字段或者整个json实体类的情况的情况，如：上传文件等
     *
     * @param tag       Object 请求标识，用于取消请求等。
     * @param baseUrl   String 网络请求地址。
     * @param apiName   String 接口名称。
     * @param headerMap Map<String, String> header。
     * @param jsonObj   Object,json格式的参数。
     * @param callback  一般情况下是RequestCallback类型的。
     **/
    public <T> void postWithHeader(Object tag, String baseUrl, String apiName, Map<String, String> headerMap, Object jsonObj, AbsCallback<BaseResponse<T>> callback) {
        PostRequest<BaseResponse<T>> postRequest = createPostRequest(tag, baseUrl, apiName, null, jsonObj);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    postRequest.headers(entry.getKey(), entry.getValue());
                }
            }
        }
        request(postRequest, callback);
    }

    /**
     * post请求
     * 一般用于含有特殊字段或者整个json实体类的情况，如：上传文件等
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param apiName  String 接口名称。
     * @param jsonObj  Object,json格式的参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestPost(Object tag, String apiName, Object jsonObj, AbsCallback<BaseResponse<T>> callback) {
        requestPost(tag, defaultUrl, apiName, jsonObj, callback);
    }

    /**
     * 带header的post请求
     * 一般用于含有特殊字段或者整个json实体类的情况的情况，如：上传文件等
     *
     * @param tag       Object 请求标识，用于取消请求等。
     * @param baseUrl   String 网络请求地址。
     * @param apiName   String 接口名称。
     * @param headerMap Map<String, String> header。
     * @param data      Object,json格式的参数。
     * @param fileMap   Object,json格式的参数。
     * @param callback  一般情况下是RequestCallback类型的。
     **/
    public <T> void postWithHeaderAndFile(Object tag, String baseUrl, String apiName, Map<String, String> headerMap, Map<String, String> data, Map<String, File> fileMap, AbsCallback<BaseResponse<T>> callback) {
        PostRequest<BaseResponse<T>> postRequest = createPostRequest(tag, baseUrl, apiName, data, null);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    postRequest.headers(entry.getKey(), entry.getValue());
                }
            }
        }

        if (fileMap != null) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    postRequest.params(entry.getKey(), entry.getValue());
                }
            }
        }
        request(postRequest, callback);
    }

    /**
     * 带header的get请求
     * 一般用于含有特殊字段或者整个json实体类的情况的情况，如：上传文件等
     *
     * @param tag       Object 请求标识，用于取消请求等。
     * @param baseUrl   String 网络请求地址。
     * @param apiName   String 接口名称。
     * @param headerMap Map<String, String> header。
     * @param jsonObj   Object,json格式的参数。
     * @param callback  一般情况下是RequestCallback类型的。
     **/
    public <T> void getWithHeader(Object tag, String baseUrl, String apiName, Map<String, String> headerMap, Object jsonObj, AbsCallback<BaseResponse<T>> callback) {
        PostRequest<BaseResponse<T>> postRequest = createPostRequest(tag, baseUrl, apiName, null, jsonObj);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    postRequest.headers(entry.getKey(), entry.getValue());
                }
            }
        }
        request(postRequest, callback);
    }

    /**
     * post请求
     * 一般用于含有特殊字段或者整个json实体类的情况的情况，如：上传文件等
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param baseUrl  String 网络请求地址。
     * @param apiName  String 接口名称。
     * @param jsonObj  Object,json格式的参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestPost(Object tag, String baseUrl, String apiName, Object jsonObj, AbsCallback<BaseResponse<T>> callback) {
        request(this.<BaseResponse<T>>createPostRequest(tag, baseUrl, apiName, null, jsonObj), callback);
    }

    /**
     * post请求
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param apiName  String 接口名称。
     * @param map      Map<String, String> 参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestPost(Object tag, String apiName, Map<String, String> map, AbsCallback<BaseResponse<T>> callback) {
        requestPost(tag, defaultUrl, apiName, map, callback);
    }

    /**
     * post请求
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param baseUrl  String 网络请求地址。
     * @param apiName  String 接口名称。
     * @param map      Map<String, String> 参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestPost(Object tag, String baseUrl, String apiName, Map<String, String> map, AbsCallback<BaseResponse<T>> callback) {
        request(this.<BaseResponse<T>>createPostRequest(tag, baseUrl, apiName, map, null), callback);
    }


    /**
     * 带header的get请求
     *
     * @param tag       Object 请求标识，用于取消请求等。
     * @param baseUrl   String 网络请求地址。
     * @param apiName   String 接口名称。
     * @param headerMap Map<String, String> header。
     * @param map       Map<String, String> 参数。
     * @param callback  一般情况下是RequestCallback类型的。
     **/
    public <T> void getWithHeader(Object tag, String baseUrl, String apiName, Map<String, String> headerMap, Map<String, String> map, AbsCallback<BaseResponse<T>> callback) {
        GetRequest<BaseResponse<T>> getRequest = createGetRequest(tag, baseUrl, apiName, map);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    getRequest.headers(entry.getKey(), entry.getValue());
                }
            }
        }
        request(getRequest, callback);
    }

    /**
     * get请求
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param apiName  String 接口名称。
     * @param map      Map<String, String> 参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestGet(Object tag, String apiName, Map<String, String> map, AbsCallback<BaseResponse<T>> callback) {
        requestGet(tag, defaultUrl, apiName, map, callback);
    }

    public <T> void requestObjectGet(String url, final TempStringCallBack callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        okhttp3.Request request = builder.get().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.onResponse(response.body().string());
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new Throwable("errorCode:" + response.code() + ""));
                    }
                }
            }
        });
    }

    public <T> void requestObjectPost(String url,Map<String,String>params, final TempStringCallBack callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        if(null != params && params.size()>0){
            for(String key : params.keySet()){
                requestBodyBuilder.add(key, params.get(key));
            }
        }
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        okhttp3.Request request = builder.get().url(url).post(requestBodyBuilder.build()).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.onResponse(response.body().string());
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new Throwable("errorCode:" + response.code() + ""));
                    }
                }
            }
        });
    }


    /**
     * get请求
     *
     * @param tag      Object 请求标识，用于取消请求等。
     * @param baseUrl  String 网络请求地址。
     * @param apiName  String 接口名称。
     * @param map      Map<String, String> 参数。
     * @param callback 一般情况下是RequestCallback类型的。
     **/
    public <T> void requestGet(Object tag, String baseUrl, String apiName, Map<String, String> map, AbsCallback<BaseResponse<T>> callback) {
        request(this.<BaseResponse<T>>createGetRequest(tag, baseUrl, apiName, map), callback);
    }

    /**
     * 获取get请求体
     *
     * @param tag     Object 请求标识，用于取消请求等。
     * @param url     String 网络请求的url。
     * @param apiName String 接口名称。
     * @param map     Map<String, String> 参数。
     **/
    private <T> GetRequest<T> createGetRequest(Object tag, String url, String apiName, Map<String, String> map) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null,you need initConfig or set url");
        }
        if (url.endsWith("/")) {
            url = url + apiName;
        } else {
            url = url + "/" + apiName;
        }
        return OkGo.<T>get(url).tag(tag).params(map);
    }

    /**
     * 获取post请求体。
     *
     * @param tag     Object 请求标识，用于取消请求等。
     * @param url     String 网络请求的url。
     * @param apiName String 接口名称。
     * @param map     Map<String, String> 参数。
     * @param upJson  参数，一般用于post而且和map互斥。
     **/
    private <T> PostRequest<T> createPostRequest(Object tag, String url, String apiName,
                                                 Map<String, String> map, Object upJson) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null,you need initConfig or set url");
        }
        if (url.endsWith("/")) {
            url = url + apiName;
        } else {
            url = url + "/" + apiName;
        }
        return upJson == null ? OkGo.<T>post(url).tag(tag).params(map) :
                OkGo.<T>post(url).tag(tag).headers("", "").upJson(getDefaultGson().toJson(upJson));
    }

    /**
     * 发起网络请求
     *
     * @param request  已经准备好的request。
     * @param callback 一般情况下是RequestCallback类型的。
     */
    public <T> void request(Request<BaseResponse<T>, ? extends Request> request, AbsCallback<BaseResponse<T>> callback) {
        if (callback == null) {
            callback = new RequestCallback<T>() {
                @Override
                public void onSuccess(BaseResponse<T> response) {

                }

                @Override
                public void onError(BaseResponse<T> response) {

                }
            };
        }
        request.execute(callback);
    }

    public <T> void requestObject(Request<T, ? extends Request> request, AbsCallback<String> callback) {
        if (callback == null) {
            callback = new AbsCallback<String>() {

                @Override
                public String convertResponse(okhttp3.Response response) throws Throwable {
                    return response.body().toString();
                }

                @Override
                public void onSuccess(Response<String> response) {

                }
            };
        }
        request.execute((Callback<T>) callback);
    }

    /**
     * 下载实现方法
     *
     * @param tag      下载标识
     * @param url      下载地址
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param listener 下载监听器
     */
    public void download(String tag, String url, String filePath, String fileName, final DownloadListener listener) {
        if (!TextUtils.isEmpty(tag) && OkDownload.getInstance().hasTask(tag)) {
            OkDownload.getInstance().removeTask(tag);
        }
        if (!TextUtils.isEmpty(url)) {
            if (TextUtils.isEmpty(tag)) {
                tag = url;
            }
            // 构建下载请求
            GetRequest<File> request = OkGo.<File>get(url)
                    .retryCount(1);
            // 这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
            final DownloadTask downloadTask = OkDownload.request(tag, request)
                    //文件路径
                    .folder(filePath)
                    // 文件名
                    .fileName(fileName)
                    .save();
            //监听器
            downloadTask.register(new DownloadListener(tag) {
                @Override
                public void onStart(Progress progress) {
                    if (listener != null) {
                        listener.onStart(progress);
                    }
                }

                @Override
                public void onProgress(Progress progress) {
                    if (listener != null) {
                        listener.onProgress(progress);
                    }
                }

                @Override
                public void onError(Progress progress) {
                    if (listener != null) {
                        listener.onError(progress);
                    }
                    downloadTask.unRegister(this);
                }

                @Override
                public void onFinish(File file, Progress progress) {
                    if (listener != null) {
                        listener.onFinish(file, progress);
                    }
                    downloadTask.unRegister(this);

                }

                @Override
                public void onRemove(Progress progress) {
                    if (listener != null) {
                        listener.onRemove(progress);
                    }
                    downloadTask.unRegister(this);
                }
            });
            downloadTask.start();
        }

    }

    /**
     * 获取正在下载的任务
     * Gets the task being downloaded
     *
     * @param tag the task tag
     */
    public DownloadTask getDownloadTask(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        return OkDownload.getInstance().getTask(tag);
    }

    /**
     * 取消正在下载的任务
     * Cancel the task being downloaded
     *
     * @param tag the task tag
     */
    public void cancelDownloadTask(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        DownloadTask task = OkDownload.getInstance().getTask(tag);
        if (task != null) {
            task.remove();
        }
    }

    /**
     * 取消所有请求
     */
    public void cancelAll() {
        OkGo.getInstance().cancelAll();
    }

    /**
     * 取消某个请求
     *
     * @param tag 唯一标识
     */
    public void cancelRequest(Object tag) {
        OkGo.getInstance().cancelTag(tag);
    }

    /**
     * 获取默认的Gson实例
     */
    public Gson getDefaultGson() {
        return mDefaultGson;
    }

    public String getCloudClipUrl() {
        return cloudClipUrl;
    }

    public void setCloudClipUrl(String cloudClipUrl) {
        this.cloudClipUrl = cloudClipUrl;
    }
}
