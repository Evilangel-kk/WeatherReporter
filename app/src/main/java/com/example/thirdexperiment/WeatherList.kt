package com.example.thirdexperiment

/*
* 存放天气集合的列表*/
object WeatherList {
    var weather= ArrayList<Weather>()
    fun add(weather:Weather){
        this.weather.add(weather)
    }
    fun clear(){
        weather.clear()
    }
}