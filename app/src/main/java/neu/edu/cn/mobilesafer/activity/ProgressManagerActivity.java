package neu.edu.cn.mobilesafer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.db.dao.ProcessInfo;
import neu.edu.cn.mobilesafer.engine.ProgressInfoProvider;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class ProgressManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String tag = "ProgressManagerActivity";
    // 显示手机中运行的总进程数的文本框
    private TextView mPhoneProcessCountText;
    // 显示进程已占用和总空间的文本框
    private TextView mPhoneMemoryText;
    // 显示进程类型描述的文本框
    private TextView mProcessListDesText;
    // 展示手机已安装应用信息的列表
    private ListView mProcessInfoListView;
    // 全选按钮
    private Button mSelectedAllButton;
    // 反选按钮
    private Button mReverseSelectedButton;
    // 一键清理按钮
    private Button mClearAllButton;
    // 设置按钮
    private Button mSettingButton;
    // 手机已安装的应用信息集合
    private List<ProcessInfo> mProcessInfoList;
    // 手机已安装的用户应用信息集合
    private List<ProcessInfo> mCustomerProcessInfoList;
    // 手机已安装的系统应用信息集合
    private List<ProcessInfo> mSystemProcessInfoList;
    // 进程信息展示所需的适配器
    private ProcessInfoAdapter mProgressInfoAdapter;
    // 当前手机正在运行的进程总数
    private int mProcessCount;
    // 当前手机剩余可用的内存空间大小
    private long mAvailableMemory;
    // 当前手机总的可用的内存空间大小
    private long mTotalMemory;
    // ListView选中的条目中的进程对象
    private ProcessInfo mProcessInfo;

    private String formatTotalMemory;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mProgressInfoAdapter == null) {
                mProgressInfoAdapter = new ProcessInfoAdapter();
                mProcessInfoListView.setAdapter(mProgressInfoAdapter);
            } else {
                mProgressInfoAdapter.notifyDataSetChanged();
            }
            if (mProcessListDesText != null && mCustomerProcessInfoList != null) {
                mProcessListDesText.setText("用户进程(" + mCustomerProcessInfoList.size() + ")");
            }
            if (mPhoneProcessCountText != null) {
                mPhoneProcessCountText.setText("进程总数：" + mProcessCount);
            }
            // 格式化获取到的内存数据，并设置给指定文本框
            String formatAvailableMemory = Formatter.formatFileSize(getApplicationContext(), mAvailableMemory);
            formatTotalMemory = Formatter.formatFileSize(getApplicationContext(), mTotalMemory);
            if (mPhoneMemoryText != null) {
                mPhoneMemoryText.setText("可用/总共:" + formatAvailableMemory + "/" + formatTotalMemory);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_manager);
        // 初始化布局文件中的View
        initView();
        // 初始化列表数据
        initData();
    }

    /**
     * 初始化列表数据
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取当前手机正在运行的所有进程信息的集合
                mProcessInfoList = ProgressInfoProvider.getProgressInfoList(getApplicationContext());
                // 获取当前手机正在运行的进程总数
                mProcessCount = ProgressInfoProvider.getProcessCount(getApplicationContext());
                // 获取可用内存大小
                mAvailableMemory = ProgressInfoProvider.getAvailableMemory(getApplicationContext());
                // 获取总运行内存大小
                mTotalMemory = ProgressInfoProvider.getTotalMemory(getApplicationContext());
                // 初始化系统进程的集合
                mSystemProcessInfoList = new ArrayList<ProcessInfo>();
                // 初始化用户进程的集合
                mCustomerProcessInfoList = new ArrayList<ProcessInfo>();
                // 循环遍历获取到的正在运行的所有进程的集合，添加到相应的集合中
                for (ProcessInfo processInfo : mProcessInfoList) {
                    if (processInfo.isSystem) {
                        // 系统进程
                        mSystemProcessInfoList.add(processInfo);
                    } else {
                        // 用户进程
                        mCustomerProcessInfoList.add(processInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 初始化布局文件中的View
     */
    private void initView() {
        mPhoneProcessCountText = (TextView) findViewById(R.id.phone_progress_count_text);
        mPhoneMemoryText = (TextView) findViewById(R.id.progress_use_memory_text);
        mProcessListDesText = (TextView) findViewById(R.id.progress_info_title_des);

        mProcessInfoListView = (ListView) findViewById(R.id.progress_info_list_view);

        mSelectedAllButton = (Button) findViewById(R.id.progress_selected_all);
        mReverseSelectedButton = (Button) findViewById(R.id.progress_reverse_selected_all);
        mClearAllButton = (Button) findViewById(R.id.progress_clear_all);
        mSettingButton = (Button) findViewById(R.id.progress_set);
        // 按钮设置点击事件
        mSelectedAllButton.setOnClickListener(this);
        mReverseSelectedButton.setOnClickListener(this);
        mClearAllButton.setOnClickListener(this);
        mSettingButton.setOnClickListener(this);
        // ListView注册滚动事件
        mProcessInfoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // AbsListView中view就是listView对象
                // firstVisibleItem第一个可见条目索引值
                // visibleItemCount当前一个屏幕的可见条目数
                if (mCustomerProcessInfoList != null && mProcessListDesText != null) {
                    if (firstVisibleItem >= mCustomerProcessInfoList.size() + 1) {
                        // 已滚动到系统应用条目
                        mProcessListDesText.setText("系统进程(" + mSystemProcessInfoList.size() + ")");
                    } else {
                        // 已滚动到用户应用条目
                        mProcessListDesText.setText("用户进程(" + mCustomerProcessInfoList.size() + ")");
                    }
                }
            }
        });
        // ListView设置点击事件
        mProcessInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerProcessInfoList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerProcessInfoList.size() + 1) {
                        mProcessInfo = mCustomerProcessInfoList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemProcessInfoList.get(position - mCustomerProcessInfoList.size() - 2);
                    }
                }
                if (mProcessInfo != null) {
                    if (!mProcessInfo.packageName.equals(getPackageName())) {
                        // 点击的条目不是当前应用的进程时，才要做相应的逻辑
                        mProcessInfo.isCheck = !mProcessInfo.isCheck;
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.process_info_check_box);
                        checkBox.setChecked(mProcessInfo.isCheck);
                    }
                }
            }
        });
    }

    // 处理按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.progress_selected_all:
                // 全选按钮,选中除当前应用的进程外的所有进程
                selectedAll();
                break;
            case R.id.progress_reverse_selected_all:
                // 反选按钮,将所有条目的状态取反
                selectedReversed();
                break;
            case R.id.progress_clear_all:
                // 一键清理按钮,一键清理所有选中的进程
                oneKeyClearSelected();
                break;
            case R.id.progress_set:
                // 设置按钮
                Intent intent = new Intent(ProgressManagerActivity.this, ProgressSettingActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mProgressInfoAdapter != null) {
            mProgressInfoAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选中除当前应用的进程外的所有进程
     */
    private void selectedAll() {
        for (ProcessInfo processInfo : mCustomerProcessInfoList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }
        for (ProcessInfo processInfo : mSystemProcessInfoList) {
            processInfo.isCheck = true;
        }
        if (mProgressInfoAdapter != null) {
            mProgressInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 将所有条目的状态取反
     */
    private void selectedReversed() {
        for (ProcessInfo processInfo : mCustomerProcessInfoList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }
        for (ProcessInfo processInfo : mSystemProcessInfoList) {
            processInfo.isCheck = !processInfo.isCheck;
        }
        if (mProgressInfoAdapter != null) {
            mProgressInfoAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 清理按钮,一键清理所有选中的进程
     */
    private void oneKeyClearSelected() {
        // 记录待杀死的进程
        List<ProcessInfo> killProcessInfoList = new ArrayList<ProcessInfo>();
        // 记录杀死进程后释放的内存大小
        long realeaseMemory = 0;
        for (ProcessInfo processInfo : mCustomerProcessInfoList) {
            if (processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            if (processInfo.isCheck) {
                killProcessInfoList.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : mSystemProcessInfoList) {
            if (processInfo.isCheck) {
                killProcessInfoList.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : killProcessInfoList) {
            if (mCustomerProcessInfoList.contains(processInfo)) {
                mCustomerProcessInfoList.remove(processInfo);
                ProgressInfoProvider.realeaseSelectedItem(getApplicationContext(), processInfo);
                realeaseMemory += processInfo.memSize;
            }
            if (mSystemProcessInfoList.contains(processInfo)) {
                mSystemProcessInfoList.remove(processInfo);
                ProgressInfoProvider.realeaseSelectedItem(getApplicationContext(), processInfo);
                realeaseMemory += processInfo.memSize;
            }
        }
        // 更新当前正在运行的进程总数
        mProcessCount -= killProcessInfoList.size();
        if (mPhoneProcessCountText != null) {
            mPhoneProcessCountText.setText("进程总数：" + mProcessCount);
        }
        // 更新当前剩余的可用内存空间大小
        mAvailableMemory += realeaseMemory;
        String formatAvailableMemory = Formatter.formatFileSize(getApplicationContext(), mAvailableMemory);
        if (mPhoneMemoryText != null) {
            mPhoneMemoryText.setText("可用/总共:" + formatAvailableMemory + "/" + formatTotalMemory);
        }
        // 提示用户清理了多少进程和空间
//        ToastUtil.show(getApplicationContext(), "杀死了" + killProcessInfoList.size() + "个进程，释放了" +
//                Formatter.formatFileSize(getApplicationContext(), realeaseMemory) + "空间");
        ToastUtil.show(getApplicationContext(), String.format("杀死了%d个进程,释放了%s空间", killProcessInfoList.size(),
                Formatter.formatFileSize(getApplicationContext(), realeaseMemory)));
        if (mProgressInfoAdapter != null) {
            mProgressInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Process信息列表展示的适配器
     */
    class ProcessInfoAdapter extends BaseAdapter {

        // 获取适配器将要创建的View的类型数
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        // 获取getView方法中将要创建的具体View的类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerProcessInfoList.size() + 1) {
                // 纯文本条目的状态码
                return 0;
            } else {
                // 图标和文本条目的状态码
                return 1;
            }
        }

        @Override
        public int getCount() {
            boolean isShowSystemProcess = SharePreferenceUtil.getBooleanFromSharePreference(getApplicationContext(),
                    ConstantValues.SHOW_SYSTEM_PROCESS, false);
            if (isShowSystemProcess) {
                return mCustomerProcessInfoList.size() + mSystemProcessInfoList.size() + 2;
            } else {
                return mCustomerProcessInfoList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position < mCustomerProcessInfoList.size() + 1) {
                return mCustomerProcessInfoList.get(position - 1);
            } else {
                return mSystemProcessInfoList.get(position - mCustomerProcessInfoList.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                // 显示灰色纯文本条目
                ViewTitleHolder viewTitleHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_info_title_item, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.titleTextView = (TextView) convertView.findViewById(R.id.app_process_info_title_view);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewTitleHolder.titleTextView.setText("用户进程(" + mCustomerProcessInfoList.size() + ")");
                } else {
                    viewTitleHolder.titleTextView.setText("系统进程(" + mSystemProcessInfoList.size() + ")");
                }
                return convertView;
            } else {
                // 显示图标和文字条目
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.process_info_list_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.processIconImage = (ImageView) convertView.findViewById(R.id.process_info_icon);
                    viewHolder.processNameText = (TextView) convertView.findViewById(R.id.process_info_name);
                    viewHolder.processUsedMemory = (TextView) convertView.findViewById(R.id.process_info_used_memory);
                    viewHolder.processCheckBox = (CheckBox) convertView.findViewById(R.id.process_info_check_box);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.processIconImage.setBackgroundDrawable(getItem(position).icon);
                viewHolder.processNameText.setText(getItem(position).appName);
                long usedMemory = getItem(position).memSize;
                String formatUsedMemory = Formatter.formatFileSize(getApplicationContext(), usedMemory);
                viewHolder.processUsedMemory.setText("占用内存:" + formatUsedMemory);
                // 判断选中项是否是当前的进程，若是则不显示CheckBox
                if (getItem(position).packageName.equals(getPackageName())) {
                    viewHolder.processCheckBox.setVisibility(View.GONE);
                } else {
                    viewHolder.processCheckBox.setVisibility(View.VISIBLE);
                }

                viewHolder.processCheckBox.setChecked(getItem(position).isCheck);

                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView processIconImage;
        TextView processNameText;
        TextView processUsedMemory;
        CheckBox processCheckBox;
    }

    static class ViewTitleHolder {
        TextView titleTextView;
    }
}
