package com.eric.gradle.plugin.privacy.config.bean

import com.eric.manager.privacy.annotation.PrivacyOpcode

/**
 * @Description: 被代理的原生隐私方法与代理方法之间的对应关系描述，使用ASM bytecode viewer插件对比发现，只需要指定类名，方法名，方法签名，即可使用asm框架完成字节码修改
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 16:27
 * @Version: 1.0
 */
data class AOPMethodContractBean(
    var targetClass: String = "",// 原始类名
    var targetMethod: String = "",// 原始方法名
    var targetMethodOpcode: Int? = PrivacyOpcode.INVOKESTATIC.opcode,// 原方法的调用方式,默认静态调用（由于代理方法我们直接静态调用，所以此处不再声明proxyMethodOpcode）
    var targetMethodDescriptor: String = "",// 原始方法签名（主要用于解决方法重载这种情况，targetClass+targetMethod+targetMethodDescriptor基本可以唯一确定某个方法）
    var proxyClass: String = "",// 代理的类名
    var proxyMethod: String = "",// 代理的方法名
    var proxyMethodDescriptor: String = ""// 代理的方法签名
)
