package neu.edu.cn.mobilesafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.NumberAddress;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

public class AddressService extends Service {

    private static final String tag = "AddressService";

    // 手机窗体布局的管理者
    private WindowManager mWindowManager;
    // 手机窗体的布局
    private WindowManager.LayoutParams mParams;
    // 电话管理者对象
    private TelephonyManager mTelephonyManager;
    // 电话状态监听者
    private MyPhoneStateListener myPhoneStateListener;
    // 查询后返回的地址信息
    private String mAddress = "位置号码";
    // 展示来电归属地信息的自定义Toast
    private View mToastView;
    // 自定义来电归属地Toast中的显示地址信息的文本框
    private TextView mToastViewText;
    // 自定义Toast背景颜色的ID值
    private int[] mToastViewBackgroundId;
    // 已选定的自定义Toast的背景色的id
    private int mToastBackgroundId;
    // 动态监听去电的广播接收器
    private InnerOutCallReceiver mInnerOutCallReceiver;

    // Handler处理子线程传递过来的信息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mToastViewText.setText(mAddress);
        }
    };

    @Override
    public void onCreate() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mToastViewBackgroundId = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};
        // 动态注册广播接收者，当收到去电时，显示自定义Toast，并查询归属地信息
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mInnerOutCallReceiver, intentFilter);
        super.onCreate();
    }

    class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String outPhone = getResultData();
            showAddressView(outPhone);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // 取消来电的电话状态监听服务
        if (mTelephonyManager != null && myPhoneStateListener != null) {
            mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        // 取消去电的广播监听
        if (mInnerOutCallReceiver != null) {
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        // 重写电话状态改变时触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(tag, "响铃" + incomingNumber);
                    showAddressView(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(tag, "接听");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(tag, "挂断");
                    // 挂断电话，移除显示来电归属地信息的自定义Toast
                    if (mWindowManager != null && mToastView != null) {
                        mWindowManager.removeView(mToastView);
                        mToastView = null;
                    }
                    break;
            }
        }
    }

    /**
     * 手机窗体上显示手机来电信息
     *
     * @param incomingNumber 来电的电话号码
     */
    private void showAddressView(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // 修改完左上角对齐
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        // 加载Toast显示效果的布局文件
        mToastView = View.inflate(this, R.layout.toast_view, null);
        mToastViewText = (TextView) mToastView.findViewById(R.id.toast_show_address);

        // 取出设置中心已设置的自定义Toast的坐标位置X和Y值
        int toastLocationX = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.LOCATION_X, 0);
        int toastLocationY = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.LOCATION_Y, 0);
        params.x = toastLocationX;
        params.y = toastLocationY;
        // 自定义Toast设置触摸事件，改变所在位置
        mToastView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        // 两个方向上所移动的距离值
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.x > mWindowManager.getDefaultDisplay().getWidth() - v.getWidth()) {
                            params.x = mWindowManager.getDefaultDisplay().getWidth() - v.getWidth();
                        }

                        if (params.y > mWindowManager.getDefaultDisplay().getHeight() - 21 - v.getHeight()) {
                            params.y = mWindowManager.getDefaultDisplay().getHeight() - 21 - v.getHeight();
                        }

                        // 更新自定义Toast的坐标位置X和Y值
                        mWindowManager.updateViewLayout(mToastView, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        // 触摸事件结束时，记录当前自定义Toast的坐标位置X和Y值
                        SharePreferenceUtil.putIntToSharePreference(getApplicationContext(),
                                ConstantValues.LOCATION_X, params.x);
                        SharePreferenceUtil.putIntToSharePreference(getApplicationContext(),
                                ConstantValues.LOCATION_Y, params.y);
                        break;
                }
                return false;
            }
        });

        // 设置自定义Toast的背景色
        mToastBackgroundId = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.TOAST_STYLE, 0);
        mToastViewText.setBackgroundResource(mToastViewBackgroundId[mToastBackgroundId]);
        mWindowManager.addView(mToastView, params);
        // 获取来电后，查询来电归属地信息
        queryIncomingNumAddress(incomingNumber);
    }

    /**
     * 获取来电后，查询来电归属地信息
     *
     * @param incomingNumber 待查询地址的电话号码
     */
    private void queryIncomingNumAddress(final String incomingNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = NumberAddress.getPhoneAddress(incomingNumber);
                Log.i(tag, "mAddress:" + mAddress);
                // 发送默认信息到Handler处理
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }
}
