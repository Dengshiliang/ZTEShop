package com.kingsley.zteshop.adapter;

import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Ware;

import java.util.List;

/**
 * 分类右部商品显示适配器
 */
public class CategoryWaresAdapter extends SimpleAdapter<Ware> {

    public CategoryWaresAdapter(Context context, List<Ware> datas) {
        super(context, datas, R.layout.template_grid_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, Ware wares) {

        holder.getTextView(R.id.tv_title).setText(wares.getName());
        holder.getTextView(R.id.tv_price).setText("￥ " + wares.getPrice());
        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));
    }
}
