package com.deeptree.echogpt.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deeptree.echogpt.service.BackgroundService

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Start background service on boot
            val serviceIntent = Intent(context, BackgroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}