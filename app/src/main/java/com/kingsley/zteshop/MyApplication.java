package com.kingsley.zteshop;

import android.app.Application;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.UserLocalData;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * 单例模式
 * 软件运行时创建
 * 存储公共信息
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    private User user;

    public Intent intent;

    public static MyApplication getInstance() {
        if (mInstance == null)
            mInstance = new MyApplication();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fresco.initialize(this);
        //初始化BmobSDK
        Bmob.initialize(this, "0dd68acabbc63f4e71644df10a75cf3a");
    }

    /**
     * 当前用户
     *
     * @return
     */
    public User getCurrentUser() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    /**
     * 清空用户信息和token信息
     */
    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
        User user = BmobUser.getCurrentUser(User.class);
        BmobUser.logOut();
    }

    /**
     * 保存用户信息和token信息到本地
     * @param user
     * @param token
     */
    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    /**
     * 保存登录意图
     */
    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * 获取登录意图
     * @return
     */
    public Intent getIntent() {
        return this.intent;
    }

}