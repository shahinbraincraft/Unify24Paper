package com.meishe.sdkdemo.edit.filter;

import android.content.Intent;
import android.view.View;

import com.meicam.sdk.NvsTimeline;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.TimelineUtil;
import com.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;

import java.util.ArrayList;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yyj
 * @CreateDate : 2019/6/28.
 * @Description :视频编辑-滤镜-Activity
 * @Description :VideoEdit-Filter-Activity
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class FilterActivity extends BaseFilterActivity {

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.filterAssetFinish) {
            // save data
            TimelineData.instance().setVideoClipFxData(mVideoClipFxInfo);
            removeTimeline();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            quitActivity();
        }
    }

    @Override
    protected VideoClipFxInfo initClipFxInfo() {
        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        if (videoClipFxData == null) {
            videoClipFxData = new VideoClipFxInfo();
        }
        return videoClipFxData;
    }

    @Override
    protected NvsTimeline initTimeLine() {
        NvsTimeline timeline = TimelineUtil.createTimeline();
        if (timeline == null) {
            return null;
        }

        TimelineUtil.applyTheme(timeline, null);
        /*
         * 移除主题，则需要删除字幕，然后重新添加，防止带片头主题删掉字幕
         * To remove a topic, you need to delete the subtitle and then add it again to prevent the title from deleting the subtitle
         * */
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(timeline, captionArray);
        return timeline;
    }

    @Override
    protected void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter) {
        TimelineUtil.buildTimelineFilter(timeline, changedClipFilter);
    }
}
