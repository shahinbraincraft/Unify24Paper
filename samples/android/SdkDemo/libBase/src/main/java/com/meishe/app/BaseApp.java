package com.meishe.app;

import android.app.Application;
import android.content.Context;

import com.meishe.base.utils.Utils;

/**
 * 父类Application为了传递模块间基本数据标记以及共有方法存放
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2021/9/18.
 */
public class BaseApp extends Application {
    private static Context context;
    public static boolean CONTENT_FLAG = true;//是否兼容鸿蒙系统contentUri读取失败
    private static boolean zhFlag = false;
    public static boolean isZh() {
        return zhFlag;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        zhFlag = Utils.isZh();

    }
}
