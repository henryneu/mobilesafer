package neu.edu.cn.mobilesafer;

import android.app.Application;

import org.xutils.x;

/**
 * Created by neuHenry on 2017/8/15.
 */

public class MyMobileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
