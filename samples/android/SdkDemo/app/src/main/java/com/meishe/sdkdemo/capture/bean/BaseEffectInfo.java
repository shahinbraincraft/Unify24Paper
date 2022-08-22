package com.meishe.sdkdemo.capture.bean;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/25 下午4:25
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseEffectInfo {

    /**
     * 特效类别
     */
    public int type;

    /**
     * 特效类型
     */
    public int captureVideoFxType;

    /**
     * 包类型安装的id
     */
    public String uuid;

    /**
     *内建特效名字
     */
    public String fxName;

    /**
     * 调节特效的key
     */
    public String fxParam;
    /**
     * 特效强度
     */
    public float strength;

    public static class CaptureVideoFxType{
        static final int TYPE_BUILTIN = 0;
        static final int TYPE_PACKAGE = 1;
        static final int TYPE_CUSTOM = 2;
    }

}
