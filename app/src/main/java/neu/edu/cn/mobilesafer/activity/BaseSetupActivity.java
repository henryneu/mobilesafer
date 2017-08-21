package neu.edu.cn.mobilesafer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**
             * @param e1 第1个ACTION_DOWN MotionEvent 并且只有一个
             * @param e2 最后一个ACTION_MOVE MotionEvent
             * @param velocityX X轴上的移动速度，像素/秒
             * @param velocityY Y轴上的移动速度，像素/秒
             * 这个方法发生在ACTION_UP时才会触发
             *
             * */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 如果此判断条件为true，则跳转到下一步的活动页面
                if ((e1.getX() - e2.getX()) > 0) {
                    // 抽象方法，跳转到下一步的活动
                    showNextPage();
                }
                // 如果此判断条件为true，则跳转到上一步的活动页面
                if ((e1.getX() - e2.getX()) < 0) {
                    // 抽象方法，跳转到上一步的活动
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 抽象方法，跳转到下一步的活动，父类不知道子类具体的操作，因此交给子类具体处理
     */
    public abstract void showNextPage();

    /**
     * 抽象方法，跳转到上一步的活动，父类不知道子类具体的操作，因此交给子类具体处理
     */
    public abstract void showPrePage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 通过GestureDetector处理点击事件，按下，滑动，抬起事件
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     *
     * @param view 布局文件中设置了onClick属性的View
     */
    public void prePage(View view) {
        showPrePage();
    }

    /**
     * 布局文件中的View被点击时将调用此方法
     *
     * @param view 布局文件中设置了onClick属性的View
     */
    public void nextPage(View view) {
        showNextPage();
    }
}
