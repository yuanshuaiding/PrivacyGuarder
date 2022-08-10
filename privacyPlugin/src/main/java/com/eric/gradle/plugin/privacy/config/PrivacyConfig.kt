package com.eric.gradle.plugin.privacy.config

/**
 * @Description: 拓展配置（必须为非final类）
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/27 15:55
 * @Version: 1.0
 */
open class PrivacyConfig {
    //插件是否生效：默认关闭
    var apply: Boolean? = false

    //永久禁止使用的隐私api，不论是否同意隐私政策
    var forbidden: Set<String>? = null

}