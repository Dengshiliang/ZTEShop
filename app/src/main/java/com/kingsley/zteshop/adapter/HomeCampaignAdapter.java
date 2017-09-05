package com.kingsley.zteshop.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Banner;
import com.kingsley.zteshop.bean.Campaign;
import com.kingsley.zteshop.bean.HomeCampaign;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeCampaignAdapter extends RecyclerView.Adapter<HomeCampaignAdapter.ViewHolder> {

    private List<HomeCampaign> mDatas;
    private Context context;

    private List<Banner> mBanners;

    private OnHomeCampaignClickListener homeCampaignClickListener;

    private static int VIEW_TYPE_L = 1;
    private static int VIEW_TYPE_R = 2;

    boolean isLoad = false;

    private int layoutId;

    public HomeCampaignAdapter(Context context) {
        this.context = context;
    }

    public void setHomeCatgoryClickListener(OnHomeCampaignClickListener homeCatgoryClickListener) {
        this.homeCampaignClickListener = homeCatgoryClickListener;
    }

    public void setBanners(List<Banner> banners) {
        this.mBanners = banners;
        notifyItemChanged(0);
    }

    public void setDatas(List<HomeCampaign> list) {
        this.mDatas = list;
        notifyItemRangeChanged(1, list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            layoutId = R.layout.layout_slider;
        } else if (viewType == VIEW_TYPE_R) {
            layoutId = R.layout.template_home_cardview2;
        } else if (viewType == VIEW_TYPE_L) {
            layoutId = R.layout.template_home_cardview;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if (position == 0) {
            if (mBanners != null && !isLoad) {
                for (Banner banner : mBanners) {
                    TextSliderView textSliderView = new TextSliderView(context);
                    textSliderView.image(banner.getImgUrl());
                    textSliderView.description(banner.getName());
                    textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                    viewHolder.banner.addSlider(textSliderView);
                }
                isLoad = !isLoad;
            }
            //设置指示器
            viewHolder.banner.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
            //设置动画效果
            viewHolder.banner.setCustomAnimation(new DescriptionAnimation());
            //设置转场效果
            viewHolder.banner.setPresetTransformer(SliderLayout.Transformer.RotateUp);
            //设置时长
            viewHolder.banner.setDuration(3000);
        } else {
            HomeCampaign campaign = mDatas.get(position - 1);
            viewHolder.textTitle.setText(campaign.getTitle());
            Picasso.with(context).load(campaign.getCpOne().getImgUrl()).into(viewHolder.imageViewBig);
            Picasso.with(context).load(campaign.getCpTwo().getImgUrl()).into(viewHolder.imageViewSmallTop);
            Picasso.with(context).load(campaign.getCpThree().getImgUrl()).into(viewHolder.imageViewSmallBottom);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 1 : mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position % 2 == 0) {
            return VIEW_TYPE_R;
        }
        return VIEW_TYPE_L;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SliderLayout banner;

        TextView textTitle;
        ImageView imageViewBig;
        ImageView imageViewSmallTop;
        ImageView imageViewSmallBottom;

        public ViewHolder(View itemView, int position) {
            super(itemView);
            if (position == 0) {
                banner = (SliderLayout) itemView.findViewById(R.id.banner);
            } else {
                textTitle = (TextView) itemView.findViewById(R.id.text_title);
                imageViewBig = (ImageView) itemView.findViewById(R.id.imgview_big);
                imageViewSmallTop = (ImageView) itemView.findViewById(R.id.imgview_small_top);
                imageViewSmallBottom = (ImageView) itemView.findViewById(R.id.imgview_small_bottom);

                imageViewBig.setOnClickListener(this);
                imageViewSmallTop.setOnClickListener(this);
                imageViewSmallBottom.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (homeCampaignClickListener != null) {
                anim(view);
            }
        }

        /**
         * 图片翻转效果
         *
         * @param v
         */
        private void anim(final View v) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0.0F, 360F)
                    .setDuration(200);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    HomeCampaign campaign = mDatas.get(getLayoutPosition() - 1);

                    switch (v.getId()) {
                        case R.id.imgview_big:
                            homeCampaignClickListener.onClick(v, campaign.getCpOne());
                            break;
                        case R.id.imgview_small_top:
                            homeCampaignClickListener.onClick(v, campaign.getCpTwo());
                            break;
                        case R.id.imgview_small_bottom:
                            homeCampaignClickListener.onClick(v, campaign.getCpThree());
                            break;
                    }
                }

            });

            animator.start();
        }

    }

    public interface OnHomeCampaignClickListener {
        void onClick(View view, Campaign campaign);
    }

}