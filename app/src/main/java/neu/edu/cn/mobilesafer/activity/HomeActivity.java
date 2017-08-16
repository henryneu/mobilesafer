package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import neu.edu.cn.mobilesafer.R;

/**
 * Created by neuHenry on 2017/8/14.
 */

public class HomeActivity extends AppCompatActivity {

    private GridView mGridView;

    private String[] mNameList;

    private int[] mIconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 初始化Activity中的视图
        initView();
        // 初始化数据
        initDate();
    }

    /**
     * 初始化Activity中的视图
     */
    private void initView() {
        mGridView = (GridView) findViewById(R.id.home_grid_view);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        mNameList = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
                "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mIconList = new int[]{R.drawable.anti_theft, R.drawable.communication_guards, R.drawable.app_manage,
                R.drawable.process_manage, R.drawable.net_statistics, R.drawable.antivirus,
                R.drawable.sys_optimize, R.drawable.advanced_tools, R.drawable.settings};
        // GridView添加适配器
        mGridView.setAdapter(new MyAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 8:
                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNameList.length;
        }

        @Override
        public Object getItem(int position) {
            return mNameList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
                viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
                // 将ViewHolder存储在View中
                view.setTag(viewHolder);
            } else {
                view = convertView;
                // 重新获取ViewHolder
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.imageView.setImageResource(mIconList[position]);
            viewHolder.textView.setText(mNameList[position]);
            return view;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }
}
