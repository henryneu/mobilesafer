package neu.edu.cn.mobilesafer.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neuHenry on 2017/9/6.
 */

public class AntiVirusDao {

    public static final String tag = "AntiVirusDao";
    // 病毒库拷贝到本地后的路径地址
    public static String virusPathName = "data/data/neu.edu.cn.mobilesafer/files/antivirus.db";

    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(virusPathName, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
