package com.example.thirdexperiment

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.thirdexperiment.databinding.ActivitySettingsBinding
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citylist.Toast.ToastUtils
import com.lljjcoder.style.citypickerview.CityPickerView
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException


/*
* settings界面
* 利用CityPickerView开源库引入城市选择的控件*/
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var location:String
    private val mPicker = CityPickerView()
    private var service: WeatherService? = null
    private var TAG="SettingsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="settings"
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);
        //标题栏的返回按钮
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        // 判断全局service是否已经存在，不存在就进行绑定
        if(MyApplication.weatherService==null){
            val serviceIntent = Intent(this, WeatherService::class.java)
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            MyApplication.weatherService=WeatherService()
        }
        // 单选框设置默认值
        if(Metric.flag=="摄氏度"){
            binding.ssd.isChecked=true;
            binding.hsd.isChecked=false;
        }else{
            binding.ssd.isChecked=false;
            binding.hsd.isChecked=true;
        }
        // 开关设置默认值
        binding.switchNotify.isChecked=Switch.status
        if(Switch.status){
            binding.editSelectedNotice.text="open"
        }else{
            binding.editSelectedNotice.text="close"
        }
        // 三级架构城市设置默认值
        binding.editSelectedCity.text=Location.province+"-"+Location.city+"-"+Location.district

        // 为editview添加点击事件
        binding.editSelectedCity.setOnClickListener{
            //添加默认的配置
            val cityConfig = CityConfig.Builder()
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)// PRO_CITY_DIS显示的是三级，PRO_CITY是两级，PRO只有省份
                .province(Location.province)//默认显示的省份
                .city(Location.city)//默认显示省份下面的城市
                .district(Location.district)//默认显示省市下面的区县数据
                .build()
            mPicker.setConfig(cityConfig)
            //监听选择点击事件及返回结果
            mPicker.setOnCityItemClickListener(object : OnCityItemClickListener() {
                override fun onSelected(
                    province: ProvinceBean?,//省份province
                    city: CityBean?,//城市city
                    district: DistrictBean?//地区district
                ) {
                    // 对静态地址进行更改
                    if (district != null) {
                        println(district.id)
                    }
                    Location.province=province.toString()
                    Location.city=city.toString()
                    Location.district=district.toString()
                    location=""+province+"-"+city+"-"+district
                    binding.editSelectedCity.text=location
                }

                override fun onCancel() {
                    ToastUtils.showLongToast(this@SettingsActivity, "已取消")
                }
            })
            //显示
            mPicker.showCityPicker()
        }

        // 为单选框组添加选项更改事件
        binding.radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedOption = radioButton.text.toString()
            // 处理选项选择事件
            Log.d("radioGroup",selectedOption)
            if(selectedOption=="华氏度"){
                Metric.flag="华氏度"
            }else{
                Metric.flag="摄氏度"
            }
        })

        // 为开关添加更改事件
        binding.switchNotify.setOnCheckedChangeListener{_, isChecked ->
            // 处理开关状态改变事件
            Switch.status = isChecked
            if(Switch.status){
                binding.editSelectedNotice.text="open"
            }else{
                binding.editSelectedNotice.text="close"
            }
        }

        // 为保存button添加点击事件
        binding.save.setOnClickListener {
            if(OldMsg.status!=Switch.status){
                OldMsg.status=Switch.status
                val serviceIntent = Intent(this, WeatherService::class.java)
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

                if(Switch.status){
                    //判断用户是否开启通知权限
//                    checkNotificationPermission()
                    MyApplication.weatherService?.startService()
                }else{
                    Log.d(TAG, "用户关闭了通知")
                    MyApplication.weatherService?.stopService()// 开关关闭，关闭通知服务
                    MyApplication.weatherService=null // 全局service置空
                }
            }
            // 跳转到过渡界面
            val intent=Intent(this,WaitingActivity::class.java)
            startActivity(intent)
        }
    }

    // 绑定Activity和Service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as Binder
            service = (serviceBinder as WeatherService.WeatherServiceBinder).getService()
            MyApplication.weatherService = service as WeatherService
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Do nothing
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkNotificationPermission() {
        //静态常量权限码
        val REQUEST_EXTERNAL_STORAGE = 101
        //静态数组，具体权限
        val PERMISSIONS_STORAGE = arrayOf(
            "android.permission.POST_NOTIFICATIONS",
            "android.permission.ACCESS_NOTIFICATION_POLICY",
            "android.permission.VIBRATE",
            "android.permission.READ_CONTACTS"
        )
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkNotificationPermission: ")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d(TAG, "请开通相关权限")
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }else{
                //申请权限  参数1.context 2.具体权限 3.权限码
                Log.d(TAG, "申请授权")
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            }
        } else {
            Toast.makeText(this, "已授权！", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "已授权")
            //有权限再获取资源，否则获取也无法下载到本地
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1-> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "onRequestPermissionsResult: done")
                    Toast.makeText(this, "你可以正常使用app", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("MainActivity", "onRequestPermissionsResult: failed")
                    Toast.makeText(this, "你拒绝了权限，无法正常保存图片", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}