package neu.edu.cn.mobilesafer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.db.dao.AntiVirusDao;
import neu.edu.cn.mobilesafer.util.MD5Util;
import neu.edu.cn.mobilesafer.util.ToastUtil;

public class AntiVirusActivity extends AppCompatActivity {
    // 正在扫描病毒
    private static final int SCANNING = 0;
    // 扫描完成
    private static final int SCANNING_END = 1;

    private ImageView mActScanningImage;

    private LinearLayout mScanningAddLayout;

    private TextView mScanningNameText;

    private ProgressBar mScanningProgressBar;

    private List<VirusScanningInfo> mVirusScanningList;

    private int count;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在扫描中
                case 0:
                    VirusScanningInfo info = (VirusScanningInfo) msg.obj;
                    TextView textView = new TextView(getApplicationContext());
                    mScanningNameText.setText(info.name);
                    if (info.isVirus) {
                        textView.setText("发现病毒:" + info.name);
                        textView.setTextColor(Color.RED);
                    } else {
                        textView.setText("扫描安全:" + info.name);
                        textView.setTextColor(Color.GREEN);
                    }
                    mScanningAddLayout.addView(textView, 0);
                    break;
                case 1:
                    mActScanningImage.clearAnimation();
                    mScanningNameText.setText("扫描完成");
                    // 卸载病毒
                    unStallVirus(mVirusScanningList);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        // 初始化布局文件中view
        initView();
        // 初始化扫描动画
        initScanningAnimation();
        // 检测手机病毒
        checkVirus();
    }

    /**
     * 卸载病毒
     */
    private void unStallVirus(final List<VirusScanningInfo> virusScanningList) {
        // 卸载病毒程序
        if (!virusScanningList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("警告！");
            builder.setMessage("发现" + virusScanningList.size() + "个病毒，请立即清理！");
            builder.setPositiveButton("立即清理", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (VirusScanningInfo virusScanningInfo : virusScanningList) {
                        Intent intent = new Intent("android.intent.action.DELETE");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + virusScanningInfo.packageName));
                        startActivity(intent);
                    }
                }
            });
        } else {
            ToastUtil.show(getApplicationContext(), "扫描完成，未发现木马病毒");
        }
    }

    /**
     * 检测手机病毒
     */
    private void checkVirus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                count = 0;
                // 获取病毒相关信息集合
                List<String> virusList = AntiVirusDao.getVirusList();
                // 将扫描到的病毒信息添加到病毒集合
                mVirusScanningList = new ArrayList<VirusScanningInfo>();
                // 扫描到的所有应用的信息集合
                List<VirusScanningInfo> scanningList = new ArrayList<VirusScanningInfo>();
                // 获取包管理者对象
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES
                        + PackageManager.GET_UNINSTALLED_PACKAGES);
                mScanningProgressBar.setMax(packageInfoList.size());
                // 循环遍历所有已获得的应用包集合
                for (PackageInfo packageInfo : packageInfoList) {
                    // 包信息获取所有的签名的数组
                    Signature[] signatures = packageInfo.signatures;
                    // 获取签名文件的第一位
                    String signature = signatures[0].toCharsString();
                    // 加密获取到的签名文件的第一位
                    String signatureMD = MD5Util.encodePassword(signature);
                    VirusScanningInfo scanningInfo = new VirusScanningInfo();
                    if (virusList.contains(signatureMD)) {
                        // 扫描到病毒,记录病毒后提醒用户卸载
                        scanningInfo.isVirus = true;
                        mVirusScanningList.add(scanningInfo);
                    } else {
                        scanningInfo.isVirus = false;
                    }
                    // 扫描到的所有应用的信息集合
                    scanningList.add(scanningInfo);
                    scanningInfo.packageName = packageInfo.packageName;
                    scanningInfo.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 扫描过程中,实时更新进度条
                    count++;
                    mScanningProgressBar.setProgress(count);

                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanningInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCANNING_END;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 初始化扫描动画
     */
    private void initScanningAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        // 设置动画无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        // 设置插值器,匀速循环且不停顿
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);
        mActScanningImage.startAnimation(rotateAnimation);
    }

    /**
     * 初始化布局文件中view
     */
    private void initView() {
        mActScanningImage = (ImageView) findViewById(R.id.act_scanning_img);
        mScanningNameText = (TextView) findViewById(R.id.current_scanning_text);
        mScanningAddLayout = (LinearLayout) findViewById(R.id.linear_anti_add_text);
        mScanningProgressBar = (ProgressBar) findViewById(R.id.scanning_progress);
    }

    class VirusScanningInfo {
        String packageName;
        String name;
        boolean isVirus;
    }
}
