package com.kingsley.zteshop.adapter;

import android.content.Context;

import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Category;

import java.util.List;


/**
 * 分类左部列表导航适配器
 */
public class CategoryAdapter extends SimpleAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, datas, R.layout.template_single_text);
    }

    @Override
    public void bindData(BaseViewHolder holder, Category category) {
        holder.getTextView(R.id.tv_category).setText(category.getName());
    }
}
