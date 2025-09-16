package com.deeptree.echogpt.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deeptree.echogpt.MainActivity
import com.deeptree.echogpt.R

class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        private const val ALARM_CHANNEL_ID = "AlarmChannel"
        private const val ALARM_NOTIFICATION_ID = 2
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "EchoGPT Alarm"
        showAlarmNotification(context, message)
    }
    
    private fun showAlarmNotification(context: Context, message: String) {
        createNotificationChannel(context)
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setContentTitle("EchoGPT Alarm")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Notifications"
            val descriptionText = "Notifications for alarms and reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ALARM_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}