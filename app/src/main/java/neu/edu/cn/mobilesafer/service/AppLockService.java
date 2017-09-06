package neu.edu.cn.mobilesafer.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;

import neu.edu.cn.mobilesafer.activity.EnterPsdActivity;
import neu.edu.cn.mobilesafer.db.dao.AppLockDao;

public class AppLockService extends Service {
    // 操作数据库的对象
    private AppLockDao mAppLockDao;
    // 数据库中已加锁的应用的
    private List<String> mLockedAppPkgNameList;
    // 是否开启监视功能
    private boolean isWatch;
    // 跳过已解锁的应用的包名
    private String mSkipPackageName;
    // AppLock对应的数据库的观察者
    private AppLockContentObserver mContentObserver;
    // 跳过已解锁应用的广播接收器
    private InnerSkipWatchReceiver innerSkipWatchReceiver;

    @Override
    public void onCreate() {
        isWatch = true;
        // 获取操作数据库的对象
        mAppLockDao = AppLockDao.getInstance(this);
        // 如果监听到带有跳过此应用的广播,则跳过此次拦截
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SKIP");
        innerSkipWatchReceiver = new InnerSkipWatchReceiver();
        registerReceiver(innerSkipWatchReceiver, intentFilter);

        // 注册一个内容观察者,观察数据库的变化,一旦数据有删除或者添加,则需要让mLockedAppPkgNameList重新获取一次数据
        mContentObserver = new AppLockContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);

        // 监视手机程序,若为已加锁程序则弹出拦截界面,否则直接进入
        appWatch();
        super.onCreate();
    }

    class AppLockContentObserver extends ContentObserver {

        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 获取数据库中所有已加锁程序的包名集合
                    mLockedAppPkgNameList = mAppLockDao.findAll();
                }
            }).start();
            super.onChange(selfChange);
        }
    }

    class InnerSkipWatchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackageName = intent.getStringExtra("packagename");
        }
    }

    /**
     * 监视手机程序,若为已加锁程序则弹出拦截界面,否则直接进入
     */
    private void appWatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取数据库中所有已加锁程序的包名集合
                mLockedAppPkgNameList = mAppLockDao.findAll();
                while (isWatch) {
                    ActivityManager acticityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    // 获取当前手机正在开启应用的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTaskInfoList = acticityManager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
                    // 获取栈顶的activity的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    if (mLockedAppPkgNameList.contains(packageName)) {
                        if (!packageName.equals(mSkipPackageName)) {
                            // 包含当前要开启的应用，则弹出拦截页面
                            Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename", packageName);
                            startActivity(intent);
                        }
                    }

                    //睡眠一下,时间片轮转
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 停止手机看门狗服务
        isWatch = true;
        // 取消广播接收器
        if (innerSkipWatchReceiver != null) {
            unregisterReceiver(innerSkipWatchReceiver);
        }
        // 注销内容观察者
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }
}
