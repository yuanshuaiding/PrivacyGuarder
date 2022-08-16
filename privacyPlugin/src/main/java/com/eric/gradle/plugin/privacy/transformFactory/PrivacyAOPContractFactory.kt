package com.eric.gradle.plugin.privacy.transformFactory

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.eric.gradle.plugin.privacy.config.AOPHelper
import com.eric.gradle.plugin.privacy.config.AOPMethodBean
import com.eric.manager.privacy.annotation.PrivacyMethodOpcode
import com.eric.manager.privacy.annotation.PrivacyProxyClass
import com.eric.manager.privacy.annotation.PrivacyProxyMethod
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Description: 负责解析项目中使用PrivacyProxyClass注解标识的类，这个类约定了哪些API需要被AOP以及我们自己的实现，为了方便，自己的实现需要为静态方法
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/12 15:17
 * @Version: 1.0
 */
abstract class PrivacyAOPContractFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PrivacyContractVisitor(Opcodes.ASM9, nextClassVisitor)
    }

    // 由于代理规则是基于注解的，所以这里只处理有指定注解PrivacyProxyClass的类
    override fun isInstrumentable(classData: ClassData): Boolean {
        return !classData.classAnnotations.find { ann ->
            ann.contains("PrivacyProxyClass")
        }.isNullOrEmpty()
    }
}

/**
 * 自定义ClassVisitor，用于对代理规则类的解析。
 */
class PrivacyContractVisitor(api: Int, nextClassVisitor: ClassVisitor) :
    ClassVisitor(api, nextClassVisitor) {
    init {
        println("进入代理规则类解析...")
    }

    private var isContract = false
    private var className: String? = ""
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
            className = name
        }
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        //二次确认，解析的是PrivacyProxyClass注解的类
        if (descriptor == Type.getDescriptor(PrivacyProxyClass::class.java)) {
            isContract = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val vm = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (isContract) {
            //解析代理规则类，并把解析后的对应规则保存到临时列表里
            return PrivacyProxyMethodVisitor(
                api,
                vm,
                access,
                name,
                className ?: "",
                descriptor
            )
        }
        return vm
    }

    override fun visitEnd() {
        super.visitEnd()
        //打印AOP映射关系
        println("AOP映射关系：")
        for (bean in AOPHelper.aopMethodBeans) {
            println(bean.toString())
        }
    }
}

//自定义MethodVisitor，用于解析方法注解
class PrivacyProxyMethodVisitor(
    api: Int,
    classVisitor: MethodVisitor,
    access: Int,
    name: String,
    private val className: String,
    descriptor: String
) : AdviceAdapter(api, classVisitor, access, name, descriptor) {

    //通过对方法注解的解析，可以构建出需要被代理的原生方法及代理方法对应关系
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val va = super.visitAnnotation(descriptor, visible)
        if (descriptor == Type.getDescriptor(PrivacyProxyMethod::class.java)) {
            //生成映射类
            return PrivacyProxyMethodAnnotationVisitor(
                api,
                va,
                name,
                className,
                super.methodDesc ?: ""
            )
        }
        return va
    }
}

//自定义
class PrivacyProxyMethodAnnotationVisitor(
    api: Int,
    va: AnnotationVisitor,
    methodName: String,
    className: String,
    descriptor: String
) :
    AnnotationVisitor(api, va) {
    private var aopBean = AOPMethodBean(
        proxyClass = className,
        proxyMethod = methodName,
        proxyMethodDescriptor = descriptor
    )

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        when (name) {
            "targetClass" -> {
                val clazz = value.toString()
                //Landroid/content/pm/PackageManager;转为android/content/pm/PackageManager
                //aopBean的targetClass和proxyClass都处理为斜杠分割的形式
                aopBean.targetClass = clazz.substring(1, clazz.length - 1)
            }
            "targetMethod" -> aopBean.targetMethod = value.toString()
        }
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {
        super.visitEnum(name, descriptor, value)
        //targetMethodOpcode注解属性是枚举类型，需要在这个方法获取
        if ("targetMethodOpcode" == name) {
            aopBean.targetMethodOpcode = value?.let { PrivacyMethodOpcode.valueOf(it).opcode }
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        // 需要注意的是，如果是实例方法调用，由于我们定义的代理方法是完全仿照原方法的，除了多了第一个参数是该实例自身，而原方法是没有这个参数，所以proxyMethodDescriptor的值裁剪掉第一个参数，就跟原方法的一致了
        if (aopBean.targetMethodOpcode == PrivacyMethodOpcode.INVOKEVIRTUAL.opcode || aopBean.targetMethodOpcode == PrivacyMethodOpcode.INVOKEINTERFACE.opcode) {
            aopBean.targetMethodDescriptor =
                aopBean.proxyMethodDescriptor.replace("L${aopBean.targetClass};", "")
        } else if (aopBean.targetMethodOpcode == PrivacyMethodOpcode.INVOKESTATIC.opcode) {
            //原方法是静态调用的，则跟代理方法的签名信息一致
            aopBean.targetMethodDescriptor = aopBean.proxyMethodDescriptor
        }
        //保存此次结果，后面正式进行字节码替换时，会从这个映射数据中进行匹配
        AOPHelper.aopMethodBeans.add(aopBean)
    }
}
