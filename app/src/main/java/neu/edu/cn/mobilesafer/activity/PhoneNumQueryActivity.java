package neu.edu.cn.mobilesafer.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.NumberAddress;

public class PhoneNumQueryActivity extends AppCompatActivity {
    private static final String tag = "PhoneNumQueryActivity";

    // 输入查询号码的文本编辑框
    private EditText inputPhoneNumView;
    // 显示查询到的电话号码地址
    private TextView showPhoneNumAddressView;
    // 查询电话号码地址按钮
    private Button queryPhoneNumButton;
    // 待查询的电话号码信息
    private String phoneNumber = "";
    // 查询返回的电话号码地址信息
    private String phoneNumAddress = "未知号码";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showPhoneNumAddressView.setText("归属地：" + phoneNumAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_query);
        // 测试代码
        // phoneNumAddress = NumberAddress.getPhoneAddress("13000201234");
        // phoneNumAddress = NumberAddress.getPhoneAddress("039612345678");
        // 初始化布局文件中的View
        initView();
        queryPhoneNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = inputPhoneNumView.getText().toString().trim();
                Log.i(tag, "phoneNumber:" + phoneNumber);
                if (TextUtils.isEmpty(phoneNumber)) {
                    Animation shakeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_view);
                    inputPhoneNumView.startAnimation(shakeAnim);
                    // 启动手机震动效果
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // 震动两秒
                    vibrator.vibrate(2000);
                    // 有规律的震动
                    vibrator.vibrate(new long[]{2000,2000,2000,2000}, -1);
                    showPhoneNumAddressView.setText("归属地:" + "");
                } else {
                    // 查询电话号码的号码归属地
                    query(phoneNumber);
                }
            }
        });
        inputPhoneNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNum = s.toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    showPhoneNumAddressView.setText("归属地:" + "");
                } else {
                    query(phoneNum);
                }
            }
        });
    }

    /**
     * 查询电话号码的号码归属地
     *
     * @param phoneNum 待查询的电话号码
     */
    private void query(final String phoneNum) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                phoneNumAddress = NumberAddress.getPhoneAddress(phoneNum);
                Log.i(tag, "phoneNumAddress:" + phoneNumAddress);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        inputPhoneNumView = (EditText) findViewById(R.id.input_query_phone_number);
        showPhoneNumAddressView = (TextView) findViewById(R.id.show_phone_address_info);
        queryPhoneNumButton = (Button) findViewById(R.id.query_phone_num_btn);
    }
}
