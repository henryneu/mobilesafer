package neu.edu.cn.mobilesafer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/16.
 */

public class SettingsItem extends RelativeLayout {
    public SettingsItem(Context context) {
        this(context, null);
    }

    public SettingsItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.settings_item, this);
    }
}
