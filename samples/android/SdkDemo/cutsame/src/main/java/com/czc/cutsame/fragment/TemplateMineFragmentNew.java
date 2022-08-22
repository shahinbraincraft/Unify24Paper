package com.czc.cutsame.fragment;

import static com.czc.cutsame.util.CustomConstants.DATA_TEMPLATE;
import static com.czc.cutsame.util.CustomConstants.TEMPLATE_IS_FROM_LOCAL;
import static com.czc.cutsame.util.CustomConstants.TEMPLATE_IS_FROM_MINE;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.czc.cutsame.R;
import com.czc.cutsame.TemplatePreviewActivity;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.czc.cutsame.fragment.adapter.TemplateAdapter;
import com.czc.cutsame.fragment.iview.TemplateView;
import com.czc.cutsame.fragment.presenter.TemplateMinePresenter;
import com.czc.cutsame.fragment.presenter.TemplatePresenter;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.NetUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.util.List;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板列表fragment
 * Template list Fragment
 */
public class TemplateMineFragmentNew extends BaseMvpFragment<TemplateMinePresenter> implements TemplateView {
    private SwipeRefreshLayout mSrlRefresh;
    private RecyclerView mRvTemplateList;
    private TemplateAdapter mAdapter;
    private View mTvNoDataView;
    private TextView tvReload;
    private LinearLayout rootNoNet;

    public TemplateMineFragmentNew() {
    }

    /**
     * Bind layout int.
     *
     * @return the int
     */
    @Override
    protected int bindLayout() {
        return R.layout.fragment_template;
    }

    /**
     * On lazy load.
     */
    @Override
    protected void onLazyLoad() {
        if (mTvNoDataView != null) {
            mTvNoDataView.setVisibility(View.GONE);
        }
        if (mPresenter != null) {
            mPresenter.getTemplateList(getContext(), 0, false);
        }
    }

    /**
     * Init view.
     *
     * @param rootView the root view
     */
    @Override
    protected void initView(View rootView) {
        mSrlRefresh = rootView.findViewById(R.id.srl_refresh);
        rootNoNet = rootView.findViewById(R.id.root_no_net);
        tvReload = rootView.findViewById(R.id.tv_reload);
        mRvTemplateList = rootView.findViewById(R.id.rv_list);
        mTvNoDataView = rootView.findViewById(R.id.tv_no_date);
        final int decoration = (int) getResources().getDimension(R.dimen.dp_px_30);
        int screenWidth = ScreenUtils.getScreenWidth();
        if (mRvTemplateList.getLayoutParams() != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mRvTemplateList.getLayoutParams();
            layoutParams.width = screenWidth - 2 * decoration;
            layoutParams.leftMargin = (int) (decoration / 2f);
            mRvTemplateList.setLayoutParams(layoutParams);
        }
        mRvTemplateList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new TemplateAdapter((ScreenUtils.getScreenWidth() - 4 * decoration) / 2);
        initListener();
        if (!NetUtils.isNetworkAvailable(getActivity()) && (!TemplatePreviewActivity.IS_DEBUG)) {
            rootNoNet.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter != null && !TemplatePreviewActivity.IS_DEBUG) {
                    mPresenter.getTemplateList(getContext(), 0, true);
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), TemplatePreviewActivity.class);
                    intent.putExtra(DATA_TEMPLATE, mAdapter.getData().get(position));
                    intent.putExtra(TEMPLATE_IS_FROM_MINE, true);
                    intent.putExtra(TEMPLATE_IS_FROM_LOCAL, !mPresenter.isFromNet());
                    getActivity().startActivity(intent);
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mPresenter != null && !mPresenter.getMoreTemplate(getContext())) {
                    mAdapter.loadMoreEnd();
                }
            }
        }, mRvTemplateList);

        tvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null && !TemplatePreviewActivity.IS_DEBUG) {
                    mPresenter.getTemplateList(getContext(), 0, true);
                }
            }
        });
    }

    /**
     * Init data.
     */
    @Override
    protected void initData() {
        mAdapter.setHasStableIds(true);
        mRvTemplateList.setAdapter(mAdapter);
        mPresenter.getTemplateList(getContext(), 0, false);
    }

    /**
     * On template category back.
     *
     * @param categoryList the category list
     */
    @Override
    public void onTemplateCategoryBack(List<TemplateCategory.Category> categoryList) {

    }

    /**
     * On template list back.
     *
     * @param templateList the template list
     */
    @Override
    public void onTemplateListBack(List<Template> templateList) {
        if (mSrlRefresh.isRefreshing()) {
            mSrlRefresh.setRefreshing(false);
        }
        rootNoNet.setVisibility(View.INVISIBLE);
        if (templateList != null && templateList.size() > 0) {
            if (templateList.size() < TemplatePresenter.PAGE_NUM) {
                mAdapter.loadMoreEnd(true);
            }
        } else {
            setEmptyView();
        }
        mAdapter.setNewData(templateList);
    }

    private void setEmptyView() {
        if (TemplatePreviewActivity.IS_DEBUG) {
            return;
        }
        if (mAdapter == null || mAdapter.getData() == null || mAdapter.getData().size() == 0) {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.VISIBLE);
                rootNoNet.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mTvNoDataView != null) {
                mTvNoDataView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * On more template back.
     *
     * @param templateList the template list
     */
    @Override
    public void onMoreTemplateBack(List<Template> templateList) {
        if (templateList == null || templateList.size() == 0) {
            mAdapter.loadMoreEnd();
            setEmptyView();
        } else {
            mAdapter.loadMoreComplete();
            mAdapter.addData(templateList);
        }
    }


    /**
     * On download template success.
     *
     * @param templatePath the template path
     */
    @Override
    public void onDownloadTemplateSuccess(String templatePath, boolean isTemplate) {

    }
}
