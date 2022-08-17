package com.eric.gradle.plugin.privacy.config

/**
 * @Description: AOP辅助类，用于缓存被代理方法与代理方法直接的映射列表，以及从映射列表中找到匹配项进行AOP
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 18:11
 * @Version: 1.0
 */
object AOPHelper {

    val aopMethodBeans = mutableListOf<AOPMethodBean>()


    fun find(ownerClass: String?, methodName: String?, descriptor: String?): AOPMethodBean? {
        if (methodName == "") {
            return null
        }

        return aopMethodBeans.find {
            isAOPItem(it, methodName ?: "", ownerClass ?: "", descriptor ?: "")
        }
    }

    private fun isAOPItem(
        aopItem: AOPMethodBean,
        methodName: String,
        classOwnerName: String = "",
        methodDesc: String = ""
    ): Boolean {
        if (methodName.isEmpty()) {
            return false
        }
        return if (classOwnerName.isEmpty() && methodDesc.isNotEmpty()) {
            methodName == aopItem.targetMethod && methodDesc == aopItem.targetMethodDescriptor
        } else if (classOwnerName.isNotEmpty() && methodDesc.isEmpty()) {
            methodName == aopItem.targetMethod && classOwnerName == aopItem.targetClass
        } else if (classOwnerName.isNotEmpty() && methodDesc.isNotEmpty()) {
            methodName == aopItem.targetMethod && classOwnerName == aopItem.targetClass && methodDesc == aopItem.targetMethodDescriptor
        } else {
            methodName == aopItem.targetMethod
        }
    }

}