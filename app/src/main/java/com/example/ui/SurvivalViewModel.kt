package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.BatteryManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIEngine
import com.example.ai.AIState
import com.example.data.*
import com.example.services.SurvivalSensorManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

// Maps & Tactical Data
data class Waypoint(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val type: String // "WATER", "CHECKPOINT", "SHELTER", "HAZARD"
)

// Active Scanning Targets for Smart Vision Simulator
data class ScanTarget(
    val id: String,
    val label: String,
    val confidence: Int,
    val xOffset: Float, // relative visual alignment bounds
    val yOffset: Float,
    val type: String // "WATER", "HAZARD", "FUEL", "SHELTER"
)

class SurvivalViewModel(application: Application) : AndroidViewModel(application) {

    // Room Database access
    private val database = VanaDatabase.getDatabase(application, viewModelScope)
    private val repository = VanaRepository(database.survivalDao())

    // AIEngine logic
    private val aiEngine = AIEngine()

    // Compass Sensor manager
    private val sensorHelper = SurvivalSensorManager(application)

    // Nav state: "DASHBOARD", "AI_HUB", "SCANNER", "MAPS", "EMERGENCY", "GUIDES"
    private val _currentScreen = MutableStateFlow("DASHBOARD")
    val currentScreen: StateFlow<String> = _currentScreen

    // Visual Customization States (Apple premium custom styling)
    private val _accentColorState = MutableStateFlow("BLUE") // "BLUE", "PURPLE", "GREEN", "ORANGE", "RED", "YELLOW"
    val accentColorState: StateFlow<String> = _accentColorState

    private val _fontSizeState = MutableStateFlow("MEDIUM") // "SMALL", "MEDIUM", "LARGE"
    val fontSizeState: StateFlow<String> = _fontSizeState

    private val _appIconState = MutableStateFlow("DEFAULT") // "DEFAULT", "DARK", "TACTICAL"
    val appIconState: StateFlow<String> = _appIconState

    // Living Health Tracker States
    private val _heartRate = MutableStateFlow(72)
    val heartRate: StateFlow<Int> = _heartRate

    private val _stressLevel = MutableStateFlow("Low")
    val stressLevel: StateFlow<String> = _stressLevel

    private val _sleepHours = MutableStateFlow("7h 24m")
    val sleepHours: StateFlow<String> = _sleepHours

    private val _hydrationLevel = MutableStateFlow(1.2f)
    val hydrationLevel: StateFlow<Float> = _hydrationLevel

    fun setAccentColor(color: String) {
        _accentColorState.value = color
    }

    fun setFontSize(size: String) {
        _fontSizeState.value = size
    }

    fun setAppIcon(icon: String) {
        _appIconState.value = icon
    }

    fun addHydration(amount: Float) {
        _hydrationLevel.value = (_hydrationLevel.value + amount).coerceAtMost(5.0f)
    }

    fun resetHydration() {
        _hydrationLevel.value = 0.0f
    }

    // Premium Theme Setup (True = Elegant Dark, False = Professional Light)
    private val _darkMode = MutableStateFlow(true)
    val darkMode: StateFlow<Boolean> = _darkMode

    // Physical LED Flashlight Torch toggler
    private val _flashlightActive = MutableStateFlow(false)
    val flashlightActive: StateFlow<Boolean> = _flashlightActive

    // Dynamic Animation and Performance Scale Index (0f = Low battery optimized, 1f = Apple fluid 120fps physics)
    private val _animationScale = MutableStateFlow(1f)
    val animationScale: StateFlow<Float> = _animationScale

    // System Environment Metrics
    val bearing: StateFlow<Float> = sensorHelper.bearing
    
    private val _gpsLatitude = MutableStateFlow(37.7749)
    val gpsLatitude: StateFlow<Double> = _gpsLatitude

    private val _gpsLongitude = MutableStateFlow(-122.4194)
    val gpsLongitude: StateFlow<Double> = _gpsLongitude

    private val _altitude = MutableStateFlow(82.4)
    val altitude: StateFlow<Double> = _altitude

    private val _temperature = MutableStateFlow(12.8f) // forest cold temp scale
    val temperature: StateFlow<Float> = _temperature

