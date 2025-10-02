package com.nishant.tapnshare

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nishant.tapnshare.ui.theme.DarkNavyBackground
import com.nishant.tapnshare.ui.theme.GreenAccent
import com.nishant.tapnshare.ui.theme.LightBlueAccent
import com.nishant.tapnshare.ui.theme.WarningYellow
import com.nishant.tapnshare.R
import com.nishant.tapnshare.data.UserProfile
import com.nishant.tapnshare.viewmodel.PermissionsViewModel
import com.nishant.tapnshare.viewmodel.UserProfileViewModel

private val ItemCardBackground = Color(0xFF1E2137)
private val TextDarkGray = Color(0xFF8A92A6)
private val AccentGreen = Color(0xFF34A853)
private val AccentBlue = Color(0xFF337CCF)
private val AccentPurple = Color(0xFF8A2BE2)

@Composable
fun SettingsScreen(
    userProfileViewModel: UserProfileViewModel = viewModel(),
    permissionsViewModel: PermissionsViewModel = viewModel(),
    onBack: () -> Unit = {},
    restartMain: () -> Unit = {} // 🔹 Callback to restart MainActivity
) {
    val currentProfile by userProfileViewModel.profile.collectAsState()
    val profileGradient = Brush.horizontalGradient(listOf(AccentBlue, AccentGreen))
    val isBluetoothGranted by permissionsViewModel.isBluetoothGranted.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionsViewModel.setBluetoothGranted(granted)
        restartMain() // 🔹 Restart after permission change
    }

    LaunchedEffect(Unit) {
        permissionsViewModel.refreshBluetoothPermission()
    }

    val requestBluetoothPermission: () -> Unit = {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            Manifest.permission.BLUETOOTH
        }
        bluetoothPermissionLauncher.launch(permission)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .systemBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrowback),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Settings",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Manage your TapNShare",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ProfileCard(
                profile = currentProfile ?: UserProfile(name = "", phone = ""),
                profileGradient = profileGradient,
                onEditProfile = { showEditDialog = true }
            )

            SettingsSectionHeader("App Permissions")

            SettingsToggleItem(
                iconRes = R.drawable.bluetooth,
                title = "Bluetooth / Nearby Devices",
                subtitle = "Detect nearby devices for contact sharing",
                accentColor = AccentBlue,
                isChecked = isBluetoothGranted,
                onCheckedChange = { isChecked ->
                    permissionsViewModel.setBluetoothToggle(isChecked) // ✅ Persist toggle state

                    if (isChecked) {
                        // Request permission if toggle is ON
                        requestBluetoothPermission()
                    } else {
                        // Remove granted state and restart MainActivity
                        permissionsViewModel.setBluetoothGranted(false)
                        restartMain()
                    }
                }

            )


            SettingsSectionHeader("Security")
            SettingsToggleItem(
                iconRes = R.drawable.shield,
                title = "Encrypt with App Key",
                subtitle = "Additional layer of protection",
                accentColor = AccentGreen,
                isChecked = true,
                onCheckedChange = {}
            )

            SettingsSectionHeader("Information")
            SettingsClickableItem(
                iconRes = R.drawable.warning,
                title = "About TapNShare",
                subtitle = "Version, privacy policy, terms",
                accentColor = AccentPurple,
                onClick = {}
            )

            SettingsSectionHeader("Device Status")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ItemCardBackground)
                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DeviceStatusItem(
                        "Bluetooth",
                        if (isBluetoothGranted) "Connected" else "Off",
                        if (isBluetoothGranted) AccentGreen else Color.Red
                    )
                    Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    DeviceStatusItem("Encryption", "Active", AccentGreen)
                    Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    DeviceStatusItem("Network", "Online", AccentBlue)
                }
            }

            Spacer(Modifier.height(25.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TapNShare v2.1.0",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Built with security and privacy in mind",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showEditDialog) {
        ProfileSetupModal(
            currentProfile = currentProfile ?: UserProfile(name = "", phone = ""),
            onSave = { updatedProfile ->
                userProfileViewModel.saveProfile(updatedProfile)
                showEditDialog = false
            },
            onCancel = { showEditDialog = false }
        )
    }
}







// --- Profile Card ---
@Composable
fun ProfileCard(
    profile: UserProfile,
    profileGradient: Brush,
    onEditProfile: () -> Unit
) {
    val isProfileComplete = profile.name.isNotBlank() && profile.phone.isNotBlank()
    val displayName = profile.name.ifBlank { "Set Profile" }
    val displayPhone = profile.phone.ifBlank { "Tap to set details" }
    val initials = if (isProfileComplete) displayName.take(2).uppercase() else "TS"
    val statusText = if (isProfileComplete) "Profile Complete" else "Profile Incomplete"
    val statusColor = if (isProfileComplete) AccentGreen else WarningYellow

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ItemCardBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(profileGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.width(16.dp))

        // Details
        Column(Modifier.weight(1f)) {
            Text(displayName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(displayPhone, color = TextDarkGray, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.checkm), contentDescription = statusText, tint = statusColor, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Edit Icon
        Icon(
            painter = painterResource(id = R.drawable.editsquare),
            contentDescription = "Edit Profile",
            tint = LightBlueAccent,
            modifier = Modifier
                .size(24.dp)
                .clickable { onEditProfile() }
        )
    }
}


@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = TextDarkGray.copy(alpha = 0.9f),
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
    )
}

@Composable
fun SettingsToggleItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ItemCardBackground)
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // If checked, show the checkmark icon inside the box, otherwise show the functional icon
                Icon(
                    painter = painterResource(id = if (isChecked) R.drawable.checkm else iconRes),
                    contentDescription = "Icon",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextDarkGray,
                    fontSize = 12.sp
                )
            }
        }

        // Toggle Switch
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.LightGray.copy(alpha = 0.7f),
                uncheckedTrackColor = Color(0xFF33334F)
            ),
            modifier = Modifier.scale(.75f)
        )
    }
}

@Composable
fun SettingsClickableItem(
    iconRes: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ItemCardBackground)
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Icon",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextDarkGray,
                    fontSize = 12.sp
                )
            }
        }

        // Arrow Icon
        Icon(
            painter = painterResource(id = R.drawable.open),
            contentDescription = "Go",
            tint = TextDarkGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun DeviceStatusItem(label: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp
            )
        }
        Text(
            text = status,
            color = statusColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
