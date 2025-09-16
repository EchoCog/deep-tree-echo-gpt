package com.deeptree.echogpt.manager

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager as AndroidLocationManager
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationManager(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private val locationManager: AndroidLocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager
    }
    
    fun getCurrentLocation(callback: (Location) -> Unit) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        callback(location)
                    } else {
                        requestLocationUpdate(callback)
                    }
                }
                .addOnFailureListener {
                    requestLocationUpdate(callback)
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    private fun requestLocationUpdate(callback: (Location) -> Unit) {
        try {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    callback(location)
                    locationManager.removeUpdates(this)
                }
                
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            
            when {
                locationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) -> {
                    locationManager.requestLocationUpdates(
                        AndroidLocationManager.GPS_PROVIDER,
                        1000L,
                        1f,
                        locationListener
                    )
                }
                locationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER) -> {
                    locationManager.requestLocationUpdates(
                        AndroidLocationManager.NETWORK_PROVIDER,
                        1000L,
                        1f,
                        locationListener
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    fun getLocationInfo(location: Location): LocationInfo {
        return LocationInfo(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            altitude = location.altitude,
            speed = location.speed,
            bearing = location.bearing,
            timestamp = location.time
        )
    }
    
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER)
    }
    
    fun getDistanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float,
        val altitude: Double,
        val speed: Float,
        val bearing: Float,
        val timestamp: Long
    )
}