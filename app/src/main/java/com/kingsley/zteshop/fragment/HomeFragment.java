package com.kingsley.zteshop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.DividerItemDecortion;
import com.kingsley.zteshop.adapter.HomeCatgoryAdapter;
import com.kingsley.zteshop.bean.HomeCategory;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private MZBannerView banner;

    private static final String TAG = "HOMEFRAGMENT";

    public static final int[] Banner = new int[]{R.mipmap.guide_one, R.mipmap.guide_two, R.mipmap.guide_three};

    private RecyclerView mRecyclerview;

    private HomeCatgoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        banner = (MZBannerView) view.findViewById(R.id.banner);

        initBanner();

        initRecyclerView(view);

        return view;

    }

    //  商品页面布局
    private void initRecyclerView(View view) {

        mRecyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);

        List<HomeCategory> datas = new ArrayList<>(15);

        HomeCategory category = new HomeCategory("热门活动",R.drawable.img_big_1,R.drawable.img_1_small1,R.drawable.img_1_small2);
        datas.add(category);
        category = new HomeCategory("有利可图",R.drawable.img_big_4,R.drawable.img_4_small1,R.drawable.img_4_small2);
        datas.add(category);
        category = new HomeCategory("品牌街",R.drawable.img_big_2,R.drawable.img_2_small1,R.drawable.img_2_small2);
        datas.add(category);
        category = new HomeCategory("金融街 包赚翻",R.drawable.img_big_1,R.drawable.img_3_small1,R.drawable.imag_3_small2);
        datas.add(category);
        category = new HomeCategory("超值购",R.drawable.img_big_0,R.drawable.img_0_small1,R.drawable.img_0_small2);
        datas.add(category);


        mAdapter = new HomeCatgoryAdapter(datas);

        mRecyclerview.setAdapter(mAdapter);

        mRecyclerview.addItemDecoration(new DividerItemDecortion());

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));


    }

    // 导航栏
    private void initBanner(){

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < Banner.length; i++) {
            list.add(Banner[i]);
        }

        banner.setPages(list, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
    }


    public static class BannerViewHolder implements MZViewHolder<Integer> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            mImageView = (ImageView) view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            mImageView.setImageResource(data);
        }
    }

}
