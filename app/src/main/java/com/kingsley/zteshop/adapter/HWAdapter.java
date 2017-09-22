package com.kingsley.zteshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Ware;
import com.kingsley.zteshop.utils.ToastUtils;

import java.util.List;

/**
 * Created by Kingsley on 2017/9/7.
 * 热卖商品适配器
 */
public class HWAdapter extends SimpleAdapter<Ware> {

    public HWAdapter(Context context, List<Ware> datas) {
        super(context, datas, R.layout.hot_ware);
    }

    @Override
    public void bindData(BaseViewHolder holder, Ware ware) {

        TextView tv_Title = holder.getTextView(R.id.tv_title);
        TextView tv_Price = holder.getTextView(R.id.tv_price);
        Button button = holder.getButton(R.id.btn_add);
        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);

        tv_Title.setText(ware.getName());
        tv_Price.setText("￥ " + ware.getPrice());
        draweeView.setImageURI(Uri.parse(ware.getImgUrl()));

        if (button != null) {

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加数据到购物车 TODO

                    ToastUtils.show(mContext, mContext.getString(R.string.has_add_cart));
                }
            });
        }

    }

    /**
     * 设置布局
     *
     * @param layoutId
     */
    public void reSetLayout(int layoutId) {
        this.mLayoutResId = layoutId;

        notifyItemRangeChanged(0, getDatas().size());
    }

}
