package com.kunaljuneja.sdk.keyboard

import android.app.Application
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

internal object BluetoothController   : BluetoothHidDevice.Callback(), BluetoothProfile.ServiceListener {
    private val sdpSettings by lazy {
        BluetoothHidDeviceAppSdpSettings(
            "HID_Keyboard",
            "Bluetooth Keyboard Access",
            "this",
            BluetoothHidDevice.SUBCLASS1_KEYBOARD,
            DescriptorCollection.KEYBOARD
        )
    }
    private val outQosSettings by lazy {
        BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
            800,
            9,
            0,
            11250,
            BluetoothHidDeviceAppQosSettings.MAX
        )
    }
    var bluetoothHidDevice: BluetoothHidDevice? = null
    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    var isRegistered = false

    var connectedDevice: BluetoothDevice? = null
    lateinit var keyboardSender : KeyboardSender

    private lateinit var context : Application

    fun init(context: Application) {
        this.context = context
        if (bluetoothHidDevice == null) {
            bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.HID_DEVICE)
        }
    }

    override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
        super.onAppStatusChanged(pluggedDevice, registered)
        isRegistered = registered
        Log.d(TAG, "App State Changed. Registered: $registered")
        context.sendBroadcast(Intent(KeyboardMainActivity.APP_STATE_CHANGED))
    }

    override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
        super.onConnectionStateChanged(device, state)
        Log.d(TAG, "State: ${when (state) {
                    BluetoothProfile.STATE_DISCONNECTED -> "Disconnected"
                    BluetoothProfile.STATE_DISCONNECTING -> "Disconnecting"
                    BluetoothProfile.STATE_CONNECTING -> "Connecting"
                    BluetoothProfile.STATE_CONNECTED -> "Connected"

                    else -> state.toString()
                }}")

        when(state) {
            BluetoothProfile.STATE_CONNECTED -> {
                val intent = Intent(context, ConnectedActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                context.sendBroadcast(Intent(BT_DISCONNECTED))
            }
        }
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        Log.d(TAG, "Connected to Service")
        if (profile != BluetoothProfile.HID_DEVICE) {
            Log.e(TAG, "Profile: $profile")
            return
        }

        val hidDevice = proxy as? BluetoothHidDevice
        if (hidDevice == null) {
            Log.e(TAG, "Proxy Received but not HID")
            return
        }

        bluetoothHidDevice = hidDevice
        bluetoothHidDevice?.registerApp(
            sdpSettings, null,
            outQosSettings, { it.run() }, this
        )
    }

    override fun onServiceDisconnected(profile: Int) {
        Log.d(TAG, "Service Disconnected")
        if (profile == BluetoothHidDevice.HID_DEVICE)
            bluetoothHidDevice = null
    }

    override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
        super.onSetReport(device, type, id, data)
        Log.d(TAG, "SetReport: $device, $type, $id, $data")
    }

    override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
        super.onGetReport(device, type, id, bufferSize)
        Log.d(TAG, "GetReport")
    }

    fun connectDevice(device: BluetoothDevice){
        bluetoothHidDevice?.connect(device) ?: run{
            Log.e(TAG, "Cannot Connect")
            return
        }
        connectedDevice = device
        keyboardSender = KeyboardSender(bluetoothHidDevice, connectedDevice)
    }

    fun disconnectDevice() {
        bluetoothHidDevice?.disconnect(connectedDevice)
    }


    private const val TAG = "BluetoothController"
    const val BT_DISCONNECTED = "Disconnected"

}