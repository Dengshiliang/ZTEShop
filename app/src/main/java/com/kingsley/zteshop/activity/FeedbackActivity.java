package com.kingsley.zteshop.activity;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kingsley.zteshop.R;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FeedbackActivity extends BasicActivity {

    @ViewInject(R.id.webview)
    private WebView webview;


    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void init() {
        webview.loadUrl("http://www.zte.com.cn/china/Suggestion");
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("意见反馈");
    }

}
