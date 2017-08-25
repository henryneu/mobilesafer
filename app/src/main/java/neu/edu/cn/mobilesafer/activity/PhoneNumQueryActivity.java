package neu.edu.cn.mobilesafer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.NumberAddress;

public class PhoneNumQueryActivity extends AppCompatActivity {
    // 输入查询号码的文本编辑框
    private EditText inputPhoneNumView;
    // 显示查询到的电话号码地址
    private TextView showPhoneNumAddressView;
    // 查询电话号码地址按钮
    private Button queryPhoneNumButton;
    // 待查询的电话号码信息
    private String phoneNumber = "";
    // 查询返回的电话号码地址信息
    private String phoneNumAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_query);
        // phoneNumAddress = NumberAddress.getPhoneAddress("13000201234");
        // phoneNumAddress = NumberAddress.getPhoneAddress("039612345678");
        // 初始化布局文件中的View
        initView();
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        inputPhoneNumView = (EditText) findViewById(R.id.input_query_phone_number);
        phoneNumber = inputPhoneNumView.getText().toString();
        showPhoneNumAddressView = (TextView) findViewById(R.id.show_phone_address_info);
        queryPhoneNumButton = (Button) findViewById(R.id.query_phone_num_btn);
        queryPhoneNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumAddress = NumberAddress.getPhoneAddress(phoneNumber);
                showPhoneNumAddressView.setText("归属地：" + phoneNumAddress);
            }
        });
    }
}
