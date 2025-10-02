package com.nishant.tapnshare.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _isBluetoothGranted = MutableStateFlow(false)
    val isBluetoothGranted = _isBluetoothGranted.asStateFlow()

    private val _isBluetoothToggleOn = MutableStateFlow(false)
    val isBluetoothToggleOn = _isBluetoothToggleOn.asStateFlow()

    private val _hasAskedBluetoothPermission = MutableStateFlow(false)
    val hasAskedBluetoothPermission = _hasAskedBluetoothPermission.asStateFlow()

    private val context: Context = application.applicationContext

    init {
        _isBluetoothToggleOn.value = prefs.getBoolean("bluetooth_toggle", false)
        _hasAskedBluetoothPermission.value = prefs.getBoolean("bluetooth_permission_asked", false)
        refreshBluetoothPermission()
    }

    fun setBluetoothGranted(granted: Boolean) {
        _isBluetoothGranted.value = granted
    }

    fun setBluetoothToggle(on: Boolean) {
        prefs.edit().putBoolean("bluetooth_toggle", on).apply()
        _isBluetoothToggleOn.value = on
    }

    fun setHasAskedBluetoothPermission(asked: Boolean) {
        prefs.edit().putBoolean("bluetooth_permission_asked", asked).apply()
        _hasAskedBluetoothPermission.value = asked
    }

    fun refreshBluetoothPermission() {
        _isBluetoothGranted.value = checkBluetoothPermission()
    }

    private fun checkBluetoothPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.Manifest.permission.BLUETOOTH_CONNECT
        } else {
            android.Manifest.permission.BLUETOOTH
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
