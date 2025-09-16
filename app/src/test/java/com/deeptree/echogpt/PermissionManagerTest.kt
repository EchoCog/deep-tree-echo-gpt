package com.deeptree.echogpt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.deeptree.echogpt.manager.PermissionManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PermissionManagerTest {
    
    @Mock
    private lateinit var context: Context
    
    private lateinit var permissionManager: PermissionManager
    
    @Before
    fun setUp() {
        permissionManager = PermissionManager(context)
    }
    
    @Test
    fun hasPermission_whenPermissionGranted_returnsTrue() {
        // Mock ContextCompat.checkSelfPermission to return PERMISSION_GRANTED
        mockStatic(ContextCompat::class.java).use { mockedContextCompat ->
            mockedContextCompat.`when`<Int> {
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            }.thenReturn(PackageManager.PERMISSION_GRANTED)
            
            val result = permissionManager.hasPermission(Manifest.permission.RECORD_AUDIO)
            
            assert(result)
        }
    }
    
    @Test
    fun hasPermission_whenPermissionDenied_returnsFalse() {
        // Mock ContextCompat.checkSelfPermission to return PERMISSION_DENIED
        mockStatic(ContextCompat::class.java).use { mockedContextCompat ->
            mockedContextCompat.`when`<Int> {
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            }.thenReturn(PackageManager.PERMISSION_DENIED)
            
            val result = permissionManager.hasPermission(Manifest.permission.RECORD_AUDIO)
            
            assert(!result)
        }
    }
}