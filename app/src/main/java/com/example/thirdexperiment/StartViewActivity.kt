package com.example.thirdexperiment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.thirdexperiment.databinding.ActivityStartViewAvtivityBinding
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
* 启动app时进行初始化
* 获取数据方便后续页面展示
* 防止下一个页面已经加载好了但是没有获取到数据的状况
* */
class StartViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartViewAvtivityBinding
    private lateinit var dbHelper:MyDataBaseHelper
    private var TAG="StartViewActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏标题栏
        supportActionBar?.hide()
        binding= ActivityStartViewAvtivityBinding.inflate(layoutInflater)
        dbHelper= MyDataBaseHelper(this,"database.db",1)
        var db=dbHelper.writableDatabase
        setContentView(binding.root)
        createNotificationChannel()
        db.execSQL("delete from weather")
        WeatherList.weather.clear()
        webGetCityCode()
    }

    // 和风天气解析中文地址对应城市编码
    private fun webGetCityCode() {
        var getCityCodeUrl = CityCodeAPI.url + CityCodeAPI.location + Location.district + CityCodeAPI.key
        HttpUtil.sendHttpRequest(getCityCodeUrl, object : HttpCallbackListener {
            override fun onFinish(response: String) {
                // 简单操作处理字符串，方便jsonObject直接分类
                val begin = response.indexOf("[")
                val end = response.lastIndexOf("]")
                var data = response.substring(begin..end)
                val jsonArray = JSONArray(data)
                for (i in 0 until jsonArray.length()) {
                    if (jsonArray.getJSONObject(i).getString("adm2") in Location.city) {
                        Log.d("CityCode", jsonArray.getJSONObject(i).getString("id"))
                        Location.lat=jsonArray.getJSONObject(i).getString("lat")
                        Location.lon=jsonArray.getJSONObject(i).getString("lon")
                        webGetWeather(jsonArray.getJSONObject(i).getString("id"))
                        break
                    }
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }

    // 和风天气API，通过城市编码获取天气数据
    private fun webGetWeather(city:String){
        var getWeatherUrl=WeatherAPI.url+WeatherAPI.location+city+WeatherAPI.key+WeatherAPI.language
        HttpUtil.sendHttpRequest(getWeatherUrl,object:HttpCallbackListener{
            override fun onFinish(response: String) {
                Log.d("Start",response)
                parseJSONWithJSONObject(response)
                var intent= Intent(this@StartViewActivity,MainActivity::class.java)
                startActivity(intent)
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    // 对获取到的天气信息进行转换，从json数据包中取出需要的值
    private fun parseJSONWithJSONObject(jsonData: String):Boolean{
        val begin=jsonData.indexOf("[")
        val end=jsonData.lastIndexOf("]")
        var data=jsonData.substring(begin..end)
        val jsonArray = JSONArray(data)

        for(i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            var date=jsonObject.getString("fxDate")
            var dayOfWeek=""
            // 将第一天和第二天手动转化为Today和Tomorrow
            // 其余对应星期英文即可
            if(i==0){
                dayOfWeek="Today"
            }else if(i==1){
                dayOfWeek="Tomorrow"
            }else{
                dayOfWeek= DayOfWeek(date)
            }
            date=DateTrans(date) // 日期格式转化为字符串，如Nov 25
            var max_temp=""
            var min_temp=""
            // 根据全局静态的单位设置不同的数值
            if(Metric.flag=="华氏度"){
                max_temp=(jsonObject.getString("tempMax").toInt()*1.8+32).toString()+"°F"
                min_temp=(jsonObject.getString("tempMin").toInt()*1.8+32).toString()+"°F"
            }else{
                max_temp=jsonObject.getString("tempMax")+"°C"
                min_temp=jsonObject.getString("tempMin")+"°C"
            }
            var textDay=jsonObject.getString("textDay")
            var textNight=jsonObject.getString("textNight")
            var iconDay=jsonObject.getString("iconDay")
            var iconNight=jsonObject.getString("iconNight")
            var windSpeedDay=jsonObject.getString("windSpeedDay")+" km/h"
            var windSpeedNight=jsonObject.getString("windSpeedNight")+" km/h"
            var windDirDay=jsonObject.getString("windDirDay")
            var windDirNight=jsonObject.getString("windDirNight")
            var humidity="Humidity: "+jsonObject.getString("humidity")+" %"
            var pressure="Pressure: "+jsonObject.getString("pressure")+" hPa"
            var weather=Weather(dayOfWeek,date,iconDay,iconNight,max_temp,min_temp,textDay,textNight,windSpeedDay,windSpeedNight,windDirDay,windDirNight,humidity,pressure)
            WeatherList.add(weather)
            InsertDataBase(weather)
        }
        return true
    }

    // 插入数据库
    private fun InsertDataBase(weather:Weather){
        var db=dbHelper.writableDatabase
        val values = ContentValues().apply {
            // 开始组装数据
            put("dayOfWeek", weather.dayOfWeek)
            put("date", weather.date)
            put("iconDay",weather.iconDay)
            put("iconNight",weather.iconNight)
            put("maxTemp",weather.max_temp)
            put("minTemp",weather.min_temp)
            put("textDay",weather.textDay)
            put("textNight",weather.textNight)
            put("windSpeedDay",weather.windSpeedDay)
            put("windSpeedNight",weather.windSpeedNight)
            put("windDirDay",weather.windDirDay)
            put("windDirNight",weather.windDirNight)
            put("humidity",weather.humidity)
            put("pressure",weather.pressure)
        }
        db.insert("weather", null, values) // 插入数据
        Log.d("StartActivity","Inserted One Weather")
        db.close()
    }

    // 转化星期
    private fun DayOfWeek(time:String):String{
        // 将字符串解析为 LocalDate 对象
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(time, formatter)
        // 获取该日期对应的星期几
        var dayOfWeek = date.dayOfWeek.toString()
        // 将字符串全部转换为小写
        dayOfWeek=dayOfWeek.toLowerCase()
        // 将第一个字符转换为大写
        dayOfWeek = dayOfWeek.replaceFirstChar { char -> char.uppercase() }
        return dayOfWeek
    }

    // 转化英文日期
    private fun DateTrans(time:String):String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }

    // 设置消息通知的通道
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "R.string.channel_name"
            val descriptionText = "R.string.channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("weather_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}