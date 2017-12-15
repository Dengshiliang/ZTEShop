package com.kingsley.zteshop.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Register2Activity extends BasicActivity {

    @ViewInject(R.id.tv_tip)
    private TextView mTvTip;

    @ViewInject(R.id.et_code)
    private ClearEditText mEtCode;

    private Button mBtnReSend;
    private String phone;
    private String countryCode;
    private String pwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register2;
    }

    @Override
    public void init() {

        //获取手机号码、密码、验证
        phone = getIntent().getStringExtra("username");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");
        System.out.println("phone=" + phone + ",pwd=" + pwd);

        mBtnReSend = (Button) findViewById(R.id.btn_Send);
        mBtnReSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send();
            }
        });

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户注册(2/2)");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightButtonText("完成");
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 验证短信验证码
                 */
                submitCode();
            }
        });
    }

    /**
     * 提交验证信息
     */
    private void submitCode() {

        String EtCode = mEtCode.getText().toString().trim();

        BmobSMS.verifySmsCode(phone, EtCode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {//短信验证码已验证成功
                    Log.i("smile", "验证通过");
                    /**
                     *  实现注册
                     */
                    BmobUser bu = new BmobUser();
                    bu.setUsername(phone);
                    bu.setPassword(pwd);
                    bu.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e == null) {
                                ToastUtils.show(Register2Activity.this, "注册成功");
                                Intent intent = new Intent(Register2Activity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtils.show(Register2Activity.this, "注册失败");
                            }
                        }
                    });

                } else {
                    Log.i("smile", "验证失败：code =" + e.getErrorCode() + ",msg = " + e.getLocalizedMessage());
                }
            }
        });

    }

    public void Send() {
        /**
         * 发送验证码
         */
        BmobSMS.requestSMSCode(phone, "ZTEShop", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    //验证码发送成功
                    ToastUtils.show(Register2Activity.this, "短信验证码已发送");
                } else {
                    ToastUtils.show(Register2Activity.this, "短信验证码发送失败,code=" + e.getErrorCode());
                }
            }
        });

    }

}
