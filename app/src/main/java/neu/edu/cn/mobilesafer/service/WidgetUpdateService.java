package neu.edu.cn.mobilesafer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.ProgressInfoProvider;
import neu.edu.cn.mobilesafer.receiver.ProcessWidgetProvider;

public class WidgetUpdateService extends Service {

    private static final String tag = "WidgetUpdateService";

    // 获取定时器对象，定时一段时间刷新小窗体界面
    private Timer mTimer;
    // AppWidgetManager对象
    private AppWidgetManager mAppWidgetManager;
    // 一键清理进程的广播接收器
    private InnerKillProcessReceiver mInnerKillProcessReceiver;
    // 锁屏取消定时器的广播接收器
    private CancleTimerReceiver mCancleTimerReceiver;

    @Override
    public void onCreate() {
        // 开启定时器
        startTimer();

        // 创建一键清理进程的广播接收器
        mInnerKillProcessReceiver = new InnerKillProcessReceiver();
        // 匹配的Action
        IntentFilter intentFilter = new IntentFilter("android.intent.action.KILL_BACKGROUND_PROCESS");
        registerReceiver(mInnerKillProcessReceiver, intentFilter);

        // 创建锁屏取消定时器的广播接收器
        CancleTimerReceiver mCancleTimerReceiver = new CancleTimerReceiver();
        IntentFilter intentScreenFilter = new IntentFilter();
        // 添加匹配规则，锁屏和解锁
        intentScreenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentScreenFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mCancleTimerReceiver, intentScreenFilter);
    }

    /**
     * 开启定时器
     */
    private void startTimer() {
        // 获取并启动定时器
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 每隔5秒刷新一次
                Log.i(tag, "每隔5秒刷新一次......");
                updateWidgetView();
            }
        }, 0, 5000);
        super.onCreate();
    }

    /**
     * 每隔5秒刷新一次widget界面
     */
    private void updateWidgetView() {
        // 获取AppWidgetManager对象，单例模式
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        // 获取当前手机正在运行的进程数,并赋值为Text
        remoteViews.setTextViewText(R.id.text_process_count, "进程总数:" + ProgressInfoProvider.getProcessCount(this));
        // 获取当前手机剩余的可用内存空间
        String availableMem = Formatter.formatFileSize(this, ProgressInfoProvider.getAvailableMemory(this));
        Log.i(tag, "availableMem:" + availableMem);
        remoteViews.setTextViewText(R.id.text_process_memory, "可用内存:" + availableMem);

        // 点击窗体小部件,进入应用主界面
        Intent intentHome = new Intent("android.intent.action.Home");
        intentHome.addCategory("android.intent.category.DEFAULT");
        // 1、在那个控件上响应点击事件2、延期的意图
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentHome, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_linear_root, pendingIntent);

        // 通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
        Intent intentClear = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent pendingIntentBroad = PendingIntent.getBroadcast(this, 0, intentClear, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.one_key_clear, pendingIntentBroad);

        ComponentName provider = new ComponentName(this, ProcessWidgetProvider.class);
        mAppWidgetManager.updateAppWidget(provider, remoteViews);
    }

    class InnerKillProcessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 一键杀死可以被杀死的进程
            ProgressInfoProvider.killAll(context);
        }
    }

    class CancleTimerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // 屏幕解锁状态时，开启定时器
                startTimer();
            } else {
                // 屏幕锁屏状态时，关闭定时器
                mTimer.cancel();
                mTimer.cancel();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mInnerKillProcessReceiver != null) {
            unregisterReceiver(mInnerKillProcessReceiver);
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }
}
