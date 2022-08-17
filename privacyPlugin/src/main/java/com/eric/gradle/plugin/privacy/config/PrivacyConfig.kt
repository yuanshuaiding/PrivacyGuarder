package com.eric.gradle.plugin.privacy.config

/**
 * @Description: 拓展配置（必须为非final类）
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/27 15:55
 * @Version: 1.0
 */
open class PrivacyConfig {
    //生成的AOP结果文件名称
    var outFile: String? = "privacyProxyResult"

    //插件是否生效：默认关闭
    var apply: Boolean? = false

    //不需与参与AOP的包【某些情况下一些包不需要AOP，这样可以加快编译速度，还有一些情况是无法AOP，如高德地图SDK】
    var ignorePKG: Set<String>? = null

    //永久禁止使用的隐私api，不论是否同意隐私政策
    var forbidden: Set<String>? = null

}