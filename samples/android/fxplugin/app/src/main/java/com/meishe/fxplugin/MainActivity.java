package com.meishe.fxplugin;

import android.os.Bundle;
import android.view.View;

import com.meishe.fxplugin.base.BasePermissionActivity;
import com.meishe.fxplugin.utils.AppManager;
import com.meishe.fxplugin.utils.Util;

import java.util.List;

public class MainActivity extends BasePermissionActivity {

    @Override
    protected int initRootView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.start_shot).setOnClickListener(this);
        findViewById(R.id.start_use).setOnClickListener(this);

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        checkPermissions();
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_use:
                Bundle editBundle = new Bundle( );
                editBundle.putInt("limitMediaCount", -1);//-1表示无限可选择素材
                AppManager.getInstance( ).jumpActivity(this, MediaSelectActivity.class, editBundle);
                break;
            default:
                break;
        }


    }
}
