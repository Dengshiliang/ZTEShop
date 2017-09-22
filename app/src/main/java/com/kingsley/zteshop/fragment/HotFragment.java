package com.kingsley.zteshop.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.BaseAdapter;
import com.kingsley.zteshop.adapter.HWAdapter;
import com.kingsley.zteshop.bean.Page;
import com.kingsley.zteshop.bean.Ware;
import com.kingsley.zteshop.net.ServiceGenerator;
import com.kingsley.zteshop.net.SubscriberCallBack;
import com.kingsley.zteshop.utils.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HotFragment extends BaseFragment {

    private HWAdapter mAdapter;

    @ViewInject(R.id.recyclerview_hot)
    private RecyclerView mRecycleView_hot;

    @ViewInject(R.id.refreshlayout_hot)
    private MaterialRefreshLayout mRefreshLayout;

    private List<Ware> datas = new ArrayList<>();

    private int curPage = 1;
    private int pageSize = 10;
    private int totalCount;
    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;

    @Override
    public void setToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot;
    }

    @Override
    public void init() {

        //获取数据
        getData();
        //刷新界面
        initRefreshLayout();

    }

    /**
     * 刷新界面
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
                super.onRefreshLoadMore(materialRefreshLayout);
                if (curPage * pageSize < totalCount) {
                    loadMoreData();
                } else {
                    ToastUtils.show(getActivity(), "没有更多数据...");
                    mRefreshLayout.finishRefreshLoadMore();
                    mRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        getData();
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        curPage = ++curPage;
        state = STATE_MORE;
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {

        ServiceGenerator.getRetrofit(getActivity())
                .getHotWares(curPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<Page<Ware>>(getActivity(), true) {
                    @Override
                    public void onSuccess(Page<Ware> result) {
                        datas = result.getList();
                        curPage = result.getCurrentPage();
                        pageSize = result.getPageSize();
                        totalCount = result.getTotalCount();
                        showData();
                    }
                });

    }

    /**
     * 展示数据
     */
    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                mAdapter = new HWAdapter(getContext(), datas);
                mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Ware wares = mAdapter.getItem(position);
                        mAdapter.showDetail(wares);
                    }
                });
                mRecycleView_hot.setAdapter(mAdapter);
                mRecycleView_hot.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecycleView_hot.setItemAnimator(new DefaultItemAnimator());
                break;
            case STATE_MORE:
                mAdapter.addData(mAdapter.getDatas().size(), datas);
                mRecycleView_hot.scrollToPosition(mAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                mAdapter.clearData();
                mAdapter.addData(datas);
                mRecycleView_hot.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
        }
    }

}
