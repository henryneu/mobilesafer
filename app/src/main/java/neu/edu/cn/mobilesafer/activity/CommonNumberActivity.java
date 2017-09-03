package neu.edu.cn.mobilesafer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.engine.CommonNumDao;

public class CommonNumberActivity extends AppCompatActivity {
    // 可扩展的ListView，用于展示查询到号码
    private ExpandableListView mExpandableListView;
    // 查询数据库的对象
    private CommonNumDao mCommonNumDao;
    // 数据库中查询到的数据组
    private List<CommonNumDao.Group> mNumGroup;
    // 展示查询到的电话号码的适配器
    private CommonNumAdapter mCommonNumAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mCommonNumAdapter == null) {
                mCommonNumAdapter = new CommonNumAdapter();
                mExpandableListView.setAdapter(mCommonNumAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        // 初始化布局文件中的view
        initView();
        // listView所加载数的据
        initData();
    }

    /**
     * 初始化布局文件中的view
     */
    private void initView() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expand_list_view);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(mNumGroup.get(groupPosition).childList.get(childPosition).number);
                return false;
            }
        });
    }

    /**
     * 呼叫指定号码
     *
     * @param number 待呼叫的电话号码
     */
    private void startCall(String number) {
        // 开启系统的打电话界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        // 高版本API需要进行权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    /**
     * listView所加载数的据
     */
    private void initData() {
        // 开启子线程加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCommonNumDao = new CommonNumDao();
                mNumGroup = mCommonNumDao.getGroup();
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    class CommonNumAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mNumGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mNumGroup.get(groupPosition).childList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mNumGroup.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mNumGroup.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("        " + mNumGroup.get(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.expandable_list_view_item, null);
                viewHolder.phoneNumName = (TextView) convertView.findViewById(R.id.phone_num_name);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.phoneNumName.setText(mNumGroup.get(groupPosition).childList.get(childPosition).name);
            viewHolder.phoneNumber.setText(mNumGroup.get(groupPosition).childList.get(childPosition).number);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    static class ViewHolder {
        TextView phoneNumName;
        TextView phoneNumber;
    }
}
