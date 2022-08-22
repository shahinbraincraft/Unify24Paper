package com.meishe.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.text.TextUtils;

import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectRenderCore;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.effect.sdk.NvsVideoEffectAnimatedSticker;
import com.meicam.effect.sdk.NvsVideoEffectCaption;
import com.meicam.effect.sdk.NvsVideoEffectCompoundCaption;
import com.meicam.effect.sdk.NvsVideoEffectTransition;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.render.entity.EffectRenderItem;
import com.meishe.render.entity.RenderEffectParams;
import com.meishe.render.utils.EGLHelper;
import com.meishe.render.utils.GLUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/4/12 15:59
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class MeisheRender implements IMeisheRender{
    private static final String TAG = "MeisheRender";
    public NvsEffectRenderCore mEffectRenderCore = null;
    private boolean mEffectRenderInit = false;
    private int[] mFrameBuffers = null;
    private int mConvertProgramId = -1;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mGlCubeBuffer;
    private int mPreProcessTextures = -1;

    private final Object mArraySyncObject = new Object( );

    private int mGlRenderTexture = 0;
    private int mGlRenderTexture1 = 0;
    private NvsVideoResolution mCurrentVideoResolution;

    // preview data
    private byte[] mPreviewImageData;
    private byte[] mPreviewRgbaImageData;
    private final Object mPreviewImageDataLock = new Object( );
    private int mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;

    private boolean renderInGlThread = true;

    @Override
    public void init() {
        init(true);
    }

    @Override
    public void init(boolean renderInGlThread) {
        NvsEffectSdkContext context = NvsEffectSdkContext.getInstance();
        mEffectRenderCore = context.createEffectRenderCore( );
        mCurrentVideoResolution = new NvsVideoResolution( );
        mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);
        this.renderInGlThread = renderInGlThread;
    }

    /**
     * 释放openGl资源，必须在openGl环境中运行
     */
    @Override
    public void release(CopyOnWriteArrayList<EffectRenderItem> currentRenderEffectList, CopyOnWriteArrayList<EffectRenderItem> clearEffectList) {
        mGlRenderTexture = 0;
        mGlRenderTexture1 = 0;
        mPreProcessTextures = 0;

        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        synchronized (mArraySyncObject) {
            if (mEffectRenderCore != null) {
                if (clearEffectList != null) {
                    for (EffectRenderItem effect : clearEffectList) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                        effect.effect = null;
                    }
                }
                if (currentRenderEffectList != null) {
                    for (EffectRenderItem effect : currentRenderEffectList) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                        effect.effect = null;
                    }
                }
            }
        }
        mEffectRenderCore.clearCacheResources( );
        mEffectRenderCore.cleanUp( );


        if (mConvertProgramId > 0) {
            GLES20.glDeleteProgram(mConvertProgramId);
        }
        mConvertProgramId = -1;
    }


    public void initRenderCore() {
        if (!mEffectRenderInit) {
            if(renderInGlThread){
                mEffectRenderInit = mEffectRenderCore.initialize(NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K);
            }else{
                mEffectRenderInit = mEffectRenderCore.initialize(NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K|NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_CREATE_GLCONTEXT_IF_NEED|NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_IN_SINGLE_GLTHREAD);

            }
        }
    }

    @Override
    public void sendPreviewBuffer(final byte[] data) {
        mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;
        if (mPreviewImageData == null || mPreviewImageData.length != data.length) {
            mPreviewImageData = new byte[data.length];
        }
        synchronized (mPreviewImageDataLock) {
            System.arraycopy(data, 0, mPreviewImageData, 0, data.length);
        }
    }

    @Override
    public void sendRgbaBuffer(final byte[] data) {
        mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA;
        if ((mPreviewRgbaImageData == null) || (mPreviewRgbaImageData.length != data.length)) {
            mPreviewRgbaImageData = new byte[data.length];
        }
        synchronized (mPreviewImageDataLock) {
            System.arraycopy(data, 0, mPreviewRgbaImageData, 0, data.length);
        }
    }

    /**
     * 渲染所有添加的滤镜，必须在opengl环境中运行
     */
    @Override
    public int renderVideoEffect(RenderEffectParams renderEffectParams) {
        if(null == renderEffectParams){
            return -1;
        }
        int dataType = mDataType;

        if (mEffectRenderCore == null) {
            return renderEffectParams.getInputTexture();
        }
        List<EffectRenderItem> clearEffect = renderEffectParams.getClearRenderItemList();
        /**
         * TODO  clear no need render effect
         */
        //清除要删除特效中的GL资源，然后删除不使用的特效。
        synchronized (mArraySyncObject) {
            if (mEffectRenderCore != null) {
                if (clearEffect != null) {
                    for (EffectRenderItem effect : clearEffect) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                    }
                    clearEffect.clear( );
                }
            }
        }
        //得到当前需要渲染的特效列表
        List<EffectRenderItem> effects = renderEffectParams.getEffectRenderItemList();
        if(null == effects || effects.size() ==0){
            if(null != mEffectRenderCore && mEffectRenderInit){
                mEffectRenderCore.clearCacheResources();
                mEffectRenderCore.cleanUp();
                mEffectRenderInit = false;
            }
            return renderEffectParams.getInputTexture();
        }
        int width = renderEffectParams.getWidth();
        int height = renderEffectParams.getHeight();
        int cameraOrientation = renderEffectParams.getCameraOrientation();
        int inRenderTex = renderEffectParams.getInputTexture();
        boolean flipHorizontal = renderEffectParams.isFlipHorizontal();
        long currentTimeStamp = renderEffectParams.getCurrentTimeStamp();
        int displayOrientation = renderEffectParams.getDisplayOrientation();
        int deviceOrientation = renderEffectParams.getDeviceOrientation();
        boolean useBufferMode = renderEffectParams.isUseBufferMode();
        boolean useImageMode = renderEffectParams.isUseImageMode();
        //处理输入的纹理，如果纹理是OES的纹理转换为标准的GL_TEXTURE_2D格式
        initRenderCore();
        if (renderEffectParams.isOESTexture()) {
            inRenderTex = preProcessOesToTexture2D(renderEffectParams.getInputTexture(), width, height, cameraOrientation, flipHorizontal);
        }
        EGLHelper.checkGlError("preProcess");

        //创建输出的纹理
        mGlRenderTexture = createGlTexture(width, height);
        EGLHelper.checkGlError("createGLTexture");

        int outRenderTex = mGlRenderTexture;
        int nEffectCount = effects.size( );

        if (nEffectCount > 1) {
            mGlRenderTexture1 = createGlTexture(width, height);
        }

        //获取当前绑定的FrameBuffer，因为在EffectSDKCore中会有创建一个新的FrameBuffer，所有要保留当前的FrameBuffer
        int[] fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        EGLHelper.checkGlError("glGetIntegerv");

        int nRenderedCount = 0;
        for (EffectRenderItem effect : effects) {
            boolean bRet = true;
            boolean isArSceneEffect = false;
            if (effect.effect instanceof NvsVideoEffect) {
                NvsVideoEffect videoEffect = (NvsVideoEffect) effect.effect;
                isArSceneEffect = ("AR Scene".equalsIgnoreCase(videoEffect.getBuiltinVideoFxName( )));
            }
            if (effect.startTimeStamp < 0) {
                effect.startTimeStamp = currentTimeStamp;
            }
            long currentRenderTime = currentTimeStamp - effect.startTimeStamp;
            if (isArSceneEffect) {
                mCurrentVideoResolution.imageWidth = width;
                mCurrentVideoResolution.imageHeight = height;


                NvsVideoFrameInfo info = new NvsVideoFrameInfo( );
                info.displayRotation = displayOrientation;
                info.flipHorizontally = flipHorizontal;
                info.isRec601 = true;
                info.isFullRangeYuv = true;
                info.pixelFormat = dataType;
                info.frameWidth = width;
                info.frameHeight = height;
                if ((displayOrientation == 90) || (displayOrientation == 270)) {
                    info.frameWidth = height;
                    info.frameHeight = width;
                }
                int physicalOrientation = calcPreviewBufferPhysicalOrientation(cameraOrientation, flipHorizontal, deviceOrientation);
                if (useImageMode) {
                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate( );
                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
                }

                if (dataType == NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA) {
                    int nRet = mEffectRenderCore.renderEffect(effect.effect, inRenderTex, mPreviewRgbaImageData, info, physicalOrientation,
                            mCurrentVideoResolution, outRenderTex, (currentRenderTime) * 1000, 0);
                    //texture2bitmap(outRenderTex,width,height);
                } else {
                    if(useBufferMode){
                        ByteBuffer resultBuffer = mEffectRenderCore.renderEffect(effect.effect,mPreviewImageData,info,physicalOrientation,NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21,
                                true,currentRenderTime*1000,0);
                        NvsVideoFrameInfo nvsVideoFrameInfo = new NvsVideoFrameInfo();
                        nvsVideoFrameInfo.frameWidth = width;
                        nvsVideoFrameInfo.frameHeight = height;
                        nvsVideoFrameInfo.pixelFormat = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;
                        nvsVideoFrameInfo.displayRotation = 0;
                        nvsVideoFrameInfo.flipHorizontally = false;
                        nvsVideoFrameInfo.isRec601 = true;
                        nvsVideoFrameInfo.isFullRangeYuv = false;
                        int nRet = mEffectRenderCore.uploadVideoFrameToTexture(bufferToByte(resultBuffer),nvsVideoFrameInfo,outRenderTex);
                    }else {
                        //texture2bitmap(outRenderTex,width,height);
                        int nRet = mEffectRenderCore.renderEffect(effect.effect, inRenderTex, mPreviewImageData, info, physicalOrientation,
                                mCurrentVideoResolution, outRenderTex, currentRenderTime*1000, 0);
                       /* if(null!= mPreviewImageData){
                            getBitmapImageFromYUV(mPreviewImageData,info.frameWidth,info.frameHeight);
                        }*/
                    }

                }

                if (useImageMode) {
                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate( );
                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE);
                }
            } else {
                bRet = processGeneralFilter(effect.effect, inRenderTex, width, height, outRenderTex, currentRenderTime * 1000);
            }
            nRenderedCount++;
            if (!bRet) {
                continue;
            }
            if (nRenderedCount == effects.size( )) {
                break;
            }
            inRenderTex = outRenderTex;
            if (outRenderTex == mGlRenderTexture) {
                outRenderTex = mGlRenderTexture1;
            } else {
                outRenderTex = mGlRenderTexture;
            }
        }
        EGLHelper.checkGlError("ProcessSingleFilter");
        //恢复之前保存的FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);
        if (effects.size( ) == 0) {
            outRenderTex = inRenderTex;
        }

        if (mGlRenderTexture != outRenderTex) {
            GLUtils.destroyGlTexture(mGlRenderTexture);
            mGlRenderTexture = -1;
        } else {
            GLUtils.destroyGlTexture(mGlRenderTexture1);
            mGlRenderTexture1 = -1;
        }

        return outRenderTex;
    }


    @Override
    public void uploadVideoFrameToTexture(byte[] array, NvsVideoFrameInfo info, int textureId) {
        initRenderCore();
        mEffectRenderCore.uploadVideoFrameToTexture(array,info,textureId);
    }

    @Override
    public ByteBuffer downloadFromTexture(int textureId, NvsVideoResolution resolution, int type, int flag) {
        initRenderCore();
        return mEffectRenderCore.downloadFromTexture(textureId,resolution,type,flag);
    }

    private byte[] bufferToByte(ByteBuffer imageBuffer) {
        if (null == imageBuffer) {
            return null;
        }
        byte[] bs = new byte[imageBuffer.capacity()];
        imageBuffer.position(0);
        imageBuffer.get(bs);
        return bs;

    }

    /**
     * 计算预览的buffer的物理角度  buffer的初始方向始终是1.2两种情况
     * @param cameraOrientation camera相机角度
     * @param mirror 是否需要镜像(前置需要)
     * @param deviceOrientation 设备的物理角度
     * @return buffer角度
     * 计算说明
     * 1.前置摄像头相机旋转角度 270
     * 2.后置摄像头相机旋转角度 90,
     * 3.camera角度+手机物理方向角度 计算出旋转多大度数到正角度。
     */
    private int calcPreviewBufferPhysicalOrientation(int cameraOrientation, boolean mirror, int deviceOrientation) {
        if (deviceOrientation <= 45 || deviceOrientation >= 315) {
            deviceOrientation = 0;
        } else if (deviceOrientation < 135) {
            deviceOrientation = 90;
        } else if (deviceOrientation <= 225) {
            deviceOrientation = 180;
        } else {
            deviceOrientation = 270;
        }

        int orientationAngle = 0;
        final boolean frontCamera = mirror;
        if (!frontCamera) {
            //后置
            orientationAngle = (cameraOrientation + deviceOrientation) % 360;
        } else {
            //前置
            orientationAngle = (cameraOrientation - deviceOrientation + 360) % 360;
        }
        return orientationAngle;
    }


    private int createGlTexture(int width, int height) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int tex = GLUtils.createGlTexture(width, height);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        if (mFrameBuffers == null) {
            mFrameBuffers = new int[1];
            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        }
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        EGLHelper.bindFrameBuffer(tex, mFrameBuffers[0], width, height);
        return tex;
    }

    /**
     * 1. 将OES的纹理转换为标准的GL_TEXTURE_2D格式
     * 2. 将纹理宽高对换，即将wxh的纹理转换为了hxw的纹理，并且如果是前置摄像头，则需要有水平的翻转
     *
     * @param textureId 输入的OES的纹理id
     * @return 转换后的GL_TEXTURE_2D的纹理id
     */
    @Override
    public int preProcessOesToTexture2D(int textureId, int width, int height, int cameraOrientation, boolean flipHorizontal) {
        if (mConvertProgramId <= 0) {
            mConvertProgramId = EGLHelper.loadProgramForSurfaceTexture( );

            mGlCubeBuffer = ByteBuffer.allocateDirect(EGLHelper.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder( ))
                    .asFloatBuffer( );
            mGlCubeBuffer.put(EGLHelper.CUBE).position(0);

            mTextureBuffer = ByteBuffer.allocateDirect(EGLHelper.TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder( ))
                    .asFloatBuffer( );
            mTextureBuffer.clear( );
            mTextureBuffer.put(EGLHelper.TEXTURE_NO_ROTATION).position(0);
        }

        if (mConvertProgramId < 0) {
            return -1;
        }

        float[] textureCords = EGLHelper.getRotation(cameraOrientation, true, flipHorizontal);
        mTextureBuffer.clear( );
        mTextureBuffer.put(textureCords).position(0);
        EGLHelper.checkGlError("preProcess");

        GLES20.glUseProgram(mConvertProgramId);
        EGLHelper.checkGlError("glUseProgram");

        if (mPreProcessTextures <= 0) {
            mPreProcessTextures = createGlTexture(width, height);
            EGLHelper.bindFrameBuffer(mPreProcessTextures, mFrameBuffers[0], width, height);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPreProcessTextures);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mPreProcessTextures, 0);

        mGlCubeBuffer.position(0);
        int glAttributePosition = GLES20.glGetAttribLocation(mConvertProgramId, POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttributePosition, 2, GLES20.GL_FLOAT, false, 0, mGlCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttributePosition);
        EGLHelper.checkGlError("glEnableVertexAttributeArray");

        mTextureBuffer.clear( );
        int glAttributeTextureCoordinate = GLES20.glGetAttribLocation(mConvertProgramId, TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttributeTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttributeTextureCoordinate);
        EGLHelper.checkGlError("glEnableVertexAttributeArray");

        if (textureId != -1) {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            int textUniform = GLES20.glGetUniformLocation(mConvertProgramId, TEXTURE_UNIFORM);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(textUniform, 0);
            EGLHelper.checkGlError("glBindTexture");
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(glAttributePosition);
        GLES20.glDisableVertexAttribArray(glAttributeTextureCoordinate);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        EGLHelper.checkGlError("glBindTexture");
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);
        return mPreProcessTextures;
    }

    private boolean start = false;
    private long modifyTime = 0;
    @Override
    public void reSetTransformStart() {
        start = false;
        modifyTime = 0;
    }
    // 一般滤镜处理
    private boolean processGeneralFilter(NvsEffect effect, int inputTex, int width, int height, int outputTex, long timeStamp) {
        if (mEffectRenderCore == null) {
            return false;
        }
        mCurrentVideoResolution.imageWidth = width;
        mCurrentVideoResolution.imageHeight = height;
//        int nRet = mEffectRenderCore.renderEffect(effect, inputTex, mCurrentVideoResolution, outputTex, timeStamp, 0);
//        return nRet == NvsEffectRenderCore.NV_EFFECT_CORE_NO_ERROR;
        if (effect instanceof NvsVideoEffectTransition){
            int texArray[] = {inputTex,inputTex};
            if(!start){
                modifyTime = timeStamp;
                start = true;
            }
            long offsetTime = timeStamp - modifyTime;
            if(offsetTime >= 5000000)
                offsetTime = timeStamp;
            int nRet = mEffectRenderCore.renderEffect(effect, texArray, 2, mCurrentVideoResolution, outputTex, offsetTime,0);
            return nRet == NvsEffectRenderCore.NV_EFFECT_CORE_NO_ERROR;
        }else {
            int nRet = mEffectRenderCore.renderEffect(effect, inputTex, mCurrentVideoResolution, outputTex, timeStamp, 0);
            //texture2bitmap(outputTex,width,height);
            //Log.e("timeStamp","timeStamp = "+timeStamp);
            return nRet == NvsEffectRenderCore.NV_EFFECT_CORE_NO_ERROR;

        }
    }

    private void texture2bitmap(int output,int texWidth,int texHeight) {
        NvsVideoResolution resolution = new NvsVideoResolution( );
        resolution.imageWidth = texWidth;
        resolution.imageHeight = texHeight;
        resolution.imagePAR = new NvsRational(1, 1);
        int[] fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        ByteBuffer mData =mEffectRenderCore.downloadFromTexture(output, resolution, NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);

        Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(mData);
        bmp.recycle();
    }

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }
}
