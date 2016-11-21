package com.elaine.okretrolib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by elaine on 2016/11/17.
 */

public class DevicesUtils {

    /**
     * 获取系统版本
     * @return
     */
    public static String getAndroidSystemVersion() {
        return "Android "+ Build.VERSION.RELEASE + "";
    }

    /**
     * 获取渠道名称
     * @param ctx
     * @return
     */
    public static String getChannelName(Context ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = "";
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }

    public static int getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        int version = -1;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo == null?-1:packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static StringBuffer getCPSID(Context context) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("C_Android_");
        buffer.append(Build.MANUFACTURER);// 设备名称
        buffer.append("_");
        buffer.append(getVersionName(context));// 应用版本号
        buffer.append("_");
        buffer.append(getSIMName(context));// 运营商
        buffer.append("_");
        buffer.append(Build.VERSION.RELEASE);// 系统版本号
        buffer.append("_");
        buffer.append(getChannelName(context));
//        buffer.append(PackerNg.getMarket(context));
        return buffer;
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     * @throws Exception
     * @author zlk
     */
    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo == null ? "" : packInfo.versionName;
    }

    /**
     * 获取运营商代号:IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
     *
     * @param context
     * @return
     */
    public static String getSIMName(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        return operator == null ? "" : operator;
    }
}
