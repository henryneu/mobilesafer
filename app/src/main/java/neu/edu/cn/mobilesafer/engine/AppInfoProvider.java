package neu.edu.cn.mobilesafer.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import neu.edu.cn.mobilesafer.db.dao.AppInfo;

/**
 * Created by neuHenry on 2017/9/2.
 */

public class AppInfoProvider {

    private static PackageManager mPackageManager;

    /**
     * 返回当前手机安装的所有应用的信息集合
     * @param context 获取包管理者对象所需的上下文
     * @return 返回安装的应用信息列表
     */
    public static List<AppInfo> getAppInfoList(Context context) {
        // 获取包的管理者对象
        mPackageManager = context.getPackageManager();
        // 获取安装在手机上的应用相关信息的集合
        List<PackageInfo> packageInfoList = mPackageManager.getInstalledPackages(0);
        List<AppInfo> appInfoLists = new ArrayList<AppInfo>();
        // 循环遍历应用信息的集合
        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
            // 获取应用的包名
            appInfo.packageName = packageInfo.packageName;
            // 清单文件中应用的信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            // 获取应用的名称
            appInfo.appName = applicationInfo.loadLabel(mPackageManager).toString();
            // 获取应用的图标
            appInfo.icon = applicationInfo.loadIcon(mPackageManager);
            // 判断是否为系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                // 系统应用
                appInfo.isSystem = true;
            } else {
                // 非系统应用
                appInfo.isSystem = false;
            }
            // 判断应用是否安装在SD卡上
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                // 应用安装在SD卡上
                appInfo.isSDCard = true;
            } else {
                // 应用没有安装在SD卡上
                appInfo.isSDCard = false;
            }
            appInfoLists.add(appInfo);
        }
        return appInfoLists;
    }
}
