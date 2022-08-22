package com.meishe.render.draw;

import static android.opengl.EGLExt.EGL_RECORDABLE_ANDROID;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.meishe.render.R;
import com.meishe.render.utils.EGLHelper;
import com.meishe.render.utils.FileUtils;
import com.meishe.render.utils.GLUtils;
import com.meishe.render.utils.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/4/12 17:47
 * @des : opengl drawer
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class GLDrawer {

    public static final String TAG= "GLDrawer";
    private int mShaderProgram;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawListBuffer;
    private int mTextureParamHandle;
    private int mTextureCoordinateHandle;
    private int mPositionHandle;
    private int mTextureTransformHandle;
    private static float[] sSquareCoords = {
            -1.0f, 1.0f,    // top left
            -1.0f, -1.0f,   // bottom left
            1.0f, -1.0f,    // bottom right
            1.0f, 1.0f};    // top right
    private static short[] sDrawOrder = {0, 1, 2, 0, 2, 3};
    // Texture to be shown in backgrund
    private FloatBuffer mTextureBuffer;
    private float[] mTextureCoords = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private EGLContext mEglContext;
    private float[] mVideoTextureTransform;

    /**
     *
     */
    private boolean mUseBufferMode;
    private int mDrawTextureProgramId = -1;
    private FloatBuffer mDrawTextureBuffer;
    private FloatBuffer mDrawGlCubeBuffer;
    public final static String POSITION_COORDINATE = "position";
    public final static String TEXTURE_UNIFORM = "inputImageTexture";
    public final static String TEXTURE_COORDINATE = "inputTextureCoordinate";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public EGLContext createEglContext() {
        /*
         * 获取显示设备
         * Get display device
         * */
        EGLDisplay mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "eglGetDisplay failed");
            return null;
        }
        /*
         *  初始化EGL
         * Initialize EGL
         * */
        final int[] version = new int[2];
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            mEglDisplay = null;
            Log.e(TAG, "eglInitialize failed");
            return null;
        }
        /*
         * 选择配置
         * Select configuration
         * */
        android.opengl.EGLConfig mEglConfig = getConfig(false, true, mEglDisplay);
        if (mEglConfig == null) {
            Log.e(TAG, "chooseConfig failed");
            return null;
        }

        /*
         * 创建上下文
         * Create context
         * */
        EGLContext currentContext = EGL14.eglGetCurrentContext( );
        final int[] attribList = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, currentContext, attribList, 0);
        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            Log.e(TAG, "eglCreateContext");
        }
        /*
         * 关联EGLContext和渲染表面
         * Associate EGLContext with the rendering surface
         * */
