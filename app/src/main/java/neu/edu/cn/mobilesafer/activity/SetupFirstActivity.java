package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/18.
 */

public class SetupFirstActivity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_first);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     */
    @Override
    public void showNextPage() {
        Intent intent = new Intent(getApplicationContext(), SetupSecondActivity.class);
        startActivity(intent);
        finish();
        // 开启平移动画效果
        overridePendingTransition(R.anim.next_in_anim, R.anim.pre_out_anim);
    }

    @Override
    public void showPrePage() {
        // 空实现，不需要跳转到上一步
    }
}
