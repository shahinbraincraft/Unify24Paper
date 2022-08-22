package com.meishe.render;

import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.render.entity.EffectRenderItem;
import com.meishe.render.entity.RenderEffectParams;

import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/4/12 15:54
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public interface IMeisheRender {
    public final static String POSITION_COORDINATE = "position";
    public final static String TEXTURE_UNIFORM = "inputImageTexture";
    public final static String TEXTURE_COORDINATE = "inputTextureCoordinate";

    /**
     * 初始化方法，主线程调用
     */
    public void init();

    public void init(boolean renderInGlThread);
    /**
     * 释放openGl资源，必须在openGl环境中运行
     */
    public void release(CopyOnWriteArrayList<EffectRenderItem> currentRenderEffectList,CopyOnWriteArrayList<EffectRenderItem> clearEffectList);
    /**
     * 渲染特效
     * @param renderEffectParams 渲染的特技及对应参数
     */
    public int renderVideoEffect(RenderEffectParams renderEffectParams);

    /**
     * 上传buffer 到纹理
     * @param array 图像数据
     * @param info 图像信息
     * @param textureId 目标纹理
     */
    public void uploadVideoFrameToTexture(byte[] array, NvsVideoFrameInfo info,int textureId);

    /**
     * 下载buffer
     * @param textureId 目标纹理
     * @param resolution 纹理的信息
     * @param type buffer类型
     * @param flag 其他配置
     * @return 图像buffer
     */
    public ByteBuffer downloadFromTexture(int textureId, NvsVideoResolution resolution, int type, int flag);

    /**
     *
     * @param textureId 输入oes纹理
     * @param width 宽
     * @param height 高
     * @param cameraOrientation 相机方向
     * @param flipHorizontal 水平翻转
     * @return
     */
    public int preProcessOesToTexture2D(int textureId, int width, int height, int cameraOrientation, boolean flipHorizontal);
    /**
     * 每一帧的数据
     * @param data 视图数据 原相机NV21类型
     */
    public void sendPreviewBuffer(final byte[] data);

    /**
     *  * 每一帧的数据
     *  @param data 视图数据 rgba类型
     */
    public void sendRgbaBuffer(final byte[] data);

    /**
     * 转场特效使用，重置转场时间
     */
    public void reSetTransformStart();

}
