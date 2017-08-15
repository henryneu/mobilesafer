package neu.edu.cn.mobilesafer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by neuHenry on 2017/8/15.
 */

public class FocusedTextView extends AppCompatTextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 自定义TextView获取焦点
     * @return 返回true表示获得焦点
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
