package com.czc.cutsame.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.czc.cutsame.R;
import com.czc.cutsame.TemplatePreviewActivity;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateCategory;
import com.czc.cutsame.fragment.adapter.TemplateAdapter;
import com.czc.cutsame.fragment.iview.TemplateView;
import com.czc.cutsame.fragment.presenter.TemplatePresenter;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.model.BaseMvpFragment;
import com.meishe.base.utils.FileUtils;
import com.meishe.base.utils.NetUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.engine.util.PathUtils;
import com.meishe.third.adpater.BaseQuickAdapter;

import java.io.File;
import java.util.List;

import static com.czc.cutsame.util.CustomConstants.CATEGORY_ID;
import static com.czc.cutsame.util.CustomConstants.DATA_TEMPLATE;

/**
 * author : lhz
 * date   : 2020/11/3
 * desc   :模板列表fragment
 * Template list Fragment
 */
public class TemplateFragment extends BaseMvpFragment<TemplatePresenter> implements TemplateView {
    private SwipeRefreshLayout mSrlRefresh;
    private RecyclerView mRvTemplateList;
    private TemplateAdapter mAdapter;
    private int mCategoryId;
    private View mTvNoDataView;
    private TextView tvReload;
    private LinearLayout rootNoNet;

    public TemplateFragment() {
    }

    /**
     * Create template fragment.
     * 创建模板片段
     *
     * @param categoryId the category id
     * @return the template fragment
     */
    public static TemplateFragment create(int categoryId) {
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGORY_ID, categoryId);
        TemplateFragment template = new TemplateFragment();
        template.setArguments(bundle);
        return template;
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
                    mPresenter.getTemplateList(1, mCategoryId + "");
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), TemplatePreviewActivity.class);
                    intent.putExtra(DATA_TEMPLATE, mAdapter.getData().get(position));
                    getActivity().startActivity(intent);
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mPresenter != null && !mPresenter.getMoreTemplate(mCategoryId + "")) {
                    mAdapter.loadMoreEnd();
                }
            }
        }, mRvTemplateList);

        tvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null && !TemplatePreviewActivity.IS_DEBUG) {
                    mPresenter.getTemplateList(1, mCategoryId + "");
                }
            }
        });
    }

    /**
     * Init data.
     */
    @Override
    protected void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategoryId = arguments.getInt(CATEGORY_ID);
        }
        mAdapter.setHasStableIds(true);
        mRvTemplateList.setAdapter(mAdapter);
        if (TemplatePreviewActivity.IS_DEBUG) {
            //检查安装字体
            String fontPath = PathUtils.getTemplateDir() + "/font";
            FileUtils.createOrExistsDir(fontPath);
            File fontFileParent = new File(fontPath);
            File[] fileList = fontFileParent.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (File listFile : fileList) {
                    String fontPathLocal = listFile.getAbsolutePath();
                    NvsStreamingContext.getInstance().registerFontByFilePath(fontPathLocal);
                }
            }

            FileUtils.createOrExistsDir(PathUtils.getTemplateDir() + "/test");
            List<File> files = FileUtils.listFilesInDir(PathUtils.getTemplateDir() + "/test");
            if (files != null && files.size() > 0) {
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    if (file != null) {
                        Template template = new Template();
                        template.setSupportedAspectRatio(1663);
                        template.setDefaultAspectRatio(4);
                        template.setDisplayName("模板" + i);
                        template.setUseNum(i * 12);
                        template.setDescription("白衣依山尽，黄河入海流");
                        template.setCoverUrl("/android_asset/filter/0FBCC8A1-C16E-4FEB-BBDE-D04B91D98A40.png");
                        template.setPackageUrl(file.getAbsolutePath());
                        mAdapter.addData(template);
                    }
                }
            }
        } else {
            mPresenter.getTemplateList(1, mCategoryId + "");
        }
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
            mAdapter.setNewData(templateList);
            if (templateList.size() < TemplatePresenter.PAGE_NUM) {
                mAdapter.loadMoreEnd(true);
            }
        } else {
            setEmptyView();
        }
    }

    private void setEmptyView() {
        if (TemplatePreviewActivity.IS_DEBUG) {
            return;
        }
        if (mAdapter == null || mAdapter.getData() == null || mAdapter.getData().size() == 0) {
            if (rootNoNet != null) {
                rootNoNet.setVisibility(View.INVISIBLE);
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
