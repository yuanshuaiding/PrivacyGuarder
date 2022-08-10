package com.eric.gradle.plugin.privacy.transformFactory

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * @Description: 隐私API代理asm
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/1 15:21
 * @Version: 1.0
 */
interface PrivacyAOPParams : InstrumentationParameters {
    @get:Input
    val ignored: ListProperty<String>

    @get:Input
    val forbidden: ListProperty<String>

    @get:Input
    val outFile: Property<String>
}

abstract class PrivacyAOPTransformFactory : AsmClassVisitorFactory<PrivacyAOPParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PrivacyAOPVisitor(nextClassVisitor)
    }

    /**
     * 当前类是否需要应用AOP，这里用于排查忽略名单里的
     */
    override fun isInstrumentable(classData: ClassData) = true
}

/**
 * 有两种主要方式访问系统隐私API：直接访问系统隐私属性，如：
 *  var
 *
 */
private class PrivacyAOPVisitor(val classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {


}