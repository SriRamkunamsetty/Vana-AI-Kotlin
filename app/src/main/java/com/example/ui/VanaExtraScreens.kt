package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*

// -------------------------------------------------------------
// SCREEN 1: EXPLORE HUB SCREEN
// -------------------------------------------------------------
@Composable
fun ExploreHubScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "VANA EXPLORE HUB",
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.5.sp,
            color = textColor
        )
        Text(
            text = "Offline Tactical Assistance",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = primaryColor
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ExploreActionCard(
            title = "OLLAMA AI INTELLIGENCE",
            desc = "Smarter responses via Gemini-2B locally optimized model.",
            icon = Icons.Default.Psychology,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("AI_HUB") }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "SMART CAMERA VISION",
            desc = "Augmented reality scanner, coordinate and distance matrix overlay.",
            icon = Icons.Default.Camera,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("SCANNER") }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "OFFLINE TOPOGRAPHICAL MAPS",
            desc = "Terrain elevation matrix, procedural mapping, custom waypoint logging.",
            icon = Icons.Default.Map,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("MAPS") }

        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "PEER MESH COMMUNICATION",
            desc = "Secure encrypted wireless intercom team chats.",
            icon = Icons.Default.CellTower,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("COMMS") }

        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "NEARBY SIGNALS SCANNER",
            desc = "Locate nearby Bluetooth, Wi-Fi mesh transceivers & local beacons.",
            icon = Icons.Default.CompassCalibration,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("NEARBY") }

        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "SURVIVAL LIBRARY INDEX",
            desc = "Fire starting, water purification, first-aid procedures.",
            icon = Icons.Default.MenuBook,
            accentColor = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("GUIDES") }

        Spacer(modifier = Modifier.height(12.dp))
        
        ExploreActionCard(
            title = "EMERGENCY BEACON SOS",
            desc = "Transmit emergency alerts, sound siren & toggle torch strobe.",
            icon = Icons.Default.Emergency,
            accentColor = AppleRed,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        ) { viewModel.setScreen("EMERGENCY") }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ExploreActionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, accentColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = secTextColor,
                lineHeight = 14.sp
            )
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = accentColor.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// -------------------------------------------------------------
// SCREEN 2: FITNESS & HEALTH HUB SCREEN
// -------------------------------------------------------------
@Composable
fun FitnessHealthHubScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    var subTab by remember { mutableStateOf("FITNESS") }
    val stepsRaw by viewModel.steps.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val stressLevel by viewModel.stressLevel.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val hydrationLevel by viewModel.hydrationLevel.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (subTab == "FITNESS") primaryColor.copy(alpha = 0.12f) else Color.Transparent)
                    .clickable { subTab = "FITNESS" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FITNESS TRACKER",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == "FITNESS") primaryColor else textColor.copy(alpha = 0.6f)
                )
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (subTab == "HEALTH") primaryColor.copy(alpha = 0.12f) else Color.Transparent)
                    .clickable { subTab = "HEALTH" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BIO SCI DIAGNOSTICS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == "HEALTH") primaryColor else textColor.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        if (subTab == "FITNESS") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var periodTime by remember { mutableStateOf("DAY") }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DAY", "WEEK", "MONTH").forEach { period ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (periodTime == period) primaryColor.copy(alpha = 0.2f) else cardBg)
                                .border(1.dp, primaryColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                .clickable { periodTime = period }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (periodTime == period) primaryColor else secTextColor
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val stepsGoal = 10000
                val progressFraction = (stepsRaw.toFloat() / stepsGoal).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = tween(1000, easing = LinearOutSlowInEasing),
                    label = "StepsProgress"
                )
                
                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.08f),
                            radius = size.width / 2.3f,
                            style = Stroke(width = 11.dp.toPx())
                        )
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("%,d", stepsRaw),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = textColor
                        )
                        Text(
                            text = "of 10,000 steps",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = secTextColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val computedDist = stepsRaw * 0.00075f
                val computedKcal = stepsRaw * 0.045f
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "DISTANCE TRAVELED",
                        value = String.format("%.2f km", computedDist),
                        icon = Icons.Default.Map,
                        tint = primaryColor,
                        cardBg = cardBg
                    )
                    CompactMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "CALORIES EXPANDED",
                        value = String.format("%.0f kcal", computedKcal),
                        icon = Icons.Default.FlashOn,
                        tint = primaryColor,
                        cardBg = cardBg
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "ACTIVE EXERTION",
                        value = "58 mins",
                        icon = Icons.Default.Speed,
                        tint = primaryColor,
                        cardBg = cardBg
                    )
                    CompactMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "ENDURANCE INDEX",
                        value = "94%",
                        icon = Icons.Default.FitnessCenter,
                        tint = primaryColor,
                        cardBg = cardBg
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LiveSparklineCard(
                    title = "HEART RATE MONITOR",
                    value = "$heartRate BPM",
                    status = "Standard (Nominal)",
                    sparklinePoints = listOf(66f, 72f, 68f, 75f, 71f, 73f, 70f, 74f, heartRate.toFloat()),
                    color = AppleRed,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
                
                LiveSparklineCard(
                    title = "NEURAL STRESS LEVEL",
                    value = stressLevel.uppercase(),
                    status = "Normal Resilient Status",
                    sparklinePoints = listOf(35f, 40f, 25f, 30f, 28f, 32f, 29f, 31f, 27f),
                    color = Color.Green,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
                
                LiveSparklineCard(
                    title = "CIRCADIAN SLEEP INDEX",
                    value = sleepHours,
                    status = "Deep Cycle Rested Index",
                    sparklinePoints = listOf(20f, 10f, 40f, 80f, 60f, 90f, 70f, 85f, 82f),
                    color = Color.Cyan,
                    cardBg = cardBg,
                    textColor = textColor,
                    secTextColor = secTextColor
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BIO-CELLULAR HYDRATION",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format("%.2f L / 3.00 L", hydrationLevel),
                                fontSize = 18.sp,
                                color = textColor,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.08f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = (hydrationLevel / 3.0f).coerceIn(0f, 1f))
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.addHydration(0.25f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Drink 250ml", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        
                        OutlinedButton(
                            onClick = { viewModel.resetHydration() },
                            modifier = Modifier.weight(0.5f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CompactMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    cardBg: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, tint.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = tint.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Light, color = SoftWhiteText)
    }
}

@Composable
fun LiveSparklineCard(
    title: String,
    value: String,
    status: String,
    sparklinePoints: List<Float>,
    color: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                color = textColor,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = status,
                fontSize = 10.sp,
                color = secTextColor
            )
        }
        
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .padding(horizontal = 4.dp)
        ) {
            val width = size.width
            val height = size.height
            if (sparklinePoints.size > 1) {
                val path = Path()
                val stepX = width / (sparklinePoints.size - 1)
                val minY = sparklinePoints.minOrNull() ?: 0f
                val maxY = sparklinePoints.maxOrNull() ?: 100f
                val deltaY = (maxY - minY).coerceAtLeast(1f)
                
                sparklinePoints.forEachIndexed { index, yVal ->
                    val x = index * stepX
                    val normalizedY = height - ((yVal - minY) / deltaY) * (height * 0.8f) - (height * 0.1f)
                    if (index == 0) {
                        path.moveTo(x, normalizedY)
                    } else {
                        val prevX = (index - 1) * stepX
                        val prevNormalizedY = height - ((sparklinePoints[index - 1] - minY) / deltaY) * (height * 0.8f) - (height * 0.1f)
                        path.cubicTo(
                            (prevX + x) / 2f, prevNormalizedY,
                            (prevX + x) / 2f, normalizedY,
                            x, normalizedY
                        )
                    }
                }
                
                drawPath(
                    path = path,
                    color = color.copy(alpha = 0.7f),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )
                
                val finalX = width
                val finalNormalizedY = height - ((sparklinePoints.last() - minY) / deltaY) * (height * 0.8f) - (height * 0.1f)
                drawCircle(
                    color = color,
                    radius = 3.dp.toPx(),
                    center = Offset(finalX, finalNormalizedY)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 3: ACTIVITY HISTORY LOGS SCREEN
// -------------------------------------------------------------
@Composable
fun HistoryScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "SAVED TRAVELS ACTIVITY LOG",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 1.2.sp,
            color = textColor
        )
        Text(
            text = "Cached Local Path Archives",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = primaryColor
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "TODAY")
        Spacer(modifier = Modifier.height(8.dp))
        
        HistoryRowCard(
            title = "Pine Forest Trail Expedition",
            desc = "Distance: 8.6 km • Duration: 2h 15m • Ascent: +124m",
            icon = Icons.Default.DirectionsRun,
            time = "10:30 AM",
            tint = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        HistoryRowCard(
            title = "Water Spring Checkpoint Safe",
            desc = "Location: Lat 37.7792, Lon -122.4214 • Safe Drinking Quality Checked",
            icon = Icons.Default.Opacity,
            time = "9:45 AM",
            tint = Color(0xFF64B5F6),
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "YESTERDAY")
        Spacer(modifier = Modifier.height(8.dp))
        
        HistoryRowCard(
            title = "Mountain Ridge Climb",
            desc = "Distance: 12.3 km • Duration: 4h 10m • Elevation peaked: 1540m",
            icon = Icons.Default.Map,
            time = "Yesterday",
            tint = primaryColor,
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        HistoryRowCard(
            title = "Survival Field Guide Reference",
            desc = "Read 'Fire Starting Basics' & 'Water Filter Siphoning Procedures' offline chapters.",
            icon = Icons.Default.MenuBook,
            time = "Yesterday",
            tint = Color(0xFFFFB74D),
            cardBg = cardBg,
            textColor = textColor,
            secTextColor = secTextColor
        )
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = SoftWhiteText.copy(alpha = 0.45f)
    )
}

@Composable
fun HistoryRowCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    time: String,
    tint: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, tint.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = time,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    color = secTextColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = secTextColor,
                lineHeight = 14.sp
            )
        }
    }
}

