package neu.edu.cn.mobilesafer.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import neu.edu.cn.mobilesafer.service.WidgetUpdateService;

public class ProcessWidgetProvider extends AppWidgetProvider {

    private static final String tag = "ProcessWidgetProvider";

    // widget的每次变化都会调用onReceive
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "onReceive............");
        super.onReceive(context, intent);
    }

    // 当widget第一次被添加时,调用onEnable
    @Override
    public void onEnabled(Context context) {
        Log.i(tag, "桌面小窗体第一次创建");
        // 启动widget服务
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
        super.onEnabled(context);
    }

    // 新增widget时,或者widget更新时,调用onUpdate,更新时间取决于xml中配置的时间,最短为半小时
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(tag, "桌面小窗体有更新，再一次创建");
        // 启动widget服务
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 当widget大小发生变化时,调用此方法
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.i(tag, "桌面小窗体有变化，再一次创建");
        // 启动widget服务
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    // 移除widget时,调onDeleted
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(tag, "桌面小窗体删除时，调用此方法");
        super.onDeleted(context, appWidgetIds);
    }

    // 当widget完全从桌面移除时,调用onDisabled
    @Override
    public void onDisabled(Context context) {
        Log.i(tag, "桌面小窗体全部删除时，调用此方法");
        // 停止widget服务
        Intent intent = new Intent(context, WidgetUpdateService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }
}
