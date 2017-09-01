package neu.edu.cn.mobilesafer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.service.AddressService;
import neu.edu.cn.mobilesafer.service.InterceptService;
import neu.edu.cn.mobilesafer.ui.SettingsClickItem;
import neu.edu.cn.mobilesafer.ui.SettingsItem;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.SmsServiceUtil;

public class SettingsActivity extends AppCompatActivity {

    private static final String tag = "SettingsActivity";

    // 设置是否开启自动更新
    private SettingsItem mSettingsItemUpdate;
    // 设置是否开启电话归属地
    private SettingsItem mSettingsItemAttribution;
    // 设置Toast背景样式选择
    private SettingsClickItem mSettingsClickItem;
    // 设置Toast所在位置
    private SettingsClickItem mToastLocationClickItem;
    // 设置是否开启黑名单拦截
    private SettingsItem mSettingsItemIntercept;

    private final String[] desItems = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // 初始化更新应用选项视图
        initUpdateItemView();
        // 初始化来点归属地选项视图
        initAttributionItemView();
        // 初始化Toast样式背景选择视图
        initToastStyleItemView();
        // 初始化自定义Toast的位置拖拽视图
        initToastLocationItemView();
        // 初始化黑名单拦截选项视图
        initInterceptBlackItemView();
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
     * 初始化来电归属地选项视图
     */
    private void initAttributionItemView() {
        // 是否打开来电归属地开关的Item
        mSettingsItemAttribution = (SettingsItem) findViewById(R.id.settings_item_attribution);
        // 查看所给服务是否正在运行
        boolean isRunning = SmsServiceUtil.isRunning(this, "neu.edu.cn.mobilesafer.service.AddressService");
        Log.i(tag, "isRunning:" + isRunning);
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

    /**
     * 初始化Toast样式背景选择视图
     */
    private void initToastStyleItemView() {
        // 设置Toast背景样式选择
        mSettingsClickItem = (SettingsClickItem) findViewById(R.id.settings_item_toast_style);
        mSettingsClickItem.setClickItemTitle("设置归属地提示框显示风格");
        int toastStyleId = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.TOAST_STYLE, 0);
        mSettingsClickItem.setClickItemDes(desItems[toastStyleId]);
        mSettingsClickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setIcon(R.drawable.settings);
                builder.setTitle("归属地提示框背景风格");
                int toastStyleId = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                        ConstantValues.TOAST_STYLE, 0);
                builder.setSingleChoiceItems(desItems, toastStyleId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharePreferenceUtil.putIntToSharePreference(getApplicationContext(),
                                ConstantValues.TOAST_STYLE, which);
                        mSettingsClickItem.setClickItemDes(desItems[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    /**
     * 初始化自定义Toast的位置拖拽视图
     */
    private void initToastLocationItemView() {
        mToastLocationClickItem = (SettingsClickItem) findViewById(R.id.settings_item_toast_location);
        mToastLocationClickItem.setClickItemTitle("归属地提示框的位置");
        mToastLocationClickItem.setClickItemDes("设置归属地提示框的位置");
        mToastLocationClickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ToastLocationActivity.class));
            }
        });
    }

    /**
     * 初始化黑名单拦截选项视图
     */
    private void initInterceptBlackItemView() {
        // 是否打开黑名单拦截开关的Item
        mSettingsItemIntercept = (SettingsItem) findViewById(R.id.settings_item_intercept);
        // 查看所给服务是否正在运行
        boolean isRunning = SmsServiceUtil.isRunning(this, "neu.edu.cn.mobilesafer.service.InterceptService");
        Log.i(tag, "isRunning:" + isRunning);
        mSettingsItemIntercept.setCheck(isRunning);
        mSettingsItemIntercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSettingsItemIntercept.IsCheck();
                mSettingsItemIntercept.setCheck(!isCheck);
                if (isCheck) {
                    stopService(new Intent(SettingsActivity.this, InterceptService.class));
                } else {
                    startService(new Intent(SettingsActivity.this, InterceptService.class));
                }
            }
        });
    }
}
