package com.deeptree.echogpt.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import java.util.*

class VoiceManager(private val context: Context) {
    
    fun startListening(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        
        try {
            launcher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun handleSpeechResult(result: ActivityResult, callback: (String) -> Unit) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                callback(spokenText)
            }
        }
    }
    
    fun isVoiceRecognitionAvailable(): Boolean {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        return intent.resolveActivity(context.packageManager) != null
    }
}