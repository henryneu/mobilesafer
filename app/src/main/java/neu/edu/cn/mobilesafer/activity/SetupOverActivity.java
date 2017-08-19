package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

/**
 * Created by neuHenry on 2017/8/18.
 */

public class SetupOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setupOver = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                ConstantValues.SETUP_OVER, false);
        if (setupOver) {
            setContentView(R.layout.activity_setup_over);
        } else {
            Intent intent = new Intent(getApplicationContext(), SetupFirstActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
