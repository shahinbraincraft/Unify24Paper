package com.meishe.render.entity;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @author :Jml
 * @date :2022/4/13 14:45
 * @des :
 * @Copyright: www.meishesdk.com Inc. All rights reserved
 */
public class RenderEffectParams {
   //需要渲染的特技
    private List<EffectRenderItem>effectRenderItemList;
    //移除的特技
    private List<EffectRenderItem>clearRenderItemList;
    //输入纹理
    private int inputTexture;
    //是否是oes类型
    private boolean isOESTexture;
    //纹理宽
    private int width;
    //纹理高
    private int height;
    //数据的类型
    private int dateType;
    //当前的时间戳
    private long currentTimeStamp;
    //相机角度
    private int cameraOrientation;
    //相机角度和手机朝向
    private int displayOrientation;
    //水平翻转
    private boolean flipHorizontal;
    //手机的垂直角度
    private int deviceOrientation;
    //使用图片模式 或 视频模式
    private boolean useImageMode;
    //是否使用buffer进行渲染
    private boolean useBufferMode;

    public RenderEffectParams setRenderEffectList(List<EffectRenderItem> effectRenderItemList){
        this.effectRenderItemList = effectRenderItemList;
        return this;
    }

    public RenderEffectParams setClearEffectList(List<EffectRenderItem> clearRenderItemList){
        this.clearRenderItemList = clearRenderItemList;
        return this;
    }
    public RenderEffectParams setTexture(int inputTexture){
        this.inputTexture = inputTexture;
        return this;
    }
    public RenderEffectParams isOesTexture(boolean isOESTexture){
        this.isOESTexture = isOESTexture;
        return this;
    }

    public RenderEffectParams setHeight(int height){
        this.height = height;
        return this;
    }
    public RenderEffectParams setWidth(int width){
        this.width = width;
        return this;
    }
    public RenderEffectParams setDataType(int dataType){
        this.dateType = dataType;
        return this;
    }
    public RenderEffectParams setCurrentTimeStamp(long currentTimeStamp){
        this.currentTimeStamp = currentTimeStamp;
        return this;
    }

    public RenderEffectParams setCameraOrientation(int cameraOrientation){
        this.cameraOrientation = cameraOrientation;
        return this;
    }

    public RenderEffectParams setDisplayOrientation(int displayOrientation){
        this.displayOrientation = displayOrientation;
        return this;
    }

    public RenderEffectParams isFlipHorizontal(boolean flipHorizontal){
        this.flipHorizontal = flipHorizontal;
        return this;
    }

    public RenderEffectParams setDeviceOrientation(int deviceOrientation){
        this.deviceOrientation = deviceOrientation;
        return this;
    }

    public RenderEffectParams isImageMode(boolean useImageMode){
        this.useImageMode = useImageMode;
        return this;
    }

    public RenderEffectParams isBufferMode(boolean useBufferMode){
        this.useBufferMode = useBufferMode;
        return this;
    }
    public RenderEffectParams build(){
        return this;
    }

    public List<EffectRenderItem> getEffectRenderItemList() {
        return effectRenderItemList;
    }

    public List<EffectRenderItem> getClearRenderItemList() {
        return clearRenderItemList;
    }

    public int getInputTexture() {
        return inputTexture;
    }

    public boolean isOESTexture() {
        return isOESTexture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDateType() {
        return dateType;
    }

    public long getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    public int getCameraOrientation() {
        return cameraOrientation;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public boolean isFlipHorizontal() {
        return flipHorizontal;
    }

    public int getDeviceOrientation() {
        return deviceOrientation;
    }

    public boolean isUseImageMode() {
        return useImageMode;
    }

    public boolean isUseBufferMode() {
        return useBufferMode;
    }
}
