package com.example.thirdexperiment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationDeleteReceiver : BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "DELETE_NOTIFICATION") {
            val notificationManager = NotificationManagerCompat.from(context!!)
            notificationManager.cancel(1)
        }
    }
}