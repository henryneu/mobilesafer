package neu.edu.cn.mobilesafer.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import neu.edu.cn.mobilesafer.R;
import neu.edu.cn.mobilesafer.util.ConstantValues;
import neu.edu.cn.mobilesafer.util.SharePreferenceUtil;

public class ToastLocationActivity extends Activity {

    private Button mDragButton;

    private Button mDragHintTopButton;

    private Button mDragHintBottomButton;

    private WindowManager mWindowManager;
    // 手机屏幕的高度
    private int mWindowHeight;
    // 手机屏幕的宽度
    private int mWindowWidth;
    // 获取上一次触摸事件结束时的坐标值X
    private int mDragBtnLastLocationX;
    // 获取上一次触摸事件结束时的坐标值Y
    private int mDragBtnLastLocationY;
    // 设置双击事件
    long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕的宽高值
        mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        // 初始化布局文件中的相关控件
        initView();
    }

    /**
     * 初始化布局文件中的相关控件
     */
    private void initView() {
        mDragButton = (Button) findViewById(R.id.click_btn_drag);
        mDragHintTopButton = (Button) findViewById(R.id.top_hint_btn);
        mDragHintBottomButton = (Button) findViewById(R.id.bottom_hint_btn);
        // 获取上一次触摸事件结束时View所在位置的坐标值
        mDragBtnLastLocationX = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.LOCATION_X, 0);
        mDragBtnLastLocationY = SharePreferenceUtil.getIntFromSharePreference(getApplicationContext(),
                ConstantValues.LOCATION_Y, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = mDragBtnLastLocationX;
        layoutParams.topMargin = mDragBtnLastLocationY;

        if (mDragBtnLastLocationY > mWindowHeight / 2) {
            mDragHintTopButton.setVisibility(View.VISIBLE);
            mDragHintBottomButton.setVisibility(View.INVISIBLE);
        } else {
            mDragHintTopButton.setVisibility(View.INVISIBLE);
            mDragHintBottomButton.setVisibility(View.VISIBLE);
        }
        // 设置双击居中的逻辑
        mDragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if ((mHits[mHits.length - 1] - mHits[0]) < 500) {
                    int centerLocationLeft = mWindowWidth / 2 - mDragButton.getWidth() / 2;
                    int centerLocationTop = mWindowHeight / 2 - mDragButton.getHeight() / 2;
                    int centerLocationRight = mWindowWidth / 2 + mDragButton.getWidth() / 2;
                    int centerLocationBottom = mWindowHeight / 2 + mDragButton.getHeight() / 2;
                    mDragButton.layout(centerLocationLeft, centerLocationTop, centerLocationRight, centerLocationBottom);
                    // 存储双击之后view所在的坐标位置X和Y值
                    SharePreferenceUtil.putIntToSharePreference(getApplicationContext(), ConstantValues.LOCATION_X, centerLocationLeft);
                    SharePreferenceUtil.putIntToSharePreference(getApplicationContext(), ConstantValues.LOCATION_Y, centerLocationTop);
                }
            }
        });

        // View设置布局参数
        mDragButton.setLayoutParams(layoutParams);
        mDragButton.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        // 两个方向上所移动的距离值
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        int top = mDragButton.getTop() + disY;
                        int bottom = mDragButton.getBottom() + disY;
                        int left = mDragButton.getLeft() + disX;
                        int right = mDragButton.getRight() + disX;

                        if (left < 0 || top < 0 || right > mWindowWidth
                                || bottom > mWindowHeight - 21) {
                            return true;
                        }

                        if (top > mWindowHeight / 2) {
                            mDragHintTopButton.setVisibility(View.VISIBLE);
                            mDragHintBottomButton.setVisibility(View.INVISIBLE);
                        } else {
                            mDragHintTopButton.setVisibility(View.INVISIBLE);
                            mDragHintBottomButton.setVisibility(View.VISIBLE);
                        }

                        mDragButton.layout(left, top, right, bottom);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        // 触摸事件结束时，记录当前View的位置
                        SharePreferenceUtil.putIntToSharePreference(getApplicationContext(),
                                ConstantValues.LOCATION_X, mDragButton.getLeft());
                        SharePreferenceUtil.putIntToSharePreference(getApplicationContext(),
                                ConstantValues.LOCATION_Y, mDragButton.getTop());
                        break;
                }
                return false;
            }
        });
    }
}