// -------------------------------------------------------------
// SCREEN 4: USER PROFILE SCREEN
// -------------------------------------------------------------
@Composable
fun ProfileScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.15f))
                    .border(1.5.dp, primaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    color = primaryColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Arjun",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = textColor
                )
                Text(
                    text = "Vana Field Explorer • Level 12",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStatItem(number = "24", label = "TRIPS COMPLETED", tint = primaryColor)
            ProfileStatVerticalDivider(color = primaryColor)
            ProfileStatItem(number = "312", label = "TOTAL KM", tint = primaryColor)
            ProfileStatVerticalDivider(color = primaryColor)
            ProfileStatItem(number = "8", label = "BADGES ACTIVE", tint = primaryColor)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "EARNED ACHIEVEMENTS & FIELD BADGES")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BadgeCapsule(modifier = Modifier.weight(1f), title = "First Responder", desc = "SOS Active OK", icon = Icons.Default.VerifiedUser, tint = AppleRed, cardBg = cardBg)
            BadgeCapsule(modifier = Modifier.weight(1f), title = "Peak Explorer", desc = "Hiked +1500m", icon = Icons.Default.Map, tint = Color(0xFFFFB74D), cardBg = cardBg)
            BadgeCapsule(modifier = Modifier.weight(1f), title = "Mesh Expert", desc = "Created Local Mesh", icon = Icons.Default.CellTower, tint = Color(0xFF81C784), cardBg = cardBg)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "VANA OFF-GRID HARDWARE ACCESS")
        Spacer(modifier = Modifier.height(10.dp))
        
        ProfileOptionRow(
            title = "Tactical System Settings",
            desc = "Hardware sensors alignment, calibration & system tuning.",
            icon = Icons.Default.Settings,
            tint = primaryColor,
            cardBg = cardBg,
            textColor = textColor
        ) { viewModel.setScreen("SETTINGS") }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        ProfileOptionRow(
            title = "Color Themes & Appearance",
            desc = "Swap accents color highlights, light/dark modes & typographic scaling.",
            icon = Icons.Default.Palette,
            tint = primaryColor,
            cardBg = cardBg,
            textColor = textColor
        ) { viewModel.setScreen("THEME") }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileStatItem(number: String, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontSize = 20.sp, fontWeight = FontWeight.Light, color = SoftWhiteText)
        Spacer(modifier = Modifier.height(1.dp))
        Text(text = label, fontSize = 7.2.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = tint.copy(alpha = 0.7f))
    }
}

