package com.eric.manager.privacyproxy

import android.app.Application
import com.eric.manager.privacyproxy.log.LogAOP
import com.tencent.mmkv.MMKV

/**
 * @Description: 隐私工具类：用于判断是否同意隐私等【这里使用MMKV来存储相关信息】
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 10:53
 * @Version: 1.0
 */
object PrivacyGuarder {
    private const val MMKVID = "privacyGuarderKV"
    private var aesEncrypt = "#Eric#Priv@cyGu@der"// 默认信息加密秘钥，如果需要修改请在Builder构建时赋值
    var app: Application? = null
    private var showLog: Boolean? = true
    private var isAgreed: Boolean? = false
    private var cacheable: Boolean? = false
    private var kv: MMKV? = null

    fun config(builder: Builder): PrivacyGuarder {
        this.app = builder.app
        this.showLog = builder.showLog
        this.cacheable = builder.cacheable
        this.setAgreement(builder.isAgreed ?: false)
        this.aesEncrypt =
            if (builder.aesEncrypt.isNullOrEmpty()) aesEncrypt else builder.aesEncrypt!!
        MMKV.initialize(app)
        kv = MMKV.mmkvWithID(MMKVID, MMKV.SINGLE_PROCESS_MODE, aesEncrypt)
        return this
    }

    /**
     * 判断是否已同意隐私
     */
    fun isAgreed(): Boolean {
        isAgreed = kv?.getBoolean("isPrivacyGuarderAgreed", false)
        return isAgreed == true
    }

    fun setAgreement(agreed: Boolean) {
        kv?.encode("isPrivacyGuarderAgreed", agreed)
    }

    fun showLog(): Boolean {
        return showLog == true
    }

    fun hasCachedPrivacy(key: String): Boolean {
        if (cacheable == true) {
            return kv?.getString(key, null) != null
        }
        return false
    }

    fun getCachedPrivacy(key: String): String {
        return kv?.getString(key, "") ?: ""
    }

    fun putCachedPrivacy(key: String, value: String) {
        kv?.encode(key, value)
    }

    /**
     * Build模式方便初始化相关操作
     */
    class Builder(internal val app: Application) {
        internal var showLog: Boolean? = true
        internal var isAgreed: Boolean? = false
        internal var cacheable: Boolean? = false
        internal var aesEncrypt: String? = ""

        fun showLog(show: Boolean): Builder {
            this.showLog = show
            return this
        }

        fun isAgreed(agreed: Boolean): Builder {
            this.isAgreed = agreed
            return this
        }

        /**
         * 一些静态隐私数据是否开启缓存，防止频繁获取
         */
        fun cacheable(cache: Boolean): Builder {
            this.cacheable = cache
            return this
        }

        /**
         * 设置缓存加密的key
         */
        fun cacheEncryptKey(key: String): Builder {
            this.aesEncrypt = key
            return this
        }

        fun init(): PrivacyGuarder {
            return config(this)
        }
    }

}