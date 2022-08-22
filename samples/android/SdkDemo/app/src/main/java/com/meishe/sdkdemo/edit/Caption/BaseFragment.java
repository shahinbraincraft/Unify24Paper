package com.meishe.sdkdemo.edit.Caption;

import androidx.fragment.app.Fragment;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/7/4 下午3:38
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class BaseFragment extends Fragment {

//    protected boolean mIsPartEdit;
//    public void setPartEdit(boolean isPartEdit) {
//        mIsPartEdit=isPartEdit;
//    }

    protected boolean checkIsPartEdit() {
        //需求变更了，当部分编辑的时候不再进行toast 提示 所以暂时注释了
//        if (CaptionStyleActivity.mIsPartEdit) {
//            ToastUtil.showToast(getContext(), R.string.pls_cancel_part_select);
//            return true;
//        }

        return false;
    }

}
