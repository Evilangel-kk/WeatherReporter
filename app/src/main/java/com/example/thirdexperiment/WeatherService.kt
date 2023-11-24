package com.example.thirdexperiment

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.nfc.Tag
import android.os.Binder
import android.os.IBinder
import android.util.Log

class WeatherService: Service() {
    private lateinit var alarmManager: AlarmManager
    private var TAG="WeatherService"
    val binder = WeatherServiceBinder()
    inner class WeatherServiceBinder : Binder() {
        fun getService(): WeatherService {
            return this@WeatherService
        }
    }
    fun getService(): WeatherService {
        return this@WeatherService
    }
    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: ")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        // 取消定时任务
        val intent = Intent(this, WeatherBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    fun startService() {
        // 启动服务的逻辑
        Log.d(TAG, "startService: ")
        // 获取 AlarmManager 实例
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // 设置定时任务
        val intent = Intent(this, WeatherBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val intervalMillis = (60 * 60 * 1000).toLong() // 每小时触发一次
        val intervalMillis = (2 * 1000).toLong() // 每5s触发一次
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, pendingIntent)
    }

    fun stopService() {
        // 停止服务的逻辑
        Log.d(TAG, "stopService: ")
        stopSelf()
    }
}