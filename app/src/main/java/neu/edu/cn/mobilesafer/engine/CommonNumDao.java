package neu.edu.cn.mobilesafer.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neuHenry on 2017/9/3.
 */

public class CommonNumDao {

    public static final String tag = "CommonNumDao";

    public static String addressPathName = "data/data/neu.edu.cn.mobilesafer/files/commonnum.db";

    /**
     * 获取数据库中的数据组
     *
     * @return 返回数据组
     */
    public List<Group> getGroup() {
        // 获取指定路径下数据库的对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(addressPathName, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null, null);
        List<Group> groupList = new ArrayList<Group>();
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            groupList.add(group);
        }
        cursor.close();
        db.close();
        return groupList;
    }

    /**
     * 获取每一个组中孩子节点的数据
     *
     * @param idx 要获取数据组的序列号
     * @return 返回每个数据组下的数据
     */
    public List<Child> getChild(String idx) {
        // 获取指定路径下数据库的对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(addressPathName, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        List<Child> childList = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childList.add(child);
        }
        cursor.close();
        db.close();
        return childList;
    }

    public class Group {
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child {
        public String _id;
        public String number;
        public String name;
    }
}
