package com.meishe.modulearscene.bean;

/**
 * @author zcy
 * @Destription: 美型Bean
 * @Emial:
 * @CreateDate: 2022/7/6.
 */
public class ShapeBean extends ArBean {
    //最新美型包标记
    private boolean packageShapeFlag = false;
    private String warpPath;
    private String faceMeshPath;
    private String warpId;
    private String faceMeshId;
    private String warpUUID;
    private String faceUUID;
    private String warpDegree;
    private String faceDegree;
    private boolean wrapFlag = false;
    //用来标记是否反转值的标记
    private boolean shapeFlag;

    public boolean isShapeFlag() {
        return shapeFlag;
    }

    public void setShapeFlag(boolean shapeFlag) {
        this.shapeFlag = shapeFlag;
    }

    public boolean isPackageShapeFlag() {
        return packageShapeFlag;
    }

    public void setPackageShapeFlag(boolean packageShapeFlag) {
        this.packageShapeFlag = packageShapeFlag;
    }

    public String getWarpPath() {
        return warpPath;
    }

    public void setWarpPath(String warpPath) {
        setPackageShapeFlag(true);
        setWrapFlag(true);
        this.warpPath = warpPath;
    }

    public String getFaceMeshPath() {
        return faceMeshPath;
    }

    public void setFaceMeshPath(String faceMeshPath) {
        setPackageShapeFlag(true);
        setWrapFlag(false);
        this.faceMeshPath = faceMeshPath;
    }

    public String getWarpId() {
        return warpId;
    }

    public void setWarpId(String warpId) {
        this.warpId = warpId;
    }

    public String getFaceMeshId() {
        return faceMeshId;
    }

    public void setFaceMeshId(String faceMeshId) {
        this.faceMeshId = faceMeshId;
    }

    public String getWarpUUID() {
        return warpUUID;
    }

    public void setWarpUUID(String warpUUID) {
        this.warpUUID = warpUUID;
    }

    public String getFaceUUID() {
        return faceUUID;
    }

    public void setFaceUUID(String faceUUID) {
        this.faceUUID = faceUUID;
    }

    public String getWarpDegree() {
        return warpDegree;
    }

    public void setWarpDegree(String warpDegree) {
        this.warpDegree = warpDegree;
    }

    public String getFaceDegree() {
        return faceDegree;
    }

    public void setFaceDegree(String faceDegree) {
        this.faceDegree = faceDegree;
    }

    public boolean isWrapFlag() {
        return wrapFlag;
    }

    public void setWrapFlag(boolean wrapFlag) {
        this.wrapFlag = wrapFlag;
    }
}
