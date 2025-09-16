package com.deeptree.echogpt

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.view.LayoutInflater
import com.deeptree.echogpt.manager.*
import com.deeptree.echogpt.service.VoiceService
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    
    
    private lateinit var voiceButton: android.widget.Button
    private lateinit var statusTextView: android.widget.TextView
    private lateinit var contactsButton: android.widget.Button
    private lateinit var calendarButton: android.widget.Button
    private lateinit var alarmButton: android.widget.Button
    private lateinit var cameraButton: android.widget.Button
    private lateinit var locationButton: android.widget.Button
    private lateinit var shareButton: android.widget.Button
    private lateinit var responseTextView: android.widget.TextView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var permissionManager: PermissionManager
    private lateinit var voiceManager: VoiceManager
    private lateinit var contactsManager: ContactsManager
    private lateinit var calendarManager: CalendarManager
    private lateinit var alarmManager: AlarmManager
    private lateinit var cameraManager: CameraManager
    private lateinit var locationManager: LocationManager
    private lateinit var fileManager: FileManager
    
    // MLOps Integration
    private lateinit var deepTreeEchoMLOpsManager: DeepTreeEchoMLOpsManager
    private val mlOpsScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>
    private lateinit var contactsLauncher: ActivityResultLauncher<Intent>
    private lateinit var calendarLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileLauncher: ActivityResultLauncher<Intent>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        initializeManagers()
        initializeLaunchers()
        setupClickListeners()
        requestPermissions()
        
        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)
        
        // Handle deep links
        handleDeepLink(intent)
    }
    
    private fun initializeViews() {
        voiceButton = findViewById(R.id.voiceButton)
        statusTextView = findViewById(R.id.statusTextView)
        contactsButton = findViewById(R.id.contactsButton)
        calendarButton = findViewById(R.id.calendarButton)
        alarmButton = findViewById(R.id.alarmButton)
        cameraButton = findViewById(R.id.cameraButton)
        locationButton = findViewById(R.id.locationButton)
        shareButton = findViewById(R.id.shareButton)
        responseTextView = findViewById(R.id.responseTextView)
    }
    
    private fun initializeManagers() {
        permissionManager = PermissionManager(this)
        voiceManager = VoiceManager(this)
        contactsManager = ContactsManager(this)
        calendarManager = CalendarManager(this)
        cameraManager = CameraManager(this)
        locationManager = LocationManager(this)
        fileManager = FileManager(this)
        
        // Initialize MLOps system
        deepTreeEchoMLOpsManager = DeepTreeEchoMLOpsManager(this)
        initializeMLOpsSystem()
    }
    
    private fun initializeLaunchers() {
        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            voiceManager.handleSpeechResult(result) { text ->
                processVoiceInput(text)
            }
        }
        
        contactsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Handle contacts result
            updateResponse("Contacts accessed successfully")
        }
        
        calendarLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Handle calendar result
            updateResponse("Calendar accessed successfully")
        }
        
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            cameraManager.handleCameraResult(result) { imagePath ->
                updateResponse("Image captured: $imagePath")
            }
        }
        
        fileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            fileManager.handleFileResult(result) { filePath ->
                updateResponse("File selected: $filePath")
            }
        }
    }
    
    private fun setupClickListeners() {
        voiceButton.setOnClickListener {
            startVoiceRecognition()
        }
        
        contactsButton.setOnClickListener {
            openContacts()
        }
        
        calendarButton.setOnClickListener {
            openCalendar()
        }
        
        alarmButton.setOnClickListener {
            setAlarm()
        }
        
        cameraButton.setOnClickListener {
            openCamera()
        }
        
        locationButton.setOnClickListener {
            getLocation()
        }
        
        shareButton.setOnClickListener {
            shareFile()
        }
    }
    
    private fun requestPermissions() {
        permissionManager.requestAllPermissions()
    }
    
    private fun startVoiceRecognition() {
        if (permissionManager.hasPermission(Manifest.permission.RECORD_AUDIO)) {
            statusTextView.text = getString(R.string.listening)
            voiceManager.startListening(speechRecognizerLauncher)
        } else {
            Toast.makeText(this, getString(R.string.microphone_access_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun processVoiceInput(text: String) {
        updateResponse("You said: $text")
        
        // Process through Deep Tree Echo MLOps pipeline
        processWithMLOps(text)
    }
    
    private fun processWithMLOps(text: String) {
        mlOpsScope.launch {
            try {
                updateResponse("Processing with Deep Tree Echo MLOps...")
                
                // Create context data
                val contextData = mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "battery_level" to getBatteryLevel(),
                    "location_enabled" to permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                    "app_state" to "foreground"
                )
                
                // Process through MLOps pipeline
                val result = deepTreeEchoMLOpsManager.processDeepTreeEcho(
                    audioInput = floatArrayOf(), // Would contain actual audio data
                    textInput = text,
                    contextData = contextData
                )
                
                // Generate AI response based on MLOps result
                val aiResponse = generateEnhancedAIResponse(text, result)
                updateResponse(aiResponse)
                speakResponse(aiResponse)
                
                // Log system intelligence metrics
                val metrics = deepTreeEchoMLOpsManager.getSystemIntelligenceMetrics()
                Log.d("MainActivity", "System Intelligence Metrics: $metrics")
                
            } catch (e: Exception) {
                Log.e("MainActivity", "MLOps processing failed", e)
                // Fallback to simple AI response
                val response = generateAIResponse(text)
                updateResponse(response)
                speakResponse(response)
            }
        }
    }
    
    private fun generateEnhancedAIResponse(input: String, mlOpsResult: DeepTreeEchoMLOpsManager.MLOpsResult): String {
        val baseResponse = generateAIResponse(input)
        val confidence = mlOpsResult.confidence
        val processingTime = mlOpsResult.processingTime
        val acceleration = mlOpsResult.accelerationUsed
        
        return when {
            confidence > 0.8f -> {
                "$baseResponse\n\nProcessed with high confidence (${(confidence * 100).toInt()}%) using $acceleration acceleration in ${processingTime}ms."
            }
            confidence > 0.5f -> {
                "$baseResponse\n\nProcessed with moderate confidence using neural network optimization."
            }
            else -> {
                "$baseResponse\n\nProcessed using fallback AI system."
            }
        }
    }
    
    private suspend fun getBatteryLevel(): Int {
        return try {
            val batteryIntent = registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
            val level = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                (level * 100 / scale)
            } else {
                50
            }
        } catch (e: Exception) {
            50
        }
    }
    
    private fun initializeMLOpsSystem() {
        mlOpsScope.launch {
            try {
                updateResponse("Initializing Deep Tree Echo MLOps system...")
                val success = deepTreeEchoMLOpsManager.initialize()
                
                if (success) {
                    updateResponse("MLOps system initialized successfully with neural network acceleration")
                    
                    // Get system capabilities
                    val metrics = deepTreeEchoMLOpsManager.getSystemIntelligenceMetrics()
                    Log.i("MainActivity", "MLOps System Capabilities: $metrics")
                    
                } else {
                    updateResponse("MLOps system initialization failed, using fallback AI")
                }
                
            } catch (e: Exception) {
                Log.e("MainActivity", "MLOps initialization error", e)
                updateResponse("MLOps initialization error, using standard AI processing")
            }
        }
    }
    
    private fun generateAIResponse(input: String): String {
        return when {
            input.contains("hello", ignoreCase = true) -> "Hello! How can I help you today?"
            input.contains("weather", ignoreCase = true) -> "I can help you check the weather. Please grant location access."
            input.contains("contact", ignoreCase = true) -> "I can help you manage your contacts."
            input.contains("alarm", ignoreCase = true) -> "I can set alarms and reminders for you."
            input.contains("photo", ignoreCase = true) || input.contains("picture", ignoreCase = true) -> "I can help you take photos and analyze images."
            else -> "I understand you said: $input. How can I assist you further?"
        }
    }
    
    private fun speakResponse(text: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    private fun updateResponse(text: String) {
        responseTextView.text = text
        statusTextView.text = ""
    }
    
    private fun openContacts() {
        if (permissionManager.hasPermission(Manifest.permission.READ_CONTACTS)) {
            contactsManager.openContacts(contactsLauncher)
        } else {
            Toast.makeText(this, getString(R.string.contacts_access_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openCalendar() {
        if (permissionManager.hasPermission(Manifest.permission.READ_CALENDAR)) {
            calendarManager.openCalendar(calendarLauncher)
        } else {
            Toast.makeText(this, getString(R.string.calendar_access_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setAlarm() {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "EchoGPT Alarm")
                putExtra(AlarmClock.EXTRA_HOUR, 8)
                putExtra(AlarmClock.EXTRA_MINUTES, 0)
            }
            startActivity(intent)
            updateResponse(getString(R.string.alarm_set))
        } catch (e: Exception) {
            updateResponse("Error setting alarm: ${e.message}")
        }
    }
    
    private fun openCamera() {
        if (permissionManager.hasPermission(Manifest.permission.CAMERA)) {
            cameraManager.openCamera(cameraLauncher)
        } else {
            Toast.makeText(this, getString(R.string.camera_access_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getLocation() {
        if (permissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationManager.getCurrentLocation { location ->
                updateResponse("Location: ${location.latitude}, ${location.longitude}")
            }
        } else {
            Toast.makeText(this, getString(R.string.location_access_required), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareFile() {
        fileManager.shareFile(fileLauncher)
    }
    
    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null) {
            val query = data.getQueryParameter("q")
            if (!query.isNullOrEmpty()) {
                updateResponse("Deep link query: $query")
                processVoiceInput(query)
            }
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.getDefault()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        
        // Cleanup MLOps resources
        mlOpsScope.cancel()
        if (::deepTreeEchoMLOpsManager.isInitialized) {
            deepTreeEchoMLOpsManager.cleanup()
        }
    }
}