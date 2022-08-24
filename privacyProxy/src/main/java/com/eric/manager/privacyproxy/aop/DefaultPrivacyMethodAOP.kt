package com.eric.manager.privacyproxy.aop

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.DhcpInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import com.eric.manager.privacy.annotation.PrivacyOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyMethod
import com.eric.manager.privacyproxy.PrivacyGuarder
import com.eric.manager.privacyproxy.log.LogAOP
import java.net.NetworkInterface
import java.nio.charset.StandardCharsets

/**
 * @Description: 默认实现的隐私AIP代理规则类，可以用来参考，也可以仿照此类自定义代理规则。需要注意的是很多隐私API依赖权限，如果没有权限声明或授权，直接调用则可能出现程序奔溃，这里都进行了异常捕捉，但不能保证在所有的设备上都不再奔溃
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/18 18:40
 * @Version: 1.0
 */
@Keep
@PrivacyProxyClass
object DefaultPrivacyMethodAOP {
    //为保证线程访问安全声明的对象锁
    //private var objectImeiLock = Object()
    //private var objectImsiLock = Object()
    //private var objectMeidLock = Object()
    private var objectMacLock = Object()
    private var objectHardMacLock = Object()
    private var objectBluetoothLock = Object()
    private var objectSNLock = Object()
    private var objectAndroidIdLock = Object()

    @PrivacyProxyMethod(
        targetClass = android.os.Build::class,
        targetMethod = "getSerial",
        targetMethodOpcode = PrivacyOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getSerial(): String {
        val key = "getSerial"
        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log("getSerial", "Serial")
            return ""
        }
        synchronized(objectSNLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log("getSerial", "Serial", fromCache = true)
                return PrivacyGuarder.getCachedPrivacy(key)
            }
            var value = ""
            LogAOP.log("getSerial", "Serial")
            try {
                value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
            return value
        }

    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getMeid",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getMeid(manager: TelephonyManager): String {
        LogAOP.log("getMeid", "移动设备标识符-getMeid()")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，所以直接返回空字符串
        return ""

//        val key = "meid"
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "移动设备标识符-getMeid()")
//            return ""
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return ""
//        }
//        synchronized(objectMeidLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "移动设备标识符-getMeid()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//            LogAOP.log(key, "移动设备标识符-getMeid()")
//            var value = ""
//            try {
//                value = manager.meid
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getMeid",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getMeid(manager: TelephonyManager, index: Int): String {
        val key = "meid-$index"
        LogAOP.log(key, "移动设备标识符-getMeid()")
        LogAOP.log("getMeid", "移动设备标识符-getMeid()")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，所以直接返回空字符串
        return ""
//        val key = "meid-$index"
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "移动设备标识符-getMeid()")
//            return ""
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return ""
//        }
//        synchronized(objectMeidLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "移动设备标识符-getMeid()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//            LogAOP.log(key, "移动设备标识符-getMeid()")
//            var value = ""
//            try {
//                value = manager.getMeid(index)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    private var objectDeviceIdLock = Object()

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getDeviceId",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getDeviceId(manager: TelephonyManager): String {
        val key = "TelephonyManager-getDeviceId"
        LogAOP.log(key, "IMEI-getDeviceId()")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，所以直接返回空字符串
        return ""
//        val key = "TelephonyManager-getDeviceId"
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "IMEI-getDeviceId()")
//            return ""
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return ""
//        }
//        synchronized(objectDeviceIdLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "IMEI-getDeviceId()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//
//            LogAOP.log(key, "IMEI-getDeviceId()")
//            var value = ""
//            try {
//                value = manager.getDeviceId()
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getDeviceId",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getDeviceId(manager: TelephonyManager, index: Int): String {
        val key = "TelephonyManager-getDeviceId-$index"
        LogAOP.log(key, "IMEI-getDeviceId(I)")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，所以直接返回空字符串
        return ""
//        val key = "TelephonyManager-getDeviceId-$index"
//
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "IMEI-getDeviceId(I)")
//            return ""
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return ""
//        }
//        synchronized(objectDeviceIdLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "IMEI-getDeviceId()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//
//            LogAOP.log(key, "IMEI-getDeviceId()")
//            var value = ""
//            try {
//                value = manager.getDeviceId(index)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getSubscriberId",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getSubscriberId(manager: TelephonyManager): String {
        val key = "TelephonyManager-getSubscriberId"
        LogAOP.log(key, "IMSI-getSubscriberId(I)")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，所以直接返回空字符串
        return ""
