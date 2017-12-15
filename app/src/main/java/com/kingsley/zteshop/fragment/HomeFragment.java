package com.kingsley.zteshop.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.daimajia.slider.library.SliderLayout;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.activity.WareListActivity;
import com.kingsley.zteshop.adapter.HomeCampaignAdapter;
import com.kingsley.zteshop.bean.Banner;
import com.kingsley.zteshop.bean.Campaign;
import com.kingsley.zteshop.bean.HomeCampaign;
import com.kingsley.zteshop.net.ServiceGenerator;
import com.kingsley.zteshop.net.SubscriberCallBack;
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 主页重构
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HOMEFRAGMENT";

    @ViewInject(R.id.banner)
    private SliderLayout banner;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerview;

    private HomeCampaignAdapter mAdapter;

    @Override
    public void setToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void init() {

        initAdapter();

        Log.d("TEST","加载图片");

        ServiceGenerator.getRetrofit(getActivity())
                .getBanner(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<Banner>>(getActivity(), false) {
                    @Override
                    public void onSuccess(List<Banner> result) {
                        mAdapter.setBanners(result);
                    }
                });

        ServiceGenerator.getRetrofit(getActivity())
                .getHome("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<HomeCampaign>>(getActivity(), true) {
                    @Override
                    public void onSuccess(List<HomeCampaign> result) {
                        mAdapter.setDatas(result);
                    }
                });

        Log.d("TEST","加载图片完成");
    }

    private void initAdapter() {
        mAdapter = new HomeCampaignAdapter(getContext());

        mAdapter.setHomeCatgoryClickListener(new HomeCampaignAdapter.OnHomeCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {

                /**
                 * 实现商品详情
                 */
                Intent intent = new Intent(getActivity(), WareListActivity.class);
                intent.putExtra(Constants.CAMPAIGN_ID,campaign.getId());
                startActivity(intent);

            }
        });

        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        banner.stopAutoCycle();
    }
}
