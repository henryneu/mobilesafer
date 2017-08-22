package neu.edu.cn.mobilesafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

public class RootReceiver extends BroadcastReceiver {

    private static final String tag = "RootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "重启手机成功");
        // 首先开机后获取手机SIM卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSimSerialnum = tm.getSimSerialNumber() + "xxx";
        // 然后获取已存储的原来的SIM卡序列号
        String savedSimSerialNum = SharePreferenceUtil.getStringFromSharePreference(context, ConstantValues.SIM_NUMBER, "");
        if (!simSimSerialnum.equals(savedSimSerialNum)) {
            // 不相同则向已绑定的手机安全号码发送报警短信
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5556", null, "sim changed", null, null);
        }
    }
}
