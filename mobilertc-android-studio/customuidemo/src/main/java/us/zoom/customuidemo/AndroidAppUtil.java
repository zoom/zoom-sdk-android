package us.zoom.customuidemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacky on 2017/12/27.
 */

public class AndroidAppUtil {

    public static boolean hasActivityForIntent(Context context, Intent intent) {
        if(context != null && intent != null) {
            List list = queryActivitiesForIntent(context, intent);
            return list != null && list.size() > 0;
        } else {
            return false;
        }
    }

    public static List<ResolveInfo> queryActivitiesForIntent(Context context, Intent intent) {
        if(context != null && intent != null) {
            PackageManager pm = context.getPackageManager();
            Object list = null;

            try {
                list = pm.queryIntentActivities(intent, 65536);
            } catch (Exception var5) {
                ;
            }

            if(list == null) {
                list = new ArrayList();
            }

            return (List)list;
        } else {
            return null;
        }
    }

    public static int dip2px(Context context, float value) {
        if(context == null)
            return (int)value;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    public static float px2dip(Context context, int value) {
        if(context == null)
            return (int)value;

        float density = context.getResources().getDisplayMetrics().density;
        return value / density;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if(windowMgr == null)
            return 0;

        Display display = windowMgr.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getDisplayWidth(Context context) {
        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if(windowMgr == null)
            return 0;

        Display display = windowMgr.getDefaultDisplay();
        return display.getWidth();
    }
}
