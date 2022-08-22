package com.czc.cutsame.fragment;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.czc.cutsame.R;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.czc.cutsame.fragment.iview.TemplateView;
import com.czc.cutsame.fragment.presenter.TemplatePresenter;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.CommonUtils;
import com.meishe.third.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板列表fragment
 */
public class TemplateListFragment extends BaseMvpFragment<TemplatePresenter> implements TemplateView {
    public static String cut_model;
    //private TextView mTvSearch;
    private SlidingTabLayout mTlTemplate;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTabTitleList = new ArrayList<>();

    public TemplateListFragment() {
    }

    public static TemplateListFragment create(String cut_model) {
        TemplateListFragment.cut_model = cut_model;
        return new TemplateListFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_template_list;
    }

    @Override
    public void onLazyLoad() {
        if (mTabTitleList.size() <= 0) {
            mPresenter.getTemplateCategory();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mViewPager != null) {
                if (mFragmentList.size() == 0) {
                    return;
                }
                int position = mTlTemplate.getCurrentTab();
                if (!CommonUtils.isIndexAvailable(position, mFragmentList)) {
                    return;
                }
                Fragment mCurrentFragment = mFragmentList.get(mTlTemplate.getCurrentTab());
                if (mCurrentFragment instanceof TemplateMineFragmentNew) {
                    ((TemplateMineFragmentNew) mCurrentFragment).onLazyLoad();
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    protected void initView(View view) {
        //mTvSearch = view.findViewById(R.id.tv_search_hint);
        mTlTemplate = view.findViewById(R.id.tl_template_title);
        mViewPager = view.findViewById(R.id.vp_pager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new CommonFragmentAdapter(getChildFragmentManager(), mFragmentList, mTabTitleList));
        mTlTemplate.setViewPager(mViewPager);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTlTemplate.getLayoutParams();
//        layoutParams.topMargin = (int) (ScreenUtils.getStatusBarHeight() + getContext().getResources().getDimension(R.dimen.title_margin_top));
        mTlTemplate.setLayoutParams(layoutParams);
//        if (!NetUtils.isNetworkAvailable(getActivity())) {
//            mTabTitleList.add(getString(R.string.activity_cut_export_template_mine));
//            mFragmentList.add(new TemplateMineFragmentNew());
//            mTlTemplate.updateTitles(mTabTitleList);
//        }
    }

    @Override
    protected void initData() {
        mPresenter.getTemplateCategory();
    }


    @Override
    public void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList) {
        mTabTitleList.clear();
        mFragmentList.clear();
        if (categoryList != null && categoryList.size() > 0) {
            for (TemplateCategory.Category category : categoryList) {
                mTabTitleList.add(getString(R.string.template_currency));
                mFragmentList.add(TemplateFragment.create(category.getCategory()));
            }
//            mTabTitleList.add(getString(R.string.activity_cut_export_template_mine));
//            mFragmentList.add(new TemplateMineFragmentNew());

            mTabTitleList.add(getString(R.string.template_not_limit_time));
            mFragmentList.add(TemplateFragment.create(2));

            mTlTemplate.updateTitles(mTabTitleList);
        } else {
//            mTabTitleList.add(getString(R.string.activity_cut_export_template_mine));
//            mFragmentList.add(new TemplateMineFragmentNew());
//            mTlTemplate.updateTitles(mTabTitleList);
        }
    }

    @Override
    public void onTemplateListBack(List<Template> templateList) {

    }

    @Override
    public void onMoreTemplateBack(List<Template> templateList) {

    }

    @Override
    public void onDownloadTemplateSuccess(String templatePath, boolean isTemplate) {

    }
}
