package com.meishe.modulemakeupcompose.makeup;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/8 15:34
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeupEffectContent {

    private List<MakeupArgs> makeupArgs;
    private List<BeautyFxArgs> beauty;
    private List<BeautyFxArgs> shape;
    private List<BeautyFxArgs> microShape;
    private List<FilterArgs> filter;

    public List<FilterArgs> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterArgs> filter) {
        this.filter = filter;
    }

    public List<BeautyFxArgs> getShape() {
        return shape;
    }

    public void setShape(List<BeautyFxArgs> shape) {
        this.shape = shape;
    }

    public List<BeautyFxArgs> getBeauty() {
        return beauty;
    }

    public void setBeauty(List<BeautyFxArgs> beauty) {
        this.beauty = beauty;
    }

    public List<MakeupArgs> getMakeupArgs() {
        return makeupArgs;
    }

    public void setMakeupArgs(List<MakeupArgs> makeupArgs) {
        this.makeupArgs = makeupArgs;
    }

    public List<BeautyFxArgs> getMicroShape() {
        return microShape;
    }

    public void setMicroShape(List<BeautyFxArgs> microShape) {
        this.microShape = microShape;
    }
}
