package com.kingsley.zteshop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.WareOrderAdapter;
import com.kingsley.zteshop.adapter.layoutmanager.FullyLinearLayoutManager;
import com.kingsley.zteshop.bean.Address;
import com.kingsley.zteshop.bean.Order;
import com.kingsley.zteshop.bean.ShoppingCart;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OrderActivity extends BasicActivity implements View.OnClickListener {
    @ViewInject(R.id.tv_name)
    private TextView mTvName;

    @ViewInject(R.id.tv_addr)
    private TextView mTvAddr;

    @ViewInject(R.id.img_add)
    private ImageView mImgAdd;

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.tv_order)
    private TextView mTvOrderList;

    @ViewInject(R.id.tv_total)
    private TextView mTvTotal;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnOrder;

    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mRLAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mRLWechat;

    @ViewInject(R.id.rl_addr)
    private RelativeLayout mRLAddr;

    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_wechat)
    private RadioButton mRbWechat;

    private int SIGN;

    private float amount;

    private WareOrderAdapter wareOrderAdapter;

    private HashMap<String, RadioButton> channels = new HashMap<>(2);

    private String payChannel = CHANNEL_ALIPAY;//默认途径为支付宝

    private Context context;

    private String orderName;
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";


    @Override
    public int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void init() {

        showData();
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, LocationActivity.class);
                //实现地址选择
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });
        initAddress();
        initPayChannels();
        context = getApplicationContext();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("订单支付");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }

    /**
     * 显示订单数据
     */
    private void showData() {

        SIGN = getIntent().getIntExtra("sign", -1);
        /**
         * 购物车商品数据
         */
        if (SIGN == Constants.CART) {
            List<ShoppingCart> carts = (List<ShoppingCart>) getIntent().getSerializableExtra("carts");
            System.out.println("showData---" + carts.size());
            orderName = carts.get(0).getName();
            wareOrderAdapter = new WareOrderAdapter(this, carts);
            FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
            layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(wareOrderAdapter);
            /**
             * 我的订单再次购买点击商品显示
             * TODO
             */
        } else if (SIGN == Constants.ORDER) {

        }
    }

    // 获取地址
    private void initAddress() {

        BmobUser user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Address> query = new BmobQuery<Address>();
        // 添加查询条件
        query.addWhereEqualTo("userId", user);
        query.findObjects(new FindListener<Address>() {
            @Override
            public void done(List<Address> list, BmobException e) {
                if (e == null) {
                    showAddress(list);
                } else {
                    ToastUtils.show(OrderActivity.this, "查询失败" + e.getErrorCode());
                }
            }
        });
    }

    private void initPayChannels() {
        //保存RadioButton
        channels.put(CHANNEL_ALIPAY, mRbAlipay);
        channels.put(CHANNEL_WECHAT, mRbWechat);

        mRLAlipay.setOnClickListener(this);
        mRLWechat.setOnClickListener(this);

        if (SIGN == Constants.CART) {
            amount = wareOrderAdapter.getTotalPrice();
        } else if (SIGN == Constants.ORDER) {
            //TODO 再次购买
        }
        mTvTotal.setText("应付款：￥" + amount);
    }

    /**
     * 选择支付渠道以及RadioButton互斥功能
     *
     * @param payChannel
     */
    private void selectPayChannel(String payChannel) {

        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {
            this.payChannel = payChannel;
            System.out.println("payChannel=" + payChannel);
            //获取的RadioButton
            RadioButton rb = entry.getValue();
            //如果当前RadioButton被点击
            if (entry.getKey().equals(payChannel)) {
                //判断是否被选中
                boolean isChecked = rb.isChecked();
                //设置为互斥操作
                rb.setChecked(!isChecked);
            } else {
                //其他的都改为未选中
                rb.setChecked(false);
            }
        }
    }

    /**
     * 支付渠道点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        selectPayChannel(v.getTag().toString());
        System.out.println("tag=" + v.getTag().toString());
    }

    /**
     * 立即支付
     *
     * @param view
     */
    public void postNewOrder(View view) {
        if (mRbAlipay.isChecked()) {
            // 支付宝支付
            ToastUtils.show(OrderActivity.this, "去支付宝支付啦");
            BP.pay(orderName, "商品详情", 0.02, BP.PayType_Alipay, new PListener() {
                @Override
                public void orderId(String s) {

                }

                @Override
                public void succeed() {

                }

                @Override
                public void fail(int i, String s) {
                    ToastUtils.show(OrderActivity.this, "fail" + s);
                    Log.i("test", s);
                }
            });
        } else {
            // 微信支付
            ToastUtils.show(OrderActivity.this, "去微信支付啦");
            BP.pay("商品", "商品介绍", 1.02, BP.PayType_Wechat, new PListener() {
                @Override
                public void orderId(String s) {

                }

                @Override
                public void succeed() {

                }

                @Override
                public void fail(int i, String s) {
                    ToastUtils.show(OrderActivity.this, "fail" + i);
                    // 当code为-2,意味着用户中断了操作
                    // code为-3意味着没有安装BmobPlugin插件
                    if (i == -3) {
                        new AlertDialog.Builder(OrderActivity.this)
                                .setMessage(
                                        "监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)还是用支付宝支付")
                                .setPositiveButton("安装",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                installBmobPayPlugin("BmobPayPlugin.apk");
                                            }
                                        })
                                .setNegativeButton("支付宝支付",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                //payByAli();
                                            }
                                        }).create().show();
                    } else {
                        Toast.makeText(OrderActivity.this, "支付中断!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    /**
     * 显示默认地址
     *
     * @param addresses
     */
    private void showAddress(List<Address> addresses) {
        /**
         * 购物车页面传递的数据显示地址
         */
        if (SIGN == Constants.CART) {
            for (Address address : addresses) {
                if (address.getIsDefault()) {
                    mTvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                    mTvAddr.setText(address.getAddr());
                }
            }
        } else if (SIGN == Constants.ORDER) {
            // TODO 再次购物
        }

    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
