package com.meishe.fxplugin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.meicam.sdk.NvsStreamingContext;

import java.io.File;
import java.util.ArrayList;

public class MSApplication extends Application {

    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        addPlugin();
    }

    private void addPlugin() {
        String libPath  = getApplicationInfo().nativeLibraryDir + "/libfxplugin.so";
        ArrayList<String> fxPluginBundlePathList = new ArrayList<String>();
        fxPluginBundlePathList.add(libPath);
        NvsStreamingContext.setFxPluginBundlePathList(fxPluginBundlePathList);
    }
}
