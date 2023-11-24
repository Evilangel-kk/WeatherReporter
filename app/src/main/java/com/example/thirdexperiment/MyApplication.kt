package com.example.thirdexperiment

/*
* 静态信息-全局weatherservice保证只有一个存在*/
object MyApplication {
    var weatherService:WeatherService?=null
}
