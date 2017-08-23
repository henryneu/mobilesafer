package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;

public class ToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        // 初始化归属地相关的View
        initView();
    }

    /**
     * 初始化归属地相关的View
     */
    private void initView() {
        TextView phoneNumQuery = (TextView) findViewById(R.id.phone_num_query);
        phoneNumQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolActivity.this, PhoneNumQueryActivity.class);
                startActivity(intent);
            }
        });
    }
}
