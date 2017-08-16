package neu.edu.cn.mobilesafer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.ui.SettingsItem;

public class SettingsActivity extends AppCompatActivity {

    private SettingsItem mSettingsItem;

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
        mSettingsItem = (SettingsItem) findViewById(R.id.settings_item_update);
        mSettingsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSettingsItem.IsCheck();
                mSettingsItem.setCheck(!isCheck);
            }
        });
    }
}
