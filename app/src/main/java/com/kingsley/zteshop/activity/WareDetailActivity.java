package com.kingsley.zteshop.activity;

import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.activity.BasicActivity;
import com.kingsley.zteshop.bean.Ware;
import com.kingsley.zteshop.utils.CartProvider;
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;

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

            }
        });
    }


    /**
     * 显示分享界面
     */
    private void showShare() {


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



        }

        /**
         * 添加到收藏夹
         * @param id 商品id
         */
        @JavascriptInterface
        public void addToCart(long id) {

        }

    }

    /**
     * 添加到收藏夹
     */
    private void addToFavorite() {

    }
}
