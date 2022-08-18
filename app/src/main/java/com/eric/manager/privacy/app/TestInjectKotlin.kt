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

    companion object{
        //直接在声明成员变量时访问隐私属性且为静态类型
        var sn3 = Build.SERIAL
    }

    //直接在声明成员变量时调用隐私API
    @RequiresApi(Build.VERSION_CODES.O)
    var sn1 = Build.getSerial()

    //直接在声明成员变量时访问隐私属性
    var sn2 = Build.SERIAL


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