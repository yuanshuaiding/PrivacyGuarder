package com.eric.gradle.plugin.privacy.transformFactory

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 用于构造配置信息
 */
interface PrivacyAOPParams : InstrumentationParameters {
    //包名列表，改列表里的字节码会被忽略，不参与AOP
    @get:Input
    val ignoredPKG: ListProperty<String>

    //永久禁用隐私API列表，这些API无论是否同意隐私政策，均不允许调用
    @get:Input
    val forbiddenAPI: ListProperty<String>

    //静态扫描结果文件，用于保存编译期AOP的原系统隐私API调用路径
    @get:Input
    val outFile: Property<String>
}

/**
 * @Description: 隐私API代理asm，注意需要为抽象类
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/1 15:21
 * @Version: 1.0
 */
abstract class PrivacyAOPTransformFactory : AsmClassVisitorFactory<PrivacyAOPParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PrivacyAOPVisitor(nextClassVisitor)
    }

    /**
     * 判断哪些类需要应用AOP，比如忽略名单里的就不需要
     */
    override fun isInstrumentable(classData: ClassData) = true
}

/**
 * 有两种主要方式访问系统隐私API：直接访问系统隐私属性，如：
 *  var
 *
 */
private class PrivacyAOPVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {
    //TODO 目前只处理方法体里出现的隐私API调用，其实属性也有可能，如在类的成员属性声明时直接赋值 val sn=Build.getSerial()，但这个比较少见，不花精力处理了
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        //获取原始methodVisitor
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        //使用原始methodVisitor构造新的methodVisitor，并完成AOP操作，此处使用AdviceAdapter是因为它是MethodVisitor的子类并做了进一步封装，使用起来更简单
        return object : AdviceAdapter(api, methodVisitor, access, name, descriptor) {


        }
    }
}