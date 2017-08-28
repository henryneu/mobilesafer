package neu.edu.cn.mobilesafer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/27.
 */

public class SettingsClickItem extends RelativeLayout {

    private static final String tag = "SettingsItem";
    // 自定义SettingsItem中title文本框
    private TextView mTextTitle;
    // 自定义SettingsItem中描述信息文本框
    private TextView mTextDes;

    public SettingsClickItem(Context context) {
        this(context, null);
    }

    public SettingsClickItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsClickItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.settings_click_item, this);
        // 初始化View
        initView();
    }

    /**
     * 设置样式选择Item的title
     * @param title 设置的title名
     */
    public void setClickItemTitle(String title) {
        mTextTitle.setText(title);
    }

    /**
     * 设置样式选择Item的des
     * @param des 设置的des描述
     */
    public void setClickItemDes(String des) {
        mTextDes.setText(des);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mTextTitle = (TextView) findViewById(R.id.click_item_title);
        mTextDes = (TextView) findViewById(R.id.click_item_des);
    }
}
