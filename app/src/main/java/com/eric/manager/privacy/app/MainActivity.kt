package com.eric.manager.privacy.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eric.manager.privacy.R
import com.eric.manager.privacy.annotation.TimeCost

class MainActivity : AppCompatActivity() {
    @TimeCost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TestInjectJava.getAppsInfo(this)
    }
}