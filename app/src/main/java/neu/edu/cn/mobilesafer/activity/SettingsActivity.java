package neu.edu.cn.mobilesafer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.ui.SettingsItem;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

public class SettingsActivity extends AppCompatActivity {

    // 设置是否开启自动更新
    private SettingsItem mSettingsItemUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // 初始化视图
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
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
}
