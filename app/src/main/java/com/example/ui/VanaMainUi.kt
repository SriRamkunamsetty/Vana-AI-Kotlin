package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIState
import com.example.data.MeshMessage
import com.example.data.SurvivalChecklistItem
import com.example.data.SurvivalGuide
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VanaAppShell(viewModel: SurvivalViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val ecoMode by viewModel.ecoModeActive.collectAsState()
    val isDark by viewModel.darkMode.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()

    // Setup physical hardware permissions on start for real GPS & step tracking
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsMap ->
            val fineGranted = permissionsMap[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = permissionsMap[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineGranted || coarseGranted) {
                viewModel.startGpsTracking()
            }
        }
    )

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                add(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }.toTypedArray()
        permissionLauncher.launch(permissions)
    }

    // Professional Apple Minimalist palettes based on isDark theme toggle
    val appBackground = if (isDark) {
        if (ecoMode) Color(0xFF070707) else Color(0xFF0F0F0F)
    } else {
        if (ecoMode) Color(0xFFFAFAFA) else Color(0xFFF3F4F6)
    }

    val selectedAccent by viewModel.accentColorState.collectAsState()
    val primaryColor = if (isDark) {
        when (selectedAccent) {
            "BLUE" -> IntelligentBlueAccent
            "PURPLE" -> Color(0xFFD0B3FF)
            "GREEN" -> Color(0xFF81C784)
            "ORANGE" -> Color(0xFFFFB74D)
            "RED" -> Color(0xFFE57373)
            "YELLOW" -> Color(0xFFFFF176)
            else -> IntelligentBlueAccent
        }
    } else {
        when (selectedAccent) {
            "BLUE" -> SubtleBlueAccent
            "PURPLE" -> Color(0xFF7E57C2)
            "GREEN" -> Color(0xFF43A047)
            "ORANGE" -> Color(0xFFFB8C00)
            "RED" -> Color(0xFFE53935)
            "YELLOW" -> Color(0xFFFDD835)
            else -> SubtleBlueAccent
        }
    }
    val secondaryColor = if (isDark) SpaceGray else SoftSilver
    
    val cardBackground = if (isDark) {
        if (ecoMode) Color(0xFF141414) else DeepGraphite.copy(alpha = 0.92f)
    } else {
        if (ecoMode) Color(0xFFFFFFFF) else PureWhite.copy(alpha = 0.88f)
    }

    val surfaceText = if (isDark) SoftWhiteText else GraphiteText
    val secondaryText = if (isDark) Color(0xFF8E8E93) else Color(0xFF111111).copy(alpha = 0.6f)
    val cardBorder = if (isDark) GlassDarkBorder else GlassLightBorder

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(CarbonBlack, appBackground)
                    } else {
                        listOf(FrostWhite, appBackground)
                    }
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {

        // Ambient layout background - very thin soft light shadows/gradients
        if (!ecoMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isDark) 0.15f else 0.4f)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = if (isDark) {
                                    listOf(primaryColor.copy(alpha = 0.2f), Color.Transparent)
                                } else {
                                    listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent)
                                }
                            ),
                            radius = size.width,
                            center = Offset(size.width / 2f, size.height * 0.2f)
                        )
                    }
            )
        }

        // Main Screen Area with customized floating headers
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Control Panel Top Bar (Floating and translucent)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBackground.copy(alpha = 0.95f))
                    .border(BorderStroke(0.5.dp, cardBorder), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (ecoMode) AppleOrange else AppleGreen)
                    )
                    Column {
                        Text(
                            text = "VANA SYSTEM v2.6",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (ecoMode) "ECO ENERGY RETENTION" else "QUANTUM INTELLIGENCE LINKED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = secondaryText
                        )
                    }
                }

                // Controls Right (Battery, Theme Toggle)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Battery indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (batteryLevel > 25) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                            contentDescription = "Battery",
                            tint = if (batteryLevel > 25) primaryColor else AppleOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$batteryLevel%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = surfaceText
                        )
                    }

                    // Theme Picker Toggle
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Screen Router with padded area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
                    .imePadding()
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)) with
                                fadeOut(animationSpec = tween(120))
                    },
                    label = "VanaOSNavigation"
                ) { screen ->
                    when (screen) {
                        "DASHBOARD" -> DashboardScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "AI_HUB" -> AiCommsHub(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText,
                            ecoMode = ecoMode
                        )
                        "SCANNER" -> SmartVisionScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            accentColor = primaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "MAPS" -> OfflineMapsScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "EMERGENCY" -> EmergencyScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            accentColor = primaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText,
                            ecoMode = ecoMode
                        )
                        "GUIDES" -> SurvivalGuidesScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "EXPLORE" -> ExploreHubScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "FITNESS_HEALTH" -> FitnessHealthHubScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "HISTORY" -> HistoryScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "PROFILE" -> ProfileScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "COMMS" -> CommunicationScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "NEARBY" -> NearbyDevicesScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "SETTINGS" -> SettingsScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "THEME" -> ThemeScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText
                        )
                        "BATTERY_INFO" -> BatteryInfoScreen(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            cardBg = cardBackground,
                            textColor = surfaceText,
                            secTextColor = secondaryText,
                            ecoMode = ecoMode
                        )
                    }
                }
            }
        }

        // Floating Glass Morphic Dock Menu (Tact touch areas > 48dp)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            MorphicDockMenu(
                currentScreen = currentScreen,
                onScreenSelected = { viewModel.setScreen(it) },
                primaryColor = primaryColor,
                cardBg = cardBackground,
                textColor = surfaceText
            )
        }
    }
}

