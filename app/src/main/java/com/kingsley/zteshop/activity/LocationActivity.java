package com.kingsley.zteshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.AddressAdapter;
import com.kingsley.zteshop.bean.Address;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.Constants;
import com.kingsley.zteshop.widget.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class LocationActivity extends BasicActivity {

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview;

    private AddressAdapter mAdapter;
    private CustomDialog mDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_location;
    }

    @Override
    public void init() {
        initAddress();
    }

    @Override
    public void setToolbar() {

        getToolbar().setTitle(R.string.tv_loc);
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightImgButtonIcon(R.drawable.icon_add_w);
        getToolbar().setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加地址
                toAddActivity();
            }
        });

    }

    /**
     * 跳转到添加地址页面
     * 点击右上角添加按钮，传入TAG_SAVE,更改添加地址页面toolbar显示
     */
    private void toAddActivity() {
        Intent intent = new Intent(this, LocationAddActivity.class);
        intent.putExtra("tag", Constants.TAG_SAVE);
        startActivityForResult(intent, Constants.ADDRESS_ADD);
    }

    /**
     * 初始化地址页面
     */
    private void initAddress() {

        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Address> query = new BmobQuery<Address>();
        // 添加查询条件
        query.addWhereEqualTo("userId", user);
        query.findObjects(new FindListener<Address>() {
            @Override
            public void done(List<Address> list, BmobException e) {
                if (e == null) {
                    showAddress(list);
                } else {
                    ToastUtils.show(LocationActivity.this, "查询失败" + e.getErrorCode());
                }
            }
        });

    }

    /**
     * 显示删除提示对话框
     *
     * @param address
     */
    private void showDialog(final Address address) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该地址吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAddress(address);
                initAddress();

                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                    }
                });

        mDialog = builder.create();
        mDialog.show();

    }

    /**
     * 删除地址
     *
     * @param address
     */
    private void deleteAddress(Address address) {

        //删除地址
        address.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.show(LocationActivity.this, "删除成功");
                } else {
                    ToastUtils.show(LocationActivity.this, "删除失败");
                }
            }
        });

    }

    /**
     * 跳转AddressAddActivity页面结果处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initAddress();
    }

    /**
     * 显示地址列表
     *
     * @param addresses
     */
    private void showAddress(final List<Address> addresses) {

        Collections.sort(addresses);
        if (mAdapter == null) {
            mAdapter = new AddressAdapter(this, addresses, new AddressAdapter.AddressLisneter() {
                @Override
                public void setDefault(Address address) {
                    setResult(RESULT_OK);
                    //更改地址
                    updateAddress(address);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onClickEdit(Address address) {
                    editAddress(address);
                }

                @Override
                public void onClickDelete(Address address) {
                    showDialog(address);
                    mDialog.show();
                }
            });
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(LocationActivity.this));
        } else {
            mAdapter.refreshData(addresses);
            mRecyclerview.setAdapter(mAdapter);
        }

    }

    /**
     * 编辑地址
     * 传入TAG_COMPLETE更改AddressAddActivitytoolbar显示
     *
     * @param address
     */
    private void editAddress(Address address) {
        Intent intent = new Intent(this, LocationAddActivity.class);
        intent.putExtra("tag", Constants.TAG_COMPLETE);
        intent.putExtra("addressBean", address);
        startActivityForResult(intent, Constants.ADDRESS_EDIT);
    }

    /**
     * 更新地址
     *
     * @param address
     */
    public void updateAddress(Address address) {
        // 更新地址
        address.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {

                if (e == null){
                    ToastUtils.show(LocationActivity.this, "已修改成功");
                }else {
                    ToastUtils.show(LocationActivity.this, "修改失败");
                }

            }
        });
    }
}
