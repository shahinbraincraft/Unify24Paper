package com.meishe.engine.interf;

import android.content.Context;

import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsTimeline;
import com.meishe.engine.bean.AnimationData;
import com.meishe.engine.bean.MaskInfoData;
import com.meishe.engine.bean.MeicamCompoundCaptionClip;
import com.meishe.engine.bean.MeicamVideoClip;

/**
 * 编辑Timeline对象的方法
 */
public interface EditOperater {


    /**
     * 获取当前timeline
     *
     * @return
     */
    NvsTimeline getCurrentTimeline();

    /**
     * 获取编辑的VideoClip
     *
     * @param timeStamp 当前timeline的时间戳
     * @return
     */
    MeicamVideoClip getEditVideoClip(long timeStamp, int trackIndex);

    /**
     * 获取当前timeline的时间戳
     * * @return
     */
    long getCurrentTimelinePosition();

}