@Composable
fun ProfileStatVerticalDivider(color: Color) {
    Divider(
        color = color.copy(alpha = 0.15f),
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
    )
}

@Composable
fun BadgeCapsule(
    modifier: Modifier,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    cardBg: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, tint.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SoftWhiteText, textAlign = TextAlign.Center)
        Text(text = desc, fontSize = 7.sp, color = OnDarkText.copy(alpha = 0.6f), textAlign = TextAlign.Center, lineHeight = 9.sp)
    }
}

@Composable
fun ProfileOptionRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    cardBg: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, tint.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = desc, fontSize = 10.sp, color = OnDarkText.copy(alpha = 0.6f), lineHeight = 14.sp)
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = tint.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

// -------------------------------------------------------------
// SCREEN 5: OFFLINE PEER MESH COMMUNICATION SCREEN
// -------------------------------------------------------------
@Composable
fun CommunicationScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    var chatTab by remember { mutableStateOf("CHATS") }
    var activeThread by remember { mutableStateOf<String?>(null) }
    var userMessageText by remember { mutableStateOf("") }
    
    val defaultThreads = listOf(
        "Team Alpha" to "Mesh network solid. Approaching river waypoint in 10 mins.",
        "Hiking Group" to "Lunch coordinates pinned near safe camp 4.",
        "Nearby Signal (VANA-1258)" to "P2P proximity handcheck OK. Siphon ready.",
        "Survival Broadcast" to "WEATHER WARNING: Severe pressure drop detected. Secure camp."
    )
    
    val threadMessages = remember {
        mutableStateMapOf<String, List<Pair<String, String>>>(
            "Team Alpha" to listOf(
                "Arjun" to "Are we hitting coordinates Alpha?",
                "Team Alpha" to "Yes, mesh network solid. Approaching river waypoint in 10 mins."
            ),
            "Hiking Group" to listOf(
                "Hiking Group" to "Lunch coordinates pinned near safe camp 4."
            ),
            "Nearby Signal (VANA-1258)" to listOf(
                "Nearby Signal (VANA-1258)" to "P2P proximity handcheck OK. Siphon ready."
            ),
            "Survival Broadcast" to listOf(
                "Survival Broadcast" to "WEATHER WARNING: Severe pressure drop detected. Secure camp."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        if (activeThread == null) {
            Text(
                text = "VANA PEER intercom MESSAGING",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.2.sp,
                color = textColor
            )
            Text(
                text = "Off-grid Local Encrypted Radio Channels",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = primaryColor
            )
            
            Spacer(modifier = Modifier.height(18.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (chatTab == "CHATS") primaryColor.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable { chatTab = "CHATS" }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DIRECT CHANNELS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (chatTab == "CHATS") primaryColor else textColor.copy(alpha = 0.6f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (chatTab == "BROADCAST") primaryColor.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable { chatTab = "BROADCAST" }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BROADCAST NETWORKS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (chatTab == "BROADCAST") primaryColor else textColor.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Search channel or team ID...", fontSize = 10.sp, color = secTextColor) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = primaryColor, modifier = Modifier.size(16.dp)) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = primaryColor.copy(alpha = 0.15f),
                    focusedBorderColor = primaryColor,
                    unfocusedContainerColor = cardBg,
                    focusedContainerColor = cardBg
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                defaultThreads.forEach { (name, lastMessage) ->
                    val isBroadcastType = name.contains("Broadcast")
                    if ((chatTab == "CHATS" && !isBroadcastType) || (chatTab == "BROADCAST" && isBroadcastType)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(cardBg)
                                .border(1.dp, primaryColor.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                                .clickable { activeThread = name }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isBroadcastType) Icons.Default.Radio else Icons.Default.Group,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = "9:30 AM",
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = secTextColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lastMessage,
                                    fontSize = 10.sp,
                                    color = secTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = { activeThread = "Team Alpha" },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = CircleShape,
                    modifier = Modifier.size(width = 130.dp, height = 48.dp)
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Chat", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        } else {
            val threadName = activeThread!!
            val messages = threadMessages[threadName] ?: emptyList()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeThread = null }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = threadName.uppercase(), fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textColor)
                    Text(text = "Encrypted Mesh Channel Active", fontSize = 8.sp, color = primaryColor, fontFamily = FontFamily.Monospace)
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                messages.forEach { (sender, body) ->
                    val isUser = sender == "Arjun"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Text(
                            text = sender,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = primaryColor,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 14.dp
                                    )
                                )
                                .background(if (isUser) primaryColor.copy(alpha = 0.2f) else cardBg)
                                .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Text(text = body, fontSize = 11.sp, color = textColor, lineHeight = 15.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userMessageText,
                    onValueChange = { userMessageText = it },
                    placeholder = { Text("Write encrypted packet...", fontSize = 10.sp, color = secTextColor) },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = primaryColor.copy(alpha = 0.2f),
                        focusedBorderColor = primaryColor,
                        unfocusedContainerColor = cardBg,
                        focusedContainerColor = cardBg
                    )
                )
                
                IconButton(
                    onClick = {
                        if (userMessageText.isNotBlank()) {
                            val newList = messages.toMutableList()
                            newList.add("Arjun" to userMessageText)
                            threadMessages[threadName] = newList
                            userMessageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -------------------------------------------------------------
// SCREEN 6: PEER SIGNAL SCANNING RADAR SCREEN
// -------------------------------------------------------------
@Composable
fun NearbyDevicesScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val radarSweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweep"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PEER SIGNAL DETECTION RADAR",
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 1.2.sp,
            color = textColor
        )
        Text(
            text = "Scanning BLE transceivers and mesh network nodes...",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = primaryColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val maxRadius = size.width / 2.1f
                
                drawCircle(color = primaryColor.copy(alpha = 0.1f), radius = maxRadius, style = Stroke(width = 0.8.dp.toPx()))
                drawCircle(color = primaryColor.copy(alpha = 0.08f), radius = maxRadius * 0.7f, style = Stroke(width = 0.8.dp.toPx()))
                drawCircle(color = primaryColor.copy(alpha = 0.06f), radius = maxRadius * 0.4f, style = Stroke(width = 0.8.dp.toPx()))
                
                drawLine(color = primaryColor.copy(alpha = 0.12f), start = Offset(cx - maxRadius, cy), end = Offset(cx + maxRadius, cy), strokeWidth = 0.8.dp.toPx())
                drawLine(color = primaryColor.copy(alpha = 0.12f), start = Offset(cx, cy - maxRadius), end = Offset(cx, cy + maxRadius), strokeWidth = 0.8.dp.toPx())
                
                rotate(radarSweepAngle, Offset(cx, cy)) {
                    val sweepPath = Path().apply {
                        moveTo(cx, cy)
                        val radEnd = Math.toRadians(25.0)
                        lineTo(cx + cos(0.0).toFloat() * maxRadius, cy + sin(0.0).toFloat() * maxRadius)
                        lineTo(cx + cos(radEnd).toFloat() * maxRadius, cy + sin(radEnd).toFloat() * maxRadius)
                        close()
                    }
                    drawPath(
                        path = sweepPath,
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = maxRadius
                        )
                    )
                }
                
                drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = Offset(cx - maxRadius * 0.4f, cy + maxRadius * 0.3f))
                drawCircle(color = AppleRed, radius = 4.dp.toPx(), center = Offset(cx + maxRadius * 0.62f, cy - maxRadius * 0.5f))
                drawCircle(color = Color.Green, radius = 4.dp.toPx(), center = Offset(cx + maxRadius * 0.25f, cy + maxRadius * 0.55f))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "SCAN ACTIVE • 4 TRANSCEIVERS DETECTED",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = primaryColor,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NearbyRowNode(name = "Team Alpha Lead Beacon (VANA-1258)", distance = "3.2 meters away", battery = "82%", rssi = "-43 dBm", tint = primaryColor, cardBg = cardBg)
            NearbyRowNode(name = "Basecamp Relay Station (B_RELAY-1)", distance = "7.8 meters away", battery = "99%", rssi = "-58 dBm", tint = Color.Green, cardBg = cardBg)
            NearbyRowNode(name = "Emergency Ground Beacon (SOS_P-8)", distance = "12.4 meters away", battery = "45%", rssi = "-76 dBm", tint = AppleRed, cardBg = cardBg)
            NearbyRowNode(name = "Discovered User Device (VANA-3345)", distance = "18.1 meters away", battery = "61%", rssi = "-89 dBm", tint = textColor, cardBg = cardBg)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun NearbyRowNode(
    name: String,
    distance: String,
    battery: String,
    rssi: String,
    tint: Color,
    cardBg: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, tint.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.CompassCalibration, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftWhiteText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = rssi, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = OnDarkText.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = distance, fontSize = 10.sp, color = OnDarkText.copy(alpha = 0.6f))
                Text(text = "Battery: $battery", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = OnDarkText.copy(alpha = 0.5f))
            }
        }
    }
}

// -------------------------------------------------------------
// SCREEN 7: TACTICAL GENERAL SETTINGS SCREEN
// -------------------------------------------------------------
@Composable
fun SettingsScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setScreen("PROFILE") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "TACTICAL SETTINGS", fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "Vana Intelligence Control Center center", fontSize = 10.sp, color = primaryColor, fontFamily = FontFamily.Monospace)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "TELEMETRY & HARDWARE CONTROLS")
        Spacer(modifier = Modifier.height(10.dp))
        
        ProfileOptionRow(title = "Battery & ECO Management", desc = "Thermal bounds, power savings, frame optimization.", icon = Icons.Default.Bolt, tint = primaryColor, cardBg = cardBg, textColor = textColor) {
            viewModel.setScreen("BATTERY_INFO")
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        ProfileOptionRow(title = "Topo Map Pre-Caching", desc = "Pre-load safehouse grids & topographical contours Offline.", icon = Icons.Default.Map, tint = primaryColor, cardBg = cardBg, textColor = textColor) {
            viewModel.setScreen("MAPS")
        }

        Spacer(modifier = Modifier.height(10.dp))
        
        ProfileOptionRow(title = "Local AI Copilot Assist", desc = "Model quantization configurations, prompt weights, cache indexing.", icon = Icons.Default.Psychology, tint = primaryColor, cardBg = cardBg, textColor = textColor) {
            viewModel.setScreen("AI_HUB")
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "DEVICE PERMISSIONS")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.VerifiedUser, null, tint = primaryColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Offline Core Permissions Configured", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "GPS Coordinates, Camera sensor, Activity steps logs approved.", fontSize = 9.sp, color = secTextColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "SYSTEM INFO")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Build Architecture Version", fontSize = 10.sp, color = SoftWhiteText.copy(alpha = 0.8f))
            Text(text = "VANA_OS v2.6-Android-Stable", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = primaryColor)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -------------------------------------------------------------
// SCREEN 8: VISUAL THEME & CUSTOM ACCENTS SCREEN
// -------------------------------------------------------------
@Composable
fun ThemeScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color
) {
    val selectedAccent by viewModel.accentColorState.collectAsState()
    val isDarkThemeActive by viewModel.darkMode.collectAsState()
    val selectedFontSize by viewModel.fontSizeState.collectAsState()
    val selectedAppIcon by viewModel.appIconState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setScreen("PROFILE") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "VISUAL APPEARANCE", fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "Configure Theme, Palette Highlights & Sizing", fontSize = 10.sp, color = primaryColor, fontFamily = FontFamily.Monospace)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "DEVICE THEME APPEARANCE")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isDarkThemeActive) primaryColor.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { if (isDarkThemeActive) viewModel.toggleTheme() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.LightMode, contentDescription = null, tint = if (!isDarkThemeActive) primaryColor else secTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "LIGHTMODE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = if (!isDarkThemeActive) primaryColor else textColor.copy(alpha = 0.6f))
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkThemeActive) primaryColor.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { if (!isDarkThemeActive) viewModel.toggleTheme() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.DarkMode, contentDescription = null, tint = if (isDarkThemeActive) primaryColor else secTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "DARK GRAPHITE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = if (isDarkThemeActive) primaryColor else textColor.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "PRIMARY BRAND ACCENT COGNITIVE TAG")
        Spacer(modifier = Modifier.height(10.dp))
        
        val colorsRaw = listOf(
            "BLUE" to IntelligentBlueAccent,
            "PURPLE" to Color(0xFFD0B3FF),
            "GREEN" to Color(0xFF81C784),
            "ORANGE" to Color(0xFFFFB74D),
            "RED" to Color(0xFFE57373),
            "YELLOW" to Color(0xFFFFF176)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            colorsRaw.forEach { (name, actualCol) ->
                val isSelected = selectedAccent == name
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(actualCol)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) textColor else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.setAccentColor(name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "FONT INTERACTION SIZING")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("SMALL", "MEDIUM", "LARGE").forEach { sizeLabel ->
                val isSizeSelected = selectedFontSize == sizeLabel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSizeSelected) primaryColor.copy(alpha = 0.2f) else cardBg)
                        .border(1.dp, primaryColor.copy(alpha = if (isSizeSelected) 0.5f else 0.1f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.setFontSize(sizeLabel) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sizeLabel,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSizeSelected) primaryColor else secTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        SectionLabel(text = "CUSTOM ADAPTIVE APP ICON SELECTOR")
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "DEFAULT" to "Blue Compass",
                "DARK" to "Carbon Mono",
                "TACTICAL" to "Strobe Orange"
            ).forEach { (iconValue, labelDesc) ->
                val isIconSelected = selectedAppIcon == iconValue
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isIconSelected) primaryColor.copy(alpha = 0.2f) else cardBg)
                        .border(1.dp, primaryColor.copy(alpha = if (isIconSelected) 0.5f else 0.1f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.setAppIcon(iconValue) }
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = if (isIconSelected) primaryColor else secTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = iconValue,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isIconSelected) primaryColor else textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = labelDesc,
                        fontSize = 7.2.sp,
                        color = secTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -------------------------------------------------------------
