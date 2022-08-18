package com.eric.manager.privacy.app.privacyAop

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.eric.manager.privacy.annotation.PrivacyOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyField
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

    @PrivacyProxyField(
        targetClass = Build::class,
        targetField = "SERIAL",
        targetFieldOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    val SERIAL: String = if (!PrivacyUtil.isAgreed()) {
        ""
    } else {
        Build.SERIAL
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledPackages",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
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
        targetMethodOpcode = PrivacyOpcode.INVOKESTATIC
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