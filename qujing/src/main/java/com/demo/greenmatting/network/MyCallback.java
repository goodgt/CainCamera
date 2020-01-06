package com.demo.greenmatting.network;

/**
 * 网络请求接口回调
 *
 * @param <T> 数据泛型
 *            Created by Administrator on 2017/3/15.
 * @author hongsir
 */
public abstract class MyCallback<T> {
    /**
     * 请求成功回调
     *
     * @param result 服务端返回数据，调用指定泛型
     */
    public abstract void loadingDataSuccess(T result);

    /**
     * 返回数据为空
     */
    public void emptyData() {
//        MyToast.showLong("无数据");
        finalCallBack();
    }

    /**
     * 接口失败回调
     *
     * @param error 服务端返回错误日志
     */
    public void loadingDataError(Throwable error) {
//        MyToast.showLong(error.getMessage());
        finalCallBack();
    }

    /**
     * 绝对执行的回调
     */
    public void finalCallBack() {
//        CustomProgressDialog.getInstance().dismiss();
    }
}
