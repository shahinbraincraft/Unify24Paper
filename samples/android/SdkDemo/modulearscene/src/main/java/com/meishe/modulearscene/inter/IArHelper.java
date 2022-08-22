package com.meishe.modulearscene.inter;

import android.content.Context;

import com.meicam.sdk.NvsFx;
import com.meishe.modulearscene.bean.ArBean;

import java.util.List;

/**
 * @author zcy
 * @Destription:
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public interface IArHelper {

    /**
     * 初始化
     */
    public void init();

    /**
     * 应用数据
     *
     * @param arSceneFx
     * @param arBean
     */
    public void applyData(NvsFx arSceneFx, ArBean arBean);

    /**
     * 获取美颜数据
     *
     * @return
     */
    public List<ArBean> getBeautyData(Context context);

    /**
     * 获取美型数据
     *
     * @return
     */
    public List<ArBean> getShapeData(Context context);

    /**
     * 获取微整形数据
     *
     * @return
     */
    public List<ArBean> getMicroShapeData(Context context);

    /**
     * 获取美肤
     * @param mContext
     * @return
     */
    List<ArBean> getBeautySkinData(Context mContext);
}
