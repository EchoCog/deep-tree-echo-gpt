package com.deeptree.echogpt.manager

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CalendarContract
import androidx.activity.result.ActivityResultLauncher
import java.util.*

class CalendarManager(private val context: Context) {
    
    fun openCalendar(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = CalendarContract.CONTENT_URI
        }
        launcher.launch(intent)
    }
    
    fun createEvent(title: String, description: String, startTime: Long, endTime: Long): Boolean {
        return try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startTime)
                put(CalendarContract.Events.DTEND, endTime)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId())
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getUpcomingEvents(limit: Int = 10): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        
        val selection = "${CalendarContract.Events.DTSTART} >= ?"
        val selectionArgs = arrayOf(System.currentTimeMillis().toString())
        val sortOrder = "${CalendarContract.Events.DTSTART} ASC"
        
        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        
        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < limit) {
                val id = it.getLong(0)
                val title = it.getString(1) ?: ""
                val description = it.getString(2) ?: ""
                val startTime = it.getLong(3)
                val endTime = it.getLong(4)
                
                events.add(CalendarEvent(id, title, description, startTime, endTime))
                count++
            }
        }
        
        return events
    }
    
    private fun getDefaultCalendarId(): Long {
        val projection = arrayOf(CalendarContract.Calendars._ID)
        val cursor: Cursor? = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(0)
            }
        }
        
        return 1L // Default calendar ID
    }
    
    data class CalendarEvent(
        val id: Long,
        val title: String,
        val description: String,
        val startTime: Long,
        val endTime: Long
    )
}