package neu.edu.cn.mobilesafer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import neu.edu.cn.mobilesafer.engine.ProgressInfoProvider;

public class LockPhoneService extends Service {

    private InnerPhoneLockReceiver innerPhoneLockReceiver;

    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerPhoneLockReceiver = new InnerPhoneLockReceiver();
        registerReceiver(innerPhoneLockReceiver, intentFilter);
        super.onCreate();
    }

    class InnerPhoneLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressInfoProvider.killAll(context);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (innerPhoneLockReceiver != null) {
            unregisterReceiver(innerPhoneLockReceiver);
        }
        super.onDestroy();
    }
}
