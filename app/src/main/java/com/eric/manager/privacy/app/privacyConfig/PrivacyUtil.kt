package com.eric.manager.privacy.app.privacyConfig

import android.app.Application
import android.content.Context

/**
 * @Description: 隐私工具类：用于判断是否同意隐私等【这里使用SP来存储是否同意隐私的标识】
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 10:53
 * @Version: 1.0
 */
object PrivacyUtil {
    private lateinit var app: Application

    /**
     * 方法调用前需要先初始化
     */
    fun init(app: Application) {
        this.app = app
    }

    //判断是否已同意隐私
    fun isAgreed(): Boolean {
        return app.getSharedPreferences("cachedSp", Context.MODE_PRIVATE)
            .getBoolean("isPrivacyAgreed", false)
    }

    fun agree() {
        app.getSharedPreferences("cachedSp", Context.MODE_PRIVATE)?.edit()
            ?.putBoolean("isPrivacyAgreed", true)?.apply()
    }

    fun disagree() {
        app.getSharedPreferences("cachedSp", Context.MODE_PRIVATE)?.edit()
            ?.putBoolean("isPrivacyAgreed", false)?.apply()
    }

}