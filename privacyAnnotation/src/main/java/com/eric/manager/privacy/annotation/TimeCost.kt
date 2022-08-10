package com.eric.manager.privacy.annotation

/**
 * @Description: 方法耗时注解(因为是编译期，所以需要指定Retention为BINARY类型)
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/29 14:52
 * @Version: 1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class TimeCost
