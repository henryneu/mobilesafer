package neu.edu.cn.mobilesafer.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.StreamUtil;

public class SplashActivity extends AppCompatActivity {

    public static final String tag = "SplashActivity";

    // 文本框：用于显示版本名称
    private TextView mTextView;

    private PackageManager mPackageManager;

    private int mLocalVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 初始化视图View
        initView();
        // 初始化数据Data
        initData();
    }

    /**
     * 初始化数据Data
     */
    private void initData() {
        // 获取当前应用版本的版本名称
        mTextView.setText("版本名称：" + getVersionName());
        // 获取本地的当前版本的版本号
        mLocalVersionCode = getVersionCode();
        // 获取服务器端应用版本最新的版本号
//        getVersionCodeFromServer();
        checkVersion();
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
     * 获取服务器端应用版本最新的版本号
     *
     * @return 返回值为0，则获取失败，否则返回服务器端最新版本的版本号
     */
//    private int getVersionCodeFromServer() {
//        checkVersion();
//    }

    /**
     * 检查服务器端版本的版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //URL url = new URL("http://10.0.2.2:8080/update.json");
                    URL url = new URL("http://113.225.216.97:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String result = StreamUtil.streamToString(is);
                        Log.i(tag, result);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 初始化视图View
     */
    private void initView() {
        mTextView = (TextView) findViewById(R.id.text_version_name);
    }
}
