package neu.edu.cn.mobilesafer.engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by neuHenry on 2017/9/1.
 */

public class SmsBackUp {

    /**
     * 备份短信
     *
     * @param context        上下文环境
     * @param path           短信备份文件的路径
     * @param progressDialog 更新进度条
     */
    public static void backup(Context context, String path, ProgressDialog progressDialog) {
        try {
            // 获取数据库表的内容解析器
            context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            // 短信备份的文件
            File smsFile = new File(path);
            FileOutputStream fos = new FileOutputStream(smsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
