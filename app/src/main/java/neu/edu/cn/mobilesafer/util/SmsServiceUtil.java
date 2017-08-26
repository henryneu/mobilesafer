package neu.edu.cn.mobilesafer.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by neuHenry on 2017/8/26.
 */

public class SmsServiceUtil {

    /**
     * @param context 上下文环境
     * @param serviceName 服务的类名
     * @return true 正在运行 false 未在运行
     */
    public static boolean isRunning(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceTask = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceTask) {
            if (serviceName.equals(runningServiceInfo.service.getClass())) {
                return true;
            }
        }
        return false;
    }
}
