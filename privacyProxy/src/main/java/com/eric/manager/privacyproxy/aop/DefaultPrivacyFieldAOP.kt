package com.eric.manager.privacyproxy.aop

import android.os.Build
import androidx.annotation.Keep
import com.eric.manager.privacy.annotation.PrivacyOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyField
import com.eric.manager.privacyproxy.PrivacyGuarder
import com.eric.manager.privacyproxy.log.LogAOP

/**
 * @Description: 默认实现的隐私属性代理规则类，可以用来参考，也可以仿照此类自定义代理规则
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/19 9:56
 * @Version: 1.0
 */
@Keep
@PrivacyProxyClass
object DefaultPrivacyFieldAOP {
    //代理属性（目前只发现这个）
    @PrivacyProxyField(
        targetClass = Build::class,
        targetField = "SERIAL",
        targetFieldOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmField
    val SERIAL: String = if (!PrivacyGuarder.isAgreed()) {
        LogAOP.log("SERIAL", "获取设备序列号")
        ""
    } else {
        val key = "SERIAL"
        var value = ""

        if (PrivacyGuarder.hasCachedPrivacy(key)) {
            LogAOP.log(key, "获取设备序列号", fromCache = true)
            value = PrivacyGuarder.getCachedPrivacy(key)
        } else {
            LogAOP.log(key, "获取设备序列号")
            try {
                value = Build.SERIAL
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
        }
        value
    }
}