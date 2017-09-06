package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import neu.edu.cn.mobilesafer.R;

public class OptimizeClearActivity extends AppCompatActivity {

    private static final String tag = "OptimizeClearActivity";

    private static final int UPDATE_CATCH_APP = 0;
    private static final int CHECK_CACHE_APP = 1;
    private static final int CHECK_FINISH = 2;
    private static final int CLEAR_CACHE = 3;
    // 包管理者对象
    private PackageManager mPackageManager;
    // 进度条
    private ProgressBar mProgressBar;
    // 查找到的有缓存应用添加的布局
    private LinearLayout mOptimizeAppLayout;
    // 显示当前正在查询的App名字
    private TextView mScanningAppName;
    // 一键清理按钮
    private Button mClearButton;
    // 记录进度条的进度
    private int mIndex;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CATCH_APP:
                    //8.在线性布局中添加有缓存应用条目
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);

                    ImageView optimizeIcon = (ImageView) view.findViewById(R.id.app_optimize_icon);
                    TextView optimizeNma = (TextView) view.findViewById(R.id.app_optimize_name);
                    TextView optimizeMemory = (TextView) view.findViewById(R.id.app_optimize_memory);
                    ImageView optimizeDelete = (ImageView) view.findViewById(R.id.app_optimize_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    optimizeIcon.setBackgroundDrawable(cacheInfo.icon);
                    optimizeNma.setText(cacheInfo.name);
                    optimizeMemory.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
                    optimizeDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //通过查看系统日志,获取开启清理缓存activity中action和data
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + cacheInfo.packageName));
                            startActivity(intent);
                        }
                    });

                    mOptimizeAppLayout.addView(view, 0);
                    break;
                case CHECK_CACHE_APP:
                    mScanningAppName.setText((String) msg.obj);
                    break;
                case CHECK_FINISH:
                    mScanningAppName.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    // 从线性布局中移除所有的条目
                    mOptimizeAppLayout.removeAllViews();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimize_clear);
        // 初始化布局文件中的View
        initView();
        // 初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPackageManager = getPackageManager();
                // 获取安装在手机上的所有的应用
                List<PackageInfo> packageInfoLists = mPackageManager.getInstalledPackages(0);
                mProgressBar.setMax(packageInfoLists.size());
                for (PackageInfo packageInfo : packageInfoLists) {
                    // 包名作为获取缓存信息的条件
                    String packageName = packageInfo.packageName;
                    // 获取传入的包名对应的应用的缓存大小
                    getPackageCache(packageName);

                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 扫描过程中实时更新进度条
                    mIndex++;
                    mProgressBar.setProgress(mIndex);

                    //每循环一次就将检测应用的名称发送给主线程显示
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    String name = null;
                    try {
                        name = mPackageManager.getApplicationInfo(packageName, 0).
                                loadLabel(mPackageManager).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 缓存信息类
     */
    class CacheInfo {
        public String name;
        public Drawable icon;
        public String packageName;
        public long cacheSize;
    }

    /**
     * 获取传入的包名对应的应用的缓存大小
     *
     * @param packageName 待获取缓存大小的应用的包名
     */
    private void getPackageCache(String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
                // 获取指定包名的缓存大小
                long cacheSize = stats.cacheSize;
                Log.i(tag, "======cacheSize=" + cacheSize);
                if (cacheSize > 0) {
                    // 消息机制,告知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CATCH_APP;
                    CacheInfo cacheInfo = null;
                    try {
                        cacheInfo = new CacheInfo();
                        cacheInfo.cacheSize = cacheSize;
                        cacheInfo.packageName = stats.packageName;
                        cacheInfo.name = mPackageManager.getApplicationInfo(stats.packageName, 0).
                                loadLabel(mPackageManager).toString();
                        cacheInfo.icon = mPackageManager.getApplicationInfo(stats.packageName, 0).
                                loadIcon(mPackageManager);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPackageManager, packageName, mStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.optimize_clear_bar);
        mOptimizeAppLayout = (LinearLayout) findViewById(R.id.linear_optimize_add_text);
        mScanningAppName = (TextView) findViewById(R.id.scanning_text_view);
        mClearButton = (Button) findViewById(R.id.optimize_clear_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取指定类的字节码文件
                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    //2.获取调用方法对象
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    //3.获取对象调用方法
                    method.invoke(mPackageManager, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded)
                                throws RemoteException {
                            // 清除缓存完成后调用的方法(考虑权限)
                            Message msg = Message.obtain();
                            msg.what = CLEAR_CACHE;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
