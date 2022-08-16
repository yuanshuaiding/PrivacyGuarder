package com.eric.manager.privacy.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.eric.manager.privacy.app.privacyConfig.PrivacyUtil

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
        PrivacyUtil.init(this)
    }
}