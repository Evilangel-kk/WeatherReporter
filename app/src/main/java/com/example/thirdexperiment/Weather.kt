package com.example.thirdexperiment

import java.io.Serializable

class Weather(dayOfWeek:String,date:String,iconDay:String,iconNight:String,max_temp:String,min_temp:String,textDay:String,textNight:String,windSpeedDay:String,windSpeedNight:String,windDirDay:String,windDirNight:String,humidity:String,pressure:String):
    Serializable {
    var dayOfWeek:String=dayOfWeek // 星期
    var date:String=date // 日期
    var iconDay:String=iconDay // 白天图标代码
    var iconNight:String=iconNight // 黑天图标代码
    var max_temp:String=max_temp // 最高温度
    var min_temp:String=min_temp // 最低温度
    var textDay:String=textDay // 白天天气概况
    var textNight:String=textNight // 黑天天气概况
    var windSpeedDay:String=windSpeedDay // 白天风速
    var windSpeedNight:String=windSpeedNight // 黑天风速
    var windDirDay:String=windDirDay // 白天风向
    var windDirNight:String=windDirNight // 黑天风向
    var humidity:String=humidity // 空气湿度
    var pressure:String=pressure // 气压

}