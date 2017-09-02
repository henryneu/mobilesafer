package neu.edu.cn.mobilesafer.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by neuHenry on 2017/9/1.
 */

public class SmsBackUp {

    private static int index = 0;

    /**
     * 备份短信
     *
     * @param context        上下文环境
     * @param path           短信备份文件的路径
     * @param progressStyleCallBack 不同风格UI的回调接口
     */
    public static void backup(Context context, String path, ProgressStyleCallBack progressStyleCallBack) {

        FileOutputStream fos = null;
        Cursor cursor = null;

        try {
            // 获取数据库表的内容解析器
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            // 短信备份的文件
            File smsFile = new File(path);
            fos = new FileOutputStream(smsFile);
            // 设置所备份短信的总条数
            if (progressStyleCallBack != null) {
                progressStyleCallBack.setMax(cursor.getCount());
            }
            // 获取序列化数据文件到XML文件的对象
            XmlSerializer xmlSerializer = Xml.newSerializer();
            // 设置输出格式
            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            // XML文件中文档的起始结点
            xmlSerializer.startTag(null, "smss");
            // 数据库中读取的数据序列化到指定的结点下
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");
                // 循环一次进度条叠加一次
                index++;
                Thread.sleep(500);

                if (progressStyleCallBack != null) {
                    progressStyleCallBack.setProgress(index);
                }
            }

            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ProgressStyleCallBack {
        public void setMax(int value);
        public void setProgress(int index);
    }
}
