package neu.edu.cn.mobilesafer.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import neu.edu.cn.mobilesafer.db.BlackNumberDBOpenHelper;
import neu.edu.cn.mobilesafer.db.BlackNumberInfo;

/**
 * Created by neuHenry on 2017/8/30.
 */

public class BlackNumberDao {

    private BlackNumberDBOpenHelper mBlackNumberDBOpenHelper;

    // 私有化构造方法
    private BlackNumberDao(Context context) {
        mBlackNumberDBOpenHelper = new BlackNumberDBOpenHelper(context);
    }

    // 声明一个当前类的对象
    private static BlackNumberDao mBlackNumberDao = null;

    // 提供一个静态方法，如果当前对象为空，创建一个新
    public static BlackNumberDao getInstance(Context context) {
        if (mBlackNumberDao == null) {
            mBlackNumberDao = new BlackNumberDao(context);
        }
        return mBlackNumberDao;
    }

    /**
     * 黑名单表中添加号码
     *
     * @param number 待添加号码
     * @param mode   拦截模式
     * @return 是否添加成功
     */
    public void add(String number, String mode) {
        // 获取到可写的数据库
        SQLiteDatabase db = mBlackNumberDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowId = db.insert("blacknum", null, values);
//        if (rowId == -1) {
//            return false;
//        } else {
//            return true;
//        }
    }

    /**
     * 黑名单表中删除号码
     *
     * @param number 待删除的号码
     * @return 是否删除成功
     */
    public void delete(String number) {
        // 获取到可写的数据库
        SQLiteDatabase db = mBlackNumberDBOpenHelper.getWritableDatabase();
        int rowId = db.delete("blacknum", "number = ?", new String[]{number});
//        if (rowId == 0) {
//            return false;
//        } else {
//            return true;
//        }
    }

    /**
     * 修改黑名单号码的拦截模式
     *
     * @param number  号码
     * @param newmode 新的拦截模式
     * @return 是否修改成功
     */
    public boolean updateNumMode(String number, String newmode) {
        // 获取到可写的数据库
        SQLiteDatabase db = mBlackNumberDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newmode);
        int rownumber = db.update("blacknum", values, "number = ?", new String[]{number});
        if (rownumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 查询黑名单表中的所有电话信息
     * @return 查找到的所有电话信息
     */
    public List<BlackNumberInfo> findAll() {
        // 获取到可写的数据库
        SQLiteDatabase db = mBlackNumberDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknum", new String[]{"number", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }
}
