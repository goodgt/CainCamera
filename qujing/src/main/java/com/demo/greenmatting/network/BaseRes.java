package com.demo.greenmatting.network;


import java.io.Serializable;

/**
 * 实体类基类
 *
 * @author guting
 */

public class BaseRes<T> implements Serializable {
    /**
     * 状态码
     */
    private int code;
    /**
     * 消息
     */
    private String message;
    /**
     * 列表数据总数
     */
    private int count;
    /**
     * token
     */
    private String token;

    private double accountbalance;

    private int wishcnt;
    private int fadzcnt;
    private int helpcnt;
    private String expiretime;
    private String sqbalance;
    private String sqbalanceetime;
    private int seqid;

    public int getSeqid() {
        return seqid;
    }

    public void setSeqid(int seqid) {
        this.seqid = seqid;
    }

    public String getSqbalanceetime() {
        return sqbalanceetime;
    }

    public void setSqbalanceetime(String sqbalanceetime) {
        this.sqbalanceetime = sqbalanceetime;
    }

    public String getSqbalance() {
        return sqbalance;
    }

    public void setSqbalance(String sqbalance) {
        this.sqbalance = sqbalance;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public int getFadzcnt() {
        return fadzcnt;
    }

    public void setFadzcnt(int fadzcnt) {
        this.fadzcnt = fadzcnt;
    }

    public double getAccountbalance() {
        return accountbalance;
    }

    public void setAccountbalance(double accountbalance) {
        this.accountbalance = accountbalance;
    }

    public int getHelpcnt() {
        return helpcnt;
    }

    public void setHelpcnt(int helpcnt) {
        this.helpcnt = helpcnt;
    }

    public int getWishcnt() {
        return wishcnt;
    }

    public void setWishcnt(int wishcnt) {
        this.wishcnt = wishcnt;
    }

    private String psid;
    private String woid;

    public String getWoid() {
        return woid;
    }

    public void setWoid(String woid) {
        this.woid = woid;
    }

    public String getPsid() {
        return psid;
    }

    public void setPsid(String psid) {
        this.psid = psid;
    }

    /**
     * 泛型对象
     */
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
