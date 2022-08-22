package com.meishe.sdkdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.meishe.sdkdemo.MSApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局异常捕获
 * <p>
 * Global exception catch
 * Created by ms on 2018/9/18.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final String TAG = "CrashHandler";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    private String errorFilePath = "crashHandle";
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;


    private static CrashHandler instance;  //单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

    private CrashHandler() {
    }


    public synchronized static CrashHandler getInstance() {  //同步方法，以免单例多线程环境下出现异常
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {  //初始化，把当前对象设置成UncaughtExceptionHandler处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = ctx.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            dumpExceptionToSDCard(ex);
            uploadExceptionToSetver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ex.printStackTrace();
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            //  主动杀掉程序
            // TODO: 2017/7/28 出现异常是否需要退出程序处理。
            //  Process.killProcess(Process.myPid());
        }
    }

    /**
     * 错误日志存储到本地SD卡
     *  崩溃日志路径
     * /storage/emulated/0/Android/data/com.meishe.ms106sdkdemo/files/crashHandle/crash/crash_2022-04-19_17:20:21.txt
     * @param ex
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        Log.d(TAG,"dumpExceptionToSDCard------");
        if (MSApplication.getContext() == null) {
            return;
        }

        String crashFilePath = PathUtils.getFolderDirPath(errorFilePath+ File.separator + FILE_NAME );
        if (TextUtils.isEmpty(crashFilePath)){
            Log.d(TAG,"crashFilePath is null");
            return;
        }

        File dir = new File(crashFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date(current));
        File file = new File(crashFilePath + File.separator+ "crash_"
                + time + FILE_NAME_SUFFIX);

        Log.d(TAG,"file path=="+file.getAbsolutePath());
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    /**
     * 存储手机型号信息
     *
     * @param pw
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.write("App Version: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        pw.print("Mode: ");
        pw.println(Build.MODEL);

        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    /**
     * 上传服务器
     */
    private void uploadExceptionToSetver() {
        // TODO: 2017/7/28 是否需要上传服务器


    }
}

