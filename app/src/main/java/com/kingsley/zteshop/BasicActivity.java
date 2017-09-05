package com.kingsley.zteshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kingsley.zteshop.widget.CnToolbar;
import com.lidroid.xutils.ViewUtils;

/**
 * BaseActivity封装
 */
public abstract class BasicActivity extends AppCompatActivity {

    private void initToolbar() {
        if (getToolbar() != null) {
            setToolbar();
            getToolbar().setLeftButtonOnClickLinster(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public CnToolbar getToolbar() {
        return (CnToolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ViewUtils.inject(this);

        initToolbar();

        init();

    }

    public abstract int getLayoutId();

    public abstract void init();

    public abstract void setToolbar();

}
