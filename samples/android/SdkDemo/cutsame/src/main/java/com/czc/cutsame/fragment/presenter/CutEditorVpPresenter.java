package com.czc.cutsame.fragment.presenter;

import static com.meicam.sdk.NvsCaption.BOUNDING_TYPE_TEXT;
import static com.meicam.sdk.NvsCaption.BOUNDING_TYPE_TEXT_FRAME;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;

import com.czc.cutsame.bean.Template;
import com.meicam.sdk.NvsCompoundCaption;
import com.meishe.engine.bean.template.TemplateCaptionDesc;
import com.czc.cutsame.bean.TemplateClip;
import com.czc.cutsame.fragment.iview.CutEditorVpView;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.ThreadUtils;
import com.meishe.engine.editor.EditorController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 剪同款中编辑页面viewpager的业务类
 * <p></p>
 * Edit page viewPager business class in the CutSameEditorActivity
 */
public class CutEditorVpPresenter extends Presenter<CutEditorVpView> {
    private static final String TAG = "CutEditorVpPresenter";
    /**
     * 获取模板中的视频信息的key
     * <p></p>
     * the key for the video information in the template
     */
    public static final String VIDEO = "VIDEO";
    /**
     * 获取模板中的字幕信息的key
     * <p></p>
     * the key for the caption information in the template
     */
    public static final String CAPTION = "caption";

    /**
     * 根据模板ID和需要的数据类型，从模板中获取数据
     * <p></p>
     * Get data from the template based on the template ID and the type of data you want
     *
     * @param type       the type 类型
     * @param templateId the template id 模板编号
     */
    public void onDataReady(String type, String templateId,String templateType,List<TemplateClip> clipList) {
        if (TextUtils.equals(type, VIDEO)) {
            if (templateType.equals(Template.TYPE_TEMPLATE_STANDER)){
                if (getView() != null) {
                    getView().getVideoData(EditorController.getInstance().getTemplateVideoClip(templateId));
                }
            }else{
                getView().getVideoData(getTemplateVideoClip(clipList));
            }
        } else if (TextUtils.equals(type, CAPTION)) {
            if (getView() != null) {
                List<TemplateCaptionDesc> templateCaptionDescList = new ArrayList<>();
                if (templateType.equals(Template.TYPE_TEMPLATE_STANDER)){
                    List<NvsAssetPackageManager.NvsTemplateCaptionDesc> nvsTemplateCaptionDescs = EditorController.getInstance().getTemplateCaptions(templateId);

                    for (NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaptionDesc : nvsTemplateCaptionDescs) {
                        TemplateCaptionDesc templateCaptionDesc = new TemplateCaptionDesc();
                        templateCaptionDesc.text = nvsTemplateCaptionDesc.text;
                        templateCaptionDesc.replaceId = nvsTemplateCaptionDesc.replaceId;
                        NvsTimelineCaption nvsTimelineCaption = EditorController.getInstance().getCaptionByAttachment(nvsTemplateCaptionDesc.replaceId);
                        if (nvsTimelineCaption == null) {
                            continue;
                        }
                        templateCaptionDesc.setInPoint(nvsTimelineCaption.getInPoint());
                        templateCaptionDescList.add(templateCaptionDesc);
                    }
                }else{
                    templateCaptionDescList = EditorController.getInstance().getTemplateCaptions(EditorController.getInstance().getNvsTimeline());
                }

                getView().getCaptionData(templateCaptionDescList);
            }
        }
    }

    public List<NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo> getTemplateVideoClip(List<TemplateClip> clipList) {
        List<NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo> lists=new ArrayList<>();
        for (int i = 0; i < clipList.size(); i++) {
            TemplateClip templateClip = clipList.get(i);
            if (templateClip==null){
                continue;
            }
            NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo clipInfo=
                    new NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo();
            clipInfo.clipIndex=i;
            clipInfo.trackIndex=0;
            clipInfo.inpoint=templateClip.getInPoint();
            clipInfo.outpoint=templateClip.getOutPoint();
            clipInfo.canReplace=templateClip.replaceMode==0;
            clipInfo.trimIn=templateClip.getTrimIn();
            clipInfo.trimOut=templateClip.getTrimOut();
            lists.add(clipInfo);
        }

        return lists;
    }

