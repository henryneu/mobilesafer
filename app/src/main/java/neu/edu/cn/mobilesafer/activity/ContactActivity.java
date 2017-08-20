package neu.edu.cn.mobilesafer.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neu.edu.cn.mobilesafer.R;

public class ContactActivity extends AppCompatActivity {

    private static final String tag = "ContactActivity";

    // 显示电话联系人的列表
    private ListView mListView;
    // 装载联系人姓名和电话号码所组成的hashmap的list
    private List<HashMap<String, String>> mContactList = new ArrayList<HashMap<String, String>>();

    private ContactAdapter mContactAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mContactAdapter = new ContactAdapter();
            mListView.setAdapter(mContactAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // 初始化布局文件中View控件
        initView();
        // 初始化ListView中用到的数据
        initData();
    }

    /**
     * 初始化ListView中用到的数据
     */
    private void initData() {
        // 查询数据是一个费时操作，开启子线程进行查询
        new Thread() {
            @Override
            public void run() {
                // 获取内容解析器，并进行查询
                ContentResolver contentResolver = getContentResolver();
                // Uri.parse("content://com.android.contacts/raw_contacts"),不要写错，还要加权限
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);
                mContactList.clear();
                // 循环遍历查询结果，直至结束
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    Log.i(tag, "id = " + id);
                    Cursor contactCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
                    // 每循环遍历一次，就把相应的数据添加到新的HashMap对象中
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (contactCursor.moveToNext()) {
                        String data = contactCursor.getString(0);
                        String type = contactCursor.getString(1);
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                        Log.i(tag, "data = " + data);
                        Log.i(tag, "type = " + type);
                    }
                    contactCursor.close();
                    mContactList.add(hashMap);
                }
                cursor.close();
                // 发送默认的消息
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化布局文件中View控件
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.contacts_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mContactAdapter != null) {
                    // 获取被点击项Item的电话号码值，并返回给打开此Activity的活动
                    HashMap<String, String> hashMap = mContactAdapter.getItem(position);
                    String phone = hashMap.get("phone");
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);
                    // 关闭此活动
                    finish();
                }
            }
        });
    }

    class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mContactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.contact_list_item, null);
            TextView contactListName = (TextView) view.findViewById(R.id.contact_list_name);
            TextView contactListPhone = (TextView) view.findViewById(R.id.contact_list_phone);
            contactListName.setText(getItem(position).get("name"));
            contactListPhone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
