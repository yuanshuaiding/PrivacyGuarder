package com.eric.manager.privacy.app

import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocationClient
import com.eric.manager.privacy.annotation.TimeCost
import com.eric.manager.privacy.databinding.ActivityMainBinding
import com.eric.manager.privacy.databinding.DialogShowValueBinding
import com.eric.manager.privacyproxy.PrivacyGuarder
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var mActivity: MainActivity

    @RequiresApi(Build.VERSION_CODES.O)
    @TimeCost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mActivity=this
        binding.btnAgree.setOnClickListener {
            PrivacyGuarder.setAgreement(true)
        }

        binding.btnDisagree.setOnClickListener {
            PrivacyGuarder.setAgreement(false)
        }

        binding.btnTestSn.setOnClickListener {
            val sn = TestInjectJava.getSerial()
            showValue("设备序列号SERIAL", sn)
        }
        binding.btnAndroidId.setOnClickListener {
            val id = TestInjectJava.getAndroidId(mActivity)
            showValue("设备Android ID", id)
        }

        binding.btnInstalledPackages.setOnClickListener {
            val apps = TestInjectJava.getAppsInfo(mActivity)
            val sb = StringBuilder()
            if (apps.isNullOrEmpty()) {
                sb.append("未获取到应用列表")
            } else {
                sb.append("JAVA:").append("\n")
                for (app in apps) {
                    sb.append(app.packageName).append("\n")
                }
            }

            showValue("应用列表", sb.toString())
        }

        binding.btnInstalledPackages2.setOnClickListener {
            val apps = TestInjectJava.getAppsInfo(mActivity)
            val sb = StringBuilder()
            if (apps.isNullOrEmpty()) {
                sb.append("未获取到应用列表")
            } else {
                sb.append("Kotlin:").append("\n")
                for (app in apps) {
                    sb.append(app.packageName).append("\n")
                }
            }
            showValue("应用列表", sb.toString())
        }




        binding.btnTestAmap.setOnClickListener {
            //2D版隐私
            try {
                val sensorManager: SensorManager =
                    mActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager

                try {
                    val b = sensorManager.getDefaultSensor(6)
                } catch (th: Throwable) {
                }
                try {
                    val c = sensorManager.getDefaultSensor(11)
                } catch (th2: Throwable) {
                }
                try {
                    val d = sensorManager.getDefaultSensor(1)
                } catch (th3: Throwable) {
                }
            } catch (th4: Throwable) {

            }
        }

    }

    private fun showValue(key: String, value: String?) {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogShowValueBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.tvKey.text = key
        dialogBinding.tvValue.text = value ?: ""

        dialog.show()
    }
}