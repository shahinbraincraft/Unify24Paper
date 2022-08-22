package com.meicam.effectsdkdemo;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cdv
 */
public class CameraProxy {

    private static final String TAG = "CameraProxy";
    private boolean isDebug = true;

    private Context mContext;
    private int mCameraId;
    private Camera mCamera;
    private boolean isCameraOpen = false;
    private boolean mCameraOpenFailed = false;
    private SurfaceTexture mSurfaceTexture;
    private int mCameraDirection;
    private CameraInfo mCameraInfo = new CameraInfo( );
    private static int mLatestRotation;

    public CameraProxy(Context context) {
        mContext = context;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void openCamera(int cameraId) {
        try {
            releaseCamera( );
            mCamera = Camera.open(cameraId);
            mCamera.getParameters( );
            mCameraId = cameraId;
            Camera.getCameraInfo(cameraId, mCameraInfo);
            mCamera.setDisplayOrientation(90);
            setDefaultParameters( );
            isCameraOpen = true;
            mCameraOpenFailed = false;
        } catch (Exception e) {
            mCameraOpenFailed = true;
            mCamera = null;
            Log.i(TAG, "openCamera fail msg=" + e.getMessage( ));
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview( );
            mCamera.release( );
            mCamera = null;
        }
    }

    public static int getLatestRotation() {
        return 0;
//        return mLatestRotation;
    }

    public void setPictureRotate(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;
        }
        orientation = (orientation + 45) / 90 * 90;
        //Log.d("cameraProxy", "setPictureRotate: originSence" + orientation);
        int rotation;
        if (mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (orientation) % 360;
            //Log.d("cameraProxy", "setPictureRotate: front" + rotation);
        } else {
            // back-facing camera
            rotation = (mCameraInfo.orientation + orientation - 90) % 360;
            //Log.d("cameraProxy", "setPictureRotate: back" + rotation);
        }
        mLatestRotation = rotation;
    }

    public void startPreview(SurfaceTexture surfaceTexture, PreviewCallback previewcallback) {
        try {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewTexture(surfaceTexture);
            if (previewcallback != null && mCamera != null) {
                mCamera.setPreviewCallback(previewcallback);
            }
            mCamera.startPreview( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview( );
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview( );
        }
    }

    public Size getPreviewSize() {
        if (mCamera != null) {
            return mCamera.getParameters( ).getPreviewSize( );
        }
        return null;
    }

    public Size getPictureSize() {
        if (mCamera != null) {
            return mCamera.getParameters( ).getPictureSize( );
        }
        return null;
    }

    public void setOneShotPreviewCallback(PreviewCallback callback) {
        mCamera.setOneShotPreviewCallback(callback);
    }


    public void addPreviewCallbackBuffer(byte[] callbackBuffer) {
        mCamera.addCallbackBuffer(callbackBuffer);
    }


    public int getOrientation() {
        if (mCameraInfo == null) {
            return 0;
        }
        return mCameraInfo.orientation;
    }

    public boolean isFlipHorizontal() {
        if (mCameraInfo == null) {
            return false;
        }
        return mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public boolean isFrontCamera() {
        return mCameraId == CameraInfo.CAMERA_FACING_FRONT;
    }

    public void setRotation(int rotation) {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters( );
            params.setRotation(rotation);
            mCamera.setParameters(params);
        }
    }

    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                            Camera.PictureCallback jpegCallback) {
        if (mCamera != null) {
            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
        }
    }

    public int getDisplayOrientation(int dir) {
        /**
         * 请注意前置摄像头与后置摄像头旋转定义不同
         * 请注意不同手机摄像头旋转定义不同
         */
        int newdir = dir;
        if (isFrontCamera( ) &&
                ((mCameraInfo.orientation == 270 && (dir & 1) == 1) ||
                        (mCameraInfo.orientation == 90 && (dir & 1) == 0))) {
            newdir = (dir ^ 2);
        }
        return newdir;
    }

    public boolean needMirror() {
        if (isFrontCamera( )) {
            return true;
        } else {
            return false;
        }
    }

