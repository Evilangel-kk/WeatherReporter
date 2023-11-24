package com.example.thirdexperiment

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class WeatherBroadcastReceiver:BroadcastReceiver() {
    private val TAG="WeatherBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: ")
        // 获取天气信息
        val weatherInfo = getWeatherInfo(context)

        // 创建删除通知的 Intent
        val deleteIntent = Intent(context, NotificationDeleteReceiver::class.java)
        deleteIntent.action = "DELETE_NOTIFICATION"
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 创建进入应用的 Intent
        val openAppIntent = Intent(context, StartViewActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 创建通知
        val builder = NotificationCompat.Builder(context!!, "weather_channel")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Weather Update")
            .setContentText(weatherInfo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setTicker(null)
            .addAction(R.drawable.before, "Delete", deletePendingIntent) // 添加删除通知的操作按钮
            .setContentIntent(openAppPendingIntent) // 设置点击通知后打开应用的操作
        Log.d(TAG, "onReceive: 通知创建完毕")

        // 显示通知
        val notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.areNotificationsEnabled()) {
            Log.d(TAG, "onReceive: 已拥有权限")
            // 发送通知的逻辑
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            Log.d(TAG, "onReceive: 检查权限完毕")
            notificationManager.notify(1, builder.build())
        } else {
            // 请求用户授予通知权限
            Log.d(TAG, "onReceive: 请求权限")
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
        }

    }

    private fun getWeatherInfo(context: Context?): String {
        // 从网络或本地获取天气信息
//        return "Today's weather is sunny."
        return "Max: "+WeatherList.weather[0].max_temp+" Min: "+WeatherList.weather[0].min_temp+" "+WeatherList.weather[0].textDay
    }
}