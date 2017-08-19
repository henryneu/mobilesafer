package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class SetupThirdActivity extends AppCompatActivity {

    // 用户输入电话号码对话框
    private EditText mInputNumText;
    // 选择电话号码按钮
    private Button mSelectNumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_third);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     * @param view 布局文件中设置了onClick属性的View
     */
    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), SetupSecondActivity.class);
        startActivity(intent);
        finish();
        // 初始化布局文件中的View控件
        initView();
    }

    /**
     * 初始化布局文件中的View控件
     */
    private void initView() {
        mInputNumText = (EditText) findViewById(R.id.text_safe_num);
        mSelectNumButton = (Button) findViewById(R.id.select_safe_num);
        mSelectNumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "hello");
            }
        });
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     * @param view 布局文件中设置了onClick属性的View
     */
    public void nextPage(View view) {
        Intent intent = new Intent(getApplicationContext(), SetupFourthActivity.class);
        startActivity(intent);
        finish();
    }
}
