package com.eric.manager.privacy.annotation

import kotlin.reflect.KClass

/**
 * @Description: 用于指定需要AOP的方法，需要明确方法名，方法所在类的全路径
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/12 15:32
 * @Version: 1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class PrivacyProxyMethod(
    val targetClass: KClass<*>,//原方法所在类名
    val targetMethod: String,//原方法名
    val targetMethodOpcode: PrivacyMethodOpcode//用于判断是静态方法调用还是实例方法调用
)