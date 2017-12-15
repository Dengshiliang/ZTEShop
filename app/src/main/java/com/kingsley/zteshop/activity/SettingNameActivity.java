package com.kingsley.zteshop.activity;

import android.view.View;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SettingNameActivity extends BasicActivity {

    @ViewInject(R.id.et_name)
    private ClearEditText et_name;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_name;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {

        getToolbar().setTitle("设置昵称");
        getToolbar().setRightButtonText("完成");
        getToolbar().setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit();
                finish();
            }
        });

    }

    /**
     * 提交用户昵称
     */
    private void commit() {

        String names = et_name.getText().toString().trim();

        User user = BmobUser.getCurrentUser(User.class);
        user.setNames(names);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {

                } else {

                }
            }
        });

    }
}
