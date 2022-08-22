package com.meishe.sdkdemo

import com.meishe.sdkdemo.utils.ToastUtil


fun showToast(content:String){
    ToastUtil.showToast(MSApplication.getContext(),content)
}