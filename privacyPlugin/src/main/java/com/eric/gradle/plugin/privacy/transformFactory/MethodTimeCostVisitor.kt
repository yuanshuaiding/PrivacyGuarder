package com.eric.gradle.plugin.privacy.transformFactory

import com.eric.manager.privacy.annotation.TimeCost
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @Description: 方法耗时ClassVisitor，负责在方法的开头和结尾插入具体的统计代码
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/29 10:22
 * @Version: 1.0
 */
class MethodTimeCostVisitor(nextClassVisitor: ClassVisitor, val className: String) :
    ClassVisitor(Opcodes.ASM9, nextClassVisitor) {

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
        val newMethodVisitor = object :
            AdviceAdapter(api, methodVisitor, access, name, descriptor) {
            var isHookMethod = false

            override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
                //根据注解判断是否为需要hook的方法(Type.getDescriptor（）的返回值为："Lcom/eric/manager/privacy/annotation/TimeCost;")
                if (Type.getDescriptor(TimeCost::class.java) == descriptor) {
                    isHookMethod = true
                    println("${name}发现方法耗时注解:${descriptor}")
                }
                return super.visitAnnotation(descriptor, visible)
            }

            override fun onMethodEnter() {
                if (isHookMethod) {
                    //添加时间统计代码
                    mv.visitLdcInsn(name)
                    mv.visitLdcInsn(className)
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        "com/eric/manager/privacy/app/aop/AOP4MethodCost",
                        "putStartTime",
                        "(Ljava/lang/String;Ljava/lang/String;)V",
                        false
                    )
                }
                super.onMethodEnter()
            }

            override fun onMethodExit(opcode: Int) {
                if (isHookMethod) {
                    mv.visitLdcInsn(name)
                    mv.visitLdcInsn(className)
                    methodVisitor.visitMethodInsn(
                        INVOKESTATIC,
                        "com/eric/manager/privacy/app/aop/AOP4MethodCost",
                        "putEndTime",
                        "(Ljava/lang/String;Ljava/lang/String;)V",
                        false
                    )
                }
                super.onMethodExit(opcode)
            }


        }
        return newMethodVisitor
    }
}