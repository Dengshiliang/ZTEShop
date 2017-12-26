package com.kingsley.zteshop.adapter;

import android.content.Context;
import android.net.Uri;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.ShoppingCart;

import java.util.List;

/**
 * 订单商品
 */
public class WareOrderAdapter extends SimpleAdapter<ShoppingCart> {

    public WareOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, datas, R.layout.template_order_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, ShoppingCart shoppingCart) {

        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);

        draweeView.setImageURI(Uri.parse(shoppingCart.getImgUrl()));
    }

    public float getTotalPrice() {

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

}
