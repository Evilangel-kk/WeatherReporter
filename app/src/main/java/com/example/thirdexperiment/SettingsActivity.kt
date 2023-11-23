package com.example.thirdexperiment

import android.content.Intent
import android.os.Bundle
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


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    private lateinit var location:String
    private lateinit var temperatureUnits:String
    private var notify:Boolean=false
    private val REQUEST_CODE_PICK_CITY = 0
    private val mPicker = CityPickerView()
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

        if(Metric.flag=="摄氏度"){
            binding.ssd.isChecked=true;
            binding.hsd.isChecked=false;
        }else{
            binding.ssd.isChecked=false;
            binding.hsd.isChecked=true;
        }

        binding.editSelectedCity.text=Location.province+"-"+Location.city+"-"+Location.district

        binding.editSelectedCity.setOnClickListener{
            //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
            val cityConfig = CityConfig.Builder()
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)
                .province(Location.province)//默认显示的省份
                .city(Location.city)//默认显示省份下面的城市
                .district(Location.district)//默认显示省市下面的区县数据
                .build()
            mPicker.setConfig(cityConfig)
            //监听选择点击事件及返回结果
            mPicker.setOnCityItemClickListener(object : OnCityItemClickListener() {
                override fun onSelected(
                    province: ProvinceBean?,
                    city: CityBean?,
                    district: DistrictBean?
                ) {
                    //省份province
                    //城市city
                    //地区district
                    Location.province=province.toString()
                    Location.city=city.toString()
                    Location.district=district.toString()
//                    if("区" in district.toString()){
//                        Location.place=district.toString().split("区")[0]
//                    }else if("市" in district.toString()){
//                        Location.place=district.toString().split("市")[0]
//                    }

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

        binding.save.setOnClickListener {
            var intent=Intent(this,WaitingActivity::class.java)
            startActivity(intent)
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