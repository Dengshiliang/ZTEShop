package com.kingsley.zteshop.activity;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WareListActivity extends BasicActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;

    @ViewInject(R.id.tv_summary)
    private TextView mTvSummary;

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecycleViewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    private HWAdapter waresAdapter;

    private static final int TAG_DEFAULT = 0;
    private static final int TAG_SALE = 1;
    private static final int TAG_PRICE = 2;

    private static final int ACTION_LIST = 1;
    private static final int ACTION_GRID = 2;

    private long campaignId = 0;
    private int orderBy = 0;

    private int curPage = 1;
    private int pageSize = 10;
    private int totalCount;

    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private List<Ware> datas = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_ware_list;
    }

    @Override
    public void init() {

        campaignId = getIntent().getLongExtra(Constants.CAMPAIGN_ID, 0);

        //初始化Tab
        initTab();

        initRefreshLayout();

        //获取数据
        getData();

    }

    private void getData() {

        ServiceGenerator.getRetrofit(this)
                .campaignList(campaignId, orderBy, curPage, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<Page<Ware>>(this, true) {

                    @Override
                    public void onSuccess(Page<Ware> result) {
                        curPage = result.getCurrentPage();
                        pageSize = result.getPageSize();
                        totalCount = result.getTotalCount();
                        datas = result.getList();
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
                mTvSummary.setText("共有" + totalCount + "件商品");
                waresAdapter = new HWAdapter(this, datas);

                waresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        waresAdapter.showDetail(waresAdapter.getItem(position));
                    }
                });

                mRecycleViewWares.setAdapter(waresAdapter);
                mRecycleViewWares.setLayoutManager(new LinearLayoutManager(this));
                mRecycleViewWares.setItemAnimator(new DefaultItemAnimator());
                break;
            case STATE_MORE:
                waresAdapter.loadMore(datas);
                mRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                waresAdapter.refreshData(datas);
                mRecycleViewWares.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
        }

    }

    private void initRefreshLayout() {

        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount) {
                    loadMoreData();
                } else {
                    ToastUtils.show(WareListActivity.this, "没有更多数据...");
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
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
     * 刷新数据
     */
    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        getData();
    }

    /**
     * 初始化Tab
     */
    private void initTab() {

        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText(R.string.defaults);
        tab.setTag(TAG_DEFAULT);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText(R.string.sales);
        tab.setTag(TAG_SALE);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText(R.string.price);
        tab.setTag(TAG_PRICE);
        mTabLayout.addTab(tab);

        mTabLayout.setOnTabSelectedListener(this);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        orderBy = (int) tab.getTag();
        curPage = 1;
        state = STATE_NORMAL;
        getData();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle(R.string.wares_list);
        getToolbar().setRightImgButtonIcon(R.drawable.grid);
        getToolbar().getRightButton().setTag(ACTION_LIST);
        getToolbar().setRightButtonOnClickListener(this);
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().getRightButton();
    }

    @Override
    public void onClick(View view) {
        int action = (int) view.getTag();

        if (ACTION_LIST == action) {
            //更改图标，布局，tag
            getToolbar().setRightImgButtonIcon(R.drawable.list);
            getToolbar().getRightButton().setTag(ACTION_GRID);
            waresAdapter.reSetLayout(R.layout.template_grid_wares);
            mRecycleViewWares.setLayoutManager(new GridLayoutManager(this, 2));
            mRecycleViewWares.setAdapter(waresAdapter);
        } else if (ACTION_GRID == action) {
            getToolbar().setRightImgButtonIcon(R.drawable.grid);
            getToolbar().getRightButton().setTag(ACTION_LIST);
            waresAdapter.reSetLayout(R.layout.template_hot_wares);
            mRecycleViewWares.setLayoutManager(new LinearLayoutManager(this));
            mRecycleViewWares.setAdapter(waresAdapter);
        }
    }

}
