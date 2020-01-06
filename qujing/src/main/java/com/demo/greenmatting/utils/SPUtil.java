package com.demo.greenmatting.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/6/27.
 */
public class SPUtil {

    private static String spName = "sp";

    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(spName, Context.MODE_MULTI_PROCESS);
    }

    public static SharedPreferences.Editor getSPEditor(Context context) {
        return getSP(context).edit();
    }

    public static void changeStringSp(Context context, String name, String value) {
        SharedPreferences.Editor editor = getSPEditor(context);
        editor.putString(name, value);
        editor.commit();
    }

    public static String getStringSpVal(Context context, String name) {
        return getSP(context).getString(name, null);
    }

    public static void changeIntSp(Context context, String name, int value) {
        SharedPreferences.Editor editor = getSPEditor(context);
        editor.putInt(name, value);
        editor.commit();
    }

    public static int getIntSpVal(Context context, String name) {
        return getSP(context).getInt(name, 0);
    }

    public static void changeFloatSp(Context context, String name, float value) {
        SharedPreferences.Editor editor = getSPEditor(context);
        editor.putFloat(name, value);
        editor.commit();
    }

    public static float getFloatSpVal(Context context, String name) {
        return getSP(context).getFloat(name, 0.0f);
    }

    public static void changeBooleanSp(Context context, String name, boolean value) {
        SharedPreferences.Editor editor = getSPEditor(context);
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static boolean getBooleanSpVal(Context context, String name) {
        return getSP(context).getBoolean(name, false);
    }

}
