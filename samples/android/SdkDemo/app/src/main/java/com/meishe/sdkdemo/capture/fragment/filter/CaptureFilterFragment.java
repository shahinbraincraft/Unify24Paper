package com.meishe.sdkdemo.capture.fragment.filter;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.meishe.base.msbus.MSBus;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.BaseFragmentPagerAdapter;
import com.meishe.sdkdemo.capture.CaptureActivity;
import com.meishe.sdkdemo.capture.bean.CategoryInfo;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.capture.bean.KindInfo;
import com.meishe.sdkdemo.capture.bean.TypeAndCategoryInfo;
import com.meishe.sdkdemo.capture.fragment.BaseFragment;
import com.meishe.sdkdemo.edit.view.CustomViewPager;
import com.meishe.sdkdemo.utils.Constants;
import com.meishe.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ms
 */
public class CaptureFilterFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private TabLayout mTabLayout;
    private CustomViewPager mViewPager;
    private TypeAndCategoryInfo mTypeAndCategoryInfo;
    private View mIvNo;


    public CaptureFilterFragment() {
        // Required empty public constructor
    }

    public static CaptureFilterFragment newInstance(TypeAndCategoryInfo filterTypeInfo) {
        CaptureFilterFragment fragment = new CaptureFilterFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, filterTypeInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTypeAndCategoryInfo = (TypeAndCategoryInfo) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_bottom_view, container, false);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);
        mIvNo = view.findViewById(R.id.iv_no);
        mIvNo.setVisibility(View.VISIBLE);
        initViewPager();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        mIvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSBus.getInstance().post(Constants.SubscribeType.SUB_UN_USE_FILTER_TYPE);
            }
        });

        return view;
    }

    @Override
    protected int initRootView() {
        return 0;
    }

    @Override
    protected void initArguments(Bundle arguments) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initViewPager() {
        List<String> stringArray = new ArrayList<>();
        List<KindInfo> kinds = null;
        List<KindInfo> allKinds = new ArrayList<>();
        List<Fragment> fragmentLists = new ArrayList<>();
        if (mTypeAndCategoryInfo != null) {
            List<CategoryInfo> categories = mTypeAndCategoryInfo.getCategories();
            if (categories != null) {
                for (CategoryInfo categoryInfo : categories) {
                    String displayNameZhCn = categoryInfo.getDisplayNameZhCn();
                    if ("动画".equals(displayNameZhCn)) {
                        continue;
                    }
                    kinds = categoryInfo.getKinds();
                    allKinds.addAll(allKinds.size(), kinds);
                    boolean isChinese = SystemUtils.isZh(MSApplication.getContext());
                    for (KindInfo kindinfo : kinds) {
                        if (isChinese) {
                            stringArray.add(kindinfo.getDisplayNameZhCn());
                        } else {
                            stringArray.add(kindinfo.getDisplayName());
                        }
                    }
                }
            }
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(Constants.TITLE_SEARCH));
        SearchFragment searchFragment = SearchFragment.newInstance("");
        fragmentLists.add(searchFragment);
        for (int i = 0; i < stringArray.toArray().length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(stringArray.get(i)));
            CaptureFilterTabFragment captureFilterFragment = CaptureFilterTabFragment.
                    newInstance(allKinds.get(i));
            fragmentLists.add(captureFilterFragment);
            if (TextUtils.equals(stringArray.get(i), "LUT")) {
                EffectInfo needSelectEffectInfo = CaptureActivity.mNeedSelectEffectInfo;
                if (needSelectEffectInfo!=null){
                    captureFilterFragment.setDefaultFilterInfo(needSelectEffectInfo);
                }
            }
        }
        stringArray.add(0, Constants.TITLE_SEARCH);

        mViewPager.setOffscreenPageLimit(1);
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(
                getChildFragmentManager(),
                fragmentLists, stringArray);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setPagingEnabled(false);
        mTabLayout.setupWithViewPager(mViewPager);

    }
}