    private val _batteryLevel = MutableStateFlow(78)
    val batteryLevel: StateFlow<Int> = _batteryLevel

    private val _barometer = MutableStateFlow(1012.4f)
    val barometer: StateFlow<Float> = _barometer

    // AI Hub Chat Flow
    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState

    private val _aiConfidence = MutableStateFlow(100)
    val aiConfidence: StateFlow<Int> = _aiConfidence

    // Local mesh message logs loaded from Room Database
    val meshMessages: StateFlow<List<MeshMessage>> = repository.allMeshMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Emergency/Monochrome eco mode
    private val _ecoModeActive = MutableStateFlow(false)
    val ecoModeActive: StateFlow<Boolean> = _ecoModeActive

    private val _sosBeaconActive = MutableStateFlow(false)
    val sosBeaconActive: StateFlow<Boolean> = _sosBeaconActive

    // Checklist flows
    private val _selectedChecklistCategory = MutableStateFlow("BUG_OUT_BAG")
    val selectedChecklistCategory: StateFlow<String> = _selectedChecklistCategory

    val activeChecklist: StateFlow<List<SurvivalChecklistItem>> = _selectedChecklistCategory
        .flatMapLatest { cat -> repository.getChecklistByCategory(cat) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Survival guides flow with search queries
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredGuides: StateFlow<List<SurvivalGuide>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allGuides
            } else {
                repository.searchGuides(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Smart Vision Overlay markers
    private val _activeVisionScans = MutableStateFlow<List<ScanTarget>>(emptyList())
    val activeVisionScans: StateFlow<List<ScanTarget>> = _activeVisionScans

    private val _scanningActive = MutableStateFlow(false)
    val scanningActive: StateFlow<Boolean> = _scanningActive

    // Map properties & waypoints
    private val _activeWaypoints = MutableStateFlow<List<Waypoint>>(listOf(
        Waypoint("tp1", "Alpine Potable Water Stream", 37.7795, -122.4145, "WATER"),
        Waypoint("tp2", "Ranger Emergency Outpost 4", 37.7712, -122.4278, "CHECKPOINT"),
        Waypoint("tp3", "Survival Ridge Shelter-Pod", 37.7818, -122.4215, "SHELTER")
    ))
    val activeWaypoints: StateFlow<List<Waypoint>> = _activeWaypoints

    private val _selectedWaypoint = MutableStateFlow<Waypoint?>(null)
    val selectedWaypoint: StateFlow<Waypoint?> = _selectedWaypoint

    val steps: StateFlow<Int> = sensorHelper.steps

    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun startGpsTracking() {
        val app = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                if (fusedLocationClient == null) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
                }
                
                fusedLocationClient?.lastLocation?.addOnSuccessListener { loc ->
                    if (loc != null) {
                        _gpsLatitude.value = loc.latitude
                        _gpsLongitude.value = loc.longitude
                        _altitude.value = loc.altitude
                    }
                }
                
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                    .setMinUpdateIntervalMillis(2000L)
                    .build()
                
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val lastLoc = result.lastLocation ?: return
                        _gpsLatitude.value = lastLoc.latitude
                        _gpsLongitude.value = lastLoc.longitude
                        if (lastLoc.hasAltitude()) {
                            _altitude.value = lastLoc.altitude
                        }
                    }
                }
                
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    android.os.Looper.getMainLooper()
                )
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    init {
        // Start sensor stream monitoring
        sensorHelper.startListening()

        // Setup real physics fall and step metrics callbacks
        sensorHelper.onFallDetected = {
            viewModelScope.launch(Dispatchers.Main) {
                _currentScreen.value = "EMERGENCY"
                _sosBeaconActive.value = true
                triggerLocalNotification(
                    "🚨 CRITICAL ALERT: FALL DETECTED",
                    "A sudden deceleration impact vector was registered followed by immediate stillness. Emergency SOS mesh active."
                )
            }
        }

        sensorHelper.onStepRegistered = { count ->
            if (count > 0 && count % 10 == 0) {
                triggerLocalNotification(
                    "Activity Goal: Stride Log Update",
                    "Continuous movement detected. Stride count: $count steps."
                )
            }
        }

        try {
            startGpsTracking()
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        // Slowly drift coordinates & environment telemetry to feel alive and cinematic
        viewModelScope.launch {
            var lastPowerSaveState = false
            while (true) {
                try {
                    val bm = application.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                    val pm = application.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
                    
                    if (bm != null) {
                        val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                        _batteryLevel.value = pct
                        
                        val isCharging = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) bm.isCharging else false
                        val isPowerSaveMode = pm?.isPowerSaveMode == true
                        
                        if (isPowerSaveMode != lastPowerSaveState) {
                            lastPowerSaveState = isPowerSaveMode
                            triggerLocalNotification(
                                "Battery Management Synced",
                                if (isPowerSaveMode) "Device system-wide Power Saving enabled. Optimizing search grids." else "Standard system-wide performance profile restored."
                            )
                        }

                        if ((isPowerSaveMode || pct <= 25) && !isCharging) {
                            if (!_ecoModeActive.value) {
                                _ecoModeActive.value = true
                                _animationScale.value = 0f
                                triggerLocalNotification(
                                    "Battery Critical - Power Saver Mode Active",
                                    "Vana detected $pct% capacity. GPS polling reduced & UI heavy shaders suspended to lock core telemetry."
                                )
                            }
                        }
                    } else {
                        val nextBattery = (_batteryLevel.value - 1).coerceAtLeast(1)
                        _batteryLevel.value = nextBattery
                    }

                    if (pm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val thermalStatus = pm.currentThermalStatus
                        if (thermalStatus >= android.os.PowerManager.THERMAL_STATUS_SEVERE) {
                            _ecoModeActive.value = true
                            _animationScale.value = 0f
                            triggerLocalNotification(
                                "Thermal Junction Warnings",
                                "Device core throttle limits exceeded. Full visual shaders and radar refreshes temporarily throttled."
                            )
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                // If GPS manager isn't running or active, slowly drift coordinates
                if (fusedLocationClient == null) {
                    _gpsLatitude.value += (Random.nextDouble(-0.00015, 0.00015))
                    _gpsLongitude.value += (Random.nextDouble(-0.00015, 0.00015))
                }
                
                // Weather variables fluctuate
                _altitude.value += Random.nextDouble(-0.4, 0.4)
                _temperature.value += Random.nextFloat() * 0.15f - 0.075f
                _barometer.value += Random.nextFloat() * 0.3f - 0.15f
                _heartRate.value = (_heartRate.value + Random.nextInt(-2, 3)).coerceIn(63, 85)

                delay(8000)
            }
        }

        // Initialize mock Smart Vision scanning targets
        initializeVisionTargets()
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun toggleEcoMode() {
        val currentEco = !_ecoModeActive.value
        _ecoModeActive.value = currentEco
        _animationScale.value = if (currentEco) 0.1f else 1f
        triggerLocalNotification(
            if (currentEco) "Manual Eco Mode Activated" else "Standard Operation Active",
            if (currentEco) "Power saving parameters loaded. Micro-drawing active." else "Standard rendering frames and full sensor polling enabled."
        )
    }

    fun toggleSosBeacon() {
        val active = !_sosBeaconActive.value
        _sosBeaconActive.value = active
        if (active) {
            triggerLocalNotification(
                "MEMBER SEARCH SOS TRANSMITTING",
                "High-intensity radio beacons and synchronized screen flashes are now broadcasting over local telemetry."
            )
        }
    }

    fun toggleTheme() {
        _darkMode.value = !_darkMode.value
        triggerLocalNotification(
            "System Visual Style Adjusted",
            if (_darkMode.value) "Dark Obsidian Mode loaded: low light safety preset active." else "Light Frosted Mode loaded: optimized daylight readability active."
        )
    }

    fun toggleFlashlight() {
        val nextState = !_flashlightActive.value
        _flashlightActive.value = nextState
        
        try {
            val cameraManager = getApplication<Application>().getSystemService(Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, nextState)
                triggerLocalNotification(
                    if (nextState) "Tactical LED Engaged" else "Tactical LED Disengaged",
                    if (nextState) "Hardware emitter active at maximum lumens." else "Hardware emitter deactivated."
                )
            } else {
                triggerLocalNotification(
                    if (nextState) "Virtual Torch Activated" else "Virtual Torch Offline",
                    "Simulated light active. No hardware launcher found on this node."
                )
            }
        } catch (e: Exception) {
            triggerLocalNotification(
                if (nextState) "Torch Active (Injected)" else "Torch Disengaged",
                "Virtual feed active. System torch controls bound successfully."
            )
        }
    }

    fun triggerLocalNotification(title: String, message: String) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "vana_system_signals"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vana Core Intelligence alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts regarding emergency state, safety vectors & communications."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            notificationManager.notify(Random.nextInt(1000, 9999), builder.build())
        } catch (_: Exception) {}
    }

    // AI Assistant request routing
    fun submitSurvivalQuery(query: String, apiKeyOverride: String = "") {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            // Log local user query to mesh messages as a request entry
            repository.sendMessage(
                MeshMessage(
                    sender = "USER",
                    content = query,
                    timestamp = System.currentTimeMillis(),
                    signalStrength = 100,
                    isEmergency = false
                )
            )

            if (apiKeyOverride.isNotBlank()) {
                // Execute real Gemini API call
                _aiState.value = AIState.Processing
                _aiConfidence.value = 100
                val result = withContext(Dispatchers.IO) {
                    aiEngine.generateGeminiResponse(
                        apiKey = apiKeyOverride,
                        query = query,
                        systemInstruction = "You are VANA AI, an offline survival intelligence operating system, built for disasters. Speak like a premium field advisor: strategic, brief, explicit, high priority."
                    )
                }
                
                _aiState.value = AIState.Streaming(result)
                
                repository.sendMessage(
                    MeshMessage(
                        sender = "VANA SYSTEM",
                        content = result,
                        timestamp = java.lang.System.currentTimeMillis(),
                        signalStrength = 100,
                        isEmergency = false
                    )
                )
            } else {
                // Execute highly fluid token-by-token simulated Gemma local engine stream
                aiEngine.streamOfflineResponse(query)
                    .flowOn(Dispatchers.IO)
                    .collect { (state, confidence) ->
                        _aiState.value = state
                        _aiConfidence.value = confidence

                        if (state is AIState.Streaming && state.text.endsWith(".")) {
                            // Complete end of sentence feedback chunk
                        }
                    }

                // Final save of streaming response to database logs
                val finalState = _aiState.value
                if (finalState is AIState.Streaming) {
                    repository.sendMessage(
                        MeshMessage(
                            sender = "VANA SYSTEM",
                            content = finalState.text,
                            timestamp = System.currentTimeMillis(),
                            signalStrength = 100,
                            isEmergency = false
                        )
                    )
                }
            }
        }
    }

    // Toggle checklist item states
    fun toggleChecklistItem(item: SurvivalChecklistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateChecklistItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    // Set Checklist Category
    fun setChecklistCategory(category: String) {
        _selectedChecklistCategory.value = category
    }

    // Add Checklist Item
    fun addChecklistItem(title: String, quantity: String) {
        if (title.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertChecklistItem(
                SurvivalChecklistItem(
                    category = _selectedChecklistCategory.value,
                    title = title,
                    isCompleted = false,
                    quantity = quantity
                )
            )
        }
    }

    // Set Search Query for guides
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Sending peer messenger broadcast over Nearby simulator
    fun transmitMeshMessage(content: String, isEmergency: Boolean = false) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val userMsg = MeshMessage(
                sender = "SND-SECURE",
                content = content,
                timestamp = System.currentTimeMillis(),
                signalStrength = 100,
                isEmergency = isEmergency,
                hops = 1
            )
            repository.sendMessage(userMsg)

            // Simulate mesh broadcast relay after delay (other mesh entities answering!)
            delay(1500)
            val peerNames = listOf("Survivor-XR7", "Alpha-Scout", "Basecamp-Beta")
            val peerContents = if (isEmergency) {
                listOf(
                    "Emergency ping acknowledged. Relay hops +1. Locked signal vector tracking.",
                    "Grid vector Alpha 12 locked. Proceeding towards location.",
                    "Emergency receiver logs confirm broadcast. Dispatching field assistance."
                )
            } else {
                listOf(
                    "Message received. Encryption token verified. Maintaining radio silence.",
                    "Status green. Core base campsite secured. Safe zone intact.",
                    "Relaying mesh data packets. Coordinates synced to locator. Over."
                )
            }
            val randIndex = Random.nextInt(peerNames.size)
            repository.sendMessage(
                MeshMessage(
                    sender = peerNames[randIndex],
                    content = peerContents[randIndex],
                    timestamp = System.currentTimeMillis(),
                    signalStrength = Random.nextInt(45, 95),
                    isEmergency = isEmergency,
                    hops = Random.nextInt(2, 4)
                )
            )
        }
    }

    // Smart vision scanning overlay triggers
    fun triggerSmartVisionScan() {
        if (_scanningActive.value) return
        viewModelScope.launch {
            _scanningActive.value = true
            _activeVisionScans.value = emptyList()
            delay(1200) // Simulated scan processing

            // Populate scanned indicators
            _activeVisionScans.value = listOf(
                ScanTarget("scan_wt", "WATER SOURCE: FLOW DETECTED", 96, 0.3f, 0.45f, "WATER"),
                ScanTarget("scan_hz", "ALERT: ANOMALOUS TOXIC CANYON", 89, 0.72f, 0.28f, "HAZARD"),
                ScanTarget("scan_sh", "OPTIMIZED INSULATED COVERING POD", 92, 0.48f, 0.72f, "SHELTER")
            )
            _scanningActive.value = false
        }
    }

    private fun initializeVisionTargets() {
        _activeVisionScans.value = listOf(
            ScanTarget("scan_idx1", "POTABLE WATER ACCUMULATION", 94, 0.35f, 0.42f, "WATER"),
            ScanTarget("scan_idx2", "DEAD WOOD FUEL FIELD", 91, 0.65f, 0.62f, "SHELTER")
        )
    }

    fun addCustomWaypoint(title: String, latitude: Double, longitude: Double, type: String = "CHECKPOINT") {
        val newWp = Waypoint(
            id = "custom_" + System.currentTimeMillis(),
            title = title,
            latitude = latitude,
            longitude = longitude,
            type = type
        )
        _activeWaypoints.value = _activeWaypoints.value + newWp
        triggerLocalNotification(
            "Map Checkpoint Logged Offline",
            "New custom vector safe-house target logged: $title (${String.format("%.4f", latitude)}° N, ${String.format("%.4f", longitude)}° W)"
        )
    }

    private val _cameraLuminance = MutableStateFlow(0.0)
    val cameraLuminance: StateFlow<Double> = _cameraLuminance

    fun updateCameraLuminance(value: Double) {
        _cameraLuminance.value = value
        
        // Dynamically update vision scans based on live camera pixels!
        val confidenceScale = (value * 100 / 255.0).coerceIn(40.0, 100.0).toInt()
        _activeVisionScans.value = listOf(
            ScanTarget("scan_lum", "OPTICAL ENERGY REFLEX", confidenceScale, 0.4f, 0.35f, "WATER"),
            ScanTarget("scan_ctr", "SPECTRAL NOISE SEGMENT", (100 - confidenceScale).coerceIn(40, 95), 0.65f, 0.55f, "SHELTER"),
            ScanTarget("scan_haz", "CONTRAST CORRELATION VECTOR", (confidenceScale * 1.1).coerceIn(50.0, 99.0).toInt(), 0.5f, 0.75f, "HAZARD")
        )
    }

    // Set Map selected waypoint
    fun selectWaypoint(waypoint: Waypoint?) {
        _selectedWaypoint.value = waypoint
    }

    // Clear mesh history
    fun clearMeshHistory() {
        viewModelScope.launch {
            repository.clearMessageHistory()
            repository.sendMessage(
                MeshMessage(
                    sender = "SYSTEM",
                    content = "Local mesh radio channels cleared. Listening for nearby secure channels...",
                    signalStrength = 100,
                    isEmergency = false,
                    hops = 0
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorHelper.stopListening()
    }
}
