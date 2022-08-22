package com.meishe.third.pop.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : yangtailin
 * @CreateDate :2021/4/1 14:04
 * @Description :View相关的工具 Util about view
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class ViewUtil {

    /**
     * Expand touch area
     * <p></>
     * 扩大View的点击区域
     * @param view view
     * @param size 扩充大小 size to be expanded
     */
    public static void expandTouchArea(final View view, final int size) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                view.getHitRect(rect);

                rect.top -= size;
                rect.bottom += size;
                rect.left -= size;
                rect.right += size;

                parentView.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }
}