// Seamless Background Grids for Cyber Punk atmosphere
@Composable
fun CyberGridBackground(primaryColor: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.08f)
    ) {
        val cellSide = 45.dp.toPx()
        val cols = (size.width / cellSide).toInt()
        val rows = (size.height / cellSide).toInt()

        for (i in 0..cols) {
            drawLine(
                color = primaryColor,
                start = Offset(i * cellSide, 0f),
                end = Offset(i * cellSide, size.height),
                strokeWidth = 1f
            )
        }
        for (j in 0..rows) {
            drawLine(
                color = primaryColor,
                start = Offset(0f, j * cellSide),
                end = Offset(size.width, j * cellSide),
                strokeWidth = 1f
            )
        }
    }
}

// 1. HOME SCREEN: TACTICAL FIELD SYSTEM MONITOR
@Composable
fun DashboardScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val bearing by viewModel.bearing.collectAsState()
    val lat by viewModel.gpsLatitude.collectAsState()
    val lon by viewModel.gpsLongitude.collectAsState()
    val alt by viewModel.altitude.collectAsState()
    val battery by viewModel.batteryLevel.collectAsState()
    val temp by viewModel.temperature.collectAsState()
    val baro by viewModel.barometer.collectAsState()
    val steps by viewModel.steps.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Futuristic Animated Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VANA OFF-GRID SYSTEM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Survival Intelligence HUD",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = textColor,
                        lineHeight = 28.sp
                    )
                }

                // Eco status indicator capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryColor.copy(alpha = 0.15f))
                        .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, java.lang.Integer.max(1, 4).dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                        Text(
                            text = "SECURE-LINK ACTIVE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Cinematic Live Neural Radar Sphere
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .testTag("radar_panel"),
                contentAlignment = Alignment.Center
            ) {
                NeuralRadarWidget(
                    bearing = bearing,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    textColor = textColor
                )
            }
        }

        // Live Coordinate Position Grid Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TelemetryCard(
                    modifier = Modifier.weight(1f).clickable { viewModel.setScreen("MAPS") },
                    title = "GPS GEO-GRID COORDS",
                    metricValue = String.format("%.5f° N", lat),
                    subMark = String.format("%.5f° W", -lon),
                    icon = Icons.Default.MyLocation,
                    accentColor = secondaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )

                TelemetryCard(
                    modifier = Modifier.weight(1f).clickable { viewModel.setScreen("MAPS") },
                    title = "ALTITUDE VECTOR",
                    metricValue = String.format("%.1f METERS", alt),
                    subMark = "+/- 0.6M CALIBRATION",
                    icon = Icons.Default.Terrain,
                    accentColor = primaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
            }
        }

        // Live Health & Fitness Stride Tracker metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TelemetryCard(
                    modifier = Modifier.weight(1f).clickable { viewModel.setScreen("FITNESS_HEALTH") },
                    title = "HEALTH STRIDE RADAR",
                    metricValue = "$steps STEPS",
                    subMark = String.format("%.2f KCAL EXPENDED", steps * 0.045f),
                    icon = Icons.Default.DirectionsRun,
                    accentColor = primaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )

                TelemetryCard(
                    modifier = Modifier.weight(1f).clickable { viewModel.setScreen("FITNESS_HEALTH") },
                    title = "TREKKING ENDURANCE",
                    metricValue = String.format("%.2f KM EST DIST", steps * 0.00075f),
                    subMark = "STRENGTH STATUS: OPTIMAL",
                    icon = Icons.Default.FitnessCenter,
                    accentColor = secondaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
            }
        }

        // Multi Environmental Quick Stats Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickSensCard(
                    modifier = Modifier.weight(1f),
                    title = "CORE CHIP TEMP",
                    value = String.format("%.1f °C", temp),
                    status = "OPTIMIZED",
                    primaryColor = primaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
                QuickSensCard(
                    modifier = Modifier.weight(1f),
                    title = "BAROMETER PRESSURE",
                    value = String.format("%.1f hPa", baro),
                    status = "FOREST BASE",
                    primaryColor = secondaryColor,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
                QuickSensCard(
                    modifier = Modifier.weight(1f).clickable { viewModel.setScreen("BATTERY_INFO") },
                    title = "BATTERY CAPACITY",
                    value = "$battery%",
                    status = if (battery > 20) "SAFE" else "CRITICAL_ECO",
                    primaryColor = if (battery > 20) primaryColor else SurvivalOrange,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
            }
        }

        // High priority operations launcher
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "IMMEDIATE EMERGENCY ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.setScreen("EMERGENCY") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sos_launcher"),
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SurvivalOrange)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Emergency, "SOS", tint = SurvivalOrange, modifier = Modifier.size(16.dp))
                            Text("TRIGGER SOS BEACON", color = SurvivalOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { viewModel.setScreen("AI_HUB") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_launcher"),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, primaryColor)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Forum, "Chat", tint = primaryColor, modifier = Modifier.size(16.dp))
                            Text("FIELD INTEL HUB", color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 2. AI COMMUNICATION HUB (AI Gemma / Ollama vs. Offline Human Mesh radio)
@Composable
fun AiCommsHub(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color,
    ecoMode: Boolean
) {
    var selectedTab by remember { mutableStateOf("AI_INTELLIGENCE") } // "AI_INTELLIGENCE" or "MESH_CHANNELS"
    var textInput by remember { mutableStateOf("") }
    val aiState by viewModel.aiState.collectAsState()
    val aiConfidence by viewModel.aiConfidence.collectAsState()
    val meshMessages by viewModel.meshMessages.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Comms Switcher Dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            TabCapsule(
                modifier = Modifier.weight(1f),
                title = "OFFLINE AI ASSIST",
                isSelected = selectedTab == "AI_INTELLIGENCE",
                icon = Icons.Default.Hardware,
                activeColor = primaryColor,
                textColor = textColor
            ) { selectedTab = "AI_INTELLIGENCE" }

            TabCapsule(
                modifier = Modifier.weight(1f),
                title = "P2P MESH INTERCOM",
                isSelected = selectedTab == "MESH_CHANNELS",
                icon = Icons.Default.CellTower,
                activeColor = secondaryColor,
                textColor = textColor
            ) { selectedTab = "MESH_CHANNELS" }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render dynamic screen inside card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            if (selectedTab == "AI_INTELLIGENCE") {
                // AI Intelligence Streaming layout
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header metric
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOCAL DEPLOYED MODEL: GEMMA-2B",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )

                        Text(
                            text = "MODEL CONFIDENCE: $aiConfidence%",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (aiConfidence > 90) primaryColor else SurvivalOrange
                        )
                    }

                    Divider(color = primaryColor.copy(alpha = 0.15f), thickness = 1.dp)

                    // Scrollable answer content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (val state = aiState) {
                            is AIState.Idle -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Psychology,
                                        "Brain",
                                        tint = primaryColor.copy(alpha = 0.3f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Vana Offline Reasoning Active",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                    Text(
                                        text = "Type how to treat injuries, find food, or purify water. Our local Gemma core executes instantly.",
                                        fontSize = 11.sp,
                                        color = secTextColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                            is AIState.Processing -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "BUFFERING PARAMETER MATRIX...",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = primaryColor
                                    )
                                }
                            }
                            is AIState.Streaming -> {
                                Text(
                                    text = state.text,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Default,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            is AIState.Error -> {
                                Text(
                                    text = "STREAM TIMEOUT: ${state.errorMsg}\nRetrying connection vector...",
                                    color = SurvivalOrange,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            } else {
                // P2P MESH INTERCOM Radio logs layout
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TACTICAL CO-ORDINATION FIELD",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = secondaryColor,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = { viewModel.clearMeshHistory() }) {
                            Text("WIPE CHANNELS", color = SurvivalOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(meshMessages) { msg ->
                            MeshMessageCapsule(msg = msg, primaryColor = primaryColor, secondaryColor = secondaryColor)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Console Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .testTag("ai_text_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = primaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = if (selectedTab == "AI_INTELLIGENCE") "Ask Offline Intelligence..." else "Broadcast encrypted radio signal...",
                        color = secTextColor,
                        fontSize = 13.sp
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (selectedTab == "AI_INTELLIGENCE") {
                        viewModel.submitSurvivalQuery(textInput)
                    } else {
                        viewModel.transmitMeshMessage(textInput)
                    }
                    textInput = ""
                    keyboardController?.hide()
                })
            )

            // Submit Button
            IconButton(
                onClick = {
                    if (selectedTab == "AI_INTELLIGENCE") {
                        viewModel.submitSurvivalQuery(textInput)
                    } else {
                        viewModel.transmitMeshMessage(textInput)
                    }
                    textInput = ""
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTab == "AI_INTELLIGENCE") primaryColor else secondaryColor)
                    .testTag("send_btn")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    "Send Request",
                    tint = DeepForestBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CameraViewfinder(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    onLuminanceUpdated: (Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasCameraPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission.value = granted
        }
    )

    val cameraProviderRef = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val cameraHardwareError = remember { mutableStateOf(false) }

    DisposableEffect(hasCameraPermission.value, lifecycleOwner) {
        onDispose {
            try {
                cameraProviderRef.value?.unbindAll()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (hasCameraPermission.value && !cameraHardwareError.value) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    try {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                cameraProviderRef.value = cameraProvider
                                val preview = Preview.Builder().build().apply {
                                    setSurfaceProvider(previewView.surfaceProvider)
                                }
                                
                                val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                                    .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .apply {
                                        setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                            try {
                                                val buffer = imageProxy.planes[0].buffer
                                                var sum = 0L
                                                val size = buffer.remaining()
                                                if (size > 0) {
                                                    var count = 0
                                                    for (i in 0 until size step 40) {
                                                        if (i < buffer.limit()) {
                                                            sum += java.lang.Byte.toUnsignedInt(buffer.get(i))
                                                            count++
                                                        }
                                                    }
                                                    if (count > 0) {
                                                        val avg = sum.toDouble() / count
                                                        onLuminanceUpdated(avg)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            } finally {
                                                imageProxy.close()
                                            }
                                        }
                                    }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                if (cameraProvider.hasCamera(cameraSelector)) {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis
                                    )
                                } else {
                                    cameraHardwareError.value = true
                                }
                            } catch (t: Throwable) {
                                t.printStackTrace()
                                cameraHardwareError.value = true
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        cameraHardwareError.value = true
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (hasCameraPermission.value && cameraHardwareError.value) {
            // Elegant simulator placeholder when physical camera has no backing hardware stream
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Tactical Feed Active",
                    tint = primaryColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "OPTICAL STREAM SIMULATOR",
                    color = primaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Terrain indices and waypoint tracking are procedurally drawn. Physical CameraX bindings are suspended.",
                    color = primaryColor.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Apple-inspired elegant auth portal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Camera Permission Required",
                    tint = primaryColor,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "CAMERA INTERFACE DEACTIVATED",
                    color = primaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Provide camera permissions to dynamically index terrain elevations & map nearby secure pods offline.",
                    color = primaryColor.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("AUTHORIZE SCAN FEED", color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

// 3. SMART VISION (Camera viewfinder overlay + Object recognition simulator)
@Composable
fun SmartVisionScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val visionScans by viewModel.activeVisionScans.collectAsState()
    val isScanning by viewModel.scanningActive.collectAsState()
    val flashlightActive by viewModel.flashlightActive.collectAsState()

    // Interactive sweep alignment animation values
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweepTransition")
    val sweepFloat by infiniteTransition.animateFloat(
        initialValue = -0.05f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweepAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic scanning viewport (Substituted overlay for Camera)
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .background(Color.Black)
                .testTag("camera_viewpoint")
        ) {
            // Real physical camera feed viewfinder!
            CameraViewfinder(
                modifier = Modifier.fillMaxSize(),
                primaryColor = primaryColor,
                onLuminanceUpdated = { lum ->
                    viewModel.updateCameraLuminance(lum)
                }
            )

            // Sweeping green scanner laser
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.04f)
                    .align(Alignment.TopCenter)
                    .offset(y = (sweepFloat * 430).dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, primaryColor, Color.Transparent)
                        )
                    )
            )

            // Render tagged landmarks / items detected
            visionScans.forEach { scan ->
                TargetOverlayIndicator(
                    scan = scan,
                    primaryColor = if (scan.type == "HAZARD") accentColor else primaryColor,
                    textColor = textColor
                )
            }

            // Central crosshair overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val len = 20.dp.toPx()

                // Drawing lines
                drawLine(primaryColor.copy(alpha = 0.7f), Offset(cx - len, cy), Offset(cx - 5.dp.toPx(), cy), 2f)
                drawLine(primaryColor.copy(alpha = 0.7f), Offset(cx + 5.dp.toPx(), cy), Offset(cx + len, cy), 2f)
                drawLine(primaryColor.copy(alpha = 0.7f), Offset(cx, cy - len), Offset(cx, cy - 5.dp.toPx()), 2f)
                drawLine(primaryColor.copy(alpha = 0.7f), Offset(cx, cy + 5.dp.toPx()), Offset(cx, cy + len), 2f)

                // Circle boundary
                drawCircle(primaryColor.copy(alpha = 0.15f), 100.dp.toPx(), Offset(cx, cy))
            }

            // Top Status Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .align(Alignment.TopStart)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VANA SMART-VISION ENG",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Text(
                        text = if (isScanning) "SCAN IN PROGRESS..." else "AUTO-LOCATOR SECURE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isScanning) accentColor else primaryColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Trigger action controls (Dual Console containing Sweep Scan and Flashlight controls)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.triggerSmartVisionScan() },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("scan_trigger_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Text("EXTRACTING TOPOGRAPHY...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        Icon(Icons.Filled.FlipCameraAndroid, "Scan", tint = Color.Black)
                        Text("EXECUTE NEURAL SWEEP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // High Fidelity Flashlight Activator Panel
            IconButton(
                onClick = { viewModel.toggleFlashlight() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (flashlightActive) AppleOrange.copy(alpha = 0.2f) else primaryColor.copy(alpha = 0.1f))
                    .border(BorderStroke(1.dp, if (flashlightActive) AppleOrange else primaryColor.copy(alpha = 0.3f)), RoundedCornerShape(14.dp))
                    .testTag("flashlight_control_btn")
            ) {
                Icon(
                    imageVector = if (flashlightActive) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                    contentDescription = "Torch Toggle",
                    tint = if (flashlightActive) AppleOrange else primaryColor
                )
            }
        }

        // Scanned objects listings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "IDENTIFIED LANDMARKS ON SPECTRUM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.height(120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visionScans) { scan ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (scan.type == "HAZARD") accentColor else primaryColor)
                            )
                            Text(scan.label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Text(
                            "CONF: ${scan.confidence}%",
                            color = if (scan.type == "HAZARD") accentColor else primaryColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// 4. OFFLINE MAPS
@Composable
fun OfflineMapsScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val lat by viewModel.gpsLatitude.collectAsState()
    val lon by viewModel.gpsLongitude.collectAsState()
    val waypoints by viewModel.activeWaypoints.collectAsState()
    val selectedWp by viewModel.selectedWaypoint.collectAsState()

    var mapScale by remember { mutableStateOf(1.0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOPOLOGIC VECTOR MAP (OFFLINE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = secondaryColor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Terrain Contour Model",
                    fontSize = 20.sp,
                    color = textColor,
                    fontWeight = FontWeight.Light
                )
            }

            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier.background(cardBg).border(1.dp, secondaryColor.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.MyLocation, "Recenter", tint = secondaryColor)
                }
                IconButton(
                    onClick = { mapScale = (mapScale + 0.2f).coerceAtMost(2.5f) },
                    modifier = Modifier.background(cardBg).border(1.dp, secondaryColor.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Zoom", tint = secondaryColor)
                }
                IconButton(
                    onClick = { mapScale = (mapScale - 0.2f).coerceAtLeast(0.5f) },
                    modifier = Modifier.background(cardBg).border(1.dp, secondaryColor.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Remove, "Zoom Out", tint = secondaryColor)
                }
            }
        }

        // Live Vector Drawing Topo Canvas
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, secondaryColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .background(if (textColor == GraphiteText) Color(0xFFE5E7EB) else Color(0xFF070707))
                .testTag("offline_canvas_map")
        ) {
            val bearing by viewModel.bearing.collectAsState()
            TopographicalMapCanvas(
                lat = lat,
                lon = lon,
                bearing = bearing,
                waypoints = waypoints,
                selectedWp = selectedWp,
                scale = mapScale,
                offsetX = offsetX,
                offsetY = offsetY,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                onScroll = { dx, dy ->
                    offsetX += dx
                    offsetY += dy
                },
                onWaypointSelected = { viewModel.selectWaypoint(it) },
                onMapDoubleTapped = { wpLat, wpLon ->
                    viewModel.addCustomWaypoint("Custom Safe-Pod", wpLat, wpLon)
                }
            )

            // Diagnostic Map Legend Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, secondaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(secondaryColor, CircleShape))
                        Text("Active User Locator", color = textColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(primaryColor, CircleShape))
                        Text("Surviving Safe-Pod Checkpoints", color = textColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Selected waypoint parameters and detail view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardBg)
                .border(1.dp, secondaryColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            if (selectedWp != null) {
                val wp = selectedWp!!
                // Calculate distance manually with a nice visual formula format
                val dist = sqrt((wp.latitude - lat).pow(2) + (wp.longitude - lon).pow(2)) * 111300 // roughly conversion to meters
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = wp.title,
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = wp.type,
                            color = if (wp.type == "WATER") secondaryColor else primaryColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(secondaryColor.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TARGET GEO-COORDS", color = secTextColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(String.format("%.5f° N, %.5f° W", wp.latitude, -wp.longitude), color = textColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("DISTANCE VECTOR", color = secTextColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(String.format("%.1f METERS", dist), color = secondaryColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TouchApp, "Tap", tint = secondaryColor.copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TAP LANDMARK DOTS ON MAP TO VECTOR TRAVEL DIRECTIONS",
                        color = secTextColor,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 5. EMERGENCY SCREEN (High level priorities SOS + Morse Code flash + Eco mode)
@Composable
fun EmergencyScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color,
    ecoMode: Boolean
) {
    val sosBeaconActive by viewModel.sosBeaconActive.collectAsState()

    // Breathing pulse trigger for giant SOS ring
    val infiniteTransition = rememberInfiniteTransition(label = "SosBeaconTransition")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SosPulseAnimation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "CRITICAL EMERGENCY SYSTEM",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Offline Rescue Beacon",
                    fontSize = 22.sp,
                    color = textColor,
                    fontWeight = FontWeight.Light
                )
            }
        }

        // Massive breathing SOS beacon button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Pulse background ring when beacon is active
                if (sosBeaconActive) {
                    Box(
                        modifier = Modifier
                            .size((120 * pulseSize).dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f))
                            .border(2.dp, accentColor.copy(alpha = 0.3f * (1.5f - pulseSize)), CircleShape)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleSosBeacon() },
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(if (sosBeaconActive) accentColor else accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor, CircleShape)
                        .testTag("sos_beacon_toggle")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EmergencyShare,
                            "SOS Indicator",
                            tint = if (sosBeaconActive) Color.Black else accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (sosBeaconActive) "BEACON ON" else "ACTIVATE",
                            color = if (sosBeaconActive) Color.Black else accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // SOS Morse Code Flash & ECO Mode Toggle Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Eco mode battery saver capsule
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .border(1.dp, if (ecoMode) Color(0xFF00FF44) else primaryColor.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                        .clickable { viewModel.toggleEcoMode() }
                        .padding(14.dp)
                        .testTag("eco_mode_toggle")
                ) {
                    Column {
                        Icon(
                            Icons.Default.BatteryChargingFull,
                            "Eco Mode",
                            tint = if (ecoMode) Color(0xFF00FF44) else textColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "MONOCHROME ECO MODE",
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (ecoMode) "ACTIVE: MONOCHROME OLED STATS" else "INACTIVE: STANDARD COLOR ENGINE",
                            color = secTextColor,
                            fontSize = 9.sp
                        )
                    }
                }

                // Simulated Morse flashing controller
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                        .clickable { viewModel.transmitMeshMessage("DISASTER EMERGENCY BEACON. LAT: 37.77 N, LON: -122.41 W. REQUIRE IMMEDIATE MEDEVAC FEEDBACK.", isEmergency = true) }
                        .padding(14.dp)
                        .testTag("morse_flash_btn")
                ) {
                    Column {
                        Icon(
                            Icons.Default.WifiTetheringError,
                            "Radio beacon",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "TRANSMIT HIGH FREQ",
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MESH EMERGENCY BROADCAST",
                            color = secTextColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // Live Rescue Instructions checklist
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "CRITICAL RESCUE SIGNAL INSTRUCTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Protocols instructions
                InstructionBullet(accentColor, "Signal Fires", "Light three fires in a perfect triangle coordinates, spaced exactly 100 feet apart, the standard international signaling vector.")
                InstructionBullet(accentColor, "Mirror Flash (Heliograph)", "Sweep sunlight flashes along the horizon sweep lines. Three short continuous pulses represents SOS.")
                InstructionBullet(accentColor, "Beacon Broadcasts", "Secure battery life parameters, Vana AI continues sending localized sub-giga broadcasts offline.")
            }
        }
    }
}

// 6. SURVIVAL KNOWLEDGE ENGINE & GUIDE ARCHIVES
@Composable
fun SurvivalGuidesScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val searchVal by viewModel.searchQuery.collectAsState()
    val guides by viewModel.filteredGuides.collectAsState()
    val checklistItems by viewModel.activeChecklist.collectAsState()
    val checkCategory by viewModel.selectedChecklistCategory.collectAsState()

    var activeTab by remember { mutableStateOf("GUIDES") } // "GUIDES" or "CHECKLIST"
    var expandedGuideId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Switcher header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            TabCapsule(
                modifier = Modifier.weight(1f),
                title = "KNOWLEDGE BANK",
                isSelected = activeTab == "GUIDES",
                icon = Icons.Default.MenuBook,
                activeColor = primaryColor,
                textColor = textColor
            ) { activeTab = "GUIDES" }

            TabCapsule(
                modifier = Modifier.weight(1f),
                title = "EQUIPMENT PREP",
                isSelected = activeTab == "CHECKLIST",
                icon = Icons.Default.FactCheck,
                activeColor = secondaryColor,
                textColor = textColor
            ) { activeTab = "CHECKLIST" }
        }

        if (activeTab == "GUIDES") {
            // Search Input Row
            TextField(
                value = searchVal,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, primaryColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .testTag("guide_search_box"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = primaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Filter survival guidelines (bleeds, Solar distillation, starchas)...", color = secTextColor, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = primaryColor) }
            )

            // Guidelines scroll list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(guides) { guide ->
                    SurvivalGuideItemRow(
                        guide = guide,
                        isExpanded = expandedGuideId == guide.id,
                        onRowClicked = {
                            expandedGuideId = if (expandedGuideId == guide.id) null else guide.id
                        },
                        primaryColor = primaryColor,
                        cardBg = cardBg,
                        textColor = textColor,
                        secTextColor = secTextColor
                    )
                }
            }
        } else {
            // Checklists selector Category buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBtn(modifier = Modifier.weight(1f), title = "BAG", isSelected = checkCategory == "BUG_OUT_BAG", activeColor = secondaryColor, cardBg = cardBg, textColor = textColor) {
                    viewModel.setChecklistCategory("BUG_OUT_BAG")
                }
                CategoryBtn(modifier = Modifier.weight(1f), title = "FAK", isSelected = checkCategory == "FIRST_AID_KIT", activeColor = secondaryColor, cardBg = cardBg, textColor = textColor) {
                    viewModel.setChecklistCategory("FIRST_AID_KIT")
                }
                CategoryBtn(modifier = Modifier.weight(1f), title = "SHELTER", isSelected = checkCategory == "SHELTER_PREP", activeColor = secondaryColor, cardBg = cardBg, textColor = textColor) {
                    viewModel.setChecklistCategory("SRE_PREP") // Correct state mapping
                    viewModel.setChecklistCategory("SHELTER_PREP")
                }
            }

            // Real-time complete list items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(checklistItems) { item ->
                    RcheckItemRow(
                        item = item,
                        primaryColor = secondaryColor,
                        cardBg = cardBg,
                        textColor = textColor,
                        onToggle = { viewModel.toggleChecklistItem(item) }
                    )
                }
            }
        }
    }
}

