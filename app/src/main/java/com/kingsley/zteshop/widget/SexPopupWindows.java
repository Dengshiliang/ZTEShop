package com.kingsley.zteshop.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.kingsley.zteshop.R;

/**
 * Created by Kingsley on 2017/11/27.
 * 性别选择器
 */

public class SexPopupWindows extends PopupWindow {

    private View mMenuView; // PopupWindow 菜单布局
    private Context context; // 上下文参数
    private View.OnClickListener mSelectListener; // 相册选取的点击监听器
    private View.OnClickListener mCaptureListener; // 拍照的点击监听器

    public SexPopupWindows(Activity context, View.OnClickListener selectListener, View.OnClickListener captureListener) {
        super(context);
        this.context = context;
        this.mSelectListener = selectListener;
        this.mCaptureListener = captureListener;
        Init();
    }

    private void Init() {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popupitem_sex, null);
        Button btn_male = (Button) mMenuView
                .findViewById(R.id.icon_btn_male);
        Button btn_female = (Button) mMenuView
                .findViewById(R.id.icon_btn_famale);
        Button btn_cancel = (Button) mMenuView
                .findViewById(R.id.btn_cancel);

        btn_male.setOnClickListener(mCaptureListener);
        btn_female.setOnClickListener(mSelectListener);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        // 导入布局
        this.setContentView(mMenuView);
        // 设置动画效果
        this.setAnimationStyle(R.style.AnimationFade);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mMenuView.findViewById(R.id.ll_sex_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
