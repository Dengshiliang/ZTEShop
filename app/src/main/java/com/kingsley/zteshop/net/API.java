package com.kingsley.zteshop.net;

import com.kingsley.zteshop.bean.Banner;
import com.kingsley.zteshop.bean.HomeCampaign;

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

}
