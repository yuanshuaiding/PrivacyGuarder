package com.eric.manager.privacy.annotation

/**
 * @Description: 代理方法发调用方式（成员方法还是静态方法等）
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/16 15:07
 * @Version: 1.0
 */
enum class PrivacyMethodOpcode(val opcode: Int) {
    /**
     * ASM框架定义的JVM方法和属性访问类型有如下几种，详见org.objectweb.asm.Opcodes类：
     *
     *  int GETSTATIC = 178; // visitFieldInsn
     *  int PUTSTATIC = 179; // -
     *  int GETFIELD = 180; // -
     *  int PUTFIELD = 181; // -
     *  int INVOKEVIRTUAL = 182; // visitMethodInsn
     *  int INVOKESPECIAL = 183; // -
     *  int INVOKESTATIC = 184; // -
     *  int INVOKEINTERFACE = 185; // -
     *
     *
     *  我们主要用到的是实例方法和静态方法调用两种，所以我们自定义几个可能会用到的枚举对象，项目依赖privacyAnnotation库也主要使用这几个枚举定义代理方法的注解，其他类型的调用方式暂不支持
     */

    //调用对象的实例方法
    INVOKEVIRTUAL(182),

    //调用特殊方法，比如初始化，私有方法，父类方法
    INVOKESPECIAL(183),

    //调用类的静态方法
    INVOKESTATIC(184),

    //调用接口方法
    INVOKEINTERFACE(185),
}