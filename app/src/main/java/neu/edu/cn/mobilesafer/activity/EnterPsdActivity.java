package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class EnterPsdActivity extends AppCompatActivity {
    // 拦截界面应用图标
    private ImageView mIconImage;
    // 拦截界面应用名称
    private TextView mLockAppNameText;
    // 拦截界面输入密码编辑框
    private EditText mEnterPsdText;
    // 拦截界面确认密码按钮
    private Button mConfirmButton;
    // 获取要开启的应用的包名
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_psd);
        mPackageName = getIntent().getStringExtra("packagename");
        // 初始化布局文件中的view
        initView();
        // 初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        PackageManager packageManager = getPackageManager();
        try {
            // 获取准备开启的应用的信息
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mPackageName, 0);
            mIconImage.setBackgroundDrawable(applicationInfo.loadIcon(packageManager));
            mLockAppNameText.setText(applicationInfo.loadLabel(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passWord = mEnterPsdText.getText().toString();
                if (!TextUtils.isEmpty(passWord)) {
                    if ("123456".equals(passWord)) {
                        // 密码匹配,则打开当前应用,告知看门狗不要再去监听已经解锁的应用,发送广播
                        Intent intent = new Intent("android.intent.action.SKIP");
                        intent.putExtra("packagename", mPackageName);
                        sendBroadcast(intent);
                        finish();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误,请重新输入");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    /**
     * 初始化布局文件中的view
     */
    private void initView() {
        mIconImage = (ImageView) findViewById(R.id.confirm_lock_icon);
        mLockAppNameText = (TextView) findViewById(R.id.confirm_lock_app_name);
        mEnterPsdText = (EditText) findViewById(R.id.enter_psd_text);
        mConfirmButton = (Button) findViewById(R.id.confirm_lock_psd);
    }

    @Override
    public void onBackPressed() {
        // 重写当前Activity的回退按钮方法,当用户点击回退时跳转到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
