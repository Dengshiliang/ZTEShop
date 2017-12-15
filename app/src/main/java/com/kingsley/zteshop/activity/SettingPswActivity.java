package com.kingsley.zteshop.activity;

import android.view.View;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SettingPswActivity extends BasicActivity {

    private String et_password, et_repassword;

    @ViewInject(R.id.change_pwd)
    private ClearEditText change_pwd;

    @ViewInject(R.id.change_repwd)
    private ClearEditText change_repwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_psw;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("修改密码");
        getToolbar().setRightButtonText("完成");
        getToolbar().setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 完成，提交数据
                 */
                commit();
                finish();
            }
        });
    }

    /**
     * 将修改的数据 提交到Bmob上
     */
    private void commit() {

        et_password = change_pwd.getText().toString().trim();
        et_repassword = change_repwd.getText().toString().trim();

        User user = BmobUser.getCurrentUser(User.class);
        user.updateCurrentUserPassword(et_password, et_repassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.show(SettingPswActivity.this, "密码修改成功，可以用新密码进行登录啦");
                } else {
                    ToastUtils.show(SettingPswActivity.this, "失败:" + e.getMessage());
                }
            }
        });

    }
}
