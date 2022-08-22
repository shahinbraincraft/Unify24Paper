package com.meishe.sdkdemo.cutsame;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.czc.cutsame.CutSameEditorActivity;
import com.czc.cutsame.fragment.TemplateListFragment;
import com.meishe.net.server.OkDownload;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.edit.interfaces.OnTitleBarClickListener;
import com.meishe.sdkdemo.edit.view.CustomTitleBar;

/**
 * 剪同款页面
 */
public class CutSameActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;
    public String cut_model = "";

    @Override
    protected int initRootView() {
        return R.layout.activity_cut_same;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        initViewFragment();
        OkDownload.initAndroidOs(this);
    }

    private void initViewFragment() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            cut_model = getIntent().getExtras().getString(CutSameEditorActivity.BUNDLE_KEY, "");
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        TemplateListFragment templateListFragment = TemplateListFragment.create(cut_model);
        fragmentTransaction.add(R.id.container, templateListFragment).commit();
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.cutSame);
    }

    @Override
    protected void initData() {
        registerFont();
    }

    private void registerFont(){
        mStreamingContext.registerFontByFilePath("assets:/cutsamefont/pp_pixel.ttf");
        mStreamingContext.registerFontByFilePath("assets:/cutsamefont/Muyao-Softbrush.ttf");
    }
    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                finish();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
