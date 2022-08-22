package com.meishe.fxplugin.data;

import com.meicam.sdk.NvsVideoResolution;
import com.meishe.fxplugin.utils.asset.NvAsset;

import java.util.ArrayList;

import static com.meishe.fxplugin.utils.Constants.VIDEOVOLUME_DEFAULTVALUE;


public class TimelineData {

    private static TimelineData m_timelineData;
    NvsVideoResolution m_videoResolution;

    private ArrayList<ClipInfo> m_clipInfoArray;

    /*
    * 主题包ID
    * Theme package ID
    * */
    private String m_themeId;
    /*
    * 主题字幕片头
    * Theme title
    * */
    private String m_themeCptionTitle = "";
    /*
    * 主题字幕片尾
    *  Theme trailer;
    * */
    private String m_themeCptionTrailer = "";//

    private float m_musicVolume = VIDEOVOLUME_DEFAULTVALUE;
    private float m_originVideoVolume = VIDEOVOLUME_DEFAULTVALUE;
    private float m_recordVolume = VIDEOVOLUME_DEFAULTVALUE;
    /*
    * 默认值，不作比例适配
    * Default value, no scaling
    * */
    private int m_makeRatio = NvAsset.AspectRatio_NoFitRatio;//

    public String getThemeCptionTitle() {
        return m_themeCptionTitle;
    }

    public void setThemeCptionTitle(String themeCptionTitle) {
        this.m_themeCptionTitle = themeCptionTitle;
    }

    public String getThemeCptionTrailer() {
        return m_themeCptionTrailer;
    }

    public void setThemeCptionTrailer(String themeCptionTrailer) {
        this.m_themeCptionTrailer = themeCptionTrailer;
    }

    public float getMusicVolume() {
        return m_musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.m_musicVolume = musicVolume;
    }

    public float getOriginVideoVolume() {
        return m_originVideoVolume;
    }

    public void setOriginVideoVolume(float originVideoVolume) {
        this.m_originVideoVolume = originVideoVolume;
    }

    public float getRecordVolume() {
        return m_recordVolume;
    }

    public void setRecordVolume(float recordVolume) {
        this.m_recordVolume = recordVolume;
    }

    public int getMakeRatio() {
        return m_makeRatio;
    }

    public void setMakeRatio(int makeRatio) {
        this.m_makeRatio = makeRatio;
    }

    public void setVideoResolution(NvsVideoResolution resolution) {
        m_videoResolution = resolution;
    }

    public NvsVideoResolution getVideoResolution() {
        return m_videoResolution;
    }

    public NvsVideoResolution cloneVideoResolution() {
        if (m_videoResolution == null)
            return null;

        NvsVideoResolution resolution = new NvsVideoResolution();
        resolution.imageWidth = m_videoResolution.imageWidth;
        resolution.imageHeight = m_videoResolution.imageHeight;
        return resolution;
    }

    public void setClipInfoData(ArrayList<ClipInfo> clipInfoArray) {
        this.m_clipInfoArray = clipInfoArray;
    }

    public ArrayList<ClipInfo> getClipInfoData() {
        return m_clipInfoArray;
    }

    public ArrayList<ClipInfo> cloneClipInfoData() {
        ArrayList<ClipInfo> newArrayList = new ArrayList<>();
        for (ClipInfo clipInfo : m_clipInfoArray) {
            ClipInfo newClipInfo = clipInfo.clone();
            newArrayList.add(newClipInfo);
        }
        return newArrayList;
    }

    public void resetClipTrimInfo() {
        for (int i = 0; i < m_clipInfoArray.size(); i++) {
            ClipInfo clipInfo = m_clipInfoArray.get(i);
            clipInfo.changeTrimIn(-1);
            clipInfo.changeTrimOut(-1);
        }
    }

    public void clear() {
        if (m_clipInfoArray != null) {
            m_clipInfoArray.clear();
        }


        m_musicVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_originVideoVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_recordVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_videoResolution = null;
        m_themeId = "";
//        cleanWaterMarkData();
    }

    public void addClip(ClipInfo clipInfo) {
        m_clipInfoArray.add(clipInfo);
    }

    public void removeClip(int index) {
        if (index < m_clipInfoArray.size()) {
            m_clipInfoArray.remove(index);
        }
    }

    public int getClipCount() {
        return m_clipInfoArray.size();
    }


    public String getThemeData() {
        return m_themeId;
    }

    public void setThemeData(String themeData) {
        this.m_themeId = themeData;
    }



    private TimelineData() {
        m_clipInfoArray = new ArrayList<>();
        m_videoResolution = new NvsVideoResolution();
    }

    public static TimelineData init() {
        if (m_timelineData == null) {
            synchronized (TimelineData.class) {
                if (m_timelineData == null) {
                    m_timelineData = new TimelineData();
                }
            }
        }
        return m_timelineData;
    }

    public static TimelineData instance() {
        if (m_timelineData == null) {
            m_timelineData = new TimelineData();
        }
        return m_timelineData;
    }

}
