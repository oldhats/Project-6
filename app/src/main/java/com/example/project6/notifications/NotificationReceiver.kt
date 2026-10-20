package com.example.project6.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "BitFit Fitness Tracker"
        val message = intent.getStringExtra("message") ?: "Don't forget to log your daily fitness and food metrics today!"
        
        NotificationHelper.showNotification(context, title, message)
    }
}
