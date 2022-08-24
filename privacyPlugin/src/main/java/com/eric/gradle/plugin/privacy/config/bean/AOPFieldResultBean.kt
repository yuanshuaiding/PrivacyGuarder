package com.eric.gradle.plugin.privacy.config.bean

/**
 * @Description: AOP结果封装（哪些类的哪些方法里的隐私属性访问被替换了）
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/18 11:09
 * @Version: 1.0
 */
data class AOPFieldResultBean(
    var originClass: String,
    var originField: String,
    val lineNum: Int?,
    var aopBean: AOPFieldContractBean
)
