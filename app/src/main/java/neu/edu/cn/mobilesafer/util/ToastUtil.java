package neu.edu.cn.mobilesafer.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by neuHenry on 2017/8/14.
 */

public class ToastUtil {

    /**
     * @param context 上下文环境
     * @param str toast中描述信息
     */
   public static void show(Context context, String str) {
       Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
