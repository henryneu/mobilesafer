package neu.edu.cn.mobilesafer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/14.
 */

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 初始化Activity中的视图
        initView();
        // 初始化数据
        initDate();
    }

    /**
     * 初始化Activity中的视图
     */
    private void initView() {

    }

    /**
     * 初始化数据
     */
    private void initDate() {

    }
}
