package com.meishe.fxplugin.data;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by admin on 2018/7/11.
 */

public class BackupData {
    private static BackupData mAssetDataBackup;
    private ArrayList<ClipInfo> mClipInfoArray;
    private int mCaptionZVal;

    private Point imageDimension;
    /*
    * 贴纸和字幕使用
    * For stickers and subtitles
    * */
    private long m_curSeekTimelinePos = 0;
    /*
    * 片段编辑专用
    * For clip editing
    * */
    private int m_clipIndex;
    /*
    * 画中画页面使用
    * For PIP pages
    * */
    private ArrayList<String> mPicInPicVideoArray;

    /*
    * 只在EditActivity点击添加视频使用
    * Use only in EditActivity click to add video
    * */
    private ArrayList<ClipInfo> mAddClipInfoList;

//    /*
//    * 只在翻转字幕编辑文本时使用
//    * Only used when flipping subtitles to edit text
//    * */
//    private ArrayList<FlipCaptionDataInfo> mFlipDataInfoList;
//
//    public ArrayList<FlipCaptionDataInfo> getFlipDataInfoList() {
//        return mFlipDataInfoList;
//    }
//    public ArrayList<FlipCaptionDataInfo> cloneFlipCaptionData() {
//        ArrayList<FlipCaptionDataInfo> newList = new ArrayList<>();
//        for(FlipCaptionDataInfo flipCaptionInfo:mFlipDataInfoList) {
//            FlipCaptionDataInfo newFlipCaptionInfo = flipCaptionInfo.clone();
//            newList.add(newFlipCaptionInfo);
//        }
//        return newList;
//    }
//
//    public void setFlipDataInfoList(ArrayList<FlipCaptionDataInfo> flipDataInfoList) {
//        this.mFlipDataInfoList = flipDataInfoList;
//    }


    public Point getImageDimension() {
        return imageDimension;
    }

    public void setImageDimension(Point imageDimension) {
        this.imageDimension = imageDimension;
    }

    public ArrayList<String> getPicInPicVideoArray() {
        return mPicInPicVideoArray;
    }

    public void setPicInPicVideoArray(ArrayList<String> picInPicVideoArray) {
        this.mPicInPicVideoArray = picInPicVideoArray;
    }

    public ArrayList<ClipInfo> getAddClipInfoList() {
        return mAddClipInfoList;
    }

    public void setAddClipInfoList(ArrayList<ClipInfo> addClipInfoList) {
        this.mAddClipInfoList = addClipInfoList;
    }
    public void clearAddClipInfoList() {
        mAddClipInfoList.clear();
    }
    public int getClipIndex() {
        return m_clipIndex;
    }

    public void setClipIndex(int clipIndex) {
        this.m_clipIndex = clipIndex;
    }

    public long getCurSeekTimelinePos() {
        return m_curSeekTimelinePos;
    }

    public void setCurSeekTimelinePos(long curSeekTimelinePos) {
        this.m_curSeekTimelinePos = curSeekTimelinePos;
    }

    public int getCaptionZVal() {
        return mCaptionZVal;
    }

    public void setCaptionZVal(int captionZVal) {
        this.mCaptionZVal = captionZVal;
    }

    public void setClipInfoData(ArrayList<ClipInfo> clipInfoArray) {
        this.mClipInfoArray = clipInfoArray;
    }

    public ArrayList<ClipInfo> getClipInfoData() {
        return mClipInfoArray;
    }

    public ArrayList<ClipInfo> cloneClipInfoData() {
        ArrayList<ClipInfo> newArrayList = new ArrayList<>();
        for(ClipInfo clipInfo:mClipInfoArray) {
            ClipInfo newClipInfo = clipInfo.clone();
            newArrayList.add(newClipInfo);
        }
        return newArrayList;
    }


    public static BackupData init() {
        if (mAssetDataBackup == null) {
            synchronized (BackupData.class){
                if (mAssetDataBackup == null) {
                    mAssetDataBackup = new BackupData();
                }
            }
        }
        return mAssetDataBackup;
    }

    public void clear() {
        clearAddClipInfoList();
        mClipInfoArray.clear();

        mCaptionZVal = 0;
        m_clipIndex = 0;
        m_curSeekTimelinePos = 0;
    }

    public static BackupData instance() {
        if (mAssetDataBackup == null)
            mAssetDataBackup = new BackupData();
        return mAssetDataBackup;
    }
    private BackupData() {
        mClipInfoArray = new ArrayList<>();
        mAddClipInfoList = new ArrayList<>();
        mPicInPicVideoArray = new ArrayList<>();
        mCaptionZVal = 0;
        m_clipIndex = 0;
        m_curSeekTimelinePos = 0;
    }
}
