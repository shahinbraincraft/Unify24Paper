package com.meishe.modulemakeupcompose.makeup;

import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/13 10:45
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeupCustomModel {
    private int id;
    private boolean isRequest;
    private String makeupId;
    private List<BeautyData> modelContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public String getMakeupId() {
        return makeupId;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
    }

    public List<BeautyData> getModelContent() {
        return modelContent;
    }

    public void setModelContent(List<BeautyData> modelContent) {
        this.modelContent = modelContent;
    }
}
