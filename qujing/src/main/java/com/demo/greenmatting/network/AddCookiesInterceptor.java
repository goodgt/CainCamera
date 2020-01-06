package com.demo.greenmatting.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加Cookie
 */
public class AddCookiesInterceptor implements Interceptor {

    public static String cookie = "";
    public static String token = "";
    public static String sme = "";
    public static String version = "";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("version", version);
        builder.addHeader("Cookie", cookie);
        builder.addHeader("token", token);
        builder.addHeader("sme", sme);
        builder.addHeader("device", "2");
        return chain.proceed(builder.build());
    }
}