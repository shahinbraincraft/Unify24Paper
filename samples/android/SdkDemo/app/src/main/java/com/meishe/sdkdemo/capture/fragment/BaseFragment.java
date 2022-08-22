package com.meishe.sdkdemo.capture.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.sdkdemo.capture.adapter.CommonRecyclerViewAdapter;
import com.meishe.sdkdemo.capture.bean.EffectInfo;
import com.meishe.sdkdemo.utils.PathUtils;

import java.io.File;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : lpf
 * @CreateDate : 2022/3/22 下午5:00
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public abstract class BaseFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected CommonRecyclerViewAdapter mCommonRecyclerViewAdapter;
    protected Context mContext;
    protected NvsStreamingContext mStreamingContext;
    protected NvsEffectSdkContext mNvsEffectSdkContext;
    protected ViewDataBinding mBinding;

    private View mRootView;

    /**
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     */
    protected boolean mIsPrepare;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,initRootView(),
                container, false);
        mRootView=mBinding.getRoot();
        initArguments(getArguments());
        initView();

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreamingContext = NvsStreamingContext.getInstance();
        mNvsEffectSdkContext= NvsEffectSdkContext.getInstance();
        initData();
        onLazyLoad();
        mIsPrepare = true;
        initListener();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser) {
            onVisibleToUser();
        }
    }

    /**
     * 用户可见时执行的操作
     *
     * @author 漆可
     * @date 2016-5-26 下午4:09:39
     */
    protected void onVisibleToUser() {
        if (mIsPrepare && mIsVisible) {
            onLazyLoad();
        }
    }



    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }

        return (T) mRootView.findViewById(id);
    }


    /**
     * 设置根布局资源id
     *
     * @return
     * @author 漆可
     * @date 2016-5-26 下午3:57:09
     */
    protected abstract int initRootView();


    /**
     * 初始化数据
     *
     * @param arguments 接收到的从其他地方传递过来的参数
     * @author 漆可
     * @date 2016-5-26 下午3:57:48
     */
    protected abstract void initArguments(Bundle arguments);


    /**
     * 初始化View
     *
     * @author 漆可
     * @date 2016-5-26 下午3:58:49
     */
    protected abstract void initView();


    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     *
     * @author 漆可
     * @date 2016-5-26 下午4:10:20
     */
    protected abstract void onLazyLoad();

    /**
     * 初始化数据，非懒加载
     *
     * @author 漆可
     * @date 2016-5-26 下午4:10:20
     */
    protected abstract void initData();


    /**
     * 设置监听事件
     *
     * @author 漆可
     * @date 2016-5-26 下午3:59:36
     */
    protected abstract void initListener();



    protected void initRecyclerView(int orientation,int layoutItemId, int variableId) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                orientation, false));
        mCommonRecyclerViewAdapter = new CommonRecyclerViewAdapter(layoutItemId, variableId);
        mRecyclerView.setAdapter(mCommonRecyclerViewAdapter);
    }

    protected void initRecyclerViewGrid(int spanCount,int layoutItemId, int variableId) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                spanCount));
        mCommonRecyclerViewAdapter = new CommonRecyclerViewAdapter(layoutItemId, variableId);
        mRecyclerView.setAdapter(mCommonRecyclerViewAdapter);
    }

    protected String getFilterPath(EffectInfo filterInfo, int assetType) {
        String packageUrl = filterInfo.getPackageUrl();
        final String pathDir = PathUtils.getAssetDownloadPath(assetType);
        String[] split = packageUrl.split("/");
        String effectPath = pathDir + File.separator + split[split.length - 1];
        return effectPath;
    }
}
