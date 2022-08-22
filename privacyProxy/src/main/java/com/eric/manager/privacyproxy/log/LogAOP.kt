package com.eric.manager.privacyproxy.log

import android.util.Log
import com.eric.manager.privacyproxy.PrivacyGuarder
import java.text.MessageFormat

/**
 * @Description: 日志输出工具类
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/19 11:16
 * @Version: 1.0
 */
object LogAOP {
    fun log(
        privacyName: String,
        privacyDesc: String,
        args: List<Pair<String, Any>>? = null,
        fromCache: Boolean = false
    ) {
        if (PrivacyGuarder.showLog()) {
            val argStr = StringBuilder("")
            if (args.isNullOrEmpty()) {
                argStr.append("\n")
            } else {
                for (arg in args) {
                    argStr.append(arg.first).append("=").append(arg.second.toString()).append("\n")
                }
            }
            Log.d(
                "PrivacyGuarderLog",
                "\n隐私名称：$privacyName\n" + "参数：${argStr}" + "隐私描述：$privacyDesc\n" + "是否同意：$fromCache\n" + "命中缓存：$fromCache\n" + "堆栈：\n${getStackTrace()}"
            )
        }
    }

    private fun getStackTrace(): String {
        val st = Thread.currentThread().stackTrace
        val sbf = StringBuilder()
        for (e in st) {
            if (e.methodName.equals("getThreadStackTrace") || e.methodName.equals("getStackTrace")) {
                continue
            }
            if (e.className.contains("com.eric.manager.privacyproxy")) {
                continue
            }
            if (sbf.isNotEmpty()) {
                sbf.append(" <- ")
                sbf.append(System.getProperty("line.separator"))
            }
            sbf.append(
                MessageFormat.format(
                    "{0}.{1}() {2}", e.className, e.methodName, e.lineNumber
                )
            )
        }
        return sbf.toString()
    }
}