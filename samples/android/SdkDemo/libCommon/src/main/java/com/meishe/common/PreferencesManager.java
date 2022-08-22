package com.meishe.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.meishe.base.utils.GsonUtils;
import com.meishe.base.utils.SpUtils;

import java.io.File;


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * 版权所有www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate : 2020/11/30
 * @Description :本地存储管理类 Local storage manager
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class PreferencesManager {
    private final String SETTING_PARAMS = "setting.params";
    private final String AGREE_PRIVACY_POLICY = "isAgreePrivacy";
    private final String AUTHOR_END_TIME = "end_timestamp";
    private final String AUTHOR_FILE_PATH = "author_file_path";
    private SpUtils mSpUtils;

    private static class Holder {
        private static final PreferencesManager INSTANCE = new PreferencesManager();
    }

    private PreferencesManager() {
    }


    public static PreferencesManager get() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化
     * initialize
     * @param context Context 上下文
     */
    public void init(Context context) {
        mSpUtils = new SpUtils(context, "nvs_app");
        adaptOldData(context);
    }

    /**
     * 适配旧的数据
     * adapt old data
     */
    private void adaptOldData(Context context) {
        @SuppressLint("SdCardPath") String spDir = "/data/data/" + context.getPackageName() + "/shared_prefs/";
        File fileDefault = new File(spDir + "default.xml");
        if (fileDefault.exists()) {
            SpUtils spUtils = new SpUtils(context, "default");
            mSpUtils.putString(SETTING_PARAMS, spUtils.getString("paramter", null));
            mSpUtils.putBoolean(AGREE_PRIVACY_POLICY, spUtils.getBoolean("isAgreePrivacy"));
            spUtils.clear();
            fileDefault.delete();
        }
        File old2 = new File(spDir + "ms_share_date.xml");
        if (old2.exists()) {
            SpUtils spUtils = new SpUtils(context, "ms_share_date");
            mSpUtils.putLong(AUTHOR_END_TIME, spUtils.getLong("end_timestamp", 0));
            mSpUtils.putString(AUTHOR_FILE_PATH, spUtils.getString("author_file_path", ""));
            spUtils.clear();
            old2.delete();
        }
    }

    /**
     * 清除所有存储的值
     * Clears all stored values
     *
     * @return the boolean
     */
    public boolean clear() {
        return mSpUtils.clear();
    }

    /**
     * 保存浮点类型的值
     * Save floating-point values
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value float 需要保存的值 The value you need to save
     */
    public boolean putFloat(String key, float value) {
        return mSpUtils.putFloat(key, value);
    }

    /**
     * 获取对应浮点类型的值
     * Gets the value of the corresponding floating point type
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue float 默认的值 the default value
     * @return float 所需的value
     */
    public float getFloat(String key, float defaultValue) {
        return mSpUtils.getFloat(key, defaultValue);
    }

    /**
     * 保存long类型的值
     * Save the value of type LONG
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value long 需要保存的值 The value you need to save
     */
    public boolean putLong(String key, long value) {
        return mSpUtils.putLong(key, value);
    }

    /**
     * 获取long类型的值
     * Gets the value of type LONG
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue float 默认的值 The default value you need
     * @return long 所需的value
     */
    public Long getLong(String key, long defaultValue) {
        return mSpUtils.getLong(key, defaultValue);
    }

    /**
     * 获取所需浮点类型的值
     * Save the value of type float
     *
     * @param key String 保存的key值 Saved key value
     */
    public float getFloat(String key) {
        return getFloat(key, 1f);
    }

    /**
     * 保存String类型的值
     * Save the value of type String
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value String 需要保存的值 The value you need to save
     */
    public boolean putString(String key, String value) {
        return mSpUtils.putString(key, value);
    }

    /**
     * 获取所需类型的值
     * Gets the value of type String
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue String 默认的值 The default value
     * @return String 所需的value
     */
    public String getString(String key, String defaultValue) {
        return mSpUtils.getString(key, defaultValue);
    }

    /**
     * 获取对应String类型的值
     * Get the value of type String
     *
     * @param key String 保存的key值 Saved key value
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 保存json对象
     * Save the value of type object
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value Object 需要保存的值 The value you need to save
     */
    public boolean putJson(String key, Object value) {
        return mSpUtils.putJson(key, value);
    }

    /**
     * 获取所需类型的值
     * Gets the value of type json
     *
     * @param key String 已保存的key值 Saved key value
     * @return String 所需的value
     */
    public <T> T getJson(String key, Class<T> type) {
        return mSpUtils.getJson(key, type);
    }

    /**
     * 保存int类型的值
     * Save the value of type int
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value int 需要保存的值 The value you need to save
     */
    public boolean putInt(String key, int value) {
        return mSpUtils.putInt(key, value);
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type int
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue int 需要保存的值 The default value
     */
    public int getInt(String key, int defaultValue) {
        return mSpUtils.getInt(key, defaultValue);
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type int
     *
     * @param key String 保存的key值
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * 保存boolean类型的值
     * Save the value of type boolean
     *
     * @param key   String 保存的key值 The key value you need to save
     * @param value boolean 需要保存的值 The key value you need to save
     */
    public boolean putBoolean(String key, boolean value) {
        return mSpUtils.putBoolean(key, value);
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type boolean
     *
     * @param key          String 已保存的key值 Saved key value
     * @param defaultValue boolean 需要保存的值 The default value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return mSpUtils.getBoolean(key, defaultValue);
    }

    /**
     * 获取所需对应类型的值
     * Gets the value of type boolean
     *
     * @param key String 已保存的key值 Saved key value
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 保存鉴权到期的时间戳
     * Save the timestamp when the authentication rights expire
     *
     * @param timestamp long 要保存的时间戳 the timestamp
     */
    public void setAuthorEndTime(long timestamp) {
        mSpUtils.putLong(AUTHOR_END_TIME, timestamp);
    }

    /**
     * 获取鉴权到期的时间戳
     * Gets the timestamp when the authentication rights expire
     *
     * @return the author end time
     */
    public long getAuthorEndTime() {
        return mSpUtils.getLong(AUTHOR_END_TIME, 0);
    }

    /**
     * 保存鉴权文件路径
     * Save the authentication file path
     *
     * @param path String 要保存文件路径 the authentication file path
     */
    public void setAuthorFilePath(String path) {
        mSpUtils.putString(AUTHOR_FILE_PATH, path);
    }

    /**
     * 获取鉴权文件路径
     * Gets  the authentication file path
     *
     * @return the author file path
     */
    public String getAuthorFilePath() {
        return mSpUtils.getString(AUTHOR_FILE_PATH);
    }

    /**
     * 保存设置页面设置的一些参数
     * Save some of the parameters set on the Settings page
     *
     * @param params String 要保存参数 the parameters need to save
     */
    public void setSettingParams(String params) {
        mSpUtils.putString(SETTING_PARAMS, params);
    }

    /**
     * 获取设置参数
     * Gets some of the parameters set on the Settings page
     *
     * @return the setting params
     */
    public String getSettingParams() {
        String params = mSpUtils.getString(SETTING_PARAMS);
        if (TextUtils.isEmpty(params)) {
            params = GsonUtils.toJson(new Object());
            setSettingParams(params);
        }
        return params;
    }

    /**
     * 设置是否同意隐私条款
     * Set whether the privacy policy is agreed or not
     *
     * @param agree boolean 是否同意 true agree false disagree
     */
    public void setAgreePrivacy(boolean agree) {
        mSpUtils.putBoolean(AGREE_PRIVACY_POLICY, agree);
    }

    /**
     * 是否同意隐私条款
     * whether the privacy policy is agreed or not
     *
     * @return the boolean
     */
    public boolean isAgreePrivacy() {
        return mSpUtils.getBoolean(AGREE_PRIVACY_POLICY);
    }
}
