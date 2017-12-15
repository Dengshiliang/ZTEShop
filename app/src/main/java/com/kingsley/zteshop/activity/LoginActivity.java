package com.kingsley.zteshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;


public class LoginActivity extends BasicActivity {

    @ViewInject(R.id.et_phone)
    private ClearEditText mClearEtPhone;

    @ViewInject(R.id.et_pwd)
    private ClearEditText mClearEtPwd;

    @ViewInject(R.id.btn_login)
    private Button mBtnLogin;

    @ViewInject(R.id.tv_register)
    private TextView mTvReg;

    @ViewInject(R.id.tv_forget_pwd)
    private TextView mTvForget;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户登录");
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {

        String phone = mClearEtPhone.getText().toString().trim();
        String pwd = mClearEtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请输入登录密码");
            return;
        }

        /**
         *  TODO
         *  设计进度条样式
         */
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        /**
         * 实现登录注册功能
         */

        BmobUser.loginByAccount(phone, pwd, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                //取消加载进度条
                progress.dismiss();
                //判断是否登录成功
                if (user != null) {
                    ToastUtils.show(LoginActivity.this, "用户登录成功");
                    //跳转到主页面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.show(LoginActivity.this, "登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage());
                }
            }
        });

    }

    /**
     * 跳转到注册页面
     *
     * @param v
     */
    @OnClick(R.id.tv_register)
    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
