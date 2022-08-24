待测试：
- getHardwareAddress
- getPackageInfo

需要移除权限：

当前库的AndroidManifest.xml声明了一个蓝牙权限，如果你的项目中未使用到，请使用移除标签移除：
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />

<!--移除方式-->
<uses-permission android:name="android.permission.BLUETOOTH" tools:node="remove" />
```