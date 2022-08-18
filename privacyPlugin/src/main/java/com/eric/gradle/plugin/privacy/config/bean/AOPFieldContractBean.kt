package com.eric.gradle.plugin.privacy.config.bean

import com.eric.manager.privacy.annotation.PrivacyOpcode

/**
 * @Description: 被代理的原生隐私方法与代理方法之间的对应关系描述，使用ASM bytecode viewer插件对比发现，只需要指定类名，方法名，方法签名，即可使用asm框架完成字节码修改
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 16:27
 * @Version: 1.0
 */
data class AOPFieldContractBean(
    var targetClass: String = "",// 原始类名
    var targetField: String = "",// 原始方法名
    var targetFieldOpcode: Int? = PrivacyOpcode.INVOKESTATIC.opcode,// 原属性的调用方式,默认静态调用（由于代理属性我们直接静态调用，所以此处不再声明proxyFieldOpcode）
    var targetFieldDescriptor: String = "",// 原始属性签名
    var proxyClass: String = "",// 代理的类名
    var proxyField: String = "",// 代理的属性名
    var proxyFieldDescriptor: String = ""// 代理的属性签名
)
