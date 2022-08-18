package com.eric.gradle.plugin.privacy.transformFactory

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.eric.gradle.plugin.privacy.config.AOPHelper
import com.eric.gradle.plugin.privacy.config.bean.AOPFieldResultBean
import com.eric.gradle.plugin.privacy.config.bean.AOPMethodResultBean
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.*
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
    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className.replace("/", ".")
        //如果在忽略名单里，则不参与AOP
        if (parameters.get().ignoredPKG.get().find {
                className.contains(it)
            } != null) {
            return false
        }
        //使用了PrivacyProxyClass注解的类（即AOP规则类），这个类已经处理了隐私合规问题
        val classAnn = classData.classAnnotations
        if (classAnn.find {
                it == "com.eric.manager.privacy.annotation.PrivacyProxyClass"
            } != null) {
            return false
        }
        //一些已知不会调用隐私API的库也不需要AOP，如自动生成的R类，Android官方库等（org.intellij.lang.annotations.Subst）
        if (className.endsWith(".R")
            || className.endsWith(".BuildConfig")
            || className.contains("android.support.")
            || className.contains("android.arch.")
            || className.contains("android.app.")
            || className.contains("android.material")
            || className.startsWith("androidx.")
            //kotlin相关库
            || className.startsWith("kotlin.")
            //intellij相关库
            || className.startsWith("org.intellij.")
            // 当前库本身(注解库，其他辅助工具库)
            || className.contains("com.eric.manager.privacy.annotation")
        ) {
            return false
        }
        return true
    }
}


/**
 * 有两种主要方式访问系统隐私API：
 * 1.直接访问系统隐私属性，如：
 *  val sn=Build.SERIAL
 * 2.访问系统隐私方法，如：
 *  val sn=Build.getSerial()
 */
private class PrivacyAOPVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {
    private var originClassName: String? = ""
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (name != null) {
            originClassName = name
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        //获取原始methodVisitor
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val originMethodName = name
        //使用原始methodVisitor构造新的methodVisitor，并完成AOP操作，此处使用AdviceAdapter是因为它是MethodVisitor的子类并做了进一步封装，使用起来更简单
        return object : AdviceAdapter(api, methodVisitor, access, name, descriptor) {
            //类体内方法指令调用（对应java代码里某一行方法调用代码）
            override fun visitMethodInsn(
                opcodeAndSource: Int,
                owner: String?,
                name: String?,
                descriptor: String?,
                isInterface: Boolean
            ) {
                //从收集到的AOP映射列表里取出对应项
                val aopBean = AOPHelper.findAopMethod(owner, name, descriptor)
                if (aopBean != null) {
                    //找到匹配的AOP项后，进行字节码替换(注意为了方便使用，代理方法为静态方法，所以这里opcode写死了INVOKESTATIC)
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        aopBean.proxyClass,
                        aopBean.proxyMethod,
                        aopBean.proxyMethodDescriptor,
                        false
                    )

                    //将AOP结果保留，全部替换完后输出到指定文件
                    AOPHelper.addMethodAOPResult(AOPMethodResultBean(originClassName ?: "", originMethodName ?: "", aopBean))
                    return
                }
                super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
            }

            //类体里除了会出现隐私API调用，其实隐私属性也有可能被访问，如：val sn=Build.SERIAL
            //类体内变量访问指令（对应java代码里某一行变量调用代码）
            override fun visitFieldInsn(
                opcode: Int,
                owner: String?,
                name: String?,
                descriptor: String?
            ) {
                //从收集到的AOP映射列表里取出对应项
                val aopBean = AOPHelper.findAopField(owner, name, descriptor)
                if (aopBean != null) {
                    mv.visitFieldInsn(Opcodes.GETSTATIC,aopBean.proxyClass,aopBean.proxyField,aopBean.proxyFieldDescriptor)

                    //将AOP结果保留，全部替换完后输出到指定文件
                    AOPHelper.addFieldAOPResult(AOPFieldResultBean(originClassName ?: "", name ?: "", aopBean))

                    return
                }
                super.visitFieldInsn(opcode, owner, name, descriptor)
            }

        }
    }
}