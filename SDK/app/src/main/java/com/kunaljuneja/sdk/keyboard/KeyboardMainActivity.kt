package com.kunaljuneja.sdk.keyboard

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.kunaljuneja.sdk.R

internal class KeyboardMainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                        BluetoothAdapter.STATE_ON -> {
                            Log.d(TAG, "Bluetooth Turned On")
                            startBluetoothService()
                        }
                    }
                }

                APP_STATE_CHANGED -> {
                    Log.d(TAG, "App State Changed Broadcast!")
                    listDevices()
                }
            }
        }
    }

    private lateinit var deviceListView : ListView

    private val bluetoothAdapter : BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_activity_main)

        deviceListView = findViewById<ListView>(R.id.device_list)

        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        registerReceiver(receiver, IntentFilter(APP_STATE_CHANGED))

        if(bluetoothAdapter.isEnabled) {
            Log.d(TAG, "Bluetooth Already On")
            startBluetoothService()
        } else {
            bluetoothAdapter.enable()
        }
    }

    private fun startBluetoothService() {
        BluetoothController.init(applicationContext as Application)
    }

    private fun listDevices() {
        val deviceList = bluetoothAdapter.bondedDevices.toList()
        val mListAdapter = DeviceAdapter(this, deviceList)
        deviceListView.adapter = mListAdapter
        deviceListView.setOnItemClickListener{adapterView, _, position, _ ->
            val device = adapterView.getItemAtPosition(position) as BluetoothDevice
            Log.d(TAG, "Trying to Connect")
            BluetoothController.connectDevice(device)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter.disable()
        unregisterReceiver(receiver)
    }

    companion object{
        private const val TAG = "BtMainActivity"
        const val APP_STATE_CHANGED = "app_state_changed"
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object:AmbientModeSupport.AmbientCallback() {}
    }
}