//        val key = "TelephonyManager-getSubscriberId"
//
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "IMSI-getSubscriberId(I)")
//            return ""
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return ""
//        }
//
//        synchronized(objectImsiLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "IMSI-getSubscriberId(I)", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//            LogAOP.log(key, "IMSI-getSubscriberId(I)")
//            var value = ""
//            try {
//                value = manager.subscriberId
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getSubscriberId",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getSubscriberId(manager: TelephonyManager, index: Int): String {
        return getSubscriberId(manager)
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getImei",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getImei(manager: TelephonyManager): String {
        val key = "TelephonyManager-getImei"
        LogAOP.log(key, "IMEI-getImei()")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，而且imei已在高版本无法获取，所以直接返回空字符串
        return ""
//        val key = "TelephonyManager-getImei"
//
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "IMEI-getImei()")
//            return ""
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return ""
//        }
//
//        synchronized(objectImeiLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "IMEI-getImei()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//
//            LogAOP.log(key, "IMEI-getImei()")
//            var value = ""
//            try {
//                value = manager.imei
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getImei",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getImei(manager: TelephonyManager, index: Int): String {
        val key = "TelephonyManager-getImei-$index"
        LogAOP.log(key, "设备id-getImei(I)")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，而且imei已在高版本无法获取，所以直接返回空字符串
        //官方说明：Starting with API level 29, persistent device identifiers are guarded behind additional * restrictions
        return ""
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "设备id-getImei(I)")
//            return ""
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return ""
//        }
//        synchronized(objectImeiLock) {
//
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "设备id-getImei(I)", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//            LogAOP.log(key, "设备id-getImei(I)")
//            var value = ""
//            try {
//                value = manager.getImei(index)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }


    @PrivacyProxyMethod(
        targetClass = Settings.Secure::class,
        targetMethod = "getString",
        targetMethodOpcode = PrivacyOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getString(contentResolver: ContentResolver?, type: String?): String {
        val key = "Secure-getString-$type"
        if ("android_id" != type) {
            return Settings.Secure.getString(contentResolver, type)
        }
        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log("getString", "系统信息", args = listOf(Pair("name", type)))
            return ""
        }
        synchronized(objectAndroidIdLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log(
                    "getString",
                    "系统信息",
                    args = listOf(Pair("name", type)),
                    fromCache = true
                )
                return PrivacyGuarder.getCachedPrivacy(key)
            }

            var value = ""
            try {
                value = Settings.Secure.getString(contentResolver, type)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
            return value
        }
    }


    @PrivacyProxyMethod(
        targetClass = Settings.System::class,
        targetMethod = "getString",
        targetMethodOpcode = PrivacyOpcode.INVOKESTATIC
    )
    @JvmStatic
    fun getStringSystem(contentResolver: ContentResolver?, type: String?): String {
        return getString(contentResolver, type)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledPackages",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
        LogAOP.log("getInstalledPackages", "安装包-getInstalledPackages")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getInstalledPackages(flags)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getPackageInfo",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getPackageInfo(manager: PackageManager,pkg:String, flags: Int): PackageInfo? {
        LogAOP.log("getPackageInfo", "安装包-getPackageInfo")
        if (!PrivacyGuarder.isAgreed()) {
            return null
        }
        return manager.getPackageInfo(pkg,flags)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getApplicationInfo",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getApplicationInfo(manager: PackageManager,pkg:String, flags: Int): ApplicationInfo? {
        LogAOP.log("getApplicationInfo", "安装包-getApplicationInfo")
        if (!PrivacyGuarder.isAgreed()) {
            return null
        }
        return manager.getApplicationInfo(pkg,flags)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledPackagesAsUser",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getInstalledPackagesAsUser(
        manager: PackageManager,
        flags: Int,
        userId: Int
    ): List<PackageInfo> {
        LogAOP.log("getInstalledPackagesAsUser", "安装包-getInstalledPackagesAsUser")
        return getInstalledPackages(manager, flags);
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledApplications",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getInstalledApplications(manager: PackageManager, flags: Int): List<ApplicationInfo> {
        LogAOP.log("getInstalledApplications", "安装包-getInstalledApplications")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getInstalledApplications(flags)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "getInstalledApplicationsAsUser",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getInstalledApplicationsAsUser(
        manager: PackageManager, flags: Int,
        userId: Int
    ): List<ApplicationInfo> {
        LogAOP.log("getInstalledApplicationsAsUser", "安装包-getInstalledApplicationsAsUser")
        return getInstalledApplications(manager, flags);
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "queryIntentActivities",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun queryIntentActivities(
        manager: PackageManager,
        intent: Intent,
        flags: Int
    ): List<ResolveInfo> {
        val paramBuilder = StringBuilder()
        var legal = true
        intent.apply {
            categories?.also {
                paramBuilder.append("-categories:").append(it.toString()).append("\n")
            }
            `package`?.also {
                paramBuilder.append("-packageName:").append(it).append("\n")
            }
            data?.also {
                paramBuilder.append("-data:").append(it.toString()).append("\n")
            }
            component?.packageName?.also {
                paramBuilder.append("-packageName:").append(it).append("\n")
            }
        }

        if (paramBuilder.isEmpty()) {
            legal = false
        }

        //不指定包名则查询不合法
        if (!paramBuilder.contains("packageName")) {
            legal = false
        }
        paramBuilder.append("-合法查询:${legal}").append("\n")
        LogAOP.log("queryIntentActivities", "读安装列表-queryIntentActivities:$paramBuilder")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.queryIntentActivities(intent, flags)
    }

    @PrivacyProxyMethod(
        targetClass = PackageManager::class,
        targetMethod = "queryIntentActivityOptions",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun queryIntentActivityOptions(
        manager: PackageManager,
        caller: ComponentName?,
        specifics: Array<Intent?>?,
        intent: Intent,
        flags: Int
    ): List<ResolveInfo> {
        LogAOP.log("queryIntentActivityOptions", "读安装列表-queryIntentActivityOptions")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
    }

    @PrivacyProxyMethod(
        targetClass = ActivityManager::class,
        targetMethod = "getRunningTasks",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getRunningTasks(
        manager: ActivityManager,
        maxNum: Int
    ): List<ActivityManager.RunningTaskInfo?>? {
        LogAOP.log("getRunningTasks", "当前运行中的任务")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getRunningTasks(maxNum)
    }

    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = ActivityManager::class,
        targetMethod = "getRecentTasks",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getRecentTasks(
        manager: ActivityManager,
        maxNum: Int,
        flags: Int
    ): List<ActivityManager.RecentTaskInfo>? {
        LogAOP.log("getRecentTasks", "最近运行中的任务")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getRecentTasks(maxNum, flags)
    }


    @PrivacyProxyMethod(
        targetClass = ActivityManager::class,
        targetMethod = "getRunningAppProcesses",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> {
        LogAOP.log("getRunningAppProcesses", "当前运行中的进程")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }

        var appProcess: List<ActivityManager.RunningAppProcessInfo> = emptyList()
        try {
            appProcess = manager.runningAppProcesses
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return appProcess
    }


    /**
     * 基站信息，需要开启定位
     */
    @SuppressLint("MissingPermission")
    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getAllCellInfo",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getAllCellInfo(manager: TelephonyManager): List<CellInfo>? {
        LogAOP.log("getAllCellInfo", "定位-基站信息")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.allCellInfo
    }

    @PrivacyProxyMethod(
        targetClass = ClipboardManager::class,
        targetMethod = "getPrimaryClip",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getPrimaryClip(manager: ClipboardManager): ClipData? {
        if (!PrivacyGuarder.isAgreed()) {
            return ClipData.newPlainText("Label", "")
        }
        LogAOP.log("getPrimaryClip", "剪贴板内容-getPrimaryClip")
        return manager.primaryClip
    }

    @PrivacyProxyMethod(
        targetClass = ClipboardManager::class,
        targetMethod = "getPrimaryClipDescription",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
        if (!PrivacyGuarder.isAgreed()) {
            return ClipDescription("", arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN))
        }
        LogAOP.log("getPrimaryClipDescription", "剪贴板内容-getPrimaryClipDescription")
        return manager.primaryClipDescription
    }

    @PrivacyProxyMethod(
        targetClass = ClipboardManager::class,
        targetMethod = "getText",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getText(manager: ClipboardManager): CharSequence? {
        LogAOP.log("getText", "剪贴板内容-getText")
        if (!PrivacyGuarder.isAgreed()) {
            return ""
        }
        return manager.text
    }

    @PrivacyProxyMethod(
        targetClass = ClipboardManager::class,
        targetMethod = "setPrimaryClip",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun setPrimaryClip(manager: ClipboardManager, clip: ClipData) {
        LogAOP.log("setPrimaryClip", "设置剪贴板内容-setPrimaryClip")
        if (!PrivacyGuarder.isAgreed()) {
            return
        }
        manager.setPrimaryClip(clip)
    }

    @PrivacyProxyMethod(
        targetClass = ClipboardManager::class,
        targetMethod = "setText",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun setText(manager: ClipboardManager, clip: CharSequence) {
        LogAOP.log("setText", "设置剪贴板内容-setText")
        if (!PrivacyGuarder.isAgreed()) {
            return
        }
        manager.text = clip
    }

    /**
     * WIFI的SSID
     */
    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = WifiInfo::class,
        targetMethod = "getSSID",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getSSID(manager: WifiInfo): String? {
        LogAOP.log("getSSID", "SSID")
        if (!PrivacyGuarder.isAgreed()) {
            return ""
        }
        return manager.ssid
    }

    /**
     * WIFI的SSID
     */
    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = WifiInfo::class,
        targetMethod = "getBSSID",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getBSSID(manager: WifiInfo): String? {
        LogAOP.log("getBSSID", "BSSID")
        if (!PrivacyGuarder.isAgreed()) {
            return ""
        }
        return manager.bssid
    }

    /**
     * WIFI扫描结果
     */
    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = WifiManager::class,
        targetMethod = "getScanResults",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getScanResults(manager: WifiManager): List<ScanResult>? {
        LogAOP.log("getScanResults", "WIFI扫描结果")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.scanResults
    }


    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = SensorManager::class,
        targetMethod = "getSensorList",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getSensorList(manager: SensorManager, type: Int): List<Sensor>? {
        LogAOP.log("getSensorList", "可用传感器")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getSensorList(type)
    }


    /**
     * DHCP信息
     */
    @JvmStatic
    @PrivacyProxyMethod(
        targetClass = WifiManager::class,
        targetMethod = "getDhcpInfo",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    fun getDhcpInfo(manager: WifiManager): DhcpInfo? {
        LogAOP.log("getDhcpInfo", "DHCP地址")
        if (!PrivacyGuarder.isAgreed()) {
            return null
        }
        return manager.getDhcpInfo()
    }

    /**
     * DHCP信息
     */
    @SuppressLint("MissingPermission")
    @PrivacyProxyMethod(
        targetClass = WifiManager::class,
        targetMethod = "getConfiguredNetworks",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getConfiguredNetworks(manager: WifiManager): List<WifiConfiguration>? {
        LogAOP.log("getConfiguredNetworks", "前台用户配置的所有网络的列表")
        if (!PrivacyGuarder.isAgreed()) {
            return emptyList()
        }
        return manager.getConfiguredNetworks()
    }


    /**
     * 位置信息
     */
    @SuppressLint("MissingPermission")
    @PrivacyProxyMethod(
        targetClass = LocationManager::class,
        targetMethod = "getLastKnownLocation",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getLastKnownLocation(
        manager: LocationManager, provider: String
    ): Location? {
        LogAOP.log("getLastKnownLocation", "上一次的位置信息")
        if (!PrivacyGuarder.isAgreed()) {
            // 这里直接写空可能有风险
            return null
        }
        return manager.getLastKnownLocation(provider)
    }


    @SuppressLint("MissingPermission")
    @PrivacyProxyMethod(
        targetClass = LocationManager::class,
        targetMethod = "requestLocationUpdates",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun requestLocationUpdates(
        manager: LocationManager, provider: String, minTime: Long, minDistance: Float,
        listener: LocationListener
    ) {
        LogAOP.log("requestLocationUpdates", "监视精细行动轨迹")
        if (!PrivacyGuarder.isAgreed()) {
            return
        }
        manager.requestLocationUpdates(provider, minTime, minDistance, listener)
    }

    private var objectSimLock = Object()

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getSimSerialNumber",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getSimSerialNumber(manager: TelephonyManager): String {
        val key = "TelephonyManager-getSimSerialNumber"
        LogAOP.log(key, "SIM卡-getSimSerialNumber()")
        //这个需要READ_PRIVILEGED_PHONE_STATE权限，目前主要用于System app，而且imei已在高版本无法获取，所以直接返回空字符串
        //官方说明：Starting with API level 29, persistent device identifiers are guarded behind additional * restrictions
        return ""
//        val key = "TelephonyManager-getSimSerialNumber"
//        if (!PrivacyGuarder.isAgreed()) {
//            LogAOP.log(key, "SIM卡-getSimSerialNumber()")
//            return ""
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return ""
//        }
//
//        synchronized(objectSimLock) {
//            if (PrivacyGuarder.hasCachedPrivacy(key)) {
//                LogAOP.log(key, "SIM卡-getSimSerialNumber()", fromCache = true)
//                return PrivacyGuarder.getCachedPrivacy(key)
//            }
//            LogAOP.log(key, "SIM卡-getSimSerialNumber()")
//            var value = ""
//            try {
//                value = manager.getSimSerialNumber()
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            } finally {
//                PrivacyGuarder.putCachedPrivacy(key, value)
//            }
//            return value
//        }
    }

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getSimSerialNumber",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
        return getSimSerialNumber(manager)
    }


    private var objectPhoneNumberLock = Object()

    @PrivacyProxyMethod(
        targetClass = TelephonyManager::class,
        targetMethod = "getLine1Number",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getLine1Number(manager: TelephonyManager): String? {

        val key = "TelephonyManager-getLine1Number"

        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log(key, "手机号-getLine1Number")
            return ""
        }
        synchronized(objectPhoneNumberLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log(key, "手机号-getLine1Number", fromCache = true)
                return PrivacyGuarder.getCachedPrivacy(key)
            }
            LogAOP.log(key, "手机号-getLine1Number")
            var value = ""
            try {
                value = manager.line1Number
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
            return value
        }
    }


    @PrivacyProxyMethod(
        targetClass = WifiInfo::class,
        targetMethod = "getMacAddress",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getMacAddress(manager: WifiInfo): String {
        val key = "WifiInfo-getMacAddress"

        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log(key, "mac地址-getMacAddress")
            return ""
        }

        synchronized(objectMacLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log(key, "mac地址-getMacAddress", fromCache = true)
                return PrivacyGuarder.getCachedPrivacy(key)
            }
            LogAOP.log(key, "mac地址-getMacAddress")
            var value = ""
            try {
                if (PrivacyGuarder.app?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } == PackageManager.PERMISSION_GRANTED
                ) {
                    value = manager.getMacAddress()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
            return value
        }
    }


    @PrivacyProxyMethod(
        targetClass = NetworkInterface::class,
        targetMethod = "getHardwareAddress",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getHardwareAddress(manager: NetworkInterface): ByteArray {
        val key = "NetworkInterface-getHardwareAddress"

        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log(key, "mac地址-getHardwareAddress")
            return ByteArray(1)
        }
        synchronized(objectHardMacLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log(key, "mac地址-getHardwareAddress", fromCache = true)
                val v = PrivacyGuarder.getCachedPrivacy(key)
                return v.toByteArray(StandardCharsets.UTF_8)
            }

            LogAOP.log(key, "mac地址-getHardwareAddress")
            var value = ByteArray(1)
            try {
                value = manager.hardwareAddress
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, String(value, StandardCharsets.UTF_8))
            }
            return value
        }
    }


    @PrivacyProxyMethod(
        targetClass = BluetoothAdapter::class,
        targetMethod = "getAddress",
        targetMethodOpcode = PrivacyOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    fun getAddress(manager: BluetoothAdapter): String {
        val key = "BluetoothAdapter-getAddress"

        if (!PrivacyGuarder.isAgreed()) {
            LogAOP.log(key, "蓝牙地址-getAddress")
            return ""
        }
        synchronized(objectBluetoothLock) {
            if (PrivacyGuarder.hasCachedPrivacy(key)) {
                LogAOP.log(key, "蓝牙地址-getAddress", fromCache = true)
                return PrivacyGuarder.getCachedPrivacy(key)
            }

            LogAOP.log(key, "蓝牙地址-getAddress")
            var value = ""
            try {
                value = manager.address
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                PrivacyGuarder.putCachedPrivacy(key, value)
            }
            return value
        }
    }

}