package com.deeptree.echogpt.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.*

class VoiceService : Service(), TextToSpeech.OnInitListener {
    
    private lateinit var textToSpeech: TextToSpeech
    private val binder = VoiceBinder()
    
    inner class VoiceBinder : Binder() {
        fun getService(): VoiceService = this@VoiceService
    }
    
    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.getDefault()
        }
    }
    
    fun speak(text: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    fun stopSpeaking() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
        }
    }
    
    fun isSpeaking(): Boolean {
        return if (::textToSpeech.isInitialized) {
            textToSpeech.isSpeaking
        } else {
            false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}