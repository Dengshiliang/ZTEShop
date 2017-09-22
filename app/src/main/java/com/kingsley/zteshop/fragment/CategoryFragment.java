package com.kingsley.zteshop.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.BaseAdapter;
import com.kingsley.zteshop.adapter.CategoryAdapter;
import com.kingsley.zteshop.adapter.CategoryWaresAdapter;
import com.kingsley.zteshop.adapter.decoration.DividerItemDecortion;
import com.kingsley.zteshop.bean.Category;
import com.kingsley.zteshop.bean.Page;
import com.kingsley.zteshop.bean.Ware;
import com.kingsley.zteshop.net.ServiceGenerator;
import com.kingsley.zteshop.net.SubscriberCallBack;
import com.kingsley.zteshop.utils.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CategoryFragment extends BaseFragment {

    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecycleViewText;

    @ViewInject(R.id.refreshlayout_category)
    private MaterialRefreshLayout mRefreshLayout;

    @ViewInject(R.id.recyclerview_category_wares)
    private RecyclerView mRecyclerViewWares;

    //左边导航适配器
    private CategoryAdapter mCategoryAdapter;

    //wares数据显示适配器
    private CategoryWaresAdapter mWaresAdapter;

    private List<Ware> mDatas;

    private long category_id = 0;//左边导航id

    private int curPage = 1;

    private int totalPage = 1;

    private int pageSize = 10;

    private int totalCount = 28;

    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;

    @Override
    public void setToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void init() {

        requestCategoryData();
        initRefreshLayout();

    }

    /**
     * 列表数据刷新
     */
    private void initRefreshLayout() {

        mRefreshLayout.setLoadMore(true);

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount)
                    loadMoreData();
                else {
                    ToastUtils.show(getContext(), "已经加载完毕了...");
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });


    }

    private void loadMoreData() {
        curPage = ++curPage;
        state = STATE_MORE;
        requestWares(category_id);
    }

    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        requestWares(category_id);
    }

    /**
     * 请求wares数据，并传入列表id
     *
     * @param categoryId 传入的点击的列表id显示该id对应商品
     */
    private void requestWares(long categoryId) {

        ServiceGenerator.getRetrofit(getActivity())
                .getWaresList(categoryId, curPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<Page<Ware>>(getActivity(), true) {
                    @Override
                    public void onSuccess(Page<Ware> result) {
                        mDatas = result.getList();

                        curPage = result.getCurrentPage();

                        totalPage = result.getTotalPage();

                        totalCount = result.getTotalCount();

                        showCategoryWaresData();
                    }

                });
    }

    /**
     * 显示wares数据
     */
    private void showCategoryWaresData() {

        switch (state) {
            case STATE_NORMAL:
                if (mWaresAdapter == null) {
                    mWaresAdapter = new CategoryWaresAdapter(getContext(), mDatas);
                    mWaresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            mWaresAdapter.showDetail(mWaresAdapter.getItem(position));
                        }
                    });
                    mRecyclerViewWares.setAdapter(mWaresAdapter);
                    mRecyclerViewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerViewWares.setItemAnimator(new DefaultItemAnimator());
                } else {
                    mWaresAdapter.clearData();
                    mWaresAdapter.addData(mDatas);
                }
                break;
            case STATE_MORE:
                mWaresAdapter.addData(mWaresAdapter.getDatas().size(), mDatas);
                mRecyclerViewWares.scrollToPosition(mWaresAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                mWaresAdapter.clearData();
                mWaresAdapter.addData(mDatas);
                mRecyclerViewWares.setAdapter(mWaresAdapter);
                mRecyclerViewWares.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
        }
    }


    /**
     * 请求左边导航菜单数据
     */
    private void requestCategoryData() {

        ServiceGenerator.getRetrofit(getActivity())
                .getCategory("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<Category>>(getActivity(), true) {
                    @Override
                    public void onSuccess(List<Category> result) {

                        showCategoryData(result);
                        if (result != null && result.size() > 0)
                            category_id = result.get(0).getId();

                        requestWares(category_id);
                    }
                });

    }

    /**
     * 左边导航
     *
     * @param categories 导航列表
     */
    private void showCategoryData(final List<Category> categories) {

        mCategoryAdapter = new CategoryAdapter(getContext(), categories);
        mRecycleViewText.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {

                //获取列表数据
                Category category = mCategoryAdapter.getItem(position);

                //获取列表数据id
                category_id = category.getId();
                curPage = 1;
                state = STATE_NORMAL;
                requestWares(category_id);

            }
        });

        mRecycleViewText.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleViewText.setItemAnimator(new DefaultItemAnimator());
        mRecycleViewText.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));

    }
}