    private void setDefaultParameters() {
        Parameters parameters = mCamera.getParameters( );
        Log.e(TAG, "parameters: " + parameters.flatten( ));
        if (parameters.getSupportedFocusModes( ).contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        List<String> flashModes = parameters.getSupportedFlashModes( );
        if ((flashModes != null) && flashModes.contains(Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }

        Point previewSize = getSuitablePreviewSize( );
        //	parameters.setPreviewSize(previewSize.x, previewSize.y);
        parameters.setPreviewSize(640, 480);
        Point pictureSize = getSuitablePictureSize( );
        parameters.setPictureSize(pictureSize.x, pictureSize.y);

        mCamera.setParameters(parameters);
    }

    public Parameters getParameters() {
        return mCamera.getParameters( );
    }

    public void setPreviewSize(int width, int height) {
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters( );
        parameters.setPreviewSize(width, height);
        if (mCamera == null) {
            return;
        }
        mCamera.setParameters(parameters);
    }

    private Point getSuitablePreviewSize() {
        Point defaultsize = new Point(1920, 1080);
        if (mCamera != null) {
            List<Size> sizes = mCamera.getParameters( ).getSupportedPreviewSizes( );
            for (Size s : sizes) {
                if ((s.width == defaultsize.x) && (s.height == defaultsize.y)) {
                    return defaultsize;
                }
            }
            return new Point(640, 480);
        }
        return null;
    }

    public ArrayList<String> getSupportedPreviewSize(String[] previewSizes) {
        ArrayList<String> result = new ArrayList<String>( );
        if (mCamera != null) {
            List<Size> sizes = mCamera.getParameters( ).getSupportedPreviewSizes( );
            for (String candidate : previewSizes) {
                int index = candidate.indexOf('x');
                if (index == -1) {
                    continue;
                }
                int width = Integer.parseInt(candidate.substring(0, index));
                int height = Integer.parseInt(candidate.substring(index + 1));
                for (Size s : sizes) {
                    if ((s.width == width) && (s.height == height)) {
                        result.add(candidate);
                    }
                }
            }
        }
        return result;
    }

    private Point getSuitablePictureSize() {
        Point defaultsize = new Point(4608, 3456);
        //	Point defaultsize = new Point(3264, 2448);
        if (mCamera != null) {
            Point maxSize = new Point(0, 0);
            List<Size> sizes = mCamera.getParameters( ).getSupportedPictureSizes( );
            for (Size s : sizes) {
                if ((s.width == defaultsize.x) && (s.height == defaultsize.y)) {
                    return defaultsize;
                }
                if (maxSize.x < s.width) {
                    maxSize.x = s.width;
                    maxSize.y = s.height;
                }
            }
            return maxSize;
        }
        return null;
    }


    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras( );
    }

    public boolean cameraOpenFailed() {
        return mCameraOpenFailed;
    }

    public boolean isCameraOpen() {
        return isCameraOpen;
    }

    public void toggleFlash(boolean state) {
        Parameters parameters = mCamera.getParameters( );
        if (state) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }
        if (mCamera == null) {
            return;
        }
        mCamera.setParameters(parameters);
    }

    public boolean setExposureCompensation(int exposureCompensation) {
        Camera.Parameters parameters = mCamera.getParameters( );
        int maxExposureCompensation = parameters.getMaxExposureCompensation( );
        int minExposureCompensation = parameters.getMinExposureCompensation( );
        if (minExposureCompensation >= maxExposureCompensation) {
            return false;
        }
        parameters.setExposureCompensation(exposureCompensation);
        mCamera.setParameters(parameters);
        return true;
    }

    public int getExposureCompensationRange() {
        Camera.Parameters parameters = mCamera.getParameters( );
        return parameters.getMaxExposureCompensation( ) * 2;
    }

    public boolean isSupportExpose() {
        Camera.Parameters parameters = mCamera.getParameters( );
        int maxExposureCompensation = parameters.getMaxExposureCompensation( );
        int minExposureCompensation = parameters.getMinExposureCompensation( );
        if (minExposureCompensation >= maxExposureCompensation) {
            return false;
        }
        return true;
    }

    public boolean isSupportZoom() {
        Parameters parameters = mCamera.getParameters( );
        return parameters.isZoomSupported( );
    }

    public int getZoomRange() {
        if (!isSupportZoom( )) {
            return 0;
        }
        Parameters parameters = mCamera.getParameters( );
        // 变焦太大 预览画面变黑
        return parameters.getMaxZoom( ) / 3;
    }

    public boolean setZoom(int zoomValue) {
        try {
            Parameters parameters = mCamera.getParameters( );
            parameters.setZoom(zoomValue);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace( );
            return false;
        }
        return true;
    }

    public void autoFocus(View surfaceView, MotionEvent event) {
        // 获取映射区域的X坐标
        int x = (int) (event.getX( ) / surfaceView.getWidth( ) * 2000) - 1000;
        // 获取映射区域的Y坐标
        int y = (int) (event.getY( ) / surfaceView.getWidth( ) * 2000) - 1000;
        // 创建Rect区域
        Rect focusArea = new Rect( );
        // 取最大或最小值，避免范围溢出屏幕坐标
        focusArea.left = Math.max(x - 100, -1000);
        focusArea.top = Math.max(y - 100, -1000);
        focusArea.right = Math.min(x + 100, 1000);
        focusArea.bottom = Math.min(y + 100, 1000);
        // 创建Camera.Area
        Parameters parameters = mCamera.getParameters( );
        Camera.Area cameraArea = new Camera.Area(focusArea, 1000);
        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>( );
        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>( );
        if (parameters.getMaxNumMeteringAreas( ) > 0) {
            meteringAreas.add(cameraArea);
            focusAreas.add(cameraArea);
        }
        // 设置对焦模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        // 设置对焦区域
        parameters.setFocusAreas(focusAreas);
        // 设置测光区域
        parameters.setMeteringAreas(meteringAreas);
        try {
            // 每次对焦前，需要先取消对焦
            mCamera.cancelAutoFocus( );
            // 设置相机参数
            mCamera.setParameters(parameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback( ) {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.d(TAG, "onAutoFocus: success");
                    }
                }
            });
        } catch (Exception e) {
            // 开启对焦
            Log.e(TAG, "autoFocus: error");
        }
    }
}
