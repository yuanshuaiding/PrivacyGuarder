package com.eric.manager.privacy.app

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.eric.manager.privacy.annotation.TimeCost
import com.eric.manager.privacy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @TimeCost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAppList.setOnClickListener {
            val apps = TestInjectJava.getAppsInfo(this)
            val sb = StringBuilder()
            if (apps.isNullOrEmpty()) {
                sb.append("未获取到应用列表")
            } else {
                sb.append("JAVA:").append("\n")
                for (app in apps) {
                    sb.append(app.packageName).append("\n")
                }
            }
            binding.tvResult.text = sb
        }

        binding.btnAppList2.setOnClickListener {
            val apps = TestInjectJava.getAppsInfo(this)
            val sb = StringBuilder()
            if (apps.isNullOrEmpty()) {
                sb.append("未获取到应用列表")
            } else {
                sb.append("Kotlin:").append("\n")
                for (app in apps) {
                    sb.append(app.packageName).append("\n")
                }
            }
            binding.tvResult.text = sb
        }

    }
}