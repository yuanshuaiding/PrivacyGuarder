package com.eric.manager.privacy.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.eric.manager.privacy.BuildConfig
import com.eric.manager.privacyproxy.PrivacyGuarder

/**
 * @Description:
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 15:13
 * @Version: 1.0
 */
class MyApp : MultiDexApplication() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //尽可能早的初始化
        PrivacyGuarder
            .Builder(this)
            .showLog(BuildConfig.DEBUG)
            //缓存已获取的隐私数据
            .cacheable(true)
            //隐私数据使用mmkv缓存，默认加密，也可以设置自己的加密key
            .cacheEncryptKey("123123")
            //默认未同意
            .isAgreed(false)
            .init()
    }
}