package neu.edu.cn.mobilesafer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/16.
 */

public class SettingsItem extends RelativeLayout {

    private TextView mTextTitle;

    private TextView mTextDes;

    private CheckBox mCheckBox;

    public SettingsItem(Context context) {
        this(context, null);
    }

    public SettingsItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.settings_item, this);
        // 初始化View
        initView();
    }

    /**
     * 检查CheckBox的状态是否是选中状态，选中则开启，否则关闭
     * @return
     */
    public boolean IsCheck() {
        return mCheckBox.isChecked();
    }

    /**
     * 如果传递的是true，则设置CheckBox为选中，否则为没选中，同时改变文本描述
     * @param isCheck 是否作为开启的变量，由点击过程中传递
     */
    public void setCheck(boolean isCheck) {
        mCheckBox.setChecked(isCheck);
        if (isCheck) {
            // 开启
            mTextDes.setText("自动跟新已开启");
        } else {
            // 关闭
            mTextDes.setText("自动跟新已关闭");
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mTextTitle = (TextView) findViewById(R.id.text_des);
        mTextDes = (TextView) findViewById(R.id.text_des);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
    }
}
