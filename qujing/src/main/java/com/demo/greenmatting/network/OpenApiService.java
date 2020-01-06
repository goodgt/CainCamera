package com.demo.greenmatting.network;

import com.demo.greenmatting.LoginActivity;
import com.demo.greenmatting.utils.UpdateAppVersion;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * API
 */
public interface OpenApiService {

    String v1 = "v1";

    /**
     * 注册
     */
    @GET("c/" + v1 + "/user/reg")
    Observable<BaseRes> userReg(@QueryMap Map<String, Object> params);

    /**
     * 登录
     */
    @GET("c/" + v1 + "/user/login")
    Observable<BaseRes<LoginActivity.Result>> userLogin(@QueryMap Map<String, Object> params);

    /**
     * 更新
     */
    @GET("c/" + v1 + "/update/info")
    Observable<BaseRes<UpdateAppVersion.UpdateInfoRes>> updateInfo(@QueryMap Map<String, Object> params);

    /**
     * ic_contact_us
     */
    @GET("a/" + v1 + "/user/lianxi")
    Observable<BaseRes> userLianxi(@QueryMap Map<String, Object> params);
}
