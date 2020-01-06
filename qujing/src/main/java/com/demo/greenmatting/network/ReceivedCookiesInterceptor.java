package com.demo.greenmatting.network;

import java.io.IOException;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 *
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            String cookies = null;
            for (String header : originalResponse.headers("Set-Cookie")) {
                if (header.contains("JSESSIONID")) cookies = header;
            }
            Preferences.userRoot().put("Cookie", cookies);
        }
        return originalResponse;
    }
}
