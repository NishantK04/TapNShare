package com.nishant.tapnshare

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nishant.tapnshare.data.UserProfile
import com.nishant.tapnshare.ui.theme.DarkNavyBackground
import com.nishant.tapnshare.ui.theme.GreenAccent
import com.nishant.tapnshare.ui.theme.LightBlueAccent
import com.nishant.tapnshare.ui.theme.WarningDarkBackground
import com.nishant.tapnshare.ui.theme.WarningYellow
import com.nishant.tapnshare.viewmodel.UserProfileViewModel

@Composable
fun MainScreen(
    isBluetoothPermitted: Boolean = true,
    userProfileViewModel: UserProfileViewModel,
    onOpenSettings: () -> Unit,
    requestBluetoothPermission: () -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var showProfileSetupModal by remember { mutableStateOf(false) }

    // Observe profile from ViewModel
    val userProfile by userProfileViewModel.profile.collectAsState()

    val context = LocalContext.current

    // Stop scanning if permission revoked
    LaunchedEffect(isBluetoothPermitted) {
        if (!isBluetoothPermitted && isScanning) isScanning = false
    }

    // Tap handler
    val onTapToScan: () -> Unit = {
        if (userProfile == null) {
            // Database empty → show modal
            showProfileSetupModal = true
        } else {
            // Profile exists → directly toggle scanning
            isScanning = !isScanning
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Top Bar ---
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
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        context.startActivity(
                            android.content.Intent(context, SettingsActivity::class.java)
                        )
                    }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TapNShare",
                    color = Color.White,
                    fontSize = 20.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isBluetoothPermitted) GreenAccent else WarningYellow)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBluetoothPermitted) "Ready" else "Limited",
                        color = if (isBluetoothPermitted) GreenAccent else WarningYellow,
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

        // --- Warning Banner ---
        if (!isBluetoothPermitted) LimitedFunctionalityBanner()

        // --- Central Scanning Area ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                ScanningRingEffect(isActive = isScanning)

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            if (isScanning) GreenAccent.copy(alpha = 0.3f)
                            else LightBlueAccent.copy(alpha = 0.2f)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = isBluetoothPermitted
                        ) { onTapToScan() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wifi),
                        contentDescription = if (isScanning) "Stop Scanning" else "Tap to Scan",
                        tint = if (isScanning) GreenAccent else LightBlueAccent,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = if (isScanning) "Searching for nearby devices..." else "Tap to scan for nearby devices",
                color = if (isScanning) GreenAccent else LightBlueAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- Footer ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bring your device close to another TapNShare\nuser to initiate a secure handshake",
                color = Color.LightGray.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!isBluetoothPermitted) {
                Text(
                    text = "Enable Bluetooth permission to start scanning",
                    color = WarningYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

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

    // --- Profile Setup Modal ---
    if (showProfileSetupModal) {
        ProfileSetupModal(
            currentProfile = UserProfile(name = "", phone = ""),
            onSave = { updatedProfile ->
                userProfileViewModel.saveProfile(updatedProfile)
                showProfileSetupModal = false
                isScanning = !isScanning
            },
            onCancel = { showProfileSetupModal = false }
        )
    }
}

@Composable
fun LimitedFunctionalityBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(WarningDarkBackground)
            .border(1.dp, WarningYellow, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = "Warning",
            tint = WarningYellow,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Limited functionality",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Grant permissions in Settings for full experience",
                color = Color.LightGray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ScanningRingEffect(isActive: Boolean) {
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
        remember { mutableStateOf(0.3f) }
    }

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = LightBlueAccent.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