// Sub-component: Survival Accordion detail item
@Composable
fun SurvivalGuideItemRow(
    guide: SurvivalGuide,
    isExpanded: Boolean,
    onRowClicked: () -> Unit,
    primaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .clickable { onRowClicked() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guide.category,
                    color = primaryColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = guide.title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Expand Detail",
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Expanded text block containing high quality markdown instructions
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Divider(color = primaryColor.copy(alpha = 0.12f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = guide.content,
                    color = textColor.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

// Sub-component: Telemetry dashboard card
@Composable
fun TelemetryCard(
    modifier: Modifier,
    title: String,
    metricValue: String,
    subMark: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, accentColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = secTextColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Icon(icon, null, tint = accentColor.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(metricValue, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(subMark, color = secTextColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

// Environmental parameters Card component
@Composable
fun QuickSensCard(
    modifier: Modifier,
    title: String,
    value: String,
    status: String,
    primaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = secTextColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Text(status, color = primaryColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

// Selector Tabs inside Glass card template
@Composable
fun TabCapsule(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) DeepForestBlack else activeColor,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isSelected) DeepForestBlack else activeColor
            )
        }
    }
}

// Sub-component Category Button
@Composable
fun CategoryBtn(
    modifier: Modifier,
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    cardBg: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor else cardBg)
            .border(1.dp, if (isSelected) Color.Transparent else activeColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) DeepForestBlack else textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

// Checklist grid row item
@Composable
fun RcheckItemRow(
    item: SurvivalChecklistItem,
    primaryColor: Color,
    cardBg: Color,
    textColor: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (item.isCompleted) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = "Toggle Complete",
                tint = if (item.isCompleted) primaryColor else textColor.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )

            Column {
                Text(
                    text = item.title,
                    color = if (item.isCompleted) textColor.copy(alpha = 0.4f) else textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(item.quantity, color = primaryColor.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

// Mesh communication packet display bubble representation
@Composable
fun MeshMessageCapsule(
    msg: MeshMessage,
    primaryColor: Color,
    secondaryColor: Color
) {
    val isUser = msg.sender == "USER" || msg.sender == "SND-SECURE"
    val isSys = msg.sender == "SYSTEM" || msg.sender == "VANA SYSTEM"
    val accent = if (msg.isEmergency) SurvivalOrange else if (isSys) primaryColor else secondaryColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // Meta details header
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg.sender,
                    color = accent,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "SIG: ${msg.signalStrength}%",
                    color = DarkGreyText,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (msg.hops > 0) {
                    Text(
                        text = "HOPS: ${msg.hops}",
                        color = DarkGreyText,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Text Bubble Container
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .background(if (isUser) accent.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.4f))
                    .border(
                        width = 1.dp,
                        color = accent.copy(alpha = if (isUser) 0.4f else 0.2f),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = msg.content,
                    color = OnDarkText,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

// Live Radar HUD Canvas helper
@Composable
fun NeuralRadarWidget(
    bearing: Float,
    primaryColor: Color,
    secondaryColor: Color,
    textColor: Color
) {
    // Rotating bearing text animation trigger with fluid Apple-like spring physical interpolation
    val animateAngle by animateFloatAsState(
        targetValue = bearing,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TargetCompassAngle"
    )

    Box(
        modifier = Modifier
            .size(260.dp)
            .padding(12.dp)
            .testTag("vana_minimalist_compass"),
        contentAlignment = Alignment.Center
    ) {
        // High fidelity compass dial Canvas drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.width / 2.3f

            // 1. Draw a thin, elegant outer ring matching Apple Weather aesthetics
            drawCircle(
                color = primaryColor.copy(alpha = 0.25f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 0.8.dp.toPx())
            )

            // 2. Draw outer ticks at every 10 degrees, with cardinal ticks more pronounced
            for (degree in 0 until 360 step 10) {
                val rad = Math.toRadians(degree.toDouble() - 90.0) // align standard compass direction
                val cos = cos(rad).toFloat()
                val sin = sin(rad).toFloat()
                
                val isCardinal = degree % 90 == 0
                val isMajor = degree % 30 == 0
                val startLen = radius * (if (isCardinal) 0.88f else if (isMajor) 0.92f else 0.95f)
                val endLen = radius * 0.98f

                drawLine(
                    color = if (isCardinal) primaryColor.copy(alpha = 0.8f) else primaryColor.copy(alpha = 0.25f),
                    start = Offset(cx + cos * startLen, cy + sin * startLen),
                    end = Offset(cx + cos * endLen, cy + sin * endLen),
                    strokeWidth = if (isCardinal) 1.5.dp.toPx() else 0.8.dp.toPx()
                )
            }

            // 3. Draw rotating elements: compass cardinal directional marks (N,S,E,W)
            rotate(-animateAngle, Offset(cx, cy)) {
                // North indicator - double red marker for high visibility (pointing up)
                val northRad = Math.toRadians(270.0)
                val nCos = cos(northRad).toFloat()
                val nSin = sin(northRad).toFloat()
                
                // Draw elegant, simple needle pointing north
                drawLine(
                    color = AppleRed,
                    start = Offset(cx + nCos * (radius * 0.45f), cy + nSin * (radius * 0.45f)),
                    end = Offset(cx + nCos * (radius * 0.86f), cy + nSin * (radius * 0.86f)),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // South indicator - simple grey line
                val southRad = Math.toRadians(90.0)
                val sCos = cos(southRad).toFloat()
                val sSin = sin(southRad).toFloat()
                drawLine(
                    color = primaryColor.copy(alpha = 0.35f),
                    start = Offset(cx + sCos * (radius * 0.3f), cy + sSin * (radius * 0.3f)),
                    end = Offset(cx + sCos * (radius * 0.86f), cy + sSin * (radius * 0.86f)),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Concentric digital state display overlaid natively inside
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(textColor.copy(alpha = 0.04f))
                .border(0.8.dp, primaryColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val facingLetter = when (bearing.toInt()) {
                    in 0..22 -> "N"
                    in 23..67 -> "NE"
                    in 68..112 -> "E"
                    in 113..157 -> "SE"
                    in 158..202 -> "S"
                    in 203..247 -> "SW"
                    in 248..292 -> "W"
                    in 293..337 -> "NW"
                    else -> "N"
                }
                
                Text(
                    text = facingLetter,
                    color = primaryColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = String.format("%03d°", bearing.toInt()),
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "STABLE BEARING",
                    color = primaryColor.copy(alpha = 0.5f),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// Topographical view landscape drawing used in camera / land overview simulation
@Composable
fun TopographicalTerrainModel(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val h = size.height
        val w = size.width

        // Layer 1
        path.moveTo(0f, h * 0.4f)
        path.cubicTo(w * 0.25f, h * 0.35f, w * 0.5f, h * 0.5f, w * 0.75f, h * 0.42f)
        path.lineTo(w, h * 0.55f)
        path.lineTo(w, h)
        path.lineTo(0f, h)
        path.close()
        drawPath(path, color.copy(alpha = 0.08f))

        // Layer 2
        val path2 = Path()
        path2.moveTo(0f, h * 0.55f)
        path2.cubicTo(w * 0.3f, h * 0.65f, w * 0.65f, h * 0.48f, w * 0.8f, h * 0.6f)
        path2.lineTo(w, h * 0.7f)
        path2.lineTo(w, h)
        path2.lineTo(0f, h)
        path2.close()
        drawPath(path2, color.copy(alpha = 0.12f))

        // Grid levels contours lines drawn as vector overlays
        for (i in 1..4) {
            drawLine(
                color = color.copy(alpha = 0.15f),
                start = Offset(0f, h * 0.2f * i),
                end = Offset(w, h * 0.2f * i),
                strokeWidth = 1f
            )
        }
    }
}

// Bounding box target tracker rendered directly in coordinate coordinates
@Composable
fun TargetOverlayIndicator(
    scan: ScanTarget,
    primaryColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "LabelBlink")
        val flashAlpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ScanFlicker"
        )

        Box(
            modifier = Modifier
                .offset(y = (scan.yOffset * 340).dp, x = (scan.xOffset * 220).dp)
                .wrapContentSize()
                .border(1.5.dp, primaryColor)
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(6.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.alpha(flashAlpha)
                ) {
                    Box(modifier = Modifier.size(6.dp).background(primaryColor, CircleShape))
                    Text(scan.label, color = textColor, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                Text("CONF: ${scan.confidence}%", color = primaryColor, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

// Custom Offline Maps topographic path renderer
@Composable
fun TopographicalMapCanvas(
    lat: Double,
    lon: Double,
    bearing: Float,
    waypoints: List<Waypoint>,
    selectedWp: Waypoint?,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    primaryColor: Color,
    secondaryColor: Color,
    onScroll: (Float, Float) -> Unit,
    onWaypointSelected: (Waypoint) -> Unit,
    onMapDoubleTapped: (Double, Double) -> Unit
) {
    // We render topographic curves procedurally on standard Android canvas!
    // This allows responsive operations without external bulky dependencies.
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onScroll(dragAmount.x, dragAmount.y)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cx = size.width / 2f + offsetX
                    val cy = size.height / 2f + offsetY
                    
                    var tappedExisting = false
                    waypoints.forEach { wp ->
                        val wpX = cx + (wp.longitude - lon).toFloat() * 25000f * scale
                        val wpY = cy - (wp.latitude - lat).toFloat() * 32000f * scale
                        
                        val dist = sqrt((offset.x - wpX).pow(2) + (offset.y - wpY).pow(2))
                        if (dist < 32.dp.toPx()) {
                            onWaypointSelected(wp)
                            tappedExisting = true
                        }
                    }
                    if (!tappedExisting) {
                        val tappedLon = lon + (offset.x - cx) / (25000f * scale)
                        val tappedLat = lat - (offset.y - cy) / (32000f * scale)
                        onMapDoubleTapped(tappedLat, tappedLon)
                    }
                }
            }
    ) {
        val cx = size.width / 2f + offsetX
        val cy = size.height / 2f + offsetY

        // 1. Draw procedural topo elevation rings centered on coordinates
        for (i in 1..8) {
            drawCircle(
                color = secondaryColor.copy(alpha = 0.05f),
                radius = 42.dp.toPx() * i * scale,
                center = Offset(cx, cy),
                style = Stroke(width = 1f)
            )
        }

        // Draw Map Grid coordinate crosslines
        for (gridX in 0..size.width.toInt() step 90) {
            drawLine(secondaryColor.copy(alpha = 0.08f), Offset(gridX.toFloat(), 0f), Offset(gridX.toFloat(), size.height), 1f)
        }
        for (gridY in 0..size.height.toInt() step 90) {
            drawLine(secondaryColor.copy(alpha = 0.08f), Offset(0f, gridY.toFloat()), Offset(size.width, gridY.toFloat()), 1f)
        }

        // 2. Draw Survival safe-house Waypoints on map
        waypoints.forEach { wp ->
            // Map coordinates project factor offset mapping (centered default on user coordinates)
            val wpX = cx + (wp.longitude - lon).toFloat() * 25000f * scale
            val wpY = cy - (wp.latitude - lat).toFloat() * 32000f * scale

            val isSelectedOnMap = selectedWp?.id == wp.id

            // Draw bounding halo glow lines
            drawCircle(
                color = if (wp.type == "WATER") secondaryColor.copy(alpha = 0.2f) else primaryColor.copy(alpha = 0.2f),
                radius = if (isSelectedOnMap) 16.dp.toPx() else 10.dp.toPx(),
                center = Offset(wpX, wpY)
            )

            // Center target node dot
            drawCircle(
                color = if (wp.type == "WATER") secondaryColor else primaryColor,
                radius = 6.dp.toPx(),
                center = Offset(wpX, wpY)
            )

            // Connect lines between selected WP and active user locator
            if (isSelectedOnMap) {
                drawLine(
                    color = secondaryColor,
                    start = Offset(cx, cy),
                    end = Offset(wpX, wpY),
                    strokeWidth = 2f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            }
        }

        // 3. Draw Active User central locator node
        drawCircle(
            color = Color.Black,
            radius = 11.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = secondaryColor,
            radius = 9.dp.toPx(),
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = Offset(cx, cy)
        )

        // 4. Draw rotating directional heading arrow aligned with standard physical bearing
        rotate(bearing, Offset(cx, cy)) {
            val arrowPath = Path().apply {
                moveTo(cx, cy - 18.dp.toPx())
                lineTo(cx - 7.dp.toPx(), cy - 8.dp.toPx())
                lineTo(cx, cy - 11.dp.toPx())
                lineTo(cx + 7.dp.toPx(), cy - 8.dp.toPx())
                close()
            }
            drawPath(
                path = arrowPath,
                color = secondaryColor.copy(alpha = 0.4f)
            )
            drawPath(
                path = arrowPath,
                color = secondaryColor,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

// Circular Morphed Bottom Glass Dock
@Composable
fun MorphicDockMenu(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    primaryColor: Color,
    cardBg: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(cardBg)
            .border(1.5.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab 1: Home
        DockNavItem(
            title = "Home",
            icon = Icons.Default.Home,
            isSelected = currentScreen == "DASHBOARD",
            activeColor = primaryColor,
            textColor = textColor,
            onClick = { onScreenSelected("DASHBOARD") }
        )

        // Tab 2: Explore
        DockNavItem(
            title = "Explore",
            icon = Icons.Default.Explore,
            isSelected = currentScreen == "EXPLORE" || currentScreen == "AI_HUB" || currentScreen == "SCANNER" || currentScreen == "MAPS" || currentScreen == "NEARBY" || currentScreen == "GUIDES" || currentScreen == "COMMS" || currentScreen == "EMERGENCY",
            activeColor = primaryColor,
            textColor = textColor,
            onClick = { onScreenSelected("EXPLORE") }
        )

        // Tab 3: Pulse Pulse (Center active node)
        DockNavItem(
            title = "Pulse",
            icon = Icons.Default.Favorite,
            isSelected = currentScreen == "FITNESS_HEALTH",
            activeColor = primaryColor,
            textColor = textColor,
            onClick = { onScreenSelected("FITNESS_HEALTH") }
        )

        // Tab 4: Logs
        DockNavItem(
            title = "History",
            icon = Icons.Default.History,
            isSelected = currentScreen == "HISTORY",
            activeColor = primaryColor,
            textColor = textColor,
            onClick = { onScreenSelected("HISTORY") }
        )

        // Tab 5: Profile
        DockNavItem(
            title = "Profile",
            icon = Icons.Default.Person,
            isSelected = currentScreen == "PROFILE" || currentScreen == "SETTINGS" || currentScreen == "THEME" || currentScreen == "BATTERY_INFO",
            activeColor = primaryColor,
            textColor = textColor,
            onClick = { onScreenSelected("PROFILE") }
        )
    }
}

// Nav items containing custom selection animations and tags
@Composable
fun DockNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val sizeScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "DockSizeScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .pointerInput(Unit) { detectTapGestures { onClick() } }
            .padding(horizontal = 4.dp)
            .scale(sizeScale)
            .testTag("dock_nav_$title")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) activeColor else textColor.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) activeColor else textColor.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun InstructionBullet(color: Color, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("-", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Column {
            Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(desc, color = OnDarkText.copy(alpha = 0.8f), fontSize = 10.sp, lineHeight = 14.sp)
        }
    }
}
