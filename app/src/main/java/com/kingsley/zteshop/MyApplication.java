package com.kingsley.zteshop;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 单例模式
 * 软件运行时创建
 * 存储公共信息
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

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

    }

}