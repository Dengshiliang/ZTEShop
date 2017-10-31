package com.kingsley.zteshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.ShoppingCart;
import com.kingsley.zteshop.utils.CartProvider;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.CircleAddAndSubView;

import java.util.List;

/**
 * Created by Kingsley on 2017/10/25.
 */

public class CartAdapter extends SimpleAdapter<ShoppingCart> implements BaseAdapter.OnItemClickListenner {

    private Context context;
    private CheckBox mCheckBox;
    private TextView mTextView;

    private CartProvider cartProvider;

    public CartAdapter(Context context, List<ShoppingCart> datas, CheckBox checkBox, TextView textView) {
        super(context, datas, R.layout.template_cart);
        this.mCheckBox = checkBox;
        this.mTextView = textView;

        cartProvider = CartProvider.getInstance(context);

        setCheckBox(checkBox);
        setTextView(textView);

    }

    public void setTextView(TextView textView) {
        this.mTextView = textView;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.mCheckBox = checkBox;
    }

    @Override
    public void bindData(BaseViewHolder holder, final ShoppingCart item) {

        holder.getTextView(R.id.tv_title_cart).setText(item.getName());
        holder.getTextView(R.id.tv_price_cart).setText("￥ " + item.getPrice());

        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view_cart);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));

        final CheckBox checkBox = (CheckBox) holder.getView(R.id.checkbox);
        checkBox.setChecked(item.isChecked());

        CircleAddAndSubView numberAddSubView = (CircleAddAndSubView) holder.getView(R.id.add_sub_view);
        numberAddSubView.setNum(item.getCount());

        numberAddSubView.setOnNumChangeListener(new CircleAddAndSubView.OnNumChangeListener() {
            @Override
            public void onNumChange(View view, int type, int num) {

                item.setCount(num);
                cartProvider.update(item);
                showTotalPrice();

            }
        });

    }

    public void showTotalPrice() {
        float total = getTotalPrice();
        mTextView.setText(Html.fromHtml(
                "合计 ￥<span style='color:#eb4f38'>" + total + "</span>"),
                TextView.BufferType.SPANNABLE);

    }

    private float getTotalPrice() {

        float sum = 0;
        if (!isNull()) {
            return sum;
        }

        for (ShoppingCart cart : mDatas) {
            if (cart.isChecked())
                sum += cart.getCount() * Float.parseFloat(cart.getPrice());
        }

        return sum;
    }

    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }

    @Override
    public void onItemClick(View view, int position) {

        ShoppingCart cart = getItem(position);
        cart.setIsChecked(!cart.isChecked());
        notifyItemChanged(position);

    }
}
