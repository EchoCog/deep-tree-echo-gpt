package com.deeptree.echogpt.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.deeptree.echogpt.MainActivity
import com.deeptree.echogpt.R

class EchoGPTWidgetProvider : AppWidgetProvider() {
    
    companion object {
        private const val ACTION_WIDGET_CLICK = "com.deeptree.echogpt.WIDGET_CLICK"
    }
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (ACTION_WIDGET_CLICK == intent.action) {
            // Handle widget click - open app with voice recognition
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("start_voice", true)
            }
            context.startActivity(mainIntent)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Create an Intent to launch MainActivity
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create an Intent for the widget button
        val buttonIntent = Intent(context, EchoGPTWidgetProvider::class.java).apply {
            action = ACTION_WIDGET_CLICK
        }
        val buttonPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            buttonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_echogpt).apply {
            setOnClickPendingIntent(R.id.widgetIcon, pendingIntent)
            setOnClickPendingIntent(R.id.widgetText, pendingIntent)
            setOnClickPendingIntent(R.id.widgetButton, buttonPendingIntent)
            setTextViewText(R.id.widgetText, context.getString(R.string.widget_label))
        }
        
        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}