    /**
     * 根据模板ID获取模板中字幕入点对应的画面的位图
     * <p></p>
     * Get the bitmap of the screen corresponding to the subtitle entry point in the template according to the template ID
     *
     * @param templateId the template id
     * @[param templateType template type
     */
    public void getCaptionBitmap(String templateId,String templateType) {
        Set<Long> inPoints = new HashSet<>();
        if (templateType.equals( Template.TYPE_TEMPLATE_STANDER)){
            List<NvsAssetPackageManager.NvsTemplateCaptionDesc> nvsTemplateCaptionDescs = EditorController.getInstance().getTemplateCaptions(templateId);
            if (CommonUtils.isEmpty(nvsTemplateCaptionDescs)) {
                return;
            }
            for (NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaptionDesc : nvsTemplateCaptionDescs) {
                NvsTimelineCaption nvsTimelineCaption = EditorController.getInstance().getCaptionByAttachment(nvsTemplateCaptionDesc.replaceId);
                if (nvsTimelineCaption == null) {
                    continue;
                }
                inPoints.add(nvsTimelineCaption.getInPoint());
            }
        }else{
            List<TemplateCaptionDesc> templateCaptions = EditorController.getInstance().getTemplateCaptions(EditorController.getInstance().getNvsTimeline());
            if (CommonUtils.isEmpty(templateCaptions)) {
                return;
            }
            for (TemplateCaptionDesc nvsTemplateCaptionDesc : templateCaptions) {
                if (nvsTemplateCaptionDesc == null) {
                    continue;
                }
                inPoints.add(nvsTemplateCaptionDesc.getInPoint());
            }
        }

        final List<Long> listData = new ArrayList<>(inPoints);
        final int[] currentIndex = {0};
        EditorController.getInstance().grabBitmapFromAuxiliaryTimelineAsync(listData.get(currentIndex[0]),
                new NvsStreamingContext.ImageGrabberCallback() {
                    NvsStreamingContext.ImageGrabberCallback callback = this;

                    @Override
                    public void onImageGrabbedArrived(final Bitmap bitmap, long l) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getView() != null) {
                                    getView().getCaptionBitmap(listData.get(currentIndex[0]), bitmap);
                                }
                                if (++currentIndex[0] < listData.size()) {
                                    EditorController.getInstance().grabBitmapFromAuxiliaryTimelineAsync(listData.get(currentIndex[0]),
                                            callback);
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 修改模板中对应footage下所有视频片段的新入点
     * <p></p>
     * Change the new trim point for all video clip in footage in the template
     *
     * @param newTrim          the new trim 新的裁剪位置
     * @param selectedClipInfo 选中的片段
     */
    public void changClipTrim(long newTrim, NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo selectedClipInfo) {
        if (selectedClipInfo == null) {
            return;
        }
        NvsVideoClip clip = EditorController.getInstance().getVideoClipByIndex(selectedClipInfo.trackIndex, selectedClipInfo.clipIndex);
        if (clip == null) {
            return;
        }
        long oldTrimIn = clip.getTrimIn();
        clip.moveTrimPoint(newTrim - oldTrimIn);
        EditorController.getInstance().seekTimeline(0);
        if (getView() != null) {
            getView().needSeekPosition(0, null);
        }
    }

    /**
     * 修改模板中对应footage下所有视频片段的新入点
     * <p></p>
     * Change the new trim point for all video clip in footage in the template
     *
     * @param newTrim          the new trim 新的裁剪位置
     * @param selectedClipInfo 选中的片段
     */
    public void changClipTrimIn(long newTrim, NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo selectedClipInfo) {
        if (selectedClipInfo == null) {
            return;
        }
        NvsVideoClip clip = EditorController.getInstance().getVideoClipByIndex(selectedClipInfo.trackIndex, selectedClipInfo.clipIndex);
        if (clip == null) {
            return;
        }
//        long oldTrimIn = clip.getTrimIn();
//        clip.moveTrimPoint(newTrim - oldTrimIn);
        clip.changeTrimInPoint(newTrim,true);
        EditorController.getInstance().seekTimeline(0);
        if (getView() != null) {
            getView().needSeekPosition(0, null);
        }
    }

    /**
     * 修改模板中对应footage下所有视频片段的新入点
     * <p></p>
     * Change the new trim point for all video clip in footage in the template
     *
     * @param newTrim          the new trim 新的裁剪位置
     * @param selectedClipInfo 选中的片段
     */
    public void changClipTrimOut(long newTrim, NvsAssetPackageManager.NvsTemplateFootageCorrespondingClipInfo selectedClipInfo) {
        if (selectedClipInfo == null) {
            return;
        }
        NvsVideoClip clip = EditorController.getInstance().getVideoClipByIndex(selectedClipInfo.trackIndex, selectedClipInfo.clipIndex);
        if (clip == null) {
            return;
        }
//        long oldTrimIn = clip.getTrimIn();
//        clip.moveTrimPoint(newTrim - oldTrimIn);
        clip.changeTrimOutPoint(newTrim,true);
        EditorController.getInstance().seekTimeline(0);
        if (getView() != null) {
            getView().needSeekPosition(0, null);
        }
    }

    /**
     * 修改字幕文字
     * <p></p>
     * Change caption text.
     *
     * @param nvsTemplateCaptionDesc the nvs template caption desc 模板中字幕的附加信息
     * @param text                   the text  新的文本信息
     */
    public void changeCaptionText(TemplateCaptionDesc nvsTemplateCaptionDesc, String text) {
        if(nvsTemplateCaptionDesc.getCaptionType() == 0){

            NvsTimelineCaption nvsTimelineCaption = EditorController.getInstance().getCaptionByAttachment(nvsTemplateCaptionDesc.replaceId);
            if (nvsTimelineCaption == null) {
                Log.e(TAG, "changeCaptionText: nvsTimelineCaption is NULL! " + nvsTemplateCaptionDesc.replaceId);
                return;
            }
            nvsTimelineCaption.setText(text);
        }else{
            //如果是不限长模板中使用的字幕，需要区分类型
            if(nvsTemplateCaptionDesc.getCaptionType()==TemplateCaptionDesc.TemplateCaptionType.CLIP_CAPTION||
                    nvsTemplateCaptionDesc.getCaptionType()==TemplateCaptionDesc.TemplateCaptionType.TIMELINE_CAPTION) {
                nvsTemplateCaptionDesc.getNvsCaption().setText(text);
            }else{
                nvsTemplateCaptionDesc.getNvsCompoundCaption().setText(nvsTemplateCaptionDesc.getCaptionIndex(),text);
            }
        }
    }

    /**
     * seek到字幕的起点位置
     * <p></p>
     * Seek to caption start time.
     *
     * @param nvsTemplateCaptionDesc the nvs template caption desc 模板中的字幕信息
     */
    public void seekToCaptionStartTime(TemplateCaptionDesc nvsTemplateCaptionDesc) {
        if(nvsTemplateCaptionDesc.getCaptionType() ==0){

            NvsTimelineCaption nvsTimelineCaption = EditorController.getInstance().getCaptionByAttachment(nvsTemplateCaptionDesc.replaceId);
            if (nvsTimelineCaption == null) {
                Log.e(TAG, "seekToCaptionStartTime: nvsTimelineCaption is NULL! " + nvsTemplateCaptionDesc.replaceId);
                return;
            }
            EditorController.getInstance().seekTimeline(nvsTimelineCaption.getInPoint(), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            if (getView() != null) {
                getView().needSeekPosition(nvsTimelineCaption.getInPoint(), nvsTimelineCaption.getCaptionBoundingVertices(BOUNDING_TYPE_TEXT));
            }
        }else{
            long inPoint = nvsTemplateCaptionDesc.getInPoint();
            EditorController.getInstance().seekTimeline(inPoint, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            if (getView() != null) {
                if(nvsTemplateCaptionDesc.getCaptionType()==TemplateCaptionDesc.TemplateCaptionType.CLIP_CAPTION||
                        nvsTemplateCaptionDesc.getCaptionType()==TemplateCaptionDesc.TemplateCaptionType.TIMELINE_CAPTION){

                    getView().needSeekPosition(inPoint, nvsTemplateCaptionDesc.getNvsCaption().getCaptionBoundingVertices(BOUNDING_TYPE_TEXT));
                }else{
                    getView().needSeekPosition(inPoint, nvsTemplateCaptionDesc.getNvsCompoundCaption().getCaptionBoundingVertices(nvsTemplateCaptionDesc.getCaptionIndex(),NvsCompoundCaption.BOUNDING_TYPE_TEXT));
                }
            }
        }
    }

    /**
     * 获取字幕的边框位置
     * Gets the border point of the caption
     */
    public List<PointF> getCaptionPointList(NvsAssetPackageManager.NvsTemplateCaptionDesc nvsTemplateCaptionDesc) {
        if (nvsTemplateCaptionDesc == null) {
            return null;
        }
        NvsTimelineCaption caption = EditorController.getInstance().getCaptionByAttachment(nvsTemplateCaptionDesc.replaceId);
        if (caption == null) {
            return null;
        }
        return caption.getCaptionBoundingVertices(BOUNDING_TYPE_TEXT);
    }

    /**
     * 处理视频片段的替换
     * Handle the replacement of video clips
     *
     * @param templateClip the templateClip
     */
    public void dealVideoReplace(TemplateClip templateClip) {
        if (templateClip == null) {
            return;
        }
        NvsTimeline nvsTimeline = EditorController.getInstance().getNvsTimeline();
        NvsVideoTrack nvsVideoTrack = nvsTimeline.getVideoTrackByIndex(templateClip.getTrackIndex());
        if (nvsVideoTrack != null) {
            for (int j = 0; j < nvsVideoTrack.getClipCount(); j++) {
                NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(j);
                if (templateClip.getInPoint() == nvsVideoClip.getInPoint()) {
                    if (templateClip.getNeedReverse()) {
                        nvsVideoClip.changeFilePath(templateClip.getReversePath());
                    } else {
                        nvsVideoClip.changeFilePath(templateClip.getFilePath());
                    }
                    EditorController.getInstance().seekTimeline();
                    break;
                }
            }
        }
    }
}
