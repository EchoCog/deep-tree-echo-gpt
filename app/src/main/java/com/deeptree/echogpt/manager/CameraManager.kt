package com.deeptree.echogpt.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraManager(private val context: Context) {
    
    private var currentPhotoPath: String = ""
    
    fun openCamera(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        
        try {
            val photoFile = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                context,
                "com.deeptree.echogpt.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcher.launch(intent)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    fun handleCameraResult(result: ActivityResult, callback: (String) -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (currentPhotoPath.isNotEmpty()) {
                callback(currentPhotoPath)
                analyzeImage(currentPhotoPath)
            }
        }
    }
    
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    fun analyzeImage(imagePath: String): ImageAnalysis {
        // Basic image analysis - can be enhanced with ML Kit or other AI services
        val bitmap = BitmapFactory.decodeFile(imagePath)
        
        return if (bitmap != null) {
            val width = bitmap.width
            val height = bitmap.height
            val size = File(imagePath).length()
            
            // Simple analysis - can be enhanced with actual image recognition
            ImageAnalysis(
                width = width,
                height = height,
                sizeBytes = size,
                dominantColor = getDominantColor(bitmap),
                description = generateImageDescription(bitmap)
            )
        } else {
            ImageAnalysis(0, 0, 0L, "#000000", "Unable to analyze image")
        }
    }
    
    private fun getDominantColor(bitmap: Bitmap): String {
        // Simple dominant color extraction
        val pixel = bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
        return String.format("#%06X", 0xFFFFFF and pixel)
    }
    
    private fun generateImageDescription(bitmap: Bitmap): String {
        // Basic image description - can be enhanced with ML Kit or cloud vision APIs
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()
        
        return when {
            aspectRatio > 1.5 -> "Landscape orientation image"
            aspectRatio < 0.75 -> "Portrait orientation image"
            else -> "Square or near-square image"
        } + " (${width}x${height})"
    }
    
    fun shareImage(imagePath: String) {
        val imageFile = File(imagePath)
        val imageUri = FileProvider.getUriForFile(
            context,
            "com.deeptree.echogpt.fileprovider",
            imageFile
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
    
    data class ImageAnalysis(
        val width: Int,
        val height: Int,
        val sizeBytes: Long,
        val dominantColor: String,
        val description: String
    )
}