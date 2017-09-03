package neu.edu.cn.mobilesafer.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.StreamUtil;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class SplashActivity extends AppCompatActivity {

    private static final String tag = "SplashActivity";
    // 应用版本需要升级时发送消息的标志
    private static final int UPDATE_MSG = 100;
    // 不需要升级应用版本直接进入应用主界面是发送消息的标志
    private static final int HOME_MSG = 101;
    // Url地址异常时所发出消息的标志位
    private static final int URL_EXC_MSG = 102;
    // IO异常时所发出消息的标志位
    private static final int IO_EXC_MSG = 103;
    // JSON解析异常时所发出消息的标志位
    private static final int JSON_EXC_MSG = 104;
    // SplashActivity界面的根布局
    private RelativeLayout mRelativeLayout;
    // 文本框：用于显示版本名称
    private TextView mTextView;

    private PackageManager mPackageManager;
    // 本地安装的应用版本的版本号
    private int mLocalVersionCode;
    // 服务器端应用版本信息更新后的版本号
    private int mServerVersionCode;
    // 服务器端应用版本信息更新的描述
    private String mVersionDes;
    // 服务器端应用版本信息更新后apk的下载地址
    private String mDownloadUrl;
    // 用于显示下载进度的进度条
    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_MSG:
                    // 弹出对话框，提示用户应用版本更新
                    updateApplicationDialog();
                    break;
                case HOME_MSG:
                    // 进入应用程序的主界面
                    enterHomeActivity();
                    break;
                case URL_EXC_MSG:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHomeActivity();
                    break;
                case IO_EXC_MSG:
                    ToastUtil.show(getApplicationContext(), "io读取异常");
                    enterHomeActivity();
                    break;
                case JSON_EXC_MSG:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHomeActivity();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 初始化视图View
        initView();
        // 初始化数据Data
        initData();
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        mRelativeLayout.startAnimation(animation);
        // 初始化数据库
        initDB();

        // 初始时生成快捷方式
        initShortCut();
    }

    /**
     * 初始时生成快捷方式
     */
    private void initShortCut() {
        // 创建一个意图，带有创建快捷方式的action
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式中要用到的名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机安全卫士");
        // 快捷方式中要用到的图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_my));
        // 创建快捷方式启动时用到的intent
        Intent shortCutIntent = new Intent("android.intent.action.Home");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        sendBroadcast(intent);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        // 初始化来电归属地数据库,并将数据库拷贝到应用Files下
        initAddressDB("address.db");
        // 初始化常用号码数据库,并将数据库拷贝到应用Files下
        initAddressDB("commonnum.db");
    }

    /**
     * 初始化来点归属地数据库,并将数据库拷贝到应用Files下
     *
     * @param databaseName 数据库的名字
     */
    private void initAddressDB(String databaseName) {
        // 获取应用所在的文件路径
        File filePath = getFilesDir();
        // 创建一个新的文件
        File file = new File(filePath, databaseName);
        // 文件若已存在，则退出数据库的拷贝过程
        if (file.exists() && file.length() > 0) {
            return;
        } else {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            try {
                // 打开assets文件目录下的数据库文件
                inputStream = getAssets().open(databaseName);
                fos = new FileOutputStream(file);
                byte[] byteBuffer = new byte[1024];
                int temp = -1;
                while ((temp = inputStream.read(byteBuffer)) != -1) {
                    // 读取到的数据流写入到指定的文件中
                    Log.i(tag, "temp = " + temp);
                    fos.write(byteBuffer, 0, temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null && fos != null) {
                    try {
                        inputStream.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 初始化数据Data
     */
    private void initData() {
        // 获取当前应用版本的版本名称
        mTextView.setText("版本名称：" + getVersionName());
        // 获取本地的当前版本的版本号
        mLocalVersionCode = getVersionCode();
        // 检测服务器端应用版本最新的版本号
        if (SharePreferenceUtil.getBooleanFromSharePreference(this, ConstantValues.OPEN_UPDATE, false)) {
            // 如果已选中自动更新，则向服务器端检测版本号
            checkVersion();
        } else {
            // 如果没有选中，则延迟4s后跳转到应用主界面
            mHandler.sendEmptyMessageDelayed(UPDATE_MSG, 4000);
        }
    }

    /**
     * 进入应用程序的主界面
     */
    private void enterHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 弹出对话框，提示用户应用版本更新
     */
    private void updateApplicationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("更新提醒");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载服务器端更新后最新的apk
                downloadUpdateApk();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 暂不更新，跳转到应用程序主界面
                dialog.dismiss();
                enterHomeActivity();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 取消更新，跳转到应用程序主界面
                dialog.dismiss();
                enterHomeActivity();
            }
        });
        builder.show();
    }

    /**
     * 下载服务器端更新后最新的apk
     */
    private void downloadUpdateApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mProgressDialog = new ProgressDialog(SplashActivity.this);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            RequestParams requestParams = new RequestParams(mDownloadUrl);
            // 为RequestParams设置文件下载后的保存路径
            requestParams.setSaveFilePath(path);
            // 下载完成后自动为文件命名
            requestParams.setAutoRename(true);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {

                @Override
                public void onSuccess(File result) {
                    Log.i(tag, "下载成功");
                    ToastUtil.show(SplashActivity.this, "下载成功");
                    mProgressDialog.dismiss();
                    // 下载完成后，安装新版本的apk
                    installApk(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(tag, "下载失败");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(tag, "取消下载");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onFinished() {
                    Log.i(tag, "结束下载");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onWaiting() {
                    // 网络请求开始的时候调用
                    Log.i(tag, "等待下载");
                }

                @Override
                public void onStarted() {
                    // 下载的时候不断回调的方法
                    Log.i(tag, "开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    // 当前的下载进度和文件总大小
                    Log.i(tag, "正在下载中......");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMessage("正在下载中......");
                    mProgressDialog.show();
                    mProgressDialog.setMax((int) total);
                    mProgressDialog.setProgress((int) current);
                }
            });
        }
    }

    /**
     * 下载完成后，安装新版本的apk
     *
     * @param file 服务器端下载的安装文件
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHomeActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取当前版本的版本名称
     *
     * @return 返回值为空，则获取失败，否则返回当前版本的版本名称
     */
    private String getVersionName() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前版本的版本号
     *
     * @return 返回值为0，则获取失败，否则返回当前版本的版本号
     */
    private int getVersionCode() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 检查服务器端版本的版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                try {
                    //URL url = new URL("http://10.0.2.2:8080/update.json");
                    URL url = new URL("http://113.225.219.41:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String result = StreamUtil.streamToString(is);
                        Log.i(tag, result);
                        JSONObject jsonObject = new JSONObject(result);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        mServerVersionCode = Integer.parseInt(versionCode);
                        if (mServerVersionCode > mLocalVersionCode) {
                            // 如果服务器端获取的应用版本号大于本地应用版本号，则弹出对话框提醒用户更新
                            msg.what = UPDATE_MSG;
                        } else {
                            // 否则直接进入应用主界面
                            msg.what = HOME_MSG;
                        }
                        Log.i(tag, versionName);
                        Log.i(tag, mVersionDes);
                        Log.i(tag, versionCode);
                        Log.i(tag, mDownloadUrl);
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_EXC_MSG;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = IO_EXC_MSG;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_EXC_MSG;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 初始化视图View
     */
    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        mTextView = (TextView) findViewById(R.id.text_version_name);
    }
}
