package com.eric.gradle.plugin.privacy

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.eric.gradle.plugin.privacy.config.AOPHelper
import com.eric.gradle.plugin.privacy.config.PrivacyConfig
import com.eric.gradle.plugin.privacy.transformFactory.PrivacyAOPContractFactory
import com.eric.gradle.plugin.privacy.transformFactory.PrivacyAOPTransformFactory
import com.google.gson.GsonBuilder
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset

/**
 * @Description: 合规治理插件：借助asm框架，对字节码文件中涉及的隐私api进行代理，并提供配置，对指定隐私api永久关闭调用
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/27 15:55
 * @Version: 1.0
 */
class PrivacyPlugin : Plugin<Project> {

    private var config: PrivacyConfig? = null

    companion object {
        //拓展配置名称
        const val PRIVACY_PLUGIN_CONFIG_EXT = "privacyConfig"
    }

    override fun apply(project: Project) {
        //只在application下生效
        if (!project.plugins.hasPlugin("com.android.application")) {
            println("${project.name}非Android app模块，无法使用合规治理插件privacyGovernPlugin，请正确配置！")
            return
        }


        println("${project.name}合规治理插件privacyGovernPlugin配置中...")

        //添加配置拓展
        project.extensions.create(PRIVACY_PLUGIN_CONFIG_EXT, PrivacyConfig::class.java)

        //获取配置(afterEvaluate是gradle配置阶段完成以后的回调)
        //project.afterEvaluate {
        //config = p.extensions.findByType(PrivacyConfig::class.java)
        //println("############################合规治理配置清单 start#########################")
        //println("此次编译privacyGovernPlugin是否生效：${config?.apply}")
        //println("永久禁用的隐私API：")
        //config?.forbidden?.forEach { api ->
        //    println(api)
        //}
        //println("############################合规治理配置清单   end#########################")


        //val android = p.extensions.getByType(AppExtension::class.java)
        // 收集注解信息的任务(APG7.x后已不推荐使用transform，从 AGP 8.0 开始，Transform API 将被移除。这意味着，软件包 com.android.build.api.transform 中的所有类都会被移除。如需转换字节码，请使用 Instrumentation API。)
        //android.registerTransform(PrivacyContractTransform(p))
        // 执行字节码替换的任务
        //android.registerTransform(PrivacyAOPTransform(p))
        //}

        //使用新的API进行transform
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.beforeVariants {
            if (config == null) {
                config = project.extensions.findByType(PrivacyConfig::class.java)
                println("############################合规治理配置清单 start#########################")
                println("此次编译privacyGovernPlugin是否生效：${config?.apply}")
                println("以下包名插件不生效：")
                config?.ignorePKG?.forEach { pkg ->
                    println(pkg)
                }
                println("############################合规治理配置清单   end#########################")
            }
        }

        androidComponents.onVariants(
            // 要想不影响开发阶段，可选择只对release版本生效，或config.apply=false
            //androidComponents.selector().withBuildType("release")
        ) { variant ->
            println("构建变体名称:" + variant.name)
            println("构建变体BuildType:" + variant.buildType)
            //清空一下缓存
            AOPHelper.aopFieldResultBeans.clear()
            AOPHelper.aopMethodResultBeans.clear()
            if (config?.apply != true) {
                println("${project.name}合规治理插件开关已关闭，字节码修改失效")
                return@onVariants
            } else {
                println("${project.name}合规治理插件开关已开启，字节码修改进行中...")

                //开始注册transform操作字节码

                //1. 隐私API替换规则类解析，用于生成需要替换的隐私API与代理API之间的映射关系
                variant.instrumentation.transformClassesWith(
                    PrivacyAOPContractFactory::class.java,
                    InstrumentationScope.ALL
                ) { }
                //2. 隐私API替换，根据第1步生成的映射关系，发现匹配的隐私方法、属性调用时使用代理API进行AOP
                variant.instrumentation.transformClassesWith(
                    PrivacyAOPTransformFactory::class.java,
                    InstrumentationScope.ALL
                ) { params ->
                    config?.let {
                        //从配置中获取忽略的包名
                        it.ignorePKG?.forEach { pkg ->
                            params.ignoredPKG.add(pkg)
                        }

                        //从配置中获取AOP结果文件名
                        params.outFile.set(it.outFile)
                    }

                }
                //设置栈帧计算模式
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )

            }
        }

        project.gradle.buildFinished {
            //3.输出AOP结果
            if (config?.apply == true) {
                println("${project.name}合规治理插件字节码修改完成")
                config?.outFile?.let {
                    println("正在生成AOP结果...")
                    writeResultFile(project, "$it.md").run {
                        println("请在${this}文件中查看AOP结果")
                    }
                }

            }
        }
    }

    //将AOP结果保存到app的build目录里
    private fun writeResultFile(project: Project, fileName: String): String {
        if (fileName.isEmpty() || AOPHelper.aopMethodResultBeans.isEmpty()) return ""
        try {
            val resultFile = File(project.buildDir.absolutePath + File.separator + fileName)
            if (resultFile.parentFile != null && !resultFile.parentFile.exists()) {
                FileUtils.forceMkdirParent(resultFile)
            }

            resultFile.let {
                FileUtils.deleteQuietly(resultFile)
            }
            val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
            val result = """
## 隐私属性AOP结果：
```json
${gson.toJson(AOPHelper.aopFieldResultBeans)}
```
## 隐私方法AOP结果：
```json
${gson.toJson(AOPHelper.aopMethodResultBeans)}
```
"""
            FileUtils.write(resultFile, result, Charset.forName("UTF-8"))
            return resultFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}