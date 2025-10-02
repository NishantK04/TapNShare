package com.nishant.tapnshare

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nishant.tapnshare.ui.theme.TapNShareTheme
import com.nishant.tapnshare.viewmodel.PermissionsViewModel
import com.nishant.tapnshare.viewmodel.UserProfileViewModel

class MainActivity : ComponentActivity() {

    private val bluetoothPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            Manifest.permission.BLUETOOTH
        }

    private val userProfileViewModel: UserProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[UserProfileViewModel::class.java]
    }

    private val permissionsViewModel: PermissionsViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[PermissionsViewModel::class.java]
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionsViewModel.setBluetoothGranted(isGranted)
            permissionsViewModel.setHasAskedBluetoothPermission(true) // Mark permission as asked
            restartActivity()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TapNShareTheme {
                val context = LocalContext.current
                val isBluetoothGranted by permissionsViewModel.isBluetoothGranted.collectAsState()
                val isBluetoothToggleOn by permissionsViewModel.isBluetoothToggleOn.collectAsState()
                val hasAskedBluetoothPermission by permissionsViewModel.hasAskedBluetoothPermission.collectAsState()

                LaunchedEffect(Unit) {
                    // ✅ Request permission if:
                    // - toggle is ON and permission not granted
                    // - OR first launch and permission not granted
                    if ((!isBluetoothGranted && isBluetoothToggleOn) || (!isBluetoothGranted && !hasAskedBluetoothPermission)) {
                        checkAndRequestPermission()
                    }
                }

                MainScreen(
                    isBluetoothPermitted = isBluetoothGranted,
                    userProfileViewModel = userProfileViewModel,
                    onOpenSettings = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    },
                    requestBluetoothPermission = { checkAndRequestPermission() }
                )
            }
        }
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, bluetoothPermission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionsViewModel.setBluetoothGranted(true)
        } else {
            permissionLauncher.launch(bluetoothPermission)
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        permissionsViewModel.refreshBluetoothPermission()
    }
}
