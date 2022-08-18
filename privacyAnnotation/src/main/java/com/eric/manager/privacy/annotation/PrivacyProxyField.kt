package com.eric.manager.privacy.annotation

import kotlin.reflect.KClass

/**
 * @Description: 用于指定需要AOP的属性，需要明确属性名，属性所在类的全路径
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/12 15:32
 * @Version: 1.0
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class PrivacyProxyField(
    val targetClass: KClass<*>,//原属性所在类名
    val targetField: String,//原属性名
    val targetFieldOpcode: PrivacyOpcode//用于判断是静态调用还是实例调用
)