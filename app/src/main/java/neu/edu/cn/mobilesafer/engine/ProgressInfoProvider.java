package neu.edu.cn.mobilesafer.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.db.ProcessInfo;

/**
 * Created by neuHenry on 2017/9/3.
 */

public class ProgressInfoProvider {

    /**
     * 获取进程总数
     *
     * @param context 上下文环境
     * @return 运行的进程总数
     */
    public static int getProcessCount(Context context) {
        int processCount = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        processCount = runningAppProcesses.size();
        return processCount;
    }

    /**
     * 获取可用的运行内存空间
     *
     * @param context 上下文环境
     * @return 返回可用的内存空间的大小，单位为bytes 返回0说明异常
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 获取memoryInfo中相应的可用内存的大小，并返回
        return memoryInfo.availMem;
    }

    /**
     * 根据Android版本选择调用合适的方法，API level 高于(包含)16时，调用此方法可以获取到总的可用内存
     * 获取手机总的运行内存空间大小
     *
     * @param context 上下文环境
     * @return 返回总的内存空间的大小，单位为bytes 返回0说明异常
     */
    public static long getTotalMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 获取memoryInfo中相应的总的可用空间大小，并返回
        return memoryInfo.totalMem;
    }

    /**
     * 根据Android版本选择调用合适的方法，API level 低于16时，调用此方法可以获取到总的可用内存
     * 获取手机总的运行内存空间大小
     *
     * @param context 上下文环境
     * @return 返回总的内存空间大小，单位为bytes 返回0说明异常
     */
    public static long getTotalSpace(Context context) {
        // 内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            //将字符串转换成字符的数组
            char[] charArray = lineOne.toCharArray();
            //循环遍历每一个字符,如果此字符的ASCII码在0到9的区域内,说明此字符有效
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : charArray) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null && bufferedReader != null) {
                    fileReader.close();
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 返回当前运行的所有进程的信息集合
     *
     * @param context 获取包管理者对象所需的上下文环境
     * @return 返回运行的进程信息列表
     */
    public static List<ProcessInfo> getProgressInfoList(Context context) {
        // 进程相关信息的列表
        List<ProcessInfo> processInfoLists = new ArrayList<>();
        // 获取ActivityManager对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取PackageManager对象
        PackageManager packageManager = context.getPackageManager();
        // 获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningProgress = activityManager.getRunningAppProcesses();
        // 循环遍历正在运行的进程的集合，获取进程相关信息
        for (ActivityManager.RunningAppProcessInfo runningProcessInfo : runningProgress) {
            ProcessInfo processInfo = new ProcessInfo();
            // 获取进程所在的应用的包名
            processInfo.packageName = runningProcessInfo.processName;
            // 获取进程占用的内存大小(传递一个进程对应的pid数组)
            Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningProcessInfo.pid});
            // 返回数组中索引位置为0的对象,为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            // 获取已使用的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(processInfo.packageName, 0);
                // 获取应用的名称
                processInfo.appName = applicationInfo.loadLabel(packageManager).toString();
                // 获取应用的图标
                processInfo.icon = applicationInfo.loadIcon(packageManager);
                // 判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (Exception e) {
                // 异常处理
                processInfo.appName = runningProcessInfo.processName;
                processInfo.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoLists.add(processInfo);
        }
        return processInfoLists;
    }

    /**
     * 杀死传入进程信息
     *
     * @param context 上下文环境
     * @param processInfo  准备杀死的进程信息
     */
    public static void realeaseSelectedItem (Context context, ProcessInfo processInfo) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(processInfo.packageName);
    }

    /**
     * 杀死所有进程信息
     *
     * @param context 上下文环境
     */
    public static void killAll (Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoLists = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfoLists) {
            // 自己不能杀死自己的进程
            if (runningAppProcessInfo.processName.equals(context.getPackageName())) {
                continue;
            }
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
    }
}
