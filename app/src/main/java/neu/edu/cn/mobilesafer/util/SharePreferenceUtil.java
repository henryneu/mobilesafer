package neu.edu.cn.mobilesafer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by neuHenry on 2017/8/16.
 */

public class SharePreferenceUtil {

    private static SharedPreferences mSharedPreferences;

    /**
     * 将所要存储的数据的键值对存储到SharedPreferences中
     * @param context 上下文环境
     * @param key     所要存储数据的键值
     * @param value   所要存储的数据值
     */
    public static void putBooleanInSharePreference(Context context, String key, boolean value) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 从SharedPreferences中读取指定键值的数据值
     * @param context  上下文环境
     * @param key      所要读取数据的键值
     * @param defValue 如果读取不到指定的值，使用默认值
     * @return 返回读取到的boolean值
     */
    public static boolean getBooleanFromSharePreference(Context context, String key, boolean defValue) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(key, defValue);
    }
}
