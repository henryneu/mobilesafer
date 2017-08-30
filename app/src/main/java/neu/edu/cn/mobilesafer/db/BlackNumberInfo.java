package neu.edu.cn.mobilesafer.db;

/**
 * Created by neuHenry on 2017/8/30.
 */

public class BlackNumberInfo {
    // 已添加到黑名单中的号码
    public String number;
    // 已添加到黑名单中的号码的拦截模式
    public String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "number:" + number + ",mode:" + mode;
    }
}
