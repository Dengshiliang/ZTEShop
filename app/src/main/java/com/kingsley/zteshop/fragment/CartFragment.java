package com.kingsley.zteshop.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kingsley.zteshop.activity.NewOrderActivity;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.CartAdapter;
import com.kingsley.zteshop.adapter.decoration.DividerItemDecortion;
import com.kingsley.zteshop.bean.ShoppingCart;
import com.kingsley.zteshop.utils.CartProvider;
import com.kingsley.zteshop.utils.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;


/**
 * 购物车
 * 添加商品到购物车，CartProvider获取购物车数据，并显示总价，选中的商品可进行购买跳到结算页面
 * 购物车为空则不能购买
 */

public class CartFragment extends BaseFragment implements View.OnClickListener {

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

    private static final int ACTION_EDIT = 1;
    private static final int ACTION_CAMPLATE = 2;

    @Override
    public void setToolbar() {

        getToolbar().hideSearchView();
        getToolbar().setRightButtonText(R.string.edit);
        getToolbar().getRightButton().setOnClickListener(this);
        getToolbar().getRightButton().setTag(ACTION_EDIT);

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
        mAdapter = new CartAdapter(getContext(), carts, mCheckBox, mTvCount);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));

    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        mAdapter.clearData();
        List<ShoppingCart> carts = mCartProvider.getAll();
        mAdapter.addData(carts);

        // 显示总价格
        mAdapter.showTotalPrice();

    }

    @Override
    public void onClick(View view) {
        //编辑
        int action = (int) view.getTag();
        if (ACTION_EDIT == action) {
            showDelControl();

        } else if (ACTION_CAMPLATE == action) {//完成
            hideDelControl();
        }

        if (view.getId() == R.id.btn_order) {
            List<ShoppingCart> carts = mAdapter.getCheckData();
            if (carts.size() != 0 && carts != null) {
                startActivity(new Intent(getActivity(), NewOrderActivity.class));
            } else {
                ToastUtils.show(getContext(), "请选择要购买的商品");
            }
        }
    }

    /**
     * 隐藏删除按钮
     */
    private void hideDelControl() {
        getToolbar().getRightButton().setText("编辑");
        mTvCount.setVisibility(View.VISIBLE);
        mBtnOrder.setVisibility(View.VISIBLE);

        mBtnDelete.setVisibility(View.GONE);
        //设置为编辑
        getToolbar().getRightButton().setTag(ACTION_EDIT);
        mAdapter.checkAll_None(true);
        mCheckBox.setChecked(true);
        mAdapter.showTotalPrice();
    }

    /**
     * 显示删除按钮
     */
    private void showDelControl() {
        getToolbar().getRightButton().setText("完成");
        mTvCount.setVisibility(View.GONE);
        mBtnOrder.setVisibility(View.GONE);
        mBtnDelete.setVisibility(View.VISIBLE);
        //设置为完成
        getToolbar().getRightButton().setTag(ACTION_CAMPLATE);

        mAdapter.checkAll_None(false);
        mCheckBox.setChecked(false);
    }

    @OnClick(R.id.btn_del)
    public void delCart(View v) {
        mAdapter.delCart();
    }

    /**
     * 结算按钮点击事件
     *
     * @param v
     */
    @OnClick(R.id.btn_order)
    public void toOrder(View v) {

        if (mAdapter.getCheckData() != null && mAdapter.getCheckData().size() > 0) {

            /**
             *  跳转到订单页面
             *  TODO
             */

        } else {
            ToastUtils.show(getContext(), "请选择要购买的商品");
        }
    }

}
