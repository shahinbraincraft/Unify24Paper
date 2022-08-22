package com.czc.cutsame.util;


import com.czc.cutsame.BuildConfig;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/3/22 10:17
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ConfigUtil {
    public static final String BUILD_TYPE_TOB = "2B";
    public static final String BUILD_TYPE_TOC = "2C";

    public static boolean isToC() {
        return BUILD_TYPE_TOC.equals(BuildConfig.TO_TYPE);
    }
}
