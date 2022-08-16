package com.eric.manager.privacy.annotation

/**
 * @Description: 用于指定某个类是否为隐私API代理规则类
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/12 15:32
 * @Version: 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class PrivacyProxyClass