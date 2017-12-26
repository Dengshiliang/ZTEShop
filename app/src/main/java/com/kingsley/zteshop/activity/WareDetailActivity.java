package com.kingsley.zteshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Favorite;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.bean.Ware;
import com.kingsley.zteshop.utils.CartProvider;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

/**
 * 商品详情
 */
public class WareDetailActivity extends BasicActivity {

    @ViewInject(R.id.webView)
    private WebView mWebView;

    private WebAppInterface mAppInterface;

    private CartProvider mCartProvider;

    private Ware mWares;

    private SpotsDialog mDialog;

    private WebClient mWebClient;


    /**
     * 初始化WebView
     * 1.设置允许执⾏JS脚本：
     * webSettings.setJavaScriptEnabled(true);
     * 2.添加通信接口
     * webView.addJavascriptInterface(Interface,”InterfaceName”)
     * 3.JS调⽤Android
     * InterfaceName.MethodName
     * 4.Android调⽤JS
     * webView.loadUrl("javascript:functionName()");
     */
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        //1、设置允许执行Js脚本
        settings.setJavaScriptEnabled(true);
        //默认为true，无法加载页面图片
        settings.setBlockNetworkImage(false);
        //设置允许缓存
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Constants.API.WARES_DETAILS);

        System.out.println(Constants.API.WARES_DETAILS);

        mAppInterface = new WebAppInterface(this);
        mWebClient = new WebClient();

        //2.添加通信接口 name和web页面名称一致
        mWebView.addJavascriptInterface(mAppInterface, "appInterface");

        mWebView.setWebViewClient(mWebClient);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_ware_detail;
    }

    @Override
    public void init() {
        Serializable serializable = getIntent().getSerializableExtra(Constants.WARES);
        if (serializable == null)
            this.finish();

        mDialog = new SpotsDialog(this, "loading...");
        mDialog.show();

        mWares = (Ware) serializable;
        mCartProvider = CartProvider.getInstance(this);

        initWebView();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle(R.string.wares_details);
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightButtonText(getString(R.string.share));
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }


    /**
     * 显示分享界面
     * 设置参数
     */
    private void showShare() {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("中兴商城");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("www.zte.com.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mWares.getName().toString());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(mWares.getImgUrl());//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("www.zte.com.cn");
        oks.setComment(mWares.getName());
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl("www.zte.com.cn");
        // 启动分享GUI
        oks.show(this);

    }


    /**
     * 页面加载完之后才调用方法进行显示数据
     * 需要实现一个监听判断页面是否加载完
     */
    class WebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
            //显示详情
            mAppInterface.showDetail();
        }

    }


    /**
     * 定义接口进行通讯
     */
    class WebAppInterface {

        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        /**
         * 方法名和js代码中必须一直
         * 显示详情页
         */
        @JavascriptInterface
        private void showDetail() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //调用js代码
                    mWebView.loadUrl("javascript:showDetail(" + mWares.getId() + ")");
                }
            });
        }

        /**
         * 添加到购物车
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void buy(long id) {
            mCartProvider.put(mWares);
            ToastUtils.show(context, R.string.has_add_cart);
        }

        /**
         * 添加到收藏夹
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void addToCart(long id) {
            addToFavorite();
        }

    }

    /**
     * 添加到收藏夹
     */
    private void addToFavorite() {

        final User user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {

            BmobQuery<Favorite> query = new BmobQuery<Favorite>();
            query.addWhereEqualTo("userId", user);
            query.addWhereEqualTo("id", mWares.getId());
            query.findObjects(new FindListener<Favorite>() {
                @Override
                public void done(List<Favorite> list, BmobException e) {
                    if (e == null) {
                        ToastUtils.show(WareDetailActivity.this, "已经收藏过了");
                    } else {
                        // 插入数据
                        Favorite favorite = new Favorite();
                        favorite.setUserId(user);
                        favorite.setId(mWares.getId());
                        favorite.setName(mWares.getName());
                        favorite.setPrice(mWares.getPrice());
                        favorite.setImgUrl(mWares.getImgUrl());
                        favorite.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    ToastUtils.show(WareDetailActivity.this, "已经加入收藏");
                                }
                            }
                        });
                    }
                }
            });

        }
    }
}
