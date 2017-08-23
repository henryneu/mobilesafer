package neu.edu.cn.mobilesafer.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.service.LocationService;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDPM;

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
        // 获取设备的管理者对象
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        // 判断是否开启防盗保护
        boolean isOpenedSecurity = SharePreferenceUtil.getBooleanFromSharePreference(context, ConstantValues.OPEN_SECURITY, false);
        if (isOpenedSecurity) {
            // 获取接收短信的内容
            Object[] messages = (Object[]) intent.getExtras().get("pdus");
            // 循环遍历获取到的短信内容
            for (Object object : messages) {
                // 获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                // 获取短信对象的基本信息
                String messageAdress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                // 判断是否包含播放音乐的关键字
                if (messageBody.contains("#*alarm*#")) {
                    // 播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                // 判断是否包含发送位置信息的关键字
                if (messageBody.contains("#*location*#")) {
                    // 发送位置信息
                    Intent intentService = new Intent(context, LocationService.class);
                    context.startService(intentService);
                }
                // 判断是否包含启动远程锁屏的关键字
                if (messageBody.contains("#*lockscreen*#")) {
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        // 远程锁屏
                        mDPM.lockNow();
                        mDPM.resetPassword("123", 0);
                    }
                }
                // 判断是否包含启动数据销毁的关键字
                if (messageBody.contains("#*wipedata*#")) {
                    // 数据销毁
                    mDPM.wipeData(0);
                }
            }
        }
    }
}
