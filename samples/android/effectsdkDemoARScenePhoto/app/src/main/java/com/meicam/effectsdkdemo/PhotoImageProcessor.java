package com.meicam.effectsdkdemo;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;

import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;
import com.meishe.render.IMeisheRender;
import com.meishe.render.MeisheRender;
import com.meishe.render.entity.RenderEffectParams;
import com.meishe.render.utils.EGLHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static android.opengl.GLES20.glGenTextures;

/**
 * @author ms
 */
public class PhotoImageProcessor {

    private static final String TAG = "PhotoImageProcessor";

    private IMeisheRender mMeisheRender;
    private int mWidth = -1;
    private int mHeight = -1;
    private ByteBuffer mData;

    private int mTextureId = -1;
    private int[] mFrameBuffers = new int[1];
    private RenderEffectParams renderEffectParams;


    public PhotoImageProcessor() {
        initRenderWrapper();
    }

    public void destroy() {
        destroyFrameBuffers( );
    }

    private int createGlTexture(int width, int height) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int[] tex = new int[1];
        glGenTextures(1, tex, 0);
        EGLHelper.checkGlError("Texture generate");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0]);
        if (mFrameBuffers == null) {
            mFrameBuffers = new int[1];
            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        }

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        // EGLHelper.bindFrameBuffer(tex[0], mFrameBuffers[0], width, height);
        return tex[0];
    }

    public String processEffects() {
        if(null == renderEffectParams){
            return null;
        }
        int width = mWidth;
        int height = mHeight;
        int mOrientation = renderEffectParams.getDisplayOrientation();
        if ((mOrientation == 90) || (mOrientation == 270)) {
            width = mHeight;
            height = mWidth;
        }
        mTextureId = createGlTexture(width, height);
        NvsVideoFrameInfo info = new NvsVideoFrameInfo( );
        info.frameWidth = mWidth;
        info.frameHeight = mHeight;
        info.pixelFormat = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA;
        info.displayRotation = mOrientation;
        info.flipHorizontally = renderEffectParams.isFlipHorizontal();
        info.frameTimestamp = renderEffectParams.getCurrentTimeStamp();
        int[] fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        mMeisheRender.uploadVideoFrameToTexture(mData.array( ), info, mTextureId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);

        mMeisheRender.sendRgbaBuffer(mData.array( ));
        renderEffectParams = renderEffectParams
                .setTexture(mTextureId)
                .setWidth(width)
                .setHeight(height)
                .isOesTexture(false)
                .isBufferMode(false)
                .isImageMode(true)
                .build();
        int mOutRenderTexture = mMeisheRender.renderVideoEffect(renderEffectParams);
        mData.position(0);
        NvsVideoResolution resolution = new NvsVideoResolution( );
        resolution.imageWidth = width;
        resolution.imageHeight = height;
        resolution.imagePAR = new NvsRational(1, 1);
        fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        mData = mMeisheRender.downloadFromTexture(mOutRenderTexture, resolution, NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(mData);
        File captureDir = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/effectsdk");
//        File captureDir = new File(Environment.getExternalStorageDirectory( ), "NvStreamingSdk" + File.separator + "Record");
        if (!captureDir.exists( ) && !captureDir.mkdirs( )) {
            Log.e(TAG, "Failed to make Record directory");
            return null;
        }

//        // 图片旋转方向处理
//        Matrix matrix = new Matrix( );
//        matrix.postRotate(CameraProxy.getLatestRotation( ));
//        Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth( ), bmp.getHeight( ), matrix, false);

        File file = new File(captureDir, System.currentTimeMillis( ) + "effectSdk.jpg");
        if (file.exists( )) {
            file.delete( );
        }
        saveJpgFile(bmp, file.getAbsolutePath( ));
        return file.getAbsolutePath( );
    }

    public void startPhotoProcessor(RenderEffectParams renderEffectParams) {
        this.renderEffectParams = renderEffectParams;
        if(null == renderEffectParams){
            mData = null;
            mWidth = -1;
            mHeight = -1;
            return;
        }
        int width = renderEffectParams.getWidth();
        int height =renderEffectParams.getHeight();
        if ((mWidth != width) || (mHeight != height)) {
            mData = null;
        }
        mWidth = width;
        mHeight = height;
    }

    public void addRenderImageData(Bitmap bmp) {
        if (mData == null) {
            int byteCount = bmp.getByteCount( );
            mData = ByteBuffer.allocateDirect(byteCount);
        }
        mData.position(0);
        bmp.copyPixelsToBuffer(mData);
        mData.position(0);

    }



    public static void saveJpgFile(Bitmap bitmap, String name) {
        File file = new File(name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace( );
        }
        try {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                out.flush( );
                out.close( );
            }

        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

    private void destroyFrameBuffers() {
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        if (mTextureId != -1) {
            int[] tex = new int[1];
            tex[0] = mTextureId;
            GLES20.glDeleteTextures(1, tex, 0);
        }
    }

    public void initRenderWrapper() {
        //mRenderCoreWrapper = new EffectRenderCoreWrapper(effectSdkContext);
        mMeisheRender = new MeisheRender();
        mMeisheRender.init(false);
    }

}