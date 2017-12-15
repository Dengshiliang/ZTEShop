package com.kingsley.zteshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.widget.CnToolbar;
import com.lidroid.xutils.ViewUtils;


/**
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

    private void initToolbar() {
        if (getToolbar() != null) {
            setToolbar();
        }
    }

    public abstract void setToolbar();

    public CnToolbar getToolbar() {
        return (CnToolbar) mRootView.findViewById(R.id.toolbar_search_view);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getLayoutId() != 0) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewUtils.inject(this, mRootView);

        initToolbar();

        init();

        return mRootView;
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    public abstract void init();


}
