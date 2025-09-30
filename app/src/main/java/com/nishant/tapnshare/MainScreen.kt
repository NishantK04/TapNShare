package com.nishant.tapnshare

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
// Removed: kotlinx.coroutines.delay is no longer needed

// NOTE: Assuming color constants are defined in a separate file in this package.
// val DarkNavyBackground, LightBlueAccent, GreenAccent

/**
 * The main application screen, which allows the user to toggle between
 * "Idle" and "Continuous Scanning" modes by clicking the center button.
 */
@Composable
fun MainScreen() {
    // 1. STATE: Track whether the device is actively scanning
    var isScanning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Top Bar (Settings/TapNShare/History) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.setting),
                contentDescription = "Settings",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TapNShare",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenAccent)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ready",
                        color = GreenAccent,
                        fontSize = 14.sp
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.history),
                contentDescription = "History",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }

        // --- Central Scanning Area ---
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pass state to control the pulsing effect
            ScanningRingEffect(isActive = isScanning)

            // Inner Circle/Tap Target
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    // Color changes based on the state for visual feedback
                    .background(if (isScanning) GreenAccent.copy(alpha = 0.3f) else LightBlueAccent.copy(alpha = 0.2f))
                    // 2. TOGGLE LOGIC: Click toggles the isScanning state
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isScanning = !isScanning // Toggle the state (Start -> Stop, Stop -> Start)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Wi-Fi or Tap Icon
                Icon(
                    painter = painterResource(id = R.drawable.wifi),
                    contentDescription = if (isScanning) "Stop Scanning" else "Tap to Scan",
                    // Icon color changes based on state
                    tint = if (isScanning) GreenAccent else LightBlueAccent,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        // --- Status Text ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                // CONDITIONAL TEXT: Display status based on state
                text = if (isScanning) "Searching for nearby devices..." else "Tap to scan for nearby devices",
                color = if (isScanning) GreenAccent else LightBlueAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Bring your device close to another TapNShare\nuser to initiate a secure handshake",
                color = Color.LightGray.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "Encrypted",
                    tint = GreenAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Encrypted by TapNShare Security Layer",
                    color = GreenAccent.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --- Scanning Ring Effect Composable (Unchanged but relies on the toggled state) ---

/**
 * Draws the outer pulsing ring effect. The animation runs indefinitely only when isActive is true.
 */
@Composable
fun ScanningRingEffect(isActive: Boolean) {

    // We only execute the infinite transition logic when active.
    val infiniteTransition = rememberInfiniteTransition(label = "scanningRingTransition")

    val scale by if (isActive) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "ringScale"
        )
    } else {
        // Return a fixed state when inactive
        remember { mutableStateOf(1f) }
    }

    val alpha by if (isActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "ringAlpha"
        )
    } else {
        // Return a fixed state when inactive
        remember { mutableStateOf(0.3f) }
    }

    // Outer Ring (Pulsing/Static)
    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                // Border color uses the changing alpha only when active
                color = LightBlueAccent.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}