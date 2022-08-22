package com.meishe.fxplugin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;


import com.meishe.fxplugin.adapter.AgendaSimpleSectionAdapter;
import com.meishe.fxplugin.base.BaseActivity;
import com.meishe.fxplugin.base.BaseFragmentPagerAdapter;
import com.meishe.fxplugin.data.ClipInfo;
import com.meishe.fxplugin.data.MediaData;
import com.meishe.fxplugin.data.TimelineData;
import com.meishe.fxplugin.interfaces.OnTotalNumChangeForActivity;
import com.meishe.fxplugin.utils.AppManager;
import com.meishe.fxplugin.utils.Constants;
import com.meishe.fxplugin.utils.Logger;
import com.meishe.fxplugin.utils.MediaConstant;
import com.meishe.fxplugin.utils.Util;
import com.meishe.fxplugin.utils.asset.NvAsset;
import com.meishe.fxplugin.view.CustomTitleBar;
import com.meishe.fxplugin.view.MediaFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.meishe.fxplugin.utils.MediaConstant.KEY_CLICK_TYPE;
import static com.meishe.fxplugin.utils.MediaConstant.LIMIT_COUNT_MAX;


public class MediaSelectActivity extends BaseActivity implements OnTotalNumChangeForActivity {
    private String TAG = getClass().getName();
    private CustomTitleBar mTitleBar;
    private TabLayout tlSelectMedia;
    private ViewPager vpSelectMedia;
    private List<Fragment> fragmentLists = new ArrayList<>();
    private List<String> fragmentTabTitles = new ArrayList<>();
    private BaseFragmentPagerAdapter fragmentPagerAdapter;
    private List<MediaData> mMediaDataList = new ArrayList<>();
    private Integer[] fragmentTotalNumber = {0,0,0};
    private int nowFragmentPosition = 0;
    private TextView importMedia;
    private int mLimiteMediaCount = -1;
    private volatile MediaData mSelectMediaData;
    private volatile List mSelectMediaList;

    @Override
    protected int initRootView() {
        return R.layout.activity_select_media;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        tlSelectMedia = (TabLayout) findViewById(R.id.tl_select_media);
        vpSelectMedia = (ViewPager) findViewById(R.id.vp_select_media);
        importMedia = (TextView) findViewById(R.id.import_media);
        importMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectMediaList != null && mSelectMediaList.size() > 0) {
                    int makeRatio = NvAsset.AspectRatio_NoFitRatio;
                    ArrayList<ClipInfo> pathList = getClipInfoList( );
                    TimelineData.instance( ).setVideoResolution(Util.getVideoEditResolution(makeRatio));
                    TimelineData.instance( ).setClipInfoData(pathList);
                    TimelineData.instance( ).setMakeRatio(makeRatio);
                    AppManager.getInstance( ).jumpActivity(MediaSelectActivity.this, MediaEditActivity.class, null);
                    AppManager.getInstance( ).finishActivity( );
                }

            }
        });

    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (int i = 0; i < mSelectMediaList.size(); i++) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setImgDispalyMode(Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY);
            clipInfo.setOpenPhotoMove(false);
            clipInfo.setFilePath(((MediaData)mSelectMediaList.get(i)).getPath());
            pathList.add(clipInfo);
        }
        return pathList;
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.selectMedia);
    }


    @Override
    protected void initData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mLimiteMediaCount = bundle.getInt("limitMediaCount",-1);
            }
        }

        String[] tabList = getResources().getStringArray(R.array.select_media);
        fragmentLists = getSupportFragmentManager().getFragments();
        if (fragmentLists == null || fragmentLists.size() == 0) {
            fragmentLists = new ArrayList<>();
            for (int i = 0; i < tabList.length; i++) {
                MediaFragment mediaFragment = new MediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.MEDIATYPECOUNT[i]);
                bundle.putInt(LIMIT_COUNT_MAX, mLimiteMediaCount);
                bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_MULTIPLE);
                mediaFragment.setArguments(bundle);
                fragmentLists.add(mediaFragment);
            }
        }
        for (int i = 0; i < tabList.length; i++) {
            fragmentTabTitles.add(tabList[i]);
        }

        /*
        * 禁止预加载
        * Disable preload
        * */
        vpSelectMedia.setOffscreenPageLimit(3);
        fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragmentLists, fragmentTabTitles);
        vpSelectMedia.setAdapter(fragmentPagerAdapter);
        vpSelectMedia.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowFragmentPosition = position;
                for (int i = 0; i < fragmentLists.size(); i++) {
                    MediaFragment mediaFragment = (MediaFragment) fragmentLists.get(i);
                    List<Integer> list = Arrays.asList(fragmentTotalNumber);
                    if (!list.isEmpty()){
                        mediaFragment.setTotalSize(Collections.max(list));
                    }
                }
                notifyFragmentDataSetChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tlSelectMedia.setupWithViewPager(vpSelectMedia);
    }

    /**
     * 校验一次数据，使得item标注的数据统一
     *
     * Check the data once to make the data marked by the item uniform
     *
     * @param position 碎片索引值0，1，2；Shard index value 0.1.2
     */
    private void notifyFragmentDataSetChanged(int position) {
        MediaFragment fragment = (MediaFragment) fragmentLists.get(position);
        List<MediaData> currentFragmentList = checkoutSelectList(fragment);
        fragment.getAdapter().setSelectList(currentFragmentList);
    }

    private List<MediaData> checkoutSelectList(MediaFragment fragment) {
        List<MediaData> currentFragmentList = fragment.getAdapter().getSelectList();
        List<MediaData> totalSelectList = getMediaDataList();
        for (MediaData mediaData : currentFragmentList) {
            for (MediaData data : totalSelectList) {
                if (data.getPath().equals(mediaData.getPath()) && data.isState() == mediaData.isState()) {
                    mediaData.setPosition(data.getPosition());
                }
            }
        }
        return currentFragmentList;
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < fragmentLists.size(); i++) {
            fragmentLists.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    public List<MediaData> getMediaDataList() {
        if (mMediaDataList == null) {
            return new ArrayList<>();
        }
        MediaFragment fragment = (MediaFragment) fragmentLists.get(0);
        if (fragment != null) {
            AgendaSimpleSectionAdapter adapter = fragment.getAdapter();
            if (adapter != null) {
                return adapter.getSelectList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
//        setTotal(0);
        nowFragmentPosition = 0;
        super.onDestroy();
        Logger.e(TAG, "onDestroy");
    }

    @Override
    public void onTotalNumChangeForActivity(List selectList, Object tag) {
        if(selectList == null) {
            return;
        }
        int index = (int) tag;
        fragmentTotalNumber[index] = selectList.size();
        if(nowFragmentPosition == ((Integer)tag)) {
            mSelectMediaList = selectList;
        }
        if(nowFragmentPosition == index) {
            if(fragmentTotalNumber[index] > 0) {
//                mSelectMediaData = (MediaData) selectList.get(0);
                importMedia.setVisibility(View.VISIBLE);
            } else {
                importMedia.setVisibility(View.GONE);
            }
        }

        Logger.e("onTotalNumChangeForActivity", "对应的碎片：  " + index+"    个数："+selectList.size() + "  tag:" + tag);
        for (int i = 0; i < fragmentLists.size(); i++) {
            if (i != index) {
                Logger.e("2222", "要刷新的碎片：  " + i);
                MediaFragment fragment = (MediaFragment) fragmentLists.get(i);
                fragment.refreshSelect(selectList, index);
            }
        }
    }
}
