package neu.edu.cn.mobilesafer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.SmsBackUp;

public class ToolActivity extends AppCompatActivity {

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
                SmsBackUp.backup(getApplicationContext(), filePath, dialog);
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
