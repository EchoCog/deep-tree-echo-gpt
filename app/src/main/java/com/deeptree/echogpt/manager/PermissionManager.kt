package com.deeptree.echogpt.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
    
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    fun hasAllPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { hasPermission(it) }
    }
    
    fun requestAllPermissions() {
        if (context is Activity) {
            val missingPermissions = REQUIRED_PERMISSIONS.filter { !hasPermission(it) }
            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context,
                    missingPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    
    fun requestPermission(permission: String) {
        if (context is Activity && !hasPermission(permission)) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(permission),
                PERMISSION_REQUEST_CODE
            )
        }
    }
}