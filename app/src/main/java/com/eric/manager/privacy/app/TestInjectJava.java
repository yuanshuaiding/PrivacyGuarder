package com.eric.manager.privacy.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: java版本：测试AOP替换隐私API
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/28 11:41
 * @Version: 1.0
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class TestInjectJava {

    //直接在声明成员变量时获取隐私数据
    String sn= Build.getSerial();

    public static List<AppInfo> getAppsInfo(Context context) {
        ArrayList<AppInfo> list = new ArrayList<>();

        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return list;
            }
            List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

            for (PackageInfo pi : installedPackages) {
                AppInfo ai = getBean(pi);
                if (ai != null) {
                    list.add(ai);
                }
            }
        } catch (Exception ignored) {
        }

        return list;
    }

    public static AppInfo getBean(PackageInfo pi) {
        if (pi == null) {
            return null;
        } else {
            String packageName = pi.packageName;
            int versionCode = pi.versionCode;
            return new AppInfo(packageName, versionCode);
        }
    }

    public static class AppInfo {
        private String packageName;
        private int versionCode;

        public AppInfo(String packageName, int versionCode) {
            this.setPackageName(packageName);
            this.setVersionCode(versionCode);
        }

        public String getPackageName() {
            return this.packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getVersionCode() {
            return this.versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }
    }
}
