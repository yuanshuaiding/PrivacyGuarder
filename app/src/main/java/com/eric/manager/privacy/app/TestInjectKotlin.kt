package com.eric.manager.privacy.app

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.eric.manager.privacy.app.TestInjectJava.AppInfo

/**
 * @Description:kotlin版本：测试AOP替换隐私API
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/17 15:39
 * @Version: 1.0
 */
class TestInjectKotlin {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAppsInfo(context: Context): List<AppInfo>? {
        val list = ArrayList<AppInfo>()
        try {
            val pm = context.packageManager ?: return list
            val installedPackages = pm.getInstalledPackages(0)
            for (pi in installedPackages) {
                val ai = TestInjectJava.getBean(pi)
                if (ai != null) {
                    list.add(ai)
                }
            }
        } catch (ignored: Exception) {
        }
        return list
    }

}