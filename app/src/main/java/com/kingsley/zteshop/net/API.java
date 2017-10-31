package com.kingsley.zteshop.net;

import com.kingsley.zteshop.bean.Banner;
import com.kingsley.zteshop.bean.Category;
import com.kingsley.zteshop.bean.HomeCampaign;
import com.kingsley.zteshop.bean.Page;
import com.kingsley.zteshop.bean.Ware;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Kingsley on 2017/9/5.
 */

public interface API {

    String BASE_URL = "http://112.124.22.238:8081/";

    String path = "course_api/";

    @FormUrlEncoded //获取轮播图
    @POST(path + "banner/query")
    Observable<List<Banner>> getBanner(@Field("type") int type);

    @FormUrlEncoded //获取首页
    @POST(path + "campaign/recommend")
    Observable<List<HomeCampaign>> getHome(@Field("") String empty);

    @FormUrlEncoded //获取热卖
    @POST(path + "wares/hot")
    Observable<Page<Ware>> getHotWares(@Field("curPage") int curPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded //获取分类标签
    @POST(path + "category/list")
    Observable<List<Category>> getCategory(@Field("") String empty);

    @FormUrlEncoded //获取分类下的列表
    @POST(path + "wares/list")
    Observable<Page<Ware>> getWaresList(@Field("categoryId") long categoryId,
                                         @Field("curPage") int curPage, @Field("pageSize") int pageSize);
}
