package com.kingsley.zteshop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.DateChooseDialog;
import com.kingsley.zteshop.widget.SexPopupWindows;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class SettingActivity extends BasicActivity {

    @ViewInject(R.id.change_name)
    private RelativeLayout ll_name;

    @ViewInject(R.id.change_password)
    private RelativeLayout ll_repassword;

    @ViewInject(R.id.change_sex)
    private RelativeLayout ll_sex;

    @ViewInject(R.id.change_birthday)
    private RelativeLayout ll_birthday;

    @ViewInject(R.id.feedback)
    private RelativeLayout ll_feedback;

    @ViewInject(R.id.tv_sex)
    private TextView tv_sex;

    @ViewInject(R.id.tv_date)
    private TextView tv_date;

    @ViewInject(R.id.change_loc)
    private RelativeLayout ll_loc;

    private Intent intent;
    private PopupWindow popupWindow;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("设置");
        getToolbar().setRightButtonText("完成");
        getToolbar().setRightButtonOnClickLinster(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 修改昵称
     */
    @OnClick(R.id.change_name)
    private void change_name(View view) {

        intent = new Intent(this, SettingNameActivity.class);
        startActivity(intent);

    }

    /**
     * 修改密码
     */
    @OnClick(R.id.change_password)
    private void change_password(View view) {

        intent = new Intent(this, SettingPswActivity.class);
        startActivity(intent);

    }

    /**
     * 修改性别
     */
    @OnClick(R.id.change_sex)
    private void change_sex(View view) {

        popupWindow = new SexPopupWindows(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 选择女
                tv_sex.setText("女");
                popupWindow.dismiss();
                commit();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 选择男
                tv_sex.setText("男");
                popupWindow.dismiss();
                commit();
            }
        });
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_setting, null);
        popupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 70);
    }

    /**
     * 修改生日
     */
    @OnClick(R.id.change_birthday)
    private void change_birthday(View view) {

        ToastUtils.show(SettingActivity.this, " 修改日期");
        DateChooseDialog.Builder builder = new DateChooseDialog.Builder(SettingActivity.this);
        builder.setTitle("选择日期");
        builder.setTextView((TextView) findViewById(R.id.tv_date));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                // 上传到Bmob数据
                commit();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * 我的地址
     */
    @OnClick(R.id.change_loc)
    private void myLocation(View view){
        intent = new Intent(SettingActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    /**
     * 意见反馈
     */
    @OnClick(R.id.feedback)
    private void feedback(View view) {
        ToastUtils.show(SettingActivity.this, "意见反馈");
        intent = new Intent(SettingActivity.this, FeedbackActivity.class);
        startActivity(intent);
    }

    /**
     * 提交信息
     */
    private void commit() {

        String sex = tv_sex.getText().toString().trim();
        String date = tv_date.getText().toString().trim();
        User user = BmobUser.getCurrentUser(User.class);
        // 创建用户详细信息
        user.setSex(sex);
        user.setBirthday(date);
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
