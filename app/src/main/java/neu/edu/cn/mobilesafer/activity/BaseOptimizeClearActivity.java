package neu.edu.cn.mobilesafer.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import neu.edu.cn.mobilesafer.R;

public class BaseOptimizeClearActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_optimize_clear);

        View view1 = View.inflate(this, R.layout.tab_host_opti, null);
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_catch").setIndicator(view1);

        View view2 = View.inflate(this, R.layout.tab_host_sd, null);
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("clear_sd_catch").setIndicator(view2);
        // 告知点中选项卡后续操作
        tab1.setContent(new Intent(this, OptimizeClearActivity.class));
        tab2.setContent(new Intent(this, SDCardClearActivity.class));
        // 将两个选项卡维护到host(选项卡宿主)中去
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
