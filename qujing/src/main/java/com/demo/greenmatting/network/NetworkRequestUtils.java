package com.demo.greenmatting.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/22 0022.
 */

public class NetworkRequestUtils {

    private static int count;

    public void simpleNetworkRequest(String methodName, MyCallback listener) {
        simpleNetworkRequest(methodName, (Map) null, listener);
    }

    public void simpleNetworkRequest(String methodName, String param, MyCallback listener) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = new Gson().fromJson(param, type);
        simpleNetworkRequest(methodName, map, listener);
    }

    public void simpleNetworkRequest(String methodName, String param, int successCode, MyCallback listener) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = new Gson().fromJson(param, type);
        simpleNetworkRequest(methodName, map, successCode, listener);
    }

    public void simpleNetworkRequest(String methodName, String param, boolean dismissDialog, MyCallback listener) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = new Gson().fromJson(param, type);
        simpleNetworkRequest(methodName, map, dismissDialog, listener);
    }

    public void simpleNetworkRequest(String methodName, Map<String, Object> param, MyCallback listener) {
        simpleNetworkRequest(methodName, param, 20, listener);
    }

    public void simpleNetworkRequest(String methodName, Map<String, Object> param, boolean dismissDialog, MyCallback listener) {
        simpleNetworkRequest(methodName, param, 20, dismissDialog, listener);
    }

    public void simpleNetworkRequest(String methodName, Map<String, Object> param, int successCode, MyCallback listener) {
        simpleNetworkRequest(methodName, param, successCode, true, listener);
    }

    public void simpleNetworkRequest(final String methodName, Map<String, Object> param, final int successCode, final boolean dismissDialog, final MyCallback listener) {

        count++;
        Log.e("count start==", count + "");
        OpenApiService api = RestDataSource.getInstance().getApiService();
        try {
            Method m = param == null ? api.getClass().getDeclaredMethod(methodName) : api.getClass().getDeclaredMethod(methodName, Map.class);
            Observable observable = param == null ? (Observable) m.invoke(api) : (Observable) m.invoke(api, param);
            Subscription subscription = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BaseRes>() {
                        @Override
                        public void onCompleted() {
                            count--;
                            Log.e("count end  ==", count + "");
//                                if (count == 0)
//                                    RestDataSource.unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (dismissDialog) {
//                                CustomProgressDialog.getInstance().dismiss();
                            }
                            if (listener != null) {
                                listener.loadingDataError(new Error(/*methodName + " --> " + */e.getMessage()));
                            }
                        }

                        @Override
                        public void onNext(BaseRes s) {
                            if (dismissDialog) {
//                                CustomProgressDialog.getInstance().dismiss();
                            }
                            if (successCode > 0) {
                                if (s.getCode() == successCode) {
                                    if (listener != null) {
                                        listener.loadingDataSuccess(s);
                                    }
                                } else {
                                    if (listener != null) {
                                        listener.loadingDataError(new Error(/*methodName + " --> " + */s.getMessage()));
                                    }
                                }

                            } else {
                                if (listener != null) {
                                    listener.loadingDataSuccess(s);
                                }
                            }
                        }
                    });
            RestDataSource.add(subscription);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.loadingDataError(new Error(/*methodName + " --> " + */e.getMessage()));
            }
        }
    }

    public void networkRequest(String methodName, Map<String, Object> param, final MyCallback listener) {
        OpenApiService api = RestDataSource.getInstance().getApiService();
        try {
            Method m = api.getClass().getDeclaredMethod(methodName, Map.class);
            Observable observable = (Observable) m.invoke(api, param);
            Subscription s = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BaseRes>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
//                            CustomProgressDialog.getInstance().dismiss();
                            if (listener != null) {
                                listener.loadingDataError(e);
                            }
                        }

                        @Override
                        public void onNext(BaseRes s) {
//                            CustomProgressDialog.getInstance().dismiss();
                            if (listener != null) {
                                listener.loadingDataSuccess(s);
                            }
                        }
                    });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
