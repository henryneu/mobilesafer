package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.service.AddressService;
import neu.edu.cn.mobilesafer.ui.SettingsItem;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.SmsServiceUtil;

public class SettingsActivity extends AppCompatActivity {

    // 设置是否开启自动更新
    private SettingsItem mSettingsItemUpdate;
    // 设置是否开启电话归属地
    private SettingsItem mSettingsItemAttribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // 初始化更新应用选项视图
        initUpdateItemView();
        // 初始化来点归属地选项视图
        initAttributionItemView();
    }

    /**
     * 初始化更新应用选项视图
     */
    private void initUpdateItemView() {
        // 是否打开版本更新开关的Item
        mSettingsItemUpdate = (SettingsItem) findViewById(R.id.settings_item_update);
        mSettingsItemUpdate.setCheck(SharePreferenceUtil.getBooleanFromSharePreference(SettingsActivity.this,
                ConstantValues.OPEN_UPDATE, false));
        mSettingsItemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSettingsItemUpdate.IsCheck();
                if (isCheck) {
                    // 如果已经被选中，点击后则为取消，状态设置为false，并将相应的状态值存入SharedPreferences中
                    mSettingsItemUpdate.setCheck(false);
                    SharePreferenceUtil.putBooleanToSharePreference(SettingsActivity.this, ConstantValues.OPEN_UPDATE, false);
                } else {
                    // 如果为被选中，点击后则为选中，状态设置为true，并将相应的状态值存入SharedPreferences中
                    mSettingsItemUpdate.setCheck(true);
                    SharePreferenceUtil.putBooleanToSharePreference(SettingsActivity.this, ConstantValues.OPEN_UPDATE, true);
                }
            }
        });
    }

    /**
     * 初始化来点归属地选项视图
     */
    private void initAttributionItemView() {
        // 是否打开来电归属地开关的Item
        mSettingsItemAttribution = (SettingsItem) findViewById(R.id.settings_item_attribution);
        // 查看所给服务是否正在运行
        boolean isRunning = SmsServiceUtil.isRunning(this, "neu.edu.cn.mobilesafer.service.AddressService");
        mSettingsItemAttribution.setCheck(isRunning);
        mSettingsItemAttribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSettingsItemAttribution.IsCheck();
                mSettingsItemAttribution.setCheck(!isCheck);
                if (isCheck) {
                    stopService(new Intent(SettingsActivity.this, AddressService.class));
                } else {
                    startService(new Intent(SettingsActivity.this, AddressService.class));
                }
            }
        });
    }
}