// SCREEN 9: BATTERY TELEMETRY & ECO POWER SWITCH SCREEN
// -------------------------------------------------------------
@Composable
fun BatteryInfoScreen(
    viewModel: SurvivalViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    cardBg: Color,
    textColor: Color,
    secTextColor: Color,
    ecoMode: Boolean
) {
    val batteryPct by viewModel.batteryLevel.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setScreen("PROFILE") }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "BATTERY TELEMETRY SCAN", fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = "Power state management and ECO Mode switch", fontSize = 10.sp, color = primaryColor, fontFamily = FontFamily.Monospace)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            val batColor = if (batteryPct > 40) Color.Green else if (batteryPct > 20) Color.Yellow else AppleRed
            val animatedBatteryPct by animateFloatAsState(
                targetValue = batteryPct.toFloat() / 100f,
                animationSpec = tween(800, easing = LinearOutSlowInEasing),
                label = "BatteryPctProgress"
            )
            
            Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = batColor.copy(alpha = 0.08f),
                        radius = size.width / 2.3f,
                        style = Stroke(width = 9.dp.toPx())
                    )
                    drawArc(
                        color = batColor,
                        startAngle = -90f,
                        sweepAngle = animatedBatteryPct * 360f,
                        useCenter = false,
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (batteryPct > 20) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = batColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$batteryPct%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = textColor
                    )
                    Text(
                        text = "Discharging (Good)",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = secTextColor
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .border(2.dp, if (ecoMode) primaryColor else Color.Transparent, RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (ecoMode) primaryColor.copy(alpha = 0.12f) else textColor.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (ecoMode) primaryColor else secTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "ECO POWER SAVING MODE",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (ecoMode) primaryColor else textColor
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = if (ecoMode) "ACTIVE: Heavy shaders and logs suspended for maximum power locks." else "INACTIVE: Full dynamic 120fps motion rendering active.",
                        fontSize = 9.sp,
                        color = secTextColor,
                        lineHeight = 13.sp
                    )
                }
            }
            
            Switch(
                checked = ecoMode,
                onCheckedChange = { viewModel.toggleEcoMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = primaryColor,
                    uncheckedThumbColor = secTextColor,
                    uncheckedTrackColor = cardBg
                ),
                modifier = Modifier.testTag("eco_mode_switch")
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionLabel(text = "BATTERY HARDWARE DIAGNOSTICS")
        Spacer(modifier = Modifier.height(10.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(1.dp, primaryColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BatteryMetricItem(label = "Core Temperature", value = "32°C", tint = primaryColor)
            Divider(color = primaryColor.copy(alpha = 0.1f))
            BatteryMetricItem(label = "Junction Voltage", value = "3.98 Volts", tint = primaryColor)
            Divider(color = primaryColor.copy(alpha = 0.1f))
            BatteryMetricItem(label = "Cell Charge Cycle Health", value = "98.2% (Perfect)", tint = primaryColor)
            Divider(color = primaryColor.copy(alpha = 0.1f))
            BatteryMetricItem(label = "Hardware Profile Status", value = "COOL / STANDARD", tint = primaryColor)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun BatteryMetricItem(label: String, value: String, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = OnDarkText.copy(alpha = 0.7f))
        Text(text = value, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = tint)
    }
}
