package com.demo.greenmatting.network;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/22.
 */

public class ExceptionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new SocketTimeoutException("连接超时，请重试");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("当前网络状况异常，请检查您的设置");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
