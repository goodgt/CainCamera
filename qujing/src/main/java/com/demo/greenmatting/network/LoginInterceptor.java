package com.demo.greenmatting.network;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by Administrator on 2017/3/22.
 */

public class LoginInterceptor implements Interceptor {

    Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                return response;
            }
        }

        if (responseBody.contentLength() != 0) {
            String json = source.buffer().clone().readString(charset);
            try {
                final BaseRes res = new Gson().fromJson(json, BaseRes.class);
                if (res != null)
                    if (res.getCode() == -1) {

                    }
            } catch (Exception e) {
                return response;
            }
        }
        return response;
    }
}
