package com.demo.greenmatting.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.TypedValue;

import java.util.List;

public class OtherUtils {

    public static float applyDimension(Context context, float value) {
        return applyDimension(context, TypedValue.COMPLEX_UNIT_DIP, value);
    }

    /**
     * 单位换算
     *
     * @param unit
     * @param value
     * @return
     */
    public static float applyDimension(Context context, int unit, float value) {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * context.getResources().getDisplayMetrics().density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * context.getResources().getDisplayMetrics().scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * context.getResources().getDisplayMetrics().xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * context.getResources().getDisplayMetrics().xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * context.getResources().getDisplayMetrics().xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void exitAPP(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
    }

    /**
     * 当前版本信息
     *
     * @return 版本代号
     */
    public static String getLocalVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
