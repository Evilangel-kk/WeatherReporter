package com.example.thirdexperiment

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.thirdexperiment.databinding.ActivityStartViewAvtivityBinding
import com.loc.db
import com.loc.l
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/*
* 启动app时进行初始化，获取数据方便后续页面展示
* */
class StartViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartViewAvtivityBinding
    private lateinit var dbHelper:MyDataBaseHelper
    private lateinit var city:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏标题栏
        supportActionBar?.hide()
        binding= ActivityStartViewAvtivityBinding.inflate(layoutInflater)
        dbHelper= MyDataBaseHelper(this,"database.db",1)
        var db=dbHelper.writableDatabase
        setContentView(binding.root)
        city="长沙"
        db.execSQL("delete from weather")
        webGetCityCode(city)
    }


    private fun webGetCityCode(city:String) {
        var getCityCodeUrl = CityCodeAPI.url + CityCodeAPI.location + city + CityCodeAPI.key
        HttpUtil.sendHttpRequest(getCityCodeUrl, object : HttpCallbackListener {
            override fun onFinish(response: String) {
                val begin = response.indexOf("[")
                val end = response.lastIndexOf("]")
                var data = response.substring(begin..end)
                val jsonArray = JSONArray(data)
                for (i in 0 until jsonArray.length()) {
                    if (jsonArray.getJSONObject(i).getString("name") == city) {
                        Log.d("CityCode", jsonArray.getJSONObject(i).getString("id"))
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
                var finish=parseJSONWithJSONObject(response)
                var intent= Intent(this@StartViewActivity,MainActivity::class.java)
                startActivity(intent)
//                if(finish){
//                    var intent= Intent(this@StartViewActivity,MainActivity::class.java)
//                    startActivity(intent)
//                }
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
            var max_temp=jsonObject.getString("tempMax")+"°"
            var min_temp=jsonObject.getString("tempMin")+"°"
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