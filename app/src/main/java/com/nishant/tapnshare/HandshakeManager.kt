package com.nishant.tapnshare

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Parcelable
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*

class HandshakeManager(private val activity: Activity) {

    private var nfcAdapter: NfcAdapter? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bleScanner: BluetoothLeScanner? = null

    private var scanCallback: ScanCallback? = null
    private var scanningJob: Job? = null

    var onDeviceDetected: ((String) -> Unit)? = null
    var onTagDetected: ((String) -> Unit)? = null

    init {
        initNfc()
        initBle()
    }

    /** NFC Initialization **/
    private fun initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        if (nfcAdapter == null) {
            Log.e("HandshakeManager", "NFC not supported")
        }
    }

    fun isNfcAvailable(): Boolean = nfcAdapter != null

    fun handleNfcIntent(intent: Intent) {
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            val tag: Tag? = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as? Tag
            tag?.let {
                val id = it.id.joinToString("") { b -> "%02X".format(b) }
                onTagDetected?.invoke(id)
            }
        }
    }

    /** BLE Initialization **/
    private fun initBle() {
        bluetoothAdapter =
            (activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        bleScanner = bluetoothAdapter?.bluetoothLeScanner
    }

    /** Start BLE Scanning **/
    fun startBleScan(filterName: String) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("HandshakeManager", "BLUETOOTH_SCAN permission not granted")
            return
        }

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let {
                    if (it.rssi > -50) { // Only close proximity devices (~2-5 cm)
                        val deviceName = it.device.name ?: "Unknown"
                        Log.d("HandshakeManager", "BLE Device Found: $deviceName")
                        onDeviceDetected?.invoke(deviceName)
                    }
                }
            }
        }

        val filters = listOf(
            ScanFilter.Builder()
                .setDeviceName(filterName)
                .build()
        )

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bleScanner?.startScan(filters, settings, scanCallback!!)
    }

    /** Stop BLE Scanning **/
    fun stopBleScan() {
        scanCallback?.let {
            bleScanner?.stopScan(it)
            scanCallback = null
        }
        scanningJob?.cancel()
    }

    /** Continuous BLE Scanning **/
    fun startContinuousScan(filterName: String) {
        scanningJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                startBleScan(filterName)
                delay(2000)
                stopBleScan()
                delay(500)
            }
        }
    }

    fun stopContinuousScan() {
        stopBleScan()
        scanningJob?.cancel()
    }
}
