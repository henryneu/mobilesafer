package neu.edu.cn.mobilesafer.db.dao;

import android.graphics.drawable.Drawable;

/**
 * Created by neuHenry on 2017/9/3.
 */

public class ProcessInfo {
    // 应用名称
    public String appName;
    // 应用图标
    public Drawable icon;
    // 应用已使用的内存数
    public long memSize;
    // 是否被选中
    public boolean isCheck;
    // 是否为系统应用
    public boolean isSystem;
    // 如果进程没有名称,则将其所在应用的包名最为名称
    public String packageName;

    public String getName() {
        return appName;
    }

    public void setName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
