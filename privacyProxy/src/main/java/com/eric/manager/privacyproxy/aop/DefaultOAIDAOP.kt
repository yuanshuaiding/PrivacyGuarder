package com.eric.manager.privacyproxy.aop

import android.content.Context
import androidx.annotation.Keep
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.miitmdid.interfaces.IIdentifierListener
import com.bun.miitmdid.provider.BaseProvider
import com.eric.manager.privacy.annotation.PrivacyOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyMethod
import com.eric.manager.privacyproxy.PrivacyGuarder
import com.eric.manager.privacyproxy.log.LogAOP

/**
 * @Description: 移动安全联盟设备标识OAID获取代理规则类
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/24 17:28
 * @Version: 1.0
 */
@Keep
@PrivacyProxyClass
object DefaultOAIDAOP {

    private var objectOAIDLock = Object()

    @PrivacyProxyMethod(
        targetClass = MdidSdkHelper::class,
        targetMethod = "InitSdk",
        targetMethodOpcode = PrivacyOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun InitSdk(
        context: Context,
        arg1: Boolean,
        arg2: IIdentifierListener
    ): Int {
        val key = "oaid"
        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log("InitSdk", "OAID SDK 初始化")
            return 0
        }
        synchronized(objectOAIDLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log("InitSdk", "OAID SDK 初始化", fromCache = true)
                val provider = object : BaseProvider() {
                    override fun isSupported() = true

                    override fun doStart() {
                        //do nothing
                    }

                    override fun getOAID(): String {
                        return PrivacyGuarder.getCachedPrivacy(key)
                    }
                }
                arg2.OnSupport(true, provider)
                return 0
            }
            var value = 0
            LogAOP.log("InitSdk", "OAID SDK 初始化")
            try {
                value = MdidSdkHelper.InitSdk(
                    context,
                    arg1
                ) { b, idSupplier ->
                    arg2.OnSupport(b, idSupplier)
                    if (idSupplier != null && idSupplier.isSupported) {
                        PrivacyGuarder.putCachedPrivacy(key, idSupplier.oaid)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return value
        }

    }
}