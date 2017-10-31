package com.kingsley.zteshop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.kingsley.zteshop.utils.StringUtil;

/**
 * Created by Kingsley on 2017/9/22.
 */

public class ForbidenNumEditText extends android.support.v7.widget.AppCompatEditText {

    public ForbidenNumEditText(Context context) {
        super(context);
    }

    public ForbidenNumEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForbidenNumEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * If you handled the event, return true <br>
     * If you want to allow the event to be handled by the next receiver, return false.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //屏蔽删除键(保留edittext最低位数为1)
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            String content = ForbidenNumEditText.this.getText().toString();

            if (StringUtil.isNotEmpty(content) && content.length() == 1) {
                return false;
                //不管返回false还是true都会把del键盘屏蔽,不同的是false会继续传入到Activity中
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}