package com.eric.gradle.plugin.privacy.config

/**
 * @Description: AOP辅助类，用于缓存被代理方法与代理方法直接的映射列表，以及从映射列表中找到匹配项进行AOP
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 18:11
 * @Version: 1.0
 */
object AOPHelper {
    val aopMethodBeans = mutableListOf<AOPMethodBean>()
}