package com.example.thirdexperiment

object WeatherList {
    var weather= ArrayList<Weather>()
    fun add(weather:Weather){
        this.weather.add(weather)
    }
    fun clear(){
        weather.clear()
    }
}