# EchoGPT AI Android App

A comprehensive Android application that provides AI-powered voice assistance with full device integration capabilities.

## Package Information
- **Package Name**: `com.deeptree.echogpt`
- **App Name**: EchoGPT
- **Target SDK**: Android 13 (API 33)
- **Minimum SDK**: Android 7.0 (API 24)

## Features

### 🎤 Voice Processing
- **Speech Recognition**: Process voice input using Android's built-in speech recognition
- **Text-to-Speech**: Provide audio responses to user queries
- **Background Voice Monitoring**: Continuous voice processing capabilities
- **Voice Service**: Dedicated service for voice operations

### 📱 Device Integration
- **Contact Access**: Read and search device contacts
- **Calendar Integration**: Access and create calendar events
- **Camera Functionality**: Capture and analyze images
- **Location Services**: GPS location access and processing
- **File Management**: Document processing and sharing

### ⏰ Productivity Features
- **Alarm Management**: Set alarms and reminders
- **Background Operations**: Foreground service for continuous operation
- **Notification System**: Smart notifications and alerts
- **Boot Receiver**: Auto-start functionality

### 🔧 Advanced Features
- **Widget Support**: Home screen widget for quick access
- **Deep Link Handling**: URL scheme integration (`echogpt://`)
- **File Sharing**: Document and media sharing capabilities
- **Web Services**: HTTP client integration for external APIs

## Architecture

### Core Components

1. **MainActivity.kt** - Main application interface
2. **Manager Classes** - Feature-specific managers:
   - `PermissionManager` - Handles runtime permissions
   - `VoiceManager` - Speech recognition and TTS
   - `ContactsManager` - Contact operations
   - `CalendarManager` - Calendar integration
   - `CameraManager` - Image capture and analysis
   - `LocationManager` - GPS and location services
   - `FileManager` - File operations and sharing

3. **Services**:
   - `VoiceService` - Background voice processing
   - `BackgroundService` - Continuous background operations

4. **Receivers**:
   - `AlarmReceiver` - Alarm and reminder notifications
   - `BootReceiver` - Auto-start on device boot

5. **Widget**:
   - `EchoGPTWidgetProvider` - Home screen widget

### Permissions Required

The app requests the following permissions:

```xml
<!-- Voice and Audio -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- Contacts and Calendar -->
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.WRITE_CONTACTS" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />

<!-- Alarms and Reminders -->
<uses-permission android:name="android.permission.SET_ALARM" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<!-- Camera and Storage -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- Location -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<!-- Background and Notifications -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<!-- Network -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## User Interface

The main interface features:
- **Voice Input Button** - Tap to start voice recognition
- **Quick Action Buttons** - Direct access to key features:
  - Contacts
  - Calendar
  - Set Alarm
  - Camera
  - Location
  - Share File
- **Response Area** - Displays AI responses and system feedback
- **Status Indicator** - Shows current operation status

## Usage Examples

### Voice Commands
- "Hello" → Greeting response
- "Weather" → Weather information request
- "Contact [name]" → Search contacts
- "Set alarm" → Create new alarm
- "Take photo" → Open camera

### Deep Link Integration
```
echogpt://query?q=Hello+EchoGPT
```

### Widget Usage
Add the EchoGPT widget to your home screen for quick voice access.

## Building the Project

1. **Prerequisites**:
   - Android Studio Arctic Fox or later
   - Android SDK 33
   - Kotlin 1.7.20+

2. **Build Commands**:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

3. **Testing**:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/deeptree/echogpt/
│   │   ├── MainActivity.kt
│   │   ├── manager/           # Feature managers
│   │   ├── service/           # Background services
│   │   ├── receiver/          # Broadcast receivers
│   │   └── widget/            # Home screen widget
│   ├── res/
│   │   ├── layout/            # UI layouts
│   │   ├── values/            # Strings, colors, themes
│   │   ├── drawable/          # Icons and graphics
│   │   └── xml/               # Configuration files
│   └── AndroidManifest.xml
└── src/test/                  # Unit tests
```

## Dependencies

Key dependencies include:
- **AndroidX Libraries**: Core, AppCompat, Material Design
- **Lifecycle Components**: ViewModel, LiveData
- **Camera**: CameraX for image capture
- **Location**: Google Play Services Location
- **Network**: Retrofit, OkHttp
- **Testing**: JUnit, Mockito, Espresso

## AI Integration Ready

The app is designed to integrate with AI services:
- Voice input processing pipeline
- Response generation framework
- Context-aware command processing
- Multi-modal input support (voice, text, image)

## Security & Privacy

- Runtime permission handling
- Secure file operations
- Privacy-compliant data access
- Background processing controls

## Future Enhancements

- Machine Learning integration
- Cloud AI service connectivity
- Advanced image analysis
- Natural language processing
- Multi-language support

---

**Version**: 1.0  
**License**: See LICENSE file  
**Package**: com.deeptree.echogpt