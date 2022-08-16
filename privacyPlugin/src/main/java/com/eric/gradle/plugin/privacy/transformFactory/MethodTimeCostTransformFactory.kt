package com.eric.gradle.plugin.privacy.transformFactory

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

/**
 * @Description: 统计方法耗时的transform，AsmClassVisitorFactory即创建ClassVisitor对象的工厂。此接口的实现必须是一个抽象类
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/28 15:24
 * @Version: 1.0
 */
abstract class MethodTimeCostTransformFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {
    /**
     * 返回我们自定义的ClassVisitor，在自定义Visitor处理完成后，需要传内容传递给下一个Visitor，因此我们将nextClassVisitor放在构造函数中传入
     */
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return MethodTimeCostVisitor(nextClassVisitor,classContext.currentClassData.className)
    }

    /**
     * 用于控制我们的自定义Visitor是否需要处理这个类，通过这个方法可以过滤我们不需要的类，加快编译速度.
     * 比如:只对我们正在开发的app进行处理，则判断classData.className是否含有app的包名
     */
    override fun isInstrumentable(classData: ClassData) =classData.className.contains("com.eric.manager.privacy")
}