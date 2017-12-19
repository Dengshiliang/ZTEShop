package com.kingsley.zteshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.adapter.BaseAdapter;
import com.kingsley.zteshop.adapter.FavoriteAdapter;
import com.kingsley.zteshop.adapter.decoration.CardViewtemDecortion;
import com.kingsley.zteshop.bean.Favorite;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MyFarvoriteActivity extends BasicActivity {

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecyclerview;

    private FavoriteAdapter mAdapter;
    private CustomDialog mDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_farvorite;
    }

    @Override
    public void init() {
        initFavorite();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("我的收藏");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }

    private void initFavorite() {
        /**
         * 初始化 我的收藏
         */
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Favorite> query = new BmobQuery<Favorite>();
        // 添加查询条件
        query.addWhereEqualTo("userId", user);
        query.findObjects(new FindListener<Favorite>() {
            @Override
            public void done(List<Favorite> list, BmobException e) {
                if (e == null) {
                    showFavorite(list);
                } else {
                    ToastUtils.show(MyFarvoriteActivity.this, "查询失败" + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 显示删除提示对话框
     *
     * @param favorite
     */
    private void showDialog(final Favorite favorite) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该商品吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteFavorite(favorite);
                initFavorite();

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
     * 取消收藏
     *
     * @param favorite
     */
    private void deleteFavorite(Favorite favorite) {

        /**
         * 删除喜欢的商品
         */
        //删除地址
        favorite.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.show(MyFarvoriteActivity.this, "删除成功");
                } else {
                    ToastUtils.show(MyFarvoriteActivity.this, "删除失败");
                }
            }
        });

    }

    /**
     * 展示喜欢
     *
     * @param favorites
     */
    private void showFavorite(final List<Favorite> favorites) {

        if (mAdapter == null) {
            mAdapter = new FavoriteAdapter(this, favorites, new FavoriteAdapter.FavoriteLisneter() {
                @Override
                public void onClickDelete(Favorite favorite) {
                    showDialog(favorite);
                }
            });
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    // 取消收藏
                    showDialog(favorites.get(position));
                }
            });
        } else {
            mAdapter.refreshData(favorites);
            mRecyclerview.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initFavorite();
    }
}
