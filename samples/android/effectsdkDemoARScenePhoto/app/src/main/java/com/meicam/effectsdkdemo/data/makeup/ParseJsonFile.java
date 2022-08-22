package com.meicam.effectsdkdemo.data.makeup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author admin
 * @date 2018/11/28
 */
public class ParseJsonFile {
    private static final String TAG = "ParseJsonFile";

    /**
     * Json转Java对象
     * Json to Java Object
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return new Gson( ).fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return new Gson( ).fromJson(json, typeOfT);
    }



    public static String readAssetJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder( );
        try {
            InputStream inputStream = context.getAssets( ).open(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine( )) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.d(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close( );
                }
            } catch (Exception e) {
                Log.d(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString( );
    }

    public static String readSDJsonFile(String jsonFilePath) {
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder( );
        try {
            FileInputStream inputStream = new FileInputStream(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine( )) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.d(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close( );
                }
            } catch (Exception e) {
                Log.d(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString( );
    }
}
