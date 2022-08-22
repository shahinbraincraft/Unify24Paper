package com.meishe.fxplugin.utils;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;

import com.meicam.sdk.NvsVideoResolution;
import com.meishe.fxplugin.utils.asset.NvAsset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Util {
    private final static String TAG = "Util";


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String processName = getProcessName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return true;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(processName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false;
            }
        }

        return true;

    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }


    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 判断当前文件是否存在
     * Determine if the current file exists
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            return f.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * 获取所有权限列表(相机权限，麦克风权限，存储权限)
     * Get a list of all permissions (camera permissions, microphone permissions, storage permissions)
     * */
    public static List<String> getAllPermissionsList() {
        ArrayList<String> newList = new ArrayList<>();
        newList.add(Manifest.permission.CAMERA);
        newList.add(Manifest.permission.RECORD_AUDIO);
        newList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        newList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return newList;
    }

    public static NvsVideoResolution getVideoEditResolution(int ratio) {
        int compileRes = 720;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        if (ratio == NvAsset.AspectRatio_16v9) {
            size.set(compileRes * 16 / 9, compileRes);
        } else if (ratio == NvAsset.AspectRatio_1v1) {
            size.set(compileRes, compileRes);
        } else if (ratio == NvAsset.AspectRatio_9v16) {
            size.set(compileRes, compileRes * 16 / 9);
        } else if (ratio == NvAsset.AspectRatio_3v4) {
            size.set(compileRes, compileRes * 4 / 3);
        } else if (ratio == NvAsset.AspectRatio_4v3) {
            size.set(compileRes * 4 / 3, compileRes);
        } else {
            size.set(1280, 720);
        }
        videoEditRes.imageWidth = size.x;
        videoEditRes.imageHeight = size.y;
        Logger.e("getVideoEditResolution   ", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;
    }
//
//    /**
//     * 根据x坐标进行排序
//     * Sort by x coordinate
//     */
//    public static class PointXComparator implements Comparator<PointF> {
//
//        @Override
//        public int compare(PointF bean1, PointF bean2) {
//            return (int) (bean1.x - bean2.x);
//        }
//    }
//
//    /**
//     * 根据x坐标进行排序
//     * Sort by x coordinate
//     */
//    public static class PointYComparator implements Comparator<PointF> {
//
//        @Override
//        public int compare(PointF bean1, PointF bean2) {
//            return (int) (bean1.y - bean2.y);
//        }
//    }
//
//    public static void showDialog(Context context, final String title, final String first_tip) {
//        final CommonDialog dialog = new CommonDialog(context, 1);
//        dialog.setOnCreateListener(new CommonDialog.OnCreateListener() {
//            @Override
//            public void OnCreated() {
//                dialog.setTitleTxt(title);
//                dialog.setFirstTipsTxt(first_tip);
//            }
//        });
//        dialog.setOnBtnClickListener(new CommonDialog.OnBtnClickListener() {
//            @Override
//            public void OnOkBtnClicked(View view) {
//                dialog.dismiss();
//            }
//
//            @Override
//            public void OnCancelBtnClicked(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
//
////    public static void showDialog(Context context, final String title, final String first_tip, final TipsButtonClickListener tipsButtonClickListener) {
//    public static void showDialog(Context context, final String title, final String first_tip, final View.OnClickListener listener) {
//        final CommonDialog dialog = new CommonDialog(context, 1);
//        dialog.setOnCreateListener(new CommonDialog.OnCreateListener() {
//            @Override
//            public void OnCreated() {
//                dialog.setTitleTxt(title);
//                dialog.setFirstTipsTxt(first_tip);
//            }
//        });
//        dialog.setOnBtnClickListener(new CommonDialog.OnBtnClickListener() {
//            @Override
//            public void OnOkBtnClicked(View view) {
//                dialog.dismiss();
//                if (listener != null)
//                    listener.onClick(view);
//            }
//
//            @Override
//            public void OnCancelBtnClicked(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    public static boolean getBundleFilterInfo(Context context, ArrayList<NvAsset> assetArrayList, String bundlePath) {
//        if (context == null)
//            return false;
//
//        if (TextUtils.isEmpty(bundlePath))
//            return false;
//
//        try {
//            InputStream nameListStream = context.getAssets().open(bundlePath);
//            BufferedReader nameListBuffer = new BufferedReader(new InputStreamReader(nameListStream, "GBK"));
//
//            String strLine;
//            while ((strLine = nameListBuffer.readLine()) != null) {
//                String[] strNameArray = strLine.split(",");
//                if (strNameArray.length < 3)
//                    continue;
//
//                for (int i = 0; i < assetArrayList.size(); ++i) {
//                    NvAsset assetItem = assetArrayList.get(i);
//                    if (assetItem == null)
//                        continue;
//
//                    if (!assetItem.isReserved)
//                        continue;
//
//                    String packageId = assetItem.uuid;
//                    if (TextUtils.isEmpty(packageId))
//                        continue;
//
//                    if (packageId.equals(strNameArray[0])) {
//                        assetItem.name = strNameArray[1];
//                        assetItem.aspectRatio = Integer.parseInt(strNameArray[2]);
//                        break;
//                    }
//
//                }
//            }
//            nameListBuffer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 将bitmap保存到SD卡
//     *
//     * Save bitmap to SD card
//     *
//     */
//    public static boolean saveBitmapToSD(Bitmap bt, String target_path) {
//        if (bt == null || target_path == null || target_path.isEmpty()) {
//            return false;
//        }
//        File file = new File(target_path);
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bt.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//            return true;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 将bitmap 转换为BGRA数组（四通道）
//     * @param context
//     * @param bitmap
//     * @return
//     */
//    public static byte[] bitmap2BGRAData(Context context, Bitmap bitmap) {
//
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] intValues = new int[width * height];
//        bitmap.getPixels(intValues, 0, width, 0, 0, width,
//                height);
//        byte[] rgba = new byte[width * height * 4];
//        byte[] r = new byte[width * height];
//        byte[] g = new byte[width * height];
//        byte[] b = new byte[width * height];
//        for (int i = 0; i < intValues.length; ++i) {
//            final int val = intValues[i];
//            rgba[i * 4] = (byte) (val & 0xFF);//B
//            rgba[i * 4 + 1] = (byte) ((val >> 8) & 0xFF);//G
//            rgba[i * 4 + 2] = (byte) ((val >> 16) & 0xFF);//R
//            rgba[i * 4 + 3] = (byte) (1);//A
//        }
//        return rgba;
//    }
}