//        EGLSurface currentSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
//        if (!EGL14.eglMakeCurrent(mEglDisplay, currentSurface, currentSurface, mEglContext)) {
//            Log.e(TAG, "eglMakeCurrent failed");
//        }
        return mEglContext;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private android.opengl.EGLConfig getConfig(final boolean withDepthBuffer,
                                               final boolean isRecordable, EGLDisplay mEglDisplay) {
        final int[] attribList = {
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                // EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_NONE, EGL14.EGL_NONE,
                /*EGL_RECORDABLE_ANDROID, 1,
                this flag need to recording of MediaCodec*/
                EGL14.EGL_NONE, EGL14.EGL_NONE,
                //	withDepthBuffer ? EGL14.EGL_DEPTH_SIZE : EGL14.EGL_NONE,
                EGL14.EGL_NONE, EGL14.EGL_NONE,
                // withDepthBuffer ? 16 : 0,
                EGL14.EGL_NONE
        };
        int offset = 10;
        if (false) {
            attribList[offset++] = EGL14.EGL_STENCIL_SIZE;
            attribList[offset++] = 8;
        }
        if (withDepthBuffer) {
            attribList[offset++] = EGL14.EGL_DEPTH_SIZE;
            attribList[offset++] = 16;
        }
        if (isRecordable && (Build.VERSION.SDK_INT >= 18)) {
            attribList[offset++] = EGL_RECORDABLE_ANDROID;
            attribList[offset++] = 1;
        }
        for (int i = attribList.length - 1; i >= offset; i--) {
            attribList[i] = EGL14.EGL_NONE;
        }
        final android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
        final int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(mEglDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
            // XXX it will be better to fallback to RGB565
            Log.w(TAG, "unable to find RGBA8888 / " + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    public void setupGraphics(Context context) {
        final String vertexShader = FileUtils.readTextFileFromRawResource(context, R.raw.vetext_sharder);
        final String fragmentShader = FileUtils.readTextFileFromRawResource(context, R.raw.fragment_sharder);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        mShaderProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"texture", "vPosition", "vTexCoordinate", "textureTransform"});

        GLES20.glUseProgram(mShaderProgram);
        mTextureParamHandle = GLES20.glGetUniformLocation(mShaderProgram, "texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mShaderProgram, "vTexCoordinate");
        mPositionHandle = GLES20.glGetAttribLocation(mShaderProgram, "vPosition");
        mTextureTransformHandle = GLES20.glGetUniformLocation(mShaderProgram, "textureTransform");
    }

    public void setupVertexBuffer() {
        // Draw list buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(sDrawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder( ));
        mDrawListBuffer = dlb.asShortBuffer( );
        mDrawListBuffer.put(sDrawOrder);
        mDrawListBuffer.position(0);

        // Initialize the texture holder
        ByteBuffer bb = ByteBuffer.allocateDirect(sSquareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder( ));

        mVertexBuffer = bb.asFloatBuffer( );
        mVertexBuffer.put(sSquareCoords);
        mVertexBuffer.position(0);
    }

    public void setTextureBuffer(FloatBuffer mTextureBuffer){
        this.mTextureBuffer = mTextureBuffer;

    }

    public int drawTextureToTexture(int displayTex,int textureWidth,int textureHeight,int mDisplayWidth,int mDisplayHeight) {
        int outTex = GLUtils.createGlTexture(textureWidth, textureHeight);
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mDisplayWidth, mDisplayHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        EGLHelper.bindFrameBuffer(outTex, frameBuffers[0], mDisplayWidth, mDisplayHeight);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, displayTex);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, outTex, 0);
        this.drawTextureOES(displayTex, textureWidth, textureHeight,mDisplayWidth,mDisplayHeight);
        GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        return outTex;
    }

    public void drawTextureOES(int displayTex, int texWidht, int texHeight,int  mDisplayWidth ,int mDisplayHeight ) {
        // Draw texture
        GLES20.glUseProgram(mShaderProgram);

        float ar = (float) texWidht / texHeight;
        float disar = (float) mDisplayWidth / mDisplayHeight;
        float cropWidth = 1.0f;
        float cropHeight = (float) 1.0;
        if (ar > disar) {
            cropHeight = 1.0f;
            cropWidth = ar / disar;
        } else {
            cropWidth = 1.0f;
            cropHeight = disar / ar;
        }

        mVertexBuffer.put(0, -cropWidth);
        mVertexBuffer.put(1, cropHeight);
        mVertexBuffer.put(2, -cropWidth);
        mVertexBuffer.put(3, -cropHeight);
        mVertexBuffer.put(4, cropWidth);
        mVertexBuffer.put(5, -cropHeight);
        mVertexBuffer.put(6, cropWidth);
        mVertexBuffer.put(7, cropHeight);
        mVertexBuffer.position(0);

        // corp 转化为fit模式

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        EGLHelper.checkGlError("glEnableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, displayTex);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(mTextureParamHandle, 0);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glUniformMatrix4fv(mTextureTransformHandle, 1, false, mVideoTextureTransform, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, sDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    public void setupFrameInfo(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void setTransformMatrix(float[] mVideoTextureTransform){
        this.mVideoTextureTransform = mVideoTextureTransform;
    }

    /**
     * 输入结果到GLSurfaceView，必须在opengl环境中运行
     */
    public void drawTexture(int displayTex, int texWidth, int texHeight, int displayWidth, int displayHeight) {
        // Draw texture
        if (mDrawTextureProgramId < 0) {
            mDrawTextureProgramId = EGLHelper.loadProgramForTexture( );
            mDrawGlCubeBuffer = ByteBuffer.allocateDirect(EGLHelper.CUBE.length * 4).order(ByteOrder.nativeOrder( )).asFloatBuffer( );
            mDrawGlCubeBuffer.put(EGLHelper.CUBE).position(0);

            mDrawTextureBuffer = ByteBuffer.allocateDirect(EGLHelper.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder( )).asFloatBuffer( );
            mDrawTextureBuffer.clear( );
            mDrawTextureBuffer.put(EGLHelper.TEXTURE_NO_ROTATION).position(0);
        }
        float ar = (float) texWidth / texHeight;
        float disar = (float) displayWidth / displayHeight;
        float cropWidth = 1.0f;
        float cropHeight = 1.0f;
        if (ar > disar) {
            cropHeight = 1.0f;
            cropWidth = ar / disar;
        } else {
            cropWidth = 1.0f;
            cropHeight = disar / ar;
        }
        mDrawGlCubeBuffer.put(0, -cropWidth);
        mDrawGlCubeBuffer.put(1, cropHeight);
        mDrawGlCubeBuffer.put(2, cropWidth);
        mDrawGlCubeBuffer.put(3, cropHeight);
        mDrawGlCubeBuffer.put(4, -cropWidth);
        mDrawGlCubeBuffer.put(5, -cropHeight);
        mDrawGlCubeBuffer.put(6, cropWidth);
        mDrawGlCubeBuffer.put(7, -cropHeight);
        mDrawGlCubeBuffer.position(0);
        GLES20.glUseProgram(mDrawTextureProgramId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, displayTex);

        mDrawGlCubeBuffer.position(0);
        int glAttribPosition = GLES20.glGetAttribLocation(mDrawTextureProgramId, POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mDrawGlCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mDrawTextureBuffer.position(0);
        int glAttribTextureCoordinate = GLES20.glGetAttribLocation(mDrawTextureProgramId, TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mDrawTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        int textUniform = GLES20.glGetUniformLocation(mDrawTextureProgramId, TEXTURE_UNIFORM);
        GLES20.glUniform1i(textUniform, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }

    public void setBufferMode(boolean isChecked) {
        if(mUseBufferMode!=isChecked){
            mUseBufferMode = isChecked;
            mDrawTextureProgramId = -1;
        }
    }

    public void release(){
        if (mDrawTextureProgramId > 0) {
            GLES20.glDeleteProgram(mDrawTextureProgramId);
        }
        mDrawTextureProgramId = -1;
    }
}
