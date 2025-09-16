package com.deeptree.echogpt.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileManager(private val context: Context) {
    
    fun shareFile(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        launcher.launch(intent)
    }
    
    fun handleFileResult(result: ActivityResult, callback: (String) -> Unit) {
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri)
                callback("Selected file: $fileName")
                processFile(uri, fileName)
            }
        }
    }
    
    private fun getFileName(uri: Uri): String {
        var fileName = "unknown_file"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    
    private fun processFile(uri: Uri, fileName: String) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val file = File(context.filesDir, fileName)
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
                analyzeFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun analyzeFile(file: File): FileAnalysis {
        val extension = file.extension.lowercase()
        val size = file.length()
        
        val fileType = when (extension) {
            "pdf" -> FileType.PDF
            "doc", "docx" -> FileType.DOCUMENT
            "txt" -> FileType.TEXT
            "jpg", "jpeg", "png", "gif" -> FileType.IMAGE
            "mp3", "wav", "m4a" -> FileType.AUDIO
            "mp4", "avi", "mov" -> FileType.VIDEO
            else -> FileType.UNKNOWN
        }
        
        return FileAnalysis(
            name = file.name,
            size = size,
            type = fileType,
            extension = extension,
            path = file.absolutePath,
            lastModified = file.lastModified()
        )
    }
    
    fun shareFileWithOthers(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "com.deeptree.echogpt.fileprovider",
            file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = getMimeType(file.extension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share File"))
    }
    
    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "mp4" -> "video/mp4"
            "avi" -> "video/avi"
            else -> "*/*"
        }
    }
    
    fun createTextFile(content: String, fileName: String): File {
        val file = File(context.filesDir, fileName)
        file.writeText(content)
        return file
    }
    
    fun readTextFile(file: File): String {
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }
    
    data class FileAnalysis(
        val name: String,
        val size: Long,
        val type: FileType,
        val extension: String,
        val path: String,
        val lastModified: Long
    )
    
    enum class FileType {
        PDF, DOCUMENT, TEXT, IMAGE, AUDIO, VIDEO, UNKNOWN
    }
}