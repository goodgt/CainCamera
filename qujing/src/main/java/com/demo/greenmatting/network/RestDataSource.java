package com.demo.greenmatting.network;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Retrofit 初始化
 */
public class RestDataSource {

    //    private static String BASE_URL = "http://192.168.2.200:8383/";
    private static String BASE_URL = "https://shenpai.kuaimashi.com/";
    private static RestDataSource sInstance;
    private static Retrofit.Builder mRetrofit;
    private static OpenApiService apiService;
    private static CompositeSubscription mCompositeSubscription;
    private static final int DEFAULT_TIMEOUT = 10;

    public RestDataSource() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                /*.baseUrl(OtherUtils.getBaseUrl())*/
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient());
    }

    private OkHttpClient okHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(final String message) {
                Log.i("ღღღღღღღღღღღღ>", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ExceptionInterceptor())
                .addInterceptor(new LoginInterceptor()) //必须要在logging前面
                .addInterceptor(logging)
//                .addInterceptor(new ReceivedCookiesInterceptor())
                .addInterceptor(new AddCookiesInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
//        builder.sslSocketFactory(createBadSslSocketFactory());
//        builder.sslSocketFactory(badSslSocketFactory(),new X509TrustManager() {
//            @Override
//            publicui void checkClientTrusted(X509Certificate[] chain, String authType)
//                    throws CertificateException {
//            }
//            @Override
//            publicui void checkServerTrusted(X509Certificate[] chain, String authType)
//                    throws CertificateException {
//            }
//            @Override
//            publicui X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        });
        return builder.build();
    }

    public static RestDataSource getInstance() {
        if (sInstance == null) {
            synchronized (RestDataSource.class) {
                if (sInstance == null) {
                    sInstance = new RestDataSource();
                }
            }
        }
        return sInstance;
    }

    private static CompositeSubscription getCompositeSubscription() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }

    public static void add(Subscription s) {

    }

    public static void unsubscribe() {

    }

    public OpenApiService getApiService() {
        if (apiService == null) apiService = mRetrofit.build().create(OpenApiService.class);
        return apiService;
    }


    public static void setBaseUrl(String baseUrl) {
        mRetrofit.baseUrl(TextUtils.isEmpty(baseUrl) ? BASE_URL : baseUrl);
//        mRetrofit.baseUrl(TextUtils.isEmpty(baseUrl) ? OtherUtils.getBaseUrl() : baseUrl);
        apiService = mRetrofit.build().create(OpenApiService.class);
    }

    private static SSLSocketFactory badSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            return SSLContext.getInstance("TLS").getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private static SSLSocketFactory createBadSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            context.init(null, new TrustManager[]{permissive}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
