package com.kingsley.zteshop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BasicActivity {

    @ViewInject(R.id.tv_Country)
    private TextView mTvCountry;

    @ViewInject(R.id.tv_country_code)
    private TextView mTvCountryCode;

    @ViewInject(R.id.et_phone)
    private ClearEditText mEtPhone;

    @ViewInject(R.id.et_pwd)
    private ClearEditText mEtPsw;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户注册(1/2)");
        getToolbar().setRightButtonText("下一步");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 获取验证码
                 */
                getCode();

            }
        });
    }

    /**
     * 提交手机号码和国家代码
     */
    private void getCode() {

        String phone = mEtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = mTvCountryCode.getText().toString().trim();
        String password = mEtPsw.getText().toString().trim();

        /**
         * 判断 如果PhoneNum合法 则跳转到注册界面2/2
         * 否则 弹出吐司 手机号码不合法
         */
        if (checkPhoneNum(phone, countryCode,password)){
            Intent intent = new Intent(RegisterActivity.this,Register2Activity.class);
            intent.putExtra("username",phone);
            intent.putExtra("pwd",password);
            startActivity(intent);
            finish();
        }

    }

    /**
     * 验证手机号码合法性
     * @param phone
     * @param code
     */
    private Boolean checkPhoneNum(String phone, String code , String password) {

        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(password)) {
            ToastUtils.show(this, "请输入手机号码");
            return false;
        }

        if (code == "86") {
            if (phone.length() != 11) {
                ToastUtils.show(this, "手机号码长度不正确");
                return false;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            return false;
        }
        return true;
    }

}
