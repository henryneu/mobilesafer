package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class SetupFourthActivity extends BaseSetupActivity {
    // 是否已开启手机安全保护的CheckBox
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_fourth);
        // 初始化布局文件中的View
        initView();
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showNextPage() {
        boolean isChecked = SharePreferenceUtil.getBooleanFromSharePreference(this, ConstantValues.OPEN_SECURITY, false);
        if (isChecked) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SharePreferenceUtil.putBooleanToSharePreference(this, ConstantValues.SETUP_OVER, true);
        } else {
            ToastUtil.show(getApplicationContext(), "开启手机防盗功能未勾选");
        }
        // 开启平移动画效果
        overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), SetupThirdActivity.class);
        startActivity(intent);
        finish();
        // 开启平移动画效果
        overridePendingTransition(R.anim.pre_in_anim, R.anim.next_out_anim);
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        mCheckBox = (CheckBox) findViewById(R.id.security_check_box);
        boolean isChecked = SharePreferenceUtil.getBooleanFromSharePreference(this,
                ConstantValues.OPEN_SECURITY, false);
        mCheckBox.setChecked(isChecked);
        if (isChecked) {
            mCheckBox.setText("手机防盗功能已关闭");
        } else {
            mCheckBox.setText("手机防盗功能已开启");
        }
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckBox.setChecked(isChecked);
                if (isChecked) {
                    mCheckBox.setText("手机防盗功能已开启");
                } else {
                    mCheckBox.setText("手机防盗功能已关闭");
                }
                // 存储已改变后的状态值
                SharePreferenceUtil.putBooleanToSharePreference(getApplicationContext(),
                        ConstantValues.OPEN_SECURITY, isChecked);
            }
        });
    }
}
