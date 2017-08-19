package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/18.
 */

public class SetupFirstActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_first);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     * @param view 布局文件中设置了onClick属性的View
     */
    public void nextPage(View view) {
        Intent intent = new Intent(getApplicationContext(), SetupSecondActivity.class);
        startActivity(intent);
        finish();
    }
}
