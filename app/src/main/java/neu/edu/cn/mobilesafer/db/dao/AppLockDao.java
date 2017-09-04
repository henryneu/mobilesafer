package neu.edu.cn.mobilesafer.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import neu.edu.cn.mobilesafer.db.AppLockDBOpenHelper;

/**
 * Created by neuHenry on 2017/9/4.
 */

public class AppLockDao {

    private AppLockDBOpenHelper mAppLockDBOpenHelper;

    // 私有化构造方法
    private AppLockDao(Context context) {
        mAppLockDBOpenHelper = new AppLockDBOpenHelper(context);
    }

    // 声明一个当前类的对象
    private static AppLockDao mAppLockDao = null;

    // 提供一个静态方法，如果当前对象为空，创建一个新
    public static AppLockDao getInstance(Context context) {
        if (mAppLockDao == null) {
            mAppLockDao = new AppLockDao(context);
        }
        return mAppLockDao;
    }

    /**
     * 插入一条数据到数据库
     *
     * @param packageName 待插入数据库中的数据
     */
    public void insert(String packageName) {
        // 获取到可写的数据库
        SQLiteDatabase db = mAppLockDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("lockedpackage", null, values);
        db.close();
    }

    /**
     * 数据库中删除指定数据
     *
     * @param packageName 待删除数据库中的数据
     */
    public void delete(String packageName) {
        // 获取到可写的数据库
        SQLiteDatabase db = mAppLockDBOpenHelper.getWritableDatabase();
        db.delete("lockedpackage", "packagename = ?", new String[]{packageName});
        db.close();
    }

    /**
     * 获取数据库中所有的数据
     *
     * @return 数据库中获取到的包名的集合
     */
    public List<String> findAll() {
        // 获取到可写的数据库
        SQLiteDatabase db = mAppLockDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("lockedpackage", new String[]{"packagename"}, null, null, null, null, null);
        List<String> lockPackageList = new ArrayList<String>();
        while (cursor.moveToNext()) {
            lockPackageList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return lockPackageList;
    }
}
