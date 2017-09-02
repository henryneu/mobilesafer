package neu.edu.cn.mobilesafer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.SmsBackUp;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class ToolActivity extends AppCompatActivity {

    // 短信备份的进度条
    // private ProgressBar progressBar;

    // 消息传递机制
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 子线程中备份完成时，弹出备份完成
            ToastUtil.show(getApplicationContext(), "短信备份完成");
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        // 初始化归属地itemView
        initQueryAddressView();
        // 初始化短信备份itemView
        initBackUpSmsView();
    }

    /**
     * 初始化短信备份itemView
     */
    private void initBackUpSmsView() {
        TextView smsBackUp = (TextView) findViewById(R.id.phone_sms_back_up);
        // progressBar = (ProgressBar) findViewById(R.id.back_up_sms_progress);
        smsBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示短信备份的进度对话框
                showSmsBackUpDialog();
            }
        });
    }

    /**
     * 显示短信备份的进度对话框
     */
    private void showSmsBackUpDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("短信备份");
        dialog.setIcon(R.drawable.settings);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "mobilesafe.xml";
                SmsBackUp.backup(getApplicationContext(), filePath, new SmsBackUp.ProgressStyleCallBack() {
                    @Override
                    public void setMax(int value) {
                        dialog.setMax(value);
                        // progressBar.setMax(value);
                    }

                    @Override
                    public void setProgress(int index) {
                        dialog.setProgress(index);
                        // progressBar.setProgress(index);
                    }
                });
                dialog.dismiss();
                // 子线程不能修改主线程中的UI，传递空消息，由主线程完成UI的改变,弹出短信备份完成
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化归属地itemView
     */
    private void initQueryAddressView() {
        TextView phoneNumAddressQuery = (TextView) findViewById(R.id.phone_num_query);
        phoneNumAddressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolActivity.this, PhoneNumQueryActivity.class);
                startActivity(intent);
            }
        });
    }
}
