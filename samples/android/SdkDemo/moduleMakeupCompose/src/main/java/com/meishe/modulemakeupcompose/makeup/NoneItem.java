package com.meishe.modulemakeupcompose.makeup;

import android.content.Context;
import com.meishe.utils.ColorUtil;
import java.io.File;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: LiFei
 * @CreateDate: 2021/7/9 18:15
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class NoneItem extends Makeup {
    public static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    public String ASSETS_MAKEUP_COMPOSE_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_compose_none.png";
    public String ASSETS_MAKEUP_CUSTOM_NULL_PATH = ASSETS_MAKEUP_PATH + File.separator + "icon_custom_none.png";


    @Override
    public String getName(Context context) {
        return "无";
    }

    @Override
    public Object getImageResource() {
        return isCompose ? ASSETS_MAKEUP_COMPOSE_NULL_PATH : ASSETS_MAKEUP_CUSTOM_NULL_PATH;
    }

    @Override
    public void setFolderPath(String folderPath) {

    }

    @Override
    public String getFolderPath() {
        return null;
    }

    @Override
    public boolean isBuildIn() {
        return true;
    }

    @Override
    public void setIsBuildIn(boolean isBuildIn) {

    }

    @Override
    public int getBackgroundColor() {
        return isCompose ? ColorUtil.MAKEUP_DEFAULT_TEXT_BG : ColorUtil.FILTER_BG_NO_SELECTED;
    }

}
