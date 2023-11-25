package com.example.thirdexperiment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdexperiment.databinding.ActivitySettingsBinding
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citylist.Toast.ToastUtils
import com.lljjcoder.style.citypickerview.CityPickerView

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
                    MyApplication.weatherService?.startService()
                }else{
                    Log.d(TAG, "用户关闭了通知")
                    MyApplication.weatherService?.stopService()// 开关关闭，关闭通知服务
                    MyApplication.weatherService=null // 全局service置空
                }
            }
            // 跳转到过渡界面
            var intent=Intent(this,WaitingActivity::class.java)
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
}