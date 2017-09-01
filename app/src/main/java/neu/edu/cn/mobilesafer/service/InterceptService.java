package neu.edu.cn.mobilesafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import neu.edu.cn.mobilesafer.db.dao.BlackNumberDao;

public class InterceptService extends Service {

    private static final String tag = "InterceptService";

    // 自定义的拦截短信的广播接收器
    private InnerSmsReceiver mInnerSmsReceiver;
    // 操作黑名单列表数据的对象
    private BlackNumberDao mBlackNumberDao;
    // 电话管理的对象
    private TelephonyManager mTelephonyManager;
    // 自定义的电话状态监听器
    private MyPhoneStateListener myPhoneStateListener;
    // 内容观察者对象
    private MyContentObserver mMyContentObserver;

    @Override
    public void onCreate() {
        // 获取操作黑名单列表数据的对象
        mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
        // 获取电话管理者对象
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 创建自定义的电话状态监听器
        myPhoneStateListener = new MyPhoneStateListener();
        // 监听电话来电状态
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // 拦截短信的过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        // 设置优先级为最高
        intentFilter.setPriority(Integer.MAX_VALUE);
        mInnerSmsReceiver = new InnerSmsReceiver();
        // 注册自定义的拦截短信的广播接收器
        registerReceiver(mInnerSmsReceiver, intentFilter);
        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        // 重写电话状态改变时触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(tag, "响铃" + incomingNumber);
                    // 响铃之后挂断电话
                    endCall(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(tag, "接听");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(tag, "挂断");
                    break;
            }
        }
    }

    /**
     * 响铃之后挂断电话
     */
    private void endCall(String phoneNumber) {
        int mode = mBlackNumberDao.getMode(phoneNumber);
        // 判断获取的拦截模式，若为1和3则拦截
        if (mode == 1 || mode == 3) {
            try {
                // 反射调用
                // 获取ServiceManager字节码文件,需要类的完整的路径
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                // 获取方法
                Method method = clazz.getMethod("getService", String.class);
                // 反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                // 调用获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                // 调用在aidl中隐藏的endCall方法
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 创建内容观察者，并注册
            mMyContentObserver = new MyContentObserver(new Handler(), phoneNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mMyContentObserver);
        }
    }

    class MyContentObserver extends ContentObserver {

        String phoneNumber;

        /**
         * 创建一个内容观察者对象
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         * @param phoneNumber  被拦截的电话号码
         */
        public MyContentObserver(Handler handler, String phoneNumber) {
            super(handler);
            this.phoneNumber = phoneNumber;
        }

        // 内容观察者若是观察到数据库中数据有变化时，调用此方法
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phoneNumber});
            super.onChange(selfChange);
        }
    }

    class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取短信的内容和发送短信的地址
            Object[] messages = (Object[]) intent.getExtras().get("pdus");
            // 循环遍历获取到的短信内容
            for (Object message : messages) {
                // 获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) message);
                // 获取短信对象的基本信息,发短信的号码，短信内容
                String messageAdressNum = sms.getOriginatingAddress();
                Log.i(tag, "messageAdressNum:" + messageAdressNum);
                String messageBody = sms.getMessageBody();
                int mode = mBlackNumberDao.getMode(messageAdressNum);
                Log.i(tag, "mode:" + mode);
                // 获取到发送短信的号码，也能查到拦截模式，但是没法拦截短信，待解决？？？
                // 如果黑名单中该电话号码的拦截模式满足以下条件则阻断广播传递
                // 解决：拦截短信(android 4.4版本失效	短信数据库,删除)
                if (mode == 2 || mode == 3) {
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 服务结束时，取消注册自定义的拦截短信的广播接收器
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        // 服务结束时，注销内容观察者
        if (mMyContentObserver != null) {
            getContentResolver().unregisterContentObserver(mMyContentObserver);
        }
        // 服务结束时，取消电话状态的监听
        if (myPhoneStateListener != null) {
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }
}
