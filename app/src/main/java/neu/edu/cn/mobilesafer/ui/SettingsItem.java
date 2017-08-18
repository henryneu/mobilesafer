package neu.edu.cn.mobilesafer.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/16.
 */

public class SettingsItem extends RelativeLayout {

    private static final String tag = "SettingsItem";
    // 自定义SettingsItem的命名空间
    private static final String namespace = "http://schemas.android.com/apk/res/neu.edu.cn.mobilesafer";
    // 自定义SettingsItem中title文本框
    private TextView mTextTitle;
    // 自定义SettingsItem中描述信息文本框
    private TextView mTextDes;
    // 自定义SettingsItem中CheckBox控件
    private CheckBox mCheckBox;
    // title文本框中显示的标题信息
    private String mTitle;
    // 描述信息文本框中显示的描述信息（关）
    private String mDesoff;
    // 描述信息文本框中显示的描述信息（开）
    private String mDeson;

    public SettingsItem(Context context) {
        this(context, null);
    }

    public SettingsItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.settings_item, this);
        // 初始化自定义属性值
        initAttrs(attrs);
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
        // 设置CheckBox的状态
        mCheckBox.setChecked(isCheck);
        if (isCheck) {
            // 开启
            mTextDes.setTextColor(Color.GREEN);
            mTextDes.setText(mDeson);
        } else {
            // 关闭
            mTextDes.setTextColor(Color.RED);
            mTextDes.setText(mDesoff);
        }
    }

    /**
     * 初始化自定义属性值
     * @param attrs 构造方法中所获取到的自定义属性的集合
     */
    private void initAttrs(AttributeSet attrs) {
        for (int i=0; i<attrs.getAttributeCount(); i++) {
            Log.i(tag, "name = " + attrs.getAttributeName(i));
            Log.i(tag, "value = " + attrs.getAttributeValue(i));
        }
        mTitle = attrs.getAttributeValue(namespace, "title");
        mDesoff = attrs.getAttributeValue(namespace, "desoff");
        mDeson = attrs.getAttributeValue(namespace, "deson");
        Log.i(tag, mTitle);
        Log.i(tag, mDesoff);
        Log.i(tag, mDeson);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextDes = (TextView) findViewById(R.id.text_des);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mTextTitle.setText(mTitle);
    }
}
