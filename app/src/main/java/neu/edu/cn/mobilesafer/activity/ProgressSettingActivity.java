package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.service.LockPhoneService;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.SmsServiceUtil;

public class ProgressSettingActivity extends AppCompatActivity {
    // 显示或隐藏系统进程
    private CheckBox mHideSystemBox;
    // 开启或关闭锁屏清理
    private CheckBox mLockPhoneClearBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_setting);
        // 初始化布局文件中的显示或隐藏系统进程的view
        initShowSystemView();
        // 初始化布局文件中的开启或关闭锁屏清理进程的view
        initLockPhoneClearView();
    }

    /**
     * 初始化布局文件中的显示或隐藏系统进程的view
     */
    private void initShowSystemView() {
        // 显示或隐藏系统进程的回显
        boolean isShowSystemProcess = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                ConstantValues.SHOW_SYSTEM_PROCESS, false);
        mHideSystemBox = (CheckBox) findViewById(R.id.hide_system_process);
        mHideSystemBox.setChecked(isShowSystemProcess);
        if (isShowSystemProcess) {
            mHideSystemBox.setText("显示系统进程");
        } else {
            mHideSystemBox.setText("隐藏系统进程");
        }
        mLockPhoneClearBox = (CheckBox) findViewById(R.id.lock_phone_clear_process);
        mHideSystemBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mHideSystemBox.setText("显示系统进程");
                } else {
                    mHideSystemBox.setText("隐藏系统进程");
                }
                SharePreferenceUtil.putBooleanToSharePreference(getApplicationContext(),
                        ConstantValues.SHOW_SYSTEM_PROCESS, isChecked);
            }
        });
    }

    /**
     * 初始化布局文件中的开启或关闭锁屏清理进程的view
     */
    private void initLockPhoneClearView() {
        mLockPhoneClearBox = (CheckBox) findViewById(R.id.lock_phone_clear_process);
        // 开启或关闭锁屏清理进程的回显
        boolean isOpenClearService = SmsServiceUtil.isRunning(getApplicationContext(),
                "neu.edu.cn.mobilesafer.service.LockPhoneService");
        mLockPhoneClearBox.setChecked(isOpenClearService);
        if (isOpenClearService) {
            mLockPhoneClearBox.setText("锁屏清理已开启");
        } else {
            mLockPhoneClearBox.setText("锁屏清理已关闭");
        }
        mLockPhoneClearBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLockPhoneClearBox.setText("锁屏清理已开启");
                    startService(new Intent(ProgressSettingActivity.this, LockPhoneService.class));
                } else {
                    mLockPhoneClearBox.setText("锁屏清理已关闭");
                    stopService(new Intent(ProgressSettingActivity.this, LockPhoneService.class));
                }
            }
        });
    }
}
