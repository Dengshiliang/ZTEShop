package com.kingsley.zteshop.net;

import android.app.Activity;

import dmax.dialog.SpotsDialog;
import rx.Subscriber;

public abstract class SubscriberCallBack<T> extends Subscriber<T> {

    private SpotsDialog mDialog;
    private Activity mContext;

    private boolean isShowDialog = false;

    public SubscriberCallBack(Activity context) {
        mContext = context;
    }

    public SubscriberCallBack(Activity context, boolean isShowDialog) {
        this.mContext = context;
        this.isShowDialog = isShowDialog;

        if (isShowDialog) {
            mDialog = new SpotsDialog(mContext, "拼命加载中");
        }
    }

    public abstract void onSuccess(T result);

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null)
            mDialog.show();
    }

    @Override
    public void onCompleted() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mDialog != null)
            mDialog.dismiss();

    }

    @Override
    public void onNext(T result) {

        onSuccess(result);

    }
}
