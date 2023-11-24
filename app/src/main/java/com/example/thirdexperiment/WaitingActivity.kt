package com.example.thirdexperiment

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.thirdexperiment.databinding.ActivityWaitingBinding
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
* 过渡页面
* 在settings选型保存更改后进行重新数据获取*/
class WaitingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWaitingBinding
    private lateinit var dbHelper:MyDataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding=ActivityWaitingBinding.inflate(layoutInflater)
        dbHelper= MyDataBaseHelper(this,"database.db",1)
        var db=dbHelper.writableDatabase
        db.execSQL("delete from weather") // 数据库清空，再次插入就是最新的数据
        WeatherList.clear()
        setContentView(binding.root)
        Log.d("Waiting",Location.city+" "+Location.district)
        webGetCityCode()
    }

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

    private fun webGetWeather(city:String){
        var getWeatherUrl=WeatherAPI.url+WeatherAPI.location+city+WeatherAPI.key+WeatherAPI.language
        HttpUtil.sendHttpRequest(getWeatherUrl,object:HttpCallbackListener{
            override fun onFinish(response: String) {
                Log.d("Start",response)
                parseJSONWithJSONObject(response)
                var intent= Intent(this@WaitingActivity,MainActivity::class.java)
                startActivity(intent)
            }
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
    private fun parseJSONWithJSONObject(jsonData: String):Boolean{

        val begin=jsonData.indexOf("[")
        val end=jsonData.lastIndexOf("]")
        var data=jsonData.substring(begin..end)
        val jsonArray = JSONArray(data)

        for(i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            var date=jsonObject.getString("fxDate")
            var dayOfWeek=""
            if(i==0){
                dayOfWeek="Today"
            }else if(i==1){
                dayOfWeek="Tomorrow"
            }else{
                dayOfWeek= DayOfWeek(date)
            }
            date=DateTrans(date)
            var max_temp=""
            var min_temp=""
            if(Metric.flag=="华氏度"){
                max_temp=Math.round((jsonObject.getString("tempMax").toInt()*1.8+32)).toString()+"°F"
                min_temp=Math.round((jsonObject.getString("tempMin").toInt()*1.8+32)).toString()+"°F"
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
            Log.d("InsertDatabase","Inserted one value")
        }
        return true
    }

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

    private fun DateTrans(time:String):String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }
}