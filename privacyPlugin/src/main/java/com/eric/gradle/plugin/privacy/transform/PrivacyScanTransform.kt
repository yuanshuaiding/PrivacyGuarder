package com.eric.gradle.plugin.privacy.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import org.gradle.api.Project

/**
 * @Description: 字节码中用到隐私api的代码扫描transform
 * @Author: Eric
 * @Email: yuanshuaiding@163.com
 * @CreateDate: 2022/7/27 18:50
 * @Version: 1.0
 */
class PrivacyScanTransform(val p: Project) : Transform() {
    override fun getName() = "privacyScan"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        TODO("Not yet implemented")
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        TODO("Not yet implemented")
    }

    override fun isIncremental(): Boolean {
        TODO("Not yet implemented")
    }
}