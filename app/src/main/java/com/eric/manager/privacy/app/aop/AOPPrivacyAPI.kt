package com.eric.manager.privacy.app.aop

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.eric.manager.privacy.annotation.PrivacyMethodOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyMethod
import com.eric.manager.privacy.app.privacyConfig.PrivacyUtil

/**
 * @Description: 隐私API代理规则类，通过注解指定哪些方法会被AOP
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/12 19:44
 * @Version: 1.0
 */
@PrivacyProxyClass
object AOPPrivacyAPI {
    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledPackages",
        targetMethodOpcode = PrivacyMethodOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getInstalledPackages(packageManager: PackageManager, flage: Int): List<PackageInfo> {
        if (!PrivacyUtil.isAgreed()) {
            return emptyList()
        }
        return packageManager.getInstalledPackages(flage)
    }

    @PrivacyProxyMethod(
        targetClass = Build::class,
        targetMethod = "getSerial",
        targetMethodOpcode = PrivacyMethodOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getSerial(): String {
        if (!PrivacyUtil.isAgreed()) {
            return ""
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }
    }
}