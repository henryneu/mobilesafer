package neu.edu.cn.mobilesafer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.ui.SettingsItem;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class SetupSecondActivity extends AppCompatActivity {

    // 是否绑定手机SIM卡的设置选项
    private SettingsItem mSettingsItemBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_second);
        // 初始化布局文件中View控件
        initView();
    }

    /**
     * 初始化布局文件中View控件
     */
    private void initView() {
        mSettingsItemBind = (SettingsItem) findViewById(R.id.settings_item_bind);
        // 检查是否已存储了SIM卡序列号
        String simNumber = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                ConstantValues.SIM_NUMBER, "");
        // 判断序列号是否为null
        if (TextUtils.isEmpty(simNumber)) {
            // 若为null mSettingsItemBind状态设置为false
            mSettingsItemBind.setCheck(false);
        } else {
            // 否则mSettingsItemBind状态设置为true
            mSettingsItemBind.setCheck(true);
        }
        mSettingsItemBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取mSettingsItemBind的原有状态
                boolean isChecked = mSettingsItemBind.IsCheck();
                // 点击后状态值取反
                mSettingsItemBind.setCheck(!isChecked);
                if (!isChecked) {
                    // 如果是已绑定状态，则将SIM卡序列号存储到SharedPreferences中
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    // 获取SIM卡序列号，并存储到SharedPreferences中
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    SharePreferenceUtil.putStringToSharePreference(getApplicationContext(),
                            ConstantValues.SIM_NUMBER, simSerialNumber);
                } else {
                    // 若是未绑定状态，则从SharedPreferences中移除相应的Key值所对应的结点
                    SharePreferenceUtil.removeStringFromSharePreference(getApplicationContext(), ConstantValues.SIM_NUMBER);
                }
            }
        });
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     *
     * @param view 布局文件中设置了onClick属性的View
     */
    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), SetupFirstActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     *
     * @param view 布局文件中设置了onClick属性的View
     */
    public void nextPage(View view) {
        String simSerialNumber = SharePreferenceUtil.getStringFromSharePreference(getApplicationContext(),
                ConstantValues.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simSerialNumber)) {
            // 若从SharedPreferences中取出的SIM卡序列号不为空，则可以跳转到下一步
            Intent intent = new Intent(getApplicationContext(), SetupThirdActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 否者提示用户异常
            ToastUtil.show(getApplicationContext(), "必须先绑定SIM卡序列号");
        }
    }
}
