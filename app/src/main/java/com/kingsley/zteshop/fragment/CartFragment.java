package com.kingsley.zteshop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.CartAdapter;
import com.kingsley.zteshop.adapter.decoration.DividerItemDecortion;
import com.kingsley.zteshop.bean.ShoppingCart;
import com.kingsley.zteshop.utils.CartProvider;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.CircleAddAndSubView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 购物车
 * 添加商品到购物车，CartProvider获取购物车数据，并显示总价，选中的商品可进行购买跳到结算页面
 * 购物车为空则不能购买
 */

public class CartFragment extends BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.recyclerview_cart)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.checkbox_all)
    private CheckBox mCheckBox;

    @ViewInject(R.id.tv_total)
    private TextView mTvCount;

    @ViewInject(R.id.btn_order)
    private Button mBtnOrder;

    @ViewInject(R.id.btn_del)
    private Button mBtnDelete;

    private CartProvider mCartProvider;
    private CartAdapter mAdapter;

    @Override
    public void setToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cart;
    }

    @Override
    public void init() {

        mCartProvider = CartProvider.getInstance(getContext());
        showData();

    }

    /**
     * 显示购物车数据
     */
    private void showData() {

        List<ShoppingCart> carts = mCartProvider.getAll();
        mAdapter = new CartAdapter(getContext() , carts , mCheckBox , mTvCount);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));

    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        mAdapter.clearData();
        List<ShoppingCart> carts = mCartProvider.getAll();
        mAdapter.addData(carts);

        // 显示总价格
        mAdapter.showTotalPrice();

    }

    @Override
    public void onClick(View view) {

    }
}
