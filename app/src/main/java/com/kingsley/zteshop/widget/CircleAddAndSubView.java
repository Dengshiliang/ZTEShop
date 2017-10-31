package com.kingsley.zteshop.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.utils.KeyboardUtil;

/**
 * Created by Kingsley on 2017/9/22.
 */

public class CircleAddAndSubView extends LinearLayout implements View.OnClickListener {

    public static final int TYPE_SUBTRACT = 0; //减

    public static final int TYPE_ADD = 1; //加

    public static final int TYPE_INPUT = 2; //输入框 输入事件

    private static final int DEFAULT_NUM = 1;//默认num值

    private static final int MAX_NUM = 999;

    private View mLayoutView;

    private Context mContext;

    private ImageView mBtnAdd;//加按钮

    private ImageView mBtnSub;//减按钮

    private EditText mEditCount;//数量显示

    private OnNumChangeListener mOnNumChangeListener;

    private int mType = -1;

    /**
     * 设置监听回调
     *
     * @param onNumChangeListener
     */
    public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener) {
        this.mOnNumChangeListener = onNumChangeListener;
    }


    public CircleAddAndSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutView = LayoutInflater.from(context).inflate(R.layout.add_sub_view, this);
        this.mContext = context;
        initView();
        initData();
        setListener();
    }


    private void initView() {

        mBtnAdd = (ImageView) mLayoutView.findViewById(R.id.btn_add);
        mBtnSub = (ImageView) mLayoutView.findViewById(R.id.btn_sub);
        mEditCount = (ForbidenNumEditText) mLayoutView.findViewById(R.id.edt_count);

        //重新设置mBtnAdd mBtnSub宽高 用来保证正方形
        setViewSize(mBtnAdd);
        setViewSize(mBtnSub);
    }

    private void initData() {
        setAddBtnImageResource(R.mipmap.ic_add_goods);
        setSubBtnImageResource(R.mipmap.ic_sub_goods);
        setNum(DEFAULT_NUM);//设置默认数量

    }

    private void setListener() {

        mBtnAdd.setOnClickListener(this);
        mBtnSub.setOnClickListener(this);

        mEditCount.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mType = TYPE_INPUT;
                return false;
            }
        });

        mEditCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Editable s:变化后的所有字符
                if (mOnNumChangeListener != null) {
                    mOnNumChangeListener.onNumChange(mLayoutView, mType, getNum());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        //隐藏软键盘x
        KeyboardUtil.closeKeybord(v, mContext);
        String countText = mEditCount.getText().toString();
        if (TextUtils.isEmpty(countText)) {
            setNum(DEFAULT_NUM);
            return;
        }
        int num = DEFAULT_NUM;
        try {
            num = Integer.valueOf(mEditCount.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.btn_add:
                //加号
                mType = TYPE_ADD;
                if (num >= MAX_NUM) {
                    num = MAX_NUM;
                } else {
                    num++;
                }
                setNum(num);
                break;
            case R.id.btn_sub://减号
                mType = TYPE_SUBTRACT;
                num--;
                setNum(num);
                break;
            default:
                break;
        }

    }

    public void setViewSize(final View view) {
        view.post(new Runnable() {
            public void run() {
                //这里获取宽高
                int width = view.getWidth();
                int height = view.getHeight();
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                if (width < height) {
                    params.height = width;
                } else if (width > height) {
                    params.width = height;
                }
                view.setLayoutParams(params);
            }
        });
    }

    /**
     * 设置中间的距离
     *
     * @param distance
     */
    public void setMiddleDistance(int distance) {
        mEditCount.setPadding(distance, 0, distance, 0);
    }

    /**
     * 设置数量
     *
     * @param num
     */
    public void setNum(int num) {
        if (num > 0) {
            mBtnSub.setVisibility(View.VISIBLE);
            mEditCount.setVisibility(View.VISIBLE);
        } else {
            mBtnSub.setVisibility(View.INVISIBLE);
            mEditCount.setVisibility(View.INVISIBLE);
        }
        mEditCount.setText(String.valueOf(num));
        //光标设置在结尾
        mEditCount.setSelection(mEditCount.getText().length());
    }

    /**
     * 获取值
     *
     * @return
     */
    public int getNum() {
        String countText = mEditCount.getText().toString().trim();
        if (!TextUtils.isEmpty(countText)) {
            return Integer.parseInt(countText);
        } else {
            return DEFAULT_NUM;
        }
    }

    /**
     * 设置加号图片
     *
     * @param addBtnDrawable
     */
    public void setAddBtnImageResource(int addBtnDrawable) {
        mBtnAdd.setImageResource(addBtnDrawable);
    }

    /**
     * 设置减法图片
     *
     * @param subBtnDrawable
     */
    public void setSubBtnImageResource(int subBtnDrawable) {
        mBtnSub.setImageResource(subBtnDrawable);
    }

    /**
     * 设置加法减法的背景色
     *
     * @param addBtnColor
     * @param subBtnColor
     */
    public void setButtonBgColor(int addBtnColor, int subBtnColor) {
        mBtnAdd.setBackgroundColor(addBtnColor);
        mBtnSub.setBackgroundColor(subBtnColor);
    }

    public interface OnNumChangeListener {
        /**
         * 监听方法
         */
        void onNumChange(View view, int type, int num);

    }

}
