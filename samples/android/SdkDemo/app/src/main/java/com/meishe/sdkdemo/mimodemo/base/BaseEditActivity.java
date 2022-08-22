package com.meishe.sdkdemo.mimodemo.base;

import androidx.fragment.app.FragmentManager;

import com.meicam.sdk.NvsTimeline;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseActivity;
import com.meishe.sdkdemo.mimodemo.MimoVideoFragment;
import com.meishe.sdkdemo.mimodemo.common.Constants;

public abstract class BaseEditActivity extends BaseActivity {
    protected MimoVideoFragment mMimoVideoFragment;
    protected NvsTimeline mTimeline;

    @Override
    protected void initData() {
        mTimeline = initTimeLine();
        initVideoFragment();
        initEditData();
    }

    protected abstract void initEditData();

    protected abstract NvsTimeline initTimeLine();

    protected void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mMimoVideoFragment = MimoVideoFragment.newInstance(getVideoDuration());
        mMimoVideoFragment.setEditMode(Constants.EDIT_MODE_COMPOUND_CAPTION);//设置字幕组合模式
        mMimoVideoFragment.setTimeLine(mTimeline);
        fragmentManager.beginTransaction().add(R.id.videoLayout, mMimoVideoFragment).commit();
        fragmentManager.beginTransaction().show(mMimoVideoFragment);
    }

    protected abstract long getVideoDuration();

    @Override
    protected void onPause() {
        super.onPause();
        if (mMimoVideoFragment != null) {
            mMimoVideoFragment.stopEngine();
        }
    }

}
