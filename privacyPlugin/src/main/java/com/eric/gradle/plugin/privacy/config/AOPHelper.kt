package com.eric.gradle.plugin.privacy.config

import com.eric.gradle.plugin.privacy.config.bean.AOPFieldContractBean
import com.eric.gradle.plugin.privacy.config.bean.AOPFieldResultBean
import com.eric.gradle.plugin.privacy.config.bean.AOPMethodContractBean
import com.eric.gradle.plugin.privacy.config.bean.AOPMethodResultBean

/**
 * @Description: AOP辅助类，用于缓存被代理方法与代理方法直接的映射列表，以及从映射列表中找到匹配项进行AOP
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/8/15 18:11
 * @Version: 1.0
 */
object AOPHelper {

    //记录隐私方法代理规则
    val aopMethodBeans = mutableListOf<AOPMethodContractBean>()

    //记录隐私属性代理规则
    val aopFieldBeans = mutableListOf<AOPFieldContractBean>()

    //记录已完成的AOP结果(key:被替换的原生类名+方法名，value：替换后的类和方法列表)
    val aopMethodResultBeans = mutableMapOf<String, ArrayList<String>>()

    //记录已完成的AOP结果(key:被替换的原生类名+属性名，value：替换后的类和属性列表)
    val aopFieldResultBeans = mutableMapOf<String, ArrayList<String>>()


    fun findAopField(
        ownerClass: String?,
        fieldName: String?,
        descriptor: String?
    ): AOPFieldContractBean? {
        if (fieldName == "") {
            return null
        }
        return aopFieldBeans.find {
            isAOPField(it, fieldName ?: "", ownerClass ?: "", descriptor ?: "")
        }
    }

    fun findAopMethod(
        ownerClass: String?,
        methodName: String?,
        descriptor: String?
    ): AOPMethodContractBean? {
        if (methodName == "") {
            return null
        }

        return aopMethodBeans.find {
            isAOPMethod(it, methodName ?: "", ownerClass ?: "", descriptor ?: "")
        }
    }

    private fun isAOPField(
        aopItem: AOPFieldContractBean,
        fieldName: String,
        classOwnerName: String = "",
        fieldDesc: String = ""
    ): Boolean {
        if (fieldName.isEmpty()) {
            return false
        }
        return if (classOwnerName.isEmpty() && fieldDesc.isNotEmpty()) {
            fieldName == aopItem.targetField && fieldDesc == aopItem.targetFieldDescriptor
        } else if (classOwnerName.isNotEmpty() && fieldDesc.isEmpty()) {
            fieldName == aopItem.targetField && classOwnerName == aopItem.targetClass
        } else if (classOwnerName.isNotEmpty() && fieldDesc.isNotEmpty()) {
            fieldName == aopItem.targetField && classOwnerName == aopItem.targetClass && fieldDesc == aopItem.targetFieldDescriptor
        } else {
            fieldName == aopItem.targetField
        }
    }

    private fun isAOPMethod(
        aopItem: AOPMethodContractBean,
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

    fun addMethodAOPResult(aopResult: AOPMethodResultBean) {
        val key =
            aopResult.aopBean.targetClass.replace("/", ".") + "." + aopResult.aopBean.targetMethod
        var bean = aopMethodResultBeans[key]
        if (bean == null) {
            bean = arrayListOf()
        }
        val aop = aopResult.originClass.replace("/", ".") +
                "." + aopResult.originMethod + "：" + aopResult.lineNum +
                " --> " +
                aopResult.aopBean.proxyClass.replace("/", ".") +
                "." + aopResult.aopBean.proxyMethod
        if (!bean.contains(aop)) {
            bean.add(aop)
        }
        aopMethodResultBeans[key] = bean
    }

    fun addFieldAOPResult(aopResult: AOPFieldResultBean) {
        val key =
            aopResult.aopBean.targetClass.replace("/", ".") + "." + aopResult.aopBean.targetField
        var bean = aopFieldResultBeans[key]
        if (bean == null) {
            bean = arrayListOf()
        }
        val aop = aopResult.originClass.replace("/", ".") +
                "." + aopResult.originField + "：" + aopResult.lineNum +
                " --> " +
                aopResult.aopBean.proxyClass.replace("/", ".") +
                "." + aopResult.aopBean.proxyField
        if (!bean.contains(aop)) {
            bean.add(aop)
        }
        aopFieldResultBeans[key] = bean
    }

}