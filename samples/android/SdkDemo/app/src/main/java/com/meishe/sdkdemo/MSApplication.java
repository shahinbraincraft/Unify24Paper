package com.meishe.sdkdemo;

import static com.meishe.sdkdemo.BuildConfig.UMENG_KEY;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.app.BaseApp;
import com.meishe.base.constants.AndroidOS;
import com.meishe.http.HttpConstants;
import com.meishe.common.PreferencesManager;
import com.meishe.net.NvsServerClient;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.sdkdemo.utils.CrashHandler;
import com.meishe.sdkdemo.utils.Logger;
import com.meishe.sdkdemo.utils.SpUtil;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;


/**
 * @author gexinyu on 2018/5/24.
 */

public class MSApplication extends BaseApp {
    private static String sdkVersion = "";

    public static String getSdkVersion() {
        return sdkVersion;
    }

    public static String getAppId() {
        return getContext().getPackageName();
    }

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        AndroidOS.initConfig(mContext);
        Logger.e("MSApplication", "onCreate");
//        if (BuildConfig.DEBUG) {
//            String traceFileDir = PathUtils.getTraceFileDir();
//            if (!TextUtils.isEmpty(traceFileDir)){
//                Log.d("MSApplication=",traceFileDir);
//                Debug.startMethodTracing(traceFileDir+"/load.trace");
//            }
//        }

        /*
         * 初始化
         * initialization
         * */
        mContext = this;
        NvsStreamingContext.setIconSize(1);
        NvsStreamingContext.setMaxReaderCount(4);
        String licensePath = "assets:/meishesdk.lic";
        TimelineUtil.initStreamingContext(getContext(), licensePath);
        TimelineUtil.initEffectSdkContext(getContext(), licensePath);


        NvAssetManager.init(getContext());

        Fresco.initialize(this);

        NvsStreamingContext instance = NvsStreamingContext.getInstance();
        if (instance != null) {
            NvsStreamingContext.SdkVersion version = instance.getSdkVersion();
            if (version != null) {
                MSApplication.sdkVersion = version.majorVersion + "." + version.minorVersion + "." + version.revisionNumber;
            }
            //HDR亮度调节 增亮
            instance.setColorGainForSDRToHDR(2);
        }

//        if (BuildConfig.DEBUG) {
//            NvsServerClient.get().initConfig(this, HttpConstants.HOST_HTTP + "://" + HttpConstants.HOST_DEBUG);
//        } else {
            NvsServerClient.get().initConfig(this, HttpConstants.HOST_HTTPS + "://" + HttpConstants.HOST);
//        }
        PreferencesManager.get().init(this);
        String channel = AnalyticsConfig.getChannel(this);
        UMConfigure.preInit(this,UMENG_KEY, channel);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        Log.d("lhz","channel="+channel);
        if(SpUtil.getInstance(this).getBoolean(Constants.KEY_AGREE_PRIVACY, false)){
            UMConfigure.init(this,UMENG_KEY,channel,UMConfigure.DEVICE_TYPE_PHONE,"");
        }
//        String crashFilePath = PathUtils.getFolderDirPath( File.separator + "crash" );
//        Log.e("MSApplication","crashFilePath:"+crashFilePath);
        // 手机crash日志的路径：/storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/crash
        if (BuildConfig.DEBUG) {
            CrashHandler.getInstance().init(getContext());
//            Debug.stopMethodTracing();
        }
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.e("MSApplication", "onTerminate");
        NvsEffectSdkContext.close();
        NvsStreamingContext.close();
    }